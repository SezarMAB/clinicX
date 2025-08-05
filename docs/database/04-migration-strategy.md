# Migration Strategy: From Logical to Schema-Per-Tenant

## Overview

This document outlines the migration strategy from the current logical isolation approach to a schema-per-tenant architecture in ClinicX.

## Current State Analysis

### What We Have
```sql
-- Single shared schema
public.tenants      -- Tenant registry (exists)
public.patients     -- All tenants' data mixed (planned tenant_id column)
public.appointments -- All tenants' data mixed (planned tenant_id column)
public.staff        -- All tenants' data mixed (planned tenant_id column)
```

### Migration Goals
1. **Zero Downtime**: Migrate without service interruption
2. **Data Integrity**: No data loss during migration
3. **Rollback Capability**: Ability to revert if issues arise
4. **Gradual Migration**: Test with new tenants first

## Migration Phases

### Phase 1: Infrastructure Preparation (Week 1)

#### 1.1 Database Preparation
```sql
-- Add schema tracking to tenants table
ALTER TABLE public.tenants 
ADD COLUMN IF NOT EXISTS database_schema VARCHAR(63) UNIQUE,
ADD COLUMN IF NOT EXISTS schema_version VARCHAR(20) DEFAULT '1.0.0',
ADD COLUMN IF NOT EXISTS schema_status VARCHAR(20) DEFAULT 'PENDING',
ADD COLUMN IF NOT EXISTS migration_status VARCHAR(20) DEFAULT 'NOT_STARTED',
ADD COLUMN IF NOT EXISTS migration_started_at TIMESTAMPTZ,
ADD COLUMN IF NOT EXISTS migration_completed_at TIMESTAMPTZ;

-- Create migration tracking table
CREATE TABLE IF NOT EXISTS public.schema_migrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(100) NOT NULL,
    schema_name VARCHAR(63) NOT NULL,
    migration_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    error_message TEXT,
    records_migrated BIGINT,
    created_by VARCHAR(255) NOT NULL
);

-- Create helper functions
CREATE OR REPLACE FUNCTION create_tenant_schema_structure(
    p_schema_name VARCHAR(63)
) RETURNS VOID AS $$
BEGIN
    -- Create schema
    EXECUTE format('CREATE SCHEMA IF NOT EXISTS %I', p_schema_name);
    
    -- Clone table structure from public schema
    EXECUTE format('
        CREATE TABLE %I.patients (LIKE public.patients INCLUDING ALL)',
        p_schema_name
    );
    
    EXECUTE format('
        CREATE TABLE %I.appointments (LIKE public.appointments INCLUDING ALL)',
        p_schema_name
    );
    
    EXECUTE format('
        CREATE TABLE %I.staff (LIKE public.staff INCLUDING ALL)',
        p_schema_name
    );
    
    -- Add other tables as needed
END;
$$ LANGUAGE plpgsql;
```

#### 1.2 Application Configuration
```yaml
# application.yml
spring:
  profiles:
    active: migration

app:
  migration:
    mode: dual-write  # Options: dual-write, read-old-write-new, schema-only
    batch-size: 1000
    parallel-threads: 4
```

### Phase 2: Dual-Write Implementation (Week 2)

#### 2.1 Migration Service
```java
@Service
@Slf4j
public class TenantMigrationService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    public MigrationResult migrateTenant(String tenantId) {
        log.info("Starting migration for tenant: {}", tenantId);
        
        return transactionTemplate.execute(status -> {
            try {
                // 1. Create schema
                String schemaName = createSchemaForTenant(tenantId);
                
                // 2. Create table structure
                createTableStructure(schemaName);
                
                // 3. Migrate data
                MigrationStats stats = migrateData(tenantId, schemaName);
                
                // 4. Verify migration
                verifyMigration(tenantId, schemaName, stats);
                
                // 5. Update tenant record
                updateTenantStatus(tenantId, schemaName, "COMPLETED");
                
                return MigrationResult.success(stats);
                
            } catch (Exception e) {
                log.error("Migration failed for tenant: {}", tenantId, e);
                status.setRollbackOnly();
                return MigrationResult.failure(e.getMessage());
            }
        });
    }
    
    private MigrationStats migrateData(String tenantId, String schemaName) {
        MigrationStats stats = new MigrationStats();
        
        // Migrate patients
        int patients = jdbcTemplate.update(
            String.format(
                "INSERT INTO %s.patients SELECT * FROM public.patients WHERE tenant_id = ?",
                schemaName
            ),
            tenantId
        );
        stats.setPatientCount(patients);
        
        // Migrate appointments
        int appointments = jdbcTemplate.update(
            String.format(
                "INSERT INTO %s.appointments SELECT * FROM public.appointments WHERE tenant_id = ?",
                schemaName
            ),
            tenantId
        );
        stats.setAppointmentCount(appointments);
        
        // Continue for other tables...
        
        return stats;
    }
}
```

