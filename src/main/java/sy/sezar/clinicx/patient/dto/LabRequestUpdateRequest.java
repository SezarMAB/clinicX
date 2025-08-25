package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO for updating an existing lab request.
 */
public record LabRequestUpdateRequest(
    @Size(max = 100, message = "Lab name cannot exceed 100 characters")
    String labName,

    @Size(max = 100, message = "Test type cannot exceed 100 characters")
    String testType,

    @Size(max = 500, message = "Instructions cannot exceed 500 characters")
    String instructions,

    LocalDate expectedCompletionDate,

    @Size(max = 200, message = "Priority cannot exceed 200 characters")
    String priority,

    @Size(max = 50, message = "Status cannot exceed 50 characters")
    String status,

    @Size(max = 1000, message = "Results cannot exceed 1000 characters")
    String results
) {}
