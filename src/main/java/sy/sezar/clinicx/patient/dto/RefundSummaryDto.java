package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for refund summary statistics.
 */
public record RefundSummaryDto(
    BigDecimal totalRefundAmount,
    int totalRefundCount,
    int pendingCount,
    int approvedCount,
    int rejectedCount,
    int completedCount,
    BigDecimal averageRefundAmount,
    LocalDate startDate,
    LocalDate endDate,
    Map<String, BigDecimal> byPaymentMethod,
    Map<String, BigDecimal> byPeriod,
    Map<String, Integer> byStatus
) {}