package sy.sezar.clinicx.staff.mapper;

import org.mapstruct.*;
import sy.sezar.clinicx.clinic.mapper.SpecialtyMapper;
import sy.sezar.clinicx.staff.dto.StaffCreateRequest;
import sy.sezar.clinicx.staff.dto.StaffDto;
import sy.sezar.clinicx.staff.dto.StaffUpdateRequest;
import sy.sezar.clinicx.staff.model.Staff;

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
