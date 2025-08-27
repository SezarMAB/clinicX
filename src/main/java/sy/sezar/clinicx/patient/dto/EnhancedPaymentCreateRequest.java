package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;
import sy.sezar.clinicx.patient.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Enhanced request DTO for creating payments with additional fields.
 */
public record EnhancedPaymentCreateRequest(
    @NotNull UUID patientId,
    UUID invoiceId,
    @NotNull LocalDate paymentDate,
    @NotNull @Positive BigDecimal amount,
    @NotNull PaymentMethod paymentMethod,
    @NotNull PaymentType type,
    @Size(max = 255) String description,
    @Size(max = 100) String referenceNumber,
    @Size(max = 100) String transactionId,
    @Size(max = 500) String gatewayResponse,
    @Size(max = 500) String notes
) {}
