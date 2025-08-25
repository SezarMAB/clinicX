package sy.sezar.clinicx.tenant.exception;

/**
 * Exception thrown when user management operations fail.
 */
public class UserManagementException extends RuntimeException {
    
    public UserManagementException(String message) {
        super(message);
    }
    
    public UserManagementException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static UserManagementException userAlreadyExists(String email) {
        return new UserManagementException(
            String.format("User with email %s already exists", email)
        );
    }
    
    public static UserManagementException cannotDeactivateAdmin(String userId) {
        return new UserManagementException(
            String.format("Cannot deactivate admin user %s", userId)
        );
    }
    
    public static UserManagementException cannotRevokePrimaryAccess(String userId, String tenantId) {
        return new UserManagementException(
            String.format("Cannot revoke primary tenant access for user %s in tenant %s", userId, tenantId)
        );
    }
    
    public static UserManagementException cannotRevokeOnlyAccess(String userId) {
        return new UserManagementException(
            String.format("Cannot revoke the only tenant access for user %s", userId)
        );
    }
}