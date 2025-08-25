# Keycloak Mapper Frontend Freeze Issue - Updated Solution Guide for ClinicX
accessible_tenants
clinic_type
specialty
tenant_id
user_tenant_roles
clinic name
active_tenant_id
## ⚠️ Important Update (Post-Commit 78088eb)

The `TenantAwareJwtAuthoritiesConverter` class has been removed in the latest commit, significantly simplifying the solution:
- **Before**: Complex JSON claims were required for backend authorization
- **After**: Standard Keycloak realm roles are sufficient
- **Impact**: We can now safely remove ALL complex JSON mappers without breaking backend authorization

## Executive Summary

Based on analysis of the ClinicX codebase, the frontend freeze issue occurs when complex JSON mappers (`user_tenant_roles`, `accessible_tenants`) in JWT tokens cause parsing failures in the Angular application. With the removal of `TenantAwareJwtAuthoritiesConverter`, the solution is now much simpler.

## Current Implementation Analysis

### Backend Components Affected

#### 1. KeycloakAdminServiceImpl
- **Location**: `clinicX/src/main/java/sy/sezar/clinicx/tenant/service/impl/KeycloakAdminServiceImpl.java:449-476`
- **Issue**: Configures complex JSON mappers for both frontend and backend clients
- **Critical Lines**: 
  - Line 453-458: `accessible_tenants` mapper with String type (JSON content)
  - Line 470-476: `user_tenant_roles` mapper with String type (JSON content)

#### 2. KeycloakJwtGrantedAuthoritiesConverter (Current Implementation)
- **Location**: `clinicX/src/main/java/sy/sezar/clinicx/core/security/KeycloakJwtGrantedAuthoritiesConverter.java`
- **Current Behavior**: 
  - Extracts roles from `realm_access` and `resource_access` claims
  - Processes standard Keycloak role structures
  - Maps roles to Spring Security authorities
- **Note**: The complex `TenantAwareJwtAuthoritiesConverter` has been removed in commit 78088eb

#### 3. TenantSwitchingServiceImpl
- **Location**: `clinicX/src/main/java/sy/sezar/clinicx/tenant/service/impl/TenantSwitchingServiceImpl.java`
- **Current Behavior**: Processes tenant data from JWT claims
- **Risk**: Expects complex JSON structures in tokens

#### 4. MultiTenantJwtDecoder
- **Location**: `clinicX/src/main/java/sy/sezar/clinicx/core/security/MultiTenantJwtDecoder.java`
- **Current Processing**: Extracts `tenant_id` from JWT claims
- **Good Practice**: Only processes simple string claims

## Root Cause in ClinicX Context

### Token Size Analysis
```yaml
Current Token Structure:
  - tenant_id: String (50 bytes)
  - clinic_name: String (100 bytes)
  - clinic_type: String (50 bytes)
  - active_tenant_id: String (50 bytes)
  - accessible_tenants: JSON Array (2-10KB) ⚠️ PROBLEM FOR FRONTEND
  - user_tenant_roles: JSON Object (1-5KB) ⚠️ PROBLEM FOR FRONTEND, REQUIRED FOR BACKEND
  
Total Token Size: 3-15KB (exceeds recommended 4KB limit)
```

### Update After Recent Changes (Commit 78088eb)
The `TenantAwareJwtAuthoritiesConverter` has been removed, simplifying the architecture:
- Backend now uses standard `KeycloakJwtGrantedAuthoritiesConverter`
- Complex JSON claims (`user_tenant_roles`, `accessible_tenants`) are no longer required for authorization
- This makes the solution simpler - we can now safely remove these complex claims from tokens

### Frontend Impact Points
1. **Token Storage**: Browser localStorage/sessionStorage limitations
2. **Token Parsing**: JSON.parse() failures on pre-parsed objects
3. **Token Refresh**: Infinite loops when parsing fails

## Immediate Solution for ClinicX

### Simplified Solution Strategy

