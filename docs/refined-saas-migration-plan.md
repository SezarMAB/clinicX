# Refined SaaS Migration Plan - Leveraging Existing Keycloak

## Overview
Since you already have Keycloak running and experience with it, we can implement a **progressive security approach** that starts with Keycloak from Day 1, but in a simplified manner.

## Revised Approach: Progressive Keycloak Integration

### Phase 0: Keycloak Setup for ClinicX (3-4 days)

#### Day 1: Create ClinicX Realm
```bash
# Access your existing Keycloak at http://localhost:18080/auth/
# Create new realm: clinicx-dev
```

**Realm Configuration:**
- [ ] Create realm `clinicx-dev`
- [ ] Create client `clinicx-backend`
  - Client Protocol: `openid-connect`
  - Access Type: `confidential`
  - Valid Redirect URIs: `http://localhost:8080/*`
  - Web Origins: `+`

#### Day 2: Simple User Setup
- [ ] Create test users without tenant complexity:
  ```
  - dev-admin (password: admin123)
  - dev-doctor (password: doctor123)
  - dev-staff (password: staff123)
  ```
- [ ] Create basic roles:
  - `ADMIN`
  - `DOCTOR`
  - `STAFF`
- [ ] Assign roles to users

#### Day 3-4: Spring Boot Integration
- [ ] Add Keycloak dependencies to `build.gradle`:
  ```groovy
  dependencies {
      // Keycloak
      implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
      implementation 'org.springframework.boot:spring-boot-starter-security'
  }
  ```

- [ ] Create initial application-dev.yml:
  ```yaml
  spring:
    security:
      oauth2:
        resourceserver:
          jwt:
            issuer-uri: http://localhost:18080/auth/realms/clinicx-dev
            jwk-set-uri: http://localhost:18080/auth/realms/clinicx-dev/protocol/openid-connect/certs
  
  # Tenant configuration (initially single-tenant)
  clinicx:
    tenant:
      mode: single  # Will change to 'multi' in Phase 1
      default-id: dev-tenant-001
  ```

- [ ] Create simplified security configuration:
  ```java
  @Configuration
  @EnableWebSecurity
  public class DevSecurityConfig {
      
      @Value("${clinicx.tenant.mode}")
      private String tenantMode;
      
      @Value("${clinicx.tenant.default-id}")
      private String defaultTenantId;
      
      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http
              .authorizeHttpRequests(authz -> authz
                  .requestMatchers("/api/public/**").permitAll()
                  .requestMatchers("/api/admin/**").hasRole("ADMIN")
                  .anyRequest().authenticated()
              )
              .oauth2ResourceServer(oauth2 -> oauth2
                  .jwt(jwt -> jwt
                      .jwtAuthenticationConverter(jwtAuthenticationConverter())
                  )
              );
          
          return http.build();
      }
      
      @Bean
      public JwtAuthenticationConverter jwtAuthenticationConverter() {
          JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
          converter.setJwtGrantedAuthoritiesConverter(jwt -> {
              // Simple role extraction for now
              Collection<GrantedAuthority> authorities = new ArrayList<>();
              Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
              if (realmAccess != null && realmAccess.containsKey("roles")) {
                  ((List<String>) realmAccess.get("roles")).forEach(role -> 
                      authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                  );
              }
              return authorities;
          });
          return converter;
      }
  }
  ```

### Phase 1: Multi-tenancy with Keycloak (4-6 weeks)

#### Week 1-2: Tenant-Aware Keycloak Setup
- [ ] Add tenant attribute to Keycloak users:
  ```
  User Attributes:
  - tenant_id: tenant_001
  - clinic_name: Demo Dental Clinic
  - clinic_type: DENTAL
  ```

- [ ] Create Protocol Mapper in Keycloak:
  ```
  Name: tenant-mapper
  Mapper Type: User Attribute
  User Attribute: tenant_id
  Token Claim Name: tenant_id
  Add to Access Token: Yes
  ```

- [ ] Update JWT converter to extract tenant:
  ```java
  @Component
  public class KeycloakTenantResolver implements TenantResolver {
      
      @Override
      public String resolveTenant() {
          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
          if (auth != null && auth.getPrincipal() instanceof Jwt) {
              Jwt jwt = (Jwt) auth.getPrincipal();
              String tenantId = jwt.getClaimAsString("tenant_id");
              return tenantId != null ? tenantId : "default-tenant";
          }
          return "default-tenant";
      }
  }
  ```

#### Week 3-4: Database Multi-tenancy
- [ ] Implement schema-per-tenant with tenant from JWT
- [ ] Update repositories to use tenant context
- [ ] Create tenant provisioning service

#### Week 5-6: Tenant Management via Keycloak
- [ ] Create Keycloak admin client integration:
  ```java
  @Service
  public class KeycloakTenantService {
      
      private final Keycloak keycloakAdmin;
      
      public void createTenantUser(String tenantId, String username, String clinicType) {
          UserRepresentation user = new UserRepresentation();
          user.setUsername(username);
          user.setEnabled(true);
          user.setAttributes(Map.of(
              "tenant_id", List.of(tenantId),
              "clinic_type", List.of(clinicType)
          ));
          
          keycloakAdmin.realm("clinicx-dev")
              .users()
              .create(user);
      }
  }
  ```

### Phase 2: Feature Modularization (6-8 weeks)

