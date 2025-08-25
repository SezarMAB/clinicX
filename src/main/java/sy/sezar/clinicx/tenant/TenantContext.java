package sy.sezar.clinicx.tenant;

/**
 * Thread-local storage for the current tenant context.
 * This will be used for multi-tenant data isolation.
 */
public class TenantContext {
    
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    /**
     * Set the current tenant ID for this thread.
     * 
     * @param tenantId the tenant identifier
     */
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }
    
    /**
     * Get the current tenant ID for this thread.
     * 
     * @return the tenant identifier, or null if not set
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }
    
    /**
     * Clear the tenant context for this thread.
     * Should be called after request processing is complete.
     */
    public static void clear() {
        currentTenant.remove();
    }
    
    /**
     * Check if a tenant context is set for this thread.
     * 
     * @return true if tenant is set, false otherwise
     */
    public static boolean hasTenant() {
        return currentTenant.get() != null;
    }
}