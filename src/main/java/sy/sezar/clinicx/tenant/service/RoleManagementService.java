package sy.sezar.clinicx.tenant.service;

import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.List;
import java.util.Set;

/**
 * Service for managing role-related operations.
 * Handles role validation, conversion, and hierarchy management.
 */
public interface RoleManagementService {
    
    /**
     * Validates if a user can assign the given roles.
     */
    void validateRoleAssignment(Set<StaffRole> currentUserRoles, Set<StaffRole> rolesToAssign);
    
    /**
     * Checks if a user has administrative privileges.
     */
    boolean isAdministrator(Set<StaffRole> roles);
    
    /**
     * Checks if a user has super admin privileges.
     */
    boolean isSuperAdmin(Set<StaffRole> roles);
    
    /**
     * Checks if a user has clinical privileges.
     */
    boolean isClinical(Set<StaffRole> roles);
    
    /**
     * Gets the highest role from a set of roles.
     */
    StaffRole getHighestRole(Set<StaffRole> roles);
    
    /**
     * Converts role names to StaffRole enums with validation.
     */
    Set<StaffRole> parseRoles(List<String> roleNames);
    
    /**
     * Converts StaffRole enums to string names.
     */
    List<String> rolesToStrings(Set<StaffRole> roles);
    
    /**
     * Validates a single role name.
     */
    boolean isValidRole(String roleName);
    
    /**
     * Gets the default role for new users.
     */
    StaffRole getDefaultRole();
    
    /**
     * Checks if one role has authority over another.
     */
    boolean hasAuthorityOver(StaffRole role1, StaffRole role2);
    
    /**
     * Merges two sets of roles, keeping the highest privileges.
     */
    Set<StaffRole> mergeRoles(Set<StaffRole> roles1, Set<StaffRole> roles2);
    
    /**
     * Filters roles based on user's authority level.
     */
    Set<StaffRole> filterAssignableRoles(Set<StaffRole> userRoles, Set<StaffRole> requestedRoles);
    
    /**
     * Gets all roles that a user with given roles can assign.
     */
    Set<StaffRole> getAssignableRoles(Set<StaffRole> userRoles);
}