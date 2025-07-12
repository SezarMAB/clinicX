package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.patient.dto.PatientBalanceSummaryDto;
import sy.sezar.clinicx.patient.repository.PatientFinancialSummaryViewRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.service.FinancialSummaryService;
import sy.sezar.clinicx.patient.view.PatientFinancialSummaryView;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of FinancialSummaryService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialSummaryServiceImpl implements FinancialSummaryService {

    private final PatientFinancialSummaryViewRepository financialSummaryViewRepository;
    private final PatientRepository patientRepository;

    @Override
    public PatientBalanceSummaryDto getPatientFinancialSummary(UUID patientId) {
        log.debug("Getting financial summary for patient: {}", patientId);

        // TODO: Implement mapping from PatientFinancialSummaryView to PatientBalanceSummaryDto
        throw new UnsupportedOperationException("Financial summary mapping not yet implemented");
    }

    @Override
    public List<PatientFinancialSummaryView> getAllPatientFinancialSummaries() {
        log.debug("Getting all patient financial summaries");

        return financialSummaryViewRepository.findAll();
    }

    @Override
    public List<PatientFinancialSummaryView> getPatientsWithOutstandingBalances() {
        log.debug("Getting patients with outstanding balances");

        // TODO: Implement when repository has method for filtering by balance > 0
        throw new UnsupportedOperationException("Outstanding balances filter not yet implemented");
    }
}
