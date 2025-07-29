# SaaS Migration TODO Plan for ClinicX

## Overview
This plan outlines the migration path to transform ClinicX into a multi-tenant SaaS platform with feature modularity. Based on analysis, **implementing security AFTER Phase 1 & 2 is possible but NOT recommended**.

## Security Implementation Timing Analysis

### Option A: Security AFTER Phase 1 & 2 (Not Recommended ⚠️)
**Pros:**
- Faster initial development
- Focus on business logic first
- Simpler testing during development

**Cons:**
- Major refactoring required later
- Security vulnerabilities during development
- Difficult to retrofit tenant isolation
- Rework of all APIs and data access layers

### Option B: Basic Security in Phase 1, Full in Phase 3 (Recommended ✅)
**Pros:**
- Tenant isolation from the start
- No major refactoring later
- Secure development environment
- Progressive enhancement

**Cons:**
- Slightly slower Phase 1
- Need to learn security concepts early

## Recommended Approach: Minimal Security First

### Phase 0: Preparation (1 week)
**Goal:** Set up development environment and basic security

#### Development Environment
- [ ] Set up local Keycloak instance using Docker
  ```bash
  docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
  ```
- [ ] Create test realm "clinicx-dev"
- [ ] Create test users for development
- [ ] Document local setup in README

#### Temporary Security Layer
- [ ] Create `SimpleSecurityConfig.java` with basic auth
  ```java
  @Configuration
  @Profile("dev")
  public class SimpleSecurityConfig {
      // Basic security for development
      // Will be replaced with Keycloak later
  }
  ```
- [ ] Add mock `TenantContext` for development
- [ ] Create `@MockTenant` annotation for tests

### Phase 1: Multi-tenancy Foundation (4-6 weeks)

#### Week 1-2: Database Multi-tenancy
- [ ] Add Flyway configuration for migrations
- [ ] Create `public.tenants` table
  ```sql
  CREATE TABLE public.tenants (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      tenant_key VARCHAR(50) UNIQUE NOT NULL,
      clinic_name VARCHAR(200) NOT NULL,
      clinic_type VARCHAR(50) NOT NULL,
      schema_name VARCHAR(63) NOT NULL,
      active BOOLEAN DEFAULT true,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );
  ```
- [ ] Implement schema-per-tenant strategy
- [ ] Create `TenantDataSource` configuration
- [ ] Add tenant column to existing tables (preparation)
  ```sql
  ALTER TABLE patients ADD COLUMN tenant_id UUID;
  ALTER TABLE appointments ADD COLUMN tenant_id UUID;
  -- etc for all tables
  ```

#### Week 3-4: Tenant Context & Routing
- [ ] Implement `TenantContext` ThreadLocal holder
- [ ] Create `TenantInterceptor` (basic version)
  ```java
  // Initially use header-based for dev
  // Will upgrade to JWT-based later
  ```
- [ ] Add `@TenantAware` annotation
- [ ] Create `MultiTenantConnectionProvider`
- [ ] Implement `CurrentTenantIdentifierResolver`
- [ ] Add tenant validation to base repository

#### Week 5-6: Tenant Management
- [ ] Create tenant onboarding service
- [ ] Implement schema creation automation
- [ ] Add tenant switching for development
- [ ] Create data migration tools
- [ ] Test with 2-3 mock tenants
- [ ] Add basic tenant admin endpoints

### Phase 2: Feature Modularization (6-8 weeks)

#### Week 1-2: Code Reorganization
- [ ] Create module structure
  ```
  src/main/java/sy/sezar/clinicx/
  ├── core/          # Always loaded
  ├── patient/       # Always loaded  
  ├── appointment/   # Always loaded
  └── modules/
      ├── dental/    # Conditional
      ├── lab/       # Conditional
      └── financial/ # Conditional
  ```
- [ ] Move dental-specific code to `modules/dental`
  - [ ] DentalChartController
  - [ ] ToothCondition, PatientTooth entities
  - [ ] Dental services and repositories
- [ ] Move lab features to `modules/lab`
- [ ] Move advanced financial to `modules/financial`

#### Week 3-4: Feature Flag Implementation
- [ ] Add Togglz dependency
- [ ] Create `ClinicFeatures` enum
- [ ] Implement feature configuration
- [ ] Add `@ConditionalOnFeature` to modules
- [ ] Create feature flag admin UI
- [ ] Test feature toggling

