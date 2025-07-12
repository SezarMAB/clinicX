package sy.sezar.clinicx.patient.service;

import sy.sezar.clinicx.patient.dto.PatientBalanceSummaryDto;
import sy.sezar.clinicx.patient.view.PatientFinancialSummaryView;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing patient financial summaries.
 */
public interface FinancialSummaryService {

    /**
     * Gets financial summary for a specific patient.
     */
    PatientBalanceSummaryDto getPatientFinancialSummary(UUID patientId);

    /**
     * Gets financial summaries for all patients (for reports).
     */
    List<PatientFinancialSummaryView> getAllPatientFinancialSummaries();

    /**
     * Gets patients with outstanding balances.
     */
    List<PatientFinancialSummaryView> getPatientsWithOutstandingBalances();
}
