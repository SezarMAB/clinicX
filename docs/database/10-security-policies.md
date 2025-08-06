# Security Policies for Multi-Tenant Database Architecture

## Overview

This document outlines comprehensive security policies and implementations for ClinicX's schema-per-tenant PostgreSQL architecture, ensuring complete tenant isolation and HIPAA compliance.

## Core Security Principles

1. **Zero Trust**: Never trust, always verify
2. **Defense in Depth**: Multiple layers of security
3. **Least Privilege**: Minimum necessary access
4. **Audit Everything**: Complete audit trail
5. **Fail Secure**: Deny by default

## 1. Row-Level Security (RLS) Implementation

### 1.1 Enable RLS on All Tables

```sql
-- Enable RLS for tenant schemas
CREATE OR REPLACE FUNCTION enable_rls_for_schema(schema_name TEXT)
RETURNS void AS $$
DECLARE
    tbl TEXT;
BEGIN
    FOR tbl IN 
        SELECT tablename 
        FROM pg_tables 
        WHERE schemaname = schema_name
        AND tablename NOT IN ('schema_version', 'audit_log')
    LOOP
        EXECUTE format('ALTER TABLE %I.%I ENABLE ROW LEVEL SECURITY', schema_name, tbl);
        EXECUTE format('ALTER TABLE %I.%I FORCE ROW LEVEL SECURITY', schema_name, tbl);
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Apply to all tenant schemas
DO $$
DECLARE
    schema_record RECORD;
BEGIN
    FOR schema_record IN 
        SELECT schema_name 
        FROM information_schema.schemata 
        WHERE schema_name LIKE 'tenant_%'
    LOOP
        PERFORM enable_rls_for_schema(schema_record.schema_name);
    END LOOP;
END $$;
```

### 1.2 RLS Policies for Extra Protection

```sql
-- Policy to ensure users can only access their tenant's schema
CREATE POLICY tenant_isolation_policy ON public.tenants
    USING (
        tenant_id = current_setting('app.current_tenant', true)
        OR current_user = 'postgres'  -- Admin exception
    );

-- Function to create RLS policies for tenant tables
CREATE OR REPLACE FUNCTION create_tenant_rls_policy(
    schema_name TEXT,
    table_name TEXT
)
RETURNS void AS $$
BEGIN
    -- Drop existing policy if exists
    EXECUTE format('DROP POLICY IF EXISTS %I ON %I.%I',
        table_name || '_tenant_policy',
        schema_name,
        table_name
    );
    
    -- Create new policy
    EXECUTE format('
        CREATE POLICY %I ON %I.%I
        FOR ALL
        USING (true)  -- Within schema, all access allowed
        WITH CHECK (true)',
        table_name || '_tenant_policy',
        schema_name,
        table_name
    );
END;
$$ LANGUAGE plpgsql;
```

## 2. Database User Security

### 2.1 User Roles and Permissions

```sql
-- Create role hierarchy
CREATE ROLE clinicx_admin NOLOGIN;
CREATE ROLE clinicx_app NOLOGIN;
CREATE ROLE clinicx_readonly NOLOGIN;

-- Admin role - full access to public schema only
GRANT ALL ON SCHEMA public TO clinicx_admin;
GRANT ALL ON ALL TABLES IN SCHEMA public TO clinicx_admin;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO clinicx_admin;

-- Application role - limited access
GRANT USAGE ON SCHEMA public TO clinicx_app;
GRANT SELECT ON public.tenants TO clinicx_app;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO clinicx_app;

-- Read-only role for reporting
GRANT USAGE ON SCHEMA public TO clinicx_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO clinicx_readonly;

-- Create actual users
CREATE USER app_user WITH PASSWORD 'strong_password_here' IN ROLE clinicx_app;
CREATE USER admin_user WITH PASSWORD 'admin_password_here' IN ROLE clinicx_admin;
CREATE USER report_user WITH PASSWORD 'report_password_here' IN ROLE clinicx_readonly;
```

### 2.2 Dynamic Schema Permissions

