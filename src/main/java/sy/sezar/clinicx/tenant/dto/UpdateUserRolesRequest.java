package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;

/**
 * Request DTO for updating user roles.
 * Uses StaffRole enum for type safety and validation.
 */
@Schema(description = "Request to update user roles")
public record UpdateUserRolesRequest(
    @NotEmpty(message = "At least one role is required")
    @Schema(description = "New roles to assign to the user", 
            example = "[\"DOCTOR\", \"ADMIN\"]",
            allowableValues = {"SUPER_ADMIN", "ADMIN", "DOCTOR", "NURSE", "ASSISTANT", "RECEPTIONIST", "ACCOUNTANT", "EXTERNAL", "INTERNAL"})
    Set<StaffRole> roles
) {}