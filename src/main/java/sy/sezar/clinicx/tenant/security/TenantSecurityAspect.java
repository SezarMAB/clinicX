package sy.sezar.clinicx.tenant.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;
import sy.sezar.clinicx.tenant.service.TenantAuditService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Aspect that enforces @RequiresTenant security constraints.
 * Validates tenant access before allowing method execution.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TenantSecurityAspect {
    
    private final TenantAccessValidator tenantAccessValidator;
    private final TenantAuditService tenantAuditService;
    
    @Around("@annotation(requiresTenant)")
    public Object validateTenantAccess(ProceedingJoinPoint joinPoint, RequiresTenant requiresTenant) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User must be authenticated");
        }
        
        // Check if super admin can bypass
        if (requiresTenant.allowSuperAdmin() && hasSuperAdminRole(authentication)) {
            log.debug("Super admin bypassing tenant validation");
            return joinPoint.proceed();
        }
        
        String tenantId;
        String methodName = joinPoint.getSignature().getName();
        
        if (requiresTenant.validateCurrentTenant()) {
            // Validate against current tenant context
            tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                log.warn("No tenant context available for method: {}", methodName);
                throw new AccessDeniedException("No tenant context available");
            }
        } else {
            // Extract tenant ID from method parameter
            tenantId = extractTenantIdFromParameters(joinPoint, requiresTenant.tenantIdParam());
            if (tenantId == null) {
                log.warn("Could not extract tenant ID from parameters for method: {}", methodName);
                throw new AccessDeniedException("Tenant ID parameter not found");
            }
        }
        
        // Validate access
        boolean hasAccess = tenantAccessValidator.validateAccess(tenantId);
        
        if (!hasAccess) {
            String username = authentication.getName();
            log.warn("User {} denied access to tenant {} for method: {}", username, tenantId, methodName);
            tenantAuditService.auditAccessDenied(username, tenantId, methodName, "Method access denied");
            throw new AccessDeniedException(requiresTenant.message());
        }
        
        // Check role if specified
        if (!requiresTenant.role().isEmpty()) {
            boolean hasRole = tenantAccessValidator.validateRole(tenantId, requiresTenant.role());
            if (!hasRole) {
                String username = authentication.getName();
                log.warn("User {} lacks required role {} in tenant {} for method: {}", 
                    username, requiresTenant.role(), tenantId, methodName);
                tenantAuditService.auditAccessDenied(username, tenantId, methodName, 
                    "Missing required role: " + requiresTenant.role());
                throw new AccessDeniedException("Insufficient role privileges in tenant");
            }
        }
        
        // Log successful access
        tenantAuditService.auditAccessGranted(authentication.getName(), tenantId, methodName);
        
        // Proceed with method execution
        return joinPoint.proceed();
    }
    
    @Around("@within(requiresTenant)")
    public Object validateTenantAccessForClass(ProceedingJoinPoint joinPoint, RequiresTenant requiresTenant) throws Throwable {
        // Reuse the same logic for class-level annotations
        return validateTenantAccess(joinPoint, requiresTenant);
    }
    
    private boolean hasSuperAdminRole(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
    }
    
    private String extractTenantIdFromParameters(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            
            // Check if parameter has matching name
            if (param.getName().equals(paramName) || 
                (param.isAnnotationPresent(org.springframework.web.bind.annotation.PathVariable.class) &&
                 param.getAnnotation(org.springframework.web.bind.annotation.PathVariable.class).value().equals(paramName))) {
                
                Object arg = args[i];
                if (arg != null) {
                    return arg.toString();
                }
            }
        }
        
        return null;
    }
}