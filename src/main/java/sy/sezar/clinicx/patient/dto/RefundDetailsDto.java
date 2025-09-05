package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Detailed DTO for refund information.
 */
public record RefundDetailsDto(
    UUID id,
    UUID patientId,
    String patientName,
    UUID invoiceId,
    String invoiceNumber,
    BigDecimal amount,
    LocalDate refundDate,
    String paymentMethod,
    String status,
    String reason,
    String referenceNumber,
    UUID originalPaymentId,
    BigDecimal originalPaymentAmount,
    LocalDate originalPaymentDate,
    String approvedBy,
    Instant approvedAt,
    String rejectedBy,
    Instant rejectedAt,
    String rejectionReason,
    UUID createdById,
    String createdByName,
    Instant createdAt,
    Instant updatedAt
) {}