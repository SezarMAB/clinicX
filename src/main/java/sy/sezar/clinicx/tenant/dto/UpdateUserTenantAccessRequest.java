package sy.sezar.clinicx.tenant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserTenantAccessRequest {
    private String role;
    private Boolean isPrimary;
    private Boolean isActive;
}