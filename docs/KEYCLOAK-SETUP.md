# Keycloak Security Setup for ClinicX

## Overview
This document explains how to test the Keycloak integration with ClinicX. The application is now secured with Keycloak OAuth2/JWT authentication.

## Prerequisites
- Keycloak running at `http://localhost:18081`
- Realm: `clinicx-dev`
- Client: `clinicx-backend`
- Client Secret: `tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk`

## Quick Start

### 1. Create Test Users in Keycloak

Navigate to your Keycloak admin console and create these test users:

1. **Admin User**
   - Username: `admin@clinicx.com`
   - Password: `admin123`
   - Realm Roles: `ADMIN`

2. **Doctor User**
   - Username: `doctor@clinicx.com`
   - Password: `doctor123`
   - Realm Roles: `DOCTOR`

3. **Staff User**
   - Username: `staff@clinicx.com`
   - Password: `staff123`
   - Realm Roles: `STAFF`

### 2. Get Access Token

#### Using cURL:
```bash
# Get token for admin user
curl -X POST http://localhost:18081/realms/clinicx-dev/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=clinicx-backend" \
  -d "client_secret=tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk" \
  -d "username=admin@clinicx.com" \
  -d "password=admin123" \
  -d "grant_type=password"
```

#### Using HTTPie:
```bash
http --form POST http://localhost:18081/realms/clinicx-dev/protocol/openid-connect/token \
  client_id=clinicx-backend \
  client_secret=tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk \
  username=admin@clinicx.com \
  password=admin123 \
  grant_type=password
```

#### Response Example:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6IC...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6IC...",
  "token_type": "Bearer",
  "not-before-policy": 0,
  "session_state": "...",
  "scope": "email profile"
}
```

### 3. Test API Endpoints

#### Public Endpoint (No Auth Required):
```bash
curl http://localhost:8080/actuator/health
```

#### Protected Endpoint (Auth Required):
```bash
# Set token variable
ACCESS_TOKEN="<paste-your-access-token-here>"

# Get all patients
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
     http://localhost:8080/api/v1/patients
```

#### Admin-Only Endpoint:
```bash
# This will only work with admin token
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
     http://localhost:8080/api/admin/users
```

### 4. Test with Swagger UI

1. Open http://localhost:8080/swagger-ui.html
2. Click "Authorize" button
3. Enter: `Bearer <your-access-token>`
4. Click "Authorize"
5. Now you can test all endpoints with authentication

## Security Features Implemented

### 1. JWT Token Validation
- Tokens are validated against Keycloak's public keys
- Token expiration is enforced
- Issuer validation ensures tokens come from correct realm

### 2. Role-Based Access Control
- Realm roles are converted to Spring Security authorities
- Method-level security with `@PreAuthorize`
- Endpoint-level security in SecurityConfig

### 3. Multi-Tenancy Preparation
- TenantContext for future tenant isolation
- Tenant ID can be added to JWT claims
- TenantResolver extracts tenant from JWT

### 4. CORS Configuration
- Configured for common frontend ports (3000, 4200)
- Allows Authorization header
- Credentials enabled for cookie support

## Troubleshooting

### Common Issues:

1. **401 Unauthorized**
   - Check token is not expired
   - Verify token includes "Bearer " prefix
   - Ensure Keycloak is running

2. **403 Forbidden**
   - User doesn't have required role
   - Check role mappings in Keycloak

3. **Connection Refused**
   - Verify Keycloak is running on port 18081
   - Check application.yml configuration

### Debug Mode:
Enable debug logging in application.yml:
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
```

## Adding Multi-Tenancy Support

### Step 1: Add Tenant Attribute to Users

#### Method A: Via Keycloak Admin Console (GUI)

1. **Login to Keycloak Admin Console**
   - URL: http://localhost:18081/auth/
   - Login with admin credentials

2. **Navigate to Users**
   - Select realm: `clinicx-dev`
   - Left menu: Click `Users`
   - Click on the user you want to edit

3. **Add Tenant Attribute**
   - Click on `Attributes` tab
   - Click `Add attribute`
   - Key: `tenant_id`
   - Value: `tenant-001` (or your tenant ID)
   - Click `Save`

![User Attributes Example]
```
Key         | Value        | Actions
------------|--------------|----------
tenant_id   | tenant-001   | [Delete]
clinic_name | Smile Dental | [Delete]
clinic_type | DENTAL       | [Delete]
```

#### Method B: Via Keycloak Admin API

```bash
# Get admin token first
ADMIN_TOKEN=$(curl -s -X POST "http://localhost:18081/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

# Get user ID
USER_ID=$(curl -s -X GET "http://localhost:18081/admin/realms/clinicx-dev/users?username=admin@clinicx.com" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

# Update user attributes
curl -X PUT "http://localhost:18081/admin/realms/clinicx-dev/users/$USER_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "attributes": {
      "tenant_id": ["tenant-001"],
      "clinic_name": ["Smile Dental Clinic"],
      "clinic_type": ["DENTAL"]
    }
  }'
```

