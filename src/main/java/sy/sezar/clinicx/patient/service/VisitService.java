package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.VisitCreateRequest;
import sy.sezar.clinicx.patient.dto.VisitLogDto;
import sy.sezar.clinicx.patient.dto.VisitSearchCriteria;

import java.util.UUID;

/**
 * Service interface for managing patient visits.
 */
public interface VisitService {

    /**
     * Creates a new visit record for a specific patient.
     */
    VisitLogDto createVisit(UUID patientId, VisitCreateRequest request);

    /**
     * Gets visit history for a patient with pagination.
     */
    Page<VisitLogDto> getPatientVisitHistory(UUID patientId, Pageable pageable);

    /**
     * Finds a visit by ID.
     */
    VisitLogDto findVisitById(UUID visitId);

    /**
     * Updates a visit record.
     */
    VisitLogDto updateVisit(UUID visitId, VisitCreateRequest request);

    /**
     * Deletes a visit record.
     */
    void deleteVisit(UUID visitId);

    /**
     * Advanced search for visits with multiple criteria.
     */
    Page<VisitLogDto> searchVisits(VisitSearchCriteria criteria, Pageable pageable);
}
