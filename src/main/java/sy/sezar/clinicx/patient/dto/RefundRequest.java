package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for processing payment refunds.
 */
public record RefundRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,
    
    UUID invoiceId,
    
    @NotNull(message = "Refund amount is required")
    @Positive(message = "Refund amount must be positive")
    BigDecimal amount,

    @NotNull(message = "Refund reason is required")
    @Size(min = 3, max = 500, message = "Refund reason must be between 3 and 500 characters")
    String reason,

    PaymentMethod paymentMethod,

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    String referenceNumber,

    LocalDate refundDate
) {}