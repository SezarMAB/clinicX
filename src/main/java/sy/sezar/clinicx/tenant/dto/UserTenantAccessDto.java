package sy.sezar.clinicx.tenant.dto;

import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserTenantAccessDto(
    UUID id,
    String userId,
    String tenantId,
    String tenantName,
    Set<StaffRole> roles,
    boolean isPrimary,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy
) {}