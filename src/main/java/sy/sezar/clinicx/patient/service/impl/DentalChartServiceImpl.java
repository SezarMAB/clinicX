package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.DentalChartDto;
import sy.sezar.clinicx.patient.dto.ToothDto;
import sy.sezar.clinicx.patient.mapper.DentalChartMapper;
import sy.sezar.clinicx.patient.model.PatientTooth;
import sy.sezar.clinicx.patient.model.ToothCondition;
import sy.sezar.clinicx.patient.view.DentalChartView;
import sy.sezar.clinicx.patient.repository.DentalChartViewRepository;
import sy.sezar.clinicx.patient.repository.PatientToothRepository;
import sy.sezar.clinicx.patient.repository.ToothConditionRepository;
import sy.sezar.clinicx.patient.service.DentalChartService;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of DentalChartService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DentalChartServiceImpl implements DentalChartService {

    private final DentalChartViewRepository dentalChartViewRepository;
    private final DentalChartMapper dentalChartMapper;
    private final PatientToothRepository patientToothRepository;
    private final ToothConditionRepository toothConditionRepository;

    @Override
    public DentalChartDto getPatientDentalChart(UUID patientId) {
        log.info("Getting dental chart for patient: {}", patientId);
        
        if (patientId == null) {
            log.error("Patient ID cannot be null for dental chart retrieval");
            throw new IllegalArgumentException("Patient ID cannot be null");
        }

        List<DentalChartView> teeth = dentalChartViewRepository.findByPatientIdOrderByToothNumber(patientId);
        log.info("Retrieved dental chart with {} teeth for patient: {}", teeth.size(), patientId);
        
        if (teeth.size() != 32) {
            log.warn("Expected 32 teeth but found {} for patient: {}", teeth.size(), patientId);
        }
        
        return dentalChartMapper.toDentalChartDtoFromView(teeth);
    }

    @Override
    @Transactional
    public ToothDto updateToothCondition(UUID patientId, Integer toothNumber, UUID conditionId, String notes) {
        log.info("Updating tooth {} condition for patient: {} to condition: {}", 
                toothNumber, patientId, conditionId);
        log.debug("Tooth condition update - Patient: {}, Tooth: {}, Condition: {}, Notes: '{}'", 
                patientId, toothNumber, conditionId, notes);
        
        // Validate input parameters
        if (patientId == null) {
            log.error("Patient ID cannot be null for tooth condition update");
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        if (toothNumber == null || toothNumber < 1 || toothNumber > 32) {
            log.error("Invalid tooth number: {} (must be between 1 and 32)", toothNumber);
            throw new IllegalArgumentException("Tooth number must be between 1 and 32");
        }
        if (conditionId == null) {
            log.error("Condition ID cannot be null for tooth condition update");
            throw new IllegalArgumentException("Condition ID cannot be null");
        }

        PatientTooth patientTooth = patientToothRepository.findByPatientIdAndToothNumber(patientId, toothNumber)
                .orElseThrow(() -> {
                    log.error("Tooth {} not found for patient: {}", toothNumber, patientId);
                    return new NotFoundException("Tooth " + toothNumber + " not found for patient: " + patientId);
                });

        ToothCondition oldCondition = patientTooth.getCurrentCondition();
        log.debug("Current condition for tooth {}: {}", toothNumber, 
                oldCondition != null ? oldCondition.getName() : "None");

        // Find and set the new tooth condition
        ToothCondition newCondition = toothConditionRepository.findById(conditionId)
                .orElseThrow(() -> {
                    log.error("Tooth condition not found with ID: {}", conditionId);
                    return new NotFoundException("Tooth condition not found with ID: " + conditionId);
                });
        
        log.debug("Changing tooth {} condition from '{}' to '{}'", toothNumber,
                oldCondition != null ? oldCondition.getName() : "None", newCondition.getName());

        patientTooth.setCurrentCondition(newCondition);
        
        // Handle notes update
        if (notes != null && !notes.trim().isEmpty()) {
            String trimmedNotes = notes.trim();
            log.debug("Adding notes to tooth {}: '{}'", toothNumber, trimmedNotes);
            patientTooth.setNotes(trimmedNotes);
        } else if (notes != null) {
            log.debug("Clearing notes for tooth {}", toothNumber);
            patientTooth.setNotes(null);
        }

        PatientTooth savedTooth = patientToothRepository.save(patientTooth);
        log.info("Successfully updated tooth {} condition for patient {} from '{}' to '{}'",
                 toothNumber, patientId, 
                 oldCondition != null ? oldCondition.getName() : "None", 
                 newCondition.getName());

        return dentalChartMapper.toToothDto(savedTooth);
    }

    @Override
    public ToothDto getToothDetails(UUID patientId, Integer toothNumber) {
        log.info("Getting tooth {} details for patient: {}", toothNumber, patientId);
        
        // Validate input parameters
        if (patientId == null) {
            log.error("Patient ID cannot be null for tooth details retrieval");
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        if (toothNumber == null || toothNumber < 1 || toothNumber > 32) {
            log.error("Invalid tooth number: {} (must be between 1 and 32)", toothNumber);
            throw new IllegalArgumentException("Tooth number must be between 1 and 32");
        }

        List<DentalChartView> teeth = dentalChartViewRepository.findByPatientIdOrderByToothNumber(patientId);
        log.debug("Retrieved {} teeth records for patient: {}", teeth.size(), patientId);
        
        DentalChartView tooth = teeth.stream()
                .filter(t -> t.getToothNumber().equals(toothNumber))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Tooth {} not found for patient: {} (total teeth found: {})", 
                            toothNumber, patientId, teeth.size());
                    return new NotFoundException("Tooth " + toothNumber + " not found for patient: " + patientId);
                });

        log.debug("Found tooth {} for patient: {} - Condition: {}", 
                toothNumber, patientId, tooth.getConditionName());
        
        return dentalChartMapper.toToothDto(tooth);
    }
}
