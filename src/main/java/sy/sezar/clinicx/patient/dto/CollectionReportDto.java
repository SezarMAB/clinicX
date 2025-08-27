package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for collection report.
 */
public record CollectionReportDto(
    BigDecimal totalBilled,
    BigDecimal totalCollected,
    BigDecimal totalOutstanding,
    BigDecimal collectionRate,
    LocalDate startDate,
    LocalDate endDate,
    Map<String, BigDecimal> byPaymentMethod,
    List<DailyCollection> dailyBreakdown
) {
    public record DailyCollection(
        LocalDate date,
        BigDecimal billed,
        BigDecimal collected,
        BigDecimal outstanding,
        int paymentCount,
        int invoiceCount
    ) {}
}