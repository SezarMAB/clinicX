package sy.sezar.clinicx.clinic.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SpecialtyDto {
    private UUID id;
    private String name;
    private String description;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
