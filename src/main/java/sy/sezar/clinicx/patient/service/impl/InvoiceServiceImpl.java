package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.FinancialRecordDto;
import sy.sezar.clinicx.patient.dto.PaymentInstallmentDto;
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
    public FinancialRecordDto addPayment(UUID invoiceId, BigDecimal amount, String paymentMethod) {
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

        // Determine payment status for logging
        String paymentStatus = "PARTIAL";
        if (newTotalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            paymentStatus = "FULL";
        }
        log.info("Payment status for invoice {}: {}", invoice.getInvoiceNumber(), paymentStatus);

        // Recalculate patient balance
        BigDecimal newBalance = recalculatePatientBalance(invoice.getPatient().getId());
        log.info("Patient balance updated to: {} after payment", newBalance);

        log.info("Successfully added payment of {} to invoice: {} (total paid: {}/{})",
                amount, invoiceId, newTotalPaid, invoice.getTotalAmount());
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

    private String determineStatus(Invoice invoice) {
        BigDecimal totalPaid = invoice.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String status;
        if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
            status = "UNPAID";
        } else if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            status = "PAID";
        } else {
            status = "PARTIALLY_PAID";
        }

        log.debug("Determined status for invoice {}: {} (paid: {}, total: {})",
                invoice.getInvoiceNumber(), status, totalPaid, invoice.getTotalAmount());

        return status;
    }

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
}
