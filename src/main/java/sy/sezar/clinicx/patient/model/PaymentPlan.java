package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.patient.model.enums.PaymentPlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_plans")
@Getter
@Setter
public class PaymentPlan extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @NotNull
    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @Positive
    @Column(name = "installment_count", nullable = false)
    private Integer installmentCount;

    @NotNull
    @Column(name = "installment_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal installmentAmount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "frequency_days")
    private Integer frequencyDays; // Days between installments

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PaymentPlanStatus status = PaymentPlanStatus.ACTIVE;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Staff createdBy;

    @OneToMany(mappedBy = "paymentPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentPlanInstallment> installments = new HashSet<>();
}
