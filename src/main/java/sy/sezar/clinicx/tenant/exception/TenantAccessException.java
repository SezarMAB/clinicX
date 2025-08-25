package sy.sezar.clinicx.tenant.exception;

/**
 * Exception thrown when a user attempts to access a tenant they don't have permission for.
 */
public class TenantAccessException extends RuntimeException {
    
    public TenantAccessException(String message) {
        super(message);
    }
    
    public TenantAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static TenantAccessException noAccess(String userId, String tenantId) {
        return new TenantAccessException(
            String.format("User %s does not have access to tenant %s", userId, tenantId)
        );
    }
    
    public static TenantAccessException inactiveAccess(String userId, String tenantId) {
        return new TenantAccessException(
            String.format("User %s has inactive access to tenant %s", userId, tenantId)
        );
    }
}