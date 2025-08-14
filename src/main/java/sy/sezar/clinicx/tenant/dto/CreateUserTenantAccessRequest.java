package sy.sezar.clinicx.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;

@Data
@Builder
public class CreateUserTenantAccessRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Tenant ID is required")
    private String tenantId;
    
    @NotNull(message = "Roles are required")
    @Size(min = 1, message = "At least one role is required")
    private Set<StaffRole> roles;
    
    private boolean isPrimary;
    
    @Builder.Default
    private boolean isActive = true;
}