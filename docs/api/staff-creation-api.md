# Staff Creation API Documentation

## Overview
The Staff API has been updated to automatically handle Keycloak user creation and user_tenant_access records when creating new staff members.

## Endpoint
`POST /api/v1/staff`

## Headers
- `Content-Type: application/json`
- `X-Tenant-ID: {tenant_id}` - Required for tenant context
- `Authorization: Bearer {jwt_token}` - Required for authentication

## Request Body

### Create Staff with New Keycloak User
```json
{
  "fullName": "Dr. Jane Smith",
  "email": "jane.smith@sampleclinic.com",
  "phoneNumber": "+1234567890",
  "role": "DOCTOR",
  "specialtyIds": ["specialty-uuid-1", "specialty-uuid-2"],
  
  // Keycloak user creation fields
  "createKeycloakUser": true,
  "password": "TempPassword123!",
  "username": "jane.smith",  // Optional - defaults to email
  "firstName": "Jane",        // Optional - extracted from fullName
  "lastName": "Smith",        // Optional - extracted from fullName
  
  // Access control fields
  "accessRole": "DOCTOR",     // Optional - defaults to role
  "isPrimaryTenant": true     // Optional - defaults to true
}
```

### Create Staff with Existing Keycloak User
```json
{
  "fullName": "Dr. John Doe",
  "email": "john.doe@sampleclinic.com",
  "phoneNumber": "+1234567890",
  "role": "DOCTOR",
  "specialtyIds": ["specialty-uuid-1"],
  
  // Link to existing Keycloak user
  "keycloakUserId": "existing-keycloak-user-id",
  
  // Access control fields
  "accessRole": "DOCTOR",
  "isPrimaryTenant": false
}
```

### Create Staff without Keycloak User (Local Only)
```json
{
  "fullName": "Nurse Mary Johnson",
  "email": "mary.johnson@sampleclinic.com",
  "phoneNumber": "+1234567890",
  "role": "NURSE",
  
  // No Keycloak fields - creates staff record only
  "createKeycloakUser": false
}
```

## Field Descriptions

### Basic Fields
- **fullName** (required): Full name of the staff member
- **email** (required): Email address (must be unique)
- **phoneNumber** (optional): Contact phone number
- **role** (required): Staff role enum - `ADMIN`, `DOCTOR`, `NURSE`, `RECEPTIONIST`, `STAFF`, `DENTIST`, `HYGIENIST`
- **specialtyIds** (optional): Array of specialty UUIDs to assign

### Keycloak User Creation Fields
- **createKeycloakUser** (boolean): Set to `true` to create a new Keycloak user
- **password** (string): Required when `createKeycloakUser` is true (min 8 characters)
- **username** (string): Optional - defaults to email if not provided
- **firstName** (string): Optional - extracted from fullName if not provided
- **lastName** (string): Optional - extracted from fullName if not provided
- **keycloakUserId** (string): ID of existing Keycloak user to link (mutually exclusive with createKeycloakUser)

### Access Control Fields
- **accessRole** (string): Role for user_tenant_access table - defaults to the staff role
- **isPrimaryTenant** (boolean): Whether this is the primary tenant for the user - defaults to true

## Response

### Success Response (201 Created)
```json
{
  "id": "staff-uuid",
  "fullName": "Dr. Jane Smith",
  "role": "DOCTOR",
  "email": "jane.smith@sampleclinic.com",
  "phoneNumber": "+1234567890",
  "isActive": true,
  "specialties": [
    {
      "id": "specialty-uuid-1",
      "name": "Cardiology"
    },
    {
      "id": "specialty-uuid-2",
      "name": "Internal Medicine"
    }
  ],
  "createdAt": "2024-01-10T10:00:00Z",
  "updatedAt": "2024-01-10T10:00:00Z"
}
```

### Error Responses

#### 400 Bad Request - Validation Error
```json
{
  "error": "Validation failed",
  "message": "Password is required when creating a Keycloak user",
  "timestamp": "2024-01-10T10:00:00Z"
}
```

#### 409 Conflict - Email Already Exists
```json
{
  "error": "Conflict",
  "message": "Staff member with email 'jane.smith@sampleclinic.com' already exists",
  "timestamp": "2024-01-10T10:00:00Z"
}
```

## Process Flow

When creating a staff member with `createKeycloakUser: true`:

1. **Validate Request**
   - Check email uniqueness in staff table
   - Validate password meets requirements
   - Verify specialty IDs if provided

2. **Create Keycloak User**
   - Create user in tenant's Keycloak realm
   - Set username, email, first name, last name
   - Set password
   - Assign roles
   - Add tenant attributes

3. **Create Staff Record**
   - Save staff entity with keycloak_user_id
   - Link specialties
   - Set tenant_id from context

4. **Create User Tenant Access**
   - Create record in user_tenant_access table
   - Set user_id (Keycloak ID)
   - Set tenant_id
   - Set role from accessRole field (or defaults to staff role)
   - Set is_primary flag from request
   - Mark as active

## Error Handling

The service implements transactional consistency:
- If Keycloak user creation fails, no staff record is created
- If user_tenant_access creation fails for a new Keycloak user, the entire operation is rolled back
- For existing Keycloak users, user_tenant_access failure only logs a warning

## Examples

### Example 1: Create Doctor with New Keycloak Account
```bash
curl -X POST http://localhost:8080/api/v1/staff \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: clinic-001" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "fullName": "Dr. Sarah Williams",
    "email": "sarah.williams@clinic.com",
    "phoneNumber": "+1-555-0123",
    "role": "DOCTOR",
    "createKeycloakUser": true,
    "password": "SecurePass123!",
    "isPrimaryTenant": true,
    "specialtyIds": ["550e8400-e29b-41d4-a716-446655440000"]
  }'
```

### Example 2: Add Existing User as Staff
```bash
curl -X POST http://localhost:8080/api/v1/staff \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: clinic-002" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "fullName": "Dr. Michael Brown",
    "email": "michael.brown@anotherclinic.com",
    "role": "DOCTOR",
    "keycloakUserId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "isPrimaryTenant": false,
    "accessRole": "CONSULTANT"
  }'
```

### Example 3: Create Local Staff (No Keycloak)
```bash
curl -X POST http://localhost:8080/api/v1/staff \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: clinic-001" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "fullName": "Reception Desk",
    "email": "reception@clinic.com",
    "role": "RECEPTIONIST",
    "createKeycloakUser": false
  }'
```

## Related Endpoints

- `GET /api/v1/staff/{id}` - Get staff details
- `PUT /api/v1/staff/{id}` - Update staff
- `DELETE /api/v1/staff/{id}` - Deactivate staff
- `GET /api/v1/staff` - List all staff
- `GET /api/v1/staff/search` - Search staff

## Notes

1. **Tenant Context**: The X-Tenant-ID header is required to determine which tenant the staff belongs to
2. **Unique Emails**: Email addresses must be unique within the entire system
3. **Password Requirements**: Minimum 8 characters when creating Keycloak users
4. **Primary Tenant**: Each user should have exactly one primary tenant
5. **Roles**: The role in the staff table and user_tenant_access can differ (e.g., DOCTOR in staff, CONSULTANT in access)
6. **No Database Trigger**: User access management is handled entirely in the application layer for better control and to avoid conflicts