package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for payment statistics and analytics.
 */
public record PaymentStatisticsDto(
    BigDecimal totalCollected,
    BigDecimal totalRefunded,
    BigDecimal totalCredits,
    BigDecimal netAmount,
    Integer paymentCount,
    Integer refundCount,
    Integer creditCount,
    BigDecimal averagePaymentAmount,
    LocalDate firstPaymentDate,
    LocalDate lastPaymentDate,
    Map<String, BigDecimal> byPaymentMethod,
    Map<String, BigDecimal> byMonth,
    Map<String, Integer> countByType
) {}