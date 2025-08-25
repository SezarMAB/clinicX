# Architectural Decision: user_tenant_access vs staff Table

## Executive Summary

**Recommendation**: Use a **separate `user_tenant_access` table** instead of integrating multi-tenant fields into the `staff` table.

## Context

The system is evaluating two approaches for managing multi-tenant user access:
1. **Separate table**: `user_tenant_access` table for user-tenant relationships, with `staff` table for business data
2. **Integrated approach**: Multi-tenant fields (`userId`, `tenantId`, `isPrimary`) directly in the `staff` table

## Current Implementation Analysis

### Current Staff Entity
```java
public class Staff extends BaseEntity {
    private String fullName;
    private StaffRole role;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    
    // Integrated multi-tenant fields
    private String userId;     // Keycloak user ID
    private String tenantId;
    private boolean isPrimary;
}
```

### Existing Migration History
- **V1**: Initial schema with basic `staff` table
- **V4**: Added `tenants` table for multi-tenancy
- **V8**: Introduced `user_tenant_access` table (currently unused)

## Architectural Options Comparison

### Option 1: Separate user_tenant_access Table ✅ RECOMMENDED

#### Structure
```sql
-- Public schema (shared across all tenants)
CREATE TABLE user_tenant_access (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,      -- Keycloak user ID
    tenant_id VARCHAR(255) NOT NULL,    -- References tenants.tenant_id
    role VARCHAR(50) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, tenant_id)
);

-- Shared schema mode (current)
CREATE TABLE staff (
    id UUID PRIMARY KEY,
    keycloak_user_id VARCHAR(255),      -- Links to Keycloak user
    tenant_id VARCHAR(255),              -- Tenant context (for now)
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30),
    specialties JSONB,
    is_active BOOLEAN DEFAULT TRUE
);

-- Tenant schema (future schema-per-tenant)
CREATE TABLE staff (
    id UUID PRIMARY KEY,
    keycloak_user_id VARCHAR(255),      -- Links to Keycloak user
    -- No tenant_id needed (implied by schema)
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30),
    specialties JSONB,
    is_active BOOLEAN DEFAULT TRUE
);
```

#### Advantages
1. **Clean Separation of Concerns**
   - Authentication/authorization separate from business logic
   - User-tenant mapping independent of staff details

2. **Schema-per-Tenant Compatibility**
   - `user_tenant_access` remains in public schema
   - Staff data migrates to tenant schemas without duplication
   - No authentication data replicated across schemas

3. **Multi-Tenant Access Support**
   - One Keycloak user can access multiple tenants
   - Different roles per tenant for the same user
   - Easy to track and audit access permissions

4. **Scalability**
   - Centralized access control
   - Efficient queries for "which tenants can this user access?"
   - Simple permission revocation across all tenants

#### Disadvantages
- Additional JOIN required when combining staff and access data
- Slightly more complex data model
- Need to maintain consistency between tables (but NO foreign keys)

### Option 2: Integrated staff Table (Current Approach)

#### Advantages
- Simpler queries (no JOINs)
- Single table to maintain
- Straightforward implementation

#### Disadvantages
1. **Schema-per-Tenant Incompatibility**
   - Staff records duplicated across schemas
   - Keycloak user IDs stored in multiple schemas
   - Complex synchronization requirements

2. **Limited Multi-Tenant Access**
   - One person needs multiple staff records
   - Difficult to manage cross-tenant permissions
   - No clear audit trail for access changes

3. **Mixed Responsibilities**
   - Authentication data mixed with business data
   - Violates single responsibility principle

## Decision Rationale

### Why Separate Tables Are Better for Your Architecture

1. **Future Schema-per-Tenant Migration**
   - Your documented plan in `02-schema-per-tenant-implementation.md` requires clean separation
   - The `SchemaBasedMultiTenantConnectionProvider` expects tenant resolution at connection level
   - Access control must remain centralized while business data is distributed

