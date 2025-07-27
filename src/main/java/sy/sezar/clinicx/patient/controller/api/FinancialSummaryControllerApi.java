package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.PatientBalanceSummaryDto;
import sy.sezar.clinicx.patient.view.PatientFinancialSummaryView;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial-summaries")
@Tag(name = "Financial Summaries", description = "Operations related to patient financial summary management")
public interface FinancialSummaryControllerApi {

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient financial summary",
        description = "Retrieves financial summary for a patient including balance information."
    )
    @ApiResponse(responseCode = "200", description = "Financial summary retrieved",
                content = @Content(schema = @Schema(implementation = PatientBalanceSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<PatientBalanceSummaryDto> getPatientFinancialSummary(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId);

    @GetMapping("/all")
    @Operation(
        summary = "Get all patient financial summaries",
        description = "Retrieves financial summaries for all patients (for reports)."
    )
    @ApiResponse(responseCode = "200", description = "Financial summaries retrieved")
    ResponseEntity<List<PatientFinancialSummaryView>> getAllPatientFinancialSummaries();

    @GetMapping("/outstanding-balances")
    @Operation(
        summary = "Get patients with outstanding balances",
        description = "Retrieves financial summaries for patients with outstanding balances."
    )
    @ApiResponse(responseCode = "200", description = "Outstanding balances retrieved")
    ResponseEntity<List<PatientFinancialSummaryView>> getPatientsWithOutstandingBalances();
}