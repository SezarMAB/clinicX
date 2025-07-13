package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.PatientBalanceSummaryDto;
import sy.sezar.clinicx.patient.mapper.PatientFinancialSummaryMapper;
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
    private final PatientFinancialSummaryMapper financialSummaryMapper;

    @Override
    public PatientBalanceSummaryDto getPatientFinancialSummary(UUID patientId) {
        log.debug("Getting financial summary for patient: {}", patientId);

        // IMPLEMENTED: Mapping from PatientFinancialSummaryView to PatientBalanceSummaryDto
        PatientFinancialSummaryView summaryView = financialSummaryViewRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Financial summary not found for patient: " + patientId));

        return financialSummaryMapper.toPatientBalanceSummaryDto(summaryView);
    }

    @Override
    public List<PatientFinancialSummaryView> getAllPatientFinancialSummaries() {
        log.debug("Getting all patient financial summaries");

        return financialSummaryViewRepository.findAll();
    }

    @Override
    public List<PatientFinancialSummaryView> getPatientsWithOutstandingBalances() {
        log.debug("Getting patients with outstanding balances");

        // IMPLEMENTED: Use repository method for filtering by balance > 0
        return financialSummaryViewRepository.findPatientsWithOutstandingBalances();
    }
}
