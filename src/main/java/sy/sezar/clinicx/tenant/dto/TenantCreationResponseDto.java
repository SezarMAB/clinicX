package sy.sezar.clinicx.tenant.dto;

import java.util.UUID;

/**
 * Response DTO for tenant creation containing essential configuration details
 */
public record TenantCreationResponseDto(
    UUID id,
    String tenantId,
    String name,
    String subdomain,
    String realmName,
    String backendClientId,
    String backendClientSecret,
    String frontendClientId,
    String keycloakUrl,
    String adminUsername
) {}