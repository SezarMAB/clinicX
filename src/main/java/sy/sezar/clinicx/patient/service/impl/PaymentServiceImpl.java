package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.ResourceNotFoundException;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.mapper.PaymentMapper;
import sy.sezar.clinicx.patient.service.PaymentService;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import sy.sezar.clinicx.patient.model.enums.PaymentType;
import sy.sezar.clinicx.patient.repository.InvoiceRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.repository.PaymentRepository;
import sy.sezar.clinicx.patient.repository.PaymentAllocationRepository;
import sy.sezar.clinicx.patient.model.PaymentAllocation;
import sy.sezar.clinicx.patient.dto.PaymentAllocationItem;
import sy.sezar.clinicx.patient.service.LedgerService;
import sy.sezar.clinicx.patient.model.enums.LedgerEntryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing payments, refunds, and credits.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;
    // private final StaffService staffService; // reserved for future use (current staff attribution)
    private final PaymentAllocationRepository paymentAllocationRepository;
    private final LedgerService ledgerService;

    /**
     * Get all payments with optional filtering.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPayments(UUID patientId, UUID invoiceId, PaymentType type,
                                          LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Getting payments with filters - patientId: {}, invoiceId: {}, type: {}", 
                  patientId, invoiceId, type);
        
        // Build dynamic query based on filters
        Page<Payment> payments;
        
        if (patientId != null && startDate != null && endDate != null) {
            payments = paymentRepository.findByPatientIdAndPaymentDateBetweenOrderByPaymentDateDesc(
                patientId, startDate, endDate, pageable);
        } else if (patientId != null && type != null) {
            payments = paymentRepository.findByPatientIdAndTypeOrderByPaymentDateDesc(
                patientId, type, pageable);
        } else if (patientId != null) {
            payments = paymentRepository.findByPatientIdOrderByPaymentDateDesc(patientId, pageable);
        } else if (invoiceId != null) {
            payments = paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId, pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }
        
        return payments.map(paymentMapper::toDto);
    }

    /**
     * Get payment by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPayment(UUID paymentId) {
        log.debug("Getting payment with ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return paymentMapper.toDto(payment);
    }

    /**
     * Create a new payment.
     */
    @Override
    public PaymentDto createPayment(PaymentCreateRequest request, UUID patientId, UUID invoiceId) {
        log.info("Creating payment for patient: {} invoice: {} amount: {}", 
                 patientId, invoiceId, request.amount());
        
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
        
        Payment payment = new Payment();
        payment.setPatient(patient);
        payment.setAmount(request.amount());
        payment.setPaymentDate(request.paymentDate());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setType(PaymentType.PAYMENT);
        payment.setDescription(request.notes());
        payment.setReferenceNumber(request.referenceNumber());
        
        // Link to invoice if provided
        if (invoiceId != null) {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));
            
            validateInvoiceForPayment(invoice);
            payment.setInvoice(invoice);
            updateInvoiceStatus(invoice);
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        
        // Record ledger entry for receipt
        ledgerService.record(patient.getId(), invoiceId != null ? invoiceRepository.findById(invoiceId).orElse(null) : null,
                savedPayment, LedgerEntryType.PAYMENT_RECEIPT, request.amount(), request.notes());
        
        return paymentMapper.toDto(savedPayment);
    }

    /**
     * Update an existing payment.
     */
    @Override
    public PaymentDto updatePayment(UUID paymentId, PaymentUpdateRequest request) {
        log.info("Updating payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        
        // Validate payment can be updated
        validatePaymentForUpdate(payment);
        
        // Update fields if provided
        if (request.amount() != null) {
            payment.setAmount(request.amount());
        }
        if (request.paymentDate() != null) {
            payment.setPaymentDate(request.paymentDate());
        }
        if (request.paymentMethod() != null) {
            payment.setPaymentMethod(request.paymentMethod());
        }
        if (request.description() != null) {
            payment.setDescription(request.description());
        }
        if (request.referenceNumber() != null) {
            payment.setReferenceNumber(request.referenceNumber());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Update invoice status if linked
        if (payment.getInvoice() != null) {
            updateInvoiceStatus(payment.getInvoice());
        }
        
        log.info("Payment updated successfully");
        return paymentMapper.toDto(updatedPayment);
    }

    /**
     * Void a payment.
     */
    @Override
    public void voidPayment(UUID paymentId) {
        log.info("Voiding payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        
        validatePaymentForVoid(payment);
        
        // Instead of deleting, mark as voided (add status field to Payment entity)
        // For now, we'll delete it
        paymentRepository.delete(payment);
        
        // Update invoice status if linked
        if (payment.getInvoice() != null) {
            updateInvoiceStatus(payment.getInvoice());
        }
        
        log.info("Payment voided successfully");
    }

    /**
     * Get payment statistics.
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentStatisticsDto getPaymentStatistics(UUID patientId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating payment statistics for patient: {}", patientId);
        
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalRefunded = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;
        BigDecimal netAmount = BigDecimal.ZERO;
        Integer paymentCount = 0;
        LocalDate lastPaymentDate = null;
        Map<String, BigDecimal> byMethod = new HashMap<>();
        
        if (patientId != null) {
            totalCollected = paymentRepository.calculateTotalByType(patientId, PaymentType.PAYMENT);
            totalRefunded = paymentRepository.calculateTotalByType(patientId, PaymentType.REFUND);
            totalCredits = paymentRepository.calculateTotalByType(patientId, PaymentType.CREDIT);
            
            netAmount = totalCollected
                .subtract(totalRefunded)
                .add(totalCredits);
            
            paymentCount = paymentRepository.countPaymentsByPatientId(patientId);
            lastPaymentDate = paymentRepository.findLastPaymentDate(patientId);
            
            // Payment method breakdown
            List<Object[]> methodBreakdown = paymentRepository.calculatePaymentMethodBreakdown(patientId);
            for (Object[] row : methodBreakdown) {
                byMethod.put((String) row[0], (BigDecimal) row[1]);
            }
        }
        
        return new PaymentStatisticsDto(
            totalCollected,
            totalRefunded,
            totalCredits,
            netAmount,
            paymentCount,
            null, // refundCount
            null, // creditCount
            null, // averagePaymentAmount
            null, // firstPaymentDate
            lastPaymentDate,
            byMethod,
            new HashMap<>(), // byMonth
            new HashMap<>()  // countByType
        );
    }

    /**
     * Process bulk payments.
     */
    @Override
    public BulkPaymentResponse processBulkPayments(BulkPaymentRequest request) {
        log.info("Processing bulk payments - count: {}", request.payments().size());
        
        List<PaymentDto> successfulPayments = new ArrayList<>();
        List<BulkPaymentResponse.BulkPaymentError> errors = new ArrayList<>();
        
        for (int i = 0; i < request.payments().size(); i++) {
            BulkPaymentItem item = request.payments().get(i);
            try {
                PaymentCreateRequest paymentRequest = new PaymentCreateRequest(
                    item.amount(),
                    item.paymentDate(),
                    item.paymentMethod(),
                    item.description(),
                    item.referenceNumber()
                );
                
                PaymentDto payment = createPayment(paymentRequest, item.patientId(), item.invoiceId());
                successfulPayments.add(payment);
                
            } catch (Exception e) {
                log.error("Error processing payment at index {}: {}", i, e.getMessage());
                
                BulkPaymentResponse.BulkPaymentError error = new BulkPaymentResponse.BulkPaymentError(
                    i,
                    item.patientId().toString(),
                    null,
                    e.getMessage(),
                    e.getClass().getSimpleName()
                );
                errors.add(error);
                
                if (request.stopOnError()) {
                    break;
                }
            }
        }
        
        return new BulkPaymentResponse(
            request.payments().size(),
            successfulPayments.size(),
            errors.size(),
            successfulPayments,
            errors,
            String.format("Processed %d of %d payments successfully", 
                         successfulPayments.size(), request.payments().size())
        );
    }

    /**
     * Get payment method breakdown.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getPaymentMethodBreakdown(UUID patientId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting payment method breakdown");
        
        List<Object[]> breakdown = paymentRepository.calculatePaymentMethodBreakdown(patientId);
        return breakdown.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (BigDecimal) row[1]
            ));
    }

    /**
     * Apply payment to invoice.
     */
    @Override
    public PaymentDto applyPaymentToInvoice(UUID paymentId, UUID invoiceId) {
        log.info("Applying payment {} to invoice {}", paymentId, invoiceId);
        
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        
        if (payment.getInvoice() != null) {
            throw new IllegalStateException("Payment is already allocated to invoice: " + payment.getInvoice().getInvoiceNumber());
        }
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));
        
        validateInvoiceForPayment(invoice);
        
        payment.setInvoice(invoice);
        Payment updatedPayment = paymentRepository.save(payment);

        // Allocation record (full amount)
        PaymentAllocation allocation = new PaymentAllocation();
        allocation.setPayment(updatedPayment);
        allocation.setInvoice(invoice);
        allocation.setAllocatedAmount(updatedPayment.getAmount());
        paymentAllocationRepository.save(allocation);

        // Ledger credit applied
        ledgerService.record(invoice.getPatient().getId(), invoice, updatedPayment,
                LedgerEntryType.CREDIT_APPLIED, updatedPayment.getAmount(), "Applied to single invoice");
        
        updateInvoiceStatus(invoice);
        
        return paymentMapper.toDto(updatedPayment);
    }

    /**
     * Get unallocated payments (credits).
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getUnallocatedPayments(UUID patientId, Pageable pageable) {
        log.debug("Getting unallocated payments for patient: {}", patientId);
        
        Page<Payment> payments;
        if (patientId != null) {
            payments = paymentRepository.findByPatientIdAndTypeOrderByPaymentDateDesc(
                patientId, PaymentType.CREDIT, pageable);
        } else {
            // Query for all unallocated payments
            payments = paymentRepository.findAll(pageable);
        }
        
        return payments.map(paymentMapper::toDto);
    }

    @Override
    public PaymentDto allocatePayment(UUID paymentId, java.util.List<PaymentAllocationItem> allocations) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        // Validate totals
        BigDecimal total = allocations.stream().map(PaymentAllocationItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(payment.getAmount()) != 0) {
            throw new IllegalArgumentException("Allocation total must equal payment amount");
        }

        for (PaymentAllocationItem item : allocations) {
            Invoice invoice = invoiceRepository.findById(item.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + item.invoiceId()));

            validateInvoiceForPayment(invoice);

            PaymentAllocation allocation = new PaymentAllocation();
            allocation.setPayment(payment);
            allocation.setInvoice(invoice);
            allocation.setAllocatedAmount(item.amount());
            paymentAllocationRepository.save(allocation);

            // Ledger for credit applied
            ledgerService.record(invoice.getPatient().getId(), invoice, payment,
                    LedgerEntryType.CREDIT_APPLIED, item.amount(), "Allocation to invoice");

            updateInvoiceStatus(invoice);
        }

        return paymentMapper.toDto(payment);
    }

    // Private helper methods

    private void validateInvoiceForPayment(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already fully paid");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot apply payment to cancelled invoice");
        }
    }

    private void validatePaymentForUpdate(Payment payment) {
        // Add validation logic for payment updates
        // e.g., check if payment is reconciled, voided, etc.
    }

    private void validatePaymentForVoid(Payment payment) {
        // Add validation logic for voiding payments
        // e.g., check if payment is already voided, reconciled, etc.
    }

    private void updateInvoiceStatus(Invoice invoice) {
        BigDecimal totalPaid = invoice.getPayments().stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        } else {
            invoice.setStatus(InvoiceStatus.UNPAID);
        }
        
        invoiceRepository.save(invoice);
    }
}