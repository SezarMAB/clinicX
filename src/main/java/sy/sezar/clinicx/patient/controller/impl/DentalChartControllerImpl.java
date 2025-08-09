package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.patient.controller.api.DentalChartControllerApi;
import sy.sezar.clinicx.patient.dto.ChartDataDto;
import sy.sezar.clinicx.patient.dto.ChartToothDto;
import sy.sezar.clinicx.patient.service.DentalChartService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DentalChartControllerImpl implements DentalChartControllerApi {

    private final DentalChartService dentalChartService;

    @Override
    public ResponseEntity<ChartDataDto> getPatientDentalChart(UUID patientId) {
        log.info("Retrieving dental chart for patient ID: {}", patientId);
        ChartDataDto dentalChart = dentalChartService.getPatientDentalChart(patientId);
        return ResponseEntity.ok(dentalChart);
    }

    @Override
    public ResponseEntity<ChartToothDto> updateToothCondition(UUID patientId, String toothId, ChartToothDto toothData) {
        log.info("Updating tooth {} condition for patient ID: {}", toothId, patientId);
        ChartToothDto updatedTooth = dentalChartService.updateToothCondition(patientId, toothId, toothData);
        return ResponseEntity.ok(updatedTooth);
    }

    @Override
    public ResponseEntity<ChartToothDto> getToothDetails(UUID patientId, String toothId) {
        log.info("Retrieving details for tooth {} of patient ID: {}", toothId, patientId);
        ChartToothDto tooth = dentalChartService.getToothDetails(patientId, toothId);
        return ResponseEntity.ok(tooth);
    }

    @Override
    public ResponseEntity<Void> updateSurfaceCondition(UUID patientId, String toothId, String surfaceName, 
                                                       String condition, String notes) {
        log.info("Updating surface {} of tooth {} for patient ID: {} with condition: {}", 
                surfaceName, toothId, patientId, condition);
        dentalChartService.updateSurfaceCondition(patientId, toothId, surfaceName, condition, notes);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ChartDataDto> initializeDentalChart(UUID patientId) {
        log.info("Initializing dental chart for patient ID: {}", patientId);
        ChartDataDto dentalChart = dentalChartService.initializeDentalChart(patientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dentalChart);
    }
}