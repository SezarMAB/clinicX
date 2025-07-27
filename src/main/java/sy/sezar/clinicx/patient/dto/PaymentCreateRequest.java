package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new payment.
 */
public record PaymentCreateRequest(
    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be positive")
    BigDecimal amount,

    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,

    @NotNull(message = "Payment method is required")
    @Size(max = 50, message = "Payment method cannot exceed 50 characters")
    String paymentMethod,

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes,

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    String referenceNumber
) {}
