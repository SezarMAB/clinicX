package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for procedure information.
 */
public record ProcedureDto(
    UUID id,
    String code,
    String name,
    String description,
    BigDecimal defaultCost,
    Integer estimatedDurationMinutes,
    String category,
    Boolean active
) {}
