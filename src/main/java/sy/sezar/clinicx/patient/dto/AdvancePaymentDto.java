package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.PaymentType;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO representing an advance payment or credit.
 */
public record AdvancePaymentDto(
    UUID id,
    UUID patientId,
    String patientName,
    BigDecimal amount,
    BigDecimal remainingCredit,
    LocalDate paymentDate,
    PaymentMethod paymentMethod,
    PaymentType type,
    String description,
    String referenceNumber,
    boolean isFullyApplied,
    String createdBy,
    String createdAt
) {}