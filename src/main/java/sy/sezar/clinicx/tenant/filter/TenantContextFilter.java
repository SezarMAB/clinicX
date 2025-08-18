package sy.sezar.clinicx.tenant.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Filter that resolves and validates tenant context for each request.
 * This filter runs early in the chain to ensure tenant context is available
 * for all subsequent processing.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final TenantAccessValidator tenantAccessValidator;
    
    @Value("${app.multi-tenant.header-name:X-Tenant-ID}")
    private String tenantHeaderName;
    
    @Value("${app.multi-tenant.enabled:true}")
    private boolean multiTenantEnabled;
    
    @Value("${app.multi-tenant.default-tenant:default}")
    private String defaultTenant;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Skip tenant resolution for public endpoints
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String tenantId = resolveTenant(request);
            
            if (tenantId == null || tenantId.isBlank()) {
                // For endpoints that require a tenant
                if (requiresTenant(request.getRequestURI())) {
                    log.error("Missing tenant context for protected endpoint: {}", request.getRequestURI());
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant context required");
                    return;
                }
                // Use default tenant for non-tenant-specific endpoints
                tenantId = defaultTenant;
            }
            
            // Validate tenant access if user is authenticated
            if (!validateTenantAccess(tenantId)) {
                log.warn("Access denied for tenant: {} - User: {}", tenantId, getCurrentUsername());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for tenant");
                return;
            }
            
            TenantContext.setCurrentTenant(tenantId);
            log.debug("Set tenant context: {} for request: {}", tenantId, request.getRequestURI());
            
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
    
    /**
     * Resolves tenant from multiple sources in order of precedence:
     * 1. Explicit header (highest priority)
     * 2. JWT claim
     * 3. Subdomain
     * 4. Path segment
     */
    private String resolveTenant(HttpServletRequest request) {
        // 1. Check explicit header (e.g., from API gateway or frontend)
        String tenantFromHeader = request.getHeader(tenantHeaderName);
        if (tenantFromHeader != null && !tenantFromHeader.isBlank()) {
            log.debug("Resolved tenant from header: {}", tenantFromHeader);
            return tenantFromHeader;
        }
        
        // 2. Check JWT claim
        String tenantFromJwt = getTenantFromJwt();
        if (tenantFromJwt != null && !tenantFromJwt.isBlank()) {
            log.debug("Resolved tenant from JWT: {}", tenantFromJwt);
            return tenantFromJwt;
        }
        
        // 3. Check subdomain (e.g., tenant-a.clinicx.com)
        String tenantFromSubdomain = extractTenantFromSubdomain(request);
        if (tenantFromSubdomain != null && !tenantFromSubdomain.isBlank()) {
            log.debug("Resolved tenant from subdomain: {}", tenantFromSubdomain);
            return tenantFromSubdomain;
        }
        
        // 4. Check path segment (e.g., /api/t/tenant-a/...)
        String tenantFromPath = extractTenantFromPath(request);
        if (tenantFromPath != null && !tenantFromPath.isBlank()) {
            log.debug("Resolved tenant from path: {}", tenantFromPath);
            return tenantFromPath;
        }
        
        return null;
    }
    
    private String getTenantFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Try current_tenant claim first (for optimized tokens)
            String currentTenant = jwt.getClaimAsString("current_tenant");
            if (currentTenant != null && !currentTenant.isBlank()) {
                return currentTenant;
            }
            
            // Fallback to tenant_id claim
            String tenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId;
            }
        }
        return null;
    }
    
    private String extractTenantFromSubdomain(HttpServletRequest request) {
        String host = request.getServerName();
        if (host != null && !host.startsWith("www.") && host.contains(".")) {
            String subdomain = host.substring(0, host.indexOf("."));
            if (!subdomain.equals("api") && !subdomain.equals("app")) {
                return subdomain;
            }
        }
        return null;
    }
    
    private String extractTenantFromPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Pattern: /api/t/{tenantId}/...
        if (path.startsWith("/api/t/") || path.startsWith("/t/")) {
            String[] segments = path.split("/");
            for (int i = 0; i < segments.length - 1; i++) {
                if ("t".equals(segments[i]) && i + 1 < segments.length) {
                    return segments[i + 1];
                }
            }
        }
        return null;
    }
    
    private boolean validateTenantAccess(String tenantId) {
        if (!multiTenantEnabled) {
            return true;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // No authentication yet, will be handled by security filter
            return true;
        }
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Check if user has access to this tenant
            return validateUserTenantAccess(jwt, tenantId);
        }
        
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private boolean validateUserTenantAccess(Jwt jwt, String tenantId) {
        // Check accessible_tenants claim
        Object accessibleTenantsObj = jwt.getClaim("accessible_tenants");
        if (accessibleTenantsObj instanceof List<?> accessibleTenants) {
            if (accessibleTenants.contains(tenantId)) {
                return true;
            }
        }
        
        // Check user_tenant_roles claim
        Object tenantRolesObj = jwt.getClaim("user_tenant_roles");
        if (tenantRolesObj instanceof Map<?, ?> tenantRoles) {
            if (tenantRoles.containsKey(tenantId)) {
                return true;
            }
        }
        
        // Fallback to database validation if claims are not present
        String username = jwt.getClaimAsString("preferred_username");
        if (username != null) {
            try {
                return tenantAccessValidator.validateUserAccess(username, tenantId);
            } catch (Exception e) {
                log.error("Error validating tenant access via database", e);
                return false;
            }
        }
        
        return false;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("preferred_username");
        }
        return "anonymous";
    }
    
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/public/") ||
               uri.startsWith("/actuator/health") ||
               uri.startsWith("/swagger-ui") ||
               uri.startsWith("/v3/api-docs");
    }
    
    private boolean requiresTenant(String uri) {
        // Endpoints that explicitly require tenant context
        return uri.startsWith("/api/v1/patients") ||
               uri.startsWith("/api/v1/appointments") ||
               uri.startsWith("/api/v1/staff") ||
               uri.startsWith("/api/v1/invoices") ||
               uri.startsWith("/api/v1/clinic-info");
    }
}