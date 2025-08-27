package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.patient.dto.PaymentPlanDto;
import sy.sezar.clinicx.patient.dto.PaymentPlanCreateRequest;
import sy.sezar.clinicx.patient.dto.PaymentPlanInstallmentDto;
import sy.sezar.clinicx.patient.dto.PaymentPlanStatisticsDto;
import sy.sezar.clinicx.patient.dto.PaymentPlanReportDto;
import sy.sezar.clinicx.patient.model.PaymentPlan;
import sy.sezar.clinicx.patient.model.PaymentPlanInstallment;
import sy.sezar.clinicx.patient.model.enums.InstallmentStatus;
import sy.sezar.clinicx.patient.model.enums.PaymentPlanStatus;
import sy.sezar.clinicx.patient.repository.PaymentPlanRepository;
import sy.sezar.clinicx.patient.service.PaymentPlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Comparator;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.math.RoundingMode;

/**
 * Implementation of PaymentPlanService with support for variable installment amounts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentPlanServiceImpl implements PaymentPlanService {

    private final PaymentPlanRepository paymentPlanRepository;

    @Override
    @Transactional
    public PaymentPlanDto createPaymentPlanWithVariableAmounts(PaymentPlanCreateRequest request) {
        log.info("Creating payment plan with variable amounts for patient: {}", request.patientId());
        
        // Validate that total matches the sum of variable amounts
        BigDecimal totalVariableAmount = request.variableInstallmentAmounts().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalVariableAmount.compareTo(request.totalAmount()) != 0) {
            throw new IllegalArgumentException(
                "Sum of variable amounts (" + totalVariableAmount + 
                ") must equal total amount (" + request.totalAmount() + ")");
        }
        
        // Create the payment plan
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setPlanName(request.planName());
        paymentPlan.setTotalAmount(request.totalAmount());
        paymentPlan.setInstallmentCount(request.variableInstallmentAmounts().size());
        paymentPlan.setStartDate(request.startDate());
        paymentPlan.setStatus(PaymentPlanStatus.ACTIVE);
        paymentPlan.setNotes(request.notes());
        
        // Create installments with variable amounts
        List<PaymentPlanInstallment> installments = new ArrayList<>();
        for (int i = 0; i < request.variableInstallmentAmounts().size(); i++) {
            PaymentPlanInstallment installment = new PaymentPlanInstallment();
            installment.setPaymentPlan(paymentPlan);
            installment.setInstallmentNumber(i + 1);
            installment.setAmount(request.variableInstallmentAmounts().get(i));
            installment.setStatus(InstallmentStatus.PENDING);
            
            // Set due date (use custom dates if provided, otherwise calculate)
            LocalDate dueDate;
            if (request.customDueDates() != null && i < request.customDueDates().size()) {
                dueDate = request.customDueDates().get(i);
            } else {
                dueDate = request.startDate().plusDays(
                    (request.frequencyDays() != null ? request.frequencyDays() : 30) * i
                );
            }
            installment.setDueDate(dueDate);
            
            installments.add(installment);
        }
        
        paymentPlan.setInstallments(new HashSet<>(installments));
        
        PaymentPlan savedPlan = paymentPlanRepository.save(paymentPlan);
        log.info("Created payment plan with {} variable installments", installments.size());
        
        return mapToDto(savedPlan);
    }

    @Override
    @Transactional
    public PaymentPlanDto createCustomPaymentPlan(UUID patientId, UUID invoiceId, String planName, 
                                                 LocalDate startDate, List<BigDecimal> installmentAmounts, 
                                                 List<LocalDate> dueDates) {
        log.info("Creating custom payment plan for patient: {} with {} installments", patientId, installmentAmounts.size());
        
        // Validate inputs
        if (installmentAmounts.size() != dueDates.size()) {
            throw new IllegalArgumentException("Number of amounts must match number of due dates");
        }
        
        BigDecimal totalAmount = installmentAmounts.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Create payment plan
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setPlanName(planName);
        paymentPlan.setTotalAmount(totalAmount);
        paymentPlan.setInstallmentCount(installmentAmounts.size());
        paymentPlan.setStartDate(startDate);
        paymentPlan.setStatus(PaymentPlanStatus.ACTIVE);
        
        // Create installments
        List<PaymentPlanInstallment> installments = new ArrayList<>();
        for (int i = 0; i < installmentAmounts.size(); i++) {
            PaymentPlanInstallment installment = new PaymentPlanInstallment();
            installment.setPaymentPlan(paymentPlan);
            installment.setInstallmentNumber(i + 1);
            installment.setAmount(installmentAmounts.get(i));
            installment.setDueDate(dueDates.get(i));
            installment.setStatus(InstallmentStatus.PENDING);
            
            installments.add(installment);
        }
        
        paymentPlan.setInstallments(new HashSet<>(installments));
        
        PaymentPlan savedPlan = paymentPlanRepository.save(paymentPlan);
        log.info("Created custom payment plan with total amount: {}", totalAmount);
        
        return mapToDto(savedPlan);
    }

    // Example method showing different variable amount scenarios
    public PaymentPlanDto createFlexiblePaymentPlan(UUID patientId, UUID invoiceId, BigDecimal totalAmount) {
        log.info("Creating flexible payment plan for amount: {}", totalAmount);
        
        // Scenario 1: Front-loaded payments (higher amounts early)
        if (totalAmount.compareTo(new BigDecimal("2000")) > 0) {
            return createFrontLoadedPlan(patientId, invoiceId, totalAmount);
        }
        
        // Scenario 2: Back-loaded payments (higher amounts later)
        else if (totalAmount.compareTo(new BigDecimal("1000")) > 0) {
            return createBackLoadedPlan(patientId, invoiceId, totalAmount);
        }
        
        // Scenario 3: Even distribution
        else {
            return createEvenDistributionPlan(patientId, invoiceId, totalAmount);
        }
    }
    
    private PaymentPlanDto createFrontLoadedPlan(UUID patientId, UUID invoiceId, BigDecimal totalAmount) {
        // Example: $3000 total -> $1000, $800, $600, $400, $200
        List<BigDecimal> amounts = List.of(
            new BigDecimal("1000.00"),
            new BigDecimal("800.00"),
            new BigDecimal("600.00"),
            new BigDecimal("400.00"),
            new BigDecimal("200.00")
        );
        
        List<LocalDate> dueDates = List.of(
            LocalDate.now().plusDays(0),   // Immediate
            LocalDate.now().plusDays(30),  // 1 month
            LocalDate.now().plusDays(60),  // 2 months
            LocalDate.now().plusDays(90),  // 3 months
            LocalDate.now().plusDays(120)  // 4 months
        );
        
        return createCustomPaymentPlan(patientId, invoiceId, "Front-Loaded Payment Plan", 
                                     LocalDate.now(), amounts, dueDates);
    }
    
    private PaymentPlanDto createBackLoadedPlan(UUID patientId, UUID invoiceId, BigDecimal totalAmount) {
        // Example: $1500 total -> $200, $300, $400, $600
        List<BigDecimal> amounts = List.of(
            new BigDecimal("200.00"),
            new BigDecimal("300.00"),
            new BigDecimal("400.00"),
            new BigDecimal("600.00")
        );
        
        List<LocalDate> dueDates = List.of(
            LocalDate.now().plusDays(0),   // Immediate
            LocalDate.now().plusDays(30),  // 1 month
            LocalDate.now().plusDays(60),  // 2 months
            LocalDate.now().plusDays(90)   // 3 months
        );
        
        return createCustomPaymentPlan(patientId, invoiceId, "Back-Loaded Payment Plan", 
                                     LocalDate.now(), amounts, dueDates);
    }
    
    private PaymentPlanDto createEvenDistributionPlan(UUID patientId, UUID invoiceId, BigDecimal totalAmount) {
        // Even distribution: $800 total -> $200, $200, $200, $200
        int installmentCount = 4;
        BigDecimal installmentAmount = totalAmount.divide(new BigDecimal(installmentCount), 2, RoundingMode.HALF_UP);
        
        List<BigDecimal> amounts = new ArrayList<>();
        BigDecimal remaining = totalAmount;
        
        for (int i = 0; i < installmentCount; i++) {
            if (i == installmentCount - 1) {
                // Last installment gets the remainder to avoid rounding issues
                amounts.add(remaining);
            } else {
                amounts.add(installmentAmount);
                remaining = remaining.subtract(installmentAmount);
            }
        }
        
        List<LocalDate> dueDates = new ArrayList<>();
        for (int i = 0; i < installmentCount; i++) {
            dueDates.add(LocalDate.now().plusDays(30 * i));
        }
        
        return createCustomPaymentPlan(patientId, invoiceId, "Even Distribution Payment Plan", 
                                     LocalDate.now(), amounts, dueDates);
    }

    @Override
    @Transactional
    public PaymentPlanDto createPaymentPlan(PaymentPlanCreateRequest request) {
        log.info("Creating payment plan for patient: {}", request.patientId());
        
        // Create the payment plan
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setPlanName(request.planName());
        paymentPlan.setTotalAmount(request.totalAmount());
        paymentPlan.setInstallmentCount(request.installmentCount());
        paymentPlan.setStartDate(request.startDate());
        paymentPlan.setStatus(PaymentPlanStatus.ACTIVE);
        paymentPlan.setNotes(request.notes());
        
        // Create installments
        List<PaymentPlanInstallment> installments = new ArrayList<>();
        BigDecimal installmentAmount = request.installmentAmount();
        
        for (int i = 0; i < request.installmentCount(); i++) {
            PaymentPlanInstallment installment = new PaymentPlanInstallment();
            installment.setPaymentPlan(paymentPlan);
            installment.setInstallmentNumber(i + 1);
            installment.setAmount(installmentAmount);
            installment.setStatus(InstallmentStatus.PENDING);
            installment.setDueDate(request.startDate().plusDays(
                (request.frequencyDays() != null ? request.frequencyDays() : 30) * i
            ));
            
            installments.add(installment);
        }
        
        paymentPlan.setInstallments(new HashSet<>(installments));
        
        PaymentPlan savedPlan = paymentPlanRepository.save(paymentPlan);
        log.info("Created payment plan with {} installments", installments.size());
        
        return mapToDto(savedPlan);
    }

    @Override
    public PaymentPlanDto getPaymentPlan(UUID planId) {
        log.info("Getting payment plan: {}", planId);
        PaymentPlan paymentPlan = paymentPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Payment plan not found: " + planId));
        return mapToDto(paymentPlan);
    }

    @Override
    public List<PaymentPlanInstallmentDto> getPaymentPlanInstallments(UUID planId) {
        log.info("Getting installments for payment plan: {}", planId);
        PaymentPlan paymentPlan = paymentPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Payment plan not found: " + planId));
        
        return paymentPlan.getInstallments().stream()
            .map(this::mapInstallmentToDto)
            .sorted(Comparator.comparing(PaymentPlanInstallmentDto::installmentNumber))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentPlanDto updatePaymentPlanStatus(UUID planId, PaymentPlanStatus status, String notes) {
        log.info("Updating payment plan status: {} to {}", planId, status);
        PaymentPlan paymentPlan = paymentPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Payment plan not found: " + planId));
        
        paymentPlan.setStatus(status);
        if (notes != null) {
            paymentPlan.setNotes(notes);
        }
        
        PaymentPlan savedPlan = paymentPlanRepository.save(paymentPlan);
        return mapToDto(savedPlan);
    }

    @Override
    @Transactional
    public PaymentPlanInstallmentDto recordInstallmentPayment(UUID installmentId, BigDecimal amount, 
                                                             LocalDate paymentDate, String notes) {
        log.info("Recording payment for installment: {} amount: {}", installmentId, amount);
        // Implementation would require PaymentPlanInstallmentRepository
        // For now, return a mock implementation
        return new PaymentPlanInstallmentDto(
            installmentId, UUID.randomUUID(), 1, LocalDate.now(), 
            amount, amount, paymentDate, InstallmentStatus.PAID, notes,
            false, 0
        );
    }

    @Override
    public Page<PaymentPlanDto> getPaymentPlansByStatus(PaymentPlanStatus status, Pageable pageable) {
        log.info("Getting payment plans by status: {}", status);
        Page<PaymentPlan> paymentPlans = paymentPlanRepository.findByStatus(status, pageable);
        return paymentPlans.map(this::mapToDto);
    }

    @Override
    public Page<PaymentPlanInstallmentDto> getOverdueInstallments(Pageable pageable) {
        log.info("Getting overdue installments");
        // Implementation would require PaymentPlanInstallmentRepository
        // For now, return empty page
        return Page.empty(pageable);
    }

    @Override
    public List<PaymentPlanInstallmentDto> getInstallmentsDueBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Getting installments due between {} and {}", startDate, endDate);
        // Implementation would require PaymentPlanInstallmentRepository
        // For now, return empty list
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public int markOverdueInstallments() {
        log.info("Marking overdue installments");
        // Implementation would require PaymentPlanInstallmentRepository
        // This would update installments where due_date < today and status = PENDING
        return 0; // Return count of marked installments
    }

    @Override
    @Transactional
    public PaymentPlanDto cancelPaymentPlan(UUID planId, String reason) {
        log.info("Cancelling payment plan: {} reason: {}", planId, reason);
        PaymentPlan paymentPlan = paymentPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Payment plan not found: " + planId));
        
        paymentPlan.setStatus(PaymentPlanStatus.CANCELLED);
        paymentPlan.setNotes(reason);
        
        PaymentPlan savedPlan = paymentPlanRepository.save(paymentPlan);
        return mapToDto(savedPlan);
    }

    @Override
    public Page<PaymentPlanDto> getPatientPaymentPlans(UUID patientId, Pageable pageable) {
        log.info("Getting payment plans for patient: {}", patientId);
        Page<PaymentPlan> paymentPlans = paymentPlanRepository.findByPatientId(patientId, pageable);
        return paymentPlans.map(this::mapToDto);
    }

    @Override
    public PaymentPlanStatisticsDto getPaymentPlanStatistics(UUID planId) {
        log.info("Getting statistics for payment plan: {}", planId);
        PaymentPlan paymentPlan = paymentPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Payment plan not found: " + planId));
        
        long totalInstallments = paymentPlan.getInstallments().size();
        long paidInstallments = paymentPlan.getInstallments().stream()
            .filter(i -> i.getStatus() == InstallmentStatus.PAID)
            .count();
        long pendingInstallments = totalInstallments - paidInstallments;
        
        BigDecimal totalPaid = paymentPlan.getInstallments().stream()
            .filter(i -> i.getStatus() == InstallmentStatus.PAID)
            .map(PaymentPlanInstallment::getPaidAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal remainingAmount = paymentPlan.getTotalAmount().subtract(totalPaid);
        
        return new PaymentPlanStatisticsDto(
            paymentPlan.getPatient().getId(),
            paymentPlan.getPatient().getFullName(),
            1, // totalPlans
            paymentPlan.getStatus() == PaymentPlanStatus.ACTIVE ? 1 : 0, // activePlans
            paymentPlan.getStatus() == PaymentPlanStatus.COMPLETED ? 1 : 0, // completedPlans
            paymentPlan.getStatus() == PaymentPlanStatus.DEFAULTED ? 1 : 0, // defaultedPlans
            paymentPlan.getTotalAmount(),
            totalPaid,
            remainingAmount,
            totalInstallments,
            paidInstallments,
            pendingInstallments,
            0 // overdueInstallments
        );
    }

    @Override
    public PaymentPlanReportDto generatePaymentPlanReport(UUID planId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating payment plan report for plan: {} from {} to {}", planId, startDate, endDate);
        
        PaymentPlan paymentPlan = paymentPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Payment plan not found: " + planId));
        
        List<PaymentPlanInstallmentDto> installmentsInPeriod = paymentPlan.getInstallments().stream()
            .filter(i -> !i.getDueDate().isBefore(startDate) && !i.getDueDate().isAfter(endDate))
            .map(this::mapInstallmentToDto)
            .collect(Collectors.toList());
        
        List<PaymentPlanInstallmentDto> overdueInstallments = paymentPlan.getInstallments().stream()
            .filter(i -> i.getStatus() == InstallmentStatus.OVERDUE)
            .map(this::mapInstallmentToDto)
            .collect(Collectors.toList());
        
        return new PaymentPlanReportDto(
            paymentPlan.getPatient().getId(),
            paymentPlan.getPatient().getFullName(),
            LocalDate.now(), // reportDate
            startDate,
            endDate,
            1, // totalPlans
            paymentPlan.getStatus() == PaymentPlanStatus.ACTIVE ? 1 : 0, // activePlans
            paymentPlan.getStatus() == PaymentPlanStatus.COMPLETED ? 1 : 0, // completedPlans
            paymentPlan.getStatus() == PaymentPlanStatus.DEFAULTED ? 1 : 0, // defaultedPlans
            paymentPlan.getTotalAmount(),
            paymentPlan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .map(PaymentPlanInstallment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add), // totalPaidAmount
            paymentPlan.getTotalAmount().subtract(
                paymentPlan.getInstallments().stream()
                    .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                    .map(PaymentPlanInstallment::getPaidAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            ), // remainingAmount
            paymentPlan.getInstallments().size(), // totalInstallments
            (int) paymentPlan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .count(), // paidInstallments
            (int) paymentPlan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PENDING)
                .count(), // pendingInstallments
            (int) paymentPlan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.OVERDUE)
                .count(), // overdueInstallments
            List.of(mapToDto(paymentPlan)), // paymentPlans
            overdueInstallments // overdueInstallmentDetails
        );
    }

    private PaymentPlanDto mapToDto(PaymentPlan paymentPlan) {
        // Implementation of mapping logic
        return new PaymentPlanDto(
            paymentPlan.getId(),
            paymentPlan.getPatient().getId(),
            paymentPlan.getPatient().getFullName(),
            paymentPlan.getInvoice().getId(),
            paymentPlan.getInvoice().getInvoiceNumber(),
            paymentPlan.getPlanName(),
            paymentPlan.getTotalAmount(),
            paymentPlan.getInstallmentCount(),
            paymentPlan.getInstallmentAmount(),
            paymentPlan.getStartDate(),
            paymentPlan.getEndDate(),
            paymentPlan.getFrequencyDays(),
            paymentPlan.getStatus(),
            paymentPlan.getNotes(),
            paymentPlan.getCreatedBy() != null ? paymentPlan.getCreatedBy().getFullName() : null,
            paymentPlan.getCreatedAt(),
            paymentPlan.getUpdatedAt(),
            paymentPlan.getInstallments().stream()
                .map(this::mapInstallmentToDto)
                .collect(Collectors.toList()),
            paymentPlan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .map(PaymentPlanInstallment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add),
            paymentPlan.getTotalAmount().subtract(
                paymentPlan.getInstallments().stream()
                    .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                    .map(PaymentPlanInstallment::getPaidAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            ),
            (int) paymentPlan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .count(),
            (int) paymentPlan.getInstallments().stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PENDING)
                .count()
        );
    }

    private PaymentPlanInstallmentDto mapInstallmentToDto(PaymentPlanInstallment installment) {
        LocalDate today = LocalDate.now();
        boolean isOverdue = installment.getStatus() == InstallmentStatus.PENDING && 
                           installment.getDueDate().isBefore(today);
        int daysPastDue = isOverdue ? (int) java.time.temporal.ChronoUnit.DAYS.between(
            installment.getDueDate(), today) : 0;
        
        return new PaymentPlanInstallmentDto(
            installment.getId(),
            installment.getPaymentPlan().getId(),
            installment.getInstallmentNumber(),
            installment.getDueDate(),
            installment.getAmount(),
            installment.getPaidAmount(),
            installment.getPaidDate(),
            installment.getStatus(),
            installment.getNotes(),
            isOverdue,
            daysPastDue
        );
    }
}
