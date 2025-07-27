package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.TreatmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Used in the treatment log table showing patient visit history.
 */
public record TreatmentLogDto(
    UUID treatmentId,
    LocalDate treatmentDate,
    LocalTime treatmentTime,
    String visitType,
    Integer toothNumber,
    String treatmentName,
    String doctorName,
    Integer durationMinutes,
    BigDecimal cost,
    TreatmentStatus status,
    String notes,
    LocalDate nextAppointment
) {}
