package sy.sezar.clinicx.patient.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.DocumentSummaryDto;
import sy.sezar.clinicx.patient.model.Document;

/**
 * Mapper for converting between Document entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    // Document <-> DocumentSummaryDto
    @Mapping(target = "documentId", source = "id")
    @Mapping(target = "fileType", source = "type")
    @Mapping(target = "uploadDate", source = "createdAt")
    @Mapping(target = "uploadedByStaffName", source = "uploadedByStaff.fullName")
    DocumentSummaryDto toDocumentSummaryDto(Document document);

    List<DocumentSummaryDto> toDocumentSummaryDtoList(List<Document> documents);
}
