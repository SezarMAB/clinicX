package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for granting external user access to a tenant.
 */
@Schema(description = "Request to grant external user access to the tenant")
public record GrantExternalAccessRequest(
    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the external user", example = "external.doctor")
    String username,
    
    @NotEmpty(message = "At least one role is required")
    @Schema(description = "Roles to assign to the external user in this tenant", 
            example = "[\"DOCTOR\"]")
    List<String> roles,
    
    @Schema(description = "Optional note about why access is being granted", 
            example = "Visiting specialist from partner clinic")
    String accessNote
) {}