```sql
-- Function to grant permissions on tenant schema
CREATE OR REPLACE FUNCTION grant_tenant_schema_access(
    p_schema_name TEXT,
    p_role_name TEXT DEFAULT 'clinicx_app'
)
RETURNS void AS $$
BEGIN
    -- Grant schema usage
    EXECUTE format('GRANT USAGE ON SCHEMA %I TO %I', p_schema_name, p_role_name);
    
    -- Grant table permissions
    EXECUTE format('GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA %I TO %I', 
        p_schema_name, p_role_name);
    
    -- Grant sequence permissions
    EXECUTE format('GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA %I TO %I', 
        p_schema_name, p_role_name);
    
    -- Set default privileges for future objects
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO %I', 
        p_schema_name, p_role_name);
    
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT USAGE, SELECT ON SEQUENCES TO %I', 
        p_schema_name, p_role_name);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

## 3. Connection Security

### 3.1 SSL/TLS Configuration

```ini
# postgresql.conf
ssl = on
ssl_cert_file = '/etc/postgresql/server.crt'
ssl_key_file = '/etc/postgresql/server.key'
ssl_ca_file = '/etc/postgresql/root.crt'
ssl_crl_file = '/etc/postgresql/root.crl'

# Require SSL for all connections
ssl_min_protocol_version = 'TLSv1.2'
ssl_ciphers = 'HIGH:MEDIUM:+3DES:!aNULL'
```

### 3.2 pg_hba.conf Configuration

```conf
# TYPE  DATABASE        USER            ADDRESS                 METHOD
# Reject all non-SSL connections
hostnossl all          all             0.0.0.0/0               reject

# Require SSL + password for app connections
hostssl   clinicx      app_user        10.0.0.0/8              md5
hostssl   clinicx      report_user     10.0.0.0/8              md5

# Admin requires SSL + certificate
hostssl   all          admin_user      10.0.0.0/8              cert

# Local connections for maintenance
local     all          postgres                                peer
```

### 3.3 Connection String Security

```java
@Configuration
public class SecureDataSourceConfig {
    
    @Value("${database.password}")
    private String encryptedPassword;
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // Use SSL
        config.setJdbcUrl("jdbc:postgresql://db.clinicx.io:5432/clinicx" +
            "?ssl=true" +
            "&sslmode=require" +
            "&sslcert=/app/certs/client.crt" +
            "&sslkey=/app/certs/client.key" +
            "&sslrootcert=/app/certs/ca.crt");
        
        // Decrypt password at runtime
        config.setPassword(decryptPassword(encryptedPassword));
        
        // Set secure connection properties
        config.addDataSourceProperty("tcpKeepAlive", "true");
        config.addDataSourceProperty("socketTimeout", "30");
        
        return new HikariDataSource(config);
    }
    
    private String decryptPassword(String encrypted) {
        // Use AWS KMS, HashiCorp Vault, or similar
        return kmsService.decrypt(encrypted);
    }
}
```

## 4. Query Security

### 4.1 SQL Injection Prevention

```java
@Repository
public class SecurePatientRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    // NEVER do this
    @Deprecated
    public List<Patient> badSearch(String name) {
        String sql = "SELECT * FROM patients WHERE name = '" + name + "'";
        return jdbcTemplate.query(sql, new PatientRowMapper());
    }
    
    // ALWAYS do this - use parameterized queries
    public List<Patient> secureSearch(String name) {
        String sql = "SELECT * FROM patients WHERE name = :name";
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("name", name);
        return jdbcTemplate.query(sql, params, new PatientRowMapper());
    }
    
    // For dynamic queries, use the criteria API
    public List<Patient> dynamicSearch(PatientSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
        Root<Patient> root = query.from(Patient.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (criteria.getName() != null) {
            predicates.add(cb.like(root.get("name"), "%" + criteria.getName() + "%"));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }
}
```

### 4.2 Schema Injection Prevention

```java
@Component
public class SecureSchemaResolver {
    
    private static final Pattern VALID_SCHEMA_PATTERN = Pattern.compile("^tenant_[a-z0-9_]+$");
    
    public String resolveSchema(String tenantId) {
        String schema = "tenant_" + sanitizeTenantId(tenantId);
        
        if (!VALID_SCHEMA_PATTERN.matcher(schema).matches()) {
            throw new SecurityException("Invalid schema name: " + schema);
        }
        
        // Additional validation against database
        if (!schemaExists(schema)) {
            throw new SecurityException("Schema does not exist: " + schema);
        }
        
        return schema;
    }
    
    private String sanitizeTenantId(String tenantId) {
        return tenantId.toLowerCase()
            .replaceAll("[^a-z0-9]", "_")
            .replaceAll("_{2,}", "_");
    }
    
    private boolean schemaExists(String schema) {
        String sql = "SELECT EXISTS(SELECT 1 FROM information_schema.schemata WHERE schema_name = :schema)";
        return jdbcTemplate.queryForObject(sql, Map.of("schema", schema), Boolean.class);
    }
}
```

## 5. Audit and Compliance

### 5.1 Comprehensive Audit Logging

```sql
-- Audit table in public schema
CREATE TABLE public.audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    tenant_id VARCHAR(100),
    schema_name VARCHAR(63),
    user_id VARCHAR(255),
    session_id VARCHAR(255),
    action VARCHAR(50) NOT NULL,
    table_name VARCHAR(100),
    record_id VARCHAR(255),
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    success BOOLEAN NOT NULL DEFAULT true,
    error_message TEXT
);