With the removal of `TenantAwareJwtAuthoritiesConverter`, we can now:
1. Remove complex JSON claims from ALL tokens
2. Use standard Keycloak realm roles for authorization
3. Fetch tenant data via API endpoints instead of tokens

### Step 1: Remove Complex Mappers [CRITICAL]

**Current Problem**: The `configureProtocolMappers()` method in `KeycloakAdminServiceImpl` (lines 451-476) adds complex JSON mappers to BOTH frontend and backend clients:
- Line 451-458: `accessible_tenants` mapper (JSON)
- Line 470-476: `user_tenant_roles` mapper (JSON)

Update `KeycloakAdminServiceImpl.configureProtocolMappers()`:

```java
private void configureProtocolMappers(String realmName, String clientId) {
    try {
        RealmResource realmResource = getKeycloakInstance().realm(realmName);
        List<ProtocolMapperRepresentation> mappers = new ArrayList<>();

        // Only add simple string mappers - NO JSON
        // 1. Tenant ID Mapper
        mappers.add(createUserAttributeMapper(
            "tenant_id", "tenant_id", "tenant_id",
            "String", true, true, true
        ));

        // 2. Active Tenant ID Mapper
        mappers.add(createUserAttributeMapper(
            "active_tenant_id", "active_tenant_id", "active_tenant_id",
            "String", false, true, true
        ));

        // 3. Clinic Name Mapper
        mappers.add(createUserAttributeMapper(
            "clinic_name", "clinic_name", "clinic_name",
            "String", true, true, true
        ));

        // 4. Clinic Type Mapper
        mappers.add(createUserAttributeMapper(
            "clinic_type", "clinic_type", "clinic_type",
            "String", true, true, true
        ));

        // 5. Specialty Mapper
        mappers.add(createUserAttributeMapper(
            "specialty", "clinic_type", "specialty",
            "String", false, true, true
        ));

        // DO NOT ADD accessible_tenants or user_tenant_roles
        // These complex JSON mappers cause the frontend freeze

        // Add mappers to the specific client
        List<ClientRepresentation> clients = realmResource.clients().findByClientId(clientId);
        if (!clients.isEmpty()) {
            String internalClientId = clients.get(0).getId();
            ClientResource clientResource = realmResource.clients().get(internalClientId);
            
            // Get existing mappers to avoid duplicates
            List<ProtocolMapperRepresentation> existingMappers = 
                clientResource.getProtocolMappers().getMappers();
            Set<String> existingMapperNames = existingMappers.stream()
                .map(ProtocolMapperRepresentation::getName)
                .collect(Collectors.toSet());
            
            // Add only new mappers
            for (ProtocolMapperRepresentation mapper : mappers) {
                if (!existingMapperNames.contains(mapper.getName())) {
                    clientResource.getProtocolMappers().createMapper(mapper);
                    log.info("Added mapper '{}' to client: {}", 
                        mapper.getName(), clientId);
                }
            }
        }
        
    } catch (Exception e) {
        log.error("Failed to configure protocol mappers", e);
        throw new BusinessRuleException("Failed to configure mappers: " + e.getMessage());
    }
}
```

### Step 2: Create API Endpoints for Tenant Data

Create new controller for frontend to fetch tenant data:

```java
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserTenantDataController {
    
    private final TenantSwitchingService tenantSwitchingService;
    private final TenantAccessValidator tenantAccessValidator;
    
    /**
     * Frontend fetches tenant data via API instead of from token
     */
    @GetMapping("/tenant-access")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserTenantAccessResponse> getUserTenantAccess() {
        String userId = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        List<TenantAccessDto> accessibleTenants = 
            tenantSwitchingService.getCurrentUserTenants();
        
        Map<String, List<String>> userTenantRoles = 
            extractUserTenantRoles(userId);
        
        return ResponseEntity.ok(UserTenantAccessResponse.builder()
            .userId(userId)
            .accessibleTenants(accessibleTenants)
            .userTenantRoles(userTenantRoles)
            .build());
    }
    
    /**
     * Get roles for current active tenant
     */
    @GetMapping("/current-roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getCurrentTenantRoles() {
        String tenantId = TenantContext.getCurrentTenant();
        String userId = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        List<String> roles = getRolesForTenant(userId, tenantId);
        return ResponseEntity.ok(roles);
    }
}
```

