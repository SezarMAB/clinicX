package sy.sezar.clinicx.patient.dto;

import java.time.LocalDate;

/**
 * Used in the dental chart display showing individual tooth conditions.
 */
public record ToothDto(
    Integer toothNumber,
    String conditionCode,
    String conditionName,
    String colorHex,
    String notes,
    LocalDate lastTreatmentDate
) {}
