package sy.sezar.clinicx.tenant.service.impl;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
                case EXTERNAL ->  "External role with basic clinic access";
                case INTERNAL ->  "Internal role with basic clinic access";
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
                subdomain = subdomain.replace("-realm", "");
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

            // Now configure the user profile with custom attributes including multi-tenant support
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
                      "displayName": "Tenant ID",
                      "validations": {
                        "length": { "min": 1, "max": 255 }
                      },
                      "annotations": {
                        "inputType": "text"
                      },
                      "required": {
                        "roles": ["admin", "user"]
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "clinic_name",
                      "displayName": "Clinic Name",
                      "validations": {
                        "length": { "min": 1, "max": 255 }
                      },
                      "annotations": {
                        "inputType": "text"
                      },
                      "required": {
                        "roles": ["admin", "user"]
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "clinic_type",
                      "displayName": "Clinic Type",
                      "validations": {
                        "length": { "min": 1, "max": 255 }
                      },
                      "annotations": {
                        "inputType": "text"
                      },
                      "required": {
                        "roles": ["admin", "user"]
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "active_tenant_id",
                      "displayName": "Active Tenant ID",
                      "validations": {
                        "length": { "max": 255 }
                      },
                      "annotations": {
                        "inputType": "text"
                      },
                      "required": {
                        "roles": ["admin", "user"]
                      },
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "accessible_tenants",
                      "displayName": "Accessible Tenants",
                      "validations": {
                        "length": { "max": 4000 }
                      },
                      "annotations": {
                        "inputType": "textarea",
                        "inputHelperTextBefore": "JSON array of accessible tenants"
                      },
                      "required": null,
                      "permissions": {
                        "view": ["admin", "user"],
                        "edit": ["admin"]
                      },
                      "multivalued": false
                    },
                    {
                      "name": "user_tenant_roles",
                      "displayName": "User Tenant Roles",
                      "validations": {
                        "length": { "max": 4000 }
                      },
                      "annotations": {
                        "inputType": "textarea",
                        "inputHelperTextBefore": "JSON object mapping tenant IDs to roles"
                      },
                      "required": null,
                      "permissions": {
                        "view": ["admin", "user"],
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
                    },
                    {
                      "name": "tenant-metadata",
                      "displayHeader": "Tenant metadata",
                      "displayDescription": "Multi-tenant attributes for user access control"
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

            // Define all required mappers for multi-tenant support
            List<ProtocolMapperRepresentation> mappers = new ArrayList<>();

            // 1. Tenant ID Mapper
            mappers.add(createUserAttributeMapper(
                "tenant_id",
                "tenant_id",
                "tenant_id",
                "String",
                true, true, true
            ));

            // 2. Active Tenant ID Mapper
            mappers.add(createUserAttributeMapper(
                "active_tenant_id",
                "active_tenant_id",
                "active_tenant_id",
                "String",
                false, true, true
            ));

            // 3. Accessible Tenants Mapper (JSON)
            mappers.add(createUserAttributeMapper(
                "accessible_tenants",
                "accessible_tenants",
                "accessible_tenants",
                "String",  // Use String type, the content will be JSON string
                false, true, true
            ));

            // 4. Specialty Mapper (maps clinic_type to specialty claim)
            mappers.add(createUserAttributeMapper(
                "specialty",
                "clinic_type",
                "specialty",
                "String",
                false, true, true
            ));

            // 5. User Tenant Roles Mapper (JSON)
            mappers.add(createUserAttributeMapper(
                "user_tenant_roles",
                "user_tenant_roles",
                "user_tenant_roles",
                "String",  // Use String type, the content will be JSON string
                false, true, true
            ));

            // 6. Clinic Name Mapper
            mappers.add(createUserAttributeMapper(
                "clinic_name",
                "clinic_name",
                "clinic_name",
                "String",
                true, true, true
            ));

            // 7. Clinic Type Mapper
            mappers.add(createUserAttributeMapper(
                "clinic_type",
                "clinic_type",
                "clinic_type",
                "String",
                true, true, true
            ));

            // Add mappers to both clients
            List<String> clientIds = Arrays.asList("clinicx-backend", "clinicx-frontend");

            for (String clientIdName : clientIds) {
                List<ClientRepresentation> clients = realmResource.clients().findByClientId(clientIdName);
                if (!clients.isEmpty()) {
                    String internalClientId = clients.get(0).getId();
                    ClientResource clientResource = realmResource.clients().get(internalClientId);

                    // Get existing mappers to avoid duplicates
                    List<ProtocolMapperRepresentation> existingMappers = clientResource.getProtocolMappers().getMappers();
                    Set<String> existingMapperNames = existingMappers.stream()
                        .map(ProtocolMapperRepresentation::getName)
                        .collect(Collectors.toSet());

                    // Add only new mappers
                    for (ProtocolMapperRepresentation mapper : mappers) {
                        if (!existingMapperNames.contains(mapper.getName())) {
                            try {
                                clientResource.getProtocolMappers().createMapper(mapper);
                                log.info("Added protocol mapper '{}' to client: {}", mapper.getName(), clientIdName);
                            } catch (Exception e) {
                                log.warn("Failed to add protocol mapper '{}' to client {}: {}",
                                    mapper.getName(), clientIdName, e.getMessage());
                            }
                        }
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

    /**
     * Helper method to create a user attribute protocol mapper
     */
    private ProtocolMapperRepresentation createUserAttributeMapper(
            String mapperName,
            String userAttribute,
            String claimName,
            String jsonType,
            boolean addToIdToken,
            boolean addToAccessToken,
            boolean addToUserInfo) {

        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setName(mapperName);
        mapper.setProtocol("openid-connect");
        mapper.setProtocolMapper("oidc-usermodel-attribute-mapper");

        Map<String, String> config = new HashMap<>();
        config.put("user.attribute", userAttribute);
        config.put("claim.name", claimName);
        config.put("jsonType.label", jsonType);
        config.put("id.token.claim", String.valueOf(addToIdToken));
        config.put("access.token.claim", String.valueOf(addToAccessToken));
        config.put("userinfo.token.claim", String.valueOf(addToUserInfo));
        config.put("multivalued", "false");
        config.put("aggregate.attrs", "false");

        mapper.setConfig(config);
        return mapper;
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
            // For master realm, ensure user profile is configured first
            if ("master".equals(realmName)) {
                log.info("Ensuring user profile is configured for master realm");
                try {
                    configureUserProfile(realmName);
                } catch (Exception e) {
                    log.warn("Could not configure user profile for master realm, this might cause issues with custom attributes: {}", e.getMessage());
                }
            }
            
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);

            // Set multi-tenant attributes
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("tenant_id", Arrays.asList(tenantId));
            attributes.put("clinic_name", Arrays.asList(clinicName != null ? clinicName : ""));
            attributes.put("clinic_type", Arrays.asList(clinicType != null ? clinicType : ""));
            attributes.put("active_tenant_id", Arrays.asList(tenantId)); // Set active tenant same as primary tenant initially

            // Initialize accessible_tenants with the primary tenant
            String accessibleTenants = String.format(
                "[{\"tenant_id\":\"%s\",\"clinic_name\":\"%s\",\"clinic_type\":\"%s\",\"specialty\":\"%s\",\"roles\":%s}]",
                tenantId, clinicName != null ? clinicName : "", clinicType != null ? clinicType : "", clinicType != null ? clinicType : "",
                convertRolesToJson(roles)
            );
            attributes.put("accessible_tenants", Arrays.asList(accessibleTenants));

            // Initialize user_tenant_roles
            String userTenantRoles = String.format("{\"%s\":%s}", tenantId, convertRolesToJson(roles));
            attributes.put("user_tenant_roles", Arrays.asList(userTenantRoles));

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

                log.info("Successfully created user {} with multi-tenant attributes in realm {}", username, realmName);
                return user;
            } else {
                throw new BusinessRuleException("Failed to create user: " + response.getStatusInfo().getReasonPhrase());
            }

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to create user with tenant info: " + e.getMessage());
        }
    }

    /**
     * Convert list of roles to JSON array string
     */
    private String convertRolesToJson(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return "[]";
        }
        return "[" + roles.stream()
            .map(role -> "\"" + role + "\"")
            .collect(Collectors.joining(",")) + "]";
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

    @Override
    public void updateUserAttributes(String realmName, String username, Map<String, List<String>> attributes) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            List<UserRepresentation> users = realmResource.users().search(username);

            if (!users.isEmpty()) {
                UserRepresentation user = users.get(0);
                Map<String, List<String>> existingAttributes = user.getAttributes();
                if (existingAttributes == null) {
                    existingAttributes = new HashMap<>();
                }
                existingAttributes.putAll(attributes);
                user.setAttributes(existingAttributes);

                realmResource.users().get(user.getId()).update(user);
                log.info("Updated attributes for user {} in realm {}", username, realmName);
            } else {
                throw new BusinessRuleException("User not found: " + username);
            }
        } catch (Exception e) {
            log.error("Failed to update user attributes", e);
            throw new BusinessRuleException("Failed to update user attributes: " + e.getMessage());
        }
    }

    @Override
    public UserRepresentation getUserByUsername(String realmName, String username) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            List<UserRepresentation> users = realmResource.users().search(username);

            if (!users.isEmpty()) {
                return users.get(0);
            } else {
                throw new BusinessRuleException("User not found: " + username);
            }
        } catch (Exception e) {
            log.error("Failed to get user by username", e);
            throw new BusinessRuleException("Failed to get user: " + e.getMessage());
        }
    }

    @Override
    public UserRepresentation getUserByUserId(String realmName, String userId) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();

            if (user != null) {
                return user;
            } else {
                throw new BusinessRuleException("User not found with ID: " + userId);
            }
        } catch (NotFoundException e) {
            log.error("User not found with ID: {}", userId);
            throw new BusinessRuleException("User not found with ID: " + userId);
        } catch (Exception e) {
            log.error("Failed to get user by ID", e);
            throw new BusinessRuleException("Failed to get user: " + e.getMessage());
        }
    }

    @Override
    public void copyClientsFromRealm(String sourceRealmName, String targetRealmName) {
        try {
            Keycloak keycloak = getKeycloakInstance();
            RealmResource sourceRealm = keycloak.realm(sourceRealmName);
            RealmResource targetRealm = keycloak.realm(targetRealmName);

            List<ClientRepresentation> sourceClients = sourceRealm.clients().findAll();

            for (ClientRepresentation sourceClient : sourceClients) {
                // Skip built-in clients
                if (sourceClient.getClientId().startsWith("clinicx-")) {
                    ClientRepresentation targetClient = new ClientRepresentation();
                    targetClient.setClientId(sourceClient.getClientId());
                    targetClient.setName(sourceClient.getName());
                    targetClient.setDescription(sourceClient.getDescription());
                    targetClient.setEnabled(sourceClient.isEnabled());
                    targetClient.setPublicClient(sourceClient.isPublicClient());
                    targetClient.setDirectAccessGrantsEnabled(sourceClient.isDirectAccessGrantsEnabled());
                    targetClient.setServiceAccountsEnabled(sourceClient.isServiceAccountsEnabled());
                    // Authorization services configuration might not be available in all Keycloak versions
                    // targetClient.setAuthorizationServicesEnabled(sourceClient.isAuthorizationServicesEnabled());
                    targetClient.setRedirectUris(sourceClient.getRedirectUris());
                    targetClient.setWebOrigins(sourceClient.getWebOrigins());
                    targetClient.setProtocol(sourceClient.getProtocol());
                    targetClient.setAttributes(sourceClient.getAttributes());

                    // Generate new secret for confidential clients
                    if (!sourceClient.isPublicClient()) {
                        targetClient.setSecret(UUID.randomUUID().toString());
                    }

                    targetRealm.clients().create(targetClient);
                    log.info("Copied client {} from {} to {}", sourceClient.getClientId(), sourceRealmName, targetRealmName);
                }
            }
        } catch (Exception e) {
            log.error("Failed to copy clients between realms", e);
            throw new BusinessRuleException("Failed to copy clients: " + e.getMessage());
        }
    }

    @Override
    public void ensureProtocolMapper(String realmName, String clientId, String mapperName, String attributeName) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            List<ClientRepresentation> clients = realmResource.clients().findByClientId(clientId);

            if (!clients.isEmpty()) {
                String internalClientId = clients.get(0).getId();
                ClientResource clientResource = realmResource.clients().get(internalClientId);

                // Check if mapper already exists
                List<ProtocolMapperRepresentation> existingMappers = clientResource.getProtocolMappers().getMappers();
                boolean mapperExists = existingMappers.stream()
                    .anyMatch(mapper -> mapper.getName().equals(mapperName));

                if (!mapperExists) {
                    ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
                    mapper.setName(mapperName);
                    mapper.setProtocol("openid-connect");
                    mapper.setProtocolMapper("oidc-usermodel-attribute-mapper");

                    Map<String, String> config = new HashMap<>();
                    config.put("user.attribute", attributeName);
                    config.put("claim.name", attributeName);
                    config.put("jsonType.label", "String");
                    config.put("id.token.claim", "true");
                    config.put("access.token.claim", "true");
                    config.put("userinfo.token.claim", "true");
                    config.put("multivalued", "false");
                    config.put("aggregate.attrs", "false");

                    mapper.setConfig(config);

                    clientResource.getProtocolMappers().createMapper(mapper);
                    log.info("Created protocol mapper {} for attribute {} in client {}", mapperName, attributeName, clientId);
                }
            } else {
                throw new BusinessRuleException("Client not found: " + clientId);
            }
        } catch (Exception e) {
            log.error("Failed to ensure protocol mapper", e);
            throw new BusinessRuleException("Failed to ensure protocol mapper: " + e.getMessage());
        }
    }

    @Override
    public void grantAdditionalTenantAccessByUserName(String realmName, String username, String newTenantId,
                                           String newClinicName, String newClinicType, List<String> roles) {
            UserRepresentation user = getUserByUsername(realmName, username);
        grantAdditionalTenantAccess(realmName, newTenantId, newClinicName, newClinicType, roles, user);
    }


    @Override
    public void grantAdditionalTenantAccessByUserId(String realmName, String userId, String newTenantId,
        String newClinicName, String newClinicType, List<String> roles) {
        UserRepresentation user = getUserByUserId(realmName, userId);
        grantAdditionalTenantAccess(realmName, newTenantId, newClinicName, newClinicType, roles, user);
    }

    private void grantAdditionalTenantAccess(String realmName, String newTenantId,
        String newClinicName, String newClinicType, List<String> roles, UserRepresentation user) {
        try {
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }

            // Update accessible_tenants
            String accessibleTenantsJson = attributes.getOrDefault("accessible_tenants", Arrays.asList("[]")).get(0);
            List<Map<String, Object>> accessibleTenants = parseJsonArray(accessibleTenantsJson);

            // Check if tenant already exists
            boolean tenantExists = accessibleTenants.stream()
                .anyMatch(t -> newTenantId.equals(t.get("tenant_id")));

            if (!tenantExists) {
                Map<String, Object> newTenant = new HashMap<>();
                newTenant.put("tenant_id", newTenantId);
                newTenant.put("clinic_name", newClinicName);
                newTenant.put("clinic_type", newClinicType);
                newTenant.put("specialty", newClinicType);
                newTenant.put("roles", roles);
                accessibleTenants.add(newTenant);

                attributes.put("accessible_tenants", Arrays.asList(toJsonString(accessibleTenants)));
            }

            // Update user_tenant_roles
            String userTenantRolesJson = attributes.getOrDefault("user_tenant_roles", Arrays.asList("{}")).get(0);
            Map<String, Object> userTenantRoles = parseJsonObject(userTenantRolesJson);
            userTenantRoles.put(newTenantId, roles);
            attributes.put("user_tenant_roles", Arrays.asList(toJsonString(userTenantRoles)));

            // Update user
            user.setAttributes(attributes);
            getKeycloakInstance().realm(realmName).users().get(user.getId()).update(user);

            log.info("Granted access to tenant {} for user {} in realm {}", newTenantId, user.getUsername(),
                realmName);

        } catch (Exception e) {
            log.error("Failed to grant additional tenant access", e);
            throw new BusinessRuleException("Failed to grant additional tenant access: " + e.getMessage());
        }
    }

    @Override
    public void revokeTenantAccess(String realmName, String username, String tenantId) {
        try {
            UserRepresentation user = getUserByUsername(realmName, username);
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                return;
            }

            // Update accessible_tenants
            String accessibleTenantsJson = attributes.getOrDefault("accessible_tenants", Arrays.asList("[]")).get(0);
            List<Map<String, Object>> accessibleTenants = parseJsonArray(accessibleTenantsJson);
            accessibleTenants.removeIf(t -> tenantId.equals(t.get("tenant_id")));
            attributes.put("accessible_tenants", Arrays.asList(toJsonString(accessibleTenants)));

            // Update user_tenant_roles
            String userTenantRolesJson = attributes.getOrDefault("user_tenant_roles", Arrays.asList("{}")).get(0);
            Map<String, Object> userTenantRoles = parseJsonObject(userTenantRolesJson);
            userTenantRoles.remove(tenantId);
            attributes.put("user_tenant_roles", Arrays.asList(toJsonString(userTenantRoles)));

            // If active tenant was revoked, switch to first available tenant
            String activeTenantId = attributes.getOrDefault("active_tenant_id", Arrays.asList("")).get(0);
            if (tenantId.equals(activeTenantId) && !accessibleTenants.isEmpty()) {
                String newActiveTenantId = (String) accessibleTenants.get(0).get("tenant_id");
                attributes.put("active_tenant_id", Arrays.asList(newActiveTenantId));
            }

            // Update user
            user.setAttributes(attributes);
            getKeycloakInstance().realm(realmName).users().get(user.getId()).update(user);

            log.info("Revoked access to tenant {} for user {} in realm {}", tenantId, username, realmName);

        } catch (Exception e) {
            log.error("Failed to revoke tenant access", e);
            throw new BusinessRuleException("Failed to revoke tenant access: " + e.getMessage());
        }
    }

    @Override
    public void updateUserActiveTenant(String realmName, String username, String newActiveTenantId) {
        try {
            UserRepresentation user = getUserByUsername(realmName, username);
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }

            // Verify user has access to the new tenant
            String accessibleTenantsJson = attributes.getOrDefault("accessible_tenants", Arrays.asList("[]")).get(0);
            List<Map<String, Object>> accessibleTenants = parseJsonArray(accessibleTenantsJson);

            boolean hasAccess = accessibleTenants.stream()
                .anyMatch(t -> newActiveTenantId.equals(t.get("tenant_id")));

            if (!hasAccess) {
                throw new BusinessRuleException("User does not have access to tenant: " + newActiveTenantId);
            }

            // Update active tenant
            attributes.put("active_tenant_id", Arrays.asList(newActiveTenantId));
            user.setAttributes(attributes);
            getKeycloakInstance().realm(realmName).users().get(user.getId()).update(user);

            log.info("Updated active tenant to {} for user {} in realm {}", newActiveTenantId, username, realmName);

        } catch (Exception e) {
            log.error("Failed to update user active tenant", e);
            throw new BusinessRuleException("Failed to update user active tenant: " + e.getMessage());
        }
    }

    /**
     * Parse JSON array string to List of Maps
     * Also handles legacy pipe-delimited format for backward compatibility
     */
    private List<Map<String, Object>> parseJsonArray(String json) {
        try {
            // First try to parse as JSON
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            // If JSON parsing fails, try pipe-delimited format (legacy)
            log.warn("Failed to parse as JSON, trying pipe-delimited format: {}", json);

            List<Map<String, Object>> result = new ArrayList<>();
            if (json != null && !json.isEmpty() && !json.equals("[]")) {
                // Format: tenant_id|clinic_name|role1,role2
                String[] parts = json.split("\\|");
                if (parts.length >= 3) {
                    Map<String, Object> tenant = new HashMap<>();
                    tenant.put("tenant_id", parts[0]);
                    tenant.put("clinic_name", parts[1]);
                    tenant.put("roles", Arrays.asList(parts[2].split(",")));
                    result.add(tenant);
                }
            }
            return result;
        }
    }

    /**
     * Parse JSON object string to Map
     */
    private Map<String, Object> parseJsonObject(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse JSON object: {}", json, e);
            return new HashMap<>();
        }
    }

    /**
     * Convert object to JSON string
     */
    private String toJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Failed to convert to JSON", e);
            return obj.toString();
        }
    }

    @Override
    public void resetUserPassword(String realmName, String username, String newPassword) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);

            // Find the user by username
            List<UserRepresentation> users = realmResource.users().searchByUsername(username, true);
            if (users.isEmpty()) {
                throw new sy.sezar.clinicx.core.exception.NotFoundException("User not found: " + username);
            }

            UserRepresentation user = users.get(0);
            UserResource userResource = realmResource.users().get(user.getId());

            // Reset the password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            userResource.resetPassword(credential);

            log.info("Successfully reset password for user '{}' in realm '{}'", username, realmName);
        } catch (Exception e) {
            log.error("Failed to reset password for user '{}' in realm '{}'", username, realmName, e);
            throw new BusinessRuleException("Failed to reset user password: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllUsersFromRealm(String realmName) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            List<UserRepresentation> users = realmResource.users().list();

            int deletedCount = 0;
            for (UserRepresentation user : users) {
                // Skip service accounts (they have a special username pattern)
                if (user.getUsername() != null && user.getUsername().startsWith("service-account-")) {
                    log.debug("Skipping service account: {}", user.getUsername());
                    continue;
                }

                try {
                    realmResource.users().delete(user.getId());
                    deletedCount++;
                    log.debug("Deleted user: {} ({})", user.getUsername(), user.getId());
                } catch (Exception e) {
                    log.error("Failed to delete user: {} ({})", user.getUsername(), user.getId(), e);
                }
            }

            log.info("Deleted {} users from realm '{}'", deletedCount, realmName);
        } catch (Exception e) {
            log.error("Failed to delete all users from realm '{}'", realmName, e);
            throw new BusinessRuleException("Failed to delete users from realm: " + e.getMessage());
        }
    }

    @Override
    public List<UserRepresentation> getUsersByTenantId(String realmName, String tenantId) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            List<UserRepresentation> allUsers = realmResource.users().list();

            // Filter users by tenant_id attribute
            return allUsers.stream()
                .filter(user -> {
                    Map<String, List<String>> attributes = user.getAttributes();
                    if (attributes != null && attributes.containsKey("tenant_id")) {
                        List<String> tenantIds = attributes.get("tenant_id");
                        return tenantIds != null && tenantIds.contains(tenantId);
                    }
                    return false;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get users for tenant '{}' from realm '{}'", tenantId, realmName, e);
            throw new BusinessRuleException("Failed to get users by tenant ID: " + e.getMessage());
        }
    }

    @Override
    public void deleteUserById(String realmName, String userId) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            realmResource.users().delete(userId);
            log.info("Successfully deleted user '{}' from realm '{}'", userId, realmName);
        } catch (Exception e) {
            log.error("Failed to delete user '{}' from realm '{}'", userId, realmName, e);
            throw new BusinessRuleException("Failed to delete user: " + e.getMessage());
        }
    }

    @Override
    public void disableUser(String realmName, String userId) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation user = userResource.toRepresentation();

            // Set enabled to false
            user.setEnabled(false);
            userResource.update(user);

            log.info("Successfully disabled user '{}' in realm '{}'", user.getUsername(), realmName);
        } catch (Exception e) {
            log.error("Failed to disable user '{}' in realm '{}'", userId, realmName, e);
            throw new BusinessRuleException("Failed to disable user: " + e.getMessage());
        }
    }

    @Override
    public void enableUser(String realmName, String userId) {
        try {
            RealmResource realmResource = getKeycloakInstance().realm(realmName);
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation user = userResource.toRepresentation();

            // Set enabled to true
            user.setEnabled(true);
            userResource.update(user);

            log.info("Successfully enabled user '{}' in realm '{}'", user.getUsername(), realmName);
        } catch (Exception e) {
            log.error("Failed to enable user '{}' in realm '{}'", userId, realmName, e);
            throw new BusinessRuleException("Failed to enable user: " + e.getMessage());
        }
    }
}
