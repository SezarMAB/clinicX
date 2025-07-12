package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;

import java.util.UUID;

/**
 * Service interface for managing patient treatments.
 */
public interface TreatmentService {

    /**
     * Creates a new treatment record.
     */
    TreatmentLogDto createTreatment(TreatmentCreateRequest request);

    /**
     * Gets treatment history for a patient with pagination.
     */
    Page<TreatmentLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable);

    /**
     * Finds a treatment by ID.
     */
    TreatmentLogDto findTreatmentById(UUID treatmentId);

    /**
     * Updates a treatment record.
     */
    TreatmentLogDto updateTreatment(UUID treatmentId, TreatmentCreateRequest request);

    /**
     * Deletes a treatment record.
     */
    void deleteTreatment(UUID treatmentId);
}
