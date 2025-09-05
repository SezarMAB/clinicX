package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;
import sy.sezar.clinicx.patient.mapper.InvoiceMapper;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.repository.InvoiceRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.repository.PaymentRepository;
import sy.sezar.clinicx.patient.service.InvoiceService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of InvoiceService with business logic and financial calculations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceMapper invoiceMapper;
    private final EntityManager entityManager;
    private final sy.sezar.clinicx.patient.service.LedgerService ledgerService;
    private final sy.sezar.clinicx.patient.repository.VisitProcedureRepository visitProcedureRepository;

    @Override
    @Transactional
    public FinancialRecordDto createInvoice(UUID patientId, BigDecimal amount, String description) {
        log.info("Creating invoice for patient ID: {} with amount: {} (description: {})", patientId, amount, description);
        log.debug("Invoice creation parameters - Patient: {}, Amount: {}", patientId, amount);

        Patient patient = findPatientById(patientId);
        log.debug("Found patient: {} for invoice creation", patient.getFullName());

        String invoiceNumber = getNextInvoiceNumber();
        log.debug("Generated invoice number: {}", invoiceNumber);

        Invoice invoice = new Invoice();
        invoice.setPatient(patient);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setTotalAmount(amount);
        invoice.setIssueDate(LocalDate.now());
        // Note: Invoice entity doesn't have description field - using parameter for logging only

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Record ledger entry for charge
        ledgerService.record(patient.getId(), savedInvoice, null,
                sy.sezar.clinicx.patient.model.enums.LedgerEntryType.CHARGE,
                amount, description);
        log.debug("Saved invoice with ID: {} and number: {}", savedInvoice.getId(), savedInvoice.getInvoiceNumber());

        // Recalculate patient balance
        BigDecimal newBalance = recalculatePatientBalance(patientId);
        log.info("Patient balance updated to: {} after invoice creation", newBalance);

        log.info("Successfully created invoice with number: {} for patient: {} (amount: {}, description: {})",
                savedInvoice.getInvoiceNumber(), patientId, amount, description);
        return mapToFinancialRecordDto(savedInvoice);
    }

    @Override
    @Transactional
    public FinancialRecordDto addPayment(UUID invoiceId, BigDecimal amount, PaymentMethod paymentMethod) {
        log.info("Adding payment of {} to invoice ID: {} using method: {}", amount, invoiceId, paymentMethod);
        log.debug("Payment details - Invoice: {}, Amount: {}, Method: {}", invoiceId, amount, paymentMethod);

        Invoice invoice = findInvoiceById(invoiceId);
        log.debug("Found invoice {} with current total: {} for patient: {}",
                invoice.getInvoiceNumber(), invoice.getTotalAmount(), invoice.getPatient().getId());

        // Calculate current paid amount before adding new payment
        BigDecimal currentPaid = invoice.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug("Current total paid on invoice {}: {}", invoice.getInvoiceNumber(), currentPaid);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);

        invoice.getPayments().add(payment);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        BigDecimal newTotalPaid = currentPaid.add(amount);
        log.debug("New total paid on invoice {}: {}", invoice.getInvoiceNumber(), newTotalPaid);

        // Update invoice materialized paid/due
        invoice.setAmountPaid(newTotalPaid);
        BigDecimal subTotal = invoice.getSubTotal() != null ? invoice.getSubTotal() : invoice.getTotalAmount();
        BigDecimal due = subTotal
                .subtract(invoice.getDiscountAmount())
                .add(invoice.getTaxAmount())
                .add(invoice.getAdjustmentAmount())
                .subtract(invoice.getWriteOffAmount())
                .subtract(invoice.getAmountPaid());
        invoice.setAmountDue(due.max(BigDecimal.ZERO));
        invoiceRepository.save(invoice);

        // Recalculate patient balance
        BigDecimal newBalance = recalculatePatientBalance(invoice.getPatient().getId());

        // Ledger entry for payment receipt
        ledgerService.record(invoice.getPatient().getId(), invoice, payment,
                sy.sezar.clinicx.patient.model.enums.LedgerEntryType.PAYMENT_RECEIPT,
                amount, "Payment received (" + paymentMethod + ")");
        log.info("Patient balance updated to: {} after payment", newBalance);

        log.info("Successfully added payment of {} to invoice: {} (paid: {}, due: {})",
                amount, invoiceId, newTotalPaid, invoice.getAmountDue());
        return mapToFinancialRecordDto(savedInvoice);
    }

    @Override
    public Page<FinancialRecordDto> getPatientFinancialRecords(UUID patientId, Pageable pageable) {
        log.info("Getting financial records for patient ID: {} with pagination: {}", patientId, pageable);

        Page<Invoice> invoices = invoiceRepository.findByPatientId(patientId, pageable);
        log.info("Found {} financial records (page {} of {}) for patient: {}",
                invoices.getNumberOfElements(), invoices.getNumber() + 1, invoices.getTotalPages(), patientId);

        return invoices.map(this::mapToFinancialRecordDto);
    }

    @Override
    public Page<PaymentInstallmentDto> getInvoicePayments(UUID invoiceId, Pageable pageable) {
        log.debug("Getting payment installments for invoice: {}", invoiceId);

        Page<Payment> payments = paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId, pageable);
        return payments.map(this::mapToPaymentInstallmentDto);
    }

    @Override
    @Transactional
    public BigDecimal recalculatePatientBalance(UUID patientId) {
        log.debug("Recalculating balance for patient ID: {}", patientId);

        Patient patient = findPatientById(patientId);

        // Calculate total invoiced amount
        BigDecimal totalInvoiced = invoiceRepository.findByPatientId(patientId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total payments
        BigDecimal totalPaid = invoiceRepository.findByPatientId(patientId, Pageable.unpaged())
                .getContent()
                .stream()
                .flatMap(invoice -> invoice.getPayments().stream())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalInvoiced.subtract(totalPaid);
        patient.setBalance(balance);
        patientRepository.save(patient);

        log.debug("Updated patient {} balance to: {}", patientId, balance);
        return balance;
    }

    @Override
    @Transactional
    public String getNextInvoiceNumber() {
        log.debug("Generating next invoice number from sequence");

        Query query = entityManager.createNativeQuery("SELECT nextval('invoice_number_seq')");
        Long nextValue = ((Number) query.getSingleResult()).longValue();
        String invoiceNumber = String.format("INV-%06d", nextValue);

        log.debug("Generated invoice number: {} (sequence value: {})", invoiceNumber, nextValue);
        return invoiceNumber;
    }

    private Patient findPatientById(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {} during invoice operation", patientId);
                    return new NotFoundException("Patient not found with ID: " + patientId);
                });
    }

    private Invoice findInvoiceById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.error("Invoice not found with ID: {} during financial operation", invoiceId);
                    return new NotFoundException("Invoice not found with ID: " + invoiceId);
                });
    }

    private FinancialRecordDto mapToFinancialRecordDto(Invoice invoice) {
        // IMPLEMENTED: Use InvoiceMapper when available
        return invoiceMapper.toFinancialRecordDto(invoice);
    }

    private PaymentInstallmentDto mapToPaymentInstallmentDto(Payment payment) {
        // IMPLEMENTED: Use InvoiceMapper for payment mapping
        return invoiceMapper.toPaymentInstallmentDto(payment);
    }

    // Removed unused determineStatus helper

    @Override
    @Transactional
    public FinancialRecordDto createInvoiceWithAdvancePayments(UUID patientId, BigDecimal amount, String description, boolean autoApplyCredits) {
        log.info("Creating invoice with advance payment option for patient ID: {} with amount: {} (auto-apply: {})", patientId, amount, autoApplyCredits);
        
        // Create the invoice first
        FinancialRecordDto invoice = createInvoice(patientId, amount, description);
        
        // If auto-apply is enabled and invoice was created successfully
        if (autoApplyCredits && invoice != null) {
            try {
                // We need to inject AdvancePaymentService to avoid circular dependency
                // For now, we'll return the invoice and let the controller handle auto-apply
                log.info("Invoice created successfully. Auto-apply should be handled by the controller to avoid circular dependencies.");
            } catch (Exception e) {
                log.warn("Could not auto-apply advance payments: {}", e.getMessage());
                // Return the invoice even if auto-apply fails
            }
        }
        
        return invoice;
    }

    @Override
    @Transactional
    public InvoiceDto generateInvoiceFromTreatments(GenerateInvoiceRequest request) {
        log.info("Generating invoice from procedures for patient: {}", request.patientId());

        Patient patient = findPatientById(request.patientId());
        String invoiceNumber = getNextInvoiceNumber();

        Invoice invoice = new Invoice();
        invoice.setPatient(patient);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setIssueDate(request.issueDate() != null ? request.issueDate() : LocalDate.now());
        invoice.setDueDate(request.dueDate() != null ? request.dueDate() : invoice.getIssueDate().plusDays(30));
        invoice.setStatus(InvoiceStatus.UNPAID);

        // Build items from provided procedure IDs
        BigDecimal subTotal = BigDecimal.ZERO;
        for (UUID procedureId : request.procedureIds()) {
            var procedure = visitProcedureRepository.findById(procedureId)
                .orElseThrow(() -> new NotFoundException("Procedure not found: " + procedureId));

            // Ensure not already billed (unique index will also enforce)
            boolean alreadyBilled = invoiceRepository
                .findById(invoice.getId())
                .isPresent(); // placeholder, rely on DB unique index after persist

            var item = new sy.sezar.clinicx.patient.model.InvoiceItem();
            item.setInvoice(invoice);
            item.setProcedure(procedure);
            item.setItemType(sy.sezar.clinicx.patient.model.enums.InvoiceItemType.PROCEDURE);
            item.setDescription(procedure.getName());
            var amount = procedure.getTotalFee();
            item.setAmount(amount);

            invoice.getItems().add(item);
            subTotal = subTotal.add(amount);
        }

        invoice.setSubTotal(subTotal);
        invoice.setTotalAmount(subTotal);
        // initialize materialized totals
        invoice.setAmountPaid(invoice.getAmountPaid() != null ? invoice.getAmountPaid() : BigDecimal.ZERO);
        BigDecimal due = subTotal
            .subtract(invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO)
            .add(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO)
            .add(invoice.getAdjustmentAmount() != null ? invoice.getAdjustmentAmount() : BigDecimal.ZERO)
            .subtract(invoice.getWriteOffAmount() != null ? invoice.getWriteOffAmount() : BigDecimal.ZERO)
            .subtract(invoice.getAmountPaid());
        invoice.setAmountDue(due.max(BigDecimal.ZERO));

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapToInvoiceDto(savedInvoice);
    }
    
    @Override
    @Transactional
    public InvoiceDto updateInvoiceStatus(UUID invoiceId, InvoiceStatus status, String reason) {
        log.info("Updating invoice {} status to: {} (reason: {})", invoiceId, status, reason);
        
        Invoice invoice = findInvoiceById(invoiceId);
        invoice.setStatus(status);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapToInvoiceDto(savedInvoice);
    }
    
    @Override
    @Transactional
    public InvoiceDto cancelInvoice(UUID invoiceId, String reason) {
        log.info("Cancelling invoice: {} (reason: {})", invoiceId, reason);
        
        return updateInvoiceStatus(invoiceId, InvoiceStatus.CANCELLED, reason);
    }
    
    @Override
    public void sendInvoiceReminder(UUID invoiceId, String reminderType) {
        log.info("Sending {} reminder for invoice: {}", reminderType, invoiceId);
        
        Invoice invoice = findInvoiceById(invoiceId);
        // TODO: Implement email/SMS notification service
        log.info("Reminder would be sent to patient: {} for invoice: {}", 
                invoice.getPatient().getFullName(), invoice.getInvoiceNumber());
    }
    
    @Override
    @Transactional
    public List<InvoiceDto> markOverdueInvoices() {
        log.info("Marking overdue invoices");
        
        // TODO: Implement when repository method is available
        List<InvoiceDto> result = new ArrayList<>();
        log.info("Marked {} invoices as overdue", result.size());
        return result;
    }
    
    @Override
    public Page<InvoiceDto> getUnpaidInvoices(UUID patientId, boolean includePartiallyPaid, Pageable pageable) {
        log.debug("Getting unpaid invoices for patient: {}", patientId);
        
        List<InvoiceStatus> statuses = new ArrayList<>();
        statuses.add(InvoiceStatus.UNPAID);
        if (includePartiallyPaid) {
            statuses.add(InvoiceStatus.PARTIALLY_PAID);
        }
        
        Page<Invoice> invoices = invoiceRepository.findByPatientIdAndStatusIn(patientId, statuses, pageable);
        return invoices.map(this::mapToInvoiceDto);
    }
    
    @Override
    public InvoiceAgingReportDto generateAgingReport(LocalDate asOfDate, boolean includeDetails) {
        log.info("Generating aging report as of: {}", asOfDate);
        
        // TODO: Implement when InvoiceAgingReportDto is properly defined
        return null;
    }
    
    @Override
    @Transactional
    public InvoiceDto applyDiscount(UUID invoiceId, DiscountRequest request) {
        log.info("Applying discount to invoice: {}", invoiceId);
        
        Invoice invoice = findInvoiceById(invoiceId);
        // TODO: Apply discount when DiscountRequest DTO is properly defined
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapToInvoiceDto(savedInvoice);
    }
    
    @Override
    @Transactional
    public InvoiceDto addItemsToInvoice(UUID invoiceId, AddInvoiceItemsRequest request) {
        log.info("Adding items to invoice: {}", invoiceId);

        Invoice invoice = findInvoiceById(invoiceId);

        BigDecimal addTotal = BigDecimal.ZERO;
        for (var it : request.items()) {
            var item = new sy.sezar.clinicx.patient.model.InvoiceItem();
            item.setInvoice(invoice);

            if (it.procedureId() != null) {
                var procedure = visitProcedureRepository.findById(it.procedureId())
                    .orElseThrow(() -> new NotFoundException("Procedure not found: " + it.procedureId()));
                item.setProcedure(procedure);
                item.setItemType(sy.sezar.clinicx.patient.model.enums.InvoiceItemType.PROCEDURE);
                item.setDescription(it.description() != null ? it.description() : procedure.getName());
                BigDecimal amount = it.amount();
                if (amount == null) {
                    // If quantity/unitPrice provided, compute; else use procedure total
                    if (it.quantity() != null && it.unitPrice() != null) {
                        amount = it.unitPrice().multiply(BigDecimal.valueOf(it.quantity()));
                    } else {
                        amount = procedure.getTotalFee();
                    }
                }
                item.setAmount(amount);
                addTotal = addTotal.add(amount);
            } else {
                // Ad-hoc item (OTHER/ADJUSTMENT/DISCOUNT)
                item.setItemType(sy.sezar.clinicx.patient.model.enums.InvoiceItemType.OTHER);
                item.setDescription(it.description());
                item.setAmount(it.amount());
                addTotal = addTotal.add(it.amount());
            }

            invoice.getItems().add(item);
        }

        // Update totals
        BigDecimal subTotal = (invoice.getSubTotal() != null ? invoice.getSubTotal() : invoice.getTotalAmount()).add(addTotal);
        invoice.setSubTotal(subTotal);
        invoice.setTotalAmount(subTotal);
        BigDecimal due = subTotal
            .subtract(invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO)
            .add(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO)
            .add(invoice.getAdjustmentAmount() != null ? invoice.getAdjustmentAmount() : BigDecimal.ZERO)
            .subtract(invoice.getWriteOffAmount() != null ? invoice.getWriteOffAmount() : BigDecimal.ZERO)
            .subtract(invoice.getAmountPaid() != null ? invoice.getAmountPaid() : BigDecimal.ZERO);
        invoice.setAmountDue(due.max(BigDecimal.ZERO));

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapToInvoiceDto(savedInvoice);
    }
    
    @Override
    @Transactional
    public InvoiceDto removeItemFromInvoice(UUID invoiceId, UUID itemId) {
        log.info("Removing item {} from invoice: {}", itemId, invoiceId);
        
        Invoice invoice = findInvoiceById(invoiceId);
        // TODO: Implement when InvoiceItem relationship is properly set up
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapToInvoiceDto(savedInvoice);
    }
    
    @Override
    @Transactional
    public BatchInvoiceResponse createBatchInvoices(BatchInvoiceRequest request) {
        log.info("Creating batch invoices");
        
        // TODO: Implement when BatchInvoiceRequest DTO is properly defined
        return null;
    }
    
    @Override
    public Page<PaymentDto> getInvoicePaymentHistory(UUID invoiceId, Pageable pageable) {
        log.debug("Getting payment history for invoice: {}", invoiceId);
        
        Page<Payment> payments = paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId, pageable);
        return payments.map(this::mapToPaymentDto);
    }
    
    @Override
    @Transactional
    public InvoiceDto cloneInvoice(UUID invoiceId, LocalDate issueDate, LocalDate dueDate) {
        log.info("Cloning invoice: {} with new dates", invoiceId);
        
        Invoice originalInvoice = findInvoiceById(invoiceId);
        String invoiceNumber = getNextInvoiceNumber();
        
        Invoice clonedInvoice = new Invoice();
        clonedInvoice.setPatient(originalInvoice.getPatient());
        clonedInvoice.setInvoiceNumber(invoiceNumber);
        clonedInvoice.setTotalAmount(originalInvoice.getTotalAmount());
        clonedInvoice.setIssueDate(issueDate != null ? issueDate : LocalDate.now());
        clonedInvoice.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusDays(30));
        clonedInvoice.setStatus(InvoiceStatus.UNPAID);
        
        Invoice savedInvoice = invoiceRepository.save(clonedInvoice);
        return mapToInvoiceDto(savedInvoice);
    }

    @Override
    @Transactional
    public InvoiceDto applyWriteOff(UUID invoiceId, BigDecimal amount, String reason) {
        Invoice invoice = findInvoiceById(invoiceId);
        invoice.setWriteOffAmount(invoice.getWriteOffAmount().add(amount));
        // Recompute amount due
        BigDecimal subTotal = invoice.getSubTotal() != null ? invoice.getSubTotal() : invoice.getTotalAmount();
        BigDecimal due = subTotal
                .subtract(invoice.getDiscountAmount())
                .add(invoice.getTaxAmount())
                .add(invoice.getAdjustmentAmount())
                .subtract(invoice.getWriteOffAmount())
                .subtract(invoice.getAmountPaid());
        invoice.setAmountDue(due.max(BigDecimal.ZERO));

        Invoice saved = invoiceRepository.save(invoice);
        // Ledger entry
        ledgerService.record(invoice.getPatient().getId(), saved, null,
                sy.sezar.clinicx.patient.model.enums.LedgerEntryType.WRITE_OFF, amount, reason);
        return mapToInvoiceDto(saved);
    }

    @Override
    @Transactional
    public InvoiceDto createCreditNote(UUID invoiceId, BigDecimal amount, String reason) {
        Invoice invoice = findInvoiceById(invoiceId);
        // Treat as discount increment
        invoice.setDiscountAmount(invoice.getDiscountAmount().add(amount));
        BigDecimal subTotal = invoice.getSubTotal() != null ? invoice.getSubTotal() : invoice.getTotalAmount();
        BigDecimal due = subTotal
                .subtract(invoice.getDiscountAmount())
                .add(invoice.getTaxAmount())
                .add(invoice.getAdjustmentAmount())
                .subtract(invoice.getWriteOffAmount())
                .subtract(invoice.getAmountPaid());
        invoice.setAmountDue(due.max(BigDecimal.ZERO));

        Invoice saved = invoiceRepository.save(invoice);
        // Ledger entry as negative charge (discount)
        ledgerService.record(invoice.getPatient().getId(), saved, null,
                sy.sezar.clinicx.patient.model.enums.LedgerEntryType.DISCOUNT, amount.negate(), reason);
        return mapToInvoiceDto(saved);
    }
    
    private InvoiceDto mapToInvoiceDto(Invoice invoice) {
        // Create a simplified InvoiceDto - actual mapping would be more complex
        // This is a placeholder implementation
        return null; // TODO: Implement proper mapping when DTO structure is finalized
    }
    
    private PaymentDto mapToPaymentDto(Payment payment) {
        // Create a simplified PaymentDto - actual mapping would be more complex
        // This is a placeholder implementation
        return null; // TODO: Implement proper mapping when DTO structure is finalized
    }
    
    // Removed unused calculatePaidAmount helper
    
    // Removed unused calculateOutstandingAmount helper
}
