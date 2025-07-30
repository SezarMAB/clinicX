package sy.sezar.clinicx.tenant.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;
import sy.sezar.clinicx.tenant.service.TenantAuditService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter that validates tenant access for each request.
 * Runs after Spring Security authentication but before controller execution.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class TenantAuthorizationFilter extends OncePerRequestFilter {
    
    private final TenantAccessValidator tenantAccessValidator;
    private final TenantAuditService tenantAuditService;
    
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/public",
        "/api/auth/test/public",
        "/actuator/health",
        "/swagger-ui",
        "/v3/api-docs",
        "/api/tenants",  // Super admin endpoints
        "/api/v1/tenant-switch" // Tenant switching endpoint
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Check if the path should be excluded from tenant authorization
        if (shouldExclude(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get current tenant from context
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            log.warn("No tenant context set for authenticated request: {} {}", method, path);
            sendUnauthorizedResponse(response, "No tenant context available");
            return;
        }
        
        // Validate tenant access
        if (!tenantAccessValidator.validateCurrentTenantAccess()) {
            String username = authentication.getName();
            log.warn("User {} denied access to tenant {} for request: {} {}", 
                username, tenantId, method, path);
            
            // Audit the failed access attempt
            tenantAuditService.auditAccessDenied(username, tenantId, path, "Unauthorized tenant access");
            
            sendForbiddenResponse(response, "Access denied to tenant: " + tenantId);
            return;
        }
        
        // Audit successful access
        tenantAuditService.auditAccessGranted(authentication.getName(), tenantId, path);
        
        // Continue with the request
        filterChain.doFilter(request, response);
    }
    
    private boolean shouldExclude(String path) {
        return EXCLUDED_PATHS.stream()
            .anyMatch(excludedPath -> path.startsWith(excludedPath));
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
            String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message)
        );
    }
    
    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(
            String.format("{\"error\": \"Forbidden\", \"message\": \"%s\"}", message)
        );
    }
}