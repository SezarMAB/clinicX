# Realm-Per-Type Sub-Tenant Creation Guide

## Overview

This guide explains how to create sub-tenants (additional tenants) within the same specialty using the realm-per-type architecture in ClinicX. In this architecture, all tenants of the same specialty share a single Keycloak realm, which requires special considerations when creating new tenants.

## Understanding the Architecture

### Key Concepts

1. **Shared Realms**: All tenants of the same specialty share one Keycloak realm
   - `appointments-realm`: For all APPOINTMENTS specialty tenants
   - `dental-realm`: For all DENTAL specialty tenants
   - `clinic-realm`: For all CLINIC specialty tenants

2. **Unique Tenant IDs**: Each tenant gets a unique identifier within the shared realm

3. **User Management**: Users in shared realms can access multiple tenants based on permissions

## The Username Conflict Problem

When creating multiple tenants in the same realm, you cannot use duplicate usernames. This is why you received the error:

```json
{
  "status": 400,
  "error": "Business Rule Violation",
  "message": "Failed to create tenant: Failed to create user with tenant info: Failed to create user: Conflict"
}
```

### Why This Happens

1. First tenant with specialty "APPOINTMENTS" created:
   - Created `appointments-realm`
   - Created user "admin" in that realm

2. Second tenant with same specialty attempted:
   - Uses existing `appointments-realm`
   - Tries to create another user "admin" → **CONFLICT!**

## Solutions for Creating Sub-Tenants

### Option 1: Tenant-Specific Admin Usernames (Recommended)

Use a naming convention that includes the tenant identifier:

```json
{
  "name": "Dental Downtown Branch",
  "subdomain": "dental-downtown",
  "contactEmail": "downtown@dental.com",
  "contactPhone": "+1-555-0123",
  "address": "123 Downtown Street",
  "subscriptionPlan": "PREMIUM",
  "maxUsers": 5,
  "maxPatients": 110,
  "adminUsername": "admin-dental-downtown",  // Unique username
  "adminEmail": "admin@dental-downtown.com",
  "adminFirstName": "Admin",
  "adminLastName": "Downtown",
  "adminPassword": "SecurePassword123!",
  "specialty": "APPOINTMENTS"
}
```

### Option 2: Email as Username

Use the admin's email address as the username:

```json
{
  "name": "Dental Airport Location",
  "subdomain": "dental-airport",
  "contactEmail": "airport@dental.com",
  "contactPhone": "+1-555-0456",
  "address": "456 Airport Road",
  "subscriptionPlan": "PREMIUM",
  "maxUsers": 8,
  "maxPatients": 200,
  "adminUsername": "admin@dental-airport.com",  // Email as username
  "adminEmail": "admin@dental-airport.com",
  "adminFirstName": "Admin",
  "adminLastName": "Airport",
  "adminPassword": "SecurePassword123!",
  "specialty": "APPOINTMENTS"
}
```

### Option 3: Subdomain Prefix Pattern

Prefix the username with the subdomain:

```json
{
  "name": "Dental Mall Clinic",
  "subdomain": "dental-mall",
  "contactEmail": "mall@dental.com",
  "contactPhone": "+1-555-0789",
  "address": "789 Mall Drive",
  "subscriptionPlan": "STANDARD",
  "maxUsers": 3,
  "maxPatients": 80,
  "adminUsername": "dental-mall-admin",  // Subdomain prefix
  "adminEmail": "admin@dental-mall.com",
  "adminFirstName": "Admin",
  "adminLastName": "Mall",
  "adminPassword": "SecurePassword123!",
  "specialty": "APPOINTMENTS"
}
```

## Best Practices

### 1. Username Naming Convention

Establish a consistent pattern for admin usernames:
- **Pattern**: `{subdomain}-admin` or `admin-{subdomain}`
- **Example**: `dental-downtown-admin`, `admin-dental-airport`

### 2. Email Standards

Use subdomain-specific email addresses:
- **Pattern**: `admin@{subdomain}.{domain}.com`
- **Example**: `admin@dental-downtown.clinicx.com`

### 3. Documentation

Keep track of:
- Which tenants belong to which specialty/realm
- Admin username patterns used
- Tenant relationships (parent-branch associations)

## API Examples

### Creating the First Tenant (Creates New Realm)

