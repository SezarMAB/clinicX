package sy.sezar.clinicx.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for Spring Security operations.
 */
@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the current authenticated user's ID from the JWT token.
     * 
     * @return The user ID or null if not authenticated
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            // Try different possible claim names for user ID
            if (jwt.getClaim("sub") != null) {
                return jwt.getClaim("sub");
            }
            if (jwt.getClaim("user_id") != null) {
                return jwt.getClaim("user_id");
            }
            if (jwt.getClaim("preferred_username") != null) {
                return jwt.getClaim("preferred_username");
            }
        }
        
        return null;
    }

    /**
     * Get the current authenticated user's username.
     * 
     * @return The username or null if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            if (jwt.getClaim("preferred_username") != null) {
                return jwt.getClaim("preferred_username");
            }
            if (jwt.getClaim("name") != null) {
                return jwt.getClaim("name");
            }
        }
        
        return null;
    }

    /**
     * Check if the current user has a specific role.
     * 
     * @param role The role to check (without ROLE_ prefix)
     * @return true if the user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getAuthorities() != null) {
            String roleWithPrefix = "ROLE_" + role;
            return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleWithPrefix));
        }
        
        return false;
    }

    /**
     * Check if a user is authenticated.
     * 
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Get the current tenant ID from the JWT token.
     * 
     * @return The tenant ID or null if not present
     */
    public static String getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            if (jwt.getClaim("tenant_id") != null) {
                return jwt.getClaim("tenant_id");
            }
            if (jwt.getClaim("tenantId") != null) {
                return jwt.getClaim("tenantId");
            }
        }
        
        return null;
    }
}