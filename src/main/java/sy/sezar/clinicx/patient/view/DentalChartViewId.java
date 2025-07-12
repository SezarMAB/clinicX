package sy.sezar.clinicx.patient.view;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents the composite primary key for the {@link DentalChartView} entity.
 * It consists of the patient's ID and the specific tooth number.
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DentalChartViewId implements Serializable {
    private UUID patientId;
    private Integer toothNumber;
}

