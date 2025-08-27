package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for revenue report.
 */
public record RevenueReportDto(
    BigDecimal totalRevenue,
    BigDecimal totalCollected,
    BigDecimal totalOutstanding,
    BigDecimal totalRefunded,
    BigDecimal netRevenue,
    LocalDate startDate,
    LocalDate endDate,
    List<RevenueByPeriod> periods,
    Map<String, BigDecimal> byProcedure,
    Map<String, BigDecimal> byDoctor,
    Map<String, BigDecimal> bySpecialty
) {
    public record RevenueByPeriod(
        String period,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal revenue,
        BigDecimal collected,
        BigDecimal outstanding,
        int invoiceCount,
        int paymentCount
    ) {}
}