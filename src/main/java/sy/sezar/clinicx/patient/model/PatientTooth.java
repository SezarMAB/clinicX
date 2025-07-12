package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patient_teeth", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"patient_id", "tooth_number"})
})
@Getter
@Setter
public class PatientTooth extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "tooth_number", nullable = false)
    private Integer toothNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_condition_id")
    private ToothCondition currentCondition;

    @Column(name = "notes")
    private String notes;

    @Column(name = "last_treatment_date")
    private LocalDate lastTreatmentDate;

    @OneToMany(mappedBy = "patientTooth", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ToothHistory> toothHistory = new HashSet<>();
}

