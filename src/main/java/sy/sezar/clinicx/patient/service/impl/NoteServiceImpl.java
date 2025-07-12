package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.NoteSummaryDto;
import sy.sezar.clinicx.patient.mapper.NoteSummaryMapper;
import sy.sezar.clinicx.patient.service.NoteService;

import java.util.UUID;

/**
 * Implementation of NoteService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteServiceImpl implements NoteService {

    private final NoteSummaryMapper noteMapper;
    // TODO: Inject NoteRepository when available

    @Override
    public Page<NoteSummaryDto> getPatientNotes(UUID patientId, Pageable pageable) {
        log.debug("Getting notes for patient: {}", patientId);

        // TODO: Implement when NoteRepository is available
        throw new UnsupportedOperationException("Note repository not yet implemented");
    }

    @Override
    @Transactional
    public NoteSummaryDto createNote(UUID patientId, String content) {
        log.info("Creating note for patient: {}", patientId);

        // TODO: Implement when NoteRepository is available
        throw new UnsupportedOperationException("Note creation not yet implemented");
    }

    @Override
    public NoteSummaryDto findNoteById(UUID noteId) {
        log.debug("Finding note by ID: {}", noteId);

        // TODO: Implement when NoteRepository is available
        throw new UnsupportedOperationException("Note repository not yet implemented");
    }

    @Override
    @Transactional
    public NoteSummaryDto updateNote(UUID noteId, String content) {
        log.info("Updating note with ID: {}", noteId);

        // TODO: Implement when NoteRepository is available
        throw new UnsupportedOperationException("Note update not yet implemented");
    }

    @Override
    @Transactional
    public void deleteNote(UUID noteId) {
        log.info("Deleting note with ID: {}", noteId);

        // TODO: Implement when NoteRepository is available
        throw new UnsupportedOperationException("Note deletion not yet implemented");
    }
}
