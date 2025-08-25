package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing-reports")
@Tag(name = "Billing Reports", description = "Comprehensive billing and financial reporting endpoints")
public interface BillingReportControllerApi {

    @GetMapping("/revenue")
    @Operation(
        summary = "Get revenue report",
        description = "Generates a comprehensive revenue report for the specified date range."
    )
    @ApiResponse(responseCode = "200", description = "Revenue report generated successfully",
                content = @Content(schema = @Schema(implementation = RevenueReportDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    ResponseEntity<RevenueReportDto> getRevenueReport(
            @Parameter(description = "Start date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Group results by (DAY, WEEK, MONTH, QUARTER, YEAR)")
            @RequestParam(required = false, defaultValue = "MONTH") String groupBy,
            @Parameter(description = "Include procedure breakdown")
            @RequestParam(required = false, defaultValue = "true") boolean includeProcedureBreakdown,
            @Parameter(description = "Include doctor breakdown")
            @RequestParam(required = false, defaultValue = "true") boolean includeDoctorBreakdown);

    @GetMapping("/outstanding-balances")
    @Operation(
        summary = "Get outstanding balances report",
        description = "Retrieves a report of all patients with outstanding balances."
    )
    @ApiResponse(responseCode = "200", description = "Outstanding balances retrieved successfully")
    ResponseEntity<Page<PatientBalanceDto>> getOutstandingBalances(
            @Parameter(description = "Minimum balance threshold")
            @RequestParam(required = false, defaultValue = "0") Double minBalance,
            @Parameter(description = "Include zero balances")
            @RequestParam(required = false, defaultValue = "false") boolean includeZeroBalances,
            @Parameter(description = "Sort by (BALANCE, NAME, LAST_PAYMENT_DATE)")
            @RequestParam(required = false, defaultValue = "BALANCE") String sortBy,
            @Parameter(hidden = true) @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/collections")
    @Operation(
        summary = "Get collections report",
        description = "Generates a payment collections report showing money collected vs billed."
    )
    @ApiResponse(responseCode = "200", description = "Collections report generated successfully",
                content = @Content(schema = @Schema(implementation = CollectionReportDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    ResponseEntity<CollectionReportDto> getCollectionReport(
            @Parameter(description = "Start date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Include payment method breakdown")
            @RequestParam(required = false, defaultValue = "true") boolean includePaymentMethods,
            @Parameter(description = "Include daily breakdown")
            @RequestParam(required = false, defaultValue = "false") boolean includeDailyBreakdown);

    @GetMapping("/insurance-claims")
    @Operation(
        summary = "Get insurance claims report",
        description = "Retrieves a report of all insurance claims and their statuses."
    )
    @ApiResponse(responseCode = "200", description = "Insurance claims report retrieved successfully")
    ResponseEntity<Page<InsuranceClaimDto>> getInsuranceClaims(
            @Parameter(description = "Filter by claim status")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by insurance provider")
            @RequestParam(required = false) String provider,
            @Parameter(description = "Start date for claims")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for claims")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(hidden = true) @PageableDefault(sort = "claimDate", direction = Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/daily-cash")
    @Operation(
        summary = "Get daily cash report",
        description = "Generates a daily cash report showing all cash transactions for a specific date."
    )
    @ApiResponse(responseCode = "200", description = "Daily cash report generated successfully",
                content = @Content(schema = @Schema(implementation = DailyCashReportDto.class)))
    ResponseEntity<DailyCashReportDto> getDailyCashReport(
            @Parameter(description = "Date for report (defaults to today)", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Include non-cash payments")
            @RequestParam(required = false, defaultValue = "false") boolean includeNonCash,
            @Parameter(description = "Group by staff member")
            @RequestParam(required = false, defaultValue = "true") boolean groupByStaff);

    @GetMapping("/patient/{patientId}/statement")
    @Operation(
        summary = "Get patient statement",
        description = "Generates a comprehensive financial statement for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Patient statement generated successfully",
                content = @Content(schema = @Schema(implementation = PatientStatementDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<PatientStatementDto> getPatientStatement(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(description = "Start date for statement")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for statement")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Include treatment details")
            @RequestParam(required = false, defaultValue = "true") boolean includeTreatmentDetails);

    @GetMapping("/procedure-analysis")
    @Operation(
        summary = "Get procedure analysis report",
        description = "Analyzes procedure profitability and frequency."
    )
    @ApiResponse(responseCode = "200", description = "Procedure analysis generated successfully",
                content = @Content(schema = @Schema(implementation = ProcedureAnalysisDto.class)))
    ResponseEntity<ProcedureAnalysisDto> getProcedureAnalysis(
            @Parameter(description = "Start date for analysis", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for analysis", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter by specialty")
            @RequestParam(required = false) UUID specialtyId,
            @Parameter(description = "Minimum procedure count")
            @RequestParam(required = false, defaultValue = "1") Integer minCount);

    @GetMapping("/doctor-performance")
    @Operation(
        summary = "Get doctor performance report",
        description = "Generates performance metrics for doctors including revenue and patient count."
    )
    @ApiResponse(responseCode = "200", description = "Doctor performance report generated successfully",
                content = @Content(schema = @Schema(implementation = DoctorPerformanceDto.class)))
    ResponseEntity<DoctorPerformanceDto> getDoctorPerformance(
            @Parameter(description = "Start date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter by doctor ID")
            @RequestParam(required = false) UUID doctorId,
            @Parameter(description = "Include procedure breakdown")
            @RequestParam(required = false, defaultValue = "true") boolean includeProcedures);

    @GetMapping("/payment-trends")
    @Operation(
        summary = "Get payment trends",
        description = "Analyzes payment trends over time including average payment time and amounts."
    )
    @ApiResponse(responseCode = "200", description = "Payment trends generated successfully",
                content = @Content(schema = @Schema(implementation = PaymentTrendsDto.class)))
    ResponseEntity<PaymentTrendsDto> getPaymentTrends(
            @Parameter(description = "Start date for analysis", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for analysis", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Trend interval (DAILY, WEEKLY, MONTHLY)")
            @RequestParam(required = false, defaultValue = "MONTHLY") String interval);

    @GetMapping("/tax-report")
    @Operation(
        summary = "Get tax report",
        description = "Generates a tax report for the specified period."
    )
    @ApiResponse(responseCode = "200", description = "Tax report generated successfully",
                content = @Content(schema = @Schema(implementation = TaxReportDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    ResponseEntity<TaxReportDto> getTaxReport(
            @Parameter(description = "Start date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for report", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Include detailed breakdown")
            @RequestParam(required = false, defaultValue = "true") boolean includeDetails);

    @PostMapping("/export/{reportType}")
    @Operation(
        summary = "Export report",
        description = "Exports a billing report in various formats (PDF, Excel, CSV)."
    )
    @ApiResponse(responseCode = "200", description = "Report exported successfully")
    @ApiResponse(responseCode = "400", description = "Invalid report type or parameters")
    ResponseEntity<byte[]> exportReport(
            @Parameter(description = "Type of report to export", required = true)
            @PathVariable String reportType,
            @Parameter(description = "Export format (PDF, EXCEL, CSV)")
            @RequestParam(defaultValue = "PDF") String format,
            @RequestBody ExportReportRequest request);
}