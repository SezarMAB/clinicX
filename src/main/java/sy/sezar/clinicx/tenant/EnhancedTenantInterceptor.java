package sy.sezar.clinicx.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;

import java.util.Arrays;
import java.util.List;

/**
 * Enhanced interceptor that sets and validates tenant context for each request.
 * Includes access validation to ensure users can only access their authorized tenants.
 */
@Slf4j
@Component("enhancedTenantInterceptor")
@RequiredArgsConstructor
public class EnhancedTenantInterceptor implements HandlerInterceptor {
    
    private final TenantResolver tenantResolver;
    private final TenantAccessValidator tenantAccessValidator;
    
    @Value("${app.tenant.validation.enabled:true}")
    private boolean tenantValidationEnabled;
    
    // Paths that don't require tenant validation
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs",
        "/api/auth/switch-tenant",
        "/api/auth/my-tenants",
        "/api/auth/current-tenant",
        "/api/v1/tenants" // Tenant management endpoints
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        String requestPath = request.getRequestURI();
        
        // Skip tenant validation for excluded paths
        if (isExcludedPath(requestPath)) {
            log.debug("Skipping tenant validation for path: {}", requestPath);
            return true;
        }
        
        // Resolve tenant from JWT or headers
        String tenantId = tenantResolver.resolveTenant();
        
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
            log.debug("Set tenant context for request: {} -> tenant: {}", 
                requestPath, tenantId);
            
            // Validate tenant access if enabled
            if (tenantValidationEnabled && !tenantAccessValidator.validateAccess(tenantId)) {
                log.warn("User does not have access to tenant: {} for request: {}", 
                    tenantId, requestPath);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("{\"error\": \"Access denied to tenant: " + tenantId + "\"}");
                return false;
            }
        } else {
            log.warn("No tenant could be resolved for request: {}", requestPath);
            
            // For multi-tenant mode, reject requests without tenant context
            if (tenantResolver.isMultiTenant()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("{\"error\": \"Tenant context is required\"}");
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) throws Exception {
        // Clear the tenant context after request completion
        TenantContext.clear();
        log.debug("Cleared tenant context after request: {}", request.getRequestURI());
    }
    
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream()
            .anyMatch(excluded -> path.startsWith(excluded));
    }
}