package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;
import sy.sezar.clinicx.patient.model.Visit;

/**
 * Mapper for converting between Visit entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    // Visit <-> TreatmentLogDto
    @Mapping(source = "id", target = "treatmentId")
    @Mapping(source = "treatmentDate", target = "treatmentDate")
    @Mapping(target = "treatmentTime", ignore = true)
    @Mapping(target = "visitType", ignore = true)
    @Mapping(source = "toothNumber", target = "toothNumber")
    @Mapping(source = "procedure.name", target = "treatmentName")
    @Mapping(source = "doctor.fullName", target = "doctorName")
    @Mapping(target = "durationMinutes", ignore = true)
    @Mapping(source = "cost", target = "cost")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "treatmentNotes", target = "notes")
    @Mapping(target = "nextAppointment", ignore = true)
    TreatmentLogDto toTreatmentLogDto(Visit visit);
    List<TreatmentLogDto> toTreatmentLogDtoList(List<Visit> visits);

    // TreatmentCreateRequest -> Visit
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "procedure", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "materials", ignore = true)
    Visit toTreatment(TreatmentCreateRequest request);
}
