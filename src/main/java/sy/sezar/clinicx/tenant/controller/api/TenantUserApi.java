package sy.sezar.clinicx.tenant.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.*;

/**
 * REST API interface for tenant administrators to manage users within their tenant.
 * This includes creating, updating, deactivating, and managing both internal users
 * and external users from other tenants who have access.
 */
@Tag(name = "Tenant User Management", description = "Endpoints for tenant admins to manage users")
@RequestMapping("/api/v1/tenant/users")
@PreAuthorize("hasAnyRole('ADMIN', 'GLOBAL_SUPER_ADMIN')")
public interface TenantUserApi {

    /**
     * Get all users in the current tenant.
     * This includes both internal users and external users with access.
     */
    @GetMapping

    @Operation(summary = "List all users",
               description = "Get paginated list of all users in the current tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Page<TenantUserDto>> getAllUsers(
            @Parameter(description = "Include external users", required = false)
            @RequestParam(defaultValue = "true") boolean includeExternal,
            @Parameter(hidden = true) @PageableDefault(sort = "username") Pageable pageable);

    /**
     * Search users by username, email, or name.
     */
    @GetMapping("/search")

    @Operation(summary = "Search users",
               description = "Search users by username, email, or name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results retrieved"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Page<TenantUserDto>> searchUsers(
            @Parameter(description = "Search term", required = true)
            @RequestParam String searchTerm,
            @Parameter(hidden = true) @PageableDefault(sort = "username") Pageable pageable);

    /**
     * Get a specific user by ID.
     */
    @GetMapping("/{userId}")

    @Operation(summary = "Get user details",
               description = "Get detailed information about a specific user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = TenantUserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<TenantUserDto> getUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId);

    /**
     * Create a new user in the current tenant.
     */
    @PostMapping

    @Operation(summary = "Create new user",
               description = "Create a new user in the current tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = TenantUserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or user already exists"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<TenantUserDto> createUser(@Valid @RequestBody TenantUserCreateRequest request);

    /**
     * Update an existing user.
     */
    @PutMapping("/{userId}")

    @Operation(summary = "Update user",
               description = "Update an existing user's information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = TenantUserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<TenantUserDto> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Valid @RequestBody TenantUserUpdateRequest request);

    /**
     * Deactivate a user (soft delete).
     */
    @PostMapping("/{userId}/deactivate")

    @Operation(summary = "Deactivate user",
               description = "Deactivate a user account (can be reactivated later)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Void> deactivateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId);

    /**
     * Reactivate a deactivated user.
     */
    @PostMapping("/{userId}/activate")

    @Operation(summary = "Activate user",
               description = "Reactivate a previously deactivated user account")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User activated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Void> activateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId);

    /**
     * Delete a user permanently.
     * This should be used with caution as it cannot be undone.
     */
    @DeleteMapping("/{userId}")

    @Operation(summary = "Delete user permanently",
               description = "Permanently delete a user (cannot be undone)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId);

    /**
     * Update user roles within the tenant.
     */
    @PutMapping("/{userId}/roles")

    @Operation(summary = "Update user roles",
               description = "Update the roles assigned to a user in this tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<TenantUserDto> updateUserRoles(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRolesRequest request);

    /**
     * Grant an external user access to this tenant.
     * This allows users from other tenants to access this tenant with specific roles.
     */
    @PostMapping("/grant-access")

    @Operation(summary = "Grant external user access",
               description = "Grant a user from another tenant access to this tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Access granted successfully"),
        @ApiResponse(responseCode = "400", description = "User already has access or invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<TenantUserDto> grantExternalUserAccess(@Valid @RequestBody GrantExternalAccessRequest request);

    /**
     * Revoke an external user's access to this tenant.
     */
    @DeleteMapping("/revoke-access/{userId}")

    @Operation(summary = "Revoke external user access",
               description = "Revoke an external user's access to this tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Access revoked successfully"),
        @ApiResponse(responseCode = "404", description = "User access not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Void> revokeExternalUserAccess(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId);

    /**
     * Get user activity logs.
     */
    @GetMapping("/{userId}/activity")

    @Operation(summary = "Get user activity",
               description = "Get activity logs for a specific user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Activity logs retrieved"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Page<UserActivityDto>> getUserActivity(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(hidden = true) @PageableDefault(sort = "timestamp,desc") Pageable pageable);

    /**
     * Reset user password.
     */
    @PostMapping("/{userId}/reset-password")

    @Operation(summary = "Reset user password",
               description = "Reset a user's password and optionally force them to change it on next login")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password reset successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    ResponseEntity<Void> resetUserPassword(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Valid @RequestBody ResetPasswordRequest request);
}
