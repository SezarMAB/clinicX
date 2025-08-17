package sy.sezar.clinicx.tenant.service;

import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for managing Staff entity operations.
 * Handles all Staff-related CRUD operations and business logic.
 */
public interface StaffManagementService {
    
    /**
     * Finds all staff members for a given tenant.
     */
    List<Staff> findByTenantId(String tenantId);
    
    /**
     * Finds a staff member by Keycloak user ID and tenant ID.
     */
    Optional<Staff> findByKeycloakUserIdAndTenantId(String keycloakUserId, String tenantId);
    
    /**
     * Finds all staff records for a given Keycloak user across all tenants.
     */
    List<Staff> findByKeycloakUserId(String keycloakUserId);
    
    /**
     * Checks if a staff member exists with the given email (case-insensitive).
     */
    boolean existsByEmail(String email);
    
    /**
     * Checks if a staff member exists with the given Keycloak user ID and tenant ID.
     */
    boolean existsByKeycloakUserIdAndTenantId(String keycloakUserId, String tenantId);
    
    /**
     * Creates a new staff member.
     */
    Staff createStaff(String tenantId, String keycloakUserId, String fullName, 
                     String email, String phoneNumber, Set<StaffRole> roles);
    
    /**
     * Updates a staff member's information.
     */
    Staff updateStaff(Staff staff);
    
    /**
     * Updates a staff member's phone number.
     */
    int updatePhoneNumber(String keycloakUserId, String tenantId, String phoneNumber);
    
    /**
     * Updates a staff member's roles.
     */
    Staff updateStaffRoles(String keycloakUserId, String tenantId, Set<StaffRole> roles);
    
    /**
     * Deactivates a staff member (soft delete).
     */
    Staff deactivateStaff(String keycloakUserId, String tenantId);
    
    /**
     * Activates a previously deactivated staff member.
     */
    Staff activateStaff(String keycloakUserId, String tenantId);
    
    /**
     * Deactivates all staff records for a given Keycloak user across all tenants.
     */
    void deactivateAllStaffForUser(String keycloakUserId);
    
    /**
     * Gets all tenant IDs where a user has staff records.
     */
    List<String> getTenantIdsForUser(String keycloakUserId);
    
    /**
     * Validates if a user can be deactivated (e.g., not an admin).
     */
    void validateDeactivation(Staff staff);
    
    /**
     * Creates or reactivates a staff member for an external user.
     */
    Staff createOrReactivateExternalStaff(String tenantId, String keycloakUserId, 
                                         String fullName, String email, 
                                         String phoneNumber, Set<StaffRole> roles);
}