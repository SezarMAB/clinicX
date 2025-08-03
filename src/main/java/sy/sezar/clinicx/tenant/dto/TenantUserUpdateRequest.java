package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Request DTO for updating an existing user.
 */
@Schema(description = "Request to update an existing user")
public record TenantUserUpdateRequest(
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john.doe@clinic.com")
    String email,
    
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "First name", example = "John")
    String firstName,
    
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "Last name", example = "Doe")
    String lastName,
    
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    @Schema(description = "Phone number", example = "+1234567890")
    String phoneNumber,
    
    @Schema(description = "Whether the user account is enabled", example = "true")
    Boolean enabled,
    
    @Schema(description = "Additional attributes to update")
    Map<String, String> attributes
) {}