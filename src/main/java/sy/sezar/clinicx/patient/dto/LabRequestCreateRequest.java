package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating a new lab request.
 */
public record LabRequestCreateRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Request date is required")
    LocalDate requestDate,

    @NotNull(message = "Lab name is required")
    @Size(max = 100, message = "Lab name cannot exceed 100 characters")
    String labName,

    @NotNull(message = "Test type is required")
    @Size(max = 100, message = "Test type cannot exceed 100 characters")
    String testType,

    @Size(max = 500, message = "Instructions cannot exceed 500 characters")
    String instructions,

    LocalDate expectedCompletionDate,

    @Size(max = 200, message = "Priority cannot exceed 200 characters")
    String priority
) {}
