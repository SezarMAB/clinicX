package sy.sezar.clinicx.clinic.mapper;

import org.mapstruct.*;
import sy.sezar.clinicx.clinic.dto.StaffCreateRequest;
import sy.sezar.clinicx.clinic.dto.StaffDto;
import sy.sezar.clinicx.clinic.dto.StaffUpdateRequest;
import sy.sezar.clinicx.clinic.model.Staff;

@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface StaffMapper {

    @Mapping(target = "accessRole", ignore = true)
    @Mapping(target = "isPrimary", ignore = true)
    @Mapping(target = "accessActive", ignore = true)
    StaffDto toDto(Staff staff);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Staff toEntity(StaffCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void updateFromRequest(StaffUpdateRequest request, @MappingTarget Staff staff);
}
