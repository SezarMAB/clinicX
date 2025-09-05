package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.visit.ProcedureMaterialDto;
import sy.sezar.clinicx.patient.model.ProcedureMaterial;

import java.util.List;
import java.util.Set;

/**
 * Mapper for converting between ProcedureMaterial entity and ProcedureMaterialDto.
 */
@Mapper(componentModel = "spring")
public interface MaterialMapper {

    // ProcedureMaterial -> ProcedureMaterialDto
    @Mapping(source = "procedure.id", target = "procedureId")
    ProcedureMaterialDto toDto(ProcedureMaterial material);
    
    List<ProcedureMaterialDto> toDtoList(Set<ProcedureMaterial> materials);

    // ProcedureMaterialDto -> ProcedureMaterial (using default method for builder)
    @Mapping(target = "procedure", ignore = true)
    default ProcedureMaterial toEntity(ProcedureMaterialDto dto) {
        if (dto == null) {
            return null;
        }
        
        return ProcedureMaterial.builder()
            .materialId(dto.materialId())
            .materialName(dto.materialName())
            .materialCode(dto.materialCode())
            .quantity(dto.quantity())
            .unit(dto.unit())
            .unitCost(dto.unitCost())
            .totalCost(dto.totalCost())
            .consumedAt(dto.consumedAt())
            .notes(dto.notes())
            .build();
    }
}