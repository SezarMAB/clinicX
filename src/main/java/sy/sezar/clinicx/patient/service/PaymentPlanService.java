package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.PaymentPlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing payment plans and installments.
 */
public interface PaymentPlanService {

    /**
     * Create a new payment plan for an invoice.
     *
     * @param request Payment plan creation request
     * @return Created payment plan
     */
    PaymentPlanDto createPaymentPlan(PaymentPlanCreateRequest request);

    /**
     * Create a payment plan with variable installment amounts.
     * Each installment can have a different amount as long as the total matches the invoice.
     *
     * @param request Payment plan creation request with variable amounts
     * @return Created payment plan
     */
    PaymentPlanDto createPaymentPlanWithVariableAmounts(PaymentPlanCreateRequest request);

    /**
     * Create a payment plan with custom installment amounts.
     * Allows specifying exact amounts for each installment.
     *
     * @param patientId Patient ID
     * @param invoiceId Invoice ID
     * @param planName Plan name
     * @param startDate Start date
     * @param installmentAmounts List of amounts for each installment
     * @param dueDates List of due dates for each installment
     * @return Created payment plan
     */
    PaymentPlanDto createCustomPaymentPlan(UUID patientId, UUID invoiceId, String planName, 
                                         LocalDate startDate, List<BigDecimal> installmentAmounts, 
                                         List<LocalDate> dueDates);

    /**
     * Get payment plan by ID.
     *
     * @param paymentPlanId Payment plan ID
     * @return Payment plan details
     */
    PaymentPlanDto getPaymentPlan(UUID paymentPlanId);

    /**
     * Get payment plans for a patient.
     *
     * @param patientId Patient ID
     * @param pageable Pagination information
     * @return Page of payment plans
     */
    Page<PaymentPlanDto> getPatientPaymentPlans(UUID patientId, Pageable pageable);

    /**
     * Get payment plans by status.
     *
     * @param status Payment plan status
     * @param pageable Pagination information
     * @return Page of payment plans
     */
    Page<PaymentPlanDto> getPaymentPlansByStatus(PaymentPlanStatus status, Pageable pageable);

    /**
     * Update payment plan status.
     *
     * @param paymentPlanId Payment plan ID
     * @param status New status
     * @param reason Status change reason
     * @return Updated payment plan
     */
    PaymentPlanDto updatePaymentPlanStatus(UUID paymentPlanId, PaymentPlanStatus status, String reason);

    /**
     * Cancel a payment plan.
     *
     * @param paymentPlanId Payment plan ID
     * @param reason Cancellation reason
     * @return Updated payment plan
     */
    PaymentPlanDto cancelPaymentPlan(UUID paymentPlanId, String reason);

    /**
     * Get installments for a payment plan.
     *
     * @param paymentPlanId Payment plan ID
     * @return List of installments
     */
    List<PaymentPlanInstallmentDto> getPaymentPlanInstallments(UUID paymentPlanId);

    /**
     * Record payment for an installment.
     *
     * @param installmentId Installment ID
     * @param amount Payment amount
     * @param paymentDate Payment date
     * @param notes Payment notes
     * @return Updated installment
     */
    PaymentPlanInstallmentDto recordInstallmentPayment(UUID installmentId, BigDecimal amount, 
                                                      LocalDate paymentDate, String notes);

    /**
     * Get overdue installments.
     *
     * @param pageable Pagination information
     * @return Page of overdue installments
     */
    Page<PaymentPlanInstallmentDto> getOverdueInstallments(Pageable pageable);

    /**
     * Get installments due within a date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of installments
     */
    List<PaymentPlanInstallmentDto> getInstallmentsDueBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Mark overdue installments.
     *
     * @return Number of installments marked as overdue
     */
    int markOverdueInstallments();

    /**
     * Get payment plan statistics for a patient.
     *
     * @param patientId Patient ID
     * @return Payment plan statistics
     */
    PaymentPlanStatisticsDto getPaymentPlanStatistics(UUID patientId);

    /**
     * Generate payment plan report.
     *
     * @param patientId Patient ID (optional)
     * @param startDate Start date (optional)
     * @param endDate End date (optional)
     * @return Payment plan report
     */
    PaymentPlanReportDto generatePaymentPlanReport(UUID patientId, LocalDate startDate, LocalDate endDate);
}
