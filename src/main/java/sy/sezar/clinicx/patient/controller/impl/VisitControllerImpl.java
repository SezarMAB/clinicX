package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.VisitControllerApi;
import sy.sezar.clinicx.patient.dto.VisitCreateRequest;
import sy.sezar.clinicx.patient.dto.VisitLogDto;
import sy.sezar.clinicx.patient.dto.VisitSearchCriteria;
import sy.sezar.clinicx.patient.service.TreatmentService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class VisitControllerImpl implements VisitControllerApi {

    private final TreatmentService treatmentService;

    @Override
    public ResponseEntity<VisitLogDto> createTreatment(UUID patientId, VisitCreateRequest request) {
        log.info("Creating new treatment for patient ID: {} (procedure: {})", patientId, request.procedureId());
        log.debug("Visit creation request validation: {}", request);

        try {
            VisitLogDto treatment = treatmentService.createTreatment(patientId, request);
            log.info("Successfully created treatment with ID: {} for patient: {} - Status: 201 CREATED",
                    treatment.visitId(), patientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(treatment);
        } catch (Exception e) {
            log.error("Failed to create treatment for patient: {} - Error: {}", patientId, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<VisitLogDto>> getPatientTreatmentHistory(UUID patientId, Pageable pageable) {
        log.info("Retrieving treatment history for patient ID: {} with pagination: {}", patientId, pageable);
        Page<VisitLogDto> treatments = treatmentService.getPatientTreatmentHistory(patientId, pageable);
        return ResponseEntity.ok(treatments);
    }

    @Override
    public ResponseEntity<VisitLogDto> getTreatmentById(UUID id) {
        log.info("Retrieving treatment with ID: {}", id);
        VisitLogDto treatment = treatmentService.findTreatmentById(id);
        return ResponseEntity.ok(treatment);
    }

    @Override
    public ResponseEntity<VisitLogDto> updateTreatment(UUID id, VisitCreateRequest request) {
        log.info("Updating treatment with ID: {}", id);
        log.debug("Visit update request validation: {}", request);

        try {
            VisitLogDto treatment = treatmentService.updateTreatment(id, request);
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
    public ResponseEntity<Page<VisitLogDto>> searchTreatments(VisitSearchCriteria criteria, Pageable pageable) {
        log.info("Advanced search for treatments with criteria: {}", criteria);
        Page<VisitLogDto> treatments = treatmentService.searchTreatments(criteria, pageable);
        return ResponseEntity.ok(treatments);
    }
}
