package sy.sezar.clinicx.tenant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.security.RequiresTenant;
import sy.sezar.clinicx.tenant.service.TenantSecurityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Example controller demonstrating multi-tenant security features.
 * Shows various ways to secure endpoints with tenant-aware authorization.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/secure")
@RequiredArgsConstructor
public class SecuredTenantExampleController {
    
    private final TenantSecurityService tenantSecurityService;
    
    /**
     * Basic endpoint that requires tenant context.
     * The TenantAuthorizationFilter ensures user has access to current tenant.
     */
    @GetMapping("/tenant-info")
    public ResponseEntity<Map<String, Object>> getCurrentTenantInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("currentTenant", TenantContext.getCurrentTenant());
        info.put("isTenantAdmin", tenantSecurityService.isTenantAdmin());
        info.put("permissions", tenantSecurityService.getCurrentTenantPermissions());
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Endpoint that requires admin role in the current tenant.
     * Uses @RequiresTenant annotation for method-level security.
     */
    @RequiresTenant(role = "ADMIN")
    @GetMapping("/admin/users")
    public ResponseEntity<Map<String, String>> getTenantUsers() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This would return all users in tenant: " + TenantContext.getCurrentTenant());
        response.put("role", "You are accessing this as ADMIN");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint that validates tenant parameter matches access.
     * Uses @RequiresTenant with custom parameter validation.
     */
    @RequiresTenant(validateCurrentTenant = false, tenantIdParam = "tenantId")
    @GetMapping("/tenants/{tenantId}/settings")
    public ResponseEntity<Map<String, String>> getTenantSettings(@PathVariable String tenantId) {
        Map<String, String> response = new HashMap<>();
        response.put("tenantId", tenantId);
        response.put("message", "Settings for tenant: " + tenantId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint using Spring Security expression with tenant service.
     * Shows integration with @PreAuthorize.
     */
    @PreAuthorize("@tenantSecurityService.canPerformAction('VIEW_APPOINTMENTS')")
    @GetMapping("/appointments")
    public ResponseEntity<Map<String, String>> getAppointments() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Viewing appointments for tenant: " + TenantContext.getCurrentTenant());
        response.put("permission", "VIEW_APPOINTMENTS granted");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint combining multiple security checks.
     * Requires both tenant access and specific permission.
     */
    @RequiresTenant
    @PreAuthorize("@tenantSecurityService.canPerformAction('UPDATE_MEDICAL_RECORDS')")
    @PutMapping("/medical-records/{recordId}")
    public ResponseEntity<Map<String, String>> updateMedicalRecord(
            @PathVariable String recordId,
            @RequestBody Map<String, Object> updates) {
        
        Map<String, String> response = new HashMap<>();
        response.put("recordId", recordId);
        response.put("tenantId", TenantContext.getCurrentTenant());
        response.put("message", "Medical record updated with tenant validation");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint for super admins that bypasses tenant validation.
     * Uses allowSuperAdmin flag in @RequiresTenant.
     */
    @RequiresTenant(allowSuperAdmin = true, role = "ADMIN")
    @GetMapping("/system/audit")
    public ResponseEntity<Map<String, String>> getSystemAudit() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "System audit accessible by super admin or tenant admin");
        response.put("currentTenant", TenantContext.getCurrentTenant());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint showing programmatic security checks.
     * Uses TenantSecurityService for dynamic authorization.
     */
    @PostMapping("/dynamic-action")
    public ResponseEntity<Map<String, Object>> performDynamicAction(@RequestBody Map<String, String> request) {
        String action = request.get("action");
        String targetTenant = request.get("targetTenant");
        
        Map<String, Object> response = new HashMap<>();
        
        // Check if user can perform action in target tenant
        if (targetTenant != null) {
            boolean allowed = tenantSecurityService.canPerformActionInTenant(targetTenant, action);
            response.put("allowed", allowed);
            response.put("targetTenant", targetTenant);
        } else {
            boolean allowed = tenantSecurityService.canPerformAction(action);
            response.put("allowed", allowed);
            response.put("currentTenant", TenantContext.getCurrentTenant());
        }
        
        response.put("action", action);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to get all accessible tenants for the current user.
     * Useful for tenant switching UI.
     */
    @GetMapping("/my-tenants")
    public ResponseEntity<Map<String, Object>> getMyTenants() {
        List<String> accessibleTenants = tenantSecurityService.getAccessibleTenants();
        String primaryTenant = tenantSecurityService.getPrimaryTenant();
        
        Map<String, Object> response = new HashMap<>();
        response.put("accessibleTenants", accessibleTenants);
        response.put("primaryTenant", primaryTenant);
        response.put("currentTenant", TenantContext.getCurrentTenant());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Class-level @RequiresTenant annotation example.
     * All methods in this nested controller require tenant validation.
     */
    @RestController
    @RequestMapping("/api/v1/secure/tenant-required")
    @RequiresTenant(message = "This entire controller requires tenant access")
    @RequiredArgsConstructor
    public static class TenantRequiredController {
        
        private final TenantSecurityService tenantSecurityService;
        
        @GetMapping("/info")
        public ResponseEntity<Map<String, String>> getInfo() {
            Map<String, String> response = new HashMap<>();
            response.put("message", "This method inherits @RequiresTenant from class");
            response.put("tenant", TenantContext.getCurrentTenant());
            
            return ResponseEntity.ok(response);
        }
        
        @RequiresTenant(role = "DOCTOR")
        @GetMapping("/doctor-only")
        public ResponseEntity<Map<String, String>> getDoctorOnlyInfo() {
            Map<String, String> response = new HashMap<>();
            response.put("message", "This requires DOCTOR role in addition to tenant access");
            response.put("tenant", TenantContext.getCurrentTenant());
            
            return ResponseEntity.ok(response);
        }
    }
}