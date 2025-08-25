package sy.sezar.clinicx.tenant.dto;

/**
 * DTO representing user's access to a tenant.
 */
public record TenantAccessDto(
    String tenantId,
    String tenantName,
    String subdomain,
    String role,
    boolean isPrimary,
    boolean isActive,
    String specialty
) {}