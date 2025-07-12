package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.NoteSummaryDto;
import sy.sezar.clinicx.patient.model.Note;

/**
 * Mapper for converting between Note entity and NoteSummaryDto.
 */
@Mapper(componentModel = "spring")
public interface NoteSummaryMapper {

  // Note <-> NoteSummaryDto
  @Mapping(target = "noteId", source = "id")
  @Mapping(target = "createdByStaffName", source = "createdBy.fullName")
  NoteSummaryDto toNoteSummaryDto(Note note);
  List<NoteSummaryDto> toNoteSummaryDtoList(List<Note> notes);
}