### Step 3: Fix TenantSwitchingServiceImpl 

**Critical Issue**: The `syncUserTenantsToKeycloak()` method (lines 383-437) is still creating the problematic JSON claims!

Update the service to STOP syncing complex JSON to Keycloak:

```java
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TenantSwitchingServiceImpl implements TenantSwitchingService {
    
    @Override
    public TenantSwitchResponseDto switchTenant(String tenantId) {
        String userId = getCurrentUserId();
        
        // Validate access from database
        if (!tenantAccessValidator.validateUserAccess(userId, tenantId)) {
            throw new BusinessRuleException("Access denied to tenant: " + tenantId);
        }
        
        // Update ONLY active_tenant_id - no complex JSON
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("active_tenant_id", List.of(tenantId));
        
        keycloakAdminService.updateUserAttributes(
            getCurrentRealm(), userId, attributes
        );
        
        return TenantSwitchResponseDto.builder()
            .tenantId(tenantId)
            .message("Switched successfully")
            .requiresRefresh(true)
            .build();
    }
    
    @Override
    public List<TenantAccessDto> getCurrentUserTenants() {
        String userId = getCurrentUserId();
        
        // REMOVE the automatic sync to Keycloak (lines 53-68)
        // This was creating the problematic JSON claims
        
        List<Staff> staffList = staffRepository
            .findByKeycloakUserIdAndIsActiveIsTrue(userId);
        
        return staffList.stream()
            .filter(staff -> tenantAccessValidator
                .validateUserAccess(userId, staff.getTenantId()))
            .map(this::mapToTenantAccessDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated // This method should NOT be used
    public void syncUserTenantsToKeycloak(String userId, String realmName, String username) {
        // DO NOT sync complex JSON to Keycloak
        // This method creates the problematic claims that freeze the frontend
        log.warn("syncUserTenantsToKeycloak called but should not be used - skipping");
    }
}
```

### Step 4: Frontend Angular Updates

#### Update Auth Service
```typescript
// src/app/core/authentication/auth.service.ts

@Injectable({ providedIn: 'root' })
export class AuthService {
  
  private tenantDataCache$ = new BehaviorSubject<UserTenantData | null>(null);
  
  /**
   * Initialize auth without parsing complex claims
   */
  initializeAuth(): Observable<void> {
    return this.keycloakService.init().pipe(
      switchMap(() => {
        // Only parse simple claims from token
        const token = this.keycloakService.getToken();
        const simpleClaims = this.parseSimpleClaims(token);
        
        // Store simple user info
        this.currentUser$.next({
          id: simpleClaims.sub,
          email: simpleClaims.email,
          tenantId: simpleClaims.tenant_id,
          activeTenantId: simpleClaims.active_tenant_id
        });
        
        // Fetch complex data from API
        return this.loadTenantData();
      }),
      map(() => void 0)
    );
  }
  
  /**
   * Parse only simple string claims
   */
  private parseSimpleClaims(token: string): SimpleClaims {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return {
        sub: payload.sub,
        email: payload.email,
        tenant_id: payload.tenant_id,
        active_tenant_id: payload.active_tenant_id,
        clinic_name: payload.clinic_name
      };
    } catch (error) {
      console.error('Failed to parse token:', error);
      return {} as SimpleClaims;
    }
  }
  
  /**
   * Load tenant data from API
   */
  private loadTenantData(): Observable<void> {
    return this.http.get<UserTenantData>('/api/v1/user/tenant-access').pipe(
      tap(data => this.tenantDataCache$.next(data)),
      map(() => void 0),
      catchError(error => {
        console.error('Failed to load tenant data:', error);
        return of(void 0);
      })
    );
  }
  
  /**
   * Get accessible tenants from cache
   */
  getAccessibleTenants(): Observable<TenantAccess[]> {
    return this.tenantDataCache$.pipe(
      map(data => data?.accessibleTenants || [])
    );
  }
  
  /**
   * Switch tenant
   */
  switchTenant(tenantId: string): Observable<void> {
    return this.http.post<TenantSwitchResponse>(
      `/api/v1/tenant-switch/${tenantId}`, {}
    ).pipe(
      switchMap(response => {
        if (response.requiresRefresh) {
          // Refresh token to get new active_tenant_id
          return this.keycloakService.updateToken(30);
        }
        return of(void 0);
      }),
      switchMap(() => this.loadTenantData()),
      map(() => void 0)
    );
  }
}
```

