package sy.sezar.clinicx.tenant.service;

import org.keycloak.representations.idm.UserRepresentation;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.tenant.dto.TenantUserDto;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;

import java.util.List;
import java.util.Set;

/**
 * Service for mapping between entities and DTOs.
 * Handles complex mapping logic for user data transformation.
 */
public interface UserMappingService {
    
    /**
     * Maps Keycloak user to TenantUserDto with tenant context.
     */
    TenantUserDto mapToDto(UserRepresentation keycloakUser, String tenantId, Staff staff);
    
    /**
     * Maps Keycloak user to TenantUserDto with user access context.
     */
    TenantUserDto mapToDto(UserRepresentation keycloakUser, String tenantId, UserTenantAccess access);
    
    /**
     * Maps Keycloak user to TenantUserDto with both Staff and UserTenantAccess.
     */
    TenantUserDto mapToDto(UserRepresentation keycloakUser, String tenantId, 
                          Staff staff, UserTenantAccess access);
    
    /**
     * Creates a basic TenantUserDto from Keycloak user.
     */
    TenantUserDto createBasicDto(UserRepresentation keycloakUser, String tenantId);
    
    /**
     * Builds accessible tenants list for a user.
     */
    List<TenantUserDto.TenantAccessInfo> buildAccessibleTenants(String userId);
    
    /**
     * Determines the user type based on attributes and roles.
     */
    StaffRole determineUserType(UserRepresentation keycloakUser, Set<StaffRole> roles);
    
    /**
     * Extracts the primary role from a set of roles.
     */
    String getPrimaryRoleName(Set<StaffRole> roles);
    
    /**
     * Maps role strings to StaffRole enums.
     */
    Set<StaffRole> mapToStaffRoles(List<String> roleNames);
    
    /**
     * Converts StaffRole set to string list.
     */
    List<String> rolesToStringList(Set<StaffRole> roles);
    
    /**
     * Enhances UserRepresentation with Staff data.
     */
    void enhanceWithStaffData(UserRepresentation user, Staff staff);
    
    /**
     * Updates user attributes with tenant information.
     */
    void updateUserAttributes(UserRepresentation user, String tenantId, List<String> roles);
}