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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoice-management")
@Tag(name = "Invoice Management", description = "Advanced invoice management and operations")
public interface InvoiceManagementControllerApi {

    @PostMapping("/generate-from-treatments")
    @Operation(
        summary = "Generate invoice from treatments",
        description = "Creates a new invoice from selected treatments that haven't been billed yet."
    )
    @ApiResponse(responseCode = "201", description = "Invoice generated successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or treatments already billed")
    @ApiResponse(responseCode = "404", description = "Patient or treatments not found")
    ResponseEntity<InvoiceDto> generateInvoiceFromTreatments(
            @Valid @RequestBody GenerateInvoiceRequest request);

    @PutMapping("/{invoiceId}/status")
    @Operation(
        summary = "Update invoice status",
        description = "Updates the status of an invoice. Some status transitions may trigger additional actions."
    )
    @ApiResponse(responseCode = "200", description = "Invoice status updated successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid status transition")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    @ApiResponse(responseCode = "409", description = "Status transition not allowed")
    ResponseEntity<InvoiceDto> updateInvoiceStatus(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Parameter(description = "New invoice status", required = true)
            @RequestParam InvoiceStatus status,
            @Parameter(description = "Reason for status change")
            @RequestParam(required = false) String reason);

    @PostMapping("/{invoiceId}/cancel")
    @Operation(
        summary = "Cancel invoice",
        description = "Cancels an invoice with a reason. Cancelled invoices cannot be modified."
    )
    @ApiResponse(responseCode = "200", description = "Invoice cancelled successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "400", description = "Cannot cancel paid invoice")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<InvoiceDto> cancelInvoice(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Valid @RequestBody CancelInvoiceRequest request);

    @PostMapping("/{invoiceId}/reminder")
    @Operation(
        summary = "Send invoice reminder",
        description = "Sends a payment reminder for an unpaid invoice to the patient."
    )
    @ApiResponse(responseCode = "204", description = "Reminder sent successfully")
    @ApiResponse(responseCode = "400", description = "Invoice is already paid")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<Void> sendInvoiceReminder(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Parameter(description = "Reminder type")
            @RequestParam(required = false, defaultValue = "STANDARD") String reminderType);

    @PostMapping("/mark-overdue")
    @Operation(
        summary = "Mark overdue invoices",
        description = "Batch process to mark all invoices past due date as overdue."
    )
    @ApiResponse(responseCode = "200", description = "Overdue invoices marked successfully")
    ResponseEntity<List<InvoiceDto>> markOverdueInvoices();

    @GetMapping("/unpaid")
    @Operation(
        summary = "Get unpaid invoices",
        description = "Retrieves all unpaid invoices with optional patient filter."
    )
    @ApiResponse(responseCode = "200", description = "Unpaid invoices retrieved successfully")
    ResponseEntity<Page<InvoiceDto>> getUnpaidInvoices(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Include partially paid invoices")
            @RequestParam(required = false, defaultValue = "true") boolean includePartiallyPaid,
            @Parameter(hidden = true) @PageableDefault(sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable);

    @GetMapping("/aging-report")
    @Operation(
        summary = "Get invoice aging report",
        description = "Generates an aging report showing outstanding invoices by age brackets."
    )
    @ApiResponse(responseCode = "200", description = "Aging report generated successfully",
                content = @Content(schema = @Schema(implementation = InvoiceAgingReportDto.class)))
    ResponseEntity<InvoiceAgingReportDto> getAgingReport(
            @Parameter(description = "Report as of date (defaults to today)")
            @RequestParam(required = false) String asOfDate,
            @Parameter(description = "Include details per patient")
            @RequestParam(required = false, defaultValue = "false") boolean includeDetails);

    @PostMapping("/{invoiceId}/discount")
    @Operation(
        summary = "Apply discount to invoice",
        description = "Applies a discount to an invoice. Can be percentage or fixed amount."
    )
    @ApiResponse(responseCode = "200", description = "Discount applied successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid discount or invoice already paid")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<InvoiceDto> applyDiscount(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Valid @RequestBody DiscountRequest request);

    @PostMapping("/{invoiceId}/add-items")
    @Operation(
        summary = "Add items to invoice",
        description = "Adds additional items to an existing invoice (if not paid)."
    )
    @ApiResponse(responseCode = "200", description = "Items added successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "400", description = "Cannot modify paid invoice")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<InvoiceDto> addItemsToInvoice(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Valid @RequestBody AddInvoiceItemsRequest request);

    @DeleteMapping("/{invoiceId}/items/{itemId}")
    @Operation(
        summary = "Remove item from invoice",
        description = "Removes an item from an invoice (if not paid)."
    )
    @ApiResponse(responseCode = "200", description = "Item removed successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "400", description = "Cannot modify paid invoice")
    @ApiResponse(responseCode = "404", description = "Invoice or item not found")
    ResponseEntity<InvoiceDto> removeItemFromInvoice(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Parameter(description = "Invoice item UUID", required = true)
            @PathVariable UUID itemId);

    @PostMapping("/batch-invoice")
    @Operation(
        summary = "Create batch invoices",
        description = "Creates invoices for multiple patients with unbilled treatments."
    )
    @ApiResponse(responseCode = "201", description = "Batch invoices created successfully")
    @ApiResponse(responseCode = "207", description = "Partial success - some invoices created, some failed")
    ResponseEntity<BatchInvoiceResponse> createBatchInvoices(
            @Valid @RequestBody BatchInvoiceRequest request);

    @GetMapping("/{invoiceId}/payment-history")
    @Operation(
        summary = "Get invoice payment history",
        description = "Retrieves all payments made against a specific invoice."
    )
    @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<Page<PaymentDto>> getInvoicePaymentHistory(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Parameter(hidden = true) @PageableDefault(sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable);

    @PostMapping("/{invoiceId}/clone")
    @Operation(
        summary = "Clone invoice",
        description = "Creates a copy of an existing invoice with a new invoice number."
    )
    @ApiResponse(responseCode = "201", description = "Invoice cloned successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<InvoiceDto> cloneInvoice(
            @Parameter(description = "Invoice UUID to clone", required = true)
            @PathVariable UUID invoiceId,
            @Parameter(description = "New issue date")
            @RequestParam(required = false) String issueDate,
            @Parameter(description = "New due date")
            @RequestParam(required = false) String dueDate);

    @PostMapping("/{invoiceId}/write-off")
    @Operation(
        summary = "Apply write-off to invoice",
        description = "Applies a write-off amount to an invoice and adjusts amount due."
    )
    @ApiResponse(responseCode = "200", description = "Write-off applied successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<InvoiceDto> applyWriteOff(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Valid @RequestBody sy.sezar.clinicx.patient.dto.WriteOffRequest request);

    @PostMapping("/{invoiceId}/credit-note")
    @Operation(
        summary = "Issue credit note",
        description = "Issues a credit note that reduces the invoice due amount."
    )
    @ApiResponse(responseCode = "200", description = "Credit note applied successfully",
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    ResponseEntity<InvoiceDto> createCreditNote(
            @Parameter(description = "Invoice UUID", required = true)
            @PathVariable UUID invoiceId,
            @Valid @RequestBody sy.sezar.clinicx.patient.dto.CreditNoteRequest request);
}