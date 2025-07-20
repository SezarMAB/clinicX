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
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialSearchCriteria;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/treatment-materials")
@Tag(name = "Treatment Materials", description = "Operations related to treatment material management")
public interface TreatmentMaterialControllerApi {

    @PostMapping
    @Operation(
        summary = "Create treatment material record",
        description = "Creates a new treatment material record."
    )
    @ApiResponse(responseCode = "201", description = "Treatment material created",
                content = @Content(schema = @Schema(implementation = TreatmentMaterialDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Treatment not found")
    ResponseEntity<TreatmentMaterialDto> createTreatmentMaterial(
            @Valid @RequestBody TreatmentMaterialCreateRequest request);

    @GetMapping("/{id}")
    @Operation(
        summary = "Get treatment material by ID",
        description = "Retrieves a specific treatment material by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Treatment material found",
                content = @Content(schema = @Schema(implementation = TreatmentMaterialDto.class)))
    @ApiResponse(responseCode = "404", description = "Treatment material not found")
    ResponseEntity<TreatmentMaterialDto> getTreatmentMaterial(
            @Parameter(description = "Treatment material ID") @PathVariable UUID id);

    @GetMapping("/treatment/{treatmentId}")
    @Operation(
        summary = "Get materials by treatment ID",
        description = "Retrieves all materials used in a specific treatment."
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    ResponseEntity<List<TreatmentMaterialDto>> getMaterialsByTreatment(
            @Parameter(description = "Treatment ID") @PathVariable UUID treatmentId);

    @GetMapping("/treatment/{treatmentId}/paged")
    @Operation(
        summary = "Get materials by treatment ID (paginated)",
        description = "Retrieves materials used in a specific treatment with pagination.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: createdAt", example = "createdAt")
        }
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    ResponseEntity<Page<TreatmentMaterialDto>> getMaterialsByTreatmentPaged(
            @Parameter(description = "Treatment ID") @PathVariable UUID treatmentId,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get materials by patient ID",
        description = "Retrieves all materials used for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    ResponseEntity<List<TreatmentMaterialDto>> getMaterialsByPatient(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId);

    @GetMapping("/patient/{patientId}/paged")
    @Operation(
        summary = "Get materials by patient ID (paginated)",
        description = "Retrieves materials used for a specific patient with pagination.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: createdAt", example = "createdAt")
        }
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    ResponseEntity<Page<TreatmentMaterialDto>> getMaterialsByPatientPaged(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);

    @PutMapping("/{id}")
    @Operation(
        summary = "Update treatment material",
        description = "Updates an existing treatment material record."
    )
    @ApiResponse(responseCode = "200", description = "Treatment material updated",
                content = @Content(schema = @Schema(implementation = TreatmentMaterialDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Treatment material not found")
    ResponseEntity<TreatmentMaterialDto> updateTreatmentMaterial(
            @Parameter(description = "Treatment material ID") @PathVariable UUID id,
            @Valid @RequestBody TreatmentMaterialCreateRequest request);

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete treatment material",
        description = "Deletes a treatment material record."
    )
    @ApiResponse(responseCode = "204", description = "Treatment material deleted")
    @ApiResponse(responseCode = "404", description = "Treatment material not found")
    ResponseEntity<Void> deleteTreatmentMaterial(
            @Parameter(description = "Treatment material ID") @PathVariable UUID id);

    @GetMapping("/treatment/{treatmentId}/total-cost")
    @Operation(
        summary = "Get total material cost for treatment",
        description = "Calculates the total cost of materials used in a treatment."
    )
    @ApiResponse(responseCode = "200", description = "Total cost calculated")
    ResponseEntity<BigDecimal> getTotalMaterialCostByTreatment(
            @Parameter(description = "Treatment ID") @PathVariable UUID treatmentId);

    @GetMapping("/patient/{patientId}/total-cost")
    @Operation(
        summary = "Get total material cost for patient",
        description = "Calculates the total cost of materials used for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Total cost calculated")
    ResponseEntity<BigDecimal> getTotalMaterialCostByPatient(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId);

    @PostMapping("/search")
    @Operation(
        summary = "Advanced material search",
        description = "Search treatment materials with multiple criteria and filters.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: createdAt", example = "createdAt")
        }
    )
    @ApiResponse(responseCode = "200", description = "Materials retrieved")
    ResponseEntity<Page<TreatmentMaterialDto>> searchMaterials(
            @Valid @RequestBody TreatmentMaterialSearchCriteria criteria,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);
}