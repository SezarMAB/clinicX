# Schema-Per-Tenant Implementation Guide

## Overview

This guide provides detailed implementation steps for schema-based multi-tenancy in ClinicX using PostgreSQL and Spring Boot with Hibernate.

## Prerequisites

- PostgreSQL 12+ (for proper schema support)
- Spring Boot 3.x with Hibernate 6.x
- Java 17+
- Existing multi-tenant Keycloak setup

## Implementation Steps

### Step 1: Database Preparation

#### 1.1 Create Schema Management Functions

```sql
-- Function to create a new tenant schema with proper permissions
CREATE OR REPLACE FUNCTION create_tenant_schema(
    p_schema_name VARCHAR(63),
    p_app_user VARCHAR(63) DEFAULT 'clinicx_app'
) RETURNS VOID AS $$
DECLARE
    v_sql TEXT;
BEGIN
    -- Validate schema name
    IF p_schema_name !~ '^[a-z][a-z0-9_]*$' THEN
        RAISE EXCEPTION 'Invalid schema name: %', p_schema_name;
    END IF;
    
    -- Create schema
    v_sql := format('CREATE SCHEMA IF NOT EXISTS %I', p_schema_name);
    EXECUTE v_sql;
    
    -- Grant permissions
    v_sql := format('GRANT ALL ON SCHEMA %I TO %I', p_schema_name, p_app_user);
    EXECUTE v_sql;
    
    -- Set default privileges for tables
    v_sql := format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON TABLES TO %I', 
                    p_schema_name, p_app_user);
    EXECUTE v_sql;
    
    -- Set default privileges for sequences
    v_sql := format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON SEQUENCES TO %I', 
                    p_schema_name, p_app_user);
    EXECUTE v_sql;
    
    RAISE NOTICE 'Schema % created successfully', p_schema_name;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function to drop a tenant schema safely
CREATE OR REPLACE FUNCTION drop_tenant_schema(
    p_schema_name VARCHAR(63)
) RETURNS VOID AS $$
BEGIN
    -- Validate this is a tenant schema
    IF p_schema_name NOT LIKE 'tenant_%' THEN
        RAISE EXCEPTION 'Cannot drop non-tenant schema: %', p_schema_name;
    END IF;
    
    -- Drop schema cascade
    EXECUTE format('DROP SCHEMA IF EXISTS %I CASCADE', p_schema_name);
    
    RAISE NOTICE 'Schema % dropped', p_schema_name;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

#### 1.2 Update Tenants Table

```sql
-- Add schema-related columns to tenants table
ALTER TABLE public.tenants 
ADD COLUMN IF NOT EXISTS database_schema VARCHAR(63) UNIQUE,
ADD COLUMN IF NOT EXISTS schema_version VARCHAR(20) DEFAULT '1.0.0',
ADD COLUMN IF NOT EXISTS schema_created_at TIMESTAMPTZ,
ADD COLUMN IF NOT EXISTS schema_status VARCHAR(20) DEFAULT 'PENDING';

-- Add check constraint for schema status
ALTER TABLE public.tenants 
ADD CONSTRAINT chk_schema_status 
CHECK (schema_status IN ('PENDING', 'CREATING', 'ACTIVE', 'MIGRATING', 'ERROR'));

-- Create index for schema lookups
CREATE INDEX IF NOT EXISTS idx_tenants_database_schema 
ON public.tenants(database_schema) 
WHERE database_schema IS NOT NULL;
```

### Step 2: Spring Boot Configuration

#### 2.1 Multi-Tenant Configuration Class

```java
package sy.sezar.clinicx.config;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SchemaBasedMultiTenantConfig {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            MultiTenantConnectionProvider multiTenantConnectionProvider,
            CurrentTenantIdentifierResolver currentTenantIdentifierResolver,
            JpaProperties jpaProperties) {
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("sy.sezar.clinicx");
        em.setJpaVendorAdapter(jpaVendorAdapter());
        
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        properties.put(AvailableSettings.MULTI_TENANT, "SCHEMA");
        
        em.setJpaPropertyMap(properties);
        return em;
    }
}
```

#### 2.2 Schema-Based Connection Provider

```java
package sy.sezar.clinicx.tenant.provider;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class SchemaBasedMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private TenantSchemaResolver schemaResolver;
    
    private static final String DEFAULT_SCHEMA = "public";

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = getAnyConnection();
        
        try {
            String schema = schemaResolver.resolveSchema(tenantIdentifier);
            setSchema(connection, schema);
        } catch (Exception e) {
            releaseAnyConnection(connection);
            throw new SQLException("Failed to set schema for tenant: " + tenantIdentifier, e);
        }
        
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            // Reset to default schema before returning to pool
            setSchema(connection, DEFAULT_SCHEMA);
        } finally {
            releaseAnyConnection(connection);
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
    
    private void setSchema(Connection connection, String schema) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO " + schema + ", public");
        }
    }
}
```

#### 2.3 Tenant Identifier Resolver

```java
package sy.sezar.clinicx.tenant.resolver;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.TenantContext;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        return tenantId != null ? tenantId : DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
```

#### 2.4 Schema Resolver Service

```java
package sy.sezar.clinicx.tenant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sy.sezar.clinicx.tenant.entity.Tenant;
import sy.sezar.clinicx.tenant.repository.TenantRepository;

@Service
public class TenantSchemaResolver {
    
    @Autowired
    private TenantRepository tenantRepository;
    
