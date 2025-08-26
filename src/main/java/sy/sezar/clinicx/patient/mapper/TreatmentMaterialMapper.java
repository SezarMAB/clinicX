package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.model.TreatmentMaterial;

import java.util.List;

/**
 * Mapper for converting between TreatmentMaterial entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface TreatmentMaterialMapper {

    // TreatmentMaterial -> TreatmentMaterialDto
    @Mapping(source = "visit.id", target = "visitId")
    TreatmentMaterialDto toDto(TreatmentMaterial treatmentMaterial);

    List<TreatmentMaterialDto> toDtoList(List<TreatmentMaterial> treatmentMaterials);

    // TreatmentMaterialCreateRequest -> TreatmentMaterial
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    TreatmentMaterial toEntity(TreatmentMaterialCreateRequest request);
}
