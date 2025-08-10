package sy.sezar.clinicx.clinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;
import java.util.UUID;

@Data
public class StaffCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotNull(message = "Role is required")
    private StaffRole role;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phoneNumber;

    private Set<UUID> specialtyIds;
    
    // Keycloak user creation fields
    private boolean createKeycloakUser = false;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    private String username; // Optional - defaults to email if not provided
    
    private String firstName; // Optional - extracted from fullName if not provided
    
    private String lastName; // Optional - extracted from fullName if not provided
    
    // Keycloak user ID - optional, if provided will link to existing user
    private String keycloakUserId;
    
    // Access role for user_tenant_access - defaults to staff role if not provided
    private String accessRole;
    
    // Whether this should be the primary tenant for the user
    private boolean isPrimaryTenant = true;
}