## Database Schema Updates

### Add Caching Table for Tenant Access
```sql
-- Create table for caching tenant access data
CREATE TABLE user_tenant_cache (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    tenant_data JSONB NOT NULL,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    UNIQUE(user_id)
);

-- Index for quick lookups
CREATE INDEX idx_user_tenant_cache_user_id ON user_tenant_cache(user_id);
CREATE INDEX idx_user_tenant_cache_expires ON user_tenant_cache(expires_at);

-- Cleanup job for expired cache
CREATE OR REPLACE FUNCTION cleanup_expired_tenant_cache()
RETURNS void AS $$
BEGIN
    DELETE FROM user_tenant_cache WHERE expires_at < CURRENT_TIMESTAMP;
END;
$$ LANGUAGE plpgsql;
```

## Testing Strategy

### 1. Unit Tests
```java
@Test
public void testFrontendTokenSize() {
    // Create test user with multiple tenants
    String token = generateTokenForUser(testUser, "clinicx-frontend");
    
    // Verify token size
    int tokenSizeKB = token.getBytes().length / 1024;
    assertTrue(tokenSizeKB < 4, "Frontend token exceeds 4KB limit");
    
    // Verify no complex claims
    Jwt jwt = jwtDecoder.decode(token);
    assertNull(jwt.getClaim("accessible_tenants"));
    assertNull(jwt.getClaim("user_tenant_roles"));
}

@Test
public void testBackendTokenContainsComplexClaims() {
    String token = generateTokenForUser(testUser, "clinicx-backend");
    Jwt jwt = jwtDecoder.decode(token);
    
    assertNotNull(jwt.getClaim("accessible_tenants"));
    assertNotNull(jwt.getClaim("user_tenant_roles"));
}
```

### 2. Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
public class TenantAccessIntegrationTest {
    
    @Test
    public void testTenantDataAPIEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/user/tenant-access")
            .header("Authorization", "Bearer " + validToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessibleTenants").isArray())
            .andExpect(jsonPath("$.userTenantRoles").isMap());
    }
    
