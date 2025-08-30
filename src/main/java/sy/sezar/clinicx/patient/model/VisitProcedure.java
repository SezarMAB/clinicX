package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.patient.model.enums.ProcedureStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a clinical procedure performed during a visit.
 * Each procedure is a billable line item with its own status and tracking.
 * Follows Single Responsibility Principle - manages procedure data only.
 */
@Entity
@Table(name = "procedures", indexes = {
    @Index(name = "idx_procedures_visit_id", columnList = "visit_id"),
    @Index(name = "idx_procedures_status", columnList = "status"),
    @Index(name = "idx_procedures_tooth_number", columnList = "tooth_number"),
    @Index(name = "idx_procedures_code", columnList = "code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"visit", "labCase", "materials"})
@EqualsAndHashCode(callSuper = true, exclude = {"visit", "labCase", "materials"})
public class VisitProcedure extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @NotBlank
    @Size(max = 20)
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Min(11)
    @Max(48)
    @Column(name = "tooth_number")
    private Integer toothNumber;

    @ElementCollection
    @CollectionTable(
        name = "procedure_surfaces",
        joinColumns = @JoinColumn(name = "procedure_id")
    )
    @Column(name = "surface")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Builder.Default
    private List<String> surfaces = new ArrayList<>();

    @NotNull
    @Min(1)
    @Max(32)
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("999999.99")
    @Column(name = "unit_fee", nullable = false, precision = 8, scale = 2)
    private BigDecimal unitFee;

    @Min(5)
    @Max(480)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private Staff performedBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ProcedureStatus status = ProcedureStatus.PLANNED;

    @NotNull
    @Column(name = "billable", nullable = false)
    @Builder.Default
    private Boolean billable = true;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @OneToOne(mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private LabCase labCase;

    @OneToMany(mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ProcedureMaterial> materials = new HashSet<>();

    /**
     * Calculate total fee for this procedure
     */
    @Transient
    public BigDecimal getTotalFee() {
        if (unitFee == null || quantity == null || !billable) {
            return BigDecimal.ZERO;
        }
        return unitFee.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Calculate total material cost
     */
    @Transient
    public BigDecimal getTotalMaterialCost() {
        return materials.stream()
                .map(ProcedureMaterial::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Update status with automatic timestamp tracking
     */
    public void updateStatus(ProcedureStatus newStatus) {
        if (this.status != null && !this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }

        if (newStatus == ProcedureStatus.IN_PROGRESS && this.startedAt == null) {
            this.startedAt = Instant.now();
        } else if (newStatus == ProcedureStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = Instant.now();
        }

        this.status = newStatus;
    }

    /**
     * Add material to procedure
     */
    public void addMaterial(ProcedureMaterial material) {
        materials.add(material);
        material.setProcedure(this);
    }

    /**
     * Remove material from procedure
     */
    public void removeMaterial(ProcedureMaterial material) {
        materials.remove(material);
        material.setProcedure(null);
    }

    /**
     * Check if procedure can be modified
     */
    @Transient
    public boolean canModify() {
        return status != null && status.canModify();
    }

    /**
     * Check if procedure has lab work
     */
    @Transient
    public boolean hasLabWork() {
        return labCase != null;
    }
}