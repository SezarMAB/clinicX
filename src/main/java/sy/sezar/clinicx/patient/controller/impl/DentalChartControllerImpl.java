package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.patient.controller.api.DentalChartControllerApi;
import sy.sezar.clinicx.patient.dto.DentalChartDto;
import sy.sezar.clinicx.patient.dto.ToothDto;
import sy.sezar.clinicx.patient.service.DentalChartService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DentalChartControllerImpl implements DentalChartControllerApi {

    private final DentalChartService dentalChartService;

    @Override
    public ResponseEntity<DentalChartDto> getPatientDentalChart(UUID patientId) {
        log.info("Retrieving dental chart for patient ID: {}", patientId);
        DentalChartDto dentalChart = dentalChartService.getPatientDentalChart(patientId);
        return ResponseEntity.ok(dentalChart);
    }

    @Override
    public ResponseEntity<ToothDto> updateToothCondition(UUID patientId, Integer toothNumber, UUID conditionId, String notes) {
        log.info("Updating tooth {} condition for patient ID: {} with condition ID: {}",
                toothNumber, patientId, conditionId);
        ToothDto tooth = dentalChartService.updateToothCondition(patientId, toothNumber, conditionId, notes);
        return ResponseEntity.ok(tooth);
    }

    @Override
    public ResponseEntity<ToothDto> getToothDetails(UUID patientId, Integer toothNumber) {
        log.info("Retrieving details for tooth {} of patient ID: {}", toothNumber, patientId);
        ToothDto tooth = dentalChartService.getToothDetails(patientId, toothNumber);
        return ResponseEntity.ok(tooth);
    }
}