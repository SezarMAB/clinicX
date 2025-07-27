package sy.sezar.clinicx.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

/**
 * Utility class for security-related operations.
 */
public final class SecurityUtils {
    
    private SecurityUtils() {
        // Utility class
    }
    
    /**
     * Get the current authenticated user's username.
     * 
     * @return Optional containing the username if authenticated
     */
    public static Optional<String> getCurrentUsername() {
        return getAuthentication()
            .map(Authentication::getName);
    }
    
    /**
     * Get the current JWT token.
     * 
     * @return Optional containing the JWT if present
     */
    public static Optional<Jwt> getCurrentJwt() {
        return getAuthentication()
            .filter(auth -> auth.getPrincipal() instanceof Jwt)
            .map(auth -> (Jwt) auth.getPrincipal());
    }
    
    /**
     * Get the current user's ID from the JWT subject claim.
     * 
     * @return Optional containing the user ID if present
     */
    public static Optional<String> getCurrentUserId() {
        return getCurrentJwt()
            .map(jwt -> jwt.getClaimAsString("sub"));
    }
    
    /**
     * Get the current tenant ID from the JWT.
     * 
     * @return Optional containing the tenant ID if present
     */
    public static Optional<String> getCurrentTenantId() {
        return getCurrentJwt()
            .map(jwt -> jwt.getClaimAsString("tenant_id"));
    }
    
    /**
     * Check if the current user has a specific role.
     * 
     * @param role the role to check (without ROLE_ prefix)
     * @return true if the user has the role
     */
    public static boolean hasRole(String role) {
        return getAuthentication()
            .map(auth -> auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)))
            .orElse(false);
    }
    
    /**
     * Check if the current user has a specific feature enabled.
     * 
     * @param feature the feature to check (e.g., "FEATURE_DENTAL_MODULE")
     * @return true if the user has the feature
     */
    public static boolean hasFeature(String feature) {
        return getAuthentication()
            .map(auth -> auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(feature)))
            .orElse(false);
    }
    
    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }
}