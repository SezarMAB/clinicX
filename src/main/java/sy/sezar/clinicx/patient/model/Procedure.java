package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.clinic.model.Specialty;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "procedure_templates")
@Getter
@Setter
public class Procedure extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Size(max = 20)
    @Column(name = "procedure_code", unique = true, length = 20)
    private String procedureCode;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "default_cost", precision = 10, scale = 2)
    private BigDecimal defaultCost;

    @Column(name = "default_duration_minutes")
    private Integer defaultDurationMinutes;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}

