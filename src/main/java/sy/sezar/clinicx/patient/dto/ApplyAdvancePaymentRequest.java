package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for applying advance payment to an invoice.
 */
public record ApplyAdvancePaymentRequest(
    @NotNull(message = "Advance payment ID is required")
    UUID advancePaymentId,

    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,

    @NotNull(message = "Amount to apply is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amountToApply
) {}