package sy.sezar.clinicx.tenant.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.controller.api.SecuredTenantExampleControllerApi;
import sy.sezar.clinicx.tenant.service.TenantSecurityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of example controller demonstrating multi-tenant security features.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecuredTenantExampleControllerImpl implements SecuredTenantExampleControllerApi {
    
    private final TenantSecurityService tenantSecurityService;
    
    @Override
    public ResponseEntity<Map<String, Object>> getCurrentTenantInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("currentTenant", TenantContext.getCurrentTenant());
        info.put("isTenantAdmin", tenantSecurityService.isTenantAdmin());
        info.put("permissions", tenantSecurityService.getCurrentTenantPermissions());
        
        return ResponseEntity.ok(info);
    }
    
    @Override
    public ResponseEntity<Map<String, String>> getTenantUsers() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This would return all users in tenant: " + TenantContext.getCurrentTenant());
        response.put("role", "You are accessing this as ADMIN");
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<Map<String, String>> getTenantSettings(String tenantId) {
        Map<String, String> response = new HashMap<>();
        response.put("tenantId", tenantId);
        response.put("message", "Settings for tenant: " + tenantId);
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<Map<String, String>> getAppointments() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Viewing appointments for tenant: " + TenantContext.getCurrentTenant());
        response.put("permission", "VIEW_APPOINTMENTS granted");
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<Map<String, String>> updateMedicalRecord(
            String recordId,
            Map<String, Object> updates) {
        
        Map<String, String> response = new HashMap<>();
        response.put("recordId", recordId);
        response.put("tenantId", TenantContext.getCurrentTenant());
        response.put("message", "Medical record updated with tenant validation");
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<Map<String, String>> getSystemAudit() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "System audit accessible by super admin or tenant admin");
        response.put("currentTenant", TenantContext.getCurrentTenant());
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<Map<String, Object>> performDynamicAction(Map<String, String> request) {
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
    
    @Override
    public ResponseEntity<Map<String, Object>> getMyTenants() {
        List<String> accessibleTenants = tenantSecurityService.getAccessibleTenants();
        String primaryTenant = tenantSecurityService.getPrimaryTenant();
        
        Map<String, Object> response = new HashMap<>();
        response.put("accessibleTenants", accessibleTenants);
        response.put("primaryTenant", primaryTenant);
        response.put("currentTenant", TenantContext.getCurrentTenant());
        
        return ResponseEntity.ok(response);
    }
}