package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.LabRequest;

import java.util.UUID;

/**
 * Repository for managing LabRequest entities.
 */
public interface LabRequestRepository extends JpaRepository<LabRequest, UUID> {

    /**
     * Finds all lab requests for a specific patient with pagination.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A Page of lab requests for the given patient.
     */
    Page<LabRequest> findByPatientIdOrderByDateSentDesc(UUID patientId, Pageable pageable);
}
