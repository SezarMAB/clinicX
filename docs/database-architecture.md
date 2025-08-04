# Database Architecture for ClinicX Multi-Tenant SaaS

## Executive Summary

This document outlines the database architecture strategy for ClinicX, a multi-tenant SaaS platform for clinic management. We analyze the current state and propose a migration path from logical isolation to schema-per-tenant architecture.

## Current Architecture

### Database Strategy
- **Type**: Single PostgreSQL database with logical isolation
- **Tenant Isolation**: Planned tenant_id columns (not yet implemented)
- **Connection**: Single connection pool for all tenants
- **Schema**: Shared public schema for all tenants

### Current Schema Structure
```sql
public.tenants              -- Tenant management
public.patients             -- Shared by all tenants
public.appointments         -- Shared by all tenants
public.staff               -- Shared by all tenants
public.invoices            -- Shared by all tenants
public.payments            -- Shared by all tenants
```

### Limitations of Current Approach
1. **Data Isolation**: Risk of cross-tenant data access
2. **Performance**: One tenant can impact others
3. **Compliance**: Harder to meet HIPAA/GDPR requirements
4. **Backup/Restore**: Cannot backup individual tenants
5. **Scalability**: Limited by single schema constraints

## Proposed Architecture: Schema Per Tenant

### Overview
Implement PostgreSQL schema-based multi-tenancy where each tenant has its own schema within a single database.

### Schema Structure
```sql
-- Shared system schema
public.tenants                    -- Tenant registry
public.system_configurations      -- System-wide settings
public.feature_flags             -- Feature management
public.audit_logs               -- Cross-tenant audit trail

-- Tenant-specific schemas
tenant_clinic1.patients
tenant_clinic1.appointments
tenant_clinic1.staff
tenant_clinic1.invoices
tenant_clinic1.payments

tenant_dental1.patients
tenant_dental1.appointments
tenant_dental1.staff
tenant_dental1.dental_charts    -- Dental-specific tables
tenant_dental1.treatments
```

### Benefits
1. **Complete Data Isolation**: Each tenant's data is physically separated
2. **Performance Isolation**: Indexes and queries don't affect other tenants
3. **Easy Backup/Restore**: Can backup individual tenant schemas
4. **Compliance**: Clear data boundaries for regulatory requirements
5. **Flexible Schema Evolution**: Different tenants can have different versions

## Implementation Strategy

### Phase 1: Foundation (Week 1-2)

#### 1.1 Database Schema Updates
```sql
-- Add schema name to tenants table
ALTER TABLE public.tenants 
ADD COLUMN database_schema VARCHAR(63) UNIQUE,
ADD COLUMN schema_version VARCHAR(20) DEFAULT '1.0.0';

-- Create function to setup new tenant schema
CREATE OR REPLACE FUNCTION create_tenant_schema(
    p_tenant_id VARCHAR(100),
    p_schema_name VARCHAR(63)
) RETURNS VOID AS $$
BEGIN
    -- Create schema
    EXECUTE format('CREATE SCHEMA IF NOT EXISTS %I', p_schema_name);
    
    -- Grant permissions
    EXECUTE format('GRANT ALL ON SCHEMA %I TO clinicx_app', p_schema_name);
    
    -- Set search path for session
    EXECUTE format('SET search_path TO %I, public', p_schema_name);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

#### 1.2 Hibernate Configuration
```java
@Configuration
@EnableTransactionManagement
public class MultiTenantDatabaseConfig {
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            MultiTenantConnectionProvider multiTenantConnectionProvider,
            CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("sy.sezar.clinicx");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        Map<String, Object> properties = new HashMap<>();
        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        
        em.setJpaPropertyMap(properties);
        return em;
    }
}
```

### Phase 2: Schema Management (Week 2-3)

#### 2.1 Dynamic Schema Creation
```java
@Service
@Transactional
public class TenantSchemaService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private FlywayMigrationService flywayService;
    
    public void createTenantSchema(String tenantId, String schemaName) {
        // Create schema
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        
        // Run Flyway migrations for tenant schema
        flywayService.migrateTenantSchema(schemaName);
        
        // Update tenant record
        jdbcTemplate.update(
            "UPDATE tenants SET database_schema = ? WHERE tenant_id = ?",
            schemaName, tenantId
        );
    }
}
```

#### 2.2 Connection Provider
```java
@Component
public class SchemaBasedMultiTenantConnectionProvider 
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    protected DataSource selectAnyDataSource() {
        return dataSource;
    }
    
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return dataSource;
    }
    
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = super.getConnection(tenantIdentifier);
        
        // Set schema based on tenant
        String schema = resolveDatabaseSchema(tenantIdentifier);
        connection.createStatement().execute("SET search_path TO " + schema + ", public");
        
        return connection;
    }
    
    private String resolveDatabaseSchema(String tenantId) {
        // Logic to resolve schema name from tenant ID
        return "tenant_" + tenantId.toLowerCase().replace("-", "_");
    }
}
```

### Phase 3: Migration Tools (Week 3-4)

#### 3.1 Flyway Multi-Schema Support
```java
@Service
public class FlywayMigrationService {
    
