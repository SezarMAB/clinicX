package sy.sezar.clinicx.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialtyCreateRequest(
    @NotBlank(message = "Specialty name is required")
    @Size(max = 100, message = "Specialty name must not exceed 100 characters")
    String name,
    
    String description
) {}
