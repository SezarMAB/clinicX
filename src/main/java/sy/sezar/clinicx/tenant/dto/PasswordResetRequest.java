package sy.sezar.clinicx.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for resetting tenant admin password.
 * Only accessible by SUPER_ADMIN.
 */
public record PasswordResetRequest(
    @NotBlank(message = "Admin username is required")
    String adminUsername,
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String newPassword
) {}