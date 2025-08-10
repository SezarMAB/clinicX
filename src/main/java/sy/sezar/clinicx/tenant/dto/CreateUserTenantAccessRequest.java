package sy.sezar.clinicx.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserTenantAccessRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Tenant ID is required")
    private String tenantId;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    private boolean isPrimary;
    
    @Builder.Default
    private boolean isActive = true;
}