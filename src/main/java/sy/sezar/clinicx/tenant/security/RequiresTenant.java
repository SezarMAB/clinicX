package sy.sezar.clinicx.tenant.security;

import java.lang.annotation.*;

/**
 * Annotation to indicate that a method or class requires tenant context validation.
 * Can be used to enforce tenant-specific access control at the method level.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresTenant {
    
    /**
     * The required role within the tenant.
     * If empty, any valid tenant access is sufficient.
     */
    String role() default "";
    
    /**
     * Whether to validate against the current tenant context.
     * If false, the tenant ID must be provided as a method parameter.
     */
    boolean validateCurrentTenant() default true;
    
    /**
     * The parameter name containing the tenant ID.
     * Used when validateCurrentTenant is false.
     */
    String tenantIdParam() default "tenantId";
    
    /**
     * Whether to allow super admins to bypass tenant validation.
     */
    boolean allowSuperAdmin() default true;
    
    /**
     * Custom error message when access is denied.
     */
    String message() default "Access denied: insufficient tenant privileges";
}