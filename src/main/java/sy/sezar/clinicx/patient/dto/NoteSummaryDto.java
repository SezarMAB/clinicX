package sy.sezar.clinicx.patient.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Used in the notes section showing clinical notes and observations.
 */
public record NoteSummaryDto(
    UUID noteId,
    String content,
    String createdByStaffName,
    Instant noteDate,
    Instant createdAt
) {}
