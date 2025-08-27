package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for treatment cost details.
 */
public record TreatmentCostDetailDto(
    UUID treatmentId,
    UUID patientId,
    String patientName,
    String treatmentName,
    LocalDate treatmentDate,
    BigDecimal treatmentCost,
    BigDecimal materialCost,
    BigDecimal laborCost,
    BigDecimal totalCost,
    BigDecimal revenue,
    BigDecimal profit,
    BigDecimal profitMargin
) {}
