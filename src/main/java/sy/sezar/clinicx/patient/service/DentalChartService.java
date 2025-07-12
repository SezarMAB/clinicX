package sy.sezar.clinicx.patient.service;

import sy.sezar.clinicx.patient.dto.DentalChartDto;
import sy.sezar.clinicx.patient.dto.ToothDto;

import java.util.UUID;

/**
 * Service interface for managing dental chart and tooth conditions.
 */
public interface DentalChartService {

    /**
     * Gets the complete dental chart for a patient.
     */
    DentalChartDto getPatientDentalChart(UUID patientId);

    /**
     * Updates a specific tooth condition.
     */
    ToothDto updateToothCondition(UUID patientId, Integer toothNumber, UUID conditionId, String notes);

    /**
     * Gets details for a specific tooth.
     */
    ToothDto getToothDetails(UUID patientId, Integer toothNumber);
}
