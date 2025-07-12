package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.AppointmentStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * Used in the upcoming appointments info card on patient overview.
 */
public record UpcomingAppointmentDto(
    UUID appointmentId,
    Instant appointmentDateTime,
    String specialty,
    String treatmentType,
    String doctorName,
    AppointmentStatus status,
    Integer durationMinutes
) {}
