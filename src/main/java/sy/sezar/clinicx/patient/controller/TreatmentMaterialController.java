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
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialSearchCriteria;
import sy.sezar.clinicx.patient.service.TreatmentMaterialService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/treatment-materials")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Treatment Materials", description = "Operations related to treatment material management")
public class TreatmentMaterialController {

    private final TreatmentMaterialService treatmentMaterialService;

    @PostMapping
    @Operation(
        summary = "Create treatment material record",
        description = "Creates a new treatment material record."
    )
    @ApiResponse(responseCode = "201", description = "Treatment material created",
                content = @Content(schema = @Schema(implementation = TreatmentMaterialDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Treatment not found")
    public ResponseEntity<TreatmentMaterialDto> createTreatmentMaterial(
            @Valid @RequestBody TreatmentMaterialCreateRequest request) {
        log.info("Creating new treatment material for treatment ID: {}", request.treatmentId());
        TreatmentMaterialDto material = treatmentMaterialService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(material);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get treatment material by ID",
        description = "Retrieves a specific treatment material by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Treatment material found",
                content = @Content(schema = @Schema(implementation = TreatmentMaterialDto.class)))
    @ApiResponse(responseCode = "404", description = "Treatment material not found")
    public ResponseEntity<TreatmentMaterialDto> getTreatmentMaterial(
            @Parameter(description = "Treatment material ID") @PathVariable UUID id) {
        log.info("Retrieving treatment material with ID: {}", id);
        TreatmentMaterialDto material = treatmentMaterialService.findById(id);
        return ResponseEntity.ok(material);
    }

    @GetMapping("/treatment/{treatmentId}")
    @Operation(
        summary = "Get materials by treatment ID",
        description = "Retrieves all materials used in a specific treatment."
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    public ResponseEntity<List<TreatmentMaterialDto>> getMaterialsByTreatment(
            @Parameter(description = "Treatment ID") @PathVariable UUID treatmentId) {
        log.info("Retrieving materials for treatment ID: {}", treatmentId);
        List<TreatmentMaterialDto> materials = treatmentMaterialService.findByTreatmentId(treatmentId);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/treatment/{treatmentId}/paged")
    @Operation(
        summary = "Get materials by treatment ID (paginated)",
        description = "Retrieves materials used in a specific treatment with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    public ResponseEntity<Page<TreatmentMaterialDto>> getMaterialsByTreatmentPaged(
            @Parameter(description = "Treatment ID") @PathVariable UUID treatmentId,
            Pageable pageable) {
        log.info("Retrieving paginated materials for treatment ID: {}", treatmentId);
        Page<TreatmentMaterialDto> materials = treatmentMaterialService.findByTreatmentId(treatmentId, pageable);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get materials by patient ID",
        description = "Retrieves all materials used for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    public ResponseEntity<List<TreatmentMaterialDto>> getMaterialsByPatient(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId) {
        log.info("Retrieving materials for patient ID: {}", patientId);
        List<TreatmentMaterialDto> materials = treatmentMaterialService.findByPatientId(patientId);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/patient/{patientId}/paged")
    @Operation(
        summary = "Get materials by patient ID (paginated)",
        description = "Retrieves materials used for a specific patient with pagination."
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    public ResponseEntity<Page<TreatmentMaterialDto>> getMaterialsByPatientPaged(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            Pageable pageable) {
        log.info("Retrieving paginated materials for patient ID: {}", patientId);
        Page<TreatmentMaterialDto> materials = treatmentMaterialService.findByPatientId(patientId, pageable);
        return ResponseEntity.ok(materials);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update treatment material",
        description = "Updates an existing treatment material record."
    )
    @ApiResponse(responseCode = "200", description = "Treatment material updated",
                content = @Content(schema = @Schema(implementation = TreatmentMaterialDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Treatment material not found")
    public ResponseEntity<TreatmentMaterialDto> updateTreatmentMaterial(
            @Parameter(description = "Treatment material ID") @PathVariable UUID id,
            @Valid @RequestBody TreatmentMaterialCreateRequest request) {
        log.info("Updating treatment material with ID: {}", id);
        TreatmentMaterialDto updated = treatmentMaterialService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete treatment material",
        description = "Deletes a treatment material record."
    )
    @ApiResponse(responseCode = "204", description = "Treatment material deleted")
    @ApiResponse(responseCode = "404", description = "Treatment material not found")
    public ResponseEntity<Void> deleteTreatmentMaterial(
            @Parameter(description = "Treatment material ID") @PathVariable UUID id) {
        log.info("Deleting treatment material with ID: {}", id);
        treatmentMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/treatment/{treatmentId}/total-cost")
    @Operation(
        summary = "Get total material cost for treatment",
        description = "Calculates the total cost of materials used in a treatment."
    )
    @ApiResponse(responseCode = "200", description = "Total cost calculated")
    public ResponseEntity<BigDecimal> getTotalMaterialCostByTreatment(
            @Parameter(description = "Treatment ID") @PathVariable UUID treatmentId) {
        log.info("Calculating total material cost for treatment ID: {}", treatmentId);
        BigDecimal totalCost = treatmentMaterialService.getTotalMaterialCostByTreatmentId(treatmentId);
        return ResponseEntity.ok(totalCost);
    }

    @GetMapping("/patient/{patientId}/total-cost")
    @Operation(
        summary = "Get total material cost for patient",
        description = "Calculates the total cost of materials used for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Total cost calculated")
    public ResponseEntity<BigDecimal> getTotalMaterialCostByPatient(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId) {
        log.info("Calculating total material cost for patient ID: {}", patientId);
        BigDecimal totalCost = treatmentMaterialService.getTotalMaterialCostByPatientId(patientId);
        return ResponseEntity.ok(totalCost);
    }

    @PostMapping("/search")
    @Operation(
        summary = "Advanced material search",
        description = "Search treatment materials with multiple criteria and filters."
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    public ResponseEntity<Page<TreatmentMaterialDto>> searchMaterials(
            @Valid @RequestBody TreatmentMaterialSearchCriteria criteria,
            Pageable pageable) {
        log.info("Advanced search for treatment materials with criteria: {}", criteria);
        Page<TreatmentMaterialDto> materials = treatmentMaterialService.searchMaterials(criteria, pageable);
        return ResponseEntity.ok(materials);
    }
}