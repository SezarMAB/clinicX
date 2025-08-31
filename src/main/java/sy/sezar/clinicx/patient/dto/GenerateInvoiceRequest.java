package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for generating an invoice from procedures.
 */
public record GenerateInvoiceRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Procedure IDs are required")
    @NotEmpty(message = "At least one procedure ID is required")
    List<UUID> procedureIds,

    LocalDate issueDate,
    LocalDate dueDate,
    String notes,
    boolean sendNotification,
    boolean autoApplyCredits
) {}
