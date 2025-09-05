package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.visit.CreateProcedureRequest;
import sy.sezar.clinicx.patient.dto.visit.ProcedureDto;
import sy.sezar.clinicx.patient.model.VisitProcedure;
import sy.sezar.clinicx.patient.model.enums.ProcedureStatus;

import java.util.List;
import java.util.Set;

/**
 * Mapper for converting between VisitProcedure entity and its DTOs.
 * Follows MapStruct patterns for clean entity-DTO conversions.
 */
@Mapper(componentModel = "spring", uses = {MaterialMapper.class, LabCaseMapper.class})
public interface ProcedureMapper {

    // VisitProcedure -> ProcedureDto
    @Mapping(source = "visit.id", target = "visitId")
    @Mapping(source = "performedBy.id", target = "performedById")
    @Mapping(source = "performedBy.fullName", target = "performedByName")
    @Mapping(source = "materials", target = "materials")
    @Mapping(source = "labCase", target = "labCase")
    ProcedureDto toDto(VisitProcedure procedure);
    
    List<ProcedureDto> toDtoList(Set<VisitProcedure> procedures);

    // CreateProcedureRequest -> VisitProcedure (using default method for builder)
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "performedBy", ignore = true)
    @Mapping(target = "materials", ignore = true)
    @Mapping(target = "labCase", ignore = true)
    default VisitProcedure toEntity(CreateProcedureRequest request) {
        if (request == null) {
            return null;
        }
        
        return VisitProcedure.builder()
            .code(request.code())
            .name(request.name())
            .toothNumber(request.toothNumber())
            .surfaces(request.surfaces())
            .quantity(request.quantity())
            .unitFee(request.unitFee())
            .durationMinutes(request.durationMinutes())
            .status(ProcedureStatus.PLANNED) // Default status
            .billable(request.billable())
            .notes(request.notes())
            .build();
    }
}