    @Test
    public void testTenantSwitchWithoutComplexClaims() throws Exception {
        mockMvc.perform(post("/api/v1/tenant-switch/tenant-123")
            .header("Authorization", "Bearer " + minimalToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requiresRefresh").value(true));
    }
}
```

## Migration Plan

### Phase 1: Backend Changes (Day 1)
1. Deploy updated `KeycloakAdminServiceImpl`
2. Add new API endpoints for tenant data
3. Update `TenantSwitchingServiceImpl`

### Phase 2: Keycloak Configuration (Day 2)
1. Remove complex mappers from frontend client
2. Keep complex mappers only on backend client
3. Clear Keycloak cache

### Phase 3: Frontend Deployment (Day 3)
1. Deploy updated Angular auth service
2. Test with small user group
3. Monitor for freeze issues

### Phase 4: Cleanup (Day 4)
1. Remove old token parsing code
2. Update documentation
3. Performance testing

## Monitoring & Metrics

### Key Metrics to Track
```java
@Component
public class TokenMetricsCollector {
    
    private final MeterRegistry registry;
    
    public void recordTokenSize(String clientType, int sizeBytes) {
        registry.gauge("jwt.token.size", 
            Tags.of("client", clientType), sizeBytes);
    }
    
    public void recordParseError(String errorType) {
        registry.counter("jwt.parse.errors", 
            Tags.of("type", errorType)).increment();
    }
    
    public void recordApiCallLatency(String endpoint, long latencyMs) {
        registry.timer("api.tenant.data.latency", 
            Tags.of("endpoint", endpoint))
            .record(latencyMs, TimeUnit.MILLISECONDS);
    }
}
```

### Alert Thresholds
- Frontend token size > 3KB: WARNING
- Frontend token size > 4KB: CRITICAL
- Token parse errors > 10/min: WARNING
- API latency > 500ms: WARNING

## Rollback Procedure

If issues occur after deployment:

```bash
# 1. Revert Keycloak mapper configuration
kubectl exec -it keycloak-pod -- /opt/keycloak/bin/kcadm.sh \
  update clients/{client-id}/protocol-mappers/{mapper-id} \
  -r {realm} \
  --merge -f original-mapper-config.json

# 2. Revert backend code
git revert --no-commit HEAD~3..HEAD
git commit -m "Revert: Token optimization changes"

# 3. Clear all caches
kubectl exec -it app-pod -- curl -X POST http://localhost:8080/actuator/cache/clear

# 4. Force token refresh for all users
UPDATE user_sessions SET force_refresh = true WHERE created_at > NOW() - INTERVAL '7 days';
```

## Long-term Architecture Improvements

### 1. Implement Token Exchange Pattern
```mermaid
sequenceDiagram
    participant Frontend
    participant Gateway
    participant Backend
    participant Keycloak
    
    Frontend->>Gateway: Simple token
    Gateway->>Keycloak: Exchange for backend token
    Keycloak-->>Gateway: Complex token
    Gateway->>Backend: Complex token
    Backend-->>Gateway: Response
    Gateway-->>Frontend: Response
```

### 2. Use Redis for Tenant Data Caching
```java
@Configuration
public class TenantCacheConfig {
    
    @Bean
    public RedisTemplate<String, UserTenantData> tenantDataRedisTemplate() {
        RedisTemplate<String, UserTenantData> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserTenantData.class));
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    
    @Bean
    public RedisCacheManager tenantCacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory)
            .cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(15))
                    .serializeValuesWith(
                        SerializationPair.fromSerializer(
                            new GenericJackson2JsonRedisSerializer()
                        )
                    )
            )
            .build();
    }
}
```

## Important Security Considerations

### Simplified Backend Authorization (Post-Revert)
After removing `TenantAwareJwtAuthoritiesConverter` (commit 78088eb), the backend now uses:
- **KeycloakJwtGrantedAuthoritiesConverter**: Extracts roles from standard Keycloak claims
- **Realm roles**: Authorization based on realm_access and resource_access claims
- **No complex JSON claims required**: Simplifies token structure and prevents frontend freeze

### Simplified Token Flow
```mermaid
sequenceDiagram
    participant Frontend
    participant Backend
    participant Keycloak
    
    Frontend->>Keycloak: Login (clinicx-frontend client)
    Keycloak-->>Frontend: Simple token (only basic claims)
    Frontend->>Backend: API call with token
    Backend->>Backend: KeycloakJwtGrantedAuthoritiesConverter extracts roles
    Backend->>Backend: Authorize based on realm roles
    Backend-->>Frontend: Response
    
    Note over Frontend: Fetch tenant data via API if needed
    Frontend->>Backend: GET /api/v1/user/tenant-access
    Backend-->>Frontend: Tenant data (from database)
