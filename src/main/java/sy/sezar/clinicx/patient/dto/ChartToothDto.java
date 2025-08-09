package sy.sezar.clinicx.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * DTO for individual tooth data in the dental chart.
 * Matches the JSONB structure stored in the database.
 */
public record ChartToothDto(
    @JsonProperty("tooth_id") String toothId,
    String condition,
    Map<String, SurfaceDto> surfaces,
    FlagsDto flags,
    String notes
) {
    public record SurfaceDto(
        String condition,
        String notes
    ) {}
    
    public record FlagsDto(
        boolean impacted,
        boolean mobile,
        boolean periapical,
        boolean abscess
    ) {}
}