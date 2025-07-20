package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.Document;

import java.util.UUID;

/**
 * Repository for managing patient Document entities.
 */
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    /**
     * Finds all documents associated with a specific patient.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A Page of documents.
     */
    Page<Document> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable);
}
