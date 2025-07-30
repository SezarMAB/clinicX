package sy.sezar.clinicx.clinic.mapper;

import org.mapstruct.*;
import sy.sezar.clinicx.clinic.dto.StaffCreateRequest;
import sy.sezar.clinicx.clinic.dto.StaffDto;
import sy.sezar.clinicx.clinic.dto.StaffUpdateRequest;
import sy.sezar.clinicx.clinic.model.Staff;

@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface StaffMapper {

    StaffDto toDto(Staff staff);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "specialties", ignore = true)
    Staff toEntity(StaffCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    void updateFromRequest(StaffUpdateRequest request, @MappingTarget Staff staff);
}
