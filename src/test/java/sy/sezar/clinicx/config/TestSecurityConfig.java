package sy.sezar.clinicx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import sy.sezar.clinicx.core.security.KeycloakJwtGrantedAuthoritiesConverter;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test configuration for mocking JWT tokens in tests.
 * This allows testing without a running Keycloak instance.
 */
@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/test/public").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(testJwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> createMockJwt(token);
    }
    
    @Bean
    @Primary
    public JwtAuthenticationConverter testJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthoritiesConverter());
        converter.setPrincipalClaimName("preferred_username");
        return converter;
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
        claims.put("iss", "http://localhost:18081/realms/clinicx-test");
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