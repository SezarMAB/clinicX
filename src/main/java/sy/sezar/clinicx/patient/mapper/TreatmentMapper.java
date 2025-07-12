package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;
import sy.sezar.clinicx.patient.model.Treatment;

/**
 * Mapper for converting between Treatment entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    // Treatment <-> TreatmentLogDto
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
    TreatmentLogDto toTreatmentLogDto(Treatment treatment);
    List<TreatmentLogDto> toTreatmentLogDtoList(List<Treatment> treatments);

    // TreatmentCreateRequest -> Treatment
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "procedure", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Treatment toTreatment(TreatmentCreateRequest request);
}
