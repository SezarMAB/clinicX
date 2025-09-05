package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Payment summary for a patient
 */
public record PaymentSummaryDto(
    BigDecimal totalPayments,
    BigDecimal totalRefunds,
    BigDecimal totalCredits,
    BigDecimal netAmount,
    BigDecimal outstandingBalance,
    Integer paymentCount,
    LocalDate lastPaymentDate,
    Map<String, BigDecimal> paymentMethodBreakdown
) {}