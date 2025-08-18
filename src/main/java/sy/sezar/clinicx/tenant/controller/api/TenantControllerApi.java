package sy.sezar.clinicx.tenant.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenants", description = "Multi-tenant management APIs")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public interface TenantControllerApi {

    @PostMapping
    @Operation(
        summary = "Create a new tenant",
        description = "Creates a new tenant with Keycloak realm and admin user"
    )
    @ApiResponse(responseCode = "201", description = "Tenant created successfully",
                content = @Content(schema = @Schema(implementation = TenantCreationResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Tenant already exists")
    ResponseEntity<TenantCreationResponseDto> createTenant(
            @Valid @RequestBody TenantCreateRequest request);

    @GetMapping
    @Operation(
        summary = "Get all tenants",
        description = "Retrieves paginated list of tenants with usage statistics",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: name", example = "name")
        }
    )
    @ApiResponse(responseCode = "200", description = "Tenants retrieved successfully")
    ResponseEntity<Page<TenantSummaryDto>> getAllTenants(
            @Parameter(name = "searchTerm", description = "Search term for filtering tenants")
            @RequestParam(required = false) String searchTerm,
            @Parameter(name = "isActive", description = "Filter by active status")
            @RequestParam(required = false) Boolean isActive,
            @Parameter(hidden = true) @PageableDefault(sort = "name") Pageable pageable);

    @GetMapping("/{id}")
    @Operation(
        summary = "Get tenant by ID",
        description = "Retrieves a specific tenant by ID"
    )
    @ApiResponse(responseCode = "200", description = "Tenant found",
                content = @Content(schema = @Schema(implementation = TenantDetailDto.class)))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    ResponseEntity<TenantDetailDto> getTenantById(
            @Parameter(description = "Tenant ID") @PathVariable UUID id);

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(
        summary = "Get tenant by subdomain",
        description = "Retrieves a tenant by subdomain"
    )
    @ApiResponse(responseCode = "200", description = "Tenant found",
                content = @Content(schema = @Schema(implementation = TenantDetailDto.class)))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    ResponseEntity<TenantDetailDto> getTenantBySubdomain(
            @Parameter(description = "Tenant subdomain") @PathVariable String subdomain);

    @PutMapping("/{id}")
    @Operation(
        summary = "Update tenant",
        description = "Updates tenant information"
    )
    @ApiResponse(responseCode = "200", description = "Tenant updated successfully",
                content = @Content(schema = @Schema(implementation = TenantDetailDto.class)))
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    ResponseEntity<TenantDetailDto> updateTenant(
            @Parameter(description = "Tenant ID") @PathVariable UUID id,
            @Valid @RequestBody TenantUpdateRequest request);

    @PostMapping("/{id}/activate")
    @Operation(
        summary = "Activate tenant",
        description = "Activates a deactivated tenant"
    )
    @ApiResponse(responseCode = "204", description = "Tenant activated successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    ResponseEntity<Void> activateTenant(
            @Parameter(description = "Tenant ID") @PathVariable UUID id);

    @PostMapping("/{id}/deactivate")
    @Operation(
        summary = "Deactivate tenant",
        description = "Deactivates an active tenant"
    )
    @ApiResponse(responseCode = "204", description = "Tenant deactivated successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    ResponseEntity<Void> deactivateTenant(
            @Parameter(description = "Tenant ID") @PathVariable UUID id);

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete tenant",
        description = "Permanently deletes a tenant and its Keycloak realm"
    )
    @ApiResponse(responseCode = "204", description = "Tenant deleted successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    ResponseEntity<Void> deleteTenant(
            @Parameter(description = "Tenant ID") @PathVariable UUID id);

    @GetMapping("/check-subdomain/{subdomain}")
    @Operation(
        summary = "Check subdomain availability",
        description = "Checks if a subdomain is available"
    )
    @ApiResponse(responseCode = "200", description = "Subdomain availability checked",
                content = @Content(schema = @Schema(implementation = SubdomainAvailabilityDto.class)))
    ResponseEntity<SubdomainAvailabilityDto> checkSubdomainAvailability(
            @Parameter(description = "Subdomain to check") @PathVariable String subdomain);

    @PostMapping("/{id}/reset-admin-password")
    @Operation(
        summary = "Reset tenant admin password",
        description = "Resets the password for a tenant's admin user. Only accessible by SUPER_ADMIN."
    )
    @ApiResponse(responseCode = "204", description = "Password reset successfully")
    @ApiResponse(responseCode = "404", description = "Tenant or user not found")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    ResponseEntity<Void> resetTenantAdminPassword(
            @Parameter(description = "Tenant ID") @PathVariable UUID id,
            @Valid @RequestBody PasswordResetRequest request);
}