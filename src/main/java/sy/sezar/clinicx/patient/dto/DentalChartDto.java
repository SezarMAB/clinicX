package sy.sezar.clinicx.patient.dto;

import java.util.List;

/**
 * Used in the dental chart tab containing all teeth for a patient.
 */
public record DentalChartDto(
    List<ToothDto> teeth
) {}