2. **Current Multi-Tenant Patterns**
   - Using realm-per-type strategy (shared realms by specialty)
   - Users may work across multiple clinics
   - Need centralized access management

3. **Alignment with Keycloak Architecture**
   - Keycloak manages authentication (who is the user)
   - `user_tenant_access` manages authorization (which tenants can they access)
   - `staff` manages business data (their role and details within each tenant)
   - Linked by Keycloak user ID, not database foreign keys

## Implementation Strategy

### Phase 1: Reintroduce user_tenant_access
```sql
-- Already exists from V8 migration
CREATE TABLE user_tenant_access (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, tenant_id),
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE
);
```

### Phase 2: Refactor Staff Table
```sql
-- Remove authentication fields from staff
ALTER TABLE staff 
DROP COLUMN IF EXISTS user_id,      -- Remove old user_id
DROP COLUMN IF EXISTS is_primary;    -- This belongs in user_tenant_access

-- Add Keycloak user identifier
ALTER TABLE staff 
ADD COLUMN keycloak_user_id VARCHAR(255);

-- Keep tenant_id for now (will be removed in schema-per-tenant)
-- Do NOT add foreign key to user_tenant_access
```

### Phase 3: Update Application Layer

**Important**: Use loose coupling between `staff` and `user_tenant_access` tables through Keycloak user ID, NOT direct foreign key relationships.

```java
@Entity
@Table(name = "staff")
public class Staff extends BaseEntity {
    // Business fields only
    private String fullName;
    private StaffRole role;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    
    // Link to Keycloak user (NOT to user_tenant_access)
    @Column(name = "keycloak_user_id")
    private String keycloakUserId;  // Links to the actual person
    
    // In shared schema mode, include tenant_id
    @Column(name = "tenant_id")
    private String tenantId;  // Will be removed in schema-per-tenant
    
    // NO direct @ManyToOne relationship to user_tenant_access
}

@Entity
@Table(name = "user_tenant_access", schema = "public")
public class UserTenantAccess {
    @Id
    private UUID id;
    private String userId;  // Keycloak ID
    private String tenantId;
    private String role;
    private boolean isPrimary;
    private boolean isActive;
    
    // NO relationship annotations - this is a pure mapping table
}
```

## Migration Path to Schema-per-Tenant

### Current State (Shared Schema)
```
public schema:
  - tenants
  - user_tenant_access
  - staff (with tenant_id for filtering)
  - patients (with tenant_id for filtering)
```

### Target State (Schema-per-Tenant)
```
public schema:
  - tenants
  - user_tenant_access (central access control)

tenant_abc schema:
  - staff (tenant-specific data)
  - patients
  - appointments
  - treatments

tenant_xyz schema:
  - staff (tenant-specific data)
  - patients
  - appointments
  - treatments
```

## Service Layer Implementation

### Correct Pattern: Loose Coupling
```java
@Service
public class StaffService {
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private UserTenantAccessRepository accessRepository;
    
    public StaffDto getStaffWithAccess(String staffId) {
        // Get staff from current tenant context
        Staff staff = staffRepository.findById(staffId)
            .orElseThrow(() -> new NotFoundException("Staff not found"));
        
        // Get access info from public schema
        UserTenantAccess access = accessRepository
            .findByUserIdAndTenantId(
                staff.getKeycloakUserId(), 
                TenantContext.getCurrentTenant()
            )
            .orElseThrow(() -> new UnauthorizedException("No access"));
        
        // Combine for DTO
        return StaffDto.builder()
            .id(staff.getId())
            .fullName(staff.getFullName())
            .email(staff.getEmail())
            .role(staff.getRole())
            .accessRole(access.getRole())      // From user_tenant_access
            .isPrimary(access.isPrimary())      // From user_tenant_access
            .build();
    }
    
    public Staff createStaff(CreateStaffRequest request, String keycloakUserId) {
        // Verify access exists
        UserTenantAccess access = accessRepository
            .findByUserIdAndTenantId(keycloakUserId, TenantContext.getCurrentTenant())
            .orElseThrow(() -> new UnauthorizedException("No tenant access"));
        
        // Create staff with Keycloak link
        Staff staff = new Staff();
        staff.setKeycloakUserId(keycloakUserId);
        staff.setTenantId(TenantContext.getCurrentTenant()); // In shared schema mode
        staff.setFullName(request.getFullName());
        staff.setEmail(request.getEmail());
        staff.setRole(request.getRole());
        
        return staffRepository.save(staff);
    }
}
```

