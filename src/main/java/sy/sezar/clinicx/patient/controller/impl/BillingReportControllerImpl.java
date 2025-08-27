package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.patient.controller.api.BillingReportControllerApi;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.service.BillingReportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of BillingReportControllerApi for comprehensive billing and financial reporting.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BillingReportControllerImpl implements BillingReportControllerApi {

    private final BillingReportService billingReportService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<RevenueReportDto> getRevenueReport(
            LocalDate startDate,
            LocalDate endDate,
            String groupBy,
            boolean includeProcedureBreakdown,
            boolean includeDoctorBreakdown) {
        log.info("Generating revenue report from {} to {}", startDate, endDate);
        RevenueReportDto report = billingReportService.generateRevenueReport(
            startDate, endDate, groupBy, includeProcedureBreakdown, includeDoctorBreakdown);
        return ResponseEntity.ok(report);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<PatientBalanceDto>> getOutstandingBalances(
            Double minBalance,
            boolean includeZeroBalances,
            String sortBy,
            Pageable pageable) {
        log.debug("Getting outstanding balances with minBalance: {}", minBalance);
        Page<PatientBalanceDto> balances = billingReportService.getOutstandingBalances(
            BigDecimal.valueOf(minBalance != null ? minBalance : 0),
            includeZeroBalances,
            sortBy,
            pageable);
        return ResponseEntity.ok(balances);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<CollectionReportDto> getCollectionReport(
            LocalDate startDate,
            LocalDate endDate,
            boolean includePaymentMethods,
            boolean includeDailyBreakdown) {
        log.info("Generating collection report from {} to {}", startDate, endDate);
        CollectionReportDto report = billingReportService.generateCollectionReport(
            startDate, endDate, includePaymentMethods, includeDailyBreakdown);
        return ResponseEntity.ok(report);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<InsuranceClaimDto>> getInsuranceClaims(
            String status,
            String provider,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        log.debug("Getting insurance claims with status: {}, provider: {}", status, provider);
        Page<InsuranceClaimDto> claims = billingReportService.getInsuranceClaims(
            status, provider, startDate, endDate, pageable);
        return ResponseEntity.ok(claims);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<DailyCashReportDto> getDailyCashReport(
            LocalDate date,
            boolean includeNonCash,
            boolean groupByStaff) {
        log.info("Generating daily cash report for: {}", date);
        LocalDate reportDate = date != null ? date : LocalDate.now();
        DailyCashReportDto report = billingReportService.generateDailyCashReport(
            reportDate, includeNonCash, groupByStaff);
        return ResponseEntity.ok(report);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<PatientStatementDto> getPatientStatement(
            UUID patientId,
            LocalDate startDate,
            LocalDate endDate,
            boolean includeTreatmentDetails) {
        log.info("Generating patient statement for: {}", patientId);
        PatientStatementDto statement = billingReportService.generatePatientStatement(
            patientId, startDate, endDate, includeTreatmentDetails);
        return ResponseEntity.ok(statement);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProcedureAnalysisDto> getProcedureAnalysis(
            LocalDate startDate,
            LocalDate endDate,
            UUID specialtyId,
            Integer minCount) {
        log.info("Generating procedure analysis from {} to {}", startDate, endDate);
        ProcedureAnalysisDto analysis = billingReportService.generateProcedureAnalysis(
            startDate, endDate, specialtyId, minCount);
        return ResponseEntity.ok(analysis);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorPerformanceDto> getDoctorPerformance(
            LocalDate startDate,
            LocalDate endDate,
            UUID doctorId,
            boolean includeProcedures) {
        log.info("Generating doctor performance report from {} to {}", startDate, endDate);
        DoctorPerformanceDto performance = billingReportService.generateDoctorPerformance(
            startDate, endDate, doctorId, includeProcedures);
        return ResponseEntity.ok(performance);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentTrendsDto> getPaymentTrends(
            LocalDate startDate,
            LocalDate endDate,
            String interval) {
        log.info("Analyzing payment trends from {} to {}", startDate, endDate);
        PaymentTrendsDto trends = billingReportService.analyzePaymentTrends(
            startDate, endDate, interval);
        return ResponseEntity.ok(trends);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaxReportDto> getTaxReport(
            LocalDate startDate,
            LocalDate endDate,
            boolean includeDetails) {
        log.info("Generating tax report from {} to {}", startDate, endDate);
        TaxReportDto report = billingReportService.generateTaxReport(
            startDate, endDate, includeDetails);
        return ResponseEntity.ok(report);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<byte[]> exportReport(
            String reportType,
            String format,
            ExportReportRequest request) {
        log.info("Exporting {} report in {} format", reportType, format);
        
        byte[] reportData = billingReportService.exportReport(reportType, format, request);
        
        HttpHeaders headers = new HttpHeaders();
        String filename = String.format("%s_%s.%s", 
            reportType, 
            LocalDate.now(), 
            format.toLowerCase());
        
        MediaType mediaType = switch (format.toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF;
            case "EXCEL", "XLSX" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "CSV" -> MediaType.parseMediaType("text/csv");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
        
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(reportData.length);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(reportData);
    }
}