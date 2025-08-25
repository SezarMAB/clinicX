package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.Parameter;

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