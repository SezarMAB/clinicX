package sy.sezar.clinicx.tenant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.tenant.dto.UserTenantAccessDto;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;

@Mapper(componentModel = "spring")
public interface UserTenantAccessMapper {

    @Mapping(target = "tenantName", source = "tenant.name")
    @Mapping(target = "isPrimary", source = "primary")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UserTenantAccessDto toDto(UserTenantAccess entity);

    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "isPrimary", source = "primary")
    @Mapping(target = "isActive", source = "active")
    UserTenantAccess toEntity(UserTenantAccessDto dto);
}