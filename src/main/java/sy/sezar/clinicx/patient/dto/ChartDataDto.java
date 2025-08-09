package sy.sezar.clinicx.patient.dto;

import java.util.Map;

/**
 * DTO for the complete dental chart data.
 * Matches the JSONB structure stored in the database.
 */
public record ChartDataDto(
    MetaDto meta,
    Map<String, ChartToothDto> teeth
) {
    public record MetaDto(
        String version,
        String lastUpdated,
        String updatedBy
    ) {}
}