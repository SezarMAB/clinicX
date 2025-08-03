package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for resetting a user's password.
 */
@Schema(description = "Request to reset a user's password")
public record ResetPasswordRequest(
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    @Schema(description = "New password", example = "NewSecurePass123!")
    String newPassword,
    
    @Schema(description = "Whether to force password change on next login", example = "true", defaultValue = "true")
    Boolean temporary
) {
    /**
     * Constructor with default value for temporary field.
     */
    public ResetPasswordRequest(String newPassword) {
        this(newPassword, true);
    }
}