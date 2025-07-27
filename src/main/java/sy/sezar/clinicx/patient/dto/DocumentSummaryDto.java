package sy.sezar.clinicx.patient.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Used in the documents tab table showing uploaded patient documents.
 */
public record DocumentSummaryDto(
    UUID documentId,
    String fileName,
    String fileType,
    String mimeType,
    Instant uploadDate,
    Long fileSizeBytes,
    String uploadedByStaffName
) {}
