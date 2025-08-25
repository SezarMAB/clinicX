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
        log.info("Getting financial summary for patient: {}", patientId);

        if (patientId == null) {
            log.error("Patient ID cannot be null for financial summary retrieval");
            throw new IllegalArgumentException("Patient ID cannot be null");
        }

        // IMPLEMENTED: Mapping from PatientFinancialSummaryView to PatientBalanceSummaryDto
        PatientFinancialSummaryView summaryView = financialSummaryViewRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Financial summary not found for patient: {}", patientId);
                    return new NotFoundException("Financial summary not found for patient: " + patientId);
                });

        log.debug("Retrieved financial summary for patient: {} - Balance: {}, Total Invoiced: {}",
                patientId, summaryView.getBalance(), summaryView.getTotalInvoices());

        PatientBalanceSummaryDto result = financialSummaryMapper.toPatientBalanceSummaryDto(summaryView);
        log.info("Successfully retrieved financial summary for patient: {}", patientId);

        return result;
    }

    @Override
    public List<PatientFinancialSummaryView> getAllPatientFinancialSummaries() {
        log.info("Getting all patient financial summaries");

        List<PatientFinancialSummaryView> summaries = financialSummaryViewRepository.findAll();
        log.info("Retrieved {} patient financial summaries", summaries.size());

        if (log.isDebugEnabled()) {
            long patientsWithBalance = summaries.stream()
                    .filter(s -> s.getBalance() != null && s.getBalance().compareTo(java.math.BigDecimal.ZERO) > 0)
                    .count();
            log.debug("Financial summaries breakdown - Total: {}, With outstanding balance: {}",
                    summaries.size(), patientsWithBalance);
        }

        return summaries;
    }

    @Override
    public List<PatientFinancialSummaryView> getPatientsWithOutstandingBalances() {
        log.info("Getting patients with outstanding balances");

        try {
            // IMPLEMENTED: Use repository method for filtering by balance > 0
            List<PatientFinancialSummaryView> patientsWithBalance = financialSummaryViewRepository.findPatientsWithOutstandingBalances();

            log.info("Found {} patients with outstanding balances", patientsWithBalance.size());

            if (log.isDebugEnabled() && !patientsWithBalance.isEmpty()) {
                java.math.BigDecimal totalOutstanding = patientsWithBalance.stream()
                        .map(PatientFinancialSummaryView::getBalance)
                        .filter(balance -> balance != null)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                log.debug("Total outstanding balance across all patients: {}", totalOutstanding);
            }

            return patientsWithBalance;
        } catch (Exception e) {
            log.error("Error retrieving patients with outstanding balances", e);
            throw e;
        }
    }
}
