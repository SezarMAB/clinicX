package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for procedure analysis report.
 */
public record ProcedureAnalysisDto(
    LocalDate startDate,
    LocalDate endDate,
    int totalProcedures,
    BigDecimal totalRevenue,
    List<ProcedureMetrics> procedures
) {
    public record ProcedureMetrics(
        UUID procedureId,
        String procedureCode,
        String procedureName,
        int performedCount,
        BigDecimal totalRevenue,
        BigDecimal averagePrice,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        List<String> topDoctors,
        BigDecimal profitMargin
    ) {}
}