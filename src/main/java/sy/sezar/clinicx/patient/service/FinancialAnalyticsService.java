package sy.sezar.clinicx.patient.service;

import sy.sezar.clinicx.patient.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for comprehensive financial analytics and reporting.
 */
public interface FinancialAnalyticsService {

    /**
     * Get comprehensive financial summary for a patient.
     *
     * @param patientId Patient ID
     * @return Financial summary
     */
    PatientFinancialSummaryDto getPatientFinancialSummary(UUID patientId);

    /**
     * Get revenue analytics for a date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Revenue analytics
     */
    RevenueAnalyticsDto getRevenueAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * Get payment collection analytics.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Collection analytics
     */
    CollectionAnalyticsDto getCollectionAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * Get accounts receivable aging report.
     *
     * @param asOfDate As of date
     * @return Aging report
     */
    AccountsReceivableAgingDto getAccountsReceivableAging(LocalDate asOfDate);

    /**
     * Get payment method distribution.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Payment method distribution
     */
    Map<String, BigDecimal> getPaymentMethodDistribution(LocalDate startDate, LocalDate endDate);

    /**
     * Get treatment cost analysis.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Treatment cost analysis
     */
    TreatmentCostAnalysisDto getTreatmentCostAnalysis(LocalDate startDate, LocalDate endDate);

    /**
     * Get patient payment trends.
     *
     * @param patientId Patient ID
     * @param months Number of months to analyze
     * @return Payment trends
     */
    PaymentTrendsDto getPatientPaymentTrends(UUID patientId, int months);

    /**
     * Get cash flow analysis.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Cash flow analysis
     */
    CashFlowAnalysisDto getCashFlowAnalysis(LocalDate startDate, LocalDate endDate);

    /**
     * Get outstanding balances report.
     *
     * @param threshold Minimum balance threshold
     * @return Outstanding balances
     */
    OutstandingBalancesDto getOutstandingBalances(BigDecimal threshold);

    /**
     * Get financial performance metrics.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Performance metrics
     */
    FinancialPerformanceMetricsDto getFinancialPerformanceMetrics(LocalDate startDate, LocalDate endDate);
}
