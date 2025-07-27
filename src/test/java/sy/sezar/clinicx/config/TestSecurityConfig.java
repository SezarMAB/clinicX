package sy.sezar.clinicx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test configuration for mocking JWT tokens in tests.
 * This allows testing without a running Keycloak instance.
 */
@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> createMockJwt(token);
    }

    private Jwt createMockJwt(String token) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        
        // Parse simple test tokens like "admin", "doctor", "staff"
        String role = token.toUpperCase();
        String username = "test-" + token;
        String tenantId = "test-tenant";
        
        // Standard JWT claims
        claims.put("sub", username);
        claims.put("iss", "http://localhost:18081/realms/clinicx-dev");
        claims.put("preferred_username", username);
        claims.put("email", username + "@test.com");
        claims.put("email_verified", true);
        
        // Keycloak-specific claims
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of(role));
        claims.put("realm_access", realmAccess);
        
        // Tenant claim for multi-tenancy
        claims.put("tenant_id", tenantId);
        
        // Feature flags as client roles
        if ("ADMIN".equals(role)) {
            Map<String, Object> resourceAccess = new HashMap<>();
            Map<String, Object> clientRoles = new HashMap<>();
            clientRoles.put("roles", List.of(
                "FEATURE_DENTAL_MODULE",
                "FEATURE_LAB_REQUESTS",
                "FEATURE_ADVANCED_FINANCIAL"
            ));
            resourceAccess.put("clinicx-backend", clientRoles);
            claims.put("resource_access", resourceAccess);
        }

        return new Jwt(
            token,
            Instant.now(),
            Instant.now().plusSeconds(3600),
            headers,
            claims
        );
    }
}