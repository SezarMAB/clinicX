package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.VisitCreateRequest;
import sy.sezar.clinicx.patient.dto.VisitLogDto;
import sy.sezar.clinicx.patient.model.Visit;

/**
 * Mapper for converting between Visit entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    // Visit <-> VisitLogDto
    @Mapping(source = "id", target = "visitId")
    @Mapping(source = "visitDate", target = "visitDate")
    @Mapping(target = "visitTime", ignore = true)
    @Mapping(target = "visitType", ignore = true)
    @Mapping(source = "toothNumber", target = "toothNumber")
    @Mapping(source = "procedure.name", target = "visitName")
    @Mapping(source = "doctor.fullName", target = "doctorName")
    @Mapping(target = "durationMinutes", ignore = true)
    @Mapping(source = "cost", target = "cost")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "visitNotes", target = "notes")
    @Mapping(target = "nextAppointment", ignore = true)
    VisitLogDto toTreatmentLogDto(Visit visit);
    List<VisitLogDto> toTreatmentLogDtoList(List<Visit> visits);

    // VisitCreateRequest -> Visit
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "procedure", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "materials", ignore = true)
    Visit toTreatment(VisitCreateRequest request);
}
