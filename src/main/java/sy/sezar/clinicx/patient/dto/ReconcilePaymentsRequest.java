package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for reconciling payments.
 */
public record ReconcilePaymentsRequest(
    @NotNull(message = "Payment IDs are required")
    @NotEmpty(message = "At least one payment ID is required")
    List<UUID> paymentIds,
    
    LocalDate reconciliationDate,
    String referenceNumber,
    String notes
) {}