package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for Payment entity.
 */
public record PaymentDto(
    UUID paymentId,
    UUID patientId,
    String patientName,
    UUID invoiceId,
    LocalDate paymentDate,
    BigDecimal amount,
    String paymentMethod,
    PaymentType type,
    String description,
    String referenceNumber,
    String createdBy,
    Instant createdAt
) {}