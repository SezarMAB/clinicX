package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.DocumentSummaryDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "Operations related to patient document management")
public interface DocumentControllerApi {

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
    ResponseEntity<Page<DocumentSummaryDto>> getPatientDocuments(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}")
    @Operation(
        summary = "Get document by ID",
        description = "Retrieves a specific document by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Document found",
                content = @Content(schema = @Schema(implementation = DocumentSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Document not found")
    ResponseEntity<DocumentSummaryDto> getDocumentById(
            @Parameter(name = "id", description = "Document UUID", required = true)
            @PathVariable UUID id);

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete document",
        description = "Deletes a document by its UUID."
    )
    @ApiResponse(responseCode = "204", description = "Document deleted")
    @ApiResponse(responseCode = "404", description = "Document not found")
    ResponseEntity<Void> deleteDocument(
            @Parameter(name = "id", description = "Document UUID", required = true)
            @PathVariable UUID id);
}