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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;
import sy.sezar.clinicx.patient.dto.TreatmentSearchCriteria;
import sy.sezar.clinicx.patient.service.TreatmentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/treatments")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Treatments", description = "Operations related to patient treatment management")
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping
    @Operation(
        summary = "Create new treatment",
        description = "Creates a new treatment record for a patient."
    )
    @ApiResponse(responseCode = "201", description = "Treatment created",
                content = @Content(schema = @Schema(implementation = TreatmentLogDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient or procedure not found")
    public ResponseEntity<TreatmentLogDto> createTreatment(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @RequestParam UUID patientId,
            @Valid @RequestBody TreatmentCreateRequest request) {
        log.info("Creating new treatment for patient ID: {}", patientId);
        TreatmentLogDto treatment = treatmentService.createTreatment(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(treatment);
    }

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
    @ApiResponse(responseCode = "200", description = "Treatment history retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<TreatmentLogDto>> getPatientTreatmentHistory(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "treatmentDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        log.info("Retrieving treatment history for patient ID: {} with pagination: {}", patientId, pageable);
        Page<TreatmentLogDto> treatments = treatmentService.getPatientTreatmentHistory(patientId, pageable);
        return ResponseEntity.ok(treatments);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get treatment by ID",
        description = "Retrieves a specific treatment by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Treatment found",
                content = @Content(schema = @Schema(implementation = TreatmentLogDto.class)))
    @ApiResponse(responseCode = "404", description = "Treatment not found")
    public ResponseEntity<TreatmentLogDto> getTreatmentById(
            @Parameter(name = "id", description = "Treatment UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving treatment with ID: {}", id);
        TreatmentLogDto treatment = treatmentService.findTreatmentById(id);
        return ResponseEntity.ok(treatment);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update treatment",
        description = "Updates an existing treatment record."
    )
    @ApiResponse(responseCode = "200", description = "Treatment updated",
                content = @Content(schema = @Schema(implementation = TreatmentLogDto.class)))
    @ApiResponse(responseCode = "404", description = "Treatment not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<TreatmentLogDto> updateTreatment(
            @Parameter(name = "id", description = "Treatment UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody TreatmentCreateRequest request) {
        log.info("Updating treatment with ID: {}", id);
        TreatmentLogDto treatment = treatmentService.updateTreatment(id, request);
        return ResponseEntity.ok(treatment);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete treatment",
        description = "Deletes a treatment record by its UUID."
    )
    @ApiResponse(responseCode = "204", description = "Treatment deleted")
    @ApiResponse(responseCode = "404", description = "Treatment not found")
    public ResponseEntity<Void> deleteTreatment(
            @Parameter(name = "id", description = "Treatment UUID", required = true)
            @PathVariable UUID id) {
        log.info("Deleting treatment with ID: {}", id);
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }

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
    public ResponseEntity<Page<TreatmentLogDto>> searchTreatments(
            @Valid @RequestBody TreatmentSearchCriteria criteria,
            @Parameter(hidden = true) @PageableDefault(sort = "treatmentDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        log.info("Advanced search for treatments with criteria: {}", criteria);
        Page<TreatmentLogDto> treatments = treatmentService.searchTreatments(criteria, pageable);
        return ResponseEntity.ok(treatments);
    }
}
