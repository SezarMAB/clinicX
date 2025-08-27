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
import sy.sezar.clinicx.patient.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Comprehensive payment management operations")
public interface PaymentControllerApi {

    @GetMapping
    @Operation(
        summary = "Get all payments",
        description = "Retrieves paginated list of payments with optional filtering by patient, invoice, type, and date range."
    )
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    ResponseEntity<Page<PaymentDto>> getAllPayments(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Filter by invoice ID")
            @RequestParam(required = false) UUID invoiceId,
            @Parameter(description = "Filter by payment type")
            @RequestParam(required = false) PaymentType type,
            @Parameter(description = "Filter by start date (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter by end date (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(hidden = true) @PageableDefault(sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{paymentId}")
    @Operation(
        summary = "Get payment by ID",
        description = "Retrieves detailed information about a specific payment."
    )
    @ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found")
    ResponseEntity<PaymentDto> getPayment(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID paymentId);

    @PostMapping
    @Operation(
        summary = "Create payment",
        description = "Creates a new payment record. Can be linked to an invoice or standalone (advance payment)."
    )
    @ApiResponse(responseCode = "201", description = "Payment created successfully",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient or invoice not found")
    ResponseEntity<PaymentDto> createPayment(
            @Valid @RequestBody PaymentCreateRequest request);

    @PutMapping("/{paymentId}")
    @Operation(
        summary = "Update payment",
        description = "Updates an existing payment record. Used for corrections and adjustments."
    )
    @ApiResponse(responseCode = "200", description = "Payment updated successfully",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @ApiResponse(responseCode = "409", description = "Cannot update payment - already reconciled or voided")
    ResponseEntity<PaymentDto> updatePayment(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID paymentId,
            @Valid @RequestBody PaymentUpdateRequest request);

    @DeleteMapping("/{paymentId}")
    @Operation(
        summary = "Void payment",
        description = "Voids a payment record. Payment is not deleted but marked as void for audit trail."
    )
    @ApiResponse(responseCode = "204", description = "Payment voided successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @ApiResponse(responseCode = "409", description = "Cannot void payment - already voided or reconciled")
    ResponseEntity<Void> voidPayment(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID paymentId);

    @GetMapping("/statistics")
    @Operation(
        summary = "Get payment statistics",
        description = "Retrieves comprehensive payment statistics for a patient or date range."
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                content = @Content(schema = @Schema(implementation = PaymentStatisticsDto.class)))
    ResponseEntity<PaymentStatisticsDto> getPaymentStatistics(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Start date for statistics")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for statistics")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PostMapping("/bulk")
    @Operation(
        summary = "Process bulk payments",
        description = "Processes multiple payments in a single transaction. Useful for batch processing."
    )
    @ApiResponse(responseCode = "201", description = "Bulk payments processed successfully")
    @ApiResponse(responseCode = "400", description = "Validation error in one or more payments")
    @ApiResponse(responseCode = "207", description = "Partial success - some payments processed, some failed")
    ResponseEntity<BulkPaymentResponse> processBulkPayments(
            @Valid @RequestBody BulkPaymentRequest request);

    @GetMapping("/methods/breakdown")
    @Operation(
        summary = "Get payment method breakdown",
        description = "Retrieves payment totals grouped by payment method."
    )
    @ApiResponse(responseCode = "200", description = "Breakdown retrieved successfully")
    ResponseEntity<Map<String, BigDecimal>> getPaymentMethodBreakdown(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Start date for breakdown")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for breakdown")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PostMapping("/{paymentId}/apply-to-invoice")
    @Operation(
        summary = "Apply payment to invoice",
        description = "Links an unallocated payment to a specific invoice."
    )
    @ApiResponse(responseCode = "200", description = "Payment applied successfully",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "400", description = "Payment already allocated or invoice fully paid")
    @ApiResponse(responseCode = "404", description = "Payment or invoice not found")
    ResponseEntity<PaymentDto> applyPaymentToInvoice(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID paymentId,
            @Parameter(description = "Invoice UUID", required = true)
            @RequestParam UUID invoiceId);

    @GetMapping("/unallocated")
    @Operation(
        summary = "Get unallocated payments",
        description = "Retrieves payments not yet applied to any invoice (advance payments/credits)."
    )
    @ApiResponse(responseCode = "200", description = "Unallocated payments retrieved successfully")
    ResponseEntity<Page<PaymentDto>> getUnallocatedPayments(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable);

    @PostMapping("/reconcile")
    @Operation(
        summary = "Reconcile payments",
        description = "Reconciles a list of payments with bank statements or external systems."
    )
    @ApiResponse(responseCode = "200", description = "Payments reconciled successfully")
    @ApiResponse(responseCode = "400", description = "Reconciliation errors")
    ResponseEntity<ReconciliationResultDto> reconcilePayments(
            @Valid @RequestBody ReconcilePaymentsRequest request);

    @PostMapping("/{paymentId}/allocate")
    @Operation(
        summary = "Allocate payment to multiple invoices",
        description = "Allocates a payment across one or more invoices by amount."
    )
    @ApiResponse(responseCode = "200", description = "Payment allocated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid allocation totals or invoice states")
    ResponseEntity<PaymentDto> allocatePayment(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID paymentId,
            @Valid @RequestBody java.util.List<sy.sezar.clinicx.patient.dto.PaymentAllocationItem> allocations);
}