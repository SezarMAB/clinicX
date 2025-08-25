# Multi-Role Architecture Transformation

## Overview

This document outlines the comprehensive transformation of the ClinicX system from single-role to multi-role architecture for both Staff and UserTenantAccess entities. This transformation is a critical foundation for implementing tenant-aware security and preventing role leakage between tenants.

## Business Requirements

### Problem Statement
- **Role Leakage Risk**: Users with ADMIN role in one tenant were getting admin access in all tenants
- **Limited Role Flexibility**: Staff and users could only have one role per tenant
- **Security Vulnerability**: JWT tokens containing all realm-level roles were being used for authorization across all tenants

### Solution Goals
1. Enable multiple roles per staff member per tenant
2. Enable multiple roles per user per tenant
3. Prepare foundation for tenant-aware role checking
4. Maintain data integrity during transformation
5. Preserve backward compatibility where possible

## Technical Implementation

### Phase 1: Staff Entity Transformation

#### 1.1 Entity Layer Changes
**File**: `src/main/java/sy/sezar/clinicx/clinic/model/Staff.java`

**Before**:
```java
@Column(name = "role", nullable = false, length = 50)
@Enumerated(EnumType.STRING)
private StaffRole role;
```

**After**:
```java
@ElementCollection(targetClass = StaffRole.class, fetch = FetchType.LAZY)
@CollectionTable(name = "staff_roles", joinColumns = @JoinColumn(name = "staff_id"))
@Column(name = "role", nullable = false, length = 50)
@Enumerated(EnumType.STRING)
private Set<StaffRole> roles = new HashSet<>();
```

#### 1.2 DTO Layer Updates

**Files Updated**:
- `StaffDto.java`: Changed `StaffRole role` → `Set<StaffRole> roles`
- `StaffWithAccessDto.java`: Updated role field
- `StaffCreateRequest.java`: Added validation for role collection
- `StaffUpdateRequest.java`: Updated to handle multiple roles

**Key Changes**:
```java
// Before
StaffRole role;

// After
@NotNull(message = "Roles are required")
@Size(min = 1, message = "At least one role is required")
Set<StaffRole> roles;
```

#### 1.3 Mapper Enhancements
**File**: `src/main/java/sy/sezar/clinicx/clinic/mapper/StaffMapper.java`

**Features Added**:
- Safe collection copying with null handling
- Priority-based role selection for backward compatibility
- Custom mapping methods for roles initialization
- Collection integrity maintenance

**Priority Logic**:
```java
// Priority order for single role selection
SUPER_ADMIN > ADMIN > DOCTOR > NURSE > RECEPTIONIST > ACCOUNTANT > ASSISTANT
```

#### 1.4 Repository Updates
**File**: `src/main/java/sy/sezar/clinicx/clinic/repository/StaffRepository.java`

**Query Updates**:
```java
// Before
@Query("SELECT s FROM Staff s WHERE s.role = :role")
Page<Staff> findByRole(@Param("role") StaffRole role, Pageable pageable);

// After
@Query("SELECT DISTINCT s FROM Staff s JOIN s.roles r WHERE r = :role")
Page<Staff> findByRole(@Param("role") StaffRole role, Pageable pageable);
```

**New Methods Added**:
- `findByRolesIn()`: Find staff with any of the specified roles
- `findAllWithRoles()`: Find all staff that have at least one role

#### 1.5 Database Migration
**File**: `src/main/resources/db/migration/V18__migrate_staff_role_to_roles_collection.sql`

**Migration Steps**:
1. **Create Collection Table**:
   ```sql
   CREATE TABLE staff_roles (
       staff_id UUID NOT NULL,
       role VARCHAR(50) NOT NULL,
       PRIMARY KEY (staff_id, role),
       CONSTRAINT fk_staff_roles_staff_id 
           FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE
   );
   ```

2. **Data Migration**:
   - Migrated 26 existing staff role records
   - Preserved all existing role assignments
   - Verified data integrity before committing

3. **Schema Cleanup**:
   - Dropped old `role` column from `staff` table
   - Updated `v_staff_with_access` view to work without role column dependency

### Phase 2: UserTenantAccess Entity Transformation

#### 2.1 Entity Layer Changes
**File**: `src/main/java/sy/sezar/clinicx/tenant/model/UserTenantAccess.java`

**Before**:
```java
@Column(name = "role", nullable = false, length = 50)
private String role;
```

**After**:
```java
@ElementCollection(targetClass = StaffRole.class, fetch = FetchType.LAZY)
@CollectionTable(name = "user_tenant_access_roles", joinColumns = @JoinColumn(name = "user_tenant_access_id"))
@Column(name = "role", nullable = false, length = 50)
@Enumerated(EnumType.STRING)
@Builder.Default
private Set<StaffRole> roles = new HashSet<>();
```

#### 2.2 DTO Layer Updates

**Files Updated**:
- `UserTenantAccessDto.java`: Converted to record, updated to use `Set<StaffRole> roles`
- `CreateUserTenantAccessRequest.java`: Added role collection validation
- `UpdateUserTenantAccessRequest.java`: Updated for roles collection

**Record Conversion Example**:
```java
// After - Modern record implementation
public record UserTenantAccessDto(
    UUID id,
    String userId,
    String tenantId,
    Set<StaffRole> roles,  // Changed from String role
    boolean isPrimary,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {}
```

#### 2.3 Mapper Transformation
**File**: `src/main/java/sy/sezar/clinicx/tenant/mapper/UserTenantAccessMapper.java`