#### Week 5-6: Conditional Loading
- [ ] Add conditional Flyway migrations
- [ ] Implement module-aware services
- [ ] Create feature-dependent DTOs
- [ ] Add OpenAPI conditional documentation
- [ ] Test different feature combinations
- [ ] Performance testing with/without features

#### Week 7-8: Integration Testing
- [ ] Create test fixtures for each clinic type
- [ ] End-to-end testing per feature set
- [ ] Load testing with multiple tenants
- [ ] Document feature dependencies
- [ ] Create onboarding checklist

### Phase 3: Security Layer (2-3 weeks) - AFTER Phase 1 & 2

#### Week 1: Keycloak Integration
- [ ] Replace simple security with Keycloak
- [ ] Configure Spring Security OAuth2
- [ ] Implement JWT validation
- [ ] Update `TenantInterceptor` to use JWT
- [ ] Add realm-per-tenant or claims-based approach
- [ ] Configure CORS properly

#### Week 2: Security Hardening
- [ ] Add method-level security
- [ ] Implement audit trail
- [ ] Add rate limiting per tenant
- [ ] Configure security headers
- [ ] Penetration testing
- [ ] Security documentation

## Alternative: Minimal Security During Phase 1 & 2

If you want to defer full security implementation, here's the minimal setup:

### Minimal Security Checklist
```java
// 1. Simple tenant context (can be header-based initially)
@Component
public class DevTenantInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        // For dev only - will be replaced
        String tenantId = request.getHeader("X-Dev-Tenant-ID");
        if (tenantId == null) {
            tenantId = "default-tenant"; // For non-tenant-aware testing
        }
        TenantContext.setCurrentTenant(tenantId);
        return true;
    }
}

// 2. Basic Spring Security (no Keycloak yet)
@Configuration
@EnableWebSecurity
public class DevSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // For development only
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(); // Simple auth for development
        return http.build();
    }
}

// 3. Mock user for development
@Component
public class DevAuthenticationProvider {
    public static final String DEV_USER_ID = "dev-user-123";
    public static final String DEV_TENANT_ID = "dev-tenant-001";
}
```

### Migration Path from Minimal to Full Security

1. **During Development (Phase 1 & 2)**
   - Use header-based tenant ID (development only)
   - Basic authentication
   - Hard-coded test users
   - Disabled CSRF for easier testing

2. **Before Production (Phase 3)**
   - Replace with Keycloak JWT
   - Implement proper tenant validation
   - Add audit trails
   - Enable all security features
   - Security testing

## Critical Decision Points

### After Phase 1 Completion
- [ ] Evaluate: Continue with minimal security or implement Keycloak?
- [ ] If minimal: Document all security debt
- [ ] Plan security sprint before any production deployment

### After Phase 2 Completion  
- [ ] Security implementation is MANDATORY before production
- [ ] No customer data without proper security
- [ ] Complete security audit required

## Development Tips for Minimal Security Approach

1. **Use Interfaces**
   ```java
   public interface TenantResolver {
       String resolveTenant();
   }
   
   @Profile("dev")
   @Component
   public class HeaderTenantResolver implements TenantResolver {
       // Header-based for development
   }
   
   @Profile("prod")
   @Component  
   public class JwtTenantResolver implements TenantResolver {
       // JWT-based for production
   }
   ```

2. **Security Annotations (prepare for future)**
   ```java
   @PreAuthorize("@tenantSecurity.hasAccessToPatient(#patientId)")
   public PatientDto getPatient(UUID patientId) {
       // Ready for security, but not enforced in dev
   }
   ```

3. **Audit Preparation**
   ```java
   @Auditable(action = "PATIENT_UPDATE")
   public void updatePatient(PatientUpdateRequest request) {
       // Annotation ready, implementation comes later
   }
   ```

## Recommended Timeline

```
Week 1-2:   Phase 0 - Setup & Minimal Security
Week 3-8:   Phase 1 - Multi-tenancy
Week 9-16:  Phase 2 - Modularization  
Week 17-19: Phase 3 - Full Security (MANDATORY before production)
Week 20:    Production readiness review
```

## Conclusion

While it's technically possible to implement security after Phase 1 & 2, it's recommended to:

1. **Implement minimal security from the start** (Phase 0)
2. **Use interfaces and abstractions** for easy security upgrade
3. **Plan for security refactoring time** after Phase 2
4. **NEVER go to production** without full security implementation

The extra 2-3 weeks for proper security implementation is worth avoiding:
- Security vulnerabilities
- Major refactoring costs
- Compliance issues
- Customer trust problems