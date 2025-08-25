package sy.sezar.clinicx.tenant.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.TenantAccessDto;
import sy.sezar.clinicx.tenant.dto.TenantSwitchResponseDto;

import java.util.List;

/**
 * REST controller API for tenant switching operations.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Tenant Switching", description = "APIs for multi-tenant access management")
@SecurityRequirement(name = "bearer-jwt")
public interface TenantSwitchControllerApi {
    
    @GetMapping("/my-tenants")
    @Operation(summary = "Get accessible tenants", 
               description = "Get list of all tenants the current user can access")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<List<TenantAccessDto>> getMyTenants();
    
    @PostMapping("/switch-tenant")
    @Operation(summary = "Switch active tenant", 
               description = "Switch to a different tenant the user has access to")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<TenantSwitchResponseDto> switchTenant(@RequestParam String tenantId);
    
    @GetMapping("/current-tenant")
    @Operation(summary = "Get current tenant", 
               description = "Get the currently active tenant for the user")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<TenantAccessDto> getCurrentTenant();
    
    @PostMapping("/sync-tenants")
    @Operation(summary = "Sync user tenants to Keycloak", 
               description = "Sync the current user's accessible tenants from backend to Keycloak")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<String> syncTenants();
}