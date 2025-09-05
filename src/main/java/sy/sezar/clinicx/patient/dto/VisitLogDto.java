package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.TreatmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Used in the treatment log table showing patient visit history.
 */
public record VisitLogDto(
    UUID visitId,
    LocalDate visitDate,
    LocalTime visitTime,
    String visitType,
    Integer toothNumber,
    String visitName,
    String doctorName,
    Integer durationMinutes,
    BigDecimal cost,
    TreatmentStatus status,
    String notes,
    LocalDate nextAppointment
) {}
