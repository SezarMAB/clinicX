package sy.sezar.clinicx.clinic.dto;

import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO that combines Staff data with UserTenantAccess information
 */
public record StaffWithAccessDto(
    // Staff fields
    UUID id,
    String fullName,
    StaffRole role,
    String email,
    String phoneNumber,
    boolean isActive,
    Set<SpecialtyDto> specialties,
    String keycloakUserId,
    String tenantId,
    
    // UserTenantAccess fields
    String accessRole,
    boolean isPrimary,
    boolean accessActive,
    
    // Metadata
    Instant createdAt,
    Instant updatedAt
) {}