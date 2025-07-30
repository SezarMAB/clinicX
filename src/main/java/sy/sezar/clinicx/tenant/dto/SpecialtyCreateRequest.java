package sy.sezar.clinicx.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new specialty type.
 */
public record SpecialtyCreateRequest(
    @NotBlank(message = "Specialty code is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Code must be uppercase letters and underscores only")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    String code,
    
    @NotBlank(message = "Specialty name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,
    
    @NotNull(message = "Features array is required")
    @Size(min = 1, message = "At least one feature must be specified")
    String[] features,
    
    @Pattern(regexp = "^[a-z-]+$", message = "Realm name must be lowercase letters and hyphens only")
    @Size(max = 100, message = "Realm name must not exceed 100 characters")
    String realmName
) {
    // Compact constructor for default values
    public SpecialtyCreateRequest {
        if (realmName == null || realmName.isEmpty()) {
            realmName = code.toLowerCase() + "-realm";
        }
    }
}