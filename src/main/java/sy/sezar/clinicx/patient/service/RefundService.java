package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface for managing refunds.
 */
public interface RefundService {

    /**
     * Process a new refund request.
     */
    PaymentDto processRefund(RefundRequest request);

    /**
     * Get refunds with filtering.
     */
    Page<PaymentDto> getRefunds(UUID patientId, LocalDate startDate, LocalDate endDate, 
                                String status, Pageable pageable);

    /**
     * Approve a pending refund.
     */
    PaymentDto approveRefund(UUID refundId, String approvalNotes);

    /**
     * Reject a pending refund.
     */
    PaymentDto rejectRefund(UUID refundId, String rejectionReason);

    /**
     * Cancel a refund.
     */
    void cancelRefund(UUID refundId, String cancellationReason);

    /**
     * Get detailed refund information.
     */
    RefundDetailsDto getRefundDetails(UUID refundId);

    /**
     * Process multiple refunds in batch.
     */
    BatchRefundResponse processBatchRefunds(BatchRefundRequest request);

    /**
     * Get all pending refunds awaiting approval.
     */
    Page<PaymentDto> getPendingRefunds(Pageable pageable);

    /**
     * Process an approved refund to completion.
     */
    PaymentDto processApprovedRefund(UUID refundId, ProcessRefundRequest request);

    /**
     * Get refund summary statistics.
     */
    RefundSummaryDto getRefundSummary(LocalDate startDate, LocalDate endDate, String groupBy);
}