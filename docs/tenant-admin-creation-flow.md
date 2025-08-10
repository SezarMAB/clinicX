# Tenant Admin Creation Flow

## Overview
This document explains how tenant creation works with admin users and how all necessary records are created in the system.

## Data Flow Architecture

```
Tenant Creation Request
         ↓
    Keycloak Realm
    (Create Realm & Admin User)
         ↓
    Tenant Record
    (Save in tenants table)
         ↓
    Staff Record
    (Create admin staff with keycloak_user_id)
         ↓
    User Tenant Access
    (Grant admin access with is_primary=true)
```

## 1. Tenant Creation with Admin User

### Two Service Implementations

#### A. EnhancedTenantServiceImpl (Realm-per-type mode)
Used when `app.multi-tenant.realm-per-type=true`

```java
// Location: EnhancedTenantServiceImpl.java

1. Creates Keycloak user in shared realm
2. Creates Tenant record
3. Creates Staff record with:
   - keycloak_user_id
   - tenant_id
   - role = ADMIN
4. Creates user_tenant_access record via:
   userTenantAccessService.createAdminAccess(keycloakUserId, tenantId)
```

#### B. TenantServiceImpl (Standard mode)
Used for traditional realm-per-tenant approach

```java
// Location: TenantServiceImpl.java

1. Creates new Keycloak realm
2. Creates admin user in new realm
3. Creates Tenant record
4. Creates Staff record with:
   - keycloak_user_id
   - tenant_id  
   - role = ADMIN
5. Creates user_tenant_access record via:
   userTenantAccessService.createAdminAccess(adminUser.getId(), tenantId)
```

## 2. Staff Creation Flow

When creating a new staff member through the Staff API:

### Request
```json
POST /api/v1/staff
{
  "fullName": "Dr. Jane Smith",
  "email": "jane.smith@clinic.com",
  "role": "DOCTOR",
  "createKeycloakUser": true,
  "password": "SecurePass123!",
  "isPrimaryTenant": false
}
```

### Process
```java
// Location: StaffServiceImpl.java

1. Create Keycloak user (if requested)
2. Save Staff record with keycloak_user_id
3. Create user_tenant_access record:
   - user_id = keycloak_user_id
   - tenant_id = from context
   - role = from request
   - is_primary = from request
```

## 3. Database Schema

### Tables Involved

#### tenants
```sql
- tenant_id (unique identifier)
- name
- subdomain
- realm_name
- is_active
```

#### staff
```sql
- id (UUID)
- keycloak_user_id (links to Keycloak)
- tenant_id (tenant context)
- full_name
- email
- role
- is_active
```

#### user_tenant_access
```sql
- id (UUID)
- user_id (Keycloak user ID)
- tenant_id
- role
- is_primary
- is_active
UNIQUE(user_id, tenant_id)
```

## 4. Key Points

### Consistency Rules
1. **Every admin user** gets:
   - A Staff record in the staff table
   - A user_tenant_access record with role=ADMIN and is_primary=true

2. **Every staff member with Keycloak** gets:
   - A user_tenant_access record for authorization
   - Proper role and primary tenant settings

3. **No Database Triggers**:
   - All logic handled in application code
   - Avoids race conditions and duplicate key errors

### Authorization Flow
```
User Login → JWT Token → Extract user_id
                ↓
         Check user_tenant_access
                ↓
         Verify tenant access & role
                ↓
         Load Staff data for business logic
```

## 5. Common Scenarios

### Scenario 1: Create New Clinic with Admin
1. Admin fills tenant creation form
2. System creates Keycloak realm/user
3. System creates tenant, staff, and access records
4. Admin can login immediately

### Scenario 2: Add Doctor to Existing Clinic
1. Admin creates staff via API
2. System creates Keycloak user
3. System creates staff and access records
4. Doctor gets login credentials

### Scenario 3: Grant Existing User Access to Another Clinic
1. Admin adds existing user to new clinic
2. System creates new staff record in new tenant
3. System creates new user_tenant_access record
4. User can switch between clinics

## 6. Troubleshooting

### Issue: User created but can't login
**Check:**
- user_tenant_access record exists
- is_active = true in both staff and user_tenant_access
- Keycloak user is enabled

### Issue: Duplicate key error on user_tenant_access
**Cause:** User already has access to this tenant
**Solution:** Check for existing access before creating

### Issue: Admin user missing after tenant creation
**Check:**
- Both TenantServiceImpl implementations now create staff records
- userTenantAccessService.createAdminAccess() is called
- No exceptions in logs during tenant creation

## 7. Migration Notes

For existing systems:
1. Run V15 migration to refactor staff table
2. Run V16 migration to remove database triggers
3. Ensure all admin users have staff and user_tenant_access records
4. Verify no orphaned records exist

## 8. API Endpoints

### Tenant Management
- `POST /api/tenants` - Creates tenant with admin user, staff, and access records
- `GET /api/tenants/{id}` - Get tenant details

### Staff Management  
- `POST /api/v1/staff` - Creates staff with optional Keycloak user and access record
- `GET /api/v1/staff/{id}` - Get staff details

### Access Management
- `POST /api/user-access/grant` - Grant user access to tenant
- `GET /api/user-access/user/{userId}` - Get user's tenant accesses