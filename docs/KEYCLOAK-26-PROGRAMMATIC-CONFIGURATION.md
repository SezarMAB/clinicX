# Keycloak 26 Programmatic Multi-Tenant Configuration

## Overview

This document describes how ClinicX programmatically configures Keycloak 26 for multi-tenant support, including user profile attributes and protocol mappers.

## Key Changes in Keycloak 26

1. **User Profile Configuration**: User attributes must be defined in the User Profile (Realm settings → User profile) before they can be used
2. **Attributes Location**: The Attributes tab has moved under Realm settings → User profile
3. **Programmatic Configuration**: User profiles can be configured via REST API

## Programmatic Configuration Flow

### 1. Realm Creation

When a new realm is created via `KeycloakAdminService.createRealm()`:

```java
// 1. Create the realm with basic settings
RealmRepresentation realm = new RealmRepresentation();
realm.setRealm(realmName);
realm.setDisplayName(displayName);
realm.setEnabled(true);
keycloak.realms().create(realm);

// 2. Configure user profile with multi-tenant attributes
configureUserProfile(realmName);

// 3. Create default roles
createDefaultRoles(realmName);

// 4. Create default clients from templates
createDefaultClients(realmName, subdomain);
```

### 2. User Profile Configuration

The `configureUserProfile()` method programmatically sets up all required user attributes:

#### Required Attributes for Multi-Tenant Support:

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `tenant_id` | String | Yes | Primary tenant ID |
| `clinic_name` | String | Yes | Primary clinic name |
| `clinic_type` | String | Yes | Primary clinic type/specialty |
| `active_tenant_id` | String | Yes | Currently active tenant |
| `accessible_tenants` | String (JSON) | No | JSON array of accessible tenants |
| `user_tenant_roles` | String (JSON) | No | JSON object mapping tenant IDs to roles |

#### Configuration Process:

```java
// 1. Enable user profile feature
Map<String, String> attributes = new HashMap<>();
attributes.put("userProfileEnabled", "true");
realmRep.setAttributes(attributes);
realmResource.update(realmRep);

// 2. Configure user profile via REST API
String userProfileConfig = """
{
  "attributes": [
    {
      "name": "tenant_id",
      "displayName": "Tenant ID",
      "validations": {
        "length": { "min": 1, "max": 255 }
      },
      "required": {
        "roles": ["admin", "user"]
      },
      "permissions": {
        "view": ["admin", "user"],
        "edit": ["admin"]
      }
    },
    // ... other attributes
  ]
}
""";

// 3. Update via REST API
updateUserProfileViaRest(realmName, userProfileConfig, adminToken);
```

### 3. Protocol Mapper Configuration

The `configureProtocolMappers()` method creates all required mappers for JWT tokens:

#### Required Protocol Mappers:

1. **tenant_id** - Maps to tenant_id claim (String)
2. **active_tenant_id** - Maps to active_tenant_id claim (String)
3. **accessible_tenants** - Maps to accessible_tenants claim (JSON)
4. **specialty** - Maps clinic_type attribute to specialty claim (String)
5. **user_tenant_roles** - Maps to user_tenant_roles claim (JSON)
6. **clinic_name** - Maps to clinic_name claim (String)
7. **clinic_type** - Maps to clinic_type claim (String)

#### Mapper Configuration:

```java
ProtocolMapperRepresentation mapper = createUserAttributeMapper(
    "tenant_id",           // mapper name
    "tenant_id",           // user attribute
    "tenant_id",           // claim name
    "String",              // JSON type
    true,                  // add to ID token
    true,                  // add to access token
    true                   // add to userinfo
);
```

### 4. User Creation with Multi-Tenant Attributes

When creating users via `createUserWithTenantInfo()`:

```java
// Set multi-tenant attributes
Map<String, List<String>> attributes = new HashMap<>();
attributes.put("tenant_id", Arrays.asList(tenantId));
attributes.put("clinic_name", Arrays.asList(clinicName));
attributes.put("clinic_type", Arrays.asList(clinicType));
attributes.put("active_tenant_id", Arrays.asList(tenantId));

// Initialize accessible_tenants with primary tenant
String accessibleTenants = String.format(
    "[{\"tenant_id\":\"%s\",\"clinic_name\":\"%s\",\"clinic_type\":\"%s\",\"specialty\":\"%s\",\"roles\":%s}]",
    tenantId, clinicName, clinicType, clinicType, 
    convertRolesToJson(roles)
);
attributes.put("accessible_tenants", Arrays.asList(accessibleTenants));

// Initialize user_tenant_roles
String userTenantRoles = String.format("{\"%s\":%s}", tenantId, convertRolesToJson(roles));
attributes.put("user_tenant_roles", Arrays.asList(userTenantRoles));

user.setAttributes(attributes);
```

