# Keycloak Multi-Tenant Configuration Guide

## Overview
This guide explains how the Spring Boot backend is configured to work with the Angular frontend's multi-tenant Keycloak setup.

## Angular Frontend Realm Detection Logic

The Angular `KeycloakConfigService` detects realms based on subdomain:

1. **Development (localhost)**: Uses `master` realm
2. **Production**: Extracts subdomain and builds realm as `clinic-{subdomain}`
   - Example: `smile-dental.clinicx.com` → realm: `clinic-smile-dental`

## Spring Boot Configuration

### 1. Development Profile (`application-dev.yml`)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Default for development (matches Angular's localhost behavior)
          issuer-uri: http://localhost:18081/realms/master
          jwk-set-uri: http://localhost:18081/realms/master/protocol/openid-connect/certs

clinicx:
  tenant:
    mode: multi  # Enable multi-tenant mode
    default-id: dev-tenant-001
  
  security:
    keycloak:
      realm: master  # Default realm for localhost
      client-id: clinicx-backend
      client-secret: ${KEYCLOAK_CLIENT_SECRET:tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk}

app:
  multi-tenant:
    enabled: true
    default-tenant: master  # Matches Angular's default
    default-realm: master
    realm-prefix: clinic-  # Pattern for production realms
```

### 2. Production Configuration

For production, override these environment variables:

```bash
# Enable multi-tenant mode
TENANT_MODE=multi
MULTI_TENANT_ENABLED=true

# Domain configuration
APP_DOMAIN=clinicx.com

# Default realm for fallback
DEFAULT_REALM=master
KEYCLOAK_REALM=master

# Keycloak base URL
KEYCLOAK_AUTH_SERVER_URL=https://auth.clinicx.com
```

### 3. How Multi-Tenant JWT Validation Works

The `MultiTenantJwtDecoder` class:

1. **Extracts realm from JWT issuer**
   - JWT issuer: `http://localhost:18081/realms/clinic-smile-dental`
   - Extracted realm: `clinic-smile-dental`

2. **Validates tenant exists and is active**
   - Checks against `TenantRepository`
   - Ensures tenant is enabled

3. **Dynamically creates JWT decoders**
   - Caches decoders per realm
   - Uses realm-specific JWK Set URI

4. **Sets tenant context**
   - Stores tenant ID in thread-local context
   - Available throughout request processing

## Creating New Tenants

### 1. Create Keycloak Realm

```bash
# Example: Create realm for "smile-dental" subdomain
REALM_NAME="clinic-smile-dental"
```

### 2. Create Clients in New Realm

#### Backend Client (Confidential)
- Client ID: `clinicx-backend`
- Client Protocol: `openid-connect`
- Access Type: `confidential`
- Generate client secret

#### Frontend Client (Public)
- Client ID: `clinicx-frontend`
- Client Protocol: `openid-connect`
- Access Type: `public`
- Valid Redirect URIs: 
  - `https://smile-dental.clinicx.com/*`
  - `http://localhost:4200/*` (for development)

### 3. Configure User Attributes

Add these attributes to users in the realm:
- `tenant_id`: The tenant identifier (e.g., `tenant-smile-dental`)
- `clinic_name`: Display name (e.g., `Smile Dental Clinic`)
- `clinic_type`: Type of clinic (e.g., `DENTAL`)

### 4. Create Protocol Mappers

For each attribute, create a mapper:
```
Name: tenant-mapper
Mapper Type: User Attribute
User Attribute: tenant_id
Token Claim Name: tenant_id
Add to access token: ON
```

### 5. Register Tenant in Database

```sql
INSERT INTO tenants (
    tenant_id, 
    subdomain, 
    realm_name, 
    name, 
    is_active
) VALUES (
    'tenant-smile-dental',
    'smile-dental',
    'clinic-smile-dental',
    'Smile Dental Clinic',
    true
);
```

## Testing Multi-Tenant Setup

### 1. Get Token for Specific Realm

```bash
# For development (master realm)
curl -X POST http://localhost:18081/realms/master/protocol/openid-connect/token \
  -d "client_id=clinicx-backend" \
  -d "client_secret=your-secret" \
  -d "username=user@example.com" \
  -d "password=password" \
  -d "grant_type=password"

# For production tenant
curl -X POST https://auth.clinicx.com/realms/clinic-smile-dental/protocol/openid-connect/token \
  -d "client_id=clinicx-backend" \
  -d "client_secret=tenant-specific-secret" \
  -d "username=user@smiledental.com" \
  -d "password=password" \
  -d "grant_type=password"
```

### 2. Verify JWT Contains Correct Issuer

Decode the JWT and check:
```json
{
  "iss": "http://localhost:18081/realms/clinic-smile-dental",
  "tenant_id": "tenant-smile-dental",
  "preferred_username": "user@smiledental.com"
}
```

### 3. Test API with Tenant-Specific Token

```bash
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/v1/patients
```

## Environment-Specific Configurations

### Local Development
- Uses `master` realm by default
- Single Keycloak instance at `localhost:18081`
- Can override with environment variables for testing

### Staging/Production
- Each tenant has its own realm
- Realm naming follows `clinic-{subdomain}` pattern
- Frontend automatically detects realm from subdomain
- Backend validates JWT against correct realm

## Troubleshooting

### Common Issues

1. **"Invalid or inactive tenant" error**
   - Ensure tenant exists in database
   - Check `is_active` flag is true
   - Verify `realm_name` matches JWT issuer

2. **JWT validation fails**
   - Check realm-specific JWK Set URI is accessible
   - Verify client credentials are correct
   - Ensure realm exists in Keycloak

3. **Tenant context not set**
   - Verify JWT contains `tenant_id` claim
   - Check protocol mapper configuration
   - Ensure user has tenant_id attribute

### Debug Logging

Enable debug logging for troubleshooting:
```yaml
logging:
  level:
    sy.sezar.clinicx.core.security: DEBUG
    sy.sezar.clinicx.tenant: DEBUG
    org.springframework.security: DEBUG
```

## Security Considerations

1. **Realm Isolation**: Each tenant's data is completely isolated in Keycloak
2. **Dynamic Validation**: JWTs are validated against realm-specific keys
3. **Tenant Verification**: Every request validates tenant exists and is active
4. **Context Propagation**: Tenant context is available throughout request lifecycle