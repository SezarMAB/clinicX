package sy.sezar.clinicx.tenant.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.security.RequiresTenant;

import java.util.Map;

/**
 * Example controller API demonstrating multi-tenant security features.
 * Shows various ways to secure endpoints with tenant-aware authorization.
 */
@RestController
@RequestMapping("/api/v1/secure")
@Tag(name = "Secured Tenant Examples", description = "Example endpoints demonstrating multi-tenant security")
public interface SecuredTenantExampleControllerApi {
    
    /**
     * Basic endpoint that requires tenant context.
     */
    @GetMapping("/tenant-info")
    @Operation(summary = "Get current tenant info", 
               description = "Returns current tenant context and permissions")
    ResponseEntity<Map<String, Object>> getCurrentTenantInfo();
    
    /**
     * Endpoint that requires admin role in the current tenant.
     */
    @RequiresTenant(role = "ADMIN")
    @GetMapping("/admin/users")
    @Operation(summary = "Get tenant users (Admin only)", 
               description = "Returns users in current tenant - requires ADMIN role")
    ResponseEntity<Map<String, String>> getTenantUsers();
    
    /**
     * Endpoint that validates tenant parameter matches access.
     */
    @RequiresTenant(validateCurrentTenant = false, tenantIdParam = "tenantId")
    @GetMapping("/tenants/{tenantId}/settings")
    @Operation(summary = "Get tenant settings", 
               description = "Returns settings for specified tenant")
    ResponseEntity<Map<String, String>> getTenantSettings(@PathVariable String tenantId);
    
    /**
     * Endpoint using Spring Security expression with tenant service.
     */
    @PreAuthorize("@tenantSecurityService.canPerformAction('VIEW_APPOINTMENTS')")
    @GetMapping("/appointments")
    @Operation(summary = "Get appointments", 
               description = "Returns appointments - requires VIEW_APPOINTMENTS permission")
    ResponseEntity<Map<String, String>> getAppointments();
    
    /**
     * Endpoint combining multiple security checks.
     */
    @RequiresTenant
    @PreAuthorize("@tenantSecurityService.canPerformAction('UPDATE_MEDICAL_RECORDS')")
    @PutMapping("/medical-records/{recordId}")
    @Operation(summary = "Update medical record", 
               description = "Updates medical record - requires tenant access and UPDATE_MEDICAL_RECORDS permission")
    ResponseEntity<Map<String, String>> updateMedicalRecord(
            @PathVariable String recordId,
            @RequestBody Map<String, Object> updates);
    
    /**
     * Endpoint for super admins that bypasses tenant validation.
     */
    @RequiresTenant(allowSuperAdmin = true, role = "ADMIN")
    @GetMapping("/system/audit")
    @Operation(summary = "Get system audit", 
               description = "Returns system audit - accessible by super admin or tenant admin")
    ResponseEntity<Map<String, String>> getSystemAudit();
    
    /**
     * Endpoint showing programmatic security checks.
     */
    @PostMapping("/dynamic-action")
    @Operation(summary = "Perform dynamic action", 
               description = "Performs action based on dynamic security checks")
    ResponseEntity<Map<String, Object>> performDynamicAction(@RequestBody Map<String, String> request);
    
    /**
     * Endpoint to get all accessible tenants for the current user.
     */
    @GetMapping("/my-tenants")
    @Operation(summary = "Get my accessible tenants", 
               description = "Returns all tenants accessible by current user")
    ResponseEntity<Map<String, Object>> getMyTenants();
}