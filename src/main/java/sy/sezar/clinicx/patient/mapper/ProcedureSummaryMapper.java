package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.ProcedureSummaryDto;
import sy.sezar.clinicx.patient.model.Procedure;

/**
 * Mapper for converting between Procedure entity and ProcedureSummaryDto.
 */
@Mapper(componentModel = "spring")
public interface ProcedureSummaryMapper {
  // Procedure <-> ProcedureSummaryDto
  @Mapping(target = "procedureId", source = "id")
  @Mapping(target = "specialtyName", source = "specialty.name")
  ProcedureSummaryDto toProcedureSummaryDto(Procedure procedure);
  List<ProcedureSummaryDto> toProcedureSummaryDtoList(List<Procedure> procedures);
}
