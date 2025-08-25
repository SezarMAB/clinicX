# Sub-Tenant Creation Guide for ClinicX

## Overview

This guide explains how to create sub-tenants (branch locations) for existing tenants in the ClinicX multi-tenant architecture. While the current implementation uses a realm-per-tenant approach without true parent-child relationships, this guide provides the recommended approach for managing multiple locations under the same organization.

## Current Architecture

### Key Concepts
- **Realm-per-Tenant**: Each tenant gets its own Keycloak realm
- **Specialty-based Grouping**: Tenants are grouped by specialty (DENTAL, CLINIC, etc.)
- **Cross-Tenant Access**: Users can access multiple tenants with different roles
- **Independent Tenants**: No built-in parent-child hierarchy

## Creating a Sub-Tenant

### Step 1: Create the New Tenant

Use the tenant creation endpoint to create a new tenant for the branch location:

```bash
POST /api/v1/tenants
Authorization: Bearer {super-admin-token}
Content-Type: application/json

{
  "name": "Happy Teeth Dental - Downtown Branch",
  "subdomain": "happy-teeth-downtown",
  "specialty": "DENTAL",  // Same specialty as parent
  "contactEmail": "downtown@happyteeth.com",
  "contactPhone": "+1-555-0123",
  "address": "123 Downtown St, City, State",
  "subscriptionPlan": "premium",
  "maxUsers": 20,
  "maxPatients": 2000,
  "adminUsername": "admin-downtown",
  "adminEmail": "admin@downtown.happyteeth.com",
  "adminFirstName": "Admin",
  "adminLastName": "Downtown",
  "adminPassword": "SecurePassword123!"
}
```

### Step 2: Grant Cross-Tenant Access

Grant existing users from the parent tenant access to the new sub-tenant:

```bash
POST /api/v1/users/{userId}/tenant-access
Authorization: Bearer {super-admin-token}
Content-Type: application/json

{
  "tenantId": "{new-tenant-id}",
  "role": "ADMIN",
  "isPrimary": false
}
```

### Step 3: Configure User Permissions

For each user that needs access to the sub-tenant:

1. **Admin Users**: Grant ADMIN role for full management
2. **Staff Users**: Grant appropriate roles (DOCTOR, NURSE, RECEPTIONIST)
3. **Shared Resources**: Grant READ_ONLY access if needed

## Best Practices for Sub-Tenants

### 1. Naming Convention
Use a consistent naming pattern:
- **Format**: `{Parent Name} - {Branch Identifier}`
- **Examples**: 
  - "Happy Teeth Dental - Downtown Branch"
  - "Happy Teeth Dental - Airport Location"
  - "Happy Teeth Dental - Mall Clinic"

### 2. Subdomain Pattern
Create logical subdomain structures:
- **Format**: `{parent}-{branch}`
- **Examples**:
  - `happy-teeth-downtown`
  - `happy-teeth-airport`
  - `happy-teeth-mall`

### 3. User Management Strategy

#### Centralized Admin
1. Create a super-admin user in the parent tenant
2. Grant this user ADMIN access to all sub-tenants
3. Use this account for centralized management

#### Branch Managers
1. Create branch-specific admin accounts
2. Grant access only to their respective branch
3. Optionally grant read-only access to other branches

### 4. Data Isolation Considerations

Since each tenant has its own realm:
- **Patient Data**: Isolated per branch by default
- **Shared Resources**: Must be manually synchronized
- **Reporting**: Requires cross-tenant data aggregation

## User Access Management

### How Users Access Multiple Tenants

Users with multi-tenant access have the following attributes:

```json
{
  "accessible_tenants": [
    {
      "tenant_id": "tenant-001",
      "clinic_name": "Happy Teeth Dental - Main",
      "clinic_type": "DENTAL",
      "specialty": "DENTAL",
      "roles": ["ADMIN"]
    },
    {
      "tenant_id": "tenant-002",
      "clinic_name": "Happy Teeth Dental - Downtown",
      "clinic_type": "DENTAL",
      "specialty": "DENTAL",
      "roles": ["ADMIN"]
    }
  ],
  "user_tenant_roles": {
    "tenant-001": ["ADMIN"],
    "tenant-002": ["ADMIN"]
  },
  "active_tenant_id": "tenant-001"
}
```

### Switching Between Tenants

Users can switch between accessible tenants:

```bash
POST /api/v1/tenants/switch
Authorization: Bearer {user-token}
Content-Type: application/json

{
  "tenantId": "tenant-002"
}
```

## API Examples

### List User's Accessible Tenants

```bash
GET /api/v1/users/{userId}/tenant-access
Authorization: Bearer {token}
```

Response:
```json
[
  {
    "tenantId": "tenant-001",
    "tenantName": "Happy Teeth Dental - Main",
    "specialty": "DENTAL",
    "roles": ["ADMIN"],
    "isPrimary": true,
    "isActive": true
  },
  {
    "tenantId": "tenant-002",
    "tenantName": "Happy Teeth Dental - Downtown",
    "specialty": "DENTAL",
    "roles": ["ADMIN"],
    "isPrimary": false,
    "isActive": false
  }
]
```

### Revoke Tenant Access

```bash
DELETE /api/v1/users/{userId}/tenant-access/{tenantId}
Authorization: Bearer {super-admin-token}
```

## Limitations and Considerations

### Current Limitations
1. **No True Hierarchy**: Sub-tenants are independent realms
2. **No Inheritance**: Settings don't cascade from parent to child
3. **Manual Synchronization**: Shared data requires custom implementation
4. **Separate Authentication**: Each realm has its own user base

### Future Enhancements
Consider implementing:
1. **Tenant Relationships**: Track parent-child relationships in the database
2. **Shared Resources**: Central repository for shared clinic resources
3. **Consolidated Reporting**: Cross-tenant analytics and reporting
4. **Template Inheritance**: Copy settings from parent when creating sub-tenants

## Security Considerations

### Access Control
- Verify user permissions before granting cross-tenant access
- Regularly audit user access across tenants
- Implement role-based restrictions per tenant

### Data Privacy
- Ensure patient data remains isolated per tenant
- Implement audit logs for cross-tenant access
- Consider compliance requirements (HIPAA, GDPR)

## Troubleshooting

### Common Issues

1. **Token Claim Errors**
   - Ensure user attributes are properly formatted JSON strings
   - Verify protocol mappers are configured correctly

2. **Access Denied**
   - Check user's accessible_tenants attribute
   - Verify active_tenant_id matches the target tenant

3. **Switching Fails**
   - Ensure user has access to the target tenant
   - Verify tenant exists and is active

## Conclusion

While the current architecture doesn't support true sub-tenant relationships, the multi-tenant access system provides flexibility for managing multiple clinic locations. Follow the best practices outlined above to maintain a clean and manageable tenant structure.