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
}