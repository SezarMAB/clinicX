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
}