-- Index for performance
CREATE INDEX idx_audit_log_tenant_timestamp ON audit_log(tenant_id, timestamp);
CREATE INDEX idx_audit_log_user_timestamp ON audit_log(user_id, timestamp);
CREATE INDEX idx_audit_log_action ON audit_log(action, timestamp);

-- Partition by month for better performance
CREATE TABLE audit_log_2024_01 PARTITION OF audit_log
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

### 5.2 Audit Trigger Function

```sql
CREATE OR REPLACE FUNCTION audit_trigger()
RETURNS TRIGGER AS $$
DECLARE
    audit_user TEXT;
    audit_tenant TEXT;
    audit_schema TEXT;
    old_data JSONB;
    new_data JSONB;
BEGIN
    -- Get context
    audit_user := coalesce(current_setting('app.current_user', true), current_user);
    audit_tenant := current_setting('app.current_tenant', true);
    audit_schema := TG_TABLE_SCHEMA;
    
    -- Prepare data
    IF TG_OP = 'DELETE' THEN
        old_data := to_jsonb(OLD);
        new_data := NULL;
    ELSIF TG_OP = 'UPDATE' THEN
        old_data := to_jsonb(OLD);
        new_data := to_jsonb(NEW);
    ELSIF TG_OP = 'INSERT' THEN
        old_data := NULL;
        new_data := to_jsonb(NEW);
    END IF;
    
    -- Insert audit record
    INSERT INTO public.audit_log (
        tenant_id,
        schema_name,
        user_id,
        action,
        table_name,
        record_id,
        old_values,
        new_values
    ) VALUES (
        audit_tenant,
        audit_schema,
        audit_user,
        TG_OP,
        TG_TABLE_NAME,
        CASE 
            WHEN TG_OP = 'DELETE' THEN (OLD.id)::TEXT
            ELSE (NEW.id)::TEXT
        END,
        old_data,
        new_data
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Apply to sensitive tables
CREATE TRIGGER audit_patients
    AFTER INSERT OR UPDATE OR DELETE ON patients
    FOR EACH ROW EXECUTE FUNCTION audit_trigger();
```

### 5.3 Application-Level Audit Service

```java
@Service
@Slf4j
public class AuditService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private HttpServletRequest request;
    
    @Value("${app.audit.enabled:true}")
    private boolean auditEnabled;
    
    public void auditAccess(String action, String resource, boolean success, String errorMessage) {
        if (!auditEnabled) return;
        
        try {
            String sql = """
                INSERT INTO public.audit_log 
                (tenant_id, user_id, session_id, action, table_name, 
                 ip_address, user_agent, success, error_message)
                VALUES (?, ?, ?, ?, ?, ?::inet, ?, ?, ?)
                """;
            
            jdbcTemplate.update(sql,
                TenantContext.getCurrentTenant(),
                SecurityContext.getCurrentUser(),
                request.getSession().getId(),
                action,
                resource,
                getClientIp(),
                request.getHeader("User-Agent"),
                success,
                errorMessage
            );
        } catch (Exception e) {
            log.error("Failed to write audit log", e);
            // Don't throw - audit failure shouldn't break the app
        }
    }
    
    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

## 6. Data Encryption

### 6.1 Encryption at Rest

```sql
-- Enable transparent data encryption (TDE) if using PostgreSQL 15+
-- Or use filesystem encryption

