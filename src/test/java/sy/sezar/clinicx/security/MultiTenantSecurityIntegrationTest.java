package sy.sezar.clinicx.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for multi-tenant security implementation.
 * Validates end-to-end security flow including role filtering and tenant isolation.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MultiTenantSecurityIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JwtDecoder jwtDecoder;
    
    private static final String TENANT_A = "tenant-a";
    private static final String TENANT_B = "tenant-b";
    
    @BeforeEach
    void setUp() {
        // Reset any static state
        sy.sezar.clinicx.tenant.TenantContext.clear();
    }
    
    @Test
    @DisplayName("Admin in tenant-a should NOT have admin access in tenant-b")
    void testCrossTenantAdminAccessDenied() throws Exception {
        // Given: User is ADMIN in tenant-a but DOCTOR in tenant-b
        Jwt jwt = createMultiTenantJwt(
            Map.of(
                TENANT_A, List.of("ADMIN", "DOCTOR"),
                TENANT_B, List.of("DOCTOR")
            )
        );
        
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        
        // When: User tries to access admin endpoint in tenant-b
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_B))
            // Then: Access should be denied (403)
            .andExpect(status().isForbidden());
        
        // But: Same user can access admin endpoint in tenant-a
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_A))
            // Then: Access should be allowed
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("User without access to tenant should be rejected")
    void testUnauthorizedTenantAccess() throws Exception {
        // Given: User has access only to tenant-a
        Jwt jwt = createMultiTenantJwt(
            Map.of(TENANT_A, List.of("DOCTOR"))
        );
        
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        
        // When: User tries to access tenant-b (no access)
        mockMvc.perform(get("/api/v1/patients")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_B))
            // Then: Access should be denied
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Access denied for tenant"));
    }
    
    @Test
    @DisplayName("Tenant context should be properly set from header")
    void testTenantContextFromHeader() throws Exception {
        // Given: Valid JWT with tenant access
        Jwt initialJwt = createMultiTenantJwt(
            Map.of(TENANT_A, List.of("DOCTOR"))
        );
        
        // Create new JWT with accessible_tenants claim
        Jwt jwt = Jwt.withTokenValue(initialJwt.getTokenValue())
            .headers(headers -> initialJwt.getHeaders().forEach(headers::put))
            .claims(claims -> {
                initialJwt.getClaims().forEach(claims::put);
                claims.put("accessible_tenants", List.of(TENANT_A));
            })
            .issuedAt(initialJwt.getIssuedAt())
            .expiresAt(initialJwt.getExpiresAt())
            .build();
        
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        
        // When: Request includes tenant header
        MvcResult result = mockMvc.perform(get("/api/auth/test/tenant-context")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_A))
            .andExpect(status().isOk())
            .andReturn();
        
        // Then: Response should confirm correct tenant context
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains(TENANT_A);
    }
    
    @Test
    @DisplayName("Global roles should work across all tenants")
    void testGlobalRoleAccess() throws Exception {
        // Given: User has GLOBAL_SUPPORT role
        Map<String, Object> realmAccess = Map.of(
            "roles", List.of("GLOBAL_SUPPORT")
        );
        
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "support-user")
            .claim("preferred_username", "support")
            .claim("realm_access", realmAccess)
            .claim("user_tenant_roles", Map.of(
                TENANT_A, List.of("VIEWER"),
                TENANT_B, List.of("VIEWER")
            ))
            .claim("accessible_tenants", List.of(TENANT_A, TENANT_B))
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        
        // When: Global support accesses tenant-a
        mockMvc.perform(get("/api/support/diagnostics")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_A))
            .andExpect(status().isOk());
        
        // And: Global support accesses tenant-b
        mockMvc.perform(get("/api/support/diagnostics")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_B))
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("Tenant switching should update authorities correctly")
    void testTenantSwitching() throws Exception {
        // Given: User with different roles in different tenants
        Jwt jwt = createMultiTenantJwt(
            Map.of(
                TENANT_A, List.of("ADMIN"),
                TENANT_B, List.of("STAFF")
            )
        );
        
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        
        // Scenario 1: Access as ADMIN in tenant-a
        mockMvc.perform(post("/api/v1/tenant-switch/switch")
                .header("Authorization", "Bearer token")
                .param("tenantId", TENANT_A))
            .andExpect(status().isOk());
        
        // Can access admin endpoints in tenant-a
        mockMvc.perform(get("/api/admin/settings")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_A))
            .andExpect(status().isOk());
        
        // Scenario 2: Switch to tenant-b as STAFF
        mockMvc.perform(post("/api/v1/tenant-switch/switch")
                .header("Authorization", "Bearer token")
                .param("tenantId", TENANT_B))
            .andExpect(status().isOk());
        
        // Cannot access admin endpoints in tenant-b
        mockMvc.perform(get("/api/admin/settings")
                .header("Authorization", "Bearer token")
                .header("X-Tenant-ID", TENANT_B))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("Missing tenant context should fail for tenant-required endpoints")
    void testMissingTenantContext() throws Exception {
        // Given: Valid JWT
        Jwt jwt = createMultiTenantJwt(
            Map.of(TENANT_A, List.of("DOCTOR"))
        );
        
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        
        // When: No tenant header provided for tenant-required endpoint
        mockMvc.perform(get("/api/v1/patients")
                .header("Authorization", "Bearer token"))
            // Then: Should return bad request
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Tenant context required"));
    }
    
    @Test
    @DisplayName("Public endpoints should not require tenant context")
    void testPublicEndpointsNoTenant() throws Exception {
        // When: Accessing public endpoint without authentication
        mockMvc.perform(get("/api/public/health"))
            .andExpect(status().isOk());
        
        // And: Accessing actuator health
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }
    
    // Helper methods
    
    private Jwt createMultiTenantJwt(Map<String, List<String>> tenantRoles) {
        return Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("preferred_username", "john.doe")
            .claim("user_tenant_roles", tenantRoles)
            .claim("accessible_tenants", new ArrayList<>(tenantRoles.keySet()))
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
    }
}