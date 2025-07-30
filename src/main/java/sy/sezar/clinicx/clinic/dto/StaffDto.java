package sy.sezar.clinicx.clinic.dto;

import lombok.Data;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
public class StaffDto {
    private UUID id;
    private String fullName;
    private StaffRole role;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    private Set<SpecialtyDto> specialties;
    private Instant createdAt;
    private Instant updatedAt;
}
