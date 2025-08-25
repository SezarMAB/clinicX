package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.patient.controller.api.AdvancePaymentControllerApi;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.service.AdvancePaymentService;

import java.util.UUID;

/**
 * REST controller implementation for managing advance payments.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AdvancePaymentControllerImpl implements AdvancePaymentControllerApi {

    private final AdvancePaymentService advancePaymentService;

    @Override
    public ResponseEntity<AdvancePaymentDto> createAdvancePayment(AdvancePaymentCreateRequest request) {
        log.info("Creating advance payment for patient: {}", request.patientId());
        AdvancePaymentDto createdPayment = advancePaymentService.createAdvancePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @Override
    public ResponseEntity<FinancialRecordDto> applyAdvancePaymentToInvoice(ApplyAdvancePaymentRequest request) {
        log.info("Applying advance payment {} to invoice {}", request.advancePaymentId(), request.invoiceId());
        FinancialRecordDto result = advancePaymentService.applyAdvancePaymentToInvoice(request);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Page<AdvancePaymentDto>> getPatientAdvancePayments(UUID patientId, Pageable pageable) {
        log.info("Getting advance payments for patient: {}", patientId);
        Page<AdvancePaymentDto> advancePayments = advancePaymentService.getPatientAdvancePayments(patientId, pageable);
        return ResponseEntity.ok(advancePayments);
    }

    @Override
    public ResponseEntity<Page<AdvancePaymentDto>> getUnappliedAdvancePayments(UUID patientId, Pageable pageable) {
        log.info("Getting unapplied advance payments for patient: {}", patientId);
        Page<AdvancePaymentDto> unappliedPayments = advancePaymentService.getUnappliedAdvancePayments(patientId, pageable);
        return ResponseEntity.ok(unappliedPayments);
    }

    @Override
    public ResponseEntity<PatientCreditBalanceDto> getPatientCreditBalance(UUID patientId) {
        log.info("Getting credit balance for patient: {}", patientId);
        PatientCreditBalanceDto creditBalance = advancePaymentService.getPatientCreditBalance(patientId);
        return ResponseEntity.ok(creditBalance);
    }

    @Override
    public ResponseEntity<FinancialRecordDto> autoApplyAdvancePaymentsToInvoice(UUID invoiceId) {
        log.info("Auto-applying advance payments to invoice: {}", invoiceId);
        FinancialRecordDto result = advancePaymentService.autoApplyAdvancePaymentsToInvoice(invoiceId);
        return ResponseEntity.ok(result);
    }
}