package sy.sezar.clinicx.tenant.service;

/**
 * Service for auditing tenant access attempts and operations.
 */
public interface TenantAuditService {
    
    /**
     * Audit a successful tenant access.
     * 
     * @param username the username
     * @param tenantId the tenant ID
     * @param resource the accessed resource
     */
    void auditAccessGranted(String username, String tenantId, String resource);
    
    /**
     * Audit a denied tenant access attempt.
     * 
     * @param username the username
     * @param tenantId the tenant ID
     * @param resource the attempted resource
     * @param reason the denial reason
     */
    void auditAccessDenied(String username, String tenantId, String resource, String reason);
    
    /**
     * Audit a tenant switch operation.
     * 
     * @param username the username
     * @param fromTenantId the previous tenant ID
     * @param toTenantId the new tenant ID
     */
    void auditTenantSwitch(String username, String fromTenantId, String toTenantId);
    
    /**
     * Audit a tenant creation.
     * 
     * @param username the username who created the tenant
     * @param tenantId the created tenant ID
     * @param tenantName the tenant name
     */
    void auditTenantCreated(String username, String tenantId, String tenantName);
    
    /**
     * Audit a tenant modification.
     * 
     * @param username the username who modified the tenant
     * @param tenantId the tenant ID
     * @param changes description of changes
     */
    void auditTenantModified(String username, String tenantId, String changes);
    
    /**
     * Audit user added to tenant.
     * 
     * @param adminUsername the admin who added the user
     * @param userId the user ID
     * @param tenantId the tenant ID
     * @param role the assigned role
     */
    void auditUserAddedToTenant(String adminUsername, String userId, String tenantId, String role);
    
    /**
     * Audit user removed from tenant.
     * 
     * @param adminUsername the admin who removed the user
     * @param userId the user ID
     * @param tenantId the tenant ID
     */
    void auditUserRemovedFromTenant(String adminUsername, String userId, String tenantId);
}