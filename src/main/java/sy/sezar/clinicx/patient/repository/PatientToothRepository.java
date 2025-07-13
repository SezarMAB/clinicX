package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.patient.model.PatientTooth;
import sy.sezar.clinicx.patient.model.PatientToothId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing PatientTooth entities.
 */
@Repository
public interface PatientToothRepository extends JpaRepository<PatientTooth, PatientToothId> {

    /**
     * Finds all teeth for a specific patient.
     *
     * @param patientId The UUID of the patient.
     * @return A list of patient teeth.
     */
    List<PatientTooth> findByPatientId(UUID patientId);

    /**
     * Finds a specific tooth for a patient.
     *
     * @param patientId   The UUID of the patient.
     * @param toothNumber The tooth number.
     * @return Optional PatientTooth if found.
     */
    Optional<PatientTooth> findByPatientIdAndToothNumber(UUID patientId, Integer toothNumber);

    /**
     * Deletes all teeth for a specific patient.
     *
     * @param patientId The UUID of the patient.
     */
    void deleteByPatientId(UUID patientId);
}
