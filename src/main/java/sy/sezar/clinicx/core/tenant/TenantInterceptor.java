package sy.sezar.clinicx.core.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that sets the tenant context for each request.
 * Uses the TenantResolver to determine the current tenant.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {
    
    private final TenantResolver tenantResolver;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // Resolve tenant from JWT or use default
        String tenantId = tenantResolver.resolveTenant();
        
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
            log.debug("Set tenant context for request: {} -> tenant: {}", 
                request.getRequestURI(), tenantId);
        } else {
            log.warn("No tenant could be resolved for request: {}", request.getRequestURI());
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
}