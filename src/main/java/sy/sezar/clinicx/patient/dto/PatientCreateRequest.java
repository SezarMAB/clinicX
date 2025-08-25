package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request DTO for creating a new patient from the registration form.
 */
public record PatientCreateRequest(
    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    String fullName,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    @Size(max = 10, message = "Gender must not exceed 10 characters")
    String gender,

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    String phoneNumber,

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    String address,

    @Size(max = 100, message = "Insurance provider must not exceed 100 characters")
    String insuranceProvider,

    @Size(max = 50, message = "Insurance number must not exceed 50 characters")
    String insuranceNumber,

    String importantMedicalNotes
) {}
