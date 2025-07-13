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
        log.debug("Getting notes for patient: {}", patientId);

        Page<Note> notes = noteRepository.findByPatientIdOrderByNoteDateDesc(patientId, pageable);
        return notes.map(noteMapper::toNoteSummaryDto);
    }

    @Override
    @Transactional
    public NoteSummaryDto createNote(UUID patientId, String content) {
        log.info("Creating note for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        Note note = new Note();
        note.setPatient(patient);
        note.setContent(content);
        note.setNoteDate(Instant.now());

        Note savedNote = noteRepository.save(note);
        return noteMapper.toNoteSummaryDto(savedNote);
    }

    @Override
    public NoteSummaryDto findNoteById(UUID noteId) {
        log.debug("Finding note by ID: {}", noteId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found with ID: " + noteId));
        return noteMapper.toNoteSummaryDto(note);
    }

    @Override
    @Transactional
    public NoteSummaryDto updateNote(UUID noteId, String content) {
        log.info("Updating note with ID: {}", noteId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found with ID: " + noteId));
        note.setContent(content);

        Note updatedNote = noteRepository.save(note);
        return noteMapper.toNoteSummaryDto(updatedNote);
    }

    @Override
    @Transactional
    public void deleteNote(UUID noteId) {
        log.info("Deleting note with ID: {}", noteId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found with ID: " + noteId));
        noteRepository.delete(note);
    }
}