-- For sensitive columns, use pgcrypto
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Example: Encrypt SSN
ALTER TABLE patients 
ADD COLUMN ssn_encrypted BYTEA;

-- Encrypt existing data
UPDATE patients 
SET ssn_encrypted = pgp_sym_encrypt(ssn, current_setting('app.encryption_key'))
WHERE ssn IS NOT NULL;

-- Create view for transparent decryption
CREATE VIEW patients_decrypted AS
SELECT 
    id,
    name,
    pgp_sym_decrypt(ssn_encrypted, current_setting('app.encryption_key')) as ssn,
    -- other columns
FROM patients;
```

### 6.2 Application-Level Encryption

```java
@Component
public class FieldEncryptor {
    
    @Value("${encryption.key}")
    private String masterKey;
    
    private SecretKey secretKey;
    
    @PostConstruct
    public void init() {
        // Derive key from master key
        this.secretKey = deriveKey(masterKey);
    }
    
    public String encrypt(String plaintext) {
        if (plaintext == null) return null;
        
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and ciphertext
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new SecurityException("Encryption failed", e);
        }
    }
    
    public String decrypt(String encrypted) {
        if (encrypted == null) return null;
        
        try {
            byte[] combined = Base64.getDecoder().decode(encrypted);
            byte[] iv = Arrays.copyOfRange(combined, 0, 12);
            byte[] ciphertext = Arrays.copyOfRange(combined, 12, combined.length);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
            
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException("Decryption failed", e);
        }
    }
}
```

## 7. Access Control

### 7.1 Tenant Access Validation

```java
@Aspect
@Component
@Slf4j
public class TenantAccessAspect {
    
    @Autowired
    private TenantAccessValidator validator;
    
    @Around("@annotation(ValidateTenantAccess)")
    public Object validateAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        String currentUser = SecurityContext.getCurrentUser();
        String requestedTenant = TenantContext.getCurrentTenant();
        
        if (!validator.hasAccess(currentUser, requestedTenant)) {
            log.warn("Access denied: User {} attempted to access tenant {}", 
                currentUser, requestedTenant);
            throw new AccessDeniedException("Access denied to tenant: " + requestedTenant);
        }
        
        return joinPoint.proceed();
    }
}

@Service
public class TenantAccessValidator {
    
    @Autowired
    private UserTenantAccessRepository accessRepository;
    
    @Cacheable(value = "tenant-access", key = "#userId + ':' + #tenantId")
    public boolean hasAccess(String userId, String tenantId) {
        // Super admin has access to all tenants
        if (isSuperAdmin(userId)) {
            return true;
        }
        
        // Check user-tenant mapping
        return accessRepository.existsByUserIdAndTenantIdAndActive(
            userId, tenantId, true
        );
    }
    
    private boolean isSuperAdmin(String userId) {
        return SecurityContext.hasRole("SUPER_ADMIN");
    }
}
```

### 7.2 Resource-Level Security

```java
@PreAuthorize("hasPermission(#patientId, 'Patient', 'READ')")
public Patient getPatient(UUID patientId) {
    return patientRepository.findById(patientId)
        .orElseThrow(() -> new NotFoundException("Patient not found"));
}

@Component
public class TenantPermissionEvaluator implements PermissionEvaluator {
    
    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        // Implement tenant-aware permission logic
        if (targetDomainObject instanceof TenantAware) {
            TenantAware entity = (TenantAware) targetDomainObject;
            return entity.getTenantId().equals(TenantContext.getCurrentTenant());
        }
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, 
                               String targetType, Object permission) {
        // Load entity and check tenant
        Object entity = loadEntity(targetType, targetId);
        return hasPermission(auth, entity, permission);
    }
}
```

## 8. Security Monitoring

### 8.1 Real-Time Threat Detection

```java
@Component
@Slf4j
public class SecurityMonitor {
    
    private final Map<String, AtomicInteger> failedAttempts = new ConcurrentHashMap<>();
    
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String ip = getClientIp();
        
        int attempts = failedAttempts.computeIfAbsent(ip, k -> new AtomicInteger(0))
            .incrementAndGet();
        
