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
        log.debug("Getting dental chart for patient: {}", patientId);

        List<DentalChartView> teeth = dentalChartViewRepository.findByPatientIdOrderByToothNumber(patientId);
        return dentalChartMapper.toDentalChartDtoFromView(teeth);
    }

    @Override
    @Transactional
    public ToothDto updateToothCondition(UUID patientId, Integer toothNumber, UUID conditionId, String notes) {
        log.info("Updating tooth {} condition for patient: {}", toothNumber, patientId);

        PatientTooth patientTooth = patientToothRepository.findByPatientIdAndToothNumber(patientId, toothNumber)
                .orElseThrow(() -> new NotFoundException("Tooth " + toothNumber + " not found for patient: " + patientId));

        // Find and set the new tooth condition
        ToothCondition newCondition = toothConditionRepository.findById(conditionId)
                .orElseThrow(() -> new NotFoundException("Tooth condition not found with ID: " + conditionId));

        patientTooth.setCurrentCondition(newCondition);
        if (notes != null && !notes.trim().isEmpty()) {
            patientTooth.setNotes(notes);
        }

        PatientTooth savedTooth = patientToothRepository.save(patientTooth);
        log.info("Updated tooth {} condition for patient {} to condition: {}",
                 toothNumber, patientId, newCondition.getName());

        return dentalChartMapper.toToothDto(savedTooth);
    }

    @Override
    public ToothDto getToothDetails(UUID patientId, Integer toothNumber) {
        log.debug("Getting tooth {} details for patient: {}", toothNumber, patientId);

        List<DentalChartView> teeth = dentalChartViewRepository.findByPatientIdOrderByToothNumber(patientId);
        DentalChartView tooth = teeth.stream()
                .filter(t -> t.getToothNumber().equals(toothNumber))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Tooth " + toothNumber + " not found for patient: " + patientId));

        return dentalChartMapper.toToothDto(tooth);
    }
}
