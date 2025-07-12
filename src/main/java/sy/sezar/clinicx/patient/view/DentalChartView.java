package sy.sezar.clinicx.patient.view;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a read-only view of a patient's dental chart (v_dental_chart).
 * This entity provides a flattened view of each tooth's current condition,
 * including details from the tooth_conditions table. It is intended for display
 * and reporting purposes and should not be modified.
 */
@Entity
@Table(name = "v_dental_chart")
@Immutable
@Getter
@IdClass(DentalChartViewId.class)
public class DentalChartView {

    @Id
    @Column(name = "patient_id", insertable = false, updatable = false)
    private UUID patientId;

    @Id
    @Column(name = "tooth_number", insertable = false, updatable = false)
    private Integer toothNumber;

    @Column(name = "condition_code", insertable = false, updatable = false)
    private String conditionCode;

    @Column(name = "condition_name", insertable = false, updatable = false)
    private String conditionName;

    @Column(name = "color_hex", insertable = false, updatable = false)
    private String colorHex;

    @Column(name = "notes", insertable = false, updatable = false)
    private String notes;

    @Column(name = "last_treatment_date", insertable = false, updatable = false)
    private LocalDate lastTreatmentDate;
}

