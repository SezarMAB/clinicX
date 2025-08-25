package sy.sezar.clinicx.tenant.service;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import java.util.List;
import java.util.Map;

/**
 * Service for handling Keycloak user operations.
 * Abstracts Keycloak API interactions to simplify the main service logic.
 */
public interface KeycloakUserService {
    
    /**
     * Gets a Keycloak user by ID from a specific realm.
     */
    UserRepresentation getUser(String realmName, String userId);
    
    /**
     * Updates a user in Keycloak.
     */
    void updateUser(String realmName, String userId, UserRepresentation user);
    
    /**
     * Enables or disables a user in Keycloak.
     */
    void setUserEnabled(String realmName, String userId, boolean enabled);
    
    /**
     * Deletes a user from Keycloak.
     */
    void deleteUser(String realmName, String userId);
    
    /**
     * Searches for users in a realm.
     */
    List<UserRepresentation> searchUsers(String realmName, String searchTerm);
    
    /**
     * Finds a user by username across all realms.
     */
    UserRepresentation findUserByUsername(String username);
    
    /**
     * Finds a user by ID across all realms.
     */
    UserRepresentation findUserById(String userId);
    
    /**
     * Updates user attributes.
     */
    void updateUserAttributes(UserRepresentation user, Map<String, String> attributes);
    
    /**
     * Resets a user's password.
     */
    void resetPassword(String realmName, String userId, String newPassword, boolean temporary);
    
    /**
     * Updates realm-level roles for a user.
     */
    void updateRealmRoles(String realmName, String userId, List<String> newRoles);
    
    /**
     * Gets the realm resource for a tenant.
     */
    RealmResource getRealmResource(String realmName);
    
    /**
     * Creates a credential representation for password.
     */
    CredentialRepresentation createPasswordCredential(String password, boolean temporary);
    
    /**
     * Gets effective realm roles for a user.
     */
    List<RoleRepresentation> getUserRealmRoles(String realmName, String userId);
    
    /**
     * Checks if a user exists in a realm.
     */
    boolean userExists(String realmName, String userId);
    
    /**
     * Gets all realm names.
     */
    List<String> getAllRealmNames();
    
    /**
     * Finds the realm where a user exists by their user ID.
     * Returns the realm name or null if user not found.
     */
    String findUserRealm(String userId);
    
    /**
     * Gets a user from any realm by their ID.
     * First tries to find which realm the user belongs to, then fetches the user.
     */
    UserRepresentation getUserFromAnyRealm(String userId);
}