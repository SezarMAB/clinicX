package sy.sezar.clinicx.patient.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientToothId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "tooth_number")
    private Integer toothNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientToothId that = (PatientToothId) o;
        return Objects.equals(patientId, that.patientId) &&
               Objects.equals(toothNumber, that.toothNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, toothNumber);
    }
}

