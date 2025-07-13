package sy.sezar.clinicx.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.NoteCreateRequest;
import sy.sezar.clinicx.patient.dto.NoteSummaryDto;
import sy.sezar.clinicx.patient.dto.NoteUpdateRequest;
import sy.sezar.clinicx.patient.service.NoteService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Notes", description = "Operations related to patient note management")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(
        summary = "Create new note",
        description = "Creates a new note for a patient."
    )
    @ApiResponse(responseCode = "201", description = "Note created",
                content = @Content(schema = @Schema(implementation = NoteSummaryDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<NoteSummaryDto> createNote(
            @Valid @RequestBody NoteCreateRequest request) {
        log.info("Creating new note for patient ID: {}", request.patientId());
        // Extract fields from request DTO to match service signature
        NoteSummaryDto note = noteService.createNote(request.patientId(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update note",
        description = "Updates an existing note by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Note updated",
                content = @Content(schema = @Schema(implementation = NoteSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Note not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<NoteSummaryDto> updateNote(
            @Parameter(name = "id", description = "Note UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody NoteUpdateRequest request) {
        log.info("Updating note with ID: {}", id);
        // Extract content from request DTO to match service signature
        NoteSummaryDto note = noteService.updateNote(id, request.content());
        return ResponseEntity.ok(note);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient notes",
        description = "Retrieves paginated list of notes for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Notes retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<NoteSummaryDto>> getPatientNotes(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            Pageable pageable) {
        log.info("Retrieving notes for patient ID: {} with pagination: {}", patientId, pageable);
        Page<NoteSummaryDto> notes = noteService.getPatientNotes(patientId, pageable);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get note by ID",
        description = "Retrieves a specific note by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Note found",
                content = @Content(schema = @Schema(implementation = NoteSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Note not found")
    public ResponseEntity<NoteSummaryDto> getNoteById(
            @Parameter(name = "id", description = "Note UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving note with ID: {}", id);
        NoteSummaryDto note = noteService.findNoteById(id);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete note",
        description = "Deletes a note by its UUID."
    )
    @ApiResponse(responseCode = "204", description = "Note deleted")
    @ApiResponse(responseCode = "404", description = "Note not found")
    public ResponseEntity<Void> deleteNote(
            @Parameter(name = "id", description = "Note UUID", required = true)
            @PathVariable UUID id) {
        log.info("Deleting note with ID: {}", id);
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
