package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for treatment cost analysis.
 */
public record TreatmentCostAnalysisDto(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalTreatmentCost,
    BigDecimal totalMaterialCost,
    BigDecimal totalLaborCost,
    BigDecimal averageTreatmentCost,
    Map<String, BigDecimal> costByTreatment,
    Map<String, BigDecimal> costByMaterial,
    Map<String, BigDecimal> costByDoctor,
    List<TreatmentCostDetailDto> costDetails,
    BigDecimal costVariance,
    BigDecimal profitabilityMargin
) {}
