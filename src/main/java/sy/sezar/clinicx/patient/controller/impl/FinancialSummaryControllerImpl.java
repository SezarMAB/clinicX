package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.patient.controller.api.FinancialSummaryControllerApi;
import sy.sezar.clinicx.patient.dto.PatientBalanceSummaryDto;
import sy.sezar.clinicx.patient.view.PatientFinancialSummaryView;
import sy.sezar.clinicx.patient.service.FinancialSummaryService;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinancialSummaryControllerImpl implements FinancialSummaryControllerApi {

    private final FinancialSummaryService financialSummaryService;

    @Override
    public ResponseEntity<PatientBalanceSummaryDto> getPatientFinancialSummary(UUID patientId) {
        log.info("Retrieving financial summary for patient ID: {}", patientId);
        PatientBalanceSummaryDto summary = financialSummaryService.getPatientFinancialSummary(patientId);
        return ResponseEntity.ok(summary);
    }

    @Override
    public ResponseEntity<List<PatientFinancialSummaryView>> getAllPatientFinancialSummaries() {
        log.info("Retrieving financial summaries for all patients");
        List<PatientFinancialSummaryView> summaries = financialSummaryService.getAllPatientFinancialSummaries();
        return ResponseEntity.ok(summaries);
    }

    @Override
    public ResponseEntity<List<PatientFinancialSummaryView>> getPatientsWithOutstandingBalances() {
        log.info("Retrieving patients with outstanding balances");
        List<PatientFinancialSummaryView> summaries = financialSummaryService.getPatientsWithOutstandingBalances();
        return ResponseEntity.ok(summaries);
    }
}