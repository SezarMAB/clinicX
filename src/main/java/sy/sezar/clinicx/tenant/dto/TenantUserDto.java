package sy.sezar.clinicx.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTO representing a user within a tenant context.
 * Includes information about their roles and access permissions.
 */
@Schema(description = "User information within a tenant context")
public record TenantUserDto(
    @Schema(description = "User ID in Keycloak", example = "550e8400-e29b-41d4-a716-446655440000")
    String userId,
    
    @Schema(description = "Username", example = "john.doe")
    String username,
    
    @Schema(description = "Email address", example = "john.doe@clinic.com")
    String email,
    
    @Schema(description = "First name", example = "John")
    String firstName,
    
    @Schema(description = "Last name", example = "Doe")
    String lastName,
    
    @Schema(description = "Whether the user account is enabled", example = "true")
    boolean enabled,
    
    @Schema(description = "Whether the email is verified", example = "true")
    boolean emailVerified,
    
    @Schema(description = "Roles assigned to the user in this tenant", example = "[\"DOCTOR\", \"ADMIN\"]")
    List<String> roles,
    
    @Schema(description = "Primary tenant ID (user's home tenant)", example = "tenant-001")
    String primaryTenantId,
    
    @Schema(description = "Current active tenant ID", example = "tenant-001")
    String activeTenantId,
    
    @Schema(description = "Whether this is an external user (from another tenant)", example = "false")
    boolean isExternal,
    
    @Schema(description = "List of all tenants the user has access to")
    List<TenantAccessInfo> accessibleTenants,
    
    @Schema(description = "User attributes from Keycloak")
    Map<String, List<String>> attributes,
    
    @Schema(description = "Account creation timestamp")
    Instant createdAt,
    
    @Schema(description = "Last login timestamp")
    Instant lastLogin,
    
    @Schema(description = "User type in the context of this tenant", 
            allowableValues = {"INTERNAL", "EXTERNAL", "SUPER_ADMIN"})
    UserType userType
) {
    
    /**
     * Information about tenant access.
     */
    @Schema(description = "Information about user's access to a tenant")
    public record TenantAccessInfo(
        @Schema(description = "Tenant ID", example = "tenant-001")
        String tenantId,
        
        @Schema(description = "Tenant name", example = "Smile Dental Clinic")
        String tenantName,
        
        @Schema(description = "Clinic type", example = "DENTAL")
        String clinicType,
        
        @Schema(description = "Roles in this tenant", example = "[\"DOCTOR\"]")
        List<String> roles,
        
        @Schema(description = "Whether this is the primary tenant", example = "true")
        boolean isPrimary
    ) {}
    
    /**
     * User type enumeration.
     */
    public enum UserType {
        INTERNAL,      // User created within this tenant
        EXTERNAL,      // User from another tenant with access
        SUPER_ADMIN    // System-wide super admin
    }
}