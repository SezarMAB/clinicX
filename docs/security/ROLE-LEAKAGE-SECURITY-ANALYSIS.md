# Multi-Tenant Role Leakage Security Analysis & Solution

## Executive Summary

This document addresses a critical security issue in the current ClinicX multi-tenant architecture where **roles from one tenant can leak into another tenant's context** when using Keycloak's realm-level roles. When a user has ADMIN rights in Tenant A and is granted DOCTOR access to Tenant B, they may inadvertently retain ADMIN privileges in Tenant B due to how Keycloak JWT tokens include all realm-level roles.

## Problem Statement

### Current Scenario
```
User: Dr. John Smith
├── Tenant A (Primary Clinic)
│   ├── Roles: [ADMIN, DOCTOR]
│   └── Access Level: Full administrative control
│
└── Tenant B (Partner Clinic - External Access)
    ├── Intended Roles: [DOCTOR]
    └── Actual Problem: User still has ADMIN rights from Tenant A
```

### The Security Vulnerability

When Dr. Smith authenticates via Keycloak:
1. JWT token includes ALL realm-level roles: `[ADMIN, DOCTOR]`
2. When accessing Tenant B, the system sees ADMIN role in the token
3. User gets unintended administrative access to Tenant B
4. **Critical Risk**: Can modify Tenant B's settings, view financial data, manage users

## Root Cause Analysis

### 1. Keycloak Realm-Level Roles
```json
// Current JWT Token Structure
{
  "sub": "user-123",
  "realm_access": {
    "roles": ["ADMIN", "DOCTOR"]  // All roles from the realm
  },
  "resource_access": {
    "clinicx-backend": {
      "roles": ["ADMIN", "DOCTOR"]  // Same roles everywhere
    }
  },
  "tenant_id": "tenant-b",
  "accessible_tenants": "[...]",
  "user_tenant_roles": "{
    \"tenant-a\": [\"ADMIN\", \"DOCTOR\"],
    \"tenant-b\": [\"DOCTOR\"]  // Intended roles stored here
  }"
}
```

### 2. Current Authorization Flow
```java
// Current problematic flow
@PreAuthorize("hasRole('ADMIN')")  // Checks JWT realm_access.roles
public void adminFunction() {
    // User with ADMIN in ANY tenant can access this in ALL tenants!
}
```

### 3. Why This Happens
- **Realm-level roles are global** within a Keycloak realm
- **JWT tokens are stateless** and contain all user's realm roles
- **Spring Security** checks roles from the JWT token's realm_access
- **Tenant-specific roles** in custom attributes are not used for authorization

## Solution Architecture

### Solution 1: Tenant-Aware Role Resolution (Recommended)

#### Overview
Replace Spring Security's default role checking with tenant-aware authorization that uses the `user_tenant_roles` attribute from the JWT token.

#### Implementation Components

##### 1. Custom Security Expression Handler
```java
@Component
public class TenantAwareSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        TenantAwareSecurityExpressionRoot root = 
            new TenantAwareSecurityExpressionRoot(authentication);
        root.setTenantContext(TenantContext.getCurrentTenant());
        root.setPermissionEvaluator(getPermissionEvaluator());
        return root;
    }
}
```

##### 2. Tenant-Aware Security Expression Root
```java
public class TenantAwareSecurityExpressionRoot extends SecurityExpressionRoot {
    
    private String currentTenant;
    private Map<String, List<String>> userTenantRoles;
    
    public boolean hasTenantRole(String role) {
        if (currentTenant == null) {
            return false;
        }
        
        // Extract user_tenant_roles from JWT
        Jwt jwt = (Jwt) getAuthentication().getPrincipal();
        String rolesJson = jwt.getClaimAsString("user_tenant_roles");
        
        if (rolesJson != null) {
            userTenantRoles = parseUserTenantRoles(rolesJson);
            List<String> tenantRoles = userTenantRoles.get(currentTenant);
            return tenantRoles != null && tenantRoles.contains(role);
        }
        
        return false;
    }
    
    public boolean hasTenantAnyRole(String... roles) {
        for (String role : roles) {
            if (hasTenantRole(role)) {
                return true;
            }
        }
        return false;
    }
}
```

##### 3. Updated Authorization Annotations
```java
// OLD - Vulnerable to role leakage
@PreAuthorize("hasRole('ADMIN')")
public void adminFunction() { }

// NEW - Tenant-aware authorization
@PreAuthorize("@tenantSecurity.hasTenantRole('ADMIN')")
public void adminFunction() { }

// Alternative annotation approach
@TenantAuthorize(roles = "ADMIN")
public void adminFunction() { }
```

