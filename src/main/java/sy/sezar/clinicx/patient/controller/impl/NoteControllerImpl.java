package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.NoteControllerApi;
import sy.sezar.clinicx.patient.dto.NoteCreateRequest;
import sy.sezar.clinicx.patient.dto.NoteSummaryDto;
import sy.sezar.clinicx.patient.dto.NoteUpdateRequest;
import sy.sezar.clinicx.patient.service.NoteService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class NoteControllerImpl implements NoteControllerApi {

    private final NoteService noteService;

    @Override
    public ResponseEntity<NoteSummaryDto> createNote(NoteCreateRequest request) {
        log.info("Creating new note for patient ID: {}", request.patientId());
        // Extract fields from request DTO to match service signature
        NoteSummaryDto note = noteService.createNote(request.patientId(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @Override
    public ResponseEntity<NoteSummaryDto> updateNote(UUID id, NoteUpdateRequest request) {
        log.info("Updating note with ID: {}", id);
        // Extract content from request DTO to match service signature
        NoteSummaryDto note = noteService.updateNote(id, request.content());
        return ResponseEntity.ok(note);
    }

    @Override
    public ResponseEntity<Page<NoteSummaryDto>> getPatientNotes(UUID patientId, Pageable pageable) {
        log.info("Retrieving notes for patient ID: {} with pagination: {}", patientId, pageable);
        Page<NoteSummaryDto> notes = noteService.getPatientNotes(patientId, pageable);
        return ResponseEntity.ok(notes);
    }

    @Override
    public ResponseEntity<NoteSummaryDto> getNoteById(UUID id) {
        log.info("Retrieving note with ID: {}", id);
        NoteSummaryDto note = noteService.findNoteById(id);
        return ResponseEntity.ok(note);
    }

    @Override
    public ResponseEntity<Void> deleteNote(UUID id) {
        log.info("Deleting note with ID: {}", id);
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}