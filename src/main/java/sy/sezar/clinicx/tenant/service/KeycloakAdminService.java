package sy.sezar.clinicx.tenant.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeycloakAdminService {

    RealmRepresentation createRealm(String realmName, String displayName);

    void deleteRealm(String realmName);

    boolean realmExists(String realmName);

    ClientRepresentation createClient(String realmName, String clientId, String clientSecret, List<String> redirectUris);

    void createRealmRole(String realmName, String roleName, String description);

    UserRepresentation createUser(String realmName, String username, String email, String firstName, String lastName, String password, List<String> roles);

    UserRepresentation createUserWithTenantInfo(String realmName, String username, String email,
                                              String firstName, String lastName, String password,
                                              List<String> roles, String tenantId, String clinicName, String clinicType);

    void assignRoleToUser(String realmName, String userId, String roleName);

    void updateRealmSettings(String realmName, RealmRepresentation realmSettings);

    List<UserRepresentation> getRealmUsers(String realmName);

    List<RoleRepresentation> getRealmRoles(String realmName);

    Keycloak getKeycloakInstance();

    String getClientSecret(String realmName, String clientId);

    /**
     * Update user attributes in Keycloak.
     *
     * @param realmName the realm name
     * @param username the username
     * @param attributes the attributes to update
     */
    void updateUserAttributes(String realmName, String username, java.util.Map<String, List<String>> attributes);

    /**
     * Get user by username.
     *
     * @param realmName the realm name
     * @param username the username
     * @return the user representation
     */
    UserRepresentation getUserByUsername(String realmName, String username);

    /**
     * Get user by user ID.
     *
     * @param realmName the realm name
     * @param userId the user ID
     * @return the user representation
     */
    UserRepresentation getUserByUserId(String realmName, String userId);

    /**
     * Copy clients from one realm to another.
     *
     * @param sourceRealmName the source realm
     * @param targetRealmName the target realm
     */
    void copyClientsFromRealm(String sourceRealmName, String targetRealmName);

    /**
     * Create or update a protocol mapper for a client.
     *
     * @param realmName the realm name
     * @param clientId the client ID
     * @param mapperName the mapper name
     * @param attributeName the user attribute name
     */
    void ensureProtocolMapper(String realmName, String clientId, String mapperName, String attributeName);

    /**
     * Grant access to an additional tenant for an existing user.
     *
     * @param realmName the realm name where the user exists
     * @param username the username
     * @param newTenantId the new tenant ID to grant access to
     * @param newClinicName the clinic name for the new tenant
     * @param newClinicType the clinic type for the new tenant
     * @param roles the roles to assign for the new tenant
     */
    void grantAdditionalTenantAccessByUserName(String realmName, String username, String newTenantId,
                                     String newClinicName, String newClinicType, List<String> roles);


    /**
     * Grant access to an additional tenant for an existing user.
     *
     * @param realmName the realm name where the user exists
     * @param userId the keycloak user ID
     * @param newTenantId the new tenant ID to grant access to
     * @param newClinicName the clinic name for the new tenant
     * @param newClinicType the clinic type for the new tenant
     * @param roles the roles to assign for the new tenant
     */
    void grantAdditionalTenantAccessByUserId(String realmName, String userId, String newTenantId,
        String newClinicName, String newClinicType, List<String> roles);
    /**
     * Revoke access to a tenant for a user.
     *
     * @param realmName the realm name where the user exists
     * @param username the username
     * @param tenantId the tenant ID to revoke access from
     */
    void revokeTenantAccess(String realmName, String username, String tenantId);

    /**
     * Update the active tenant for a user.
     *
     * @param realmName the realm name
     * @param username the username
     * @param newActiveTenantId the new active tenant ID
     */
    void updateUserActiveTenant(String realmName, String username, String newActiveTenantId);

    /**
     * Reset user password.
     *
     * @param realmName the realm name
     * @param username the username
     * @param newPassword the new password
     */
    void resetUserPassword(String realmName, String username, String newPassword);

    /**
     * Delete all users from a realm (except service accounts).
     *
     * @param realmName the realm name
     */
    void deleteAllUsersFromRealm(String realmName);

    /**
     * Get all users from a specific tenant (based on tenant_id attribute).
     *
     * @param realmName the realm name
     * @param tenantId the tenant ID
     * @return list of users belonging to the tenant
     */
    List<UserRepresentation> getUsersByTenantId(String realmName, String tenantId);

    /**
     * Delete a user from realm by user ID.
     *
     * @param realmName the realm name
     * @param userId the user ID
     */
    void deleteUserById(String realmName, String userId);

    /**
     * Disable a user in Keycloak (set enabled = false).
     *
     * @param realmName the realm name
     * @param userId the user ID
     */
    void disableUser(String realmName, String userId);

    /**
     * Enable a user in Keycloak (set enabled = true).
     *
     * @param realmName the realm name
     * @param userId the user ID
     */
    void enableUser(String realmName, String userId);
}
