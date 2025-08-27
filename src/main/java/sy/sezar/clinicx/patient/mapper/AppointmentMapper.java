package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.AppointmentCreateRequest;
import sy.sezar.clinicx.patient.dto.UpcomingAppointmentDto;
import sy.sezar.clinicx.patient.model.Appointment;

/**
 * Mapper for converting between Appointment entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    // Appointment <-> AppointmentCardDto
    @Mapping(target = "appointmentId", source = "id")
    @Mapping(target = "startTime", expression = "java(java.time.LocalTime.ofInstant(appointment.getAppointmentDatetime(), java.time.ZoneId.systemDefault()))")
    @Mapping(target = "endTime", expression = "java(java.time.LocalTime.ofInstant(appointment.getAppointmentDatetime().plusSeconds(appointment.getDurationMinutes() * 60L), java.time.ZoneId.systemDefault()))")
    @Mapping(target = "appointmentType", source = "specialty.name")
    @Mapping(target = "practitionerTag", source = "doctor.fullName")
    @Mapping(target = "isActive", expression = "java(appointment.getStatus() == sy.sezar.clinicx.patient.model.enums.AppointmentStatus.SCHEDULED)")
    @Mapping(target = "hasFinancialAlert", ignore = true)
    @Mapping(source = "patient.fullName", target = "patientFullName")
    @Mapping(source = "patient.publicFacingId", target = "patientPublicId")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.phoneNumber", target = "patientPhoneNumber")
    @Mapping(source = "patient.gender", target = "patientGender")
    AppointmentCardDto toAppointmentCardDto(Appointment appointment);
    List<AppointmentCardDto> toAppointmentCardDtoList(List<Appointment> appointments);

    // Appointment <-> UpcomingAppointmentDto
    @Mapping(source = "id", target = "appointmentId")
    @Mapping(source = "appointmentDatetime", target = "appointmentDateTime")
    @Mapping(source = "specialty.name", target = "specialty")
    @Mapping(source = "doctor.fullName", target = "doctorName")
    @Mapping(target = "treatmentType", expression = "java(determineTreatmentType(appointment))")
    UpcomingAppointmentDto toUpcomingAppointmentDto(Appointment appointment);

    List<UpcomingAppointmentDto> toUpcomingAppointmentDtoList(List<Appointment> appointments);

    // AppointmentCreateRequest -> Appointment
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialty", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Appointment toEntity(AppointmentCreateRequest request);

    default String determineTreatmentType(Appointment appointment) {
        // Logic to determine treatment type based on appointment
        // This could be based on the specialty, treatments, or other factors
        return appointment.getSpecialty() != null ? appointment.getSpecialty().getName() : "General";
    }
}