##### 4. Custom Tenant Authorization Annotation
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@tenantAuthorizationEvaluator.authorize(#root, #roles)")
public @interface TenantAuthorize {
    String[] roles() default {};
    String[] anyRoles() default {};
}
```

### Solution 2: Client-Specific Roles in Keycloak

#### Overview
Use Keycloak's client-level roles instead of realm-level roles, with different clients per tenant.

#### Implementation
```
Keycloak Structure:
├── Realm: clinicx-master
│   ├── Client: tenant-a-backend
│   │   └── Roles: [ADMIN, DOCTOR]  // Only for Tenant A
│   │
│   ├── Client: tenant-b-backend
│   │   └── Roles: [DOCTOR]  // Only for Tenant B
│   │
│   └── User: Dr. Smith
│       ├── Client Roles (tenant-a-backend): [ADMIN, DOCTOR]
│       └── Client Roles (tenant-b-backend): [DOCTOR]
```

**Pros**: Clean separation in Keycloak
**Cons**: Requires multiple client configurations, complex token exchange

### Solution 3: Composite Role Naming Convention

#### Overview
Use tenant-prefixed roles to ensure uniqueness across tenants.

#### Implementation
```java
// Role structure
TENANT_A_ADMIN
TENANT_A_DOCTOR
TENANT_B_DOCTOR

// Authorization
@PreAuthorize("hasRole('TENANT_' + @tenantContext.current() + '_ADMIN')")
public void adminFunction() { }
```

**Pros**: Simple to implement
**Cons**: Role explosion, difficult to manage at scale

## Recommended Implementation Plan

### Phase 1: Immediate Security Fix (1-2 weeks)

#### Step 1: Create Tenant Authorization Service
```java
@Service
public class TenantAuthorizationService {
    
    @Autowired
    private UserTenantAccessService userTenantAccessService;
    
    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            return false;
        }
        
        Jwt jwt = (Jwt) auth.getPrincipal();
        String userId = jwt.getSubject();
        String tenantId = TenantContext.getCurrentTenant();
        
        if (tenantId == null) {
            // Fall back to JWT tenant_id claim
            tenantId = jwt.getClaimAsString("tenant_id");
        }
        
        try {
            UserTenantAccessDto access = userTenantAccessService.getAccess(userId, tenantId);
            return access.isActive() && role.equals(access.getRole());
        } catch (Exception e) {
            // Fall back to user_tenant_roles in JWT
            return checkJwtTenantRoles(jwt, tenantId, role);
        }
    }
    
    private boolean checkJwtTenantRoles(Jwt jwt, String tenantId, String role) {
        String rolesJson = jwt.getClaimAsString("user_tenant_roles");
        if (rolesJson != null) {
            try {
                Map<String, List<String>> tenantRoles = 
                    objectMapper.readValue(rolesJson, new TypeReference<>() {});
                List<String> roles = tenantRoles.get(tenantId);
                return roles != null && roles.contains(role);
            } catch (Exception e) {
                log.error("Failed to parse user_tenant_roles", e);
            }
        }
        return false;
    }
}
```

#### Step 2: Update All Authorization Points
```java
// Before
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController { }

// After
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    
    @Autowired
    private TenantAuthorizationService authService;
    
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        if (!authService.hasRole("ADMIN")) {
            throw new AccessDeniedException("Admin access required for current tenant");
        }
        // ... rest of the logic
    }
}
```

### Phase 2: Systematic Refactoring (2-4 weeks)

#### Step 1: Create Custom Security Annotations
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireTenantRole {
    String[] value();
    boolean requireAll() default false;
}

@Aspect
@Component
public class TenantRoleAspect {
    
    @Autowired
    private TenantAuthorizationService authService;
    
    @Around("@annotation(requireTenantRole)")
    public Object checkTenantRole(ProceedingJoinPoint joinPoint, 
                                  RequireTenantRole requireTenantRole) throws Throwable {
        String[] roles = requireTenantRole.value();
        boolean requireAll = requireTenantRole.requireAll();
        
        boolean authorized = requireAll 
            ? Arrays.stream(roles).allMatch(authService::hasRole)
            : Arrays.stream(roles).anyMatch(authService::hasRole);
        
        if (!authorized) {
            throw new AccessDeniedException(
                "Insufficient privileges for tenant: " + TenantContext.getCurrentTenant()
            );
        }
        
        return joinPoint.proceed();
    }
}
```

#### Step 2: Apply Annotations Systematically
```java
@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {
    
    @PostMapping
    @RequireTenantRole("ADMIN")
    public ResponseEntity<StaffDto> createStaff(@RequestBody StaffCreateRequest request) {
        // Only users with ADMIN role in current tenant can create staff
    }
    
    @GetMapping
    @RequireTenantRole(value = {"ADMIN", "DOCTOR", "RECEPTIONIST"}, requireAll = false)
    public ResponseEntity<Page<StaffDto>> listStaff(Pageable pageable) {
        // Users with any of these roles in current tenant can list staff
    }
}
```

### Phase 3: Enhanced JWT Token Structure (4-6 weeks)

#### Step 1: Modify Keycloak Token Mapper
Create a custom protocol mapper that only includes tenant-specific roles:

