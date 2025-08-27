package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for payment plan reports.
 */
public record PaymentPlanReportDto(
    UUID patientId,
    String patientName,
    LocalDate reportDate,
    LocalDate startDate,
    LocalDate endDate,
    long totalPlans,
    long activePlans,
    long completedPlans,
    long defaultedPlans,
    BigDecimal totalAmount,
    BigDecimal totalPaidAmount,
    BigDecimal remainingAmount,
    long totalInstallments,
    long paidInstallments,
    long pendingInstallments,
    long overdueInstallments,
    List<PaymentPlanDto> paymentPlans,
    List<PaymentPlanInstallmentDto> overdueInstallmentDetails
) {}
