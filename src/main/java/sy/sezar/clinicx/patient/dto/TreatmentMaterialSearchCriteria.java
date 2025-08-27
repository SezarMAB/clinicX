package sy.sezar.clinicx.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Advanced search criteria for treatment materials")
public record TreatmentMaterialSearchCriteria(

        @Schema(description = "Filter by visit ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID visitId,

        @Schema(description = "Filter by patient ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID patientId,

        @Schema(description = "Filter by material name", example = "Composite Resin")
        String materialName,

        @Schema(description = "Filter by multiple material names")
        List<String> materialNames,

        @Schema(description = "Search in material names", example = "composite")
        String materialNameContains,

        @Schema(description = "Filter by supplier", example = "3M Dental")
        String supplier,

        @Schema(description = "Filter by multiple suppliers")
        List<String> suppliers,

        @Schema(description = "Filter by batch number", example = "BT2024001")
        String batchNumber,

        @Schema(description = "Filter by unit of measurement", example = "grams")
        String unit,

        @Schema(description = "Minimum quantity used", example = "1.0")
        @DecimalMin("0.0")
        BigDecimal quantityFrom,

        @Schema(description = "Maximum quantity used", example = "10.0")
        @DecimalMin("0.0")
        BigDecimal quantityTo,

        @Schema(description = "Minimum cost per unit", example = "5.00")
        @DecimalMin("0.0")
        BigDecimal costPerUnitFrom,

        @Schema(description = "Maximum cost per unit", example = "50.00")
        @DecimalMin("0.0")
        BigDecimal costPerUnitTo,

        @Schema(description = "Minimum total cost", example = "10.00")
        @DecimalMin("0.0")
        BigDecimal totalCostFrom,

        @Schema(description = "Maximum total cost", example = "100.00")
        @DecimalMin("0.0")
        BigDecimal totalCostTo,

        @Schema(description = "Search in notes", example = "high quality")
        String notesContain,

        @Schema(description = "Filter by usage date from", example = "2024-01-01")
        LocalDate usedFrom,

        @Schema(description = "Filter by usage date to", example = "2024-12-31")
        LocalDate usedTo,

        @Schema(description = "Filter by creation date from", example = "2024-01-01")
        LocalDate createdFrom,

        @Schema(description = "Filter by creation date to", example = "2024-12-31")
        LocalDate createdTo
) {
}
