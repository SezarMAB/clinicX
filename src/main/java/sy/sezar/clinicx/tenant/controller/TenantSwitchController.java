package sy.sezar.clinicx.tenant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.TenantAccessDto;
import sy.sezar.clinicx.tenant.dto.TenantSwitchResponseDto;
import sy.sezar.clinicx.tenant.service.TenantSwitchingService;

import java.util.List;

/**
 * REST controller for tenant switching operations.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Tenant Switching", description = "APIs for multi-tenant access management")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
@Slf4j
public class TenantSwitchController {
    
    private final TenantSwitchingService tenantSwitchingService;
    
    @GetMapping("/my-tenants")
    @Operation(summary = "Get accessible tenants", 
               description = "Get list of all tenants the current user can access")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TenantAccessDto>> getMyTenants() {
        log.debug("Getting accessible tenants for current user");
        List<TenantAccessDto> tenants = tenantSwitchingService.getCurrentUserTenants();
        return ResponseEntity.ok(tenants);
    }
    
    @PostMapping("/switch-tenant")
    @Operation(summary = "Switch active tenant", 
               description = "Switch to a different tenant the user has access to")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TenantSwitchResponseDto> switchTenant(
            @RequestParam String tenantId) {
        log.info("Switching to tenant: {}", tenantId);
        TenantSwitchResponseDto response = tenantSwitchingService.switchTenant(tenantId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/current-tenant")
    @Operation(summary = "Get current tenant", 
               description = "Get the currently active tenant for the user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TenantAccessDto> getCurrentTenant() {
        log.debug("Getting current tenant for user");
        TenantAccessDto currentTenant = tenantSwitchingService.getCurrentActiveTenant();
        return ResponseEntity.ok(currentTenant);
    }
}
