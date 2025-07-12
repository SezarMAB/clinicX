package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.FinancialRecordDto;
import sy.sezar.clinicx.patient.dto.PaymentInstallmentDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service interface for managing invoices, payments and financial operations.
 */
public interface InvoiceService {

    /**
     * Creates a new invoice for a patient with sequential invoice number.
     */
    FinancialRecordDto createInvoice(UUID patientId, BigDecimal amount, String description);

    /**
     * Adds a payment to an existing invoice and recalculates patient balance.
     */
    FinancialRecordDto addPayment(UUID invoiceId, BigDecimal amount, String paymentMethod);

    /**
     * Gets all financial records (invoices and payments) for a patient.
     */
    Page<FinancialRecordDto> getPatientFinancialRecords(UUID patientId, Pageable pageable);

    /**
     * Gets payment installments for a specific invoice.
     */
    Page<PaymentInstallmentDto> getInvoicePayments(UUID invoiceId, Pageable pageable);

    /**
     * Recalculates and updates patient balance based on all invoices and payments.
     */
    BigDecimal recalculatePatientBalance(UUID patientId);

    /**
     * Gets the next sequential invoice number.
     */
    String getNextInvoiceNumber();
}
