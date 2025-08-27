package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sy.sezar.clinicx.patient.model.Visit;

import java.util.List;
import java.util.UUID;

/**
 * Repository for accessing patient Visit records.
 */
public interface VisitRepository extends JpaRepository<Visit, UUID>, JpaSpecificationExecutor<Visit> {

    /**
     * Finds a paginated list of visits for a specific patient, ordered by date.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A Page of visits.
     */
    Page<Visit> findByPatientIdOrderByVisitDateDesc(UUID patientId, Pageable pageable);

    /**
     * Finds a list of visits for a specific patient.
     *
     * @param patientId The UUID of the patient.
     * @return A list of visits.
     */
    List<Visit> findByPatientId(UUID patientId);
}
