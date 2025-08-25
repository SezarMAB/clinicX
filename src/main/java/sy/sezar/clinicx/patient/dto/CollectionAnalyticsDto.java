package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Data Transfer Object for collection analytics.
 */
public record CollectionAnalyticsDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalCollected,
    BigDecimal totalOutstanding,
    BigDecimal collectionRate,
    BigDecimal averageCollectionTime,
    Map<String, BigDecimal> collectionsByMethod,
    Map<String, BigDecimal> collectionsByMonth,
    BigDecimal overdueAmount,
    BigDecimal overduePercentage,
    int totalInvoices,
    int paidInvoices,
    int overdueInvoices
) {}