## Multi-Tenant Management Operations

### 1. Grant Additional Tenant Access

```java
keycloakAdminService.grantAdditionalTenantAccess(
    realmName,
    username,
    newTenantId,
    newClinicName,
    newClinicType,
    Arrays.asList("DOCTOR", "USER")
);
```

This method:
- Adds the new tenant to `accessible_tenants` JSON array
- Updates `user_tenant_roles` with roles for the new tenant
- Preserves existing tenant access

### 2. Revoke Tenant Access

```java
keycloakAdminService.revokeTenantAccess(realmName, username, tenantId);
```

This method:
- Removes the tenant from `accessible_tenants`
- Removes the tenant from `user_tenant_roles`
- Updates `active_tenant_id` if necessary

### 3. Switch Active Tenant

```java
keycloakAdminService.updateUserActiveTenant(realmName, username, newActiveTenantId);
```

This method:
- Validates user has access to the new tenant
- Updates `active_tenant_id` attribute
- Used by the tenant switching endpoint

## Client Configuration

### Backend Client (clinicx-backend)
- Type: Confidential
- Service Accounts: Enabled
- Direct Access Grants: Enabled
- Secret: Auto-generated UUID

### Frontend Client (clinicx-frontend)
- Type: Public
- Standard Flow: Enabled
- Direct Access Grants: Enabled
- Redirect URIs: Dynamically configured based on subdomain

Both clients automatically receive all protocol mappers for multi-tenant claims.

## Example JWT Token Structure

After successful configuration, JWT tokens will include:

```json
{
  "sub": "user-id",
  "email": "user@example.com",
  "tenant_id": "smile-dental-123",
  "clinic_name": "Smile Dental Clinic",
  "clinic_type": "DENTAL",
  "specialty": "DENTAL",
  "active_tenant_id": "smile-dental-123",
  "accessible_tenants": [
    {
      "tenant_id": "smile-dental-123",
      "clinic_name": "Smile Dental",
      "clinic_type": "DENTAL",
      "specialty": "DENTAL",
      "roles": ["ADMIN", "DOCTOR"]
    },
    {
      "tenant_id": "happy-teeth-456",
      "clinic_name": "Happy Teeth",
      "clinic_type": "DENTAL",
      "specialty": "DENTAL",
      "roles": ["DOCTOR"]
    }
  ],
  "user_tenant_roles": {
    "smile-dental-123": ["ADMIN", "DOCTOR"],
    "happy-teeth-456": ["DOCTOR"]
  },
  "realm_access": {
    "roles": ["ADMIN", "DOCTOR", "USER"]
  }
}
```

## Testing the Configuration

### 1. Verify User Profile Configuration

```bash
curl -X GET "http://localhost:18081/admin/realms/{realm}/users/profile" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### 2. Verify Protocol Mappers

```bash
# Get client ID
CLIENT_ID=$(curl -s "http://localhost:18081/admin/realms/{realm}/clients?clientId=clinicx-frontend" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

# List protocol mappers
curl -X GET "http://localhost:18081/admin/realms/{realm}/clients/$CLIENT_ID/protocol-mappers/models" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### 3. Test Token Generation

```bash
# Get token
TOKEN=$(curl -s -X POST \
  "http://localhost:18081/realms/{realm}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=clinicx-frontend" \
  -d "username=admin@clinic.com" \
  -d "password=password" \
  -d "grant_type=password" | jq -r '.access_token')

# Decode and verify claims
echo $TOKEN | cut -d. -f2 | base64 --decode | jq .
```

## Troubleshooting

### Common Issues

1. **Missing attributes in token**
   - Verify user profile configuration is applied
   - Check protocol mappers are created for the client
   - Ensure user has the attributes set

2. **User profile update fails**
   - Ensure `userProfileEnabled` is set to `true` in realm attributes
   - Wait for realm update to take effect before configuring profile
   - Check admin token has sufficient permissions

3. **Protocol mapper creation fails**
   - Check for duplicate mapper names
   - Verify client exists before adding mappers
   - Ensure mapper configuration is valid

## Integration with ClinicX

The programmatic configuration is automatically triggered when:

1. **Creating a new tenant** - Full realm setup with user profile and mappers
2. **Creating realm-per-type tenants** - Ensures shared realm has proper configuration
3. **Adding users** - Automatically sets multi-tenant attributes
4. **Managing tenant access** - Updates user attributes for multi-tenant scenarios

This ensures consistent configuration across all realms and proper multi-tenant support without manual Keycloak administration.