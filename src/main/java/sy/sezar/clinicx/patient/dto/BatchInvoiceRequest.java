package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for batch invoice creation.
 */
public record BatchInvoiceRequest(
    @NotEmpty(message = "At least one patient is required")
    List<UUID> patientIds,
    
    LocalDate issueDate,
    LocalDate dueDate,
    boolean includeAllUnbilledTreatments,
    boolean sendNotifications
) {}