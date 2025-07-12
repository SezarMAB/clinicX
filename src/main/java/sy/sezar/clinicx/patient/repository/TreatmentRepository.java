package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.Treatment;

import java.util.UUID;

/**
 * Repository for accessing patient Treatment records.
 */
public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {

    /**
     * Finds a paginated list of treatments for a specific patient, ordered by date.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A Page of treatments.
     */
    Page<Treatment> findByPatientIdOrderByTreatmentDateDesc(UUID patientId, Pageable pageable);
}

