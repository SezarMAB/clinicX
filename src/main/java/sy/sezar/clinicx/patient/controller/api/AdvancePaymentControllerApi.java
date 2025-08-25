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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.*;

import java.util.UUID;

/**
 * REST API for managing advance payments (credits) and their application to invoices.
 */
@Tag(name = "Advance Payments", description = "Operations related to advance payments and credits")
@RequestMapping("/api/v1/advance-payments")
public interface AdvancePaymentControllerApi {

    @Operation(
            summary = "Create advance payment",
            description = "Creates a new advance payment (credit) for a patient"
    )
    @ApiResponse(responseCode = "201", description = "Advance payment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping
    ResponseEntity<AdvancePaymentDto> createAdvancePayment(@Valid @RequestBody AdvancePaymentCreateRequest request);

    @Operation(
            summary = "Apply advance payment to invoice",
            description = "Applies an advance payment (credit) to a specific invoice"
    )
    @ApiResponse(responseCode = "200", description = "Advance payment applied successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request or business rule violation", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Advance payment or invoice not found", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/apply")
    ResponseEntity<FinancialRecordDto> applyAdvancePaymentToInvoice(@Valid @RequestBody ApplyAdvancePaymentRequest request);

    @Operation(
            summary = "Get patient advance payments",
            description = "Retrieves all advance payments for a specific patient"
    )
    @ApiResponse(responseCode = "200", description = "Advance payments retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/patient/{patientId}")
    ResponseEntity<Page<AdvancePaymentDto>> getPatientAdvancePayments(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(description = "Pagination parameters") Pageable pageable
    );

    @Operation(
            summary = "Get unapplied advance payments",
            description = "Retrieves only unapplied advance payments for a specific patient"
    )
    @ApiResponse(responseCode = "200", description = "Unapplied advance payments retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/patient/{patientId}/unapplied")
    ResponseEntity<Page<AdvancePaymentDto>> getUnappliedAdvancePayments(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(description = "Pagination parameters") Pageable pageable
    );

    @Operation(
            summary = "Get patient credit balance",
            description = "Retrieves the credit balance summary for a specific patient"
    )
    @ApiResponse(responseCode = "200", description = "Credit balance retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/patient/{patientId}/balance")
    ResponseEntity<PatientCreditBalanceDto> getPatientCreditBalance(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId
    );

    @Operation(
            summary = "Auto-apply advance payments",
            description = "Automatically applies available advance payments to an unpaid invoice"
    )
    @ApiResponse(responseCode = "200", description = "Advance payments applied successfully")
    @ApiResponse(responseCode = "400", description = "Invoice is already paid or cancelled", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/invoice/{invoiceId}/auto-apply")
    ResponseEntity<FinancialRecordDto> autoApplyAdvancePaymentsToInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId
    );
}