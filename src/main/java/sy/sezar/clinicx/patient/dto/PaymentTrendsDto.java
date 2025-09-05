package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for payment trends analysis.
 */
public record PaymentTrendsDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalAmount,
    int totalPayments,
    BigDecimal averagePaymentAmount,
    double averageDaysToPayment,
    List<TrendData> trends,
    String trendDirection,
    double growthRate
) {
    public record TrendData(
        String period,
        LocalDate periodStart,
        LocalDate periodEnd,
        int paymentCount,
        BigDecimal totalAmount,
        BigDecimal averageAmount,
        double daysToPayment
    ) {}
}