package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.PaymentPlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-plans")
@Tag(name = "Payment Plans", description = "Comprehensive payment plan and installment management")
public interface PaymentPlanControllerApi {

    @PostMapping
    @Operation(
        summary = "Create payment plan",
        description = "Creates a new payment plan for an invoice with automatic installment generation."
    )
    @ApiResponse(responseCode = "201", description = "Payment plan created successfully",
                content = @Content(schema = @Schema(implementation = PaymentPlanDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient or invoice not found")
    ResponseEntity<PaymentPlanDto> createPaymentPlan(
            @Valid @RequestBody PaymentPlanCreateRequest request);

    @GetMapping("/{paymentPlanId}")
    @Operation(
        summary = "Get payment plan by ID",
        description = "Retrieves detailed information about a specific payment plan."
    )
    @ApiResponse(responseCode = "200", description = "Payment plan retrieved successfully",
                content = @Content(schema = @Schema(implementation = PaymentPlanDto.class)))
    @ApiResponse(responseCode = "404", description = "Payment plan not found")
    ResponseEntity<PaymentPlanDto> getPaymentPlan(
            @Parameter(description = "Payment plan UUID", required = true)
            @PathVariable UUID paymentPlanId);

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient payment plans",
        description = "Retrieves paginated list of payment plans for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Payment plans retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<PaymentPlanDto>> getPatientPaymentPlans(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    @GetMapping
    @Operation(
        summary = "Get payment plans by status",
        description = "Retrieves paginated list of payment plans filtered by status."
    )
    @ApiResponse(responseCode = "200", description = "Payment plans retrieved successfully")
    ResponseEntity<Page<PaymentPlanDto>> getPaymentPlansByStatus(
            @Parameter(description = "Payment plan status")
            @RequestParam(required = false) PaymentPlanStatus status,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    @PutMapping("/{paymentPlanId}/status")
    @Operation(
        summary = "Update payment plan status",
        description = "Updates the status of a payment plan."
    )
    @ApiResponse(responseCode = "200", description = "Payment plan status updated successfully",
                content = @Content(schema = @Schema(implementation = PaymentPlanDto.class)))
    @ApiResponse(responseCode = "404", description = "Payment plan not found")
    ResponseEntity<PaymentPlanDto> updatePaymentPlanStatus(
            @Parameter(description = "Payment plan UUID", required = true)
            @PathVariable UUID paymentPlanId,
            @Parameter(description = "New status", required = true)
            @RequestParam PaymentPlanStatus status,
            @Parameter(description = "Status change reason")
            @RequestParam(required = false) String reason);

    @DeleteMapping("/{paymentPlanId}")
    @Operation(
        summary = "Cancel payment plan",
        description = "Cancels a payment plan and marks all pending installments as cancelled."
    )
    @ApiResponse(responseCode = "200", description = "Payment plan cancelled successfully",
                content = @Content(schema = @Schema(implementation = PaymentPlanDto.class)))
    @ApiResponse(responseCode = "404", description = "Payment plan not found")
    ResponseEntity<PaymentPlanDto> cancelPaymentPlan(
            @Parameter(description = "Payment plan UUID", required = true)
            @PathVariable UUID paymentPlanId,
            @Parameter(description = "Cancellation reason")
            @RequestParam(required = false) String reason);

    @GetMapping("/{paymentPlanId}/installments")
    @Operation(
        summary = "Get payment plan installments",
        description = "Retrieves all installments for a specific payment plan."
    )
    @ApiResponse(responseCode = "200", description = "Installments retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Payment plan not found")
    ResponseEntity<List<PaymentPlanInstallmentDto>> getPaymentPlanInstallments(
            @Parameter(description = "Payment plan UUID", required = true)
            @PathVariable UUID paymentPlanId);

    @PostMapping("/installments/{installmentId}/payments")
    @Operation(
        summary = "Record installment payment",
        description = "Records a payment for a specific installment."
    )
    @ApiResponse(responseCode = "200", description = "Payment recorded successfully",
                content = @Content(schema = @Schema(implementation = PaymentPlanInstallmentDto.class)))
    @ApiResponse(responseCode = "404", description = "Installment not found")
    ResponseEntity<PaymentPlanInstallmentDto> recordInstallmentPayment(
            @Parameter(description = "Installment UUID", required = true)
            @PathVariable UUID installmentId,
            @Parameter(description = "Payment amount", required = true)
            @RequestParam BigDecimal amount,
            @Parameter(description = "Payment date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @Parameter(description = "Payment notes")
            @RequestParam(required = false) String notes);

    @GetMapping("/installments/overdue")
    @Operation(
        summary = "Get overdue installments",
        description = "Retrieves paginated list of overdue installments across all payment plans."
    )
    @ApiResponse(responseCode = "200", description = "Overdue installments retrieved successfully")
    ResponseEntity<Page<PaymentPlanInstallmentDto>> getOverdueInstallments(
            @Parameter(hidden = true) @PageableDefault(sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable);

    @GetMapping("/installments/due-between")
    @Operation(
        summary = "Get installments due between dates",
        description = "Retrieves installments due within a specified date range."
    )
    @ApiResponse(responseCode = "200", description = "Installments retrieved successfully")
    ResponseEntity<List<PaymentPlanInstallmentDto>> getInstallmentsDueBetween(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PostMapping("/installments/mark-overdue")
    @Operation(
        summary = "Mark overdue installments",
        description = "Automatically marks installments as overdue based on due dates."
    )
    @ApiResponse(responseCode = "200", description = "Overdue installments marked successfully")
    ResponseEntity<Integer> markOverdueInstallments();

    @GetMapping("/statistics/patient/{patientId}")
    @Operation(
        summary = "Get payment plan statistics",
        description = "Retrieves comprehensive payment plan statistics for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                content = @Content(schema = @Schema(implementation = PaymentPlanStatisticsDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<PaymentPlanStatisticsDto> getPaymentPlanStatistics(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID patientId);

    @GetMapping("/reports")
    @Operation(
        summary = "Generate payment plan report",
        description = "Generates a comprehensive payment plan report with optional filtering."
    )
    @ApiResponse(responseCode = "200", description = "Report generated successfully",
                content = @Content(schema = @Schema(implementation = PaymentPlanReportDto.class)))
    ResponseEntity<PaymentPlanReportDto> generatePaymentPlanReport(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Start date for report")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for report")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
}
