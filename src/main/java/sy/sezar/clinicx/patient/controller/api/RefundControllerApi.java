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

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refunds")
@Tag(name = "Refunds", description = "Refund processing and management operations")
public interface RefundControllerApi {

    @PostMapping
    @Operation(
        summary = "Process refund",
        description = "Creates a new refund for a patient. Can be linked to a specific invoice or general account credit."
    )
    @ApiResponse(responseCode = "201", description = "Refund processed successfully",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or insufficient funds")
    @ApiResponse(responseCode = "404", description = "Patient or invoice not found")
    ResponseEntity<PaymentDto> processRefund(
            @Valid @RequestBody RefundRequest request);

    @GetMapping
    @Operation(
        summary = "Get refunds",
        description = "Retrieves paginated list of refunds with optional filtering."
    )
    @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully")
    ResponseEntity<Page<PaymentDto>> getRefunds(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Filter by start date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter by end date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter by status (PENDING, APPROVED, REJECTED, COMPLETED)")
            @RequestParam(required = false) String status,
            @Parameter(hidden = true) @PageableDefault(sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable);

    @PostMapping("/{refundId}/approve")
    @Operation(
        summary = "Approve refund",
        description = "Approves a pending refund request. Requires appropriate authorization."
    )
    @ApiResponse(responseCode = "200", description = "Refund approved successfully",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "400", description = "Refund already processed")
    @ApiResponse(responseCode = "404", description = "Refund not found")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions to approve refund")
    ResponseEntity<PaymentDto> approveRefund(
            @Parameter(description = "Refund UUID", required = true)
            @PathVariable UUID refundId,
            @Parameter(description = "Approval notes")
            @RequestParam(required = false) String approvalNotes);

    @PostMapping("/{refundId}/reject")
    @Operation(
        summary = "Reject refund",
        description = "Rejects a pending refund request with a reason."
    )
    @ApiResponse(responseCode = "200", description = "Refund rejected successfully",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "400", description = "Refund already processed")
    @ApiResponse(responseCode = "404", description = "Refund not found")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions to reject refund")
    ResponseEntity<PaymentDto> rejectRefund(
            @Parameter(description = "Refund UUID", required = true)
            @PathVariable UUID refundId,
            @Parameter(description = "Rejection reason", required = true)
            @RequestParam String rejectionReason);

    @DeleteMapping("/{refundId}")
    @Operation(
        summary = "Cancel refund",
        description = "Cancels a pending refund request. Only pending refunds can be cancelled."
    )
    @ApiResponse(responseCode = "204", description = "Refund cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Cannot cancel processed refund")
    @ApiResponse(responseCode = "404", description = "Refund not found")
    ResponseEntity<Void> cancelRefund(
            @Parameter(description = "Refund UUID", required = true)
            @PathVariable UUID refundId,
            @Parameter(description = "Cancellation reason")
            @RequestParam(required = false) String cancellationReason);

    @GetMapping("/{refundId}")
    @Operation(
        summary = "Get refund details",
        description = "Retrieves detailed information about a specific refund."
    )
    @ApiResponse(responseCode = "200", description = "Refund details retrieved successfully",
                content = @Content(schema = @Schema(implementation = RefundDetailsDto.class)))
    @ApiResponse(responseCode = "404", description = "Refund not found")
    ResponseEntity<RefundDetailsDto> getRefundDetails(
            @Parameter(description = "Refund UUID", required = true)
            @PathVariable UUID refundId);

    @PostMapping("/batch")
    @Operation(
        summary = "Process batch refunds",
        description = "Processes multiple refunds in a single transaction."
    )
    @ApiResponse(responseCode = "201", description = "Batch refunds processed successfully")
    @ApiResponse(responseCode = "207", description = "Partial success - some refunds processed, some failed")
    @ApiResponse(responseCode = "400", description = "Validation errors in batch request")
    ResponseEntity<BatchRefundResponse> processBatchRefunds(
            @Valid @RequestBody BatchRefundRequest request);

    @GetMapping("/pending-approval")
    @Operation(
        summary = "Get pending refunds",
        description = "Retrieves all refunds pending approval."
    )
    @ApiResponse(responseCode = "200", description = "Pending refunds retrieved successfully")
    ResponseEntity<Page<PaymentDto>> getPendingRefunds(
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable);

    @PostMapping("/{refundId}/process")
    @Operation(
        summary = "Process approved refund",
        description = "Processes an approved refund to completion (e.g., bank transfer, cash return)."
    )
    @ApiResponse(responseCode = "200", description = "Refund processed to completion",
                content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    @ApiResponse(responseCode = "400", description = "Refund not approved or already processed")
    @ApiResponse(responseCode = "404", description = "Refund not found")
    ResponseEntity<PaymentDto> processApprovedRefund(
            @Parameter(description = "Refund UUID", required = true)
            @PathVariable UUID refundId,
            @Valid @RequestBody ProcessRefundRequest request);

    @GetMapping("/summary")
    @Operation(
        summary = "Get refund summary",
        description = "Retrieves refund summary statistics for a given period."
    )
    @ApiResponse(responseCode = "200", description = "Refund summary retrieved successfully",
                content = @Content(schema = @Schema(implementation = RefundSummaryDto.class)))
    ResponseEntity<RefundSummaryDto> getRefundSummary(
            @Parameter(description = "Start date for summary")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for summary")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Group by period (DAY, WEEK, MONTH)")
            @RequestParam(required = false, defaultValue = "MONTH") String groupBy);
}