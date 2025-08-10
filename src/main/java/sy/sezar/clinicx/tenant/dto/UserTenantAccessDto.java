package sy.sezar.clinicx.tenant.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserTenantAccessDto {
    private UUID id;
    private String userId;
    private String tenantId;
    private String tenantName;
    private String role;
    private boolean isPrimary;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}