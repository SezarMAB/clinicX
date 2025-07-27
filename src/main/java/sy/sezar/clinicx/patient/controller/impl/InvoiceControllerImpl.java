package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.InvoiceControllerApi;
import sy.sezar.clinicx.patient.dto.FinancialRecordDto;
import sy.sezar.clinicx.patient.dto.InvoiceCreateRequest;
import sy.sezar.clinicx.patient.dto.PaymentCreateRequest;
import sy.sezar.clinicx.patient.service.InvoiceService;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class InvoiceControllerImpl implements InvoiceControllerApi {

    private final InvoiceService invoiceService;
    private final sy.sezar.clinicx.patient.service.AdvancePaymentService advancePaymentService;

    @Override
    public ResponseEntity<FinancialRecordDto> createInvoice(InvoiceCreateRequest request) {
        log.info("Creating new invoice for patient ID: {} with {} items", request.patientId(), request.items().size());
        log.debug("Invoice creation request validation: {}", request);
        
        try {
            // Calculate total amount from items and extract description
            BigDecimal totalAmount = request.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            String description = request.notes() != null ? request.notes() : "Invoice for dental services";
            
            log.debug("Calculated invoice total: {} for patient: {}", totalAmount, request.patientId());

            FinancialRecordDto invoice = invoiceService.createInvoice(request.patientId(), totalAmount, description);
            
            // Check if auto-apply advance payments is requested
            if (Boolean.TRUE.equals(request.autoApplyAdvancePayments()) && invoice != null) {
                try {
                    log.info("Auto-applying advance payments to invoice: {}", invoice.recordId());
                    invoice = advancePaymentService.autoApplyAdvancePaymentsToInvoice(invoice.recordId());
                    log.info("Successfully auto-applied advance payments to invoice: {}", invoice.invoiceNumber());
                } catch (Exception e) {
                    log.warn("Could not auto-apply advance payments to invoice {}: {}", invoice.invoiceNumber(), e.getMessage());
                    // Continue with the invoice even if auto-apply fails
                }
            }
            
            log.info("Successfully created invoice: {} for patient: {} - Status: 201 CREATED", 
                    invoice.invoiceNumber(), request.patientId());
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (Exception e) {
            log.error("Failed to create invoice for patient: {} - Error: {}", request.patientId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<FinancialRecordDto> addPayment(UUID invoiceId, PaymentCreateRequest request) {
        log.info("Adding payment to invoice ID: {} with amount: {} (method: {})", 
                invoiceId, request.amount(), request.paymentMethod());
        log.debug("Payment request validation: {}", request);
        
        try {
            FinancialRecordDto payment = invoiceService.addPayment(invoiceId, request.amount(), request.paymentMethod());
            log.info("Successfully added payment of {} to invoice: {} - Status: 201 CREATED", 
                    request.amount(), invoiceId);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            log.error("Failed to add payment to invoice: {} - Error: {}", invoiceId, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<FinancialRecordDto>> getPatientFinancialRecords(UUID patientId, Pageable pageable) {
        log.info("Retrieving financial records for patient ID: {} with pagination: {}", patientId, pageable);
        Page<FinancialRecordDto> records = invoiceService.getPatientFinancialRecords(patientId, pageable);
        return ResponseEntity.ok(records);
    }

    @Override
    public ResponseEntity<String> getNextInvoiceNumber() {
        log.info("Retrieving next invoice number");
        String nextNumber = invoiceService.getNextInvoiceNumber();
        return ResponseEntity.ok(nextNumber);
    }

    @Override
    public ResponseEntity<BigDecimal> recalculatePatientBalance(UUID patientId) {
        log.info("Recalculating balance for patient ID: {}", patientId);
        
        try {
            BigDecimal newBalance = invoiceService.recalculatePatientBalance(patientId);
            log.info("Successfully recalculated balance for patient: {} - New balance: {} - Status: 200 OK", 
                    patientId, newBalance);
            return ResponseEntity.ok(newBalance);
        } catch (Exception e) {
            log.error("Failed to recalculate balance for patient: {} - Error: {}", patientId, e.getMessage());
            throw e;
        }
    }
}