    @Autowired
    private DataSource dataSource;
    
    public void migrateTenantSchema(String schemaName) {
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas(schemaName)
            .locations("classpath:db/migration/tenant")
            .baselineOnMigrate(true)
            .load();
        
        flyway.migrate();
    }
    
    public void migrateSystemSchema() {
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas("public")
            .locations("classpath:db/migration/system")
            .baselineOnMigrate(true)
            .load();
        
        flyway.migrate();
    }
}
```

#### 3.2 Migration Scripts Structure
```
src/main/resources/db/migration/
├── system/                      # Public schema migrations
│   ├── V1__create_tenants.sql
│   └── V2__add_feature_flags.sql
└── tenant/                      # Tenant schema migrations
    ├── V1__create_patients.sql
    ├── V2__create_appointments.sql
    └── V3__create_dental_module.sql
```

### Phase 4: Security & Performance (Week 4-5)

#### 4.1 Row Level Security
```sql
-- Enable RLS on tenant tables
ALTER TABLE patients ENABLE ROW LEVEL SECURITY;

-- Create policy for tenant isolation
CREATE POLICY tenant_isolation_policy ON patients
    USING (current_setting('app.current_tenant')::uuid = tenant_id);

-- Create security definer function
CREATE OR REPLACE FUNCTION set_current_tenant(p_tenant_id uuid)
RETURNS void AS $$
BEGIN
    PERFORM set_config('app.current_tenant', p_tenant_id::text, false);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

#### 4.2 Connection Pool Optimization
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      # Schema-specific settings
      data-source-properties:
        prepareThreshold: 0  # Disable prepared statement caching across schemas
```

### Phase 5: Monitoring & Maintenance (Week 5-6)

#### 5.1 Schema Monitoring
```sql
-- View to monitor schema sizes
CREATE VIEW schema_sizes AS
SELECT 
    schemaname,
    pg_size_pretty(sum(pg_total_relation_size(schemaname||'.'||tablename))) as size,
    count(*) as table_count
FROM pg_tables
WHERE schemaname LIKE 'tenant_%'
GROUP BY schemaname;

-- Query to check active connections per schema
SELECT 
    datname,
    schemaname,
    count(*) as connection_count
FROM pg_stat_activity
WHERE schemaname LIKE 'tenant_%'
GROUP BY datname, schemaname;
```

#### 5.2 Backup Strategy
```bash
#!/bin/bash
# Backup individual tenant schema
pg_dump -h localhost -U postgres -d clinicx \
  --schema=tenant_${TENANT_ID} \
  -f backup_tenant_${TENANT_ID}_$(date +%Y%m%d).sql

# Restore tenant schema
psql -h localhost -U postgres -d clinicx \
  -f backup_tenant_${TENANT_ID}_20240115.sql
