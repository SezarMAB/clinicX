package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for PaymentPlanInstallment entity.
 */
public record PaymentPlanInstallmentDto(
    UUID id,
    UUID paymentPlanId,
    Integer installmentNumber,
    LocalDate dueDate,
    BigDecimal amount,
    BigDecimal paidAmount,
    LocalDate paidDate,
    InstallmentStatus status,
    String notes,
    boolean isOverdue,
    int daysPastDue
) {}
