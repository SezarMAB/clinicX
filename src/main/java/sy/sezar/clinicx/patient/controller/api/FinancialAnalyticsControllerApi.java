package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial-analytics")
@Tag(name = "Financial Analytics", description = "Comprehensive financial analytics and reporting")
public interface FinancialAnalyticsControllerApi {

    @GetMapping("/patient/{patientId}/summary")
    @Operation(
        summary = "Get patient financial summary",
        description = "Retrieves comprehensive financial summary for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Financial summary retrieved successfully",
                content = @Content(schema = @Schema(implementation = PatientFinancialSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<PatientFinancialSummaryDto> getPatientFinancialSummary(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID patientId);

    @GetMapping("/revenue")
    @Operation(
        summary = "Get revenue analytics",
        description = "Retrieves comprehensive revenue analytics for a date range."
    )
    @ApiResponse(responseCode = "200", description = "Revenue analytics retrieved successfully",
                content = @Content(schema = @Schema(implementation = RevenueAnalyticsDto.class)))
    ResponseEntity<RevenueAnalyticsDto> getRevenueAnalytics(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/collections")
    @Operation(
        summary = "Get collection analytics",
        description = "Retrieves payment collection analytics for a date range."
    )
    @ApiResponse(responseCode = "200", description = "Collection analytics retrieved successfully",
                content = @Content(schema = @Schema(implementation = CollectionAnalyticsDto.class)))
    ResponseEntity<CollectionAnalyticsDto> getCollectionAnalytics(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/accounts-receivable/aging")
    @Operation(
        summary = "Get accounts receivable aging",
        description = "Retrieves accounts receivable aging report as of a specific date."
    )
    @ApiResponse(responseCode = "200", description = "Aging report retrieved successfully",
                content = @Content(schema = @Schema(implementation = AccountsReceivableAgingDto.class)))
    ResponseEntity<AccountsReceivableAgingDto> getAccountsReceivableAging(
            @Parameter(description = "As of date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate);

    @GetMapping("/payment-methods/distribution")
    @Operation(
        summary = "Get payment method distribution",
        description = "Retrieves payment method distribution for a date range."
    )
    @ApiResponse(responseCode = "200", description = "Payment method distribution retrieved successfully")
    ResponseEntity<Map<String, BigDecimal>> getPaymentMethodDistribution(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/treatment-costs")
    @Operation(
        summary = "Get treatment cost analysis",
        description = "Retrieves comprehensive treatment cost analysis for a date range."
    )
    @ApiResponse(responseCode = "200", description = "Treatment cost analysis retrieved successfully",
                content = @Content(schema = @Schema(implementation = TreatmentCostAnalysisDto.class)))
    ResponseEntity<TreatmentCostAnalysisDto> getTreatmentCostAnalysis(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/patient/{patientId}/payment-trends")
    @Operation(
        summary = "Get patient payment trends",
        description = "Retrieves payment trends for a specific patient over a number of months."
    )
    @ApiResponse(responseCode = "200", description = "Payment trends retrieved successfully",
                content = @Content(schema = @Schema(implementation = PaymentTrendsDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<PaymentTrendsDto> getPatientPaymentTrends(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(description = "Number of months to analyze", required = true)
            @RequestParam int months);

    @GetMapping("/cash-flow")
    @Operation(
        summary = "Get cash flow analysis",
        description = "Retrieves comprehensive cash flow analysis for a date range."
    )
    @ApiResponse(responseCode = "200", description = "Cash flow analysis retrieved successfully",
                content = @Content(schema = @Schema(implementation = CashFlowAnalysisDto.class)))
    ResponseEntity<CashFlowAnalysisDto> getCashFlowAnalysis(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/outstanding-balances")
    @Operation(
        summary = "Get outstanding balances report",
        description = "Retrieves outstanding balances report with optional threshold filtering."
    )
    @ApiResponse(responseCode = "200", description = "Outstanding balances report retrieved successfully",
                content = @Content(schema = @Schema(implementation = OutstandingBalancesDto.class)))
    ResponseEntity<OutstandingBalancesDto> getOutstandingBalances(
            @Parameter(description = "Minimum balance threshold")
            @RequestParam(required = false, defaultValue = "0") BigDecimal threshold);

    @GetMapping("/performance-metrics")
    @Operation(
        summary = "Get financial performance metrics",
        description = "Retrieves comprehensive financial performance metrics for a date range."
    )
    @ApiResponse(responseCode = "200", description = "Performance metrics retrieved successfully",
                content = @Content(schema = @Schema(implementation = FinancialPerformanceMetricsDto.class)))
    ResponseEntity<FinancialPerformanceMetricsDto> getFinancialPerformanceMetrics(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/dashboard")
    @Operation(
        summary = "Get financial dashboard",
        description = "Retrieves key financial metrics for dashboard display."
    )
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully")
    ResponseEntity<Map<String, Object>> getFinancialDashboard(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
}
