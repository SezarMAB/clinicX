package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for financial performance metrics.
 */
public record FinancialPerformanceMetricsDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalRevenue,
    BigDecimal totalExpenses,
    BigDecimal netProfit,
    BigDecimal grossProfit,
    BigDecimal profitMargin,
    BigDecimal revenueGrowth,
    BigDecimal expenseGrowth,
    BigDecimal profitGrowth,
    BigDecimal returnOnInvestment,
    BigDecimal debtToEquityRatio,
    BigDecimal currentRatio,
    BigDecimal quickRatio,
    BigDecimal workingCapital,
    BigDecimal cashFlowFromOperations,
    BigDecimal cashFlowFromInvesting,
    BigDecimal cashFlowFromFinancing,
    BigDecimal netCashFlow,
    BigDecimal daysSalesOutstanding,
    BigDecimal daysPayableOutstanding,
    BigDecimal inventoryTurnover,
    BigDecimal assetTurnover,
    BigDecimal equityMultiplier
) {}
