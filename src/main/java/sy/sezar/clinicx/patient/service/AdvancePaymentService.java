package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.*;

import java.util.UUID;

/**
 * Service interface for managing advance payments (credits) and their application to invoices.
 */
public interface AdvancePaymentService {

    /**
     * Creates a new advance payment (credit) for a patient.
     *
     * @param request The advance payment creation request.
     * @return The created advance payment DTO.
     */
    AdvancePaymentDto createAdvancePayment(AdvancePaymentCreateRequest request);

    /**
     * Applies an advance payment to an invoice.
     *
     * @param request The request containing advance payment ID, invoice ID, and amount to apply.
     * @return The financial record DTO showing the updated invoice status.
     */
    FinancialRecordDto applyAdvancePaymentToInvoice(ApplyAdvancePaymentRequest request);

    /**
     * Gets all advance payments for a patient.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A page of advance payment DTOs.
     */
    Page<AdvancePaymentDto> getPatientAdvancePayments(UUID patientId, Pageable pageable);

    /**
     * Gets only unapplied advance payments for a patient.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A page of unapplied advance payment DTOs.
     */
    Page<AdvancePaymentDto> getUnappliedAdvancePayments(UUID patientId, Pageable pageable);

    /**
     * Gets the credit balance summary for a patient.
     *
     * @param patientId The UUID of the patient.
     * @return The patient's credit balance summary.
     */
    PatientCreditBalanceDto getPatientCreditBalance(UUID patientId);

    /**
     * Automatically applies available advance payments to an unpaid invoice.
     *
     * @param invoiceId The UUID of the invoice.
     * @return The financial record DTO showing the updated invoice status.
     */
    FinancialRecordDto autoApplyAdvancePaymentsToInvoice(UUID invoiceId);
}