package sy.sezar.clinicx.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Visit material information")
public record TreatmentMaterialDto(

        @Schema(description = "Material ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Visit ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID visitId,

        @Schema(description = "Material name", example = "Composite Resin")
        String materialName,

        @Schema(description = "Quantity used", example = "2.5")
        BigDecimal quantity,

        @Schema(description = "Unit of measurement", example = "grams")
        String unit,

        @Schema(description = "Cost per unit", example = "15.00")
        BigDecimal costPerUnit,

        @Schema(description = "Total cost", example = "37.50")
        BigDecimal totalCost,

        @Schema(description = "Supplier name", example = "3M Dental")
        String supplier,

        @Schema(description = "Batch number", example = "BT2024001")
        String batchNumber,

        @Schema(description = "Additional notes", example = "High quality material")
        String notes,

        @Schema(description = "Creation timestamp")
        Instant createdAt,

        @Schema(description = "Last update timestamp")
        Instant updatedAt
) {
}
