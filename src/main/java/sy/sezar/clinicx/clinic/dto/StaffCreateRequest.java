package sy.sezar.clinicx.clinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;
import java.util.UUID;

public record StaffCreateRequest(
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    String fullName,

    @NotNull(message = "Role is required")
    StaffRole role,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    String phoneNumber,

    Set<UUID> specialtyIds,
    
    // Keycloak user creation fields
    boolean createKeycloakUser,
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,
    
    String username, // Optional - defaults to email if not provided
    
    String firstName, // Optional - extracted from fullName if not provided
    
    String lastName, // Optional - extracted from fullName if not provided
    
    // Keycloak user ID - optional, if provided will link to existing user
    String keycloakUserId,
    
    // Access role for user_tenant_access - defaults to staff role if not provided
    String accessRole,
    
    // Whether this should be the primary tenant for the user
    boolean isPrimaryTenant
) {}
