package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for generating an invoice from treatments.
 */
public record GenerateInvoiceRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Visit IDs are required")
    @NotEmpty(message = "At least one treatment ID is required")
    List<UUID> treatmentIds,

    LocalDate issueDate,
    LocalDate dueDate,
    String notes,
    boolean sendNotification,
    boolean autoApplyCredits
) {}