```

## Alternative: Database Per Tenant

### When to Consider
- More than 100 tenants
- Tenants require different database configurations
- Extreme isolation requirements
- Different backup schedules per tenant

### Architecture
```
PostgreSQL Cluster
├── clinicx_system          # System database
│   └── public.tenants     # Tenant registry
├── clinicx_tenant_001     # Tenant databases
├── clinicx_tenant_002
└── clinicx_tenant_003
```

### Implementation Considerations
1. **Connection Management**: Need dynamic DataSource routing
2. **Resource Usage**: Higher memory and CPU requirements
3. **Operational Complexity**: More databases to manage
4. **Cost**: Potentially higher infrastructure costs

## Migration Path

### From Current to Schema-Per-Tenant

#### Step 1: Prepare Infrastructure
1. Update tenants table with schema information
2. Deploy new multi-tenant configuration
3. Test with new tenants first

#### Step 2: Gradual Migration
```sql
-- For each existing tenant:
BEGIN;
-- 1. Create new schema
CREATE SCHEMA tenant_clinic1;

-- 2. Copy structure
CREATE TABLE tenant_clinic1.patients (LIKE public.patients INCLUDING ALL);

-- 3. Copy data
INSERT INTO tenant_clinic1.patients 
SELECT * FROM public.patients WHERE tenant_id = 'clinic1-abc123';

-- 4. Verify data
-- 5. Update tenant record
UPDATE tenants SET database_schema = 'tenant_clinic1' WHERE tenant_id = 'clinic1-abc123';
COMMIT;
```

#### Step 3: Cleanup
1. Remove tenant_id columns from public schema
2. Archive old data
3. Update monitoring and backups

## Best Practices

### 1. Naming Conventions
- Schema names: `tenant_{subdomain}` (lowercase, underscores)
- Maximum 63 characters (PostgreSQL limit)
- Avoid special characters

### 2. Security
- Use connection-level schema isolation
- Implement Row Level Security as backup
- Regular security audits
- Encrypt sensitive data at rest

### 3. Performance
- Monitor schema sizes
- Regular VACUUM and ANALYZE
- Schema-specific indexes
- Connection pool tuning

### 4. Maintenance
- Automated schema creation
- Consistent migration process
- Regular backups
- Schema version tracking

## Monitoring & Metrics

### Key Metrics to Track
1. **Schema Size**: Storage per tenant
2. **Query Performance**: Slow queries per schema
3. **Connection Usage**: Active connections per tenant
4. **Migration Status**: Schema versions
5. **Backup Status**: Last successful backup per tenant

### Dashboards
```sql
-- Tenant health dashboard query
SELECT 
    t.tenant_id,
    t.name,
    t.database_schema,
    pg_size_pretty(pg_database_size(current_database())) as db_size,
    (SELECT count(*) FROM pg_stat_activity WHERE schemaname = t.database_schema) as active_connections,
    t.schema_version,
    t.updated_at as last_activity
FROM public.tenants t
WHERE t.is_active = true
ORDER BY t.name;
```

## Risk Mitigation

### 1. Data Isolation Failures
- **Risk**: Cross-tenant data access
- **Mitigation**: Multiple validation layers, RLS policies, audit logging

### 2. Performance Degradation
- **Risk**: One tenant affecting others
- **Mitigation**: Schema isolation, resource limits, monitoring

### 3. Migration Failures
- **Risk**: Data loss during migration
- **Mitigation**: Comprehensive backups, staged rollout, rollback procedures

### 4. Operational Complexity
- **Risk**: Increased management overhead
- **Mitigation**: Automation, standardized procedures, monitoring

## Future Considerations

### 1. Sharding Strategy
- Horizontal sharding when exceeding 1000 tenants
- Geographic distribution for latency optimization

### 2. Read Replicas
- Tenant-specific read replicas for large clients
- Load balancing for read-heavy workloads

### 3. Data Archival
- Automated archival of inactive tenant data
- Compliance with data retention policies

### 4. Multi-Region Deployment
- Regional database clusters
- Cross-region replication for disaster recovery

## Conclusion

The schema-per-tenant approach provides the optimal balance of:
- **Security**: Strong isolation between tenants
- **Performance**: Independent resource usage
- **Flexibility**: Per-tenant customization
- **Manageability**: Single database instance
- **Cost-effectiveness**: Shared infrastructure

This architecture supports ClinicX's growth from tens to hundreds of tenants while maintaining security, performance, and operational efficiency.