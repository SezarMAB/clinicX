package sy.sezar.clinicx.tenant.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;

import java.util.Collection;

/**
 * Spring Security voter that makes access decisions based on tenant membership.
 * Integrates with Spring Security's access decision process.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantAccessDecisionVoter implements AccessDecisionVoter<Object> {
    
    private final TenantAccessValidator tenantAccessValidator;
    
    @Override
    public boolean supports(ConfigAttribute attribute) {
        // Support all security attributes
        return true;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        // Support web requests and method invocations
        return true;
    }
    
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        // If not authenticated, abstain from voting
        if (authentication == null || !authentication.isAuthenticated()) {
            return ACCESS_ABSTAIN;
        }
        
        // Check if this is a request that requires tenant validation
        if (!requiresTenantValidation(object, attributes)) {
            return ACCESS_ABSTAIN;
        }
        
        // Get current tenant from context
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            log.debug("No tenant context available for voting");
            return ACCESS_ABSTAIN;
        }
        
        // Extract user information from JWT
        String userId = extractUserId(authentication);
        if (userId == null) {
            log.warn("Could not extract user ID from authentication");
            return ACCESS_DENIED;
        }
        
        // Validate tenant access
        boolean hasAccess = tenantAccessValidator.validateUserAccess(userId, tenantId);
        
        if (hasAccess) {
            log.debug("User {} granted access to tenant {}", userId, tenantId);
            return ACCESS_GRANTED;
        } else {
            log.debug("User {} denied access to tenant {}", userId, tenantId);
            return ACCESS_DENIED;
        }
    }
    
    private boolean requiresTenantValidation(Object object, Collection<ConfigAttribute> attributes) {
        // Check if this is a web request
        if (object instanceof FilterInvocation filterInvocation) {
            String uri = filterInvocation.getRequestUrl();
            
            // Exclude public endpoints
            if (uri.startsWith("/api/public") || 
                uri.startsWith("/actuator") || 
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs")) {
                return false;
            }
            
            // Super admin endpoints don't require tenant validation
            if (uri.startsWith("/api/tenants")) {
                return false;
            }
        }
        
        // Check for specific security attributes
        for (ConfigAttribute attribute : attributes) {
            String config = attribute.getAttribute();
            
            // Skip tenant validation for super admin roles
            if ("ROLE_SUPER_ADMIN".equals(config)) {
                return false;
            }
            
            // Check for custom tenant-specific attributes
            if (config != null && config.startsWith("TENANT_")) {
                return true;
            }
        }
        
        // Default: require tenant validation for authenticated requests
        return true;
    }
    
    private String extractUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Try different claims for user ID
            String userId = jwt.getClaimAsString("sub");
            if (userId == null) {
                userId = jwt.getClaimAsString("user_id");
            }
            if (userId == null) {
                userId = jwt.getClaimAsString("preferred_username");
            }
            return userId;
        }
        
        return authentication.getName();
    }
}