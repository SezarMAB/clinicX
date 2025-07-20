package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing patient note.
 */
public record NoteUpdateRequest(
    @NotBlank(message = "Note content is required")
    @Size(max = 2000, message = "Note content cannot exceed 2000 characters")
    String content,

    @Size(max = 100, message = "Note type cannot exceed 100 characters")
    String noteType
) {}