```bash
POST /api/v1/tenants
Authorization: Bearer {super-admin-token}
Content-Type: application/json

{
  "name": "Dental Main Office",
  "subdomain": "dental-main",
  "contactEmail": "main@dental.com",
  "contactPhone": "+1-555-0100",
  "address": "100 Main Street",
  "subscriptionPlan": "PREMIUM",
  "maxUsers": 10,
  "maxPatients": 500,
  "adminUsername": "admin-dental-main",
  "adminEmail": "admin@dental-main.com",
  "adminFirstName": "Admin",
  "adminLastName": "Main",
  "adminPassword": "SecurePassword123!",
  "specialty": "APPOINTMENTS"
}
```

Response:
```json
{
  "id": "uuid-here",
  "tenantId": "dental-main-abc123",
  "name": "Dental Main Office",
  "subdomain": "dental-main",
  "realmName": "appointments-realm",  // Shared realm created
  "backendClientId": "clinicx-backend",
  "backendClientSecret": "secret-here",
  "frontendClientId": "clinicx-frontend",
  "keycloakServerUrl": "http://localhost:18081",
  "adminUsername": "admin-dental-main"
}
```

### Creating Sub-Tenants (Uses Existing Realm)

```bash
POST /api/v1/tenants
Authorization: Bearer {super-admin-token}
Content-Type: application/json

{
  "name": "Dental Downtown Branch",
  "subdomain": "dental-downtown",
  "contactEmail": "downtown@dental.com",
  "contactPhone": "+1-555-0200",
  "address": "200 Downtown Street",
  "subscriptionPlan": "STANDARD",
  "maxUsers": 5,
  "maxPatients": 200,
  "adminUsername": "admin-dental-downtown",  // Must be unique!
  "adminEmail": "admin@dental-downtown.com",
  "adminFirstName": "Admin",
  "adminLastName": "Downtown",
  "adminPassword": "SecurePassword123!",
  "specialty": "APPOINTMENTS"  // Same specialty = same realm
}
```

Response:
```json
{
  "id": "uuid-here",
  "tenantId": "dental-downtown-def456",
  "name": "Dental Downtown Branch",
  "subdomain": "dental-downtown",
  "realmName": "appointments-realm",  // Uses existing shared realm
  "backendClientId": "clinicx-backend",
  "backendClientSecret": "secret-here",
  "frontendClientId": "clinicx-frontend",
  "keycloakServerUrl": "http://localhost:18081",
  "adminUsername": "admin-dental-downtown"
}
```

## How Users Access Multiple Tenants

In the realm-per-type architecture, users automatically get configured with multi-tenant attributes:

### User Attributes Structure

```json
{
  "tenant_id": "dental-downtown-def456",
  "primary_tenant_id": "dental-downtown-def456",
  "clinic_name": "Dental Downtown Branch",
  "clinic_type": "APPOINTMENTS",
  "active_tenant_id": "dental-downtown-def456",
  "accessible_tenants": [
    {
      "tenant_id": "dental-downtown-def456",
      "clinic_name": "Dental Downtown Branch",
      "clinic_type": "APPOINTMENTS",
      "specialty": "APPOINTMENTS",
      "roles": ["ADMIN"]
    }
  ],
  "user_tenant_roles": {
    "dental-downtown-def456": ["ADMIN"]
  }
}
```

### Granting Cross-Tenant Access

To allow a user to access multiple tenants within the same realm:

```bash
POST /api/v1/users/{userId}/tenant-access
Authorization: Bearer {super-admin-token}
Content-Type: application/json

{
  "tenantId": "dental-main-abc123",
  "role": "ADMIN",
  "isPrimary": false
}
```

## Common Issues and Solutions

### Issue 1: Username Already Exists

**Error**: "Failed to create user: Conflict"
**Solution**: Use a unique username following the patterns above

### Issue 2: Realm Not Found

**Error**: "Realm not found"
**Cause**: Trying to access a specialty that hasn't been created yet
**Solution**: The first tenant of a new specialty will create the realm

### Issue 3: Token Claim Errors

**Error**: "cannot map type for token claim"
**Solution**: Ensure protocol mappers use "String" type, not "JSON" type

## Security Considerations

1. **Username Patterns**: Use predictable patterns for easier management but ensure passwords are strong
2. **Access Control**: Regularly audit which users have access to which tenants
3. **Realm Isolation**: Remember that users in the same realm can potentially see each other
4. **Password Policies**: Configure strong password policies at the realm level

## Summary

The realm-per-type architecture offers efficient resource usage by sharing realms among tenants of the same specialty. The key to successfully creating sub-tenants is ensuring unique usernames within each shared realm. Follow the naming conventions and best practices outlined above to maintain a clean and manageable multi-tenant system.