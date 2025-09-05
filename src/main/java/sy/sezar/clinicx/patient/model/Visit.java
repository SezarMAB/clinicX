package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.clinic.model.Staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a visit/encounter - a container for multiple procedures.
 * This is the header record that groups procedures performed during a patient visit.
 * Follows Domain-Driven Design principles as an aggregate root.
 */
@Entity
@Table(name = "visits", indexes = {
    @Index(name = "idx_visits_patient_id", columnList = "patient_id"),
    @Index(name = "idx_visits_date", columnList = "date"),
    @Index(name = "idx_visits_provider_id", columnList = "provider_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"procedures", "patient", "treatment", "appointment", "provider"})
@EqualsAndHashCode(callSuper = true, exclude = {"procedures", "patient", "treatment", "appointment", "provider"})
public class Visit extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Staff provider;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Size(max = 1000)
    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<VisitProcedure> procedures = new HashSet<>();

    /**
     * Add a procedure to this visit
     */
    public void addProcedure(VisitProcedure procedure) {
        procedures.add(procedure);
        procedure.setVisit(this);
    }

    /**
     * Remove a procedure from this visit
     */
    public void removeProcedure(VisitProcedure procedure) {
        procedures.remove(procedure);
        procedure.setVisit(null);
    }

    /**
     * Calculate total cost from all billable procedures
     */
    @Transient
    public BigDecimal getTotalCost() {
        return procedures.stream()
                .filter(p -> Boolean.TRUE.equals(p.getBillable()))
                .map(VisitProcedure::getTotalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total material cost from all procedures
     */
    @Transient
    public BigDecimal getTotalMaterialCost() {
        return procedures.stream()
                .map(VisitProcedure::getTotalMaterialCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Derive overall status based on procedure statuses
     */
    @Transient
    public String getOverallStatus() {
        if (procedures.isEmpty()) {
            return "NO_PROCEDURES";
        }

        boolean allCompleted = procedures.stream()
                .allMatch(p -> p.getStatus().isFinal());
        if (allCompleted) {
            return "COMPLETED";
        }

        boolean anyInProgress = procedures.stream()
                .anyMatch(p -> !p.getStatus().isFinal());
        if (anyInProgress) {
            return "IN_PROGRESS";
        }

        return "PLANNED";
    }

    /**
     * Check if visit has any procedures with lab work
     */
    @Transient
    public boolean hasLabWork() {
        return procedures.stream().anyMatch(VisitProcedure::hasLabWork);
    }

    /**
     * Get count of procedures
     */
    @Transient
    public int getProcedureCount() {
        return procedures.size();
    }
}
