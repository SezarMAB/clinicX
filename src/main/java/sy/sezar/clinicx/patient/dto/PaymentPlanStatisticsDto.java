package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object for payment plan statistics.
 */
public record PaymentPlanStatisticsDto(
    UUID patientId,
    String patientName,
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
    long overdueInstallments
) {}
