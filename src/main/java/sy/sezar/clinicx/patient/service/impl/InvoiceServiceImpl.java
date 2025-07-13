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
    private final EntityManager entityManager;

    @Override
    @Transactional
    public FinancialRecordDto createInvoice(UUID patientId, BigDecimal amount, String description) {
        log.info("Creating invoice for patient ID: {} with amount: {}", patientId, amount);

        Patient patient = findPatientById(patientId);

        Invoice invoice = new Invoice();
        invoice.setPatient(patient);
        invoice.setInvoiceNumber(getNextInvoiceNumber());
        invoice.setTotalAmount(amount);
        invoice.setIssueDate(LocalDate.now());
        // Note: Invoice entity doesn't have description field - using parameter for logging only

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Recalculate patient balance
        recalculatePatientBalance(patientId);

        log.info("Created invoice with number: {} for patient: {} (description: {})",
                savedInvoice.getInvoiceNumber(), patientId, description);
        return mapToFinancialRecordDto(savedInvoice);
    }

    @Override
    @Transactional
    public FinancialRecordDto addPayment(UUID invoiceId, BigDecimal amount, String paymentMethod) {
        log.info("Adding payment of {} to invoice ID: {}", amount, invoiceId);

        Invoice invoice = findInvoiceById(invoiceId);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);

        invoice.getPayments().add(payment);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Recalculate patient balance
        recalculatePatientBalance(invoice.getPatient().getId());

        log.info("Added payment to invoice: {}", invoiceId);
        return mapToFinancialRecordDto(savedInvoice);
    }

    @Override
    public Page<FinancialRecordDto> getPatientFinancialRecords(UUID patientId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByPatientId(patientId, pageable);
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
        Query query = entityManager.createNativeQuery("SELECT nextval('invoice_number_seq')");
        Long nextValue = ((Number) query.getSingleResult()).longValue();
        return String.format("INV-%06d", nextValue);
    }

    private Patient findPatientById(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));
    }

    private Invoice findInvoiceById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found with ID: " + invoiceId));
    }

    private FinancialRecordDto mapToFinancialRecordDto(Invoice invoice) {
        // TODO: Use InvoiceMapper when available
        return new FinancialRecordDto(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                null // installments will be mapped when PaymentMapper is available
        );
    }

    private PaymentInstallmentDto mapToPaymentInstallmentDto(Payment payment) {
        return new PaymentInstallmentDto(
                payment.getPaymentMethod(), // description
                payment.getPaymentDate(),
                payment.getAmount()
        );
    }

    private String determineStatus(Invoice invoice) {
        BigDecimal totalPaid = invoice.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
            return "UNPAID";
        } else if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            return "PAID";
        } else {
            return "PARTIALLY_PAID";
        }
    }
}
