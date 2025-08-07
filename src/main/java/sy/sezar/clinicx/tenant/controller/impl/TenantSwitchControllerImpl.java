package sy.sezar.clinicx.tenant.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.controller.api.TenantSwitchControllerApi;
import sy.sezar.clinicx.tenant.dto.TenantAccessDto;
import sy.sezar.clinicx.tenant.dto.TenantSwitchResponseDto;
import sy.sezar.clinicx.tenant.service.TenantSwitchingService;

import java.util.List;

/**
 * Implementation of tenant switching operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantSwitchControllerImpl implements TenantSwitchControllerApi {
    
    private final TenantSwitchingService tenantSwitchingService;
    
    @Override
    public ResponseEntity<List<TenantAccessDto>> getMyTenants() {
        log.debug("Getting accessible tenants for current user");
        List<TenantAccessDto> tenants = tenantSwitchingService.getCurrentUserTenants();
        return ResponseEntity.ok(tenants);
    }
    
    @Override
    public ResponseEntity<TenantSwitchResponseDto> switchTenant(String tenantId) {
        log.info("Switching to tenant: {}", tenantId);
        TenantSwitchResponseDto response = tenantSwitchingService.switchTenant(tenantId);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<TenantAccessDto> getCurrentTenant() {
        log.debug("Getting current tenant for user");
        TenantAccessDto currentTenant = tenantSwitchingService.getCurrentActiveTenant();
        return ResponseEntity.ok(currentTenant);
    }
    
    @Override
    public ResponseEntity<String> syncTenants() {
        log.info("Syncing tenants for current user");
        
        // Get current user info from JWT
        String userId = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String issuer = jwt.getIssuer().toString();
            String realmName = issuer.substring(issuer.lastIndexOf("/") + 1);
            String username = jwt.getClaimAsString("preferred_username");
            
            if (username != null) {
                tenantSwitchingService.syncUserTenantsToKeycloak(userId, realmName, username);
                return ResponseEntity.ok("Tenants synced successfully");
            }
        }
        
        return ResponseEntity.badRequest().body("Failed to sync tenants");
    }
}