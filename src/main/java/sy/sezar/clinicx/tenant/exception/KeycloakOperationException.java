package sy.sezar.clinicx.tenant.exception;

/**
 * Exception thrown when Keycloak operations fail.
 */
public class KeycloakOperationException extends RuntimeException {
    
    public KeycloakOperationException(String message) {
        super(message);
    }
    
    public KeycloakOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static KeycloakOperationException userNotFound(String userId) {
        return new KeycloakOperationException(
            String.format("Keycloak user not found: %s", userId)
        );
    }
    
    public static KeycloakOperationException realmNotFound(String realmName) {
        return new KeycloakOperationException(
            String.format("Keycloak realm not found: %s", realmName)
        );
    }
    
    public static KeycloakOperationException updateFailed(String userId, String operation) {
        return new KeycloakOperationException(
            String.format("Failed to %s for user %s in Keycloak", operation, userId)
        );
    }
    
    public static KeycloakOperationException createUserFailed(String username) {
        return new KeycloakOperationException(
            String.format("Failed to create user %s in Keycloak", username)
        );
    }
}