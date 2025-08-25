package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
    FinancialRecordDto addPayment(UUID invoiceId, BigDecimal amount, PaymentMethod paymentMethod);

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

    /**
     * Creates an invoice and optionally applies available advance payments.
     */
    FinancialRecordDto createInvoiceWithAdvancePayments(UUID patientId, BigDecimal amount, String description, boolean autoApplyCredits);
    
    /**
     * Generate invoice from treatments.
     */
    InvoiceDto generateInvoiceFromTreatments(GenerateInvoiceRequest request);
    
    /**
     * Update invoice status.
     */
    InvoiceDto updateInvoiceStatus(UUID invoiceId, InvoiceStatus status, String reason);
    
    /**
     * Cancel an invoice.
     */
    InvoiceDto cancelInvoice(UUID invoiceId, String reason);
    
    /**
     * Send invoice reminder.
     */
    void sendInvoiceReminder(UUID invoiceId, String reminderType);
    
    /**
     * Mark overdue invoices.
     */
    List<InvoiceDto> markOverdueInvoices();
    
    /**
     * Get unpaid invoices.
     */
    Page<InvoiceDto> getUnpaidInvoices(UUID patientId, boolean includePartiallyPaid, Pageable pageable);
    
    /**
     * Generate aging report.
     */
    InvoiceAgingReportDto generateAgingReport(LocalDate asOfDate, boolean includeDetails);
    
    /**
     * Apply discount to invoice.
     */
    InvoiceDto applyDiscount(UUID invoiceId, DiscountRequest request);
    
    /**
     * Add items to invoice.
     */
    InvoiceDto addItemsToInvoice(UUID invoiceId, AddInvoiceItemsRequest request);
    
    /**
     * Remove item from invoice.
     */
    InvoiceDto removeItemFromInvoice(UUID invoiceId, UUID itemId);
    
    /**
     * Create batch invoices.
     */
    BatchInvoiceResponse createBatchInvoices(BatchInvoiceRequest request);
    
    /**
     * Get invoice payment history.
     */
    Page<PaymentDto> getInvoicePaymentHistory(UUID invoiceId, Pageable pageable);
    
    /**
     * Clone an invoice.
     */
    InvoiceDto cloneInvoice(UUID invoiceId, LocalDate issueDate, LocalDate dueDate);

    /**
     * Apply write-off to invoice.
     */
    InvoiceDto applyWriteOff(UUID invoiceId, java.math.BigDecimal amount, String reason);

    /**
     * Create credit note for invoice.
     */
    InvoiceDto createCreditNote(UUID invoiceId, java.math.BigDecimal amount, String reason);
}
