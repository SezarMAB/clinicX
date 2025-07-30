# Keycloak Admin Service Methods Integration Guide

## Overview

This document explains how the Keycloak admin service methods are integrated into the ClinicX application for multi-tenant management.

## Method Usage and Integration Points

### 1. `copyClientsFromRealm(sourceRealm, targetRealm)`

**Purpose**: Copies client configurations from a template realm when creating new specialty realms.

**Where it's used**:
- `DynamicRealmServiceImpl.configureRealmForSpecialty()` - When creating new shared realms for specialties

**Configuration**:
```yaml
app:
  tenant:
    specialty-realm-mapping:
      template-realm: master  # Source realm to copy clients from
```

**Example usage**:
```java
// Automatically called when creating a new specialty realm
keycloakAdminService.copyClientsFromRealm(templateRealm, newRealmName);
```

### 2. `ensureProtocolMapper(realm, clientId, mapperName, attributeName)`

**Purpose**: Ensures protocol mappers exist for JWT token claims in multi-tenant setup.

**Where it's used**:
- `DynamicRealmServiceImpl.ensureProtocolMappers()` - Configures mappers for each client
- Called during realm creation and client setup

**Mappers configured**:
- tenant_id → tenant_id claim
- clinic_name → clinic_name claim  
- clinic_type → clinic_type claim
- active_tenant_id → active_tenant_id claim
- accessible_tenants → accessible_tenants claim (JSON)
- user_tenant_roles → user_tenant_roles claim (JSON)
- clinic_type → specialty claim

### 3. `grantAdditionalTenantAccess(realm, username, tenantId, clinicName, clinicType, roles)`

**Purpose**: Grants a user access to an additional tenant.

**Where it's used**:
- `TenantSwitchingServiceImpl.grantUserTenantAccess()` - When granting new tenant access
- `UserTenantAccessController` - REST endpoint for super admins

**REST endpoint**:
```
POST /api/v1/users/{userId}/tenant-access
{
  "tenantId": "happy-teeth-456",
  "role": "DOCTOR",
  "isPrimary": false
}
```

**What it does**:
- Adds tenant to user's `accessible_tenants` JSON array
- Updates `user_tenant_roles` with roles for the new tenant
- Maintains existing tenant access

### 4. `revokeTenantAccess(realm, username, tenantId)`

**Purpose**: Removes a user's access to a specific tenant.

**Where it's used**:
- `TenantSwitchingServiceImpl.revokeUserTenantAccess()` - When revoking tenant access
- `UserTenantAccessController` - REST endpoint for super admins

**REST endpoint**:
```
DELETE /api/v1/users/{userId}/tenant-access/{tenantId}
```

**What it does**:
- Removes tenant from `accessible_tenants` JSON array
- Removes tenant from `user_tenant_roles`
- Updates `active_tenant_id` if necessary

### 5. `updateUserActiveTenant(realm, username, newActiveTenantId)`

**Purpose**: Switches the user's active tenant for the current session.

**Where it's used**:
- `TenantSwitchingServiceImpl.switchTenant()` - When user switches between tenants
- `TenantSwitchController` - REST endpoint for tenant switching

**REST endpoint**:
```
POST /api/auth/switch-tenant
{
  "tenantId": "smile-dental-123"
}
```

**What it does**:
- Validates user has access to the new tenant
- Updates `active_tenant_id` attribute
- Returns new JWT token with updated claims

## Integration Flow Examples

### Creating a New Tenant with Realm-per-Type

```java
// 1. Create tenant in database
Tenant tenant = tenantService.createTenant(request);

// 2. If using realm-per-type, configure shared realm
if (realmPerTypeEnabled) {
    String realmName = dynamicRealmService.resolveRealmForTenant(request);
    
    // This calls copyClientsFromRealm and ensureProtocolMapper
    dynamicRealmService.configureRealmForSpecialty(realmName, specialty);
}

// 3. Create admin user with tenant attributes
keycloakAdminService.createUserWithTenantInfo(
    realmName, username, email, firstName, lastName, 
    password, roles, tenantId, clinicName, clinicType
);
```

### Granting Multi-Tenant Access

```java
// 1. Grant access in local database
userTenantAccessRepository.save(new UserTenantAccess(userId, tenantId, role));

// 2. Update Keycloak attributes
keycloakAdminService.grantAdditionalTenantAccess(
    realmName, username, tenantId, 
    clinicName, clinicType, Arrays.asList(role)
);
```

### Switching Active Tenant

```java
// 1. Validate access
UserTenantAccess access = validateUserHasAccess(userId, tenantId);

// 2. Update Keycloak
keycloakAdminService.updateUserActiveTenant(realmName, username, tenantId);

// 3. Generate new token (handled by token service)
String newToken = tokenService.refreshTokenWithNewTenant(tenantId);
```

## Configuration Requirements

### Application Properties

```yaml
app:
  multi-tenant:
    enabled: true
    realm-per-type: true  # Enable shared realms by specialty
  tenant:
    specialty-realm-mapping:
      enabled: true
      auto-create-realm: true
      template-realm: master  # Realm to copy clients from
```

### Keycloak Requirements

1. **User Profile Configuration**: All custom attributes must be defined
2. **Protocol Mappers**: Automatically configured by the service
3. **Client Templates**: Backend and frontend clients in template realm
4. **Roles**: Standard roles created automatically

## Error Handling

All methods include proper error handling:

- **Realm doesn't exist**: Automatically created if `auto-create-realm` is true
- **Client already exists**: Logged as warning, continues
- **User not found**: Throws `BusinessRuleException`
- **Invalid tenant access**: Validates before updating

## Security Considerations

1. **Super Admin Only**: Tenant access management requires SUPER_ADMIN role
2. **User Can View Own Access**: Users can see their own tenant access
3. **Audit Logging**: All tenant access changes are logged
4. **Token Validation**: Active tenant validated on each request

## Testing the Integration

### 1. Create a New Specialty Realm
```bash
# Create a tenant with new specialty
POST /api/v1/tenants
{
  "name": "Orthodontic Specialists",
  "subdomain": "ortho-spec",
  "specialty": "ORTHODONTICS"
}
# This will trigger realm creation and client copying
```

### 2. Grant Multi-Tenant Access
```bash
# Grant existing user access to new tenant
POST /api/v1/users/john.doe/tenant-access
{
  "tenantId": "ortho-spec-789",
  "role": "DOCTOR",
  "isPrimary": false
}
```

### 3. Switch Tenant
```bash
# Switch active tenant
POST /api/auth/switch-tenant
{
  "tenantId": "ortho-spec-789"
}
# Returns new JWT with updated active_tenant_id
```

## Troubleshooting

### Method Not Being Called

1. Check if feature flags are enabled:
   - `app.multi-tenant.realm-per-type`
   - `app.tenant.specialty-realm-mapping.enabled`

2. Verify authentication context has realm information

3. Check logs for error messages

### Clients Not Copied

1. Ensure template realm has required clients
2. Check `template-realm` configuration
3. Verify Keycloak admin credentials

### Protocol Mappers Not Working

1. Ensure user profile is configured first
2. Check client exists before adding mappers
3. Verify mapper configuration matches attribute names