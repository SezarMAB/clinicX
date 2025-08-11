package sy.sezar.clinicx.clinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClinicInfoUpdateRequest(
    @NotBlank(message = "Clinic name is required")
    @Size(max = 255, message = "Clinic name must not exceed 255 characters")
    String name,
    
    String address,
    
    @Size(max = 50, message = "Phone number must not exceed 50 characters")
    String phoneNumber,
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,
    
    @NotBlank(message = "Timezone is required")
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    String timezone
) {}
