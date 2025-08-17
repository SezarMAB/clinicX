package sy.sezar.clinicx.tenant.service;

import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for managing UserTenantAccess entity operations.
 * Handles user access control across multiple tenants.
 */
public interface UserAccessManagementService {
    
    /**
     * Finds user access by user ID and tenant ID.
     */
    Optional<UserTenantAccess> findByUserIdAndTenantId(String userId, String tenantId);
    
    /**
     * Gets user access, throwing exception if not found.
     */
    UserTenantAccess getAccess(String userId, String tenantId);
    
    /**
     * Finds all access records for a user across all tenants.
     */
    List<UserTenantAccess> findByUserId(String userId);
    
    /**
     * Finds all active access records for a user.
     */
    List<UserTenantAccess> findActiveAccessByUserId(String userId);
    
    /**
     * Counts active tenant access for a user.
     */
    long countActiveAccessForUser(String userId);
    
    /**
     * Creates a new user access record.
     */
    UserTenantAccess createAccess(String userId, String tenantId, Set<StaffRole> roles, boolean isPrimary);
    
    /**
     * Updates user roles for a tenant.
     */
    UserTenantAccess updateRoles(String userId, String tenantId, Set<StaffRole> roles);
    
    /**
     * Deactivates user access to a tenant.
     */
    UserTenantAccess deactivateAccess(String userId, String tenantId);
    
    /**
     * Activates user access to a tenant.
     */
    UserTenantAccess activateAccess(String userId, String tenantId);
    
    /**
     * Deactivates all access records for a user across all tenants.
     */
    void deactivateAllAccessForUser(String userId);
    
    /**
     * Creates or reactivates access for an external user.
     */
    UserTenantAccess createOrReactivateAccess(String userId, String tenantId, 
                                             Set<StaffRole> roles, boolean isPrimary);
    
    /**
     * Checks if user has active access to a tenant.
     */
    boolean hasActiveAccess(String userId, String tenantId);
    
    /**
     * Validates if access can be revoked (e.g., not primary tenant).
     */
    void validateRevocation(UserTenantAccess access);
    
    /**
     * Gets the primary tenant for a user.
     */
    Optional<UserTenantAccess> getPrimaryAccessForUser(String userId);
    
    /**
     * Updates the primary tenant for a user.
     */
    void updatePrimaryTenant(String userId, String newPrimaryTenantId);
}