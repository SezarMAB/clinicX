package sy.sezar.clinicx.clinic.dto;

import lombok.Builder;
import lombok.Data;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO that combines Staff data with UserTenantAccess information
 */
@Data
@Builder
public class StaffWithAccessDto {
    // Staff fields
    private UUID id;
    private String fullName;
    private StaffRole role;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    private Set<SpecialtyDto> specialties;
    private String keycloakUserId;
    private String tenantId;
    
    // UserTenantAccess fields
    private String accessRole;
    private boolean isPrimary;
    private boolean accessActive;
    
    // Metadata
    private Instant createdAt;
    private Instant updatedAt;
}