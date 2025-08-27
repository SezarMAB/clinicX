package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.PaymentPlanStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for PaymentPlan entity.
 */
public record PaymentPlanDto(
    UUID id,
    UUID patientId,
    String patientName,
    UUID invoiceId,
    String invoiceNumber,
    String planName,
    BigDecimal totalAmount,
    Integer installmentCount,
    BigDecimal installmentAmount,
    LocalDate startDate,
    LocalDate endDate,
    Integer frequencyDays,
    PaymentPlanStatus status,
    String notes,
    String createdBy,
    Instant createdAt,
    Instant updatedAt,
    List<PaymentPlanInstallmentDto> installments,
    BigDecimal totalPaidAmount,
    BigDecimal remainingAmount,
    Integer paidInstallments,
    Integer remainingInstallments
) {}
