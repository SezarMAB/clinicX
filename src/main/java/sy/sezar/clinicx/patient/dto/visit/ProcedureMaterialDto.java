package sy.sezar.clinicx.patient.dto.visit;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Material used in a procedure DTO.
 * Tracks materials consumed during procedures for accurate costing.
 */
public record ProcedureMaterialDto(
    UUID id,
    UUID procedureId,
    UUID materialId,
    String materialName,
    String materialCode,
    BigDecimal quantity,
    String unit,
    BigDecimal unitCost,
    BigDecimal totalCost,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant consumedAt,
    String notes
) {
    /**
     * Calculate total cost if not provided
     */
    public BigDecimal calculateTotalCost() {
        if (quantity == null || unitCost == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(unitCost);
    }

    /**
     * Validate that total cost matches calculated value
     */
    public boolean isCostValid() {
        BigDecimal calculated = calculateTotalCost();
        return totalCost != null && totalCost.compareTo(calculated) == 0;
    }
}