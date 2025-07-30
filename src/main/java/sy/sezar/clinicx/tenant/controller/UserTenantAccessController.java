package sy.sezar.clinicx.tenant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.TenantAccessDto;
import sy.sezar.clinicx.tenant.service.TenantSwitchingService;

import java.util.List;

/**
 * REST controller for managing user access to multiple tenants.
 * Allows super admins to grant and revoke tenant access for users.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Tenant Access", description = "Manage user access to multiple tenants")
public class UserTenantAccessController {
    
    private final TenantSwitchingService tenantSwitchingService;
    
    /**
     * Grant a user access to an additional tenant.
     */
    @PostMapping("/{userId}/tenant-access")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Grant tenant access", 
               description = "Grant a user access to an additional tenant with specified role")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Access granted successfully"),
        @ApiResponse(responseCode = "400", description = "User already has access to this tenant"),
        @ApiResponse(responseCode = "404", description = "User or tenant not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> grantTenantAccess(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Grant access request", required = true)
            @Valid @RequestBody GrantTenantAccessRequest request) {
        
        log.info("Granting user {} access to tenant {} with role {}", 
                userId, request.tenantId(), request.role());
        
        tenantSwitchingService.grantUserTenantAccess(
            userId, 
            request.tenantId(), 
            request.role(), 
            request.isPrimary()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    /**
     * Revoke a user's access to a specific tenant.
     */
    @DeleteMapping("/{userId}/tenant-access/{tenantId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Revoke tenant access", 
               description = "Revoke a user's access to a specific tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Access revoked successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot revoke primary tenant access"),
        @ApiResponse(responseCode = "404", description = "Access not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> revokeTenantAccess(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId) {
        
        log.info("Revoking user {} access to tenant {}", userId, tenantId);
        
        tenantSwitchingService.revokeUserTenantAccess(userId, tenantId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all tenants a user has access to.
     */
    @GetMapping("/{userId}/tenant-access")
    @PreAuthorize("hasRole('SUPER_ADMIN') or #userId == authentication.name")
    @Operation(summary = "List user's tenant access", 
               description = "Get all tenants a user has access to")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tenant access list retrieved"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<List<TenantAccessDto>> getUserTenantAccess(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        
        log.info("Getting tenant access for user {}", userId);
        
        List<TenantAccessDto> tenantAccess = tenantSwitchingService.getCurrentUserTenants();
        
        return ResponseEntity.ok(tenantAccess);
    }
    
    /**
     * Request object for granting tenant access.
     */
    public record GrantTenantAccessRequest(
        @Parameter(description = "Tenant ID to grant access to", required = true)
        String tenantId,
        
        @Parameter(description = "Role to assign in the tenant", required = true)
        String role,
        
        @Parameter(description = "Whether this should be the primary tenant", required = false)
        boolean isPrimary
    ) {}
}