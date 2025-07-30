package sy.sezar.clinicx.tenant.service.impl;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.staff.model.enums.StaffRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.master-realm:master}")
    private String masterRealm;

    @Value("${keycloak.admin-client-id:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Value("${app.domain}")
    private String appDomain;

    private Keycloak keycloak;

    @Override
    public Keycloak getKeycloakInstance() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm(masterRealm)
                    .clientId(adminClientId)
                    .username(adminUsername)
                    .password(adminPassword)
                    .build();
        }
        return keycloak;
    }

    @Override
    public RealmRepresentation createRealm(String realmName, String displayName) {
        try {
            Keycloak keycloak = getKeycloakInstance();

            RealmRepresentation realm = new RealmRepresentation();
            realm.setRealm(realmName);
            realm.setDisplayName(displayName);
            realm.setEnabled(true);

            // Configure realm settings
            realm.setRegistrationAllowed(false);
            realm.setResetPasswordAllowed(true);
            realm.setRememberMe(true);
            realm.setVerifyEmail(false);
            realm.setLoginWithEmailAllowed(true);
            realm.setDuplicateEmailsAllowed(false);

            // Token settings
            realm.setAccessTokenLifespan(1800); // 30 minutes
            realm.setRefreshTokenMaxReuse(0);
            realm.setSsoSessionIdleTimeout(1800); // 30 minutes
            realm.setSsoSessionMaxLifespan(36000); // 10 hours

            // Security settings
            realm.setBruteForceProtected(true);
            realm.setPermanentLockout(false);
            realm.setFailureFactor(3);
            realm.setWaitIncrementSeconds(60);
            realm.setQuickLoginCheckMilliSeconds(1000L);
            realm.setMinimumQuickLoginWaitSeconds(60);
            realm.setMaxFailureWaitSeconds(900);
            realm.setMaxDeltaTimeSeconds(12 * 60 * 60);

            keycloak.realms().create(realm);

            // Configure user profile for tenant attributes (Keycloak 26+)
            configureUserProfile(realmName);

            // Create default roles
            createDefaultRoles(realmName);

            // Extract subdomain from realm name (format: clinic-subdomain)
            String subdomain = realmName.startsWith("clinic-") ? realmName.substring(7) : realmName;

            // Create default clients (frontend and backend)
            createDefaultClients(realmName, subdomain);

            return realm;

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to create realm: " + e.getMessage());
        }
    }

    private void createDefaultRoles(String realmName) {
        // Create roles from StaffRole enum
        for (StaffRole role : StaffRole.values()) {
            String description = switch (role) {
                case SUPER_ADMIN -> "Super administrator with tenant management access";
                case ADMIN -> "Administrator role with full access";
                case DOCTOR -> "Doctor role with patient management access";
                case NURSE -> "Nurse role with limited patient access";
                case RECEPTIONIST -> "Receptionist role with appointment management";
                case ACCOUNTANT -> "Accountant role with financial access";
                case ASSISTANT -> "Assistant role with basic clinic access";
            };
            createRealmRole(realmName, role.name(), description);
        }

        // Create additional system roles
        createRealmRole(realmName, "USER", "Basic user role");
    }

    private ClientRepresentation createDefaultClient(String realmName) {
        // This method is no longer used, clients are created in createDefaultClients
        return null;
    }

    private void createDefaultClients(String realmName, String subdomain) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Create backend client from template
            ClientRepresentation backendClient = loadClientTemplate("keyclaok/clients/clinicx-backend.json");
            if (backendClient != null) {
                // Generate new secret for backend client
                backendClient.setSecret(UUID.randomUUID().toString());
                createClientFromTemplate(realmName, backendClient);
                log.info("Created backend client for realm: {}", realmName);
            }

            // Create frontend client from template
            ClientRepresentation frontendClient = loadClientTemplate("keyclaok/clients/clinicx-frontend.json");
            if (frontendClient != null) {
                // Update redirect URIs dynamically based on subdomain
                List<String> redirectUris = new ArrayList<>();
                redirectUris.add(String.format("https://%s.%s/*", subdomain, appDomain));
                redirectUris.add(String.format("http://%s.%s/*", subdomain, appDomain));
                frontendClient.setRedirectUris(redirectUris);

                // Update web origins
                List<String> webOrigins = new ArrayList<>();
                webOrigins.add("+"); // Allow all redirect URIs as web origins
                frontendClient.setWebOrigins(webOrigins);

                // Update root URL and admin URL
                frontendClient.setRootUrl(String.format("https://%s.%s", subdomain, appDomain));
                frontendClient.setAdminUrl(String.format("https://%s.%s", subdomain, appDomain));

                createClientFromTemplate(realmName, frontendClient);
                log.info("Created frontend client for realm: {}", realmName);
            }

        } catch (Exception e) {
            log.error("Failed to create default clients", e);
            throw new BusinessRuleException("Failed to create default clients: " + e.getMessage());
        }
    }

    private ClientRepresentation loadClientTemplate(String templatePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource(templatePath);
            return mapper.readValue(resource.getInputStream(), ClientRepresentation.class);
        } catch (IOException e) {
            log.error("Failed to load client template: {}", templatePath, e);
            return null;
        }
    }

    private void createClientFromTemplate(String realmName, ClientRepresentation client) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            Response response = realmResource.clients().create(client);

            if (response.getStatus() == 201) {
                String clientId = response.getLocation().getPath().replaceAll(".*/", "");
                client.setId(clientId);

                // Configure protocol mappers for this client
                configureProtocolMappers(realmName, clientId);
            } else {
                throw new BusinessRuleException("Failed to create client " + client.getClientId() + ": " + response.getStatusInfo().getReasonPhrase());
            }
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to create client from template: " + e.getMessage());
        }
    }

    private void configureUserProfile(String realmName) {
        try {
            // First, enable user profile feature
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            RealmRepresentation realmRep = realmResource.toRepresentation();
            Map<String, String> attributes = realmRep.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            attributes.put("userProfileEnabled", "true");
            realmRep.setAttributes(attributes);
            realmResource.update(realmRep);

            // Wait a bit for the realm update to take effect
            Thread.sleep(500);

            // Now configure the user profile with custom attributes
            String userProfileConfig = """
                {
                  "attributes": [
                    {
                      "name": "username",
                      "displayName": "${username}",
                      "validations": {
                        "length": { "min": 3, "max": 255 },
                        "username-prohibited-characters": {},
                        "up-username-not-idn-homograph": {}
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin", "user"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "email",
                      "displayName": "${email}",
                      "validations": {
                        "email": {},
                        "length": { "max": 255 }
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin", "user"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "firstName",
                      "displayName": "${firstName}",
                      "validations": {
                        "length": { "max": 255 },
                        "person-name-prohibited-characters": {}
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin", "user"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "lastName",
                      "displayName": "${lastName}",
                      "validations": {
                        "length": { "max": 255 },
                        "person-name-prohibited-characters": {}
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin", "user"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "tenant_id",
                      "displayName": "tenant id",
                      "validations": {},
                      "annotations": {},
                      "required": {
                        "roles": ["admin", "user"]
                      },
                      "permissions": {
                        "view": [],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "clinic_name",
                      "displayName": "clinic name",
                      "validations": {},
                      "annotations": {},
                      "required": {
                        "roles": ["admin", "user"]
                      },
                      "permissions": {
                        "view": [],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "clinic_type",
                      "displayName": "clinic type",
                      "validations": {},
                      "annotations": {},
                      "required": {
                        "roles": ["admin", "user"]
                      },
                      "permissions": {
                        "view": [],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    }
                  ],
                  "groups": [
                    {
                      "name": "user-metadata",
                      "displayHeader": "User metadata",
                      "displayDescription": "Attributes, which refer to user metadata"
                    }
                  ]
                }
                """;

            // Make a direct REST API call to update the user profile configuration
            String adminToken = getAdminToken();
            updateUserProfileViaRest(realmName, userProfileConfig, adminToken);

            log.info("Successfully configured user profile for realm: {}", realmName);

        } catch (Exception e) {
            log.error("Failed to configure user profile", e);
            throw new BusinessRuleException("Failed to configure user profile: " + e.getMessage());
        }
    }

    private void configureProtocolMappers(String realmName, String clientId) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);

            // Create protocol mapper for tenant_id
            ProtocolMapperRepresentation tenantIdMapper = new ProtocolMapperRepresentation();
            tenantIdMapper.setName("tenant-id-mapper");
            tenantIdMapper.setProtocol("openid-connect");
            tenantIdMapper.setProtocolMapper("oidc-usermodel-attribute-mapper");

            Map<String, String> tenantIdConfig = new HashMap<>();
            tenantIdConfig.put("userAttribute", "tenant_id");
            tenantIdConfig.put("claim.name", "tenant_id");
            tenantIdConfig.put("jsonType.label", "String");
            tenantIdConfig.put("id.token.claim", "true");
            tenantIdConfig.put("access.token.claim", "true");
            tenantIdConfig.put("userinfo.token.claim", "true");
            tenantIdMapper.setConfig(tenantIdConfig);

            // Create protocol mapper for clinic_name
            ProtocolMapperRepresentation clinicNameMapper = new ProtocolMapperRepresentation();
            clinicNameMapper.setName("clinic-name-mapper");
            clinicNameMapper.setProtocol("openid-connect");
            clinicNameMapper.setProtocolMapper("oidc-usermodel-attribute-mapper");

            Map<String, String> clinicNameConfig = new HashMap<>();
            clinicNameConfig.put("userAttribute", "clinic_name");
            clinicNameConfig.put("claim.name", "clinic_name");
            clinicNameConfig.put("jsonType.label", "String");
            clinicNameConfig.put("id.token.claim", "true");
            clinicNameConfig.put("access.token.claim", "true");
            clinicNameConfig.put("userinfo.token.claim", "true");
            clinicNameMapper.setConfig(clinicNameConfig);

            // Create protocol mapper for clinic_type
            ProtocolMapperRepresentation clinicTypeMapper = new ProtocolMapperRepresentation();
            clinicTypeMapper.setName("clinic-type-mapper");
            clinicTypeMapper.setProtocol("openid-connect");
            clinicTypeMapper.setProtocolMapper("oidc-usermodel-attribute-mapper");

            Map<String, String> clinicTypeConfig = new HashMap<>();
            clinicTypeConfig.put("userAttribute", "clinic_type");
            clinicTypeConfig.put("claim.name", "clinic_type");
            clinicTypeConfig.put("jsonType.label", "String");
            clinicTypeConfig.put("id.token.claim", "true");
            clinicTypeConfig.put("access.token.claim", "true");
            clinicTypeConfig.put("userinfo.token.claim", "true");
            clinicTypeMapper.setConfig(clinicTypeConfig);

            // Add mappers to both clients
            List<String> clientIds = Arrays.asList("clinicx-backend", "clinicx-frontend");

            for (String clientIdName : clientIds) {
                List<ClientRepresentation> clients = realmResource.clients().findByClientId(clientIdName);
                if (!clients.isEmpty()) {
                    String internalClientId = clients.get(0).getId();
                    try {
                        realmResource.clients().get(internalClientId).getProtocolMappers().createMapper(tenantIdMapper);
                        realmResource.clients().get(internalClientId).getProtocolMappers().createMapper(clinicNameMapper);
                        realmResource.clients().get(internalClientId).getProtocolMappers().createMapper(clinicTypeMapper);
                        log.info("Added protocol mappers to client: {}", clientIdName);
                    } catch (Exception e) {
                        log.warn("Failed to add protocol mappers to client {}: {}", clientIdName, e.getMessage());
                    }
                } else {
                    log.warn("Could not find client '{}' to add protocol mappers", clientIdName);
                }
            }

            log.info("Successfully configured protocol mappers for realm: {}", realmName);

        } catch (Exception e) {
            log.error("Failed to configure protocol mappers", e);
            throw new BusinessRuleException("Failed to configure protocol mappers: " + e.getMessage());
        }
    }

    @Override
    public void deleteRealm(String realmName) {
        try {
            getKeycloakInstance().realm(realmName).remove();
        } catch (NotFoundException e) {
            throw new BusinessRuleException("Realm not found: " + realmName);
        }
    }

    @Override
    public boolean realmExists(String realmName) {
        try {
            getKeycloakInstance().realm(realmName).toRepresentation();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public ClientRepresentation createClient(String realmName, String clientId, String clientSecret, List<String> redirectUris) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);

            ClientRepresentation client = new ClientRepresentation();
            client.setClientId(clientId);
            client.setSecret(clientSecret);
            client.setServiceAccountsEnabled(true);
            client.setDirectAccessGrantsEnabled(true);
            client.setAuthorizationServicesEnabled(false);
            client.setStandardFlowEnabled(true);
            client.setImplicitFlowEnabled(false);
            client.setPublicClient(false);
            client.setProtocol("openid-connect");
            client.setRedirectUris(redirectUris);

            Map<String, String> attributes = new HashMap<>();
            attributes.put("access.token.lifespan", "1800");
            attributes.put("refresh.token.max.reuse", "0");
            client.setAttributes(attributes);

            Response response = realmResource.clients().create(client);

            if (response.getStatus() == 201) {
                String id = response.getLocation().getPath().replaceAll(".*/", "");
                client.setId(id);
                return client;
            } else {
                throw new BusinessRuleException("Failed to create client: " + response.getStatusInfo().getReasonPhrase());
            }

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to create client: " + e.getMessage());
        }
    }

    @Override
    public void createRealmRole(String realmName, String roleName, String description) {
        try {
            RolesResource rolesResource = getKeycloakInstance().realm(realmName).roles();

            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription(description);

            rolesResource.create(role);

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to create role: " + e.getMessage());
        }
    }

    @Override
    public UserRepresentation createUser(String realmName, String username, String email,
                                       String firstName, String lastName, String password, List<String> roles) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);

            Response response = usersResource.create(user);

            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/", "");
                user.setId(userId);

                // Set password
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(password);
                credential.setTemporary(false);

                usersResource.get(userId).resetPassword(credential);

                // Assign roles
                for (String roleName : roles) {
                    assignRoleToUser(realmName, userId, roleName);
                }

                return user;
            } else {
                throw new BusinessRuleException("Failed to create user: " + response.getStatusInfo().getReasonPhrase());
            }

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * Creates a user with tenant attributes
     */
    public UserRepresentation createUserWithTenantInfo(String realmName, String username, String email,
                                                       String firstName, String lastName, String password,
                                                       List<String> roles, String tenantId, String clinicName, String clinicType) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);

            // Set tenant attributes
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("tenant_id", Arrays.asList(tenantId));
            attributes.put("clinic_name", Arrays.asList(clinicName));
            attributes.put("clinic_type", Arrays.asList(clinicType));
            user.setAttributes(attributes);

            Response response = usersResource.create(user);

            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/", "");
                user.setId(userId);

                // Set password
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(password);
                credential.setTemporary(false);

                usersResource.get(userId).resetPassword(credential);

                // Assign roles
                for (String roleName : roles) {
                    assignRoleToUser(realmName, userId, roleName);
                }

                log.info("Successfully created user {} with tenant attributes in realm {}", username, realmName);
                return user;
            } else {
                throw new BusinessRuleException("Failed to create user: " + response.getStatusInfo().getReasonPhrase());
            }

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to create user with tenant info: " + e.getMessage());
        }
    }

    @Override
    public void assignRoleToUser(String realmName, String userId, String roleName) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

            realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(role));

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to assign role to user: " + e.getMessage());
        }
    }

    @Override
    public void updateRealmSettings(String realmName, RealmRepresentation realmSettings) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            realmResource.update(realmSettings);

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to update realm settings: " + e.getMessage());
        }
    }

    @Override
    public List<UserRepresentation> getRealmUsers(String realmName) {
        try {
            return getKeycloakInstance().realm(realmName).users().list();
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to get realm users: " + e.getMessage());
        }
    }

    @Override
    public List<RoleRepresentation> getRealmRoles(String realmName) {
        try {
            return getKeycloakInstance().realm(realmName).roles().list();
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to get realm roles: " + e.getMessage());
        }
    }

    private String getAdminToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String tokenUrl = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "grant_type=password" +
                    "&client_id=" + adminClientId +
                    "&username=" + adminUsername +
                    "&password=" + adminPassword;

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
            throw new BusinessRuleException("Failed to get admin token");
        } catch (Exception e) {
            log.error("Failed to get admin token", e);
            throw new BusinessRuleException("Failed to get admin token: " + e.getMessage());
        }
    }

    private void updateUserProfileViaRest(String realmName, String userProfileConfig, String adminToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userProfileUrl = keycloakServerUrl + "/admin/realms/" + realmName + "/users/profile";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            HttpEntity<String> request = new HttpEntity<>(userProfileConfig, headers);
            ResponseEntity<String> response = restTemplate.exchange(userProfileUrl, HttpMethod.PUT, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new BusinessRuleException("Failed to update user profile. Status: " + response.getStatusCode());
            }

            log.info("Successfully updated user profile configuration for realm: {}", realmName);
        } catch (Exception e) {
            log.error("Failed to update user profile via REST", e);
            throw new BusinessRuleException("Failed to update user profile via REST: " + e.getMessage());
        }
    }

    @Override
    public String getClientSecret(String realmName, String clientId) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            List<ClientRepresentation> clients = realmResource.clients().findByClientId(clientId);

            if (!clients.isEmpty()) {
                String internalClientId = clients.get(0).getId();
                return realmResource.clients().get(internalClientId).getSecret().getValue();
            } else {
                throw new BusinessRuleException("Client not found: " + clientId);
            }
        } catch (Exception e) {
            log.error("Failed to get client secret", e);
            throw new BusinessRuleException("Failed to get client secret: " + e.getMessage());
        }
    }
}
