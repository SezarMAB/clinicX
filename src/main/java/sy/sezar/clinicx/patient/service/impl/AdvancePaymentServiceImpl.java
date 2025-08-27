package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.mapper.InvoiceMapper;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import sy.sezar.clinicx.patient.model.enums.PaymentType;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;
import sy.sezar.clinicx.patient.repository.InvoiceRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.repository.PaymentRepository;
import sy.sezar.clinicx.patient.service.AdvancePaymentService;
import sy.sezar.clinicx.patient.service.InvoiceService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing advance payments.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvancePaymentServiceImpl implements AdvancePaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    @Transactional
    public AdvancePaymentDto createAdvancePayment(AdvancePaymentCreateRequest request) {
        log.debug("Creating advance payment for patient: {}", request.patientId());

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found with id: " + request.patientId()));

        // TODO: Get current staff from security context when authentication is implemented
        // For now, createdBy will be handled by the BaseEntity auditing
        
        Payment advancePayment = new Payment();
        advancePayment.setPatient(patient);
        advancePayment.setAmount(request.amount());
        advancePayment.setPaymentDate(request.paymentDate());
        advancePayment.setPaymentMethod(request.paymentMethod());
        advancePayment.setType(PaymentType.CREDIT);
        advancePayment.setDescription(request.description() != null ? request.description() : "Advance payment");
        advancePayment.setReferenceNumber(request.referenceNumber());
        // createdBy will be set by security context when implemented

        Payment savedPayment = paymentRepository.save(advancePayment);

        // Recalculate patient balance
        invoiceService.recalculatePatientBalance(request.patientId());

        log.info("Created advance payment {} for patient {}", savedPayment.getId(), patient.getId());

        return mapToAdvancePaymentDto(savedPayment);
    }

    @Override
    @Transactional
    public FinancialRecordDto applyAdvancePaymentToInvoice(ApplyAdvancePaymentRequest request) {
        log.debug("Applying advance payment {} to invoice {}", request.advancePaymentId(), request.invoiceId());

        Payment advancePayment = paymentRepository.findById(request.advancePaymentId())
                .orElseThrow(() -> new NotFoundException("Advance payment not found with id: " + request.advancePaymentId()));

        if (advancePayment.getType() != PaymentType.CREDIT) {
            throw new BusinessRuleException("Payment is not an advance payment");
        }

        if (advancePayment.getInvoice() != null) {
            throw new BusinessRuleException("Advance payment has already been applied to invoice: " + advancePayment.getInvoice().getInvoiceNumber());
        }

        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new NotFoundException("Invoice not found with id: " + request.invoiceId()));

        if (!invoice.getPatient().getId().equals(advancePayment.getPatient().getId())) {
            throw new BusinessRuleException("Advance payment and invoice belong to different patients");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot apply payment to " + invoice.getStatus().toString().toLowerCase() + " invoice");
        }

        // Validate amount to apply
        BigDecimal remainingInvoiceAmount = calculateRemainingInvoiceAmount(invoice);
        BigDecimal amountToApply = request.amountToApply();

        if (amountToApply.compareTo(advancePayment.getAmount()) > 0) {
            throw new BusinessRuleException("Amount to apply exceeds advance payment amount");
        }

        if (amountToApply.compareTo(remainingInvoiceAmount) > 0) {
            throw new BusinessRuleException("Amount to apply exceeds remaining invoice balance");
        }

        // Apply the advance payment to the invoice
        advancePayment.setInvoice(invoice);
        paymentRepository.save(advancePayment);

        // If partial application is needed, create a new credit for the remaining amount
        if (amountToApply.compareTo(advancePayment.getAmount()) < 0) {
            BigDecimal remainingCredit = advancePayment.getAmount().subtract(amountToApply);

            Payment remainingCreditPayment = new Payment();
            remainingCreditPayment.setPatient(advancePayment.getPatient());
            remainingCreditPayment.setAmount(remainingCredit);
            remainingCreditPayment.setPaymentDate(advancePayment.getPaymentDate());
            remainingCreditPayment.setPaymentMethod(advancePayment.getPaymentMethod());
            remainingCreditPayment.setType(PaymentType.CREDIT);
            remainingCreditPayment.setDescription("Remaining credit from partial application");
            remainingCreditPayment.setCreatedBy(advancePayment.getCreatedBy());

            paymentRepository.save(remainingCreditPayment);

            // Update the original advance payment amount
            advancePayment.setAmount(amountToApply);
            paymentRepository.save(advancePayment);
        }

        // Update invoice status if fully paid
        BigDecimal newRemainingAmount = remainingInvoiceAmount.subtract(amountToApply);
        if (newRemainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        // Recalculate patient balance
        invoiceService.recalculatePatientBalance(invoice.getPatient().getId());

        log.info("Applied advance payment {} to invoice {}", advancePayment.getId(), invoice.getInvoiceNumber());

        return invoiceMapper.toFinancialRecordDto(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdvancePaymentDto> getPatientAdvancePayments(UUID patientId, Pageable pageable) {
        log.debug("Getting advance payments for patient: {}", patientId);

        return paymentRepository.findByPatientIdAndTypeOrderByPaymentDateDesc(patientId, PaymentType.CREDIT, pageable)
                .map(this::mapToAdvancePaymentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdvancePaymentDto> getUnappliedAdvancePayments(UUID patientId, Pageable pageable) {
        log.debug("Getting unapplied advance payments for patient: {}", patientId);

        List<Payment> unappliedPayments = paymentRepository.findUnappliedAdvancePayments(patientId, PaymentType.CREDIT);

        // Manual pagination since we're using a custom query
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), unappliedPayments.size());

        Page<Payment> page = new org.springframework.data.domain.PageImpl<>(
                unappliedPayments.subList(start, end),
                pageable,
                unappliedPayments.size()
        );

        return page.map(this::mapToAdvancePaymentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientCreditBalanceDto getPatientCreditBalance(UUID patientId) {
        log.debug("Getting credit balance for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with id: " + patientId));

        BigDecimal totalCredits = paymentRepository.calculateTotalCredit(patientId);
        BigDecimal availableCredits = paymentRepository.calculateAvailableCredit(patientId);
        BigDecimal appliedCredits = totalCredits.subtract(availableCredits);

        List<Payment> allCredits = paymentRepository.findUnappliedAdvancePayments(patientId, PaymentType.CREDIT);
        int totalAdvancePayments = (int) paymentRepository.findByPatientIdAndTypeOrderByPaymentDateDesc(patientId, PaymentType.CREDIT, Pageable.unpaged()).getTotalElements();
        int unappliedAdvancePayments = allCredits.size();

        return new PatientCreditBalanceDto(
                patient.getId(),
                patient.getFullName(),
                totalCredits,
                appliedCredits,
                availableCredits,
                totalAdvancePayments,
                unappliedAdvancePayments
        );
    }

    @Override
    @Transactional
    public FinancialRecordDto autoApplyAdvancePaymentsToInvoice(UUID invoiceId) {
        log.debug("Auto-applying advance payments to invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found with id: " + invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot apply payments to " + invoice.getStatus().toString().toLowerCase() + " invoice");
        }

        BigDecimal remainingAmount = calculateRemainingInvoiceAmount(invoice);
        List<Payment> availableCredits = paymentRepository.findUnappliedAdvancePayments(invoice.getPatient().getId(), PaymentType.CREDIT);

        BigDecimal totalApplied = BigDecimal.ZERO;

        for (Payment credit : availableCredits) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal amountToApply = credit.getAmount().min(remainingAmount);

            ApplyAdvancePaymentRequest applyRequest = new ApplyAdvancePaymentRequest(
                    credit.getId(),
                    invoiceId,
                    amountToApply
            );

            applyAdvancePaymentToInvoice(applyRequest);

            totalApplied = totalApplied.add(amountToApply);
            remainingAmount = remainingAmount.subtract(amountToApply);
        }

        log.info("Auto-applied {} in advance payments to invoice {}", totalApplied, invoice.getInvoiceNumber());

        return invoiceMapper.toFinancialRecordDto(invoiceRepository.findById(invoiceId).orElseThrow());
    }

    private BigDecimal calculateRemainingInvoiceAmount(Invoice invoice) {
        BigDecimal totalPaid = invoice.getPayments().stream()
                .filter(p -> p.getType() != PaymentType.REFUND)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return invoice.getTotalAmount().subtract(totalPaid);
    }

    private AdvancePaymentDto mapToAdvancePaymentDto(Payment payment) {
        BigDecimal remainingCredit = payment.getInvoice() == null ? payment.getAmount() : BigDecimal.ZERO;

        return new AdvancePaymentDto(
                payment.getId(),
                payment.getPatient().getId(),
                payment.getPatient().getFullName(),
                payment.getAmount(),
                remainingCredit,
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getType(),
                payment.getDescription(),
                payment.getReferenceNumber(),
                payment.getInvoice() != null,
                payment.getCreatedBy() != null ? payment.getCreatedBy().getFullName() : "System",
                payment.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).format(DATE_TIME_FORMATTER)
        );
    }
}
