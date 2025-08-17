package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.tenant.constants.TenantConstants;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.service.KeycloakUserService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of KeycloakUserService.
 * Handles all Keycloak-related user operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService {
    
    private final KeycloakAdminService keycloakAdminService;
    
    @Override
    public UserRepresentation getUser(String realmName, String userId) {
        try {
            return getRealmResource(realmName)
                .users()
                .get(userId)
                .toRepresentation();
        } catch (Exception e) {
            log.error("Failed to get user {} from realm {}", userId, realmName, e);
            throw new NotFoundException(String.format(TenantConstants.ERROR_USER_NOT_FOUND, userId));
        }
    }
    
    @Override
    public void updateUser(String realmName, String userId, UserRepresentation user) {
        try {
            getRealmResource(realmName)
                .users()
                .get(userId)
                .update(user);
        } catch (Exception e) {
            log.error("Failed to update user {} in realm {}", userId, realmName, e);
            throw new BusinessRuleException(String.format(TenantConstants.ERROR_FAILED_TO_UPDATE_USER, e.getMessage()));
        }
    }
    
    @Override
    public void setUserEnabled(String realmName, String userId, boolean enabled) {
        try {
            UserRepresentation user = getUser(realmName, userId);
            user.setEnabled(enabled);
            updateUser(realmName, userId, user);
            log.info("User {} {} in realm {}", userId, enabled ? "enabled" : "disabled", realmName);
        } catch (Exception e) {
            log.error("Failed to set user {} enabled status in realm {}", userId, realmName, e);
            throw new BusinessRuleException("Failed to update user status: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteUser(String realmName, String userId) {
        try {
            getRealmResource(realmName)
                .users()
                .delete(userId);
            log.info("Deleted user {} from realm {}", userId, realmName);
        } catch (Exception e) {
            log.error("Failed to delete user {} from realm {}", userId, realmName, e);
            throw new BusinessRuleException(String.format(TenantConstants.ERROR_FAILED_TO_DELETE, e.getMessage()));
        }
    }
    
    @Override
    public List<UserRepresentation> searchUsers(String realmName, String searchTerm) {
        try {
            return getRealmResource(realmName)
                .users()
                .search(searchTerm);
        } catch (Exception e) {
            log.error("Failed to search users in realm {} with term {}", realmName, searchTerm, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public UserRepresentation findUserByUsername(String username) {
        List<String> realmNames = getAllRealmNames();
        
        for (String realmName : realmNames) {
            try {
                List<UserRepresentation> users = searchUsers(realmName, username);
                if (!users.isEmpty()) {
                    UserRepresentation user = users.get(0);
                    // Add primary realm info
                    ensureAttributes(user);
                    user.getAttributes().put(TenantConstants.ATTR_PRIMARY_REALM, Arrays.asList(realmName));
                    return user;
                }
            } catch (Exception e) {
                log.debug("Error searching in realm {}: {}", realmName, e.getMessage());
            }
        }
        
        return null;
    }
    
    @Override
    public UserRepresentation findUserById(String userId) {
        List<String> realmNames = getAllRealmNames();
        
        for (String realmName : realmNames) {
            try {
                UserRepresentation user = getUser(realmName, userId);
                if (user != null) {
                    ensureAttributes(user);
                    user.getAttributes().put(TenantConstants.ATTR_PRIMARY_REALM, Arrays.asList(realmName));
                    return user;
                }
            } catch (Exception e) {
                log.debug("User not found in realm {}", realmName);
            }
        }
        
        return null;
    }
    
    @Override
    public void updateUserAttributes(UserRepresentation user, Map<String, String> attributes) {
        ensureAttributes(user);
        Map<String, List<String>> userAttributes = user.getAttributes();
        
        attributes.forEach((key, value) -> 
            userAttributes.put(key, Arrays.asList(value))
        );
    }
    
    @Override
    public void resetPassword(String realmName, String userId, String newPassword, boolean temporary) {
        try {
            CredentialRepresentation credential = createPasswordCredential(newPassword, temporary);
            getRealmResource(realmName)
                .users()
                .get(userId)
                .resetPassword(credential);
            log.info("Password reset for user {} in realm {}", userId, realmName);
        } catch (Exception e) {
            log.error("Failed to reset password for user {} in realm {}", userId, realmName, e);
            throw new BusinessRuleException(String.format(TenantConstants.ERROR_FAILED_TO_RESET_PASSWORD, e.getMessage()));
        }
    }
    
    @Override
    public void updateRealmRoles(String realmName, String userId, List<String> newRoles) {
        try {
            RealmResource realmResource = getRealmResource(realmName);
            
            // Get all realm roles
            List<RoleRepresentation> allRoles = realmResource.roles().list();
            
            // Get current user roles
            List<RoleRepresentation> currentRoles = realmResource.users().get(userId)
                .roles().realmLevel().listEffective();
            
            // Remove all current roles
            realmResource.users().get(userId).roles().realmLevel().remove(currentRoles);
            
            // Add new roles
            List<RoleRepresentation> rolesToAdd = allRoles.stream()
                .filter(role -> newRoles.contains(role.getName()))
                .collect(Collectors.toList());
            
            realmResource.users().get(userId).roles().realmLevel().add(rolesToAdd);
            log.info("Updated realm roles for user {} in realm {}", userId, realmName);
        } catch (Exception e) {
            log.error("Failed to update realm roles for user {} in realm {}", userId, realmName, e);
            throw new BusinessRuleException(String.format(TenantConstants.ERROR_FAILED_TO_UPDATE_ROLES, e.getMessage()));
        }
    }
    
    @Override
    public RealmResource getRealmResource(String realmName) {
        return keycloakAdminService.getKeycloakInstance().realm(realmName);
    }
    
    @Override
    public CredentialRepresentation createPasswordCredential(String password, boolean temporary) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(temporary);
        return credential;
    }
    
    @Override
    public List<RoleRepresentation> getUserRealmRoles(String realmName, String userId) {
        try {
            return getRealmResource(realmName)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listEffective();
        } catch (Exception e) {
            log.error("Failed to get realm roles for user {} in realm {}", userId, realmName, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean userExists(String realmName, String userId) {
        try {
            getUser(realmName, userId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    
    @Override
    public List<String> getAllRealmNames() {
        try {
            return keycloakAdminService.getKeycloakInstance()
                .realms()
                .findAll()
                .stream()
                .map(realm -> realm.getRealm())
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get all realm names", e);
            return new ArrayList<>();
        }
    }
    
    private void ensureAttributes(UserRepresentation user) {
        if (user.getAttributes() == null) {
            user.setAttributes(new HashMap<>());
        }
    }
}