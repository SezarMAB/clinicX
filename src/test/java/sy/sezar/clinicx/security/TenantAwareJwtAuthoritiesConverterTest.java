package sy.sezar.clinicx.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import sy.sezar.clinicx.core.security.TenantAwareJwtAuthoritiesConverter;
import sy.sezar.clinicx.tenant.TenantContext;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test suite for TenantAwareJwtAuthoritiesConverter.
 * Validates that roles are properly filtered based on tenant context.
 */
class TenantAwareJwtAuthoritiesConverterTest {
    
    private TenantAwareJwtAuthoritiesConverter converter;
    
    @BeforeEach
    void setUp() {
        converter = new TenantAwareJwtAuthoritiesConverter();
        TenantContext.clear();
    }
    
    @Test
    @DisplayName("Should only include roles for current tenant")
    void testTenantSpecificRoleFiltering() {
        // Given: User has ADMIN in tenant-a and DOCTOR in tenant-b
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN", "DOCTOR"));
        userTenantRoles.put("tenant-b", Arrays.asList("DOCTOR"));
        
        Jwt jwt = createJwtWithTenantRoles(userTenantRoles);
        
        // When: Current tenant is tenant-b
        TenantContext.setCurrentTenant("tenant-b");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Only DOCTOR role should be present
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_DOCTOR");
        
        // And: ADMIN role should NOT be present
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .doesNotContain("ROLE_ADMIN");
    }
    
    @Test
    @DisplayName("Should prevent cross-tenant privilege escalation")
    void testPreventCrossTenantPrivilegeEscalation() {
        // Given: User is ADMIN in tenant-a but only STAFF in tenant-b
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN"));
        userTenantRoles.put("tenant-b", Arrays.asList("STAFF"));
        
        Jwt jwt = createJwtWithTenantRoles(userTenantRoles);
        
        // Scenario 1: Access tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authoritiesTenantA = converter.convert(jwt);
        
        assertThat(authoritiesTenantA)
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_ADMIN")
            .doesNotContain("ROLE_STAFF");
        
        // Scenario 2: Access tenant-b
        TenantContext.setCurrentTenant("tenant-b");
        Collection<GrantedAuthority> authoritiesTenantB = converter.convert(jwt);
        
        assertThat(authoritiesTenantB)
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_STAFF")
            .doesNotContain("ROLE_ADMIN");
    }
    
    @Test
    @DisplayName("Should include global roles regardless of tenant")
    void testGlobalRoleInclusion() {
        // Given: User has GLOBAL_SUPPORT role and tenant-specific roles
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("GLOBAL_SUPPORT", "ADMIN", "DOCTOR"));
        
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN"));
        
        Jwt jwt = createJwtWithRealmAndTenantRoles(realmAccess, userTenantRoles);
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should include GLOBAL_SUPPORT and tenant-specific ADMIN
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_GLOBAL_SUPPORT", "ROLE_ADMIN")
            .doesNotContain("ROLE_DOCTOR"); // Regular realm role should be excluded
    }
    
    @Test
    @DisplayName("Should handle missing tenant context gracefully")
    void testMissingTenantContext() {
        // Given: User has tenant roles but no tenant context is set
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN"));
        
        Jwt jwt = createJwtWithTenantRoles(userTenantRoles);
        
        // When: No tenant context is set
        TenantContext.clear();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should not include any tenant-specific roles
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .doesNotContain("ROLE_ADMIN");
    }
    
    @Test
    @DisplayName("Should handle user with no roles in current tenant")
    void testNoRolesInCurrentTenant() {
        // Given: User has roles in tenant-a but not in tenant-b
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN", "DOCTOR"));
        
        Jwt jwt = createJwtWithTenantRoles(userTenantRoles);
        
        // When: Current tenant is tenant-b (where user has no roles)
        TenantContext.setCurrentTenant("tenant-b");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should have no authorities
        assertThat(authorities).isEmpty();
    }
    
    @Test
    @DisplayName("Should parse JSON string claim for backward compatibility")
    void testJsonStringClaimParsing() {
        // Given: Tenant roles as JSON string (backward compatibility)
        String jsonRoles = "{\"tenant-a\":[\"ADMIN\"],\"tenant-b\":[\"DOCTOR\"]}";
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("user_tenant_roles", jsonRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should parse and include ADMIN role
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_ADMIN");
    }
    
    @Test
    @DisplayName("Should handle multiple roles per tenant")
    void testMultipleRolesPerTenant() {
        // Given: User has multiple roles in a tenant
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN", "DOCTOR", "STAFF"));
        
        Jwt jwt = createJwtWithTenantRoles(userTenantRoles);
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should include all roles for that tenant
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_DOCTOR", "ROLE_STAFF");
    }
    
    @Test
    @DisplayName("Should include OAuth2 scopes")
    void testOAuth2ScopeInclusion() {
        // Given: JWT with OAuth2 scopes
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("scope", "read write admin")
            .claim("user_tenant_roles", Map.of("tenant-a", List.of("ADMIN")))
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should include both role and scope authorities
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_ADMIN", "SCOPE_read", "SCOPE_write", "SCOPE_admin");
    }
    
    // Helper methods
    
    private Jwt createJwtWithTenantRoles(Map<String, List<String>> userTenantRoles) {
        return Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("preferred_username", "john.doe")
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
    }
    
    private Jwt createJwtWithRealmAndTenantRoles(Map<String, Object> realmAccess, 
                                                  Map<String, List<String>> userTenantRoles) {
        return Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("preferred_username", "john.doe")
            .claim("realm_access", realmAccess)
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
    }
}