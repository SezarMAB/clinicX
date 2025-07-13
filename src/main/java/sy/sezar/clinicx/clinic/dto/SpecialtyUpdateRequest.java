package sy.sezar.clinicx.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpecialtyUpdateRequest {
    
    @NotBlank(message = "Specialty name is required")
    @Size(max = 100, message = "Specialty name must not exceed 100 characters")
    private String name;
    
    private String description;
    
    private boolean isActive;
}