## Benefits for Common Use Cases

### Use Case 1: Doctor Working at Multiple Clinics
```sql
-- Find all clinics Dr. Smith can access
SELECT t.name, t.subdomain, uta.role, uta.is_primary
FROM user_tenant_access uta
JOIN tenants t ON uta.tenant_id = t.tenant_id
WHERE uta.user_id = 'keycloak-user-123'
  AND uta.is_active = true;

-- Get staff details for each clinic
SELECT s.*, t.name as clinic_name
FROM staff s
JOIN tenants t ON s.tenant_id = t.tenant_id
WHERE s.keycloak_user_id = 'keycloak-user-123';
```

### Use Case 2: Revoke Access
```sql
-- Disable access without deleting staff history
UPDATE user_tenant_access 
SET is_active = false 
WHERE user_id = 'keycloak-user-123' 
  AND tenant_id = 'tenant-abc';

-- Staff record remains for historical data
-- But user can no longer authenticate to this tenant
```

### Use Case 3: Audit Trail
```sql
-- Track access history
SELECT 
    uta.created_at as access_granted,
    uta.updated_at as last_modified,
    uta.is_active,
    uta.role,
    t.name as tenant_name,
    s.full_name as staff_name
FROM user_tenant_access uta
JOIN tenants t ON uta.tenant_id = t.tenant_id
LEFT JOIN staff s ON s.keycloak_user_id = uta.user_id 
                  AND s.tenant_id = uta.tenant_id
WHERE uta.user_id = 'keycloak-user-123'
ORDER BY uta.created_at DESC;
```

### Use Case 4: Migration to Schema-per-Tenant
```java
// Before migration (shared schema)
String query = "SELECT * FROM staff WHERE tenant_id = :tenantId AND keycloak_user_id = :userId";

// After migration (schema-per-tenant)
// Connection automatically uses correct schema via TenantContext
String query = "SELECT * FROM staff WHERE keycloak_user_id = :userId";
// No tenant_id needed - isolation by schema!
```

## Key Architecture Principle

**DO NOT use foreign keys between `staff` and `user_tenant_access`**. Instead, use loose coupling through Keycloak user IDs:

- `user_tenant_access.user_id` → Keycloak user identifier
- `staff.keycloak_user_id` → Same Keycloak user identifier
- Join them in queries when needed, but maintain independence

This approach ensures:
1. Clean separation between access control and business data
2. Easy migration to schema-per-tenant
3. No tight coupling between authentication and business logic
4. Flexibility for users to work across multiple tenants

## Conclusion

The separate `user_tenant_access` table approach with **loose coupling** provides:
- ✅ Clean architecture alignment
- ✅ Schema-per-tenant compatibility
- ✅ Multi-tenant access support
- ✅ Centralized access control
- ✅ Clear audit capabilities
- ✅ Future scalability
- ✅ No tight coupling between tables

This design aligns perfectly with your documented schema-per-tenant migration strategy and provides the flexibility needed for a multi-tenant SaaS platform.

## Next Steps

1. **Immediate**: Keep the `user_tenant_access` table from V8 migration
2. **Short-term**: Refactor `Staff` entity to use relationship instead of embedded fields
3. **Medium-term**: Update services to work with the separated model
4. **Long-term**: Implement schema-per-tenant with this foundation