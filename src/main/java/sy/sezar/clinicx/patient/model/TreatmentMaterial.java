package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "treatment_materials")
@Getter
@Setter
public class TreatmentMaterial extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @NotBlank
    @Column(name = "material_name", nullable = false, length = 100)
    private String materialName;

    @NotNull
    @Positive
    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @NotNull
    @Positive
    @Column(name = "cost_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerUnit;

    @NotNull
    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "supplier", length = 100)
    private String supplier;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "notes")
    private String notes;

    @PrePersist
    @PreUpdate
    private void calculateTotalCost() {
        if (quantity != null && costPerUnit != null) {
            this.totalCost = quantity.multiply(costPerUnit);
        }
    }
}
