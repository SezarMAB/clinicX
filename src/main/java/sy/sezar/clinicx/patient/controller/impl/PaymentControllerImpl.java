package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.patient.controller.api.PaymentControllerApi;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.PaymentType;
import sy.sezar.clinicx.patient.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of PaymentControllerApi for managing payments.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentControllerApi {

    private final PaymentService paymentService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<Page<PaymentDto>> getAllPayments(
            UUID patientId,
            UUID invoiceId,
            PaymentType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        log.debug("Getting all payments with filters - patientId: {}, invoiceId: {}, type: {}", 
                  patientId, invoiceId, type);
        Page<PaymentDto> payments = paymentService.getAllPayments(
            patientId, invoiceId, type, startDate, endDate, pageable);
        return ResponseEntity.ok(payments);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<PaymentDto> getPayment(UUID paymentId) {
        log.debug("Getting payment with ID: {}", paymentId);
        PaymentDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentDto> createPayment(PaymentCreateRequest request) {
        log.info("Creating new payment");
        // Extract patient and invoice IDs from request or derive from context
        // For now, we'll need to enhance the request to include these
        // This is a simplified implementation
        PaymentDto payment = paymentService.createPayment(request, null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentDto> updatePayment(UUID paymentId, PaymentUpdateRequest request) {
        log.info("Updating payment with ID: {}", paymentId);
        PaymentDto payment = paymentService.updatePayment(paymentId, request);
        return ResponseEntity.ok(payment);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> voidPayment(UUID paymentId) {
        log.info("Voiding payment with ID: {}", paymentId);
        paymentService.voidPayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentStatisticsDto> getPaymentStatistics(
            UUID patientId,
            LocalDate startDate,
            LocalDate endDate) {
        log.debug("Getting payment statistics for patient: {}", patientId);
        PaymentStatisticsDto statistics = paymentService.getPaymentStatistics(
            patientId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<BulkPaymentResponse> processBulkPayments(BulkPaymentRequest request) {
        log.info("Processing bulk payments - count: {}", request.payments().size());
        BulkPaymentResponse response = paymentService.processBulkPayments(request);
        return ResponseEntity.status(
            response.failureCount() > 0 ? HttpStatus.MULTI_STATUS : HttpStatus.CREATED
        ).body(response);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<Map<String, BigDecimal>> getPaymentMethodBreakdown(
            UUID patientId,
            LocalDate startDate,
            LocalDate endDate) {
        log.debug("Getting payment method breakdown");
        Map<String, BigDecimal> breakdown = paymentService.getPaymentMethodBreakdown(
            patientId, startDate, endDate);
        return ResponseEntity.ok(breakdown);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentDto> applyPaymentToInvoice(UUID paymentId, UUID invoiceId) {
        log.info("Applying payment {} to invoice {}", paymentId, invoiceId);
        PaymentDto payment = paymentService.applyPaymentToInvoice(paymentId, invoiceId);
        return ResponseEntity.ok(payment);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentDto> allocatePayment(UUID paymentId, java.util.List<PaymentAllocationItem> allocations) {
        log.info("Allocating payment {} to {} invoices", paymentId, allocations.size());
        PaymentDto payment = paymentService.allocatePayment(paymentId, allocations);
        return ResponseEntity.ok(payment);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<PaymentDto>> getUnallocatedPayments(UUID patientId, Pageable pageable) {
        log.debug("Getting unallocated payments for patient: {}", patientId);
        Page<PaymentDto> payments = paymentService.getUnallocatedPayments(patientId, pageable);
        return ResponseEntity.ok(payments);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReconciliationResultDto> reconcilePayments(ReconcilePaymentsRequest request) {
        log.info("Reconciling payments - count: {}", request.paymentIds().size());
        // TODO: Implement reconciliation logic in service
        ReconciliationResultDto result = new ReconciliationResultDto(
            request.paymentIds().size(),
            0,
            0,
            BigDecimal.ZERO,
            request.reconciliationDate(),
            request.referenceNumber(),
            null,
            "PENDING"
        );
        return ResponseEntity.ok(result);
    }
}