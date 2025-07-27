# SaaS Architecture Recommendation for ClinicX

## Business Requirements
- **Multi-tenant SaaS** platform
- **Dental clinics**: Need full features (teeth charts, treatments, procedures)
- **General clinics**: Only need appointment scheduling
- **Scalability**: Support hundreds of clinics
- **Customization**: Each clinic type has different needs

## Recommended Architecture: Modular Monolith with Feature Flags

### Overview
```
┌─────────────────────────────────────────────────────────┐
│                   ClinicX SaaS Platform                  │
├─────────────────────────────────────────────────────────┤
│  Core Modules            │  Feature Modules             │
├──────────────────────────┼──────────────────────────────┤
│ • Authentication         │ • Dental Module (optional)   │
│ • Appointments           │ • Lab Requests (optional)    │
│ • Patients               │ • Dental Charts (optional)   │
│ • Staff                  │ • Treatments (optional)      │
│ • Invoicing              │ • Procedures (optional)      │
│ • Payments               │                              │
└──────────────────────────┴──────────────────────────────┘
                    │
                    ▼
        ┌──────────────────────┐
        │   PostgreSQL          │
        │ (Schema per tenant)   │
        └──────────────────────┘
```

### Why This Approach?

**✅ Advantages:**
1. **Single codebase** - Easier maintenance
2. **Feature flags** - Enable/disable features per tenant
3. **Shared infrastructure** - Cost-effective
4. **Gradual migration** - Can extract services later
5. **Consistent experience** - Same UI/UX patterns

**❌ What to Avoid:**
- Premature microservices add complexity
- Separate applications increase maintenance
- Multiple databases complicate backups

## Implementation Strategy

### 1. Multi-Tenancy Design

**Database Strategy: Schema-per-tenant**
```sql
-- Core schema (shared)
public.tenants
public.subscriptions
public.feature_flags

-- Tenant schemas
tenant_001.patients
tenant_001.appointments
tenant_001.dental_charts  -- only for dental clinics

tenant_002.patients
tenant_002.appointments
-- no dental tables for general clinic
```

**Benefits:**
- Data isolation
- Easy backups per clinic
- Performance isolation
- GDPR compliance

### 2. Feature Module System

```java
@Component
@ConditionalOnFeature("dental.module.enabled")
public class DentalModuleConfiguration {
    // Dental-specific beans
}

@Entity
@Table(name = "treatments")
@ConditionalOnFeature("dental.module.enabled")
public class Treatment {
    // Only loaded for dental clinics
}
```

### 3. Tenant Configuration

```yaml
# tenant-config.yml
tenants:
  smile-dental:
    type: DENTAL_CLINIC
    features:
      dental.module.enabled: true
      lab.requests.enabled: true
      treatments.enabled: true
      dental.charts.enabled: true
    schema: tenant_001
    
  family-medical:
    type: GENERAL_CLINIC
    features:
      dental.module.enabled: false
      lab.requests.enabled: true
      treatments.enabled: false
    schema: tenant_002
```

### 4. API Design

**Base Endpoints (All Clinics):**
```
GET  /api/v1/patients
POST /api/v1/appointments
GET  /api/v1/invoices
POST /api/v1/payments
```

**Dental-Specific Endpoints:**
```
GET  /api/v1/dental/charts/{patientId}  -- 404 for non-dental
POST /api/v1/dental/treatments
GET  /api/v1/dental/procedures
```

### 5. UI/Frontend Strategy

```typescript
// Feature flag service
interface ClinicFeatures {
  hasDentalModule: boolean;
  hasLabRequests: boolean;
  hasTreatments: boolean;
}

// Conditional rendering
{clinic.features.hasDentalModule && (
  <DentalChartComponent />
)}
```

## Migration Path

### Phase 1: Current State Enhancement (4-6 weeks)
1. Add multi-tenancy to existing codebase
2. Implement schema-per-tenant
3. Add feature flag system
4. Create tenant management admin

### Phase 2: Feature Modularization (6-8 weeks)
1. Extract dental-specific code into modules
2. Create clean interfaces between modules
3. Add conditional loading based on features
4. Test with pilot customers

### Phase 3: Production Rollout (2-4 weeks)
1. Onboard dental clinics with full features
2. Onboard general clinics with base features
3. Monitor performance and costs
4. Gather feedback for improvements

### Phase 4: Future Scaling (Optional)
If you reach 100+ clinics or need independent scaling:
1. Extract appointment service
2. Keep dental features in main app
3. Use event-driven communication
4. Consider separate databases

## Technology Stack Recommendations

### Backend
```groovy
dependencies {
    // Multi-tenancy
    implementation 'org.hibernate:hibernate-core' // For multi-tenant support
    implementation 'com.github.gavlyukovskiy:datasource-proxy-spring-boot-starter' // For tenant routing
    
    // Feature flags
    implementation 'org.togglz:togglz-spring-boot-starter'
    
    // API versioning
    implementation 'io.github.mweirauch:micrometer-registry-cloudwatch'
}
```

