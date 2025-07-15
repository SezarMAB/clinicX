package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.AppointmentStatus;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Used in the appointments sidebar panel showing daily appointment list.
 */
public record AppointmentCardDto(
    UUID appointmentId,
    UUID patientId,
    String patientFullName,
    String patientPublicId,
    LocalTime startTime,
    LocalTime endTime,
    String appointmentType,
    String practitionerTag,
    String patientPhoneNumber,
    String gender,
    boolean isActive,
    boolean hasFinancialAlert,
    AppointmentStatus status
) {}
