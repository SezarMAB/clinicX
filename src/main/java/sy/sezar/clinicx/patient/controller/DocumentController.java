package sy.sezar.clinicx.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.DocumentSummaryDto;
import sy.sezar.clinicx.patient.service.DocumentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Documents", description = "Operations related to patient document management")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient documents",
        description = "Retrieves paginated list of documents for a specific patient.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: createdAt", example = "createdAt")
        }
    )
    @ApiResponse(responseCode = "200", description = "Documents retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<DocumentSummaryDto>> getPatientDocuments(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Retrieving documents for patient ID: {} with pagination: {}", patientId, pageable);
        Page<DocumentSummaryDto> documents = documentService.getPatientDocuments(patientId, pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get document by ID",
        description = "Retrieves a specific document by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Document found",
                content = @Content(schema = @Schema(implementation = DocumentSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<DocumentSummaryDto> getDocumentById(
            @Parameter(name = "id", description = "Document UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving document with ID: {}", id);
        DocumentSummaryDto document = documentService.findDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete document",
        description = "Deletes a document by its UUID."
    )
    @ApiResponse(responseCode = "204", description = "Document deleted")
    @ApiResponse(responseCode = "404", description = "Document not found")
    public ResponseEntity<Void> deleteDocument(
            @Parameter(name = "id", description = "Document UUID", required = true)
            @PathVariable UUID id) {
        log.info("Deleting document with ID: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
