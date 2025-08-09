package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.ChartDataDto;
import sy.sezar.clinicx.patient.dto.ChartToothDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dental-charts")
@Tag(name = "Dental Charts", description = "Operations related to dental chart and tooth condition management")
public interface DentalChartControllerApi {

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient dental chart",
        description = "Retrieves the complete dental chart showing all tooth conditions for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Dental chart retrieved",
                content = @Content(schema = @Schema(implementation = ChartDataDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<ChartDataDto> getPatientDentalChart(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId);

    @PutMapping("/patient/{patientId}/tooth/{toothId}")
    @Operation(
        summary = "Update tooth condition",
        description = "Updates the condition and notes for a specific tooth of a patient."
    )
    @ApiResponse(responseCode = "200", description = "Tooth condition updated",
                content = @Content(schema = @Schema(implementation = ChartToothDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient or tooth not found")
    @ApiResponse(responseCode = "400", description = "Invalid tooth ID or condition")
    ResponseEntity<ChartToothDto> updateToothCondition(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(name = "toothId", description = "Tooth ID in FDI notation (11-48)", required = true)
            @PathVariable String toothId,
            @Parameter(name = "toothData", description = "Updated tooth data", required = true)
            @RequestBody ChartToothDto toothData);

    @GetMapping("/patient/{patientId}/tooth/{toothId}")
    @Operation(
        summary = "Get tooth details",
        description = "Retrieves detailed information for a specific tooth of a patient."
    )
    @ApiResponse(responseCode = "200", description = "Tooth details retrieved",
                content = @Content(schema = @Schema(implementation = ChartToothDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient or tooth not found")
    ResponseEntity<ChartToothDto> getToothDetails(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(name = "toothId", description = "Tooth ID in FDI notation (11-48)", required = true)
            @PathVariable String toothId);

    @PutMapping("/patient/{patientId}/tooth/{toothId}/surface/{surfaceName}")
    @Operation(
        summary = "Update tooth surface condition",
        description = "Updates the condition for a specific surface of a tooth."
    )
    @ApiResponse(responseCode = "200", description = "Surface condition updated")
    @ApiResponse(responseCode = "404", description = "Patient, tooth or surface not found")
    ResponseEntity<Void> updateSurfaceCondition(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(name = "toothId", description = "Tooth ID in FDI notation (11-48)", required = true)
            @PathVariable String toothId,
            @Parameter(name = "surfaceName", description = "Surface name (mesial, distal, occlusal, buccal, lingual, incisal, cervical, root)", required = true)
            @PathVariable String surfaceName,
            @Parameter(name = "condition", description = "Surface condition", required = true)
            @RequestParam String condition,
            @Parameter(name = "notes", description = "Additional notes")
            @RequestParam(required = false) String notes);

    @PostMapping("/patient/{patientId}/initialize")
    @Operation(
        summary = "Initialize dental chart",
        description = "Creates a new dental chart with all teeth set to healthy condition."
    )
    @ApiResponse(responseCode = "201", description = "Dental chart initialized")
    @ApiResponse(responseCode = "409", description = "Dental chart already exists")
    ResponseEntity<ChartDataDto> initializeDentalChart(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId);
}