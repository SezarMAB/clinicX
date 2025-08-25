package sy.sezar.clinicx.tenant.service;

import java.util.List;
import java.util.Set;

/**
 * Service for centralized tenant security operations.
 * Provides high-level security functions that combine authentication and tenant access.
 */
public interface TenantSecurityService {
    
    /**
     * Check if the current user can perform an action in the current tenant.
     * 
     * @param action the action to check (e.g., "CREATE_PATIENT", "VIEW_APPOINTMENTS")
     * @return true if the action is allowed
     */
    boolean canPerformAction(String action);
    
    /**
     * Check if the current user can perform an action in a specific tenant.
     * 
     * @param tenantId the tenant ID
     * @param action the action to check
     * @return true if the action is allowed
     */
    boolean canPerformActionInTenant(String tenantId, String action);
    
    /**
     * Get all permissions for the current user in the current tenant.
     * 
     * @return set of permission strings
     */
    Set<String> getCurrentTenantPermissions();
    
    /**
     * Get all permissions for the current user in a specific tenant.
     * 
     * @param tenantId the tenant ID
     * @return set of permission strings
     */
    Set<String> getTenantPermissions(String tenantId);
    
    /**
     * Check if the current user is a tenant admin.
     * 
     * @return true if user is admin in current tenant
     */
    boolean isTenantAdmin();
    
    /**
     * Check if the current user is a tenant admin in a specific tenant.
     * 
     * @param tenantId the tenant ID
     * @return true if user is admin in the tenant
     */
    boolean isTenantAdmin(String tenantId);
    
    /**
     * Get all tenants the current user has access to.
     * 
     * @return list of tenant IDs
     */
    List<String> getAccessibleTenants();
    
    /**
     * Check if the current user can access a resource owned by another user in the same tenant.
     * 
     * @param resourceOwnerId the ID of the resource owner
     * @param resourceType the type of resource (e.g., "PATIENT_RECORD", "APPOINTMENT")
     * @return true if access is allowed
     */
    boolean canAccessUserResource(String resourceOwnerId, String resourceType);
    
    /**
     * Validate that a user can be assigned a role in a tenant.
     * 
     * @param userId the user ID
     * @param tenantId the tenant ID
     * @param role the role to assign
     * @return true if the assignment is valid
     */
    boolean canAssignRole(String userId, String tenantId, String role);
    
    /**
     * Get the current user's primary tenant.
     * 
     * @return the primary tenant ID
     */
    String getPrimaryTenant();
    
    /**
     * Check if the current request has valid tenant context.
     * 
     * @return true if tenant context is valid
     */
    boolean hasValidTenantContext();
    
    /**
     * Enforce that the current user must have access to the current tenant.
     * 
     * @throws org.springframework.security.access.AccessDeniedException if access is denied
     */
    void enforceTenantAccess();
    
    /**
     * Enforce that the current user must have a specific role in the current tenant.
     * 
     * @param role the required role
     * @throws org.springframework.security.access.AccessDeniedException if access is denied
     */
    void enforceTenantRole(String role);
}