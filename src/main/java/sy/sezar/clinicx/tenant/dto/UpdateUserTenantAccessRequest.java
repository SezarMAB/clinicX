package sy.sezar.clinicx.tenant.dto;

import lombok.Builder;
import lombok.Data;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;

@Data
@Builder
public class UpdateUserTenantAccessRequest {
    private Set<StaffRole> roles;
    private Boolean isPrimary;
    private Boolean isActive;
}