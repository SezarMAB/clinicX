package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for revenue analytics.
 */
public record RevenueAnalyticsDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalRevenue,
    BigDecimal totalInvoiced,
    BigDecimal totalCollected,
    BigDecimal outstandingAmount,
    BigDecimal averageInvoiceAmount,
    BigDecimal averageCollectionTime,
    Map<String, BigDecimal> revenueByMonth,
    Map<String, BigDecimal> revenueByTreatment,
    Map<String, BigDecimal> revenueByDoctor,
    List<RevenueTrendDto> revenueTrends,
    BigDecimal growthRate,
    BigDecimal collectionRate
) {}
