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
import sy.sezar.clinicx.patient.service.VisitService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class VisitControllerImpl implements VisitControllerApi {

    private final VisitService visitService;

    @Override
    public ResponseEntity<VisitLogDto> createVisit(UUID patientId, VisitCreateRequest request) {
        log.info("Creating new visit for patient ID: {} (procedure: {})", patientId, request.procedureId());
        log.debug("Visit creation request validation: {}", request);

        try {
            VisitLogDto visit = visitService.createVisit(patientId, request);
            log.info("Successfully created visit with ID: {} for patient: {} - Status: 201 CREATED",
                    visit.visitId(), patientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(visit);
        } catch (Exception e) {
            log.error("Failed to create visit for patient: {} - Error: {}", patientId, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<VisitLogDto>> getPatientVisitHistory(UUID patientId, Pageable pageable) {
        log.info("Retrieving visit history for patient ID: {} with pagination: {}", patientId, pageable);
        Page<VisitLogDto> visits = visitService.getPatientVisitHistory(patientId, pageable);
        return ResponseEntity.ok(visits);
    }

    @Override
    public ResponseEntity<VisitLogDto> getVisitById(UUID id) {
        log.info("Retrieving visit with ID: {}", id);
        VisitLogDto visit = visitService.findVisitById(id);
        return ResponseEntity.ok(visit);
    }

    @Override
    public ResponseEntity<VisitLogDto> updateVisit(UUID id, VisitCreateRequest request) {
        log.info("Updating visit with ID: {}", id);
        log.debug("Visit update request validation: {}", request);

        try {
            VisitLogDto visit = visitService.updateVisit(id, request);
            log.info("Successfully updated visit with ID: {} - Status: 200 OK", id);
            return ResponseEntity.ok(visit);
        } catch (Exception e) {
            log.error("Failed to update visit with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Void> deleteVisit(UUID id) {
        log.info("Deleting visit with ID: {}", id);

        try {
            visitService.deleteVisit(id);
            log.info("Successfully deleted visit with ID: {} - Status: 204 NO CONTENT", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete visit with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<VisitLogDto>> searchVisits(VisitSearchCriteria criteria, Pageable pageable) {
        log.info("Advanced search for visits with criteria: {}", criteria);
        Page<VisitLogDto> visits = visitService.searchVisits(criteria, pageable);
        return ResponseEntity.ok(visits);
    }
}