        if (attempts > 5) {
            log.error("Possible brute force attack from IP: {} for user: {}", ip, username);
            blockIp(ip);
        }
        
        auditService.auditAccess("LOGIN_FAILED", username, false, 
            event.getException().getMessage());
    }
    
    @EventListener
    public void handleCrossTenantAccess(CrossTenantAccessEvent event) {
        log.error("Cross-tenant access attempt: User {} from tenant {} tried to access tenant {}",
            event.getUserId(), event.getUserTenant(), event.getTargetTenant());
        
        // Alert security team
        alertService.sendSecurityAlert(
            "Cross-Tenant Access Attempt",
            event
        );
    }
}
```

### 8.2 Security Metrics

```sql
-- Failed login attempts by tenant
CREATE VIEW security_metrics AS
SELECT 
    tenant_id,
    DATE_TRUNC('hour', timestamp) as hour,
    COUNT(*) FILTER (WHERE action = 'LOGIN_FAILED') as failed_logins,
    COUNT(*) FILTER (WHERE action = 'ACCESS_DENIED') as access_denied,
    COUNT(DISTINCT user_id) as unique_users,
    COUNT(DISTINCT ip_address) as unique_ips
FROM audit_log
WHERE timestamp > NOW() - INTERVAL '24 hours'
GROUP BY tenant_id, hour
ORDER BY hour DESC;

-- Suspicious activity detection
CREATE VIEW suspicious_activity AS
SELECT 
    user_id,
    tenant_id,
    COUNT(*) as failed_attempts,
    COUNT(DISTINCT ip_address) as different_ips,
    MIN(timestamp) as first_attempt,
    MAX(timestamp) as last_attempt
FROM audit_log
WHERE action IN ('LOGIN_FAILED', 'ACCESS_DENIED')
    AND timestamp > NOW() - INTERVAL '1 hour'
GROUP BY user_id, tenant_id
HAVING COUNT(*) > 5
    OR COUNT(DISTINCT ip_address) > 3;
```

## 9. HIPAA Compliance

### 9.1 PHI Access Controls

```java
@Component
public class PHIAccessController {
    
    @PreAuthorize("hasRole('ROLE_HEALTHCARE_PROVIDER')")
    @Audited(action = "PHI_ACCESS")
    public PatientRecord accessPHI(UUID patientId, String reason) {
        // Log the access reason for HIPAA compliance
        auditService.auditPHIAccess(patientId, reason);
        
        // Check if user has treatment relationship
        if (!haseTreatmentRelationship(getCurrentUser(), patientId)) {
            throw new AccessDeniedException("No treatment relationship exists");
        }
        
        return patientRepository.findById(patientId)
            .orElseThrow(() -> new NotFoundException("Patient not found"));
    }
    
    private boolean hasTreatmentRelationship(String userId, UUID patientId) {
        return jdbcTemplate.queryForObject(
            """
            SELECT EXISTS(
                SELECT 1 FROM appointments 
                WHERE patient_id = ? 
                AND provider_id = ? 
                AND status IN ('SCHEDULED', 'COMPLETED')
            )
            """,
            Boolean.class,
            patientId,
            userId
        );
    }
}
```

### 9.2 Data Retention Policies

```sql
-- Automated PHI retention management
CREATE OR REPLACE FUNCTION enforce_retention_policy()
RETURNS void AS $$
DECLARE
    retention_years INTEGER := 7; -- HIPAA minimum
BEGIN
    -- Archive old records
    INSERT INTO archived_patients
    SELECT * FROM patients
    WHERE updated_at < NOW() - INTERVAL '7 years';
    
    -- Delete from active tables
    DELETE FROM patients
    WHERE updated_at < NOW() - INTERVAL '7 years'
    AND id IN (SELECT id FROM archived_patients);
    
    -- Log retention action
    INSERT INTO audit_log (action, table_name, new_values)
    VALUES ('RETENTION_POLICY_APPLIED', 'patients', 
        jsonb_build_object('archived_count', row_count()));
END;
$$ LANGUAGE plpgsql;

-- Schedule monthly
CREATE EXTENSION IF NOT EXISTS pg_cron;
SELECT cron.schedule('retention-policy', '0 2 1 * *', 'SELECT enforce_retention_policy()');
```

## 10. Incident Response

### 10.1 Security Incident Procedures

```java
@Service
@Slf4j
public class SecurityIncidentHandler {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private TenantService tenantService;
    
