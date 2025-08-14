package sy.sezar.clinicx.clinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;
import java.util.UUID;

public record StaffUpdateRequest(
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    String fullName,

    @NotNull(message = "Roles are required")
    @Size(min = 1, message = "At least one role is required")
    Set<StaffRole> roles,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    String phoneNumber,

    boolean isActive,

    Set<UUID> specialtyIds
) {}