```

## Success Metrics

### Week 1
- [ ] Zero frontend freeze incidents
- [ ] Frontend token size < 2KB average
- [ ] Backend authorization still working correctly
- [ ] API response time < 200ms p95

### Week 2
- [ ] 100% successful tenant switches
- [ ] Zero token parse errors
- [ ] No security vulnerabilities introduced
- [ ] User satisfaction score > 4.5/5

### Month 1
- [ ] 50% reduction in auth-related support tickets
- [ ] 99.9% authentication availability
- [ ] Complete migration of all users
- [ ] Security audit passed

## Support Resources

### Documentation
- [Keycloak 26 User Profile Configuration](https://www.keycloak.org/docs/latest/server_admin/#user-profile)
- [Spring Security JWT Processing](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Angular Token Management Best Practices](https://angular.io/guide/security)

### Contact Points
- **Backend Team**: For API endpoint issues
- **DevOps Team**: For Keycloak configuration
- **Frontend Team**: For Angular implementation
- **Security Team**: For token security review

## Implementation Checklist (Post-Revert)

### Immediate Actions Required

1. **Fix KeycloakAdminServiceImpl.java**
   - [ ] Remove lines 451-458 (`accessible_tenants` mapper)
   - [ ] Remove lines 470-476 (`user_tenant_roles` mapper)
   - [ ] Keep only simple string mappers

2. **Fix TenantSwitchingServiceImpl.java**
   - [ ] Remove automatic sync in `getCurrentUserTenants()` (lines 53-68)
   - [ ] Deprecate or remove `syncUserTenantsToKeycloak()` method (lines 383-437)
   - [ ] Update `switchTenant()` to only update `active_tenant_id`

3. **Clean Existing Keycloak Realms**
   ```bash
   # Remove problematic mappers from existing clients
   kcadm.sh delete clients/{client-id}/protocol-mappers/accessible_tenants -r {realm}
   kcadm.sh delete clients/{client-id}/protocol-mappers/user_tenant_roles -r {realm}
   ```

4. **Update User Attributes**
   ```bash
   # Remove JSON attributes from existing users
   kcadm.sh update users/{user-id} -r {realm} \
     -s 'attributes.accessible_tenants=' \
     -s 'attributes.user_tenant_roles='
   ```

5. **Create API Endpoints**
   - [ ] Implement `/api/v1/user/tenant-access` endpoint
   - [ ] Implement `/api/v1/user/current-roles` endpoint

6. **Update Frontend**
   - [ ] Remove JSON parsing of complex claims
   - [ ] Fetch tenant data via API instead of from token

### Verification Steps

1. **Check Token Size**
   ```bash
   # Should be < 2KB
   curl -s https://keycloak/realms/{realm}/protocol/openid-connect/token \
     -d "client_id=clinicx-frontend" \
     -d "username=testuser" \
     -d "password=****" \
     -d "grant_type=password" | jq -r '.access_token' | wc -c
   ```

2. **Verify No Complex Claims**
   ```bash
   # Should NOT contain accessible_tenants or user_tenant_roles
   curl -s https://keycloak/realms/{realm}/protocol/openid-connect/token \
     -d "client_id=clinicx-frontend" \
     -d "username=testuser" \
     -d "password=****" \
     -d "grant_type=password" | jq -r '.access_token' | \
     awk -F. '{print $2}' | base64 -d | jq
   ```

3. **Test Frontend**
   - [ ] Login works without freeze
   - [ ] Page reload works without freeze
   - [ ] Tenant switching works correctly
   - [ ] Authorization still works with realm roles

## Appendix: Emergency Commands

```bash
# Check current token size for a user
curl -s https://keycloak/realms/{realm}/protocol/openid-connect/token \
  -d "client_id=clinicx-frontend" \
  -d "username=testuser" \
  -d "password=****" \
  -d "grant_type=password" | jq -r '.access_token' | wc -c

# Remove mapper from frontend client
kcadm.sh delete clients/{client-id}/protocol-mappers/{mapper-id} -r {realm}

# Clear user sessions
kcadm.sh create clear-user-sessions -r {realm} -s userId={user-id}

# Monitor token parse errors
kubectl logs -f deployment/clinicx-app | grep "JWT parse error"
```

---

*Document Version: 3.0*  
*Last Updated: 2025-01-18*  
*Post-Revert Update: Reflects removal of TenantAwareJwtAuthoritiesConverter*  
*Status: Ready for Implementation*  
*Author: ClinicX Architecture Team*
