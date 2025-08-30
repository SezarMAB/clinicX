package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.visit.LabCaseDto;
import sy.sezar.clinicx.patient.model.LabCase;

/**
 * Mapper for converting between LabCase entity and LabCaseDto.
 */
@Mapper(componentModel = "spring")
public interface LabCaseMapper {

    // LabCase -> LabCaseDto
    @Mapping(source = "procedure.id", target = "procedureId")
    @Mapping(source = "status", target = "status")
    LabCaseDto toDto(LabCase labCase);

    // LabCaseDto -> LabCase (Note: LabCase uses @Builder from entity)
    @Mapping(target = "procedure", ignore = true)
    default LabCase toEntity(LabCaseDto dto) {
        if (dto == null) {
            return null;
        }
        
        return LabCase.builder()
            .labName(dto.labName())
            .sentDate(dto.sentDate())
            .dueDate(dto.dueDate())
            .receivedDate(dto.receivedDate())
            .trackingNumber(dto.trackingNumber())
            .status(sy.sezar.clinicx.patient.model.enums.LabCaseStatus.valueOf(dto.status()))
            .technicianName(dto.technicianName())
            .shade(dto.shade())
            .materialType(dto.materialType())
            .notes(dto.notes())
            .build();
    }
}