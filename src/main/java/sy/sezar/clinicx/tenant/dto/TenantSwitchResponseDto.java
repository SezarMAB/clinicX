package sy.sezar.clinicx.tenant.dto;

/**
 * Response DTO for tenant switching operation.
 */
public record TenantSwitchResponseDto(
    String accessToken,
    String refreshToken,
    String tenantId,
    String tenantName,
    String role,
    String message
) {}