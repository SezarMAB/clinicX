package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing payments, refunds, and credits.
 */
public interface PaymentService {

    /**
     * Get all payments with optional filtering.
     *
     * @param patientId Patient ID filter
     * @param invoiceId Invoice ID filter
     * @param type Payment type filter
     * @param startDate Start date filter
     * @param endDate End date filter
     * @param pageable Pagination information
     * @return Page of payments
     */
    Page<PaymentDto> getAllPayments(UUID patientId, UUID invoiceId, PaymentType type,
                                   LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Get payment by ID.
     *
     * @param paymentId Payment ID
     * @return Payment details
     */
    PaymentDto getPayment(UUID paymentId);

    /**
     * Create a new payment.
     *
     * @param request Payment creation request
     * @param patientId Patient ID
     * @param invoiceId Invoice ID (optional)
     * @return Created payment
     */
    PaymentDto createPayment(PaymentCreateRequest request, UUID patientId, UUID invoiceId);

    /**
     * Update an existing payment.
     *
     * @param paymentId Payment ID to update
     * @param request Update request
     * @return Updated payment
     */
    PaymentDto updatePayment(UUID paymentId, PaymentUpdateRequest request);

    /**
     * Void a payment.
     *
     * @param paymentId Payment ID to void
     */
    void voidPayment(UUID paymentId);

    /**
     * Get payment statistics for a patient.
     *
     * @param patientId Patient ID
     * @param startDate Start date for statistics
     * @param endDate End date for statistics
     * @return Payment statistics
     */
    PaymentStatisticsDto getPaymentStatistics(UUID patientId, LocalDate startDate, LocalDate endDate);

    /**
     * Process bulk payments.
     *
     * @param request Bulk payment request
     * @return Bulk payment response with results
     */
    BulkPaymentResponse processBulkPayments(BulkPaymentRequest request);

    /**
     * Get payment method breakdown for a patient.
     *
     * @param patientId Patient ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map of payment methods to amounts
     */
    Map<String, BigDecimal> getPaymentMethodBreakdown(UUID patientId, LocalDate startDate, LocalDate endDate);

    /**
     * Apply a payment to an invoice.
     *
     * @param paymentId Payment ID
     * @param invoiceId Invoice ID
     * @return Updated payment
     */
    PaymentDto applyPaymentToInvoice(UUID paymentId, UUID invoiceId);

    /**
     * Get unallocated payments (credits) for a patient.
     *
     * @param patientId Patient ID
     * @param pageable Pagination information
     * @return Page of unallocated payments
     */
    Page<PaymentDto> getUnallocatedPayments(UUID patientId, Pageable pageable);

    /**
     * Allocate a payment across multiple invoices.
     */
    PaymentDto allocatePayment(UUID paymentId, java.util.List<sy.sezar.clinicx.patient.dto.PaymentAllocationItem> allocations);
}