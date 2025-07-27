package sy.sezar.clinicx.core.tenant;

/**
 * Interface for resolving the current tenant.
 * Allows for different implementations based on the deployment mode.
 */
public interface TenantResolver {
    
    /**
     * Resolve the current tenant identifier.
     * 
     * @return the tenant ID, or a default value if not available
     */
    String resolveTenant();
    
    /**
     * Check if multi-tenancy is enabled.
     * 
     * @return true if multi-tenant mode is active
     */
    default boolean isMultiTenant() {
        return true;
    }
}