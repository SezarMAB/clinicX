package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request DTO for updating patient information from the basic info tab.
 */
public record PatientUpdateRequest(
    @NotBlank
    @Size(max = 150)
    String fullName,

    @NotNull
    LocalDate dateOfBirth,

    @Size(max = 10)
    String gender,

    @Size(max = 30)
    String phoneNumber,

    @Email
    @Size(max = 100)
    String email,

    String address,

    @Size(max = 100)
    String insuranceProvider,

    @Size(max = 50)
    String insuranceNumber,

    String importantMedicalNotes
) {}
