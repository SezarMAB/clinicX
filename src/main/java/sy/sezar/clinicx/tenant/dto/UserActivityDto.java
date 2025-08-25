package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

/**
 * DTO representing user activity/audit log entry.
 */
@Schema(description = "User activity log entry")
public record UserActivityDto(
    @Schema(description = "Activity ID", example = "act-123456")
    String activityId,
    
    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    String userId,
    
    @Schema(description = "Username", example = "john.doe")
    String username,
    
    @Schema(description = "Activity type", example = "LOGIN")
    ActivityType activityType,
    
    @Schema(description = "Activity description", example = "User logged in successfully")
    String description,
    
    @Schema(description = "IP address", example = "192.168.1.100")
    String ipAddress,
    
    @Schema(description = "User agent", example = "Mozilla/5.0...")
    String userAgent,
    
    @Schema(description = "Activity timestamp")
    Instant timestamp,
    
    @Schema(description = "Tenant ID where activity occurred", example = "tenant-001")
    String tenantId,
    
    @Schema(description = "Additional activity details")
    Map<String, Object> details,
    
    @Schema(description = "Whether the activity was successful", example = "true")
    boolean success
) {
    
    /**
     * Activity type enumeration.
     */
    public enum ActivityType {
        LOGIN,
        LOGOUT,
        PASSWORD_CHANGE,
        PASSWORD_RESET,
        ACCOUNT_LOCKED,
        ACCOUNT_UNLOCKED,
        ROLE_CHANGE,
        PROFILE_UPDATE,
        TENANT_SWITCH,
        ACCESS_GRANTED,
        ACCESS_REVOKED,
        FAILED_LOGIN,
        PERMISSION_DENIED,
        DATA_ACCESS,
        DATA_MODIFICATION,
        EXPORT_DATA,
        API_ACCESS
    }
}