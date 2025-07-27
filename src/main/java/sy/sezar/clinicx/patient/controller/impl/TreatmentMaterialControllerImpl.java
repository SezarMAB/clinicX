package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.TreatmentMaterialControllerApi;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialSearchCriteria;
import sy.sezar.clinicx.patient.service.TreatmentMaterialService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class TreatmentMaterialControllerImpl implements TreatmentMaterialControllerApi {

    private final TreatmentMaterialService treatmentMaterialService;

    @Override
    public ResponseEntity<TreatmentMaterialDto> createTreatmentMaterial(TreatmentMaterialCreateRequest request) {
        log.info("Creating new treatment material for treatment ID: {}", request.treatmentId());
        TreatmentMaterialDto material = treatmentMaterialService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(material);
    }

    @Override
    public ResponseEntity<TreatmentMaterialDto> getTreatmentMaterial(UUID id) {
        log.info("Retrieving treatment material with ID: {}", id);
        TreatmentMaterialDto material = treatmentMaterialService.findById(id);
        return ResponseEntity.ok(material);
    }

    @Override
    public ResponseEntity<List<TreatmentMaterialDto>> getMaterialsByTreatment(UUID treatmentId) {
        log.info("Retrieving materials for treatment ID: {}", treatmentId);
        List<TreatmentMaterialDto> materials = treatmentMaterialService.findByTreatmentId(treatmentId);
        return ResponseEntity.ok(materials);
    }

    @Override
    public ResponseEntity<Page<TreatmentMaterialDto>> getMaterialsByTreatmentPaged(UUID treatmentId, Pageable pageable) {
        log.info("Retrieving paginated materials for treatment ID: {}", treatmentId);
        Page<TreatmentMaterialDto> materials = treatmentMaterialService.findByTreatmentId(treatmentId, pageable);
        return ResponseEntity.ok(materials);
    }

    @Override
    public ResponseEntity<List<TreatmentMaterialDto>> getMaterialsByPatient(UUID patientId) {
        log.info("Retrieving materials for patient ID: {}", patientId);
        List<TreatmentMaterialDto> materials = treatmentMaterialService.findByPatientId(patientId);
        return ResponseEntity.ok(materials);
    }

    @Override
    public ResponseEntity<Page<TreatmentMaterialDto>> getMaterialsByPatientPaged(UUID patientId, Pageable pageable) {
        log.info("Retrieving paginated materials for patient ID: {}", patientId);
        Page<TreatmentMaterialDto> materials = treatmentMaterialService.findByPatientId(patientId, pageable);
        return ResponseEntity.ok(materials);
    }

    @Override
    public ResponseEntity<TreatmentMaterialDto> updateTreatmentMaterial(UUID id, TreatmentMaterialCreateRequest request) {
        log.info("Updating treatment material with ID: {}", id);
        TreatmentMaterialDto updated = treatmentMaterialService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteTreatmentMaterial(UUID id) {
        log.info("Deleting treatment material with ID: {}", id);
        treatmentMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BigDecimal> getTotalMaterialCostByTreatment(UUID treatmentId) {
        log.info("Calculating total material cost for treatment ID: {}", treatmentId);
        BigDecimal totalCost = treatmentMaterialService.getTotalMaterialCostByTreatmentId(treatmentId);
        return ResponseEntity.ok(totalCost);
    }

    @Override
    public ResponseEntity<BigDecimal> getTotalMaterialCostByPatient(UUID patientId) {
        log.info("Calculating total material cost for patient ID: {}", patientId);
        BigDecimal totalCost = treatmentMaterialService.getTotalMaterialCostByPatientId(patientId);
        return ResponseEntity.ok(totalCost);
    }

    @Override
    public ResponseEntity<Page<TreatmentMaterialDto>> searchMaterials(TreatmentMaterialSearchCriteria criteria, Pageable pageable) {
        log.info("Advanced search for treatment materials with criteria: {}", criteria);
        Page<TreatmentMaterialDto> materials = treatmentMaterialService.searchMaterials(criteria, pageable);
        return ResponseEntity.ok(materials);
    }
}