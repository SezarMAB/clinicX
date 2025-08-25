package sy.sezar.clinicx.patient.service;

import sy.sezar.clinicx.patient.dto.ChartDataDto;
import sy.sezar.clinicx.patient.dto.ChartToothDto;

import java.util.UUID;

/**
 * Service interface for managing dental chart and tooth conditions.
 */
public interface DentalChartService {

    /**
     * Get the complete dental chart for a patient.
     * Returns the full JSONB chart data.
     */
    ChartDataDto getPatientDentalChart(UUID patientId);
    
    /**
     * Update a specific tooth's condition and data.
     */
    ChartToothDto updateToothCondition(UUID patientId, String toothId, ChartToothDto toothData);
    
    /**
     * Get details for a specific tooth.
     */
    ChartToothDto getToothDetails(UUID patientId, String toothId);
    
    /**
     * Update a specific surface condition for a tooth.
     */
    void updateSurfaceCondition(UUID patientId, String toothId, String surfaceName, 
                                String condition, String notes);
    
    /**
     * Initialize a new dental chart for a patient.
     */
    ChartDataDto initializeDentalChart(UUID patientId);
}
