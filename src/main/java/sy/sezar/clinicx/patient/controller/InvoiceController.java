package sy.sezar.clinicx.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.FinancialRecordDto;
import sy.sezar.clinicx.patient.dto.InvoiceCreateRequest;
import sy.sezar.clinicx.patient.dto.PaymentCreateRequest;
import sy.sezar.clinicx.patient.service.InvoiceService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Invoices", description = "Operations related to invoice and payment management")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(
        summary = "Create new invoice",
        description = "Creates a new invoice for a patient with auto-generated invoice number."
    )
    @ApiResponse(responseCode = "201", description = "Invoice created",
                content = @Content(schema = @Schema(implementation = FinancialRecordDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<FinancialRecordDto> createInvoice(
            @Valid @RequestBody InvoiceCreateRequest request) {
        log.info("Creating new invoice for patient ID: {}", request.patientId());
        // Calculate total amount from items and extract description
        BigDecimal totalAmount = request.items().stream()
            .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        String description = request.notes() != null ? request.notes() : "Invoice for dental services";

        FinancialRecordDto invoice = invoiceService.createInvoice(request.patientId(), totalAmount, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @PostMapping("/{invoiceId}/payments")
    @Operation(
        summary = "Add payment to invoice",
        description = "Records a payment against an existing invoice and updates patient balance."
    )
    @ApiResponse(responseCode = "201", description = "Payment recorded",
                content = @Content(schema = @Schema(implementation = FinancialRecordDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<FinancialRecordDto> addPayment(
            @Parameter(name = "invoiceId", description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Valid @RequestBody PaymentCreateRequest request) {
        log.info("Adding payment to invoice ID: {} with amount: {}", invoiceId, request.amount());
        FinancialRecordDto payment = invoiceService.addPayment(invoiceId, request.amount(), request.paymentMethod());
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient financial records",
        description = "Retrieves paginated financial records (invoices and payments) for a patient.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: invoiceDate", example = "invoiceDate")
        }
    )
    @ApiResponse(responseCode = "200", description = "Financial records retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<FinancialRecordDto>> getPatientFinancialRecords(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Retrieving financial records for patient ID: {} with pagination: {}", patientId, pageable);
        Page<FinancialRecordDto> records = invoiceService.getPatientFinancialRecords(patientId, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/next-invoice-number")
    @Operation(
        summary = "Get next invoice number",
        description = "Retrieves the next sequential invoice number for preview purposes."
    )
    @ApiResponse(responseCode = "200", description = "Next invoice number retrieved")
    public ResponseEntity<String> getNextInvoiceNumber() {
        log.info("Retrieving next invoice number");
        String nextNumber = invoiceService.getNextInvoiceNumber();
        return ResponseEntity.ok(nextNumber);
    }

    @PostMapping("/patient/{patientId}/recalculate-balance")
    @Operation(
        summary = "Recalculate patient balance",
        description = "Manually recalculates patient balance based on all invoices and payments."
    )
    @ApiResponse(responseCode = "200", description = "Balance recalculated")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<BigDecimal> recalculatePatientBalance(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId) {
        log.info("Recalculating balance for patient ID: {}", patientId);
        BigDecimal newBalance = invoiceService.recalculatePatientBalance(patientId);
        return ResponseEntity.ok(newBalance);
    }
}
