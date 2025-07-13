package sy.sezar.clinicx.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sy.sezar.clinicx.clinic.dto.ClinicInfoDto;
import sy.sezar.clinicx.clinic.dto.ClinicInfoUpdateRequest;
import sy.sezar.clinicx.clinic.model.ClinicInfo;

@Mapper(componentModel = "spring")
public interface ClinicInfoMapper {
    
    ClinicInfoDto toDto(ClinicInfo clinicInfo);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(ClinicInfoUpdateRequest request, @MappingTarget ClinicInfo clinicInfo);
}
