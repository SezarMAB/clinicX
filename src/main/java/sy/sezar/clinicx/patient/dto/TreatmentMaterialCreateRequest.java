package sy.sezar.clinicx.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request to create a new treatment material record")
public record TreatmentMaterialCreateRequest(
        
        @NotNull
        @Schema(description = "Treatment ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        UUID treatmentId,
        
        @NotBlank
        @Schema(description = "Material name", example = "Composite Resin", required = true)
        String materialName,
        
        @NotNull
        @Positive
        @Schema(description = "Quantity used", example = "2.5", required = true)
        BigDecimal quantity,
        
        @Schema(description = "Unit of measurement", example = "grams")
        String unit,
        
        @NotNull
        @Positive
        @Schema(description = "Cost per unit", example = "15.00", required = true)
        BigDecimal costPerUnit,
        
        @Schema(description = "Supplier name", example = "3M Dental")
        String supplier,
        
        @Schema(description = "Batch number", example = "BT2024001")
        String batchNumber,
        
        @Schema(description = "Additional notes", example = "High quality material")
        String notes
) {
}