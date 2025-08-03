package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for updating user roles.
 */
@Schema(description = "Request to update user roles")
public record UpdateUserRolesRequest(
    @NotEmpty(message = "At least one role is required")
    @Schema(description = "New roles to assign to the user", 
            example = "[\"DOCTOR\", \"ADMIN\"]")
    List<String> roles
) {}