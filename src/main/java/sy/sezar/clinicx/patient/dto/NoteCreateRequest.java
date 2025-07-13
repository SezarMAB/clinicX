package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for creating a new patient note.
 */
public record NoteCreateRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotBlank(message = "Note content is required")
    @Size(max = 2000, message = "Note content cannot exceed 2000 characters")
    String content,

    @Size(max = 100, message = "Note type cannot exceed 100 characters")
    String noteType
) {}
