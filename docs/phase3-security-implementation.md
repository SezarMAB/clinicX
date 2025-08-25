# Phase 3: Security & Authorization Implementation

## Overview

Phase 3 implements comprehensive security and authorization for the realm-per-type multi-tenant architecture. This includes request-level validation, method-level security, audit logging, and centralized security services.

## Components Implemented

### 1. Request-Level Security

#### TenantAuthorizationFilter (`/tenant/security/TenantAuthorizationFilter.java`)
- Validates tenant access for each HTTP request
- Runs after Spring Security authentication
- Excludes public endpoints and super admin operations
- Logs access attempts for audit trail

### 2. Spring Security Integration

#### TenantAccessDecisionVoter (`/tenant/security/TenantAccessDecisionVoter.java`)
- Integrates with Spring Security's access decision process
- Votes on access based on tenant membership
- Supports web requests and method invocations
- Handles super admin bypass logic

#### Updated SecurityConfig (`/core/security/SecurityConfig.java`)
- Added `@EnableAspectJAutoProxy` for AOP support
- Integrated `TenantAuthorizationFilter` into filter chain
- Configured `AccessDecisionManager` with tenant voter
- Conditional filter application based on multi-tenant mode

### 3. Method-Level Security

#### @RequiresTenant Annotation (`/tenant/security/RequiresTenant.java`)
- Custom security annotation for methods and classes
- Supports role validation within tenants
- Configurable parameter validation
- Super admin bypass option

#### TenantSecurityAspect (`/tenant/security/TenantSecurityAspect.java`)
- AOP aspect that enforces @RequiresTenant constraints
- Validates tenant access before method execution
- Extracts tenant IDs from method parameters
- Integrates with audit logging

### 4. Centralized Security Services

#### TenantSecurityService (`/tenant/service/TenantSecurityService.java`)
- High-level security API for tenant operations
- Permission checking methods
- Role validation
- Resource access control
- Tenant listing for users

#### TenantSecurityServiceImpl (`/tenant/service/impl/TenantSecurityServiceImpl.java`)
- Implements permission-based access control
- Role hierarchy with predefined permissions
- Super admin support
- Resource-based authorization

### 5. Audit Logging

#### TenantAuditService (`/tenant/service/TenantAuditService.java`)
- Interface for audit operations
- Access logging methods
- Tenant management auditing
- User-tenant relationship auditing

#### TenantAuditServiceImpl (`/tenant/service/impl/TenantAuditServiceImpl.java`)
- Logs all security events
- Publishes Spring events for external consumption
- Structured logging with timestamps

#### Event Classes
- `TenantAccessEvent` - Access granted/denied events
- `TenantSwitchEvent` - Tenant switching events
- `TenantManagementEvent` - Tenant CRUD events
- `UserTenantEvent` - User-tenant relationship events

### 6. Example Implementation

#### SecuredTenantExampleController (`/tenant/controller/SecuredTenantExampleController.java`)
Demonstrates various security patterns:
- Basic tenant validation
- Role-based access with @RequiresTenant
- Parameter-based tenant validation
- Spring Security expressions
- Combined security checks
- Programmatic authorization
- Class-level security

## Security Flow

1. **Request arrives** → TenantInterceptor sets TenantContext
2. **Authentication** → Spring Security validates JWT
3. **Filter validation** → TenantAuthorizationFilter checks tenant access
4. **Method security** → @RequiresTenant annotation validated
5. **Access decision** → TenantAccessDecisionVoter participates in voting
6. **Audit logging** → All access attempts logged via TenantAuditService

## Usage Examples

### Basic Tenant Security
```java
@GetMapping("/api/data")
public ResponseEntity<?> getData() {
    // TenantAuthorizationFilter ensures user has access
    String tenantId = TenantContext.getCurrentTenant();
    // ... fetch tenant-specific data
}
```

### Role-Based Tenant Security
```java
@RequiresTenant(role = "ADMIN")
@PostMapping("/api/admin/users")
public ResponseEntity<?> createUser(@RequestBody UserDto user) {
    // Only tenant admins can access this
}
```

### Dynamic Permission Checking
```java
@Service
public class PatientService {
    @Autowired
    private TenantSecurityService securityService;
    
    public Patient updatePatient(String patientId, PatientDto dto) {
        securityService.enforceTenantAccess();
        
        if (!securityService.canPerformAction("UPDATE_PATIENTS")) {
            throw new AccessDeniedException("Cannot update patients");
        }
        // ... update logic
    }
}
```

### Audit Event Handling
```java
@Component
public class TenantAuditListener {
    
    @EventListener
    public void handleAccessDenied(TenantAccessEvent event) {
        if (event.getAccessType() == AccessType.DENIED) {
            // Send alert, store in database, etc.
        }
    }
}
```

## Security Configuration

### Multi-Tenant Mode
When `app.multi-tenant.enabled=true`:
- TenantAuthorizationFilter is active
- TenantAccessDecisionVoter participates in decisions
- Audit logging is enabled

### Single-Tenant Mode
When `app.multi-tenant.enabled=false`:
- Tenant validation is bypassed
- Standard Spring Security applies
- Default tenant is used

## Next Steps

- Phase 4: Frontend Updates for tenant switching UI
- Phase 5: Migration & Testing
- Phase 6: Documentation & Deployment