package sy.sezar.clinicx.clinic.dto;

import java.time.Instant;
import java.util.UUID;

public record SpecialtyDto(
    UUID id,
    String name,
    String description,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {}