#### Week 1-2: Feature Flags in Keycloak
- [ ] Add feature flags as client roles:
  ```
  Client Roles (clinicx-backend):
  - FEATURE_DENTAL_MODULE
  - FEATURE_LAB_REQUESTS
  - FEATURE_ADVANCED_FINANCIAL
  ```

- [ ] Update JWT converter for features:
  ```java
  @Component
  public class KeycloakFeatureResolver {
      
      public Set<String> getEnabledFeatures() {
          Jwt jwt = getCurrentJwt();
          Set<String> features = new HashSet<>();
          
          // Extract from resource_access claim
          Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
          if (resourceAccess != null) {
              Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("clinicx-backend");
              if (clientAccess != null && clientAccess.containsKey("roles")) {
                  List<String> roles = (List<String>) clientAccess.get("roles");
                  features.addAll(roles.stream()
                      .filter(role -> role.startsWith("FEATURE_"))
                      .collect(Collectors.toSet()));
              }
          }
          
          return features;
      }
  }
  ```

#### Week 3-6: Progressive Feature Implementation
- [ ] Implement feature-based module loading
- [ ] Use Keycloak roles for feature flags
- [ ] Test with different user profiles

### Keycloak Configuration Examples

#### 1. Development Realm Export (partial):
```json
{
  "realm": "clinicx-dev",
  "enabled": true,
  "clients": [{
    "clientId": "clinicx-backend",
    "enabled": true,
    "clientAuthenticatorType": "client-secret",
    "secret": "your-generated-secret",
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "authorizationServicesEnabled": false,
    "protocol": "openid-connect",
    "attributes": {
      "saml.force.post.binding": "false",
      "saml.multivalued.roles": "false",
      "oauth2.device.authorization.grant.enabled": "false",
      "backchannel.logout.session.required": "true",
      "backchannel.logout.revoke.offline.tokens": "false"
    },
    "defaultClientScopes": [
      "web-origins",
      "profile",
      "roles",
      "email"
    ]
  }],
  "users": [{
    "username": "dev-dental-admin",
    "enabled": true,
    "emailVerified": true,
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@dentalclinic.com",
    "attributes": {
      "tenant_id": ["tenant_001"],
      "clinic_type": ["DENTAL"],
      "clinic_name": ["Smile Dental Clinic"]
    },
    "credentials": [{
      "type": "password",
      "value": "admin123",
      "temporary": false
    }],
    "realmRoles": ["ADMIN"],
    "clientRoles": {
      "clinicx-backend": [
        "FEATURE_DENTAL_MODULE",
        "FEATURE_LAB_REQUESTS",
        "FEATURE_ADVANCED_FINANCIAL"
      ]
    }
  }]
}
```

#### 2. Application Configuration:
```yaml
# application-dev.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:18080/auth/realms/clinicx-dev
          jwk-set-uri: http://localhost:18080/auth/realms/clinicx-dev/protocol/openid-connect/certs

clinicx:
  security:
    mode: keycloak  # Options: keycloak, basic, none
  tenant:
    mode: multi     # Options: single, multi
    strategy: jwt   # Options: header, jwt, subdomain
  features:
    source: keycloak-roles  # Options: keycloak-roles, database, config

# Logging for debugging
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
```

#### 3. Testing Configuration:
```java
@TestConfiguration
public class TestSecurityConfig {
    
    @Bean
    @Profile("test")
    public JwtDecoder jwtDecoder() {
        // Mock JWT decoder for tests
        return token -> {
            return Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("tenant_id", "test-tenant")
                .claim("sub", "test-user")
                .claim("realm_access", Map.of("roles", List.of("ADMIN")))
                .build();
        };
    }
}
```

### Progressive Security Implementation Timeline

```
Week 1:     Keycloak realm setup (3-4 days)
Week 2-7:   Phase 1 - Multi-tenancy with JWT-based tenant resolution
Week 8-15:  Phase 2 - Feature modularization with Keycloak roles
Week 16:    Security hardening and testing
```

### Key Advantages of This Approach

1. **Immediate Security**: Start with real security from day 1
2. **Progressive Complexity**: Begin simple, add tenant/features gradually
3. **Single Source of Truth**: Keycloak manages users, tenants, and features
4. **Production-Ready Path**: No security rewrite needed
5. **Familiar Technology**: Leverage your existing Keycloak knowledge

### Development Workflow

1. **Local Development**:
   ```bash
   # Start Keycloak
   docker-compose up -d keycloak
   
   # Import realm configuration
   /opt/keycloak/bin/kc.sh import --file clinicx-realm.json
   
   # Start Spring Boot
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

2. **Get Access Token** (for API testing):
   ```bash
   curl -X POST http://localhost:18080/auth/realms/clinicx-dev/protocol/openid-connect/token \
     -d "client_id=clinicx-backend" \
     -d "client_secret=your-secret" \
     -d "username=dev-dental-admin" \
     -d "password=admin123" \
     -d "grant_type=password"
   ```

3. **Test API with Token**:
   ```bash
   curl -H "Authorization: Bearer ${ACCESS_TOKEN}" \
        http://localhost:8080/api/v1/patients
   ```

### Migration Checkpoints

- [ ] **After Week 1**: Basic Keycloak auth working
- [ ] **After Week 4**: Tenant isolation via JWT working
- [ ] **After Week 8**: Feature flags via Keycloak roles working
- [ ] **After Week 12**: Full modularization complete
- [ ] **After Week 16**: Production-ready security

This approach leverages your existing Keycloak expertise while providing a smooth path from simple authentication to full multi-tenant SaaS with feature flags.