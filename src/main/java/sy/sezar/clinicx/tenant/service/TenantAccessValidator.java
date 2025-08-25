package sy.sezar.clinicx.tenant.service;

/**
 * Service for validating user access to tenants.
 */
public interface TenantAccessValidator {
    
    /**
     * Validate that the current user has access to the specified tenant.
     * 
     * @param tenantId the tenant ID to validate
     * @return true if user has access, false otherwise
     */
    boolean validateAccess(String tenantId);
    
    /**
     * Validate that the current user has access to the current tenant context.
     * 
     * @return true if user has access, false otherwise
     */
    boolean validateCurrentTenantAccess();
    
    /**
     * Validate that a specific user has access to a tenant.
     * 
     * @param userId the user ID
     * @param tenantId the tenant ID
     * @return true if user has access, false otherwise
     */
    boolean validateUserAccess(String userId, String tenantId);
    
    /**
     * Validate that the current user has a specific role in the tenant.
     * 
     * @param tenantId the tenant ID
     * @param role the required role
     * @return true if user has the role, false otherwise
     */
    boolean validateRole(String tenantId, String role);
    
    /**
     * Get the current user's role in a tenant.
     * 
     * @param tenantId the tenant ID
     * @return the user's role, or null if no access
     */
    String getUserRoleInTenant(String tenantId);
}