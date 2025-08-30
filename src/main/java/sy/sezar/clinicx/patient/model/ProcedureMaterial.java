package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents materials consumed during a procedure.
 * Tracks material usage for accurate procedure costing and inventory.
 * Follows Single Responsibility Principle - manages material consumption data only.
 */
@Entity
@Table(name = "procedure_materials", indexes = {
    @Index(name = "idx_procedure_materials_procedure_id", columnList = "procedure_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"procedure"})
@EqualsAndHashCode(callSuper = true, exclude = {"procedure"})
public class ProcedureMaterial extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", nullable = false)
    private VisitProcedure procedure;

    @Column(name = "material_id")
    private java.util.UUID materialId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "material_name", nullable = false)
    private String materialName;

    @Size(max = 50)
    @Column(name = "material_code", length = 50)
    private String materialCode;

    @NotNull
    @DecimalMin("0.001")
    @DecimalMax("9999.999")
    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @NotBlank
    @Size(max = 20)
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("99999.99")
    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("99999.99")
    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * Calculate total cost based on quantity and unit cost
     */
    @Transient
    public BigDecimal calculateTotalCost() {
        if (quantity == null || unitCost == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(unitCost);
    }

    /**
     * Validate that total cost matches calculated value
     */
    @Transient
    public boolean isCostValid() {
        BigDecimal calculated = calculateTotalCost();
        return totalCost != null && totalCost.compareTo(calculated) == 0;
    }

    /**
     * Set total cost based on quantity and unit cost
     */
    public void recalculateTotalCost() {
        this.totalCost = calculateTotalCost();
    }

    /**
     * Validate and recalculate total cost before persist/update
     */
    @PrePersist
    @PreUpdate
    protected void validateAndCalculateCost() {
        if (totalCost == null || !isCostValid()) {
            recalculateTotalCost();
        }
    }

    /**
     * Mark material as consumed
     */
    public void markAsConsumed() {
        if (this.consumedAt == null) {
            this.consumedAt = Instant.now();
        }
    }

    /**
     * Check if material has been consumed
     */
    @Transient
    public boolean isConsumed() {
        return consumedAt != null;
    }
}