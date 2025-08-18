package sy.sezar.clinicx.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.beans.factory.annotation.Value;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.security.TenantAccessDecisionVoter;
import sy.sezar.clinicx.tenant.security.TenantAuthorizationFilter;
import sy.sezar.clinicx.tenant.filter.TenantContextFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    
    @Value("${app.multi-tenant.enabled:true}")
    private boolean multiTenantEnabled;
    
    @Value("${keycloak.auth-server-url}")
    private String keycloakBaseUrl;
    
    @Value("${app.multi-tenant.default-realm:master}")
    private String defaultRealm;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private TenantAccessDecisionVoter tenantAccessDecisionVoter;
    
    @Autowired
    private TenantAuthorizationFilter tenantAuthorizationFilter;
    
    @Autowired
    private TenantContextFilter tenantContextFilter;
    
    @Autowired
    private TenantAwareJwtAuthoritiesConverter tenantAwareJwtAuthoritiesConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configure(http))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/auth/test/public").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Tenant management endpoints (super admin only)
                .requestMatchers("/api/tenants/**").hasRole("SUPER_ADMIN")
                
                // Tenant switching endpoint
                .requestMatchers("/api/v1/tenant-switch/**").authenticated()
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        // Add tenant filters if multi-tenant is enabled
        if (multiTenantEnabled) {
            // Add tenant context filter early to establish tenant context
            http.addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class);
            // Add tenant authorization filter for additional checks
            http.addFilterAfter(tenantAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        if (multiTenantEnabled) {
            MultiTenantJwtDecoder decoder = new MultiTenantJwtDecoder();
            decoder.setTenantRepository(tenantRepository);
            decoder.setKeycloakBaseUrl(keycloakBaseUrl);
            decoder.setMultiTenantEnabled(multiTenantEnabled);
            decoder.setDefaultRealm(defaultRealm);
            return decoder;
        }
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }

    /**
     * Custom JWT authentication converter that uses tenant-aware authority conversion.
     * This method directly returns a JwtAuthenticationToken with only the authorities
     * from our converter, ensuring no default role extraction occurs.
     */
    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        if (multiTenantEnabled) {
            // Direct conversion to JwtAuthenticationToken with tenant-aware authorities only
            return jwt -> {
                Collection<GrantedAuthority> authorities = tenantAwareJwtAuthoritiesConverter.convert(jwt);
                return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username"));
            };
        } else {
            // Single-tenant mode: use standard converter (backward compatibility)
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthoritiesConverter());
            converter.setPrincipalClaimName("preferred_username");
            return converter;
        }
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }
    
    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(
            new RoleVoter(),
            new AuthenticatedVoter(),
            tenantAccessDecisionVoter
        );
        return new AffirmativeBased(decisionVoters);
    }
}