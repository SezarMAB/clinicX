package sy.sezar.clinicx.tenant.service;

import sy.sezar.clinicx.tenant.dto.TenantAccessDto;
import sy.sezar.clinicx.tenant.dto.TenantSwitchResponseDto;

import java.util.List;

/**
 * Service for handling tenant switching operations in multi-tenant scenarios.
 */
public interface TenantSwitchingService {
    
    /**
     * Get all tenants accessible by the current user.
     * 
     * @return List of accessible tenants
     */
    List<TenantAccessDto> getCurrentUserTenants();
    
    /**
     * Switch the active tenant for the current user.
     * 
     * @param tenantId the tenant ID to switch to
     * @return Response containing new tokens and tenant info
     */
    TenantSwitchResponseDto switchTenant(String tenantId);
    
    /**
     * Get the current active tenant for the user.
     * 
     * @return The active tenant access info
     */
    TenantAccessDto getCurrentActiveTenant();
    
    /**
     * Grant a user access to a tenant.
     * 
     * @param userId the Keycloak user ID
     * @param tenantId the tenant ID
     * @param role the role in this tenant
     * @param isPrimary whether this is the primary tenant
     */
    void grantUserTenantAccess(String userId, String tenantId, String role, boolean isPrimary);
    
    /**
     * Revoke a user's access to a tenant.
     * 
     * @param userId the user ID
     * @param tenantId the tenant ID
     */
    void revokeUserTenantAccess(String userId, String tenantId);
    
    /**
     * Get all tenants accessible by a specific user.
     * 
     * @param userId the user ID
     * @return List of accessible tenants
     */
    List<TenantAccessDto> getUserTenants(String userId);
    
    /**
     * Sync user's accessible tenants from backend to Keycloak.
     * This ensures Keycloak user attributes match the backend Staff records.
     * 
     * @param userId the user ID
     * @param realmName the Keycloak realm name
     * @param username the username in Keycloak
     */
    void syncUserTenantsToKeycloak(String userId, String realmName, String username);
}