**Complete Rewrite Features**:
- Null-safe collection operations
- Custom role collection copying methods
- Priority-based single role selection utility
- Comprehensive initialization methods

#### 2.4 Repository Updates
**File**: `src/main/java/sy/sezar/clinicx/tenant/repository/UserTenantAccessRepository.java`

**Updated Queries**:
```java
// Before
@Query("SELECT uta FROM UserTenantAccess uta WHERE uta.tenantId = :tenantId AND uta.role = :role AND uta.isActive = true")
List<UserTenantAccess> findByTenantIdAndRole(@Param("tenantId") String tenantId, @Param("role") String role);

// After
@Query("SELECT DISTINCT uta FROM UserTenantAccess uta JOIN uta.roles r WHERE uta.tenantId = :tenantId AND r = :role AND uta.isActive = true")
List<UserTenantAccess> findByTenantIdAndRole(@Param("tenantId") String tenantId, @Param("role") StaffRole role);
```

**New Methods Added**:
- `findByTenantIdAndRolesIn()`: Multi-role queries
- `findByUserIdWithRoles()`: Users with any roles

#### 2.5 Service Layer Updates

**Files Updated**:
- `UserTenantAccessService.java`: Interface updated for role collections
- `UserTenantAccessServiceImpl.java`: All methods updated
- `TenantUserServiceImpl.java`: Updated user access operations
- `StaffServiceImpl.java`: Updated access information handling
- `StaffKeycloakSyncServiceImpl.java`: Fixed references to new roles field

#### 2.6 Database Migration
**File**: `src/main/resources/db/migration/V19__migrate_user_tenant_access_role_to_roles_collection.sql`

**Migration Steps**:
1. **Create Collection Table**:
   ```sql
   CREATE TABLE user_tenant_access_roles (
       user_tenant_access_id UUID NOT NULL,
       role VARCHAR(50) NOT NULL,
       PRIMARY KEY (user_tenant_access_id, role),
       CONSTRAINT fk_user_tenant_access_roles_user_tenant_access_id 
           FOREIGN KEY (user_tenant_access_id) 
           REFERENCES user_tenant_access(id) ON DELETE CASCADE
   );
   ```

2. **Data Migration**: Preserve all existing role assignments
3. **Schema Updates**: Drop old role column, update views

## Impact Analysis

### Database Changes
- **New Tables**: `staff_roles`, `user_tenant_access_roles`
- **Dropped Columns**: `staff.role`, `user_tenant_access.role`
- **Updated Views**: `v_staff_with_access`
- **Migration Status**: V18 (Staff), V19 (UserTenantAccess)

### API Changes
- **Breaking Changes**: Role fields now return collections
- **Backward Compatibility**: Mappers provide single role selection where needed
- **New Endpoints**: Support for multi-role operations

### Performance Considerations
- **Query Optimization**: Added indexes on collection join columns
- **Lazy Loading**: Collections loaded only when needed
- **Memory Impact**: Minimal due to small role collections per entity

## Security Implications

### Before Transformation
- ❌ Single role per entity limited flexibility
- ❌ Role leakage risk across tenants
- ❌ JWT realm-level roles used globally

### After Transformation
- ✅ Multiple roles per entity enable fine-grained permissions
- ✅ Foundation for tenant-aware role checking
- ✅ Proper data structure for role isolation

## Testing Strategy

### Unit Tests
- Collection handling in mappers
- Repository query functionality
- Service layer role operations
- Migration data integrity

### Integration Tests
- Multi-role assignment workflows
- Role-based access control
- Cross-tenant role isolation
- API endpoint functionality

### Migration Testing
- Data preservation verification
- Rollback procedures
- Performance impact assessment
- View functionality validation

## Rollback Procedures

### Staff Migration Rollback
**File**: `V18_ROLLBACK__restore_staff_single_role.sql`
- Restores single role column
- Implements priority-based role selection for data loss mitigation
- Warns about potential data loss for multi-role staff

### UserTenantAccess Migration Rollback
**File**: `V19_ROLLBACK__restore_user_tenant_access_single_role.sql`
- Restores single role column
- Preserves primary role per user-tenant relationship
- Complete rollback within transaction boundaries

## Future Enhancements

### Next Steps
1. **TenantAuthorizationService**: Implement secure role checking
2. **Security Annotations**: Create tenant-aware authorization
3. **Role Hierarchy**: Implement role inheritance and delegation
4. **Audit Logging**: Track role changes and access attempts

### Long-term Goals
- Dynamic role management
- Role-based UI customization
- Advanced permission matrices
- Cross-tenant role sharing (controlled)

## Monitoring and Maintenance

### Key Metrics
- Role assignment distribution
- Multi-role usage patterns
- Query performance impact
- Security event frequency

### Maintenance Tasks
- Regular role assignment audits
- Performance monitoring
- Security vulnerability assessments
- Documentation updates

## Conclusion

The multi-role transformation successfully modernizes the ClinicX role system, providing:

1. **Enhanced Flexibility**: Multiple roles per entity
2. **Security Foundation**: Proper structure for tenant-aware authorization
3. **Data Integrity**: Zero data loss during migration
4. **Performance Optimization**: Efficient collection-based queries
5. **Future Readiness**: Foundation for advanced security features

This transformation is a critical step toward implementing comprehensive tenant isolation and preventing role leakage vulnerabilities in the multi-tenant architecture.

---

**Document Version**: 1.0  
**Last Updated**: January 15, 2025  
**Migration Status**: V18 (Staff) ✅, V19 (UserTenantAccess) ✅  
**Next Phase**: Tenant-Aware Security Implementation