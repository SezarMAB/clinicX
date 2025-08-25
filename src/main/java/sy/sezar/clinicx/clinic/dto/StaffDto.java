package sy.sezar.clinicx.clinic.dto;

import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record StaffDto(
    UUID id,
    String fullName,
    Set<StaffRole> roles,
    String email,
    String phoneNumber,
    boolean isActive,
    Set<SpecialtyDto> specialties,
    String keycloakUserId,
    String tenantId,
    // Access control fields from user_tenant_access (will be populated when needed)
    Set<StaffRole> accessRoles,
    Boolean isPrimary,
    Boolean accessActive,
    Instant createdAt,
    Instant updatedAt
) {}
