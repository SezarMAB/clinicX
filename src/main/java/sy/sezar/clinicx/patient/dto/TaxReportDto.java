package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for tax report.
 */
public record TaxReportDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal grossRevenue,
    BigDecimal taxableRevenue,
    BigDecimal nonTaxableRevenue,
    BigDecimal totalTax,
    BigDecimal netRevenue,
    List<TaxBreakdown> breakdown
) {
    public record TaxBreakdown(
        String category,
        BigDecimal amount,
        BigDecimal taxRate,
        BigDecimal taxAmount,
        String description
    ) {}
}