### Security with Keycloak

**⚠️ CRITICAL: Tenant Isolation Security**

The tenant identification MUST come from the JWT token issued by Keycloak, NOT from request headers that clients can manipulate.

#### Secure Tenant Resolution
```java
// ❌ INSECURE - Never do this!
String tenantId = request.getHeader("X-Tenant-ID"); // Client can fake this!

// ✅ SECURE - Extract from JWT
@Component
public class SecureTenantInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            // Tenant ID embedded in JWT by Keycloak (cannot be forged)
            String tenantId = jwt.getClaimAsString("tenant_id");
            
            // Validate user belongs to this tenant
            validateTenantAccess(jwt.getSubject(), tenantId);
            
            TenantContext.setCurrentTenant(tenantId);
        }
        return true;
    }
}
```

#### Keycloak Configuration
```yaml
# Option 1: Realm per tenant (Recommended)
keycloak:
  realms:
    - name: clinicx-smile-dental
      tenant-id: tenant_001
      client-id: clinicx-app
      features: [dental-api, full-access]
    
    - name: clinicx-family-medical  
      tenant-id: tenant_002
      client-id: clinicx-app
      features: [basic-api, appointments-only]

# Option 2: Single realm with tenant claims
keycloak:
  realm: clinicx
  client-mappers:
    - name: tenant-mapper
      protocol: openid-connect
      protocol-mapper: oidc-usermodel-attribute-mapper
      user-attribute: tenant_id
      claim-name: tenant_id
      add-to-access-token: true
```

#### Additional Security Layers
```java
// 1. Entity-level tenant validation
@Entity
@Where(clause = "tenant_id = current_setting('app.current_tenant')::uuid")
public class Patient {
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
}

// 2. Repository-level validation
@Query("SELECT p FROM Patient p WHERE p.id = :id AND p.tenantId = :#{T(TenantContext).getCurrentTenant()}")
Optional<Patient> findById(@Param("id") UUID id);

// 3. Service-level double-check
if (!entity.getTenantId().equals(TenantContext.getCurrentTenant())) {
    throw new AccessDeniedException("Cross-tenant access attempt detected");
}
```

### Monitoring
- Tenant-aware metrics
- Feature usage analytics
- Performance per schema
- Cost allocation per tenant

## Security Best Practices for Multi-Tenancy

### 1. **Never Trust Client Headers**
- Tenant ID must come from authenticated JWT tokens
- Validate tenant access at multiple layers
- Log all cross-tenant access attempts

### 2. **Database Isolation**
- Use schema-per-tenant for complete isolation
- Add tenant_id column to all tables as backup
- Use Row Level Security (RLS) in PostgreSQL

### 3. **API Security**
- Validate tenant context in all API endpoints
- Use method-level security annotations
- Implement rate limiting per tenant

### 4. **Audit Trail**
```java
@EventListener
public void handleSecurityEvent(AbstractAuthenticationEvent event) {
    if (event instanceof AbstractAuthenticationFailureEvent) {
        // Log failed cross-tenant access attempts
        auditService.logSecurityEvent(event);
    }
}
```

## Cost Optimization

### Shared Resources
- Single Kubernetes cluster
- Shared PostgreSQL instance
- Common Redis cache
- Unified monitoring

### Per-Tenant Costs
- Storage (schema size)
- API usage
- Feature consumption
- Support level

## Success Metrics

1. **Time to Onboard**: < 1 hour per clinic
2. **Feature Toggle Time**: < 5 minutes
3. **Performance**: < 200ms API response
4. **Uptime**: 99.9% SLA
5. **Cost per Clinic**: Predictable and scalable

## Common Pitfalls to Avoid

### ❌ Security Anti-Patterns
1. **Client-Controlled Tenant Selection**
   - Never use headers like `X-Tenant-ID` that clients can modify
   - Always derive tenant from authenticated JWT tokens
   
2. **Missing Tenant Validation**
   - Don't assume the framework handles tenant isolation
   - Explicitly validate tenant access at every layer

3. **Shared Sequences/IDs**
   - Use UUIDs instead of sequential IDs
   - Prevents tenant ID enumeration attacks

### ❌ Architecture Anti-Patterns
1. **Premature Microservices**
   - Start with modular monolith
   - Extract services only when proven necessary

2. **Over-Engineering Features**
   - Use simple feature flags first
   - Add complexity only when needed

## Conclusion

The **modular monolith with feature flags** approach provides:
- Quick time to market
- Flexibility for different clinic types
- Cost-effective scaling
- Future-proof architecture
- **Secure multi-tenant isolation**

Start simple, validate with customers, then evolve based on real needs. This approach lets you serve both dental and general clinics efficiently while maintaining a single, manageable codebase with proper security boundaries.
