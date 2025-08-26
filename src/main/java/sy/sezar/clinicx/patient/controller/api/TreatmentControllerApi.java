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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;
import sy.sezar.clinicx.patient.dto.TreatmentSearchCriteria;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/treatments")
@Tag(name = "Treatments", description = "Operations related to patient treatment management")
public interface TreatmentControllerApi {

    @PostMapping
    @Operation(
        summary = "Create new treatment",
        description = "Creates a new treatment record for a patient."
    )
    @ApiResponse(responseCode = "201", description = "Visit created",
                content = @Content(schema = @Schema(implementation = TreatmentLogDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient or procedure not found")
    ResponseEntity<TreatmentLogDto> createTreatment(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @RequestParam UUID patientId,
            @Valid @RequestBody TreatmentCreateRequest request);

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient treatment history",
        description = "Retrieves paginated treatment history for a specific patient.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: treatmentDate", example = "treatmentDate")
        }
    )
    @ApiResponse(responseCode = "200", description = "Visit history retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<TreatmentLogDto>> getPatientTreatmentHistory(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "treatmentDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}")
    @Operation(
        summary = "Get treatment by ID",
        description = "Retrieves a specific treatment by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Visit found",
                content = @Content(schema = @Schema(implementation = TreatmentLogDto.class)))
    @ApiResponse(responseCode = "404", description = "Visit not found")
    ResponseEntity<TreatmentLogDto> getTreatmentById(
            @Parameter(name = "id", description = "Visit UUID", required = true)
            @PathVariable UUID id);

    @PutMapping("/{id}")
    @Operation(
        summary = "Update treatment",
        description = "Updates an existing treatment record."
    )
    @ApiResponse(responseCode = "200", description = "Visit updated",
                content = @Content(schema = @Schema(implementation = TreatmentLogDto.class)))
    @ApiResponse(responseCode = "404", description = "Visit not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    ResponseEntity<TreatmentLogDto> updateTreatment(
            @Parameter(name = "id", description = "Visit UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody TreatmentCreateRequest request);

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete treatment",
        description = "Deletes a treatment record by its UUID."
    )
    @ApiResponse(responseCode = "204", description = "Visit deleted")
    @ApiResponse(responseCode = "404", description = "Visit not found")
    ResponseEntity<Void> deleteTreatment(
            @Parameter(name = "id", description = "Visit UUID", required = true)
            @PathVariable UUID id);

    @PostMapping("/search")
    @Operation(
        summary = "Advanced treatment search",
        description = "Search treatments with multiple criteria and filters.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: treatmentDate", example = "treatmentDate")
        }
    )
    @ApiResponse(responseCode = "200", description = "Treatments retrieved")
    ResponseEntity<Page<TreatmentLogDto>> searchTreatments(
            @Valid @RequestBody TreatmentSearchCriteria criteria,
            @Parameter(hidden = true) @PageableDefault(sort = "treatmentDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);
}
