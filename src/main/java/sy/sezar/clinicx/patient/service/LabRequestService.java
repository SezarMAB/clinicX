package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.LabRequestDto;

import java.util.UUID;

/**
 * Service interface for managing lab requests.
 */
public interface LabRequestService {

    /**
     * Gets all lab requests for a patient with pagination.
     */
    Page<LabRequestDto> getPatientLabRequests(UUID patientId, Pageable pageable);

    /**
     * Finds a lab request by ID.
     */
    LabRequestDto findLabRequestById(UUID labRequestId);

    /**
     * Creates a new lab request.
     */
    LabRequestDto createLabRequest(UUID patientId, LabRequestDto request);

    /**
     * Updates lab request status.
     */
    LabRequestDto updateLabRequestStatus(UUID labRequestId, String status);
}
