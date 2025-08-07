package sy.sezar.clinicx.tenant.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.GrantTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.TenantAccessDto;

import java.util.List;

/**
 * REST controller API for managing user access to multiple tenants.
 * Allows super admins to grant and revoke tenant access for users.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Tenant Access", description = "Manage user access to multiple tenants")
public interface UserTenantAccessControllerApi {

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
    ResponseEntity<Void> grantTenantAccess(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Grant access request", required = true)
            @Valid @RequestBody GrantTenantAccessRequest request);

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
    ResponseEntity<Void> revokeTenantAccess(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId);

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
    ResponseEntity<List<TenantAccessDto>> getUserTenantAccess(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId);
}