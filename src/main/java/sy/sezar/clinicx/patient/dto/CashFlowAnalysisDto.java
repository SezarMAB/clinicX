package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for cash flow analysis.
 */
public record CashFlowAnalysisDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    BigDecimal netCashFlow,
    BigDecimal cashInflows,
    BigDecimal cashOutflows,
    Map<String, BigDecimal> cashFlowByMonth,
    List<CashFlowDetailDto> cashFlowDetails,
    BigDecimal averageDailyCashFlow,
    BigDecimal cashFlowVariance,
    BigDecimal cashFlowTrend
) {}
