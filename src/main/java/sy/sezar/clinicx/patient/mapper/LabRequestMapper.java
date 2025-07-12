package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.LabRequestDto;
import sy.sezar.clinicx.patient.model.LabRequest;

/**
 * Mapper for converting between LabRequest entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface LabRequestMapper {
  // LabRequest <-> LabRequestDto
  @Mapping(target = "labRequestId", source = "id")
  LabRequestDto toLabRequestDto(LabRequest labRequest);
  List<LabRequestDto> toLabRequestDtoList(List<LabRequest> labRequests);
}
