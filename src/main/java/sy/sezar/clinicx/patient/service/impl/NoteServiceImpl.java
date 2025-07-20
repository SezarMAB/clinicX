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
import sy.sezar.clinicx.patient.model.Note;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.repository.NoteRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.service.NoteService;

import java.time.Instant;
import java.util.UUID;

/**
 * Implementation of NoteService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final PatientRepository patientRepository;
    private final NoteSummaryMapper noteMapper;

    @Override
    public Page<NoteSummaryDto> getPatientNotes(UUID patientId, Pageable pageable) {
        log.info("Getting notes for patient: {} with pagination: {}", patientId, pageable);

        Page<Note> notes = noteRepository.findByPatientIdOrderByNoteDateDesc(patientId, pageable);
        log.info("Found {} notes (page {} of {}) for patient: {}",
                notes.getNumberOfElements(), notes.getNumber() + 1, notes.getTotalPages(), patientId);

        return notes.map(noteMapper::toNoteSummaryDto);
    }

    @Override
    @Transactional
    public NoteSummaryDto createNote(UUID patientId, String content) {
        log.info("Creating note for patient: {} (content length: {} characters)", patientId,
                content != null ? content.length() : 0);
        log.debug("Note content preview: {}", content != null && content.length() > 50 ?
                content.substring(0, 50) + "..." : content);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {} during note creation", patientId);
                    return new NotFoundException("Patient not found with ID: " + patientId);
                });

        log.debug("Found patient: {} for note creation", patient.getFullName());

        Note note = new Note();
        note.setPatient(patient);
        note.setContent(content);
        note.setNoteDate(Instant.now());

        Note savedNote = noteRepository.save(note);
        log.info("Successfully created note with ID: {} for patient: {}", savedNote.getId(), patientId);

        return noteMapper.toNoteSummaryDto(savedNote);
    }

    @Override
    public NoteSummaryDto findNoteById(UUID noteId) {
        log.info("Finding note by ID: {}", noteId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    log.error("Note not found with ID: {}", noteId);
                    return new NotFoundException("Note not found with ID: " + noteId);
                });

        log.debug("Found note for patient: {} created on: {}",
                note.getPatient().getId(), note.getNoteDate());

        return noteMapper.toNoteSummaryDto(note);
    }

    @Override
    @Transactional
    public NoteSummaryDto updateNote(UUID noteId, String content) {
        log.info("Updating note with ID: {} (new content length: {} characters)", noteId,
                content != null ? content.length() : 0);
        log.debug("New note content preview: {}", content != null && content.length() > 50 ?
                content.substring(0, 50) + "..." : content);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    log.error("Note not found with ID: {} during update", noteId);
                    return new NotFoundException("Note not found with ID: " + noteId);
                });

        log.debug("Updating note for patient: {} (original length: {} characters)",
                note.getPatient().getId(), note.getContent() != null ? note.getContent().length() : 0);

        note.setContent(content);

        Note updatedNote = noteRepository.save(note);
        log.info("Successfully updated note with ID: {} for patient: {}", noteId, note.getPatient().getId());

        return noteMapper.toNoteSummaryDto(updatedNote);
    }

    @Override
    @Transactional
    public void deleteNote(UUID noteId) {
        log.info("Deleting note with ID: {}", noteId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    log.error("Cannot delete - note not found with ID: {}", noteId);
                    return new NotFoundException("Note not found with ID: " + noteId);
                });

        UUID patientId = note.getPatient().getId();
        log.debug("Deleting note for patient: {} created on: {}", patientId, note.getNoteDate());

        noteRepository.delete(note);
        log.info("Successfully deleted note with ID: {} for patient: {}", noteId, patientId);
    }
}
