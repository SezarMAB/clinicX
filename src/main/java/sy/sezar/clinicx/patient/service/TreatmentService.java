package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.VisitCreateRequest;
import sy.sezar.clinicx.patient.dto.VisitLogDto;
import sy.sezar.clinicx.patient.dto.VisitSearchCriteria;

import java.util.UUID;

/**
 * Service interface for managing patient treatments.
 */
public interface TreatmentService {

    /**
     * Creates a new treatment record for a specific patient.
     */
    VisitLogDto createTreatment(UUID patientId, VisitCreateRequest request);

    /**
     * Gets treatment history for a patient with pagination.
     */
    Page<VisitLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable);

    /**
     * Finds a treatment by ID.
     */
    VisitLogDto findTreatmentById(UUID treatmentId);

    /**
     * Updates a treatment record.
     */
    VisitLogDto updateTreatment(UUID treatmentId, VisitCreateRequest request);

    /**
     * Deletes a treatment record.
     */
    void deleteTreatment(UUID treatmentId);

    /**
     * Advanced search for treatments with multiple criteria.
     */
    Page<VisitLogDto> searchTreatments(VisitSearchCriteria criteria, Pageable pageable);
}
