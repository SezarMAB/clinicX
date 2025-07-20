package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.DentalChartDto;
import sy.sezar.clinicx.patient.dto.ToothDto;

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
                content = @Content(schema = @Schema(implementation = DentalChartDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<DentalChartDto> getPatientDentalChart(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId);

    @PutMapping("/patient/{patientId}/tooth/{toothNumber}")
    @Operation(
        summary = "Update tooth condition",
        description = "Updates the condition and notes for a specific tooth of a patient."
    )
    @ApiResponse(responseCode = "200", description = "Tooth condition updated",
                content = @Content(schema = @Schema(implementation = ToothDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient or tooth not found")
    @ApiResponse(responseCode = "400", description = "Invalid tooth number or condition")
    ResponseEntity<ToothDto> updateToothCondition(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(name = "toothNumber", description = "Tooth number (1-32)", required = true)
            @PathVariable Integer toothNumber,
            @Parameter(name = "conditionId", description = "Tooth condition UUID", required = true)
            @RequestParam UUID conditionId,
            @Parameter(name = "notes", description = "Additional notes about the tooth condition")
            @RequestParam(required = false) String notes);

    @GetMapping("/patient/{patientId}/tooth/{toothNumber}")
    @Operation(
        summary = "Get tooth details",
        description = "Retrieves detailed information for a specific tooth of a patient."
    )
    @ApiResponse(responseCode = "200", description = "Tooth details retrieved",
                content = @Content(schema = @Schema(implementation = ToothDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient or tooth not found")
    ResponseEntity<ToothDto> getToothDetails(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(name = "toothNumber", description = "Tooth number (1-32)", required = true)
            @PathVariable Integer toothNumber);
}