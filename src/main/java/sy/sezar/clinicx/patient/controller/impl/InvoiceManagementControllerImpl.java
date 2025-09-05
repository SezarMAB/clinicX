package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.patient.controller.api.InvoiceManagementControllerApi;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import sy.sezar.clinicx.patient.service.InvoiceService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of InvoiceManagementControllerApi for advanced invoice management.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InvoiceManagementControllerImpl implements InvoiceManagementControllerApi {

    private final InvoiceService invoiceService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> generateInvoiceFromTreatments(GenerateInvoiceRequest request) {
        log.info("Generating invoice from treatments for patient: {}", request.patientId());
        InvoiceDto invoice = invoiceService.generateInvoiceFromTreatments(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> updateInvoiceStatus(
            UUID invoiceId,
            InvoiceStatus status,
            String reason) {
        log.info("Updating invoice {} status to: {}", invoiceId, status);
        InvoiceDto invoice = invoiceService.updateInvoiceStatus(invoiceId, status, reason);
        return ResponseEntity.ok(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> cancelInvoice(UUID invoiceId, CancelInvoiceRequest request) {
        log.info("Cancelling invoice: {}", invoiceId);
        InvoiceDto invoice = invoiceService.cancelInvoice(invoiceId, request.reason());
        return ResponseEntity.ok(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> sendInvoiceReminder(UUID invoiceId, String reminderType) {
        log.info("Sending {} reminder for invoice: {}", reminderType, invoiceId);
        invoiceService.sendInvoiceReminder(invoiceId, reminderType);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceDto>> markOverdueInvoices() {
        log.info("Marking overdue invoices");
        List<InvoiceDto> overdueInvoices = invoiceService.markOverdueInvoices();
        return ResponseEntity.ok(overdueInvoices);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<Page<InvoiceDto>> getUnpaidInvoices(
            UUID patientId,
            boolean includePartiallyPaid,
            Pageable pageable) {
        log.debug("Getting unpaid invoices for patient: {}", patientId);
        Page<InvoiceDto> invoices = invoiceService.getUnpaidInvoices(
            patientId, includePartiallyPaid, pageable);
        return ResponseEntity.ok(invoices);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceAgingReportDto> getAgingReport(String asOfDate, boolean includeDetails) {
        log.debug("Generating aging report as of: {}", asOfDate);
        LocalDate reportDate = asOfDate != null ? LocalDate.parse(asOfDate) : LocalDate.now();
        InvoiceAgingReportDto report = invoiceService.generateAgingReport(reportDate, includeDetails);
        return ResponseEntity.ok(report);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> applyDiscount(UUID invoiceId, DiscountRequest request) {
        log.info("Applying discount to invoice: {}", invoiceId);
        InvoiceDto invoice = invoiceService.applyDiscount(invoiceId, request);
        return ResponseEntity.ok(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> addItemsToInvoice(UUID invoiceId, AddInvoiceItemsRequest request) {
        log.info("Adding items to invoice: {}", invoiceId);
        InvoiceDto invoice = invoiceService.addItemsToInvoice(invoiceId, request);
        return ResponseEntity.ok(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> removeItemFromInvoice(UUID invoiceId, UUID itemId) {
        log.info("Removing item {} from invoice: {}", itemId, invoiceId);
        InvoiceDto invoice = invoiceService.removeItemFromInvoice(invoiceId, itemId);
        return ResponseEntity.ok(invoice);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatchInvoiceResponse> createBatchInvoices(BatchInvoiceRequest request) {
        log.info("Creating batch invoices for {} patients", request.patientIds().size());
        BatchInvoiceResponse response = invoiceService.createBatchInvoices(request);
        return ResponseEntity.status(
            response.failureCount() > 0 ? HttpStatus.MULTI_STATUS : HttpStatus.CREATED
        ).body(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<Page<PaymentDto>> getInvoicePaymentHistory(UUID invoiceId, Pageable pageable) {
        log.debug("Getting payment history for invoice: {}", invoiceId);
        Page<PaymentDto> payments = invoiceService.getInvoicePaymentHistory(invoiceId, pageable);
        return ResponseEntity.ok(payments);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> cloneInvoice(UUID invoiceId, String issueDate, String dueDate) {
        log.info("Cloning invoice: {}", invoiceId);
        LocalDate issue = issueDate != null ? LocalDate.parse(issueDate) : LocalDate.now();
        LocalDate due = dueDate != null ? LocalDate.parse(dueDate) : issue.plusDays(30);
        InvoiceDto clonedInvoice = invoiceService.cloneInvoice(invoiceId, issue, due);
        return ResponseEntity.status(HttpStatus.CREATED).body(clonedInvoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> applyWriteOff(UUID invoiceId, WriteOffRequest request) {
        log.info("Applying write-off to invoice: {} amount: {}", invoiceId, request.amount());
        InvoiceDto dto = invoiceService.applyWriteOff(invoiceId, request.amount(), request.reason());
        return ResponseEntity.ok(dto);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InvoiceDto> createCreditNote(UUID invoiceId, CreditNoteRequest request) {
        log.info("Creating credit note for invoice: {} amount: {}", invoiceId, request.amount());
        InvoiceDto dto = invoiceService.createCreditNote(invoiceId, request.amount(), request.reason());
        return ResponseEntity.ok(dto);
    }
}