#### 2.2 Dual-Write Repository Pattern
```java
@Repository
public class DualWritePatientRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private TenantContext tenantContext;
    
    @Value("${app.migration.mode}")
    private String migrationMode;
    
    @Transactional
    public Patient save(Patient patient) {
        String tenantId = tenantContext.getCurrentTenant();
        
        if ("dual-write".equals(migrationMode)) {
            // Write to both old and new schema
            saveToPublicSchema(patient, tenantId);
            saveToTenantSchema(patient, tenantId);
        } else if ("schema-only".equals(migrationMode)) {
            // Write only to new schema
            saveToTenantSchema(patient, tenantId);
        } else {
            // Default: write to public schema
            saveToPublicSchema(patient, tenantId);
        }
        
        return patient;
    }
    
    private void saveToPublicSchema(Patient patient, String tenantId) {
        jdbcTemplate.update(
            "INSERT INTO public.patients (id, name, tenant_id, ...) VALUES (?, ?, ?, ...)",
            patient.getId(), patient.getName(), tenantId
        );
    }
    
    private void saveToTenantSchema(Patient patient, String tenantId) {
        String schema = resolveSchema(tenantId);
        jdbcTemplate.update(
            String.format("INSERT INTO %s.patients (id, name, ...) VALUES (?, ?, ...)", schema),
            patient.getId(), patient.getName()
        );
    }
}
```

### Phase 3: Gradual Migration (Week 3-4)

#### 3.1 Migration Controller
```java
@RestController
@RequestMapping("/api/admin/migration")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class MigrationController {
    
    @Autowired
    private TenantMigrationService migrationService;
    
    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<MigrationResult> migrateTenant(@PathVariable String tenantId) {
        MigrationResult result = migrationService.migrateTenant(tenantId);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<BatchMigrationResult> migrateBatch(@RequestBody List<String> tenantIds) {
        BatchMigrationResult result = migrationService.migrateBatch(tenantIds);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/status")
    public ResponseEntity<MigrationStatus> getMigrationStatus() {
        MigrationStatus status = migrationService.getOverallStatus();
        return ResponseEntity.ok(status);
    }
}
```

#### 3.2 Migration Script
```bash
#!/bin/bash
# migrate-tenants.sh

# Configuration
DB_HOST="localhost"
DB_NAME="clinicx"
DB_USER="postgres"
BATCH_SIZE=10
PARALLEL_JOBS=4

# Get list of tenants to migrate
TENANTS=$(psql -h $DB_HOST -d $DB_NAME -U $DB_USER -t -c \
  "SELECT tenant_id FROM tenants WHERE migration_status = 'NOT_STARTED' LIMIT $BATCH_SIZE")

# Migrate in parallel
echo "$TENANTS" | xargs -P $PARALLEL_JOBS -I {} bash -c '
  echo "Migrating tenant: {}"
  curl -X POST http://localhost:8080/api/admin/migration/tenant/{} \
    -H "Authorization: Bearer $ADMIN_TOKEN"
'
```

### Phase 4: Validation & Cutover (Week 5)

#### 4.1 Data Validation
```sql
-- Validation queries
CREATE OR REPLACE FUNCTION validate_migration(
    p_tenant_id VARCHAR(100),
    p_schema_name VARCHAR(63)
) RETURNS TABLE (
    table_name VARCHAR(100),
    public_count BIGINT,
    schema_count BIGINT,
    match BOOLEAN
) AS $$
BEGIN
    -- Validate patients
    RETURN QUERY
    SELECT 
        'patients'::VARCHAR(100),
        (SELECT COUNT(*) FROM public.patients WHERE tenant_id = p_tenant_id),
        (SELECT COUNT(*) FROM format('%I.patients', p_schema_name)),
        (SELECT COUNT(*) FROM public.patients WHERE tenant_id = p_tenant_id) = 
        (SELECT COUNT(*) FROM format('%I.patients', p_schema_name));
    
    -- Validate appointments
    RETURN QUERY
    SELECT 
        'appointments'::VARCHAR(100),
        (SELECT COUNT(*) FROM public.appointments WHERE tenant_id = p_tenant_id),
        (SELECT COUNT(*) FROM format('%I.appointments', p_schema_name)),
        (SELECT COUNT(*) FROM public.appointments WHERE tenant_id = p_tenant_id) = 
        (SELECT COUNT(*) FROM format('%I.appointments', p_schema_name));
    
    -- Add more tables...
END;
$$ LANGUAGE plpgsql;
```

