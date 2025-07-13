package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.NoteCreateRequest;
import sy.sezar.clinicx.patient.dto.NoteSummaryDto;
import sy.sezar.clinicx.patient.dto.NoteUpdateRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Notes", description = "Operations related to patient note management")
public interface NoteControllerApi {

    @PostMapping
    @Operation(
        summary = "Create new note",
        description = "Creates a new note for a patient."
    )
    @ApiResponse(responseCode = "201", description = "Note created",
                content = @Content(schema = @Schema(implementation = NoteSummaryDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<NoteSummaryDto> createNote(
            @Valid @RequestBody NoteCreateRequest request);

    @PutMapping("/{id}")
    @Operation(
        summary = "Update note",
        description = "Updates an existing note by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Note updated",
                content = @Content(schema = @Schema(implementation = NoteSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Note not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    ResponseEntity<NoteSummaryDto> updateNote(
            @Parameter(name = "id", description = "Note UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody NoteUpdateRequest request);

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient notes",
        description = "Retrieves paginated list of notes for a specific patient.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: createdAt", example = "createdAt")
        }
    )
    @ApiResponse(responseCode = "200", description = "Notes retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<NoteSummaryDto>> getPatientNotes(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}")
    @Operation(
        summary = "Get note by ID",
        description = "Retrieves a specific note by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Note found",
                content = @Content(schema = @Schema(implementation = NoteSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Note not found")
    ResponseEntity<NoteSummaryDto> getNoteById(
            @Parameter(name = "id", description = "Note UUID", required = true)
            @PathVariable UUID id);

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete note",
        description = "Deletes a note by its UUID."
    )
    @ApiResponse(responseCode = "204", description = "Note deleted")
    @ApiResponse(responseCode = "404", description = "Note not found")
    ResponseEntity<Void> deleteNote(
            @Parameter(name = "id", description = "Note UUID", required = true)
            @PathVariable UUID id);
}