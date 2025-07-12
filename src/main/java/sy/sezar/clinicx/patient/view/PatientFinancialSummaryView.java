package sy.sezar.clinicx.patient.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a read-only view of a patient's financial summary (v_patient_financial_summary).
 * This entity aggregates key financial metrics for each patient, such as their current balance,
 * total number of invoices, and outstanding amounts. It is intended for reporting and
 * financial overview purposes and cannot be modified.
 */
@Entity
@Table(name = "v_patient_financial_summary")
@Immutable
@Getter
public class PatientFinancialSummaryView {

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", insertable = false, updatable = false)
    private String fullName;

    @Column(name = "public_facing_id", insertable = false, updatable = false)
    private String publicFacingId;

    @Column(name = "balance", insertable = false, updatable = false)
    private BigDecimal balance;

    @Column(name = "total_invoices", insertable = false, updatable = false)
    private Long totalInvoices;

    @Column(name = "unpaid_invoices", insertable = false, updatable = false)
    private Long unpaidInvoices;

    @Column(name = "total_unpaid", insertable = false, updatable = false)
    private BigDecimal totalUnpaid;
}

