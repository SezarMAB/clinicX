package sy.sezar.clinicx.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TenantCreateRequest(
    @NotBlank(message = "Tenant name is required")
    @Size(min = 3, max = 100, message = "Tenant name must be between 3 and 100 characters")
    String name,

    @NotBlank(message = "Subdomain is required")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
             message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
    @Size(min = 3, max = 50, message = "Subdomain must be between 3 and 50 characters")
    String subdomain,

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    String contactEmail,

    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    String contactPhone,

    String address,

    @NotBlank(message = "Subscription plan is required")
    String subscriptionPlan,

    Integer maxUsers,

    Integer maxPatients,

    @NotBlank(message = "Admin username is required")
    @Size(min = 3, max = 50, message = "Admin username must be between 3 and 50 characters")
    String adminUsername,

    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid admin email format")
    String adminEmail,

    @NotBlank(message = "Admin first name is required")
    String adminFirstName,

    @NotBlank(message = "Admin last name is required")
    String adminLastName,

    @NotBlank(message = "Admin password is required")
    @Size(min = 8, message = "Admin password must be at least 8 characters")
    String adminPassword,

    @Pattern(regexp = "^(CLINIC|DENTAL|APPOINTMENTS|CHRORG)$",
             message = "Specialty must be one of: CLINIC, DENTAL, APPOINTMENTS, CHRORG")
    String specialty
) {
    // Compact constructor for default values
    public TenantCreateRequest {
        if (maxUsers == null) {
            maxUsers = 10;
        }
        if (maxPatients == null) {
            maxPatients = 1000;
        }
        if (specialty == null) {
            specialty = "CLINIC";
        }
    }
}
