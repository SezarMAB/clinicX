package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.Note;

import java.util.UUID;

/**
 * Repository for managing patient Note entities.
 */
public interface NoteRepository extends JpaRepository<Note, UUID> {

    /**
     * Finds all notes for a specific patient with pagination.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A Page of notes for the given patient.
     */
    Page<Note> findByPatientIdOrderByNoteDateDesc(UUID patientId, Pageable pageable);
}
