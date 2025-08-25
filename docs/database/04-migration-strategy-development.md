# Migration Strategy for Development to Schema-Per-Tenant

## Overview

Since ClinicX is still in development with no production data, this document outlines a straightforward migration approach to implement schema-per-tenant from the beginning.

## Current Situation

- **No production data** to migrate
- **Development/testing data** only
- **Opportunity** to implement the right architecture from the start
- **No downtime concerns** - can rebuild from scratch

## Recommended Approach: Direct Implementation

### Why Direct Implementation?

Since you have no production data:
1. ✅ No complex migration needed
2. ✅ No dual-write complexity
3. ✅ No rollback concerns
4. ✅ Start with best practices
5. ✅ Clean implementation

## Implementation Steps

### Step 1: Update Database Schema (Week 1)

#### 1.1 Clean Existing Development Data
```sql
-- If you have test data you don't need
DROP DATABASE IF EXISTS clinicx;
CREATE DATABASE clinicx;

-- Or if you want to keep the database structure
TRUNCATE TABLE patients, appointments, staff, invoices CASCADE;
```

#### 1.2 Update Tenants Table
```sql
-- Add schema tracking columns
ALTER TABLE public.tenants 
ADD COLUMN IF NOT EXISTS database_schema VARCHAR(63) UNIQUE,
ADD COLUMN IF NOT EXISTS schema_version VARCHAR(20) DEFAULT '1.0.0',
ADD COLUMN IF NOT EXISTS schema_status VARCHAR(20) DEFAULT 'ACTIVE';

-- Add constraint
ALTER TABLE public.tenants
ADD CONSTRAINT chk_schema_name CHECK (database_schema ~ '^[a-z][a-z0-9_]*$');
```

#### 1.3 Create Schema Management Functions
```sql
-- Function to create tenant schema
CREATE OR REPLACE FUNCTION create_tenant_schema(
    p_schema_name VARCHAR(63)
) RETURNS VOID AS $$
BEGIN
    -- Create schema
    EXECUTE format('CREATE SCHEMA IF NOT EXISTS %I', p_schema_name);
    
    -- Grant permissions
    EXECUTE format('GRANT ALL ON SCHEMA %I TO clinicx_app', p_schema_name);
    
    RAISE NOTICE 'Schema % created successfully', p_schema_name;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

### Step 2: Update Application Code (Week 1-2)

#### 2.1 Remove Any Tenant_ID Columns
Since we're using schema isolation, we don't need tenant_id columns:

```java
// OLD - Don't do this
@Entity
public class Patient {
    @Column(name = "tenant_id")
    private String tenantId; // Remove this
}

// NEW - Clean entities
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    private UUID id;
    private String name;
    // No tenant_id needed!
}
```

#### 2.2 Implement Multi-Tenant Configuration
Use the configuration from the implementation guide:
- Configure Hibernate for SCHEMA strategy
- Implement CurrentTenantIdentifierResolver
- Set up MultiTenantConnectionProvider

### Step 3: Create Migration Scripts (Week 2)

#### 3.1 Flyway Directory Structure
```
src/main/resources/db/migration/
├── public/                        # System-wide tables
│   ├── V1__create_tenants.sql
│   ├── V2__add_schema_support.sql
│   └── V3__create_functions.sql
└── tenant/                        # Per-tenant tables
    ├── V1__create_patients.sql
    ├── V2__create_appointments.sql
    ├── V3__create_staff.sql
    ├── V4__create_invoices.sql
    └── V5__create_payments.sql
```

#### 3.2 Example Tenant Schema Migration
```sql
-- V1__create_patients.sql
CREATE TABLE IF NOT EXISTS patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    date_of_birth DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patients_email ON patients(email);
CREATE INDEX idx_patients_phone ON patients(phone);
```

### Step 4: Update Development Workflow

#### 4.1 Development Environment Setup
```yaml
# docker-compose.yml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: clinicx
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - ./init-scripts:/docker-entrypoint-initdb.d
      
  keycloak:
    image: quay.io/keycloak/keycloak:latest
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "18081:8080"
    command: start-dev
```

#### 4.2 Development Data Setup Script
```bash
#!/bin/bash
# setup-dev-tenants.sh

# Create test tenants
curl -X POST http://localhost:8080/api/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "subdomain": "demo-clinic",
    "name": "Demo Clinic",
    "adminEmail": "admin@demo.com",
    "adminPassword": "demo123"
  }'

curl -X POST http://localhost:8080/api/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "subdomain": "test-dental",
    "name": "Test Dental",
    "specialty": "DENTAL",
    "adminEmail": "admin@dental.com",
    "adminPassword": "dental123"
  }'
