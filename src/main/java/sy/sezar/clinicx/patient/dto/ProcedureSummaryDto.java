package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Used in treatment forms and procedure selection dropdowns.
 */
public record ProcedureSummaryDto(
    UUID procedureId,
    String procedureCode,
    String name,
    String description,
    BigDecimal defaultCost,
    Integer defaultDurationMinutes,
    String specialtyName
) {}