```java
public class TenantAwareRoleMapper extends AbstractOIDCProtocolMapper {
    
    @Override
    public void transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel,
                                    KeycloakSession session, UserSessionModel userSession,
                                    ClientSessionContext clientSessionCtx) {
        
        // Get current tenant from user attributes or session
        String currentTenant = userSession.getNote("current_tenant");
        if (currentTenant == null) {
            currentTenant = getUserAttribute(userSession, "active_tenant_id");
        }
        
        // Only include roles for current tenant
        String userTenantRoles = getUserAttribute(userSession, "user_tenant_roles");
        if (userTenantRoles != null && currentTenant != null) {
            Map<String, List<String>> rolesMap = parseJson(userTenantRoles);
            List<String> tenantRoles = rolesMap.get(currentTenant);
            
            // Clear realm roles and add only tenant-specific roles
            token.getRealmAccess().getRoles().clear();
            if (tenantRoles != null) {
                tenantRoles.forEach(role -> token.getRealmAccess().addRole(role));
            }
        }
    }
}
```

#### Step 2: Token Refresh on Tenant Switch
```java
@Service
public class TenantSwitchingService {
    
    @Autowired
    private KeycloakAdminService keycloakAdminService;
    
    public TenantSwitchResponseDto switchTenant(String tenantId) {
        // ... validation logic ...
        
        // Force token refresh with new tenant context
        String newAccessToken = keycloakAdminService.refreshTokenWithTenantContext(
            currentRefreshToken,
            tenantId
        );
        
        return TenantSwitchResponseDto.builder()
            .accessToken(newAccessToken)
            .tenantId(tenantId)
            .message("Switched to tenant with correct role context")
            .build();
    }
}
```

## Testing Strategy

### 1. Unit Tests for Role Resolution
```java
@Test
public void testRoleIsolationBetweenTenants() {
    // Setup: User with ADMIN in Tenant A, DOCTOR in Tenant B
    String userId = "user-123";
    
    // Create access records
    userTenantAccessService.grantAccess(CreateUserTenantAccessRequest.builder()
        .userId(userId)
        .tenantId("tenant-a")
        .role("ADMIN")
        .build());
    
    userTenantAccessService.grantAccess(CreateUserTenantAccessRequest.builder()
        .userId(userId)
        .tenantId("tenant-b")
        .role("DOCTOR")
        .build());
    
    // Test Tenant A context
    TenantContext.setCurrentTenant("tenant-a");
    assertTrue(authService.hasRole("ADMIN"));
    assertTrue(authService.hasRole("DOCTOR")); // Might have both
    
    // Test Tenant B context
    TenantContext.setCurrentTenant("tenant-b");
    assertFalse(authService.hasRole("ADMIN")); // Must NOT have ADMIN
    assertTrue(authService.hasRole("DOCTOR"));
}
```

### 2. Integration Tests
```java
@Test
@WithMockUser(roles = {"ADMIN", "DOCTOR"})
public void testAdminEndpointAccessControl() {
    // Set tenant context to where user is NOT admin
    TenantContext.setCurrentTenant("tenant-b");
    
    // Attempt to access admin endpoint
    mockMvc.perform(post("/api/v1/admin/settings")
            .header("X-Tenant-ID", "tenant-b")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Admin access required for current tenant"));
}
```

## Security Best Practices

### 1. Defense in Depth
- **Layer 1**: JWT token validation
- **Layer 2**: Tenant context validation
- **Layer 3**: UserTenantAccess table check
- **Layer 4**: Method-level authorization
- **Layer 5**: Audit logging

### 2. Audit Logging
```java
@EventListener
public void handleUnauthorizedAccess(AuthorizationFailureEvent event) {
    log.warn("Unauthorized access attempt: User {} tried to access {} in tenant {} with roles {}",
        event.getUserId(),
        event.getResource(),
        event.getTenantId(),
        event.getAttemptedRoles());
    
    // Alert security team if ADMIN access attempted in wrong tenant
    if (event.getAttemptedRoles().contains("ADMIN")) {
        securityAlertService.sendAlert("Potential role leakage detected", event);
    }
}
```

### 3. Regular Security Audits
```sql
-- Query to detect potential role mismatches
SELECT 
    uta.user_id,
    uta.tenant_id,
    uta.role as intended_role,
    s.role as staff_role
FROM user_tenant_access uta
JOIN staff s ON s.keycloak_user_id = uta.user_id 
    AND s.tenant_id = uta.tenant_id
WHERE uta.role != s.role::text;
```

## Migration Checklist

- [ ] Implement TenantAuthorizationService
- [ ] Create UserTenantAccess integration
- [ ] Update all @PreAuthorize annotations
- [ ] Add tenant-aware security tests
- [ ] Update Keycloak token mappers
- [ ] Implement audit logging
- [ ] Document new authorization patterns
- [ ] Train development team on new patterns
- [ ] Security review by external auditor
- [ ] Performance testing with multiple tenants
- [ ] Rollback plan prepared

## Conclusion

The role leakage vulnerability is a **critical security issue** that must be addressed immediately. The recommended solution (Tenant-Aware Role Resolution) provides:

1. **Immediate security improvement** without Keycloak changes
2. **Backward compatibility** with existing code
3. **Clear migration path** to enhanced security
4. **Audit trail** for compliance requirements
5. **Performance optimization** through caching

This solution ensures that **users only have the roles they were explicitly granted in each tenant**, preventing unauthorized access and maintaining proper data isolation in the multi-tenant environment.