    public void handleSecurityIncident(SecurityIncident incident) {
        log.error("SECURITY INCIDENT: {}", incident);
        
        switch (incident.getSeverity()) {
            case CRITICAL:
                // Immediate action
                lockdownTenant(incident.getTenantId());
                notifySecurityTeam(incident);
                createIncidentTicket(incident);
                break;
                
            case HIGH:
                // Alert and monitor
                alertAdministrators(incident);
                increasedMonitoring(incident.getTenantId());
                break;
                
            case MEDIUM:
                // Log and track
                logIncident(incident);
                scheduleReview(incident);
                break;
        }
    }
    
    private void lockdownTenant(String tenantId) {
        // Temporarily disable tenant access
        tenantService.updateTenantStatus(tenantId, "SECURITY_LOCKDOWN");
        
        // Revoke all active sessions
        sessionRegistry.expireSessionsForTenant(tenantId);
        
        // Block new logins
        loginBlocker.blockTenant(tenantId);
        
        log.warn("Tenant {} locked down due to security incident", tenantId);
    }
}
```

### 10.2 Incident Response Checklist

```yaml
incident_response:
  detection:
    - Monitor audit logs continuously
    - Set up alerts for suspicious patterns
    - Regular security scans
    
  containment:
    - Isolate affected tenant
    - Revoke compromised credentials
    - Block suspicious IPs
    
  investigation:
    - Analyze audit logs
    - Review access patterns
    - Check for data exfiltration
    
  recovery:
    - Restore from secure backup
    - Reset credentials
    - Apply security patches
    
  post_incident:
    - Document lessons learned
    - Update security policies
    - Conduct security training
```

## Security Best Practices Summary

1. **Always use parameterized queries** - Never concatenate SQL
2. **Validate schema names** - Prevent schema injection
3. **Encrypt sensitive data** - Both at rest and in transit
4. **Audit everything** - Maintain complete audit trail
5. **Implement RLS** - Extra layer of protection
6. **Use least privilege** - Minimal permissions
7. **Monitor continuously** - Real-time threat detection
8. **Regular security reviews** - Penetration testing
9. **Incident response plan** - Be prepared
10. **HIPAA compliance** - Follow healthcare regulations

## Monitoring Dashboard Queries

```sql
-- Security overview
SELECT 
    COUNT(DISTINCT tenant_id) as total_tenants,
    COUNT(*) FILTER (WHERE action = 'LOGIN_SUCCESS') as successful_logins,
    COUNT(*) FILTER (WHERE action = 'LOGIN_FAILED') as failed_logins,
    COUNT(*) FILTER (WHERE action = 'ACCESS_DENIED') as access_denied,
    COUNT(DISTINCT ip_address) as unique_ips
FROM audit_log
WHERE timestamp > NOW() - INTERVAL '24 hours';

-- Per-tenant security score
SELECT 
    t.tenant_id,
    t.name,
    CASE 
        WHEN failed_login_ratio > 0.2 THEN 'HIGH_RISK'
        WHEN failed_login_ratio > 0.1 THEN 'MEDIUM_RISK'
        ELSE 'LOW_RISK'
    END as risk_level,
    security_score
FROM (
    SELECT 
        tenant_id,
        COUNT(*) FILTER (WHERE action = 'LOGIN_FAILED')::FLOAT / 
            NULLIF(COUNT(*) FILTER (WHERE action = 'LOGIN_SUCCESS'), 0) as failed_login_ratio,
        100 - (COUNT(*) FILTER (WHERE success = false) * 10) as security_score
    FROM audit_log
    WHERE timestamp > NOW() - INTERVAL '7 days'
    GROUP BY tenant_id
) AS security_metrics
JOIN tenants t ON t.tenant_id = security_metrics.tenant_id
ORDER BY security_score ASC;
```

## Conclusion

Security in a multi-tenant environment requires:
- Multiple layers of protection
- Continuous monitoring
- Regular audits
- Quick incident response
- Compliance with regulations

This comprehensive approach ensures tenant data remains isolated, secure, and compliant with healthcare regulations.