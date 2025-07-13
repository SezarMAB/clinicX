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

    @Override
    public ResponseEntity<FinancialRecordDto> createInvoice(InvoiceCreateRequest request) {
        log.info("Creating new invoice for patient ID: {}", request.patientId());
        // Calculate total amount from items and extract description
        BigDecimal totalAmount = request.items().stream()
            .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        String description = request.notes() != null ? request.notes() : "Invoice for dental services";

        FinancialRecordDto invoice = invoiceService.createInvoice(request.patientId(), totalAmount, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @Override
    public ResponseEntity<FinancialRecordDto> addPayment(UUID invoiceId, PaymentCreateRequest request) {
        log.info("Adding payment to invoice ID: {} with amount: {}", invoiceId, request.amount());
        FinancialRecordDto payment = invoiceService.addPayment(invoiceId, request.amount(), request.paymentMethod());
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
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
        BigDecimal newBalance = invoiceService.recalculatePatientBalance(patientId);
        return ResponseEntity.ok(newBalance);
    }
}