package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.DentalChartDto;
import sy.sezar.clinicx.patient.dto.ToothDto;
import sy.sezar.clinicx.patient.model.PatientTooth;
import java.util.List;

/**
 * Mapper for converting between PatientTooth entity and DentalChart/Tooth DTOs.
 */
@Mapper(componentModel = "spring")
public interface DentalChartMapper {

  // PatientTooth <-> ToothDto
  @Mapping(target = "conditionCode", source = "currentCondition.code")
  @Mapping(target = "conditionName", source = "currentCondition.name")
  @Mapping(target = "colorHex", source = "currentCondition.colorHex")
  ToothDto toToothDto(PatientTooth tooth);
  List<ToothDto> toToothDtoList(List<PatientTooth> teeth);

  // List<PatientTooth> <-> DentalChartDto
  default DentalChartDto toDentalChartDto(List<PatientTooth> teeth) {
    return new DentalChartDto(toToothDtoList(teeth));
  }
}

