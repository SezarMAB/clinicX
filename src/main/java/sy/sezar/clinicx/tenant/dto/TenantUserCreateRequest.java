package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating a new user in a tenant.
 */
@Schema(description = "Request to create a new user in the tenant")
public record TenantUserCreateRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    @Schema(description = "Username for login", example = "john.doe")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john.doe@clinic.com")
    String email,
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "First name", example = "John")
    String firstName,
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "Last name", example = "Doe")
    String lastName,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    @Schema(description = "Initial password", example = "SecurePass123!")
    String password,
    
    @NotEmpty(message = "At least one role is required")
    @Schema(description = "Roles to assign to the user", example = "[\"DOCTOR\"]")
    List<String> roles,
    
    @Schema(description = "Phone number", example = "+1234567890")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    String phoneNumber,
    
    @Schema(description = "Whether to force password change on first login", example = "true", defaultValue = "true")
    Boolean temporaryPassword,
    
    @Schema(description = "Whether to send welcome email", example = "true", defaultValue = "true")
    Boolean sendWelcomeEmail,
    
    @Schema(description = "Additional attributes for the user")
    Map<String, String> additionalAttributes
) {
    /**
     * Constructor with default values for optional fields.
     */
    public TenantUserCreateRequest(
            String username,
            String email,
            String firstName,
            String lastName,
            String password,
            List<String> roles,
            String phoneNumber,
            Map<String, String> additionalAttributes) {
        this(username, email, firstName, lastName, password, roles, phoneNumber, true, true, additionalAttributes);
    }
}