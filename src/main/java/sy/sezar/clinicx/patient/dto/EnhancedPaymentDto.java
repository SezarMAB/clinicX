package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.PaymentMethod;
import sy.sezar.clinicx.patient.model.enums.PaymentStatus;
import sy.sezar.clinicx.patient.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Enhanced Data Transfer Object for Payment entity with additional fields.
 */
public record EnhancedPaymentDto(
    UUID paymentId,
    UUID patientId,
    String patientName,
    UUID invoiceId,
    String invoiceNumber,
    LocalDate paymentDate,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    PaymentType type,
    PaymentStatus status,
    String description,
    String referenceNumber,
    String transactionId,
    String gatewayResponse,
    String createdBy,
    Instant createdAt,
    Instant updatedAt,
    boolean isReconciled,
    LocalDate reconciledDate,
    String reconciliationNotes
) {}
