package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.NoteSummaryDto;

import java.util.UUID;

/**
 * Service interface for managing patient notes.
 */
public interface NoteService {

    /**
     * Gets all notes for a patient with pagination.
     */
    Page<NoteSummaryDto> getPatientNotes(UUID patientId, Pageable pageable);

    /**
     * Creates a new note for a patient.
     */
    NoteSummaryDto createNote(UUID patientId, String content);

    /**
     * Finds a note by ID.
     */
    NoteSummaryDto findNoteById(UUID noteId);

    /**
     * Updates a note's content.
     */
    NoteSummaryDto updateNote(UUID noteId, String content);

    /**
     * Deletes a note.
     */
    void deleteNote(UUID noteId);
}