### Step 2: Create Protocol Mapper (CRITICAL!)

Without this mapper, the tenant_id won't appear in the JWT token!

1. **Navigate to Client Settings**
   - Clients → `clinicx-backend`
   - Click on `Client scopes` tab
   - Click on `clinicx-backend-dedicated`

2. **Add a New Mapper**
   - Click `Configure a new mapper`
   - Select `User Attribute`

3. **Configure the Mapper**
   ```
   Name:                tenant-mapper
   Mapper Type:         User Attribute
   User Attribute:      tenant_id
   Token Claim Name:    tenant_id
   Claim JSON Type:     String
   Add to ID token:     ON
   Add to access token: ON ✓ (MUST be checked!)
   Add to userinfo:     ON
   ```

4. **Save** the mapper

### Step 3: Verify Token Contains Tenant ID

```bash
# Get a token for the user
TOKEN_RESPONSE=$(curl -s -X POST http://localhost:18081/realms/clinicx-dev/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=clinicx-backend" \
  -d "client_secret=tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk" \
  -d "username=admin@clinicx.com" \
  -d "password=admin123" \
  -d "grant_type=password")

# Extract and decode the access token
ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')

# Decode JWT (you can also use jwt.io)
echo $ACCESS_TOKEN | cut -d. -f2 | base64 --decode | jq .

# Should see:
{
  "exp": 1704094423,
  "iat": 1704094123,
  "preferred_username": "admin@clinicx.com",
  "tenant_id": "tenant-001",  ← This should appear!
  "realm_access": {
    "roles": ["ADMIN"]
  }
  ...
}
```

### Step 4: Enable Multi-Tenant Mode in Application

```yaml
# application.yml
clinicx:
  tenant:
    mode: multi  # Change from 'single' to 'multi'
    # default-id is now only used as fallback
```

### Step 5: Test Multi-Tenancy

```bash
# Test that tenant context is set correctly
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
     http://localhost:8080/api/auth/test/authenticated

# Response should include:
{
  "current_tenant": "tenant-001",  ← Extracted from JWT
  "jwt_claims": {
    "tenant_id": "tenant-001"
  }
}
```

### Troubleshooting

#### Token doesn't contain tenant_id?

1. **Check mapper is created correctly**
   - Must be in the client's dedicated scope
   - "Add to access token" must be ON

2. **Check user has the attribute**
   ```bash
   # Via API
   curl -X GET "http://localhost:18081/admin/realms/clinicx-dev/users/$USER_ID" \
     -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.attributes'
   ```

3. **Force token refresh**
   - Logout and login again
   - Or revoke existing tokens

#### Multiple Tenants Per User?

For users who can access multiple tenants:
```json
{
  "attributes": {
    "tenant_id": ["tenant-001", "tenant-002"],
    "default_tenant": ["tenant-001"]
  }
}
```

Then modify your `KeycloakTenantResolver` to handle arrays.

### Bulk User Import with Tenant IDs

```json
{
  "users": [
    {
      "username": "doctor1@clinic1.com",
      "email": "doctor1@clinic1.com",
      "enabled": true,
      "attributes": {
        "tenant_id": ["tenant-001"],
        "clinic_name": ["Smile Dental"]
      },
      "credentials": [
        {
          "type": "password",
          "value": "password123",
          "temporary": false
        }
      ],
      "realmRoles": ["DOCTOR"]
    }
  ]
}
```

Save as `users.json` and import via Keycloak admin.

## Testing Without Keycloak

For unit tests, use the `@WithMockUser` annotation:
```java
@Test
@WithMockUser(roles = "ADMIN")
void testAdminEndpoint() {
    // Test code
}
```

## Scripts for Development

### Get Token Script (save as get-token.sh):
```bash
#!/bin/bash
USERNAME=${1:-admin@clinicx.com}
PASSWORD=${2:-admin123}

TOKEN_RESPONSE=$(curl -s -X POST http://localhost:18081/realms/clinicx-dev/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=clinicx-backend" \
  -d "client_secret=tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk" \
  -d "username=$USERNAME" \
  -d "password=$PASSWORD" \
  -d "grant_type=password")

echo $TOKEN_RESPONSE | jq -r '.access_token'
```

Usage:
```bash
# Get admin token
./get-token.sh admin@clinicx.com admin123

# Get doctor token
./get-token.sh doctor@clinicx.com doctor123
```

## Next Steps

1. **Create users** in Keycloak with appropriate roles
2. **Test endpoints** with different user roles
3. **Add tenant attributes** when ready for multi-tenancy
4. **Configure feature flags** as client roles