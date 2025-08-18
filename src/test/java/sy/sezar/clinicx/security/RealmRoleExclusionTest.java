package sy.sezar.clinicx.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import sy.sezar.clinicx.core.security.TenantAwareJwtAuthoritiesConverter;
import sy.sezar.clinicx.tenant.TenantContext;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite to verify that realm roles are completely excluded from authorization
 * except for explicit GLOBAL_* roles. This ensures no cross-tenant privilege leakage.
 */
class RealmRoleExclusionTest {
    
    private TenantAwareJwtAuthoritiesConverter converter;
    
    @BeforeEach
    void setUp() {
        converter = new TenantAwareJwtAuthoritiesConverter();
        TenantContext.clear();
    }
    
    @Test
    @DisplayName("Should completely ignore non-global realm roles")
    void testRealmRolesAreIgnored() {
        // Given: JWT with realm roles including ADMIN and DOCTOR
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ADMIN", "DOCTOR", "SUPER_ADMIN"));
        
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("VIEWER"));  // Only VIEWER in tenant-a
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("preferred_username", "john.doe")
            .claim("realm_access", realmAccess)
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: ONLY tenant-specific VIEWER role should be present
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_VIEWER")
            .doesNotContain("ROLE_ADMIN", "ROLE_DOCTOR", "ROLE_SUPER_ADMIN");
    }
    
    @Test
    @DisplayName("Should allow only GLOBAL_ prefixed realm roles")
    void testOnlyGlobalRolesAllowed() {
        // Given: JWT with mixed realm roles
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList(
            "ADMIN",           // Should be ignored
            "GLOBAL_SUPPORT",  // Should be included
            "DOCTOR",          // Should be ignored
            "GLOBAL_ADMIN",    // Should be included
            "SUPER_ADMIN"      // Should be ignored (not GLOBAL_ prefix)
        ));
        
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("STAFF"));
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("realm_access", realmAccess)
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Only GLOBAL_* and tenant-specific roles should be present
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder(
                "ROLE_GLOBAL_SUPPORT",
                "ROLE_GLOBAL_ADMIN",
                "ROLE_STAFF"
            )
            .doesNotContain("ROLE_ADMIN", "ROLE_DOCTOR", "ROLE_SUPER_ADMIN");
    }
    
    @Test
    @DisplayName("Resource/client roles should be completely ignored")
    void testResourceAccessIgnored() {
        // Given: JWT with resource_access roles
        Map<String, Object> clientRoles = new HashMap<>();
        clientRoles.put("roles", Arrays.asList("CLIENT_ADMIN", "CLIENT_USER"));
        
        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("clinicx-backend", clientRoles);
        resourceAccess.put("account", Map.of("roles", Arrays.asList("manage-account", "view-profile")));
        
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("DOCTOR"));
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("resource_access", resourceAccess)  // These should be completely ignored
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Only tenant-specific role should be present
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_DOCTOR")
            .doesNotContain("ROLE_CLIENT_ADMIN", "ROLE_CLIENT_USER", 
                          "ROLE_MANAGE_ACCOUNT", "ROLE_VIEW_PROFILE");
    }
    
    @Test
    @DisplayName("Should prevent privilege escalation via realm roles")
    void testPreventPrivilegeEscalation() {
        // Given: User has ADMIN in realm but not in current tenant
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ADMIN", "SUPER_ADMIN"));
        
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN"));  // ADMIN in tenant-a
        userTenantRoles.put("tenant-b", Arrays.asList("STAFF"));  // Only STAFF in tenant-b
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("realm_access", realmAccess)
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // Scenario 1: Access tenant-b where user is only STAFF
        TenantContext.setCurrentTenant("tenant-b");
        Collection<GrantedAuthority> authoritiesTenantB = converter.convert(jwt);
        
        // Then: Should NOT have ADMIN despite realm role
        assertThat(authoritiesTenantB)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_STAFF")
            .doesNotContain("ROLE_ADMIN", "ROLE_SUPER_ADMIN");
        
        // Scenario 2: Access tenant-a where user is ADMIN
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authoritiesTenantA = converter.convert(jwt);
        
        // Then: Should have ADMIN from tenant roles, not from realm
        assertThat(authoritiesTenantA)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN")
            .doesNotContain("ROLE_SUPER_ADMIN");
    }
    
    @Test
    @DisplayName("Empty realm roles should not affect tenant roles")
    void testEmptyRealmRoles() {
        // Given: JWT with empty or missing realm_access
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-a", Arrays.asList("ADMIN", "DOCTOR"));
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            // No realm_access claim at all
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Current tenant is tenant-a
        TenantContext.setCurrentTenant("tenant-a");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Tenant roles should work normally
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_DOCTOR");
    }
    
    @Test
    @DisplayName("Verify no SimpleGrantedAuthority created from non-GLOBAL realm roles")
    void testNoAuthoritiesFromNonGlobalRealmRoles() {
        // Given: JWT with only non-GLOBAL realm roles
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ADMIN", "DOCTOR", "MANAGER", "SUPER_ADMIN"));
        
        // No tenant roles at all
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("realm_access", realmAccess)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Tenant context is set
        TenantContext.setCurrentTenant("any-tenant");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should have NO authorities since no GLOBAL_ roles and no tenant roles
        assertThat(authorities).isEmpty();
        
        // Verify each non-GLOBAL role is not present
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .doesNotContain(
                "ROLE_ADMIN",
                "ROLE_DOCTOR",
                "ROLE_MANAGER",
                "ROLE_SUPER_ADMIN"
            );
    }
    
    @Test
    @DisplayName("Mixed scenario: Global roles + tenant roles, no realm role leakage")
    void testMixedScenarioWithGlobalAndTenantRoles() {
        // Given: Complex JWT with all types of roles
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList(
            "ADMIN",              // Ignored
            "GLOBAL_SUPPORT",     // Included
            "SUPER_ADMIN",        // Ignored
            "GLOBAL_READONLY"     // Included
        ));
        
        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("backend", Map.of("roles", Arrays.asList("BACKEND_ADMIN")));  // Ignored
        
        Map<String, List<String>> userTenantRoles = new HashMap<>();
        userTenantRoles.put("tenant-x", Arrays.asList("VIEWER"));
        userTenantRoles.put("tenant-y", Arrays.asList("EDITOR", "REVIEWER"));
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("realm_access", realmAccess)
            .claim("resource_access", resourceAccess)
            .claim("user_tenant_roles", userTenantRoles)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        // When: Access tenant-y
        TenantContext.setCurrentTenant("tenant-y");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        
        // Then: Should have ONLY global roles and tenant-y roles
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder(
                "ROLE_GLOBAL_SUPPORT",   // Global role
                "ROLE_GLOBAL_READONLY",   // Global role
                "ROLE_EDITOR",            // Tenant-y role
                "ROLE_REVIEWER"           // Tenant-y role
            )
            .doesNotContain(
                "ROLE_ADMIN",           // Realm role (ignored)
                "ROLE_SUPER_ADMIN",     // Realm role (ignored)
                "ROLE_BACKEND_ADMIN",   // Resource role (ignored)
                "ROLE_VIEWER"           // Tenant-x role (different tenant)
            );
    }
}