package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for generating billing and financial reports.
 */
public interface BillingReportService {

    /**
     * Generate comprehensive revenue report.
     */
    RevenueReportDto generateRevenueReport(LocalDate startDate, LocalDate endDate, 
                                          String groupBy, boolean includeProcedureBreakdown, 
                                          boolean includeDoctorBreakdown);

    /**
     * Get outstanding patient balances.
     */
    Page<PatientBalanceDto> getOutstandingBalances(BigDecimal minBalance, 
                                                   boolean includeZeroBalances, 
                                                   String sortBy, Pageable pageable);

    /**
     * Generate collection report.
     */
    CollectionReportDto generateCollectionReport(LocalDate startDate, LocalDate endDate,
                                                 boolean includePaymentMethods, 
                                                 boolean includeDailyBreakdown);

    /**
     * Get insurance claims.
     */
    Page<InsuranceClaimDto> getInsuranceClaims(String status, String provider,
                                               LocalDate startDate, LocalDate endDate,
                                               Pageable pageable);

    /**
     * Generate daily cash report.
     */
    DailyCashReportDto generateDailyCashReport(LocalDate date, boolean includeNonCash,
                                               boolean groupByStaff);

    /**
     * Generate patient financial statement.
     */
    PatientStatementDto generatePatientStatement(UUID patientId, LocalDate startDate,
                                                 LocalDate endDate, boolean includeTreatmentDetails);

    /**
     * Generate procedure analysis report.
     */
    ProcedureAnalysisDto generateProcedureAnalysis(LocalDate startDate, LocalDate endDate,
                                                   UUID specialtyId, Integer minCount);

    /**
     * Generate doctor performance report.
     */
    DoctorPerformanceDto generateDoctorPerformance(LocalDate startDate, LocalDate endDate,
                                                   UUID doctorId, boolean includeProcedures);

    /**
     * Analyze payment trends.
     */
    PaymentTrendsDto analyzePaymentTrends(LocalDate startDate, LocalDate endDate, String interval);

    /**
     * Generate tax report.
     */
    TaxReportDto generateTaxReport(LocalDate startDate, LocalDate endDate, boolean includeDetails);

    /**
     * Export report in various formats.
     */
    byte[] exportReport(String reportType, String format, ExportReportRequest request);
}