package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.core.exception.ResourceNotFoundException;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import sy.sezar.clinicx.patient.model.enums.PaymentType;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;
import sy.sezar.clinicx.patient.repository.InvoiceRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.repository.PaymentRepository;
import sy.sezar.clinicx.patient.service.RefundService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of RefundService for managing refunds.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public PaymentDto processRefund(RefundRequest request) {
        log.info("Processing refund for patient: {} amount: {}", request.patientId(), request.amount());
        
        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.patientId()));
        
        Invoice invoice = null;
        if (request.invoiceId() != null) {
            invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + request.invoiceId()));
        }
        
        Payment refund = new Payment();
        refund.setPatient(patient);
        refund.setInvoice(invoice);
        refund.setAmount(request.amount().negate()); // Negative amount for refund
        refund.setPaymentDate(LocalDate.now());
        refund.setPaymentMethod(request.paymentMethod());
        refund.setType(PaymentType.REFUND);
        refund.setDescription("REFUND: " + request.reason());
        refund.setReferenceNumber(generateRefundReference());
        
        Payment savedRefund = paymentRepository.save(refund);
        
        // Update invoice status if applicable
        if (invoice != null) {
            updateInvoiceStatusAfterRefund(invoice);
        }
        
        log.info("Refund processed successfully with ID: {}", savedRefund.getId());
        return mapToPaymentDto(savedRefund);
    }

    @Override
    public Page<PaymentDto> getRefunds(UUID patientId, LocalDate startDate, LocalDate endDate, 
                                       String status, Pageable pageable) {
        log.debug("Fetching refunds with filters");
        
        Page<Payment> refunds;
        if (patientId != null) {
            refunds = paymentRepository.findByPatientIdAndType(patientId, PaymentType.REFUND, pageable);
        } else {
            refunds = paymentRepository.findByType(PaymentType.REFUND, pageable);
        }
        
        return refunds.map(this::mapToPaymentDto);
    }

    @Override
    @Transactional
    public PaymentDto approveRefund(UUID refundId, String approvalNotes) {
        log.info("Approving refund: {}", refundId);
        
        Payment refund = paymentRepository.findById(refundId)
            .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId));
        
        if (refund.getType() != PaymentType.REFUND) {
            throw new IllegalArgumentException("Payment is not a refund: " + refundId);
        }
        
        // Add approval notes to description
        String updatedDescription = refund.getDescription() + " | APPROVED: " + approvalNotes;
        refund.setDescription(updatedDescription);
        
        Payment savedRefund = paymentRepository.save(refund);
        return mapToPaymentDto(savedRefund);
    }

    @Override
    @Transactional
    public PaymentDto rejectRefund(UUID refundId, String rejectionReason) {
        log.info("Rejecting refund: {} with reason: {}", refundId, rejectionReason);
        
        Payment refund = paymentRepository.findById(refundId)
            .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId));
        
        if (refund.getType() != PaymentType.REFUND) {
            throw new IllegalArgumentException("Payment is not a refund: " + refundId);
        }
        
        // Add rejection reason to description
        String updatedDescription = refund.getDescription() + " | REJECTED: " + rejectionReason;
        refund.setDescription(updatedDescription);
        
        Payment savedRefund = paymentRepository.save(refund);
        return mapToPaymentDto(savedRefund);
    }

    @Override
    @Transactional
    public void cancelRefund(UUID refundId, String cancellationReason) {
        log.info("Cancelling refund: {}", refundId);
        
        Payment refund = paymentRepository.findById(refundId)
            .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId));
        
        if (refund.getType() != PaymentType.REFUND) {
            throw new IllegalArgumentException("Payment is not a refund: " + refundId);
        }
        
        // Mark as cancelled in description
        String updatedDescription = refund.getDescription() + " | CANCELLED: " + cancellationReason;
        refund.setDescription(updatedDescription);
        
        paymentRepository.save(refund);
    }

    @Override
    public RefundDetailsDto getRefundDetails(UUID refundId) {
        log.debug("Getting refund details for: {}", refundId);
        
        Payment refund = paymentRepository.findById(refundId)
            .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId));
        
        if (refund.getType() != PaymentType.REFUND) {
            throw new IllegalArgumentException("Payment is not a refund: " + refundId);
        }
        
        // TODO: Return proper DTO when structure is finalized
        return null;
    }

    @Override
    @Transactional
    public BatchRefundResponse processBatchRefunds(BatchRefundRequest request) {
        log.info("Processing batch refunds - count: {}", request.refunds().size());
        
        List<PaymentDto> successfulRefunds = new ArrayList<>();
        Map<String, String> failedRefunds = new HashMap<>();
        
        for (RefundRequest refundRequest : request.refunds()) {
            try {
                PaymentDto refund = processRefund(refundRequest);
                successfulRefunds.add(refund);
            } catch (Exception e) {
                log.error("Failed to process refund for patient: {}", refundRequest.patientId(), e);
                failedRefunds.put(refundRequest.patientId().toString(), e.getMessage());
            }
        }
        
        // TODO: Return proper DTO when structure is finalized
        return null;
    }

    @Override
    public Page<PaymentDto> getPendingRefunds(Pageable pageable) {
        log.debug("Getting pending refunds");
        
        // Get refunds that don't have APPROVED or REJECTED in their description
        Page<Payment> refunds = paymentRepository.findByType(PaymentType.REFUND, pageable);
        
        List<PaymentDto> pendingRefunds = refunds.getContent().stream()
            .filter(r -> !r.getDescription().contains("APPROVED") && 
                        !r.getDescription().contains("REJECTED") &&
                        !r.getDescription().contains("CANCELLED"))
            .map(this::mapToPaymentDto)
            .collect(Collectors.toList());
        
        return new PageImpl<>(pendingRefunds, pageable, pendingRefunds.size());
    }

    @Override
    @Transactional
    public PaymentDto processApprovedRefund(UUID refundId, ProcessRefundRequest request) {
        log.info("Processing approved refund: {}", refundId);
        
        Payment refund = paymentRepository.findById(refundId)
            .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId));
        
        if (refund.getType() != PaymentType.REFUND) {
            throw new IllegalArgumentException("Payment is not a refund: " + refundId);
        }
        
        // Update refund with processing details
        if (request.transactionId() != null) {
            refund.setReferenceNumber(request.transactionId());
        }
        
        String updatedDescription = refund.getDescription() + 
            " | PROCESSED: " + request.processingNotes() +
            " | Method: " + request.refundMethod();
        refund.setDescription(updatedDescription);
        
        Payment savedRefund = paymentRepository.save(refund);
        return mapToPaymentDto(savedRefund);
    }

    @Override
    public RefundSummaryDto getRefundSummary(LocalDate startDate, LocalDate endDate, String groupBy) {
        log.debug("Getting refund summary from {} to {}", startDate, endDate);
        
        List<Payment> refunds = paymentRepository.findByTypeAndPaymentDateBetween(
            PaymentType.REFUND, startDate, endDate);
        
        BigDecimal totalAmount = refunds.stream()
            .map(Payment::getAmount)
            .map(BigDecimal::abs)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long approvedCount = refunds.stream()
            .filter(r -> r.getDescription().contains("APPROVED"))
            .count();
        
        long rejectedCount = refunds.stream()
            .filter(r -> r.getDescription().contains("REJECTED"))
            .count();
        
        long pendingCount = refunds.stream()
            .filter(r -> !r.getDescription().contains("APPROVED") && 
                        !r.getDescription().contains("REJECTED") &&
                        !r.getDescription().contains("CANCELLED"))
            .count();
        
        Map<String, BigDecimal> breakdown = new HashMap<>();
        if ("payment_method".equals(groupBy)) {
            breakdown = refunds.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getPaymentMethod().getDisplayName(),
                    Collectors.reducing(BigDecimal.ZERO, 
                        p -> p.getAmount().abs(), 
                        BigDecimal::add)
                ));
        } else if ("month".equals(groupBy)) {
            breakdown = refunds.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getPaymentDate().getMonth().toString(),
                    Collectors.reducing(BigDecimal.ZERO, 
                        p -> p.getAmount().abs(), 
                        BigDecimal::add)
                ));
        }
        
        // TODO: Return proper DTO when structure is finalized
        return null;
    }

    private PaymentDto mapToPaymentDto(Payment payment) {
        // TODO: Return proper DTO when structure is finalized
        return null;
    }

    private void updateInvoiceStatusAfterRefund(Invoice invoice) {
        // Recalculate invoice status based on payments
        BigDecimal totalPaid = paymentRepository.findByInvoiceId(invoice.getId()).stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPaid.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.UNPAID);
        } else if (totalPaid.compareTo(invoice.getTotalAmount()) < 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }
        
        invoiceRepository.save(invoice);
    }

    private String generateRefundReference() {
        return "REF-" + System.currentTimeMillis();
    }

    private String extractRefundStatus(String description) {
        if (description.contains("APPROVED")) return "APPROVED";
        if (description.contains("REJECTED")) return "REJECTED";
        if (description.contains("CANCELLED")) return "CANCELLED";
        if (description.contains("PROCESSED")) return "PROCESSED";
        return "PENDING";
    }
}