```

### Step 5: Testing Strategy

#### 5.1 Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "app.multi-tenant.enabled=true",
    "spring.jpa.properties.hibernate.multiTenancy=SCHEMA"
})
class MultiTenantIntegrationTest {
    
    @Test
    void testTenantIsolation() {
        // Create patient in tenant1
        TenantContext.setCurrentTenant("demo-clinic");
        Patient patient1 = createPatient("John Doe");
        
        // Switch to tenant2
        TenantContext.setCurrentTenant("test-dental");
        List<Patient> patients = patientRepository.findAll();
        
        // Verify isolation
        assertThat(patients).isEmpty();
    }
    
    @Test
    void testSchemaCreation() {
        // Create new tenant
        CreateTenantRequest request = new CreateTenantRequest();
        request.setSubdomain("new-clinic");
        request.setName("New Clinic");
        
        TenantDto tenant = tenantService.createTenant(request);
        
        // Verify schema exists
        boolean schemaExists = verifySchemaExists("tenant_new_clinic");
        assertThat(schemaExists).isTrue();
    }
}
```

## Simplified Architecture Decisions

### What You DON'T Need (Since No Production Data)

1. ❌ **Dual-write mechanisms**
2. ❌ **Data migration scripts**
3. ❌ **Rollback procedures**
4. ❌ **Gradual migration**
5. ❌ **Backward compatibility**

### What You SHOULD Focus On

1. ✅ **Clean schema-per-tenant implementation**
2. ✅ **Proper testing from the start**
3. ✅ **Good development workflow**
4. ✅ **Performance benchmarking**
5. ✅ **Security validation**

## Development Best Practices

### 1. Use Separate Schemas from Day One
```java
@Component
public class TenantSchemaInitializer {
    
    @EventListener(TenantCreatedEvent.class)
    public void onTenantCreated(TenantCreatedEvent event) {
        String schemaName = "tenant_" + event.getSubdomain();
        
        // Create schema
        schemaService.createSchema(schemaName);
        
        // Run migrations
        flywayService.migrateSchema(schemaName);
        
        // Initialize default data
        initializeDefaultData(schemaName);
    }
}
```

### 2. Automated Testing
```java
@TestConfiguration
public class TestTenantConfiguration {
    
    @Bean
    @Primary
    public TenantService testTenantService() {
        return new TenantService() {
            @PostConstruct
            public void setupTestTenants() {
                createTenant("test-tenant-1", "tenant_test_1");
                createTenant("test-tenant-2", "tenant_test_2");
            }
        };
    }
}
```

### 3. Development Seed Data
```sql
-- seed-data.sql for each tenant schema
INSERT INTO patients (name, email, phone) VALUES
    ('Test Patient 1', 'patient1@test.com', '123-456-7890'),
    ('Test Patient 2', 'patient2@test.com', '123-456-7891');

INSERT INTO staff (name, email, role) VALUES
    ('Dr. Test', 'doctor@test.com', 'DOCTOR'),
    ('Nurse Test', 'nurse@test.com', 'NURSE');
```

## Performance Testing Early

### Load Testing Script
```bash
#!/bin/bash
# load-test-tenants.sh

# Create multiple test tenants
for i in {1..50}; do
    curl -X POST http://localhost:8080/api/tenants \
        -H "Content-Type: application/json" \
        -d "{
            \"subdomain\": \"load-test-$i\",
            \"name\": \"Load Test Clinic $i\"
        }"
done

# Run concurrent requests
ab -n 10000 -c 100 \
   -H "Authorization: Bearer $TOKEN" \
   http://localhost:8080/api/patients
```

## Monitoring from Start

### Essential Metrics
```java
@Component
public class TenantMetrics {
    
    @Autowired
    private MeterRegistry registry;
    
    public void recordSchemaSwitch(String tenant, long duration) {
        registry.timer("schema.switch.duration", "tenant", tenant)
                .record(duration, TimeUnit.MILLISECONDS);
    }
    
    public void recordQueryPerTenant(String tenant, String operation) {
        registry.counter("tenant.queries", 
            "tenant", tenant, 
            "operation", operation
        ).increment();
    }
}
```

## Pre-Production Checklist

Before going to production:

- [ ] All entities created in tenant schemas (not public)
- [ ] No tenant_id columns in domain tables
- [ ] Schema creation automated
- [ ] Backup procedures tested
- [ ] Performance benchmarked
- [ ] Security audit completed
- [ ] Monitoring dashboards ready
- [ ] Documentation updated

## Summary

Since you're still in development:

1. **Implement schema-per-tenant directly** - no migration needed
2. **Start with clean architecture** - avoid technical debt
3. **Test thoroughly** - easier to fix issues now
4. **Benchmark early** - understand performance characteristics
5. **Document everything** - for future team members

This approach is much simpler than migrating production data and gives you a clean, scalable architecture from the beginning!