    private static final String SCHEMA_PREFIX = "tenant_";
    
    @Cacheable(value = "tenant-schemas", key = "#tenantId")
    public String resolveSchema(String tenantId) {
        if ("public".equals(tenantId)) {
            return "public";
        }
        
        return tenantRepository.findByTenantId(tenantId)
            .map(Tenant::getDatabaseSchema)
            .filter(schema -> schema != null && !schema.isEmpty())
            .orElseGet(() -> generateSchemaName(tenantId));
    }
    
    public String generateSchemaName(String tenantId) {
        // Convert tenant ID to valid schema name
        String sanitized = tenantId.toLowerCase()
            .replaceAll("[^a-z0-9]", "_")
            .replaceAll("^[0-9]", "t$0"); // Schema can't start with number
        
        return SCHEMA_PREFIX + sanitized;
    }
}
```

### Step 3: Schema Creation Service

```java
package sy.sezar.clinicx.tenant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.tenant.entity.Tenant;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TenantSchemaService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private FlywayMigrationService flywayService;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Transactional
    public void createTenantSchema(Tenant tenant) {
        String schemaName = generateSchemaName(tenant.getTenantId());
        
        try {
            // Update tenant status
            tenant.setSchemaStatus("CREATING");
            tenant.setDatabaseSchema(schemaName);
            tenantRepository.save(tenant);
            
            // Create schema using function
            jdbcTemplate.update("SELECT create_tenant_schema(?)", schemaName);
            
            // Run Flyway migrations
            flywayService.migrateTenantSchema(schemaName);
            
            // Update tenant record
            tenant.setSchemaStatus("ACTIVE");
            tenant.setSchemaCreatedAt(LocalDateTime.now());
            tenantRepository.save(tenant);
            
            log.info("Successfully created schema {} for tenant {}", schemaName, tenant.getTenantId());
            
        } catch (Exception e) {
            log.error("Failed to create schema for tenant {}", tenant.getTenantId(), e);
            tenant.setSchemaStatus("ERROR");
            tenantRepository.save(tenant);
            throw new RuntimeException("Schema creation failed", e);
        }
    }
    
    public void validateSchema(String schemaName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            var resultSet = connection.getMetaData().getSchemas();
            boolean exists = false;
            
            while (resultSet.next()) {
                if (schemaName.equals(resultSet.getString("TABLE_SCHEM"))) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                throw new IllegalStateException("Schema does not exist: " + schemaName);
            }
        }
    }
    
    private String generateSchemaName(String tenantId) {
        return "tenant_" + tenantId.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }
}
```

### Step 4: Flyway Multi-Schema Support

```java
package sy.sezar.clinicx.tenant.service;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

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
            .table("schema_version")
            .installedBy("system")
            .load();
        
        flyway.migrate();
    }
    
    public void migratePublicSchema() {
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas("public")
            .locations("classpath:db/migration/public")
            .baselineOnMigrate(true)
            .table("schema_version")
            .installedBy("system")
            .load();
        
        flyway.migrate();
    }
}
```

### Step 5: Integration with Tenant Creation

```java
@Service
@Transactional
public class EnhancedTenantService {
    
    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private TenantSchemaService schemaService;
    
    @Autowired
    private KeycloakAdminService keycloakService;
    
    public TenantDto createTenantWithSchema(CreateTenantRequest request) {
        // 1. Create Keycloak realm
        String realmName = keycloakService.createRealm(
            request.getSubdomain(),
            request.getName()
        );
        
        // 2. Create tenant record
        Tenant tenant = tenantService.createTenant(request);
        
        // 3. Create database schema
        schemaService.createTenantSchema(tenant);
        
        // 4. Create admin user
        keycloakService.createAdminUser(realmName, request);
        
        return TenantMapper.toDto(tenant);
    }
}
```

## Testing

### Unit Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class SchemaMultiTenantTest {
    
    @Test
    void testSchemaCreation() {
        // Test schema creation
        String tenantId = "test-clinic-123";
        String expectedSchema = "tenant_test_clinic_123";
        
        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantId);
        
        schemaService.createTenantSchema(tenant);
        
        assertThat(tenant.getDatabaseSchema()).isEqualTo(expectedSchema);
        assertThat(tenant.getSchemaStatus()).isEqualTo("ACTIVE");
    }
    
    @Test
    void testTenantIsolation() {
        // Create data in tenant1 schema
        TenantContext.setCurrentTenant("tenant1");
        Patient patient1 = new Patient();
        patient1.setName("John Doe");
        patientRepository.save(patient1);
        
        // Switch to tenant2
        TenantContext.setCurrentTenant("tenant2");
        List<Patient> tenant2Patients = patientRepository.findAll();
        
        // Verify isolation
        assertThat(tenant2Patients).isEmpty();
    }
}
```

## Troubleshooting

### Common Issues

1. **Schema Not Found**
   - Check schema exists: `\dn` in psql
   - Verify permissions: `\dn+`
   - Check search_path: `SHOW search_path;`

2. **Connection Pool Issues**
   - Monitor active connections per schema
   - Ensure proper connection release
   - Check for connection leaks

3. **Migration Failures**
   - Verify Flyway locations
   - Check schema permissions
   - Review migration scripts

## Next Steps

- Continue to [Migration Strategy](./03-migration-strategy.md)
- Review [Performance Tuning](./06-performance-tuning.md)
- Implement [Monitoring](./07-monitoring-metrics.md)