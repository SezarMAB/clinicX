package sy.sezar.clinicx.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sy.sezar.clinicx.clinic.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.clinic.dto.SpecialtyDto;
import sy.sezar.clinicx.clinic.dto.SpecialtyUpdateRequest;
import sy.sezar.clinicx.clinic.model.Specialty;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {
    
    SpecialtyDto toDto(Specialty specialty);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    Specialty toEntity(SpecialtyCreateRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(SpecialtyUpdateRequest request, @MappingTarget Specialty specialty);
}
