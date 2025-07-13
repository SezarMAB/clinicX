package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.TreatmentControllerApi;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;
import sy.sezar.clinicx.patient.dto.TreatmentSearchCriteria;
import sy.sezar.clinicx.patient.service.TreatmentService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class TreatmentControllerImpl implements TreatmentControllerApi {

    private final TreatmentService treatmentService;

    @Override
    public ResponseEntity<TreatmentLogDto> createTreatment(UUID patientId, TreatmentCreateRequest request) {
        log.info("Creating new treatment for patient ID: {} (procedure: {})", patientId, request.procedureId());
        log.debug("Treatment creation request validation: {}", request);

        try {
            TreatmentLogDto treatment = treatmentService.createTreatment(patientId, request);
            log.info("Successfully created treatment with ID: {} for patient: {} - Status: 201 CREATED",
                    treatment.treatmentId(), patientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(treatment);
        } catch (Exception e) {
            log.error("Failed to create treatment for patient: {} - Error: {}", patientId, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<TreatmentLogDto>> getPatientTreatmentHistory(UUID patientId, Pageable pageable) {
        log.info("Retrieving treatment history for patient ID: {} with pagination: {}", patientId, pageable);
        Page<TreatmentLogDto> treatments = treatmentService.getPatientTreatmentHistory(patientId, pageable);
        return ResponseEntity.ok(treatments);
    }

    @Override
    public ResponseEntity<TreatmentLogDto> getTreatmentById(UUID id) {
        log.info("Retrieving treatment with ID: {}", id);
        TreatmentLogDto treatment = treatmentService.findTreatmentById(id);
        return ResponseEntity.ok(treatment);
    }

    @Override
    public ResponseEntity<TreatmentLogDto> updateTreatment(UUID id, TreatmentCreateRequest request) {
        log.info("Updating treatment with ID: {}", id);
        log.debug("Treatment update request validation: {}", request);

        try {
            TreatmentLogDto treatment = treatmentService.updateTreatment(id, request);
            log.info("Successfully updated treatment with ID: {} - Status: 200 OK", id);
            return ResponseEntity.ok(treatment);
        } catch (Exception e) {
            log.error("Failed to update treatment with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Void> deleteTreatment(UUID id) {
        log.info("Deleting treatment with ID: {}", id);

        try {
            treatmentService.deleteTreatment(id);
            log.info("Successfully deleted treatment with ID: {} - Status: 204 NO CONTENT", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete treatment with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<TreatmentLogDto>> searchTreatments(TreatmentSearchCriteria criteria, Pageable pageable) {
        log.info("Advanced search for treatments with criteria: {}", criteria);
        Page<TreatmentLogDto> treatments = treatmentService.searchTreatments(criteria, pageable);
        return ResponseEntity.ok(treatments);
    }
}
