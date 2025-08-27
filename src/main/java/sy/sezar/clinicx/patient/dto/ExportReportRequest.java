package sy.sezar.clinicx.patient.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for exporting reports.
 */
public record ExportReportRequest(
    LocalDate startDate,
    LocalDate endDate,
    UUID patientId,
    Map<String, Object> filters,
    boolean includeDetails,
    String locale
) {}