#### 4.2 Cutover Process
```java
@Service
public class CutoverService {
    
    @Transactional
    public void performCutover(String tenantId) {
        // 1. Validate migration
        ValidationResult validation = validateMigration(tenantId);
        if (!validation.isValid()) {
            throw new MigrationException("Validation failed: " + validation.getErrors());
        }
        
        // 2. Stop dual writes
        updateMigrationMode(tenantId, "schema-only");
        
        // 3. Update tenant configuration
        updateTenantConfiguration(tenantId, "ACTIVE");
        
        // 4. Clear caches
        clearTenantCaches(tenantId);
        
        // 5. Notify monitoring
        notifyMonitoring(tenantId, "CUTOVER_COMPLETE");
    }
}
```

### Phase 5: Cleanup (Week 6)

#### 5.1 Old Data Archival
```sql
-- Archive old data
CREATE TABLE public.archived_patients AS 
SELECT * FROM public.patients 
WHERE tenant_id IN (
    SELECT tenant_id FROM tenants 
    WHERE migration_status = 'COMPLETED'
);

-- Remove migrated data from public schema
DELETE FROM public.patients 
WHERE tenant_id IN (
    SELECT tenant_id FROM tenants 
    WHERE migration_status = 'COMPLETED'
    AND migration_completed_at < NOW() - INTERVAL '7 days'
);
```

## Migration Checklist

### Pre-Migration
- [ ] Backup database
- [ ] Update application configuration
- [ ] Deploy migration code
- [ ] Test rollback procedure
- [ ] Notify stakeholders

### During Migration
- [ ] Monitor migration progress
- [ ] Check error logs
- [ ] Validate data integrity
- [ ] Monitor performance impact
- [ ] Keep audit trail

### Post-Migration
- [ ] Validate all data migrated
- [ ] Update monitoring dashboards
- [ ] Remove old data (after verification)
- [ ] Update documentation
- [ ] Performance testing

## Rollback Strategy

### Immediate Rollback
```java
@Service
public class RollbackService {
    
    public void rollbackTenant(String tenantId) {
        // 1. Stop writing to new schema
        updateMigrationMode(tenantId, "public-only");
        
        // 2. Drop tenant schema (if needed)
        dropTenantSchema(tenantId);
        
        // 3. Update tenant status
        updateTenantStatus(tenantId, "ROLLBACK");
        
        // 4. Clear caches
        clearTenantCaches(tenantId);
    }
}
```

### Emergency Procedures
1. **Complete System Rollback**
   ```bash
   # Revert application deployment
   kubectl rollout undo deployment/clinicx-backend
   
   # Restore database from backup
   pg_restore -h localhost -d clinicx backup_pre_migration.sql
   ```

2. **Partial Rollback**
   - Keep successfully migrated tenants
   - Revert only failed tenants
   - Fix issues and retry

## Monitoring During Migration

### Key Metrics
```yaml
metrics:
  migration_progress:
    - tenants_total
    - tenants_migrated
    - tenants_failed
    - migration_duration_seconds
    
  performance:
    - query_time_seconds
    - connection_pool_usage
    - cpu_usage_percent
    - memory_usage_bytes
```

### Alerts
```yaml
alerts:
  - name: MigrationFailure
    condition: migration_failures > 0
    severity: critical
    
  - name: SlowMigration
    condition: migration_duration > 3600
    severity: warning
    
  - name: HighResourceUsage
    condition: cpu_usage > 80
    severity: warning
```

## Success Criteria

1. **Data Integrity**: 100% data match between old and new schemas
2. **Performance**: No degradation in query performance
3. **Zero Downtime**: No service interruption during migration
4. **Rollback Tested**: Successful rollback demonstration
5. **Monitoring**: All dashboards updated and functional

## Timeline

| Week | Phase | Activities |
|------|-------|------------|
| 1 | Preparation | Database setup, function creation |
| 2 | Implementation | Deploy dual-write code |
| 3-4 | Migration | Gradual tenant migration |
| 5 | Validation | Data validation and cutover |
| 6 | Cleanup | Archive old data, documentation |

## Conclusion

This migration strategy provides a safe, gradual approach to moving from logical to schema-based isolation with:
- Zero downtime
- Data integrity validation
- Rollback capability
- Performance monitoring
- Clear success criteria