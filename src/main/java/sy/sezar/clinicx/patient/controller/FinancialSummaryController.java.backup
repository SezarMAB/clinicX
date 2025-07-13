package sy.sezar.clinicx.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.PatientBalanceSummaryDto;
import sy.sezar.clinicx.patient.view.PatientFinancialSummaryView;
import sy.sezar.clinicx.patient.service.FinancialSummaryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial-summaries")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Financial Summaries", description = "Operations related to patient financial summary management")
public class FinancialSummaryController {

    private final FinancialSummaryService financialSummaryService;

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient financial summary",
        description = "Retrieves financial summary for a patient including balance information."
    )
    @ApiResponse(responseCode = "200", description = "Financial summary retrieved",
                content = @Content(schema = @Schema(implementation = PatientBalanceSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<PatientBalanceSummaryDto> getPatientFinancialSummary(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId) {
        log.info("Retrieving financial summary for patient ID: {}", patientId);
        PatientBalanceSummaryDto summary = financialSummaryService.getPatientFinancialSummary(patientId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/all")
    @Operation(
        summary = "Get all patient financial summaries",
        description = "Retrieves financial summaries for all patients (for reports)."
    )
    @ApiResponse(responseCode = "200", description = "Financial summaries retrieved")
    public ResponseEntity<List<PatientFinancialSummaryView>> getAllPatientFinancialSummaries() {
        log.info("Retrieving financial summaries for all patients");
        List<PatientFinancialSummaryView> summaries = financialSummaryService.getAllPatientFinancialSummaries();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/outstanding-balances")
    @Operation(
        summary = "Get patients with outstanding balances",
        description = "Retrieves financial summaries for patients with outstanding balances."
    )
    @ApiResponse(responseCode = "200", description = "Outstanding balances retrieved")
    public ResponseEntity<List<PatientFinancialSummaryView>> getPatientsWithOutstandingBalances() {
        log.info("Retrieving patients with outstanding balances");
        List<PatientFinancialSummaryView> summaries = financialSummaryService.getPatientsWithOutstandingBalances();
        return ResponseEntity.ok(summaries);
    }
}
