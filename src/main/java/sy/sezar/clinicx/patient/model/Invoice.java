package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import sy.sezar.clinicx.clinic.model.Staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull
    @Size(max = 50)
    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "sub_total", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "adjustment_amount", precision = 10, scale = 2)
    private BigDecimal adjustmentAmount = BigDecimal.ZERO;

    @Column(name = "write_off_amount", precision = 10, scale = 2)
    private BigDecimal writeOffAmount = BigDecimal.ZERO;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "amount_due", precision = 10, scale = 2)
    private BigDecimal amountDue = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Staff createdBy;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InvoiceItem> items = new HashSet<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private Set<Payment> payments = new HashSet<>();
}

