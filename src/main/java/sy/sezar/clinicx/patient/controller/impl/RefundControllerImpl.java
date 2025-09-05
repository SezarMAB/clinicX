package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.patient.controller.api.RefundControllerApi;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.service.RefundService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Implementation of RefundControllerApi for refund processing and management.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RefundControllerImpl implements RefundControllerApi {

    private final RefundService refundService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentDto> processRefund(RefundRequest request) {
        log.info("Processing refund for patient: {}", request.patientId());
        PaymentDto refund = refundService.processRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(refund);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<Page<PaymentDto>> getRefunds(
            UUID patientId,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            Pageable pageable) {
        log.debug("Getting refunds with filters - patientId: {}, status: {}", patientId, status);
        Page<PaymentDto> refunds = refundService.getRefunds(
            patientId, startDate, endDate, status, pageable);
        return ResponseEntity.ok(refunds);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> approveRefund(UUID refundId, String approvalNotes) {
        log.info("Approving refund: {}", refundId);
        PaymentDto refund = refundService.approveRefund(refundId, approvalNotes);
        return ResponseEntity.ok(refund);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> rejectRefund(UUID refundId, String rejectionReason) {
        log.info("Rejecting refund: {} with reason: {}", refundId, rejectionReason);
        PaymentDto refund = refundService.rejectRefund(refundId, rejectionReason);
        return ResponseEntity.ok(refund);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> cancelRefund(UUID refundId, String cancellationReason) {
        log.info("Cancelling refund: {}", refundId);
        refundService.cancelRefund(refundId, cancellationReason);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<RefundDetailsDto> getRefundDetails(UUID refundId) {
        log.debug("Getting refund details for: {}", refundId);
        RefundDetailsDto details = refundService.getRefundDetails(refundId);
        return ResponseEntity.ok(details);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatchRefundResponse> processBatchRefunds(BatchRefundRequest request) {
        log.info("Processing batch refunds - count: {}", request.refunds().size());
        BatchRefundResponse response = refundService.processBatchRefunds(request);
        return ResponseEntity.status(
            response.failureCount() > 0 ? HttpStatus.MULTI_STATUS : HttpStatus.CREATED
        ).body(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDto>> getPendingRefunds(Pageable pageable) {
        log.debug("Getting pending refunds");
        Page<PaymentDto> pendingRefunds = refundService.getPendingRefunds(pageable);
        return ResponseEntity.ok(pendingRefunds);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentDto> processApprovedRefund(UUID refundId, ProcessRefundRequest request) {
        log.info("Processing approved refund: {}", refundId);
        PaymentDto refund = refundService.processApprovedRefund(refundId, request);
        return ResponseEntity.ok(refund);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<RefundSummaryDto> getRefundSummary(
            LocalDate startDate,
            LocalDate endDate,
            String groupBy) {
        log.debug("Getting refund summary from {} to {}", startDate, endDate);
        RefundSummaryDto summary = refundService.getRefundSummary(startDate, endDate, groupBy);
        return ResponseEntity.ok(summary);
    }
}