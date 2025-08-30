package sy.sezar.clinicx.patient.dto.visit;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for material consumed during a procedure.
 */
@Builder
public record MaterialDto(
    UUID id,
    UUID procedureId,
    UUID materialId,
    
    @NotBlank(message = "Material name is required")
    @Size(max = 255, message = "Material name cannot exceed 255 characters")
    String materialName,
    
    @Size(max = 50, message = "Material code cannot exceed 50 characters")
    String materialCode,
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be at least 0.001")
    @DecimalMax(value = "9999.999", message = "Quantity cannot exceed 9999.999")
    BigDecimal quantity,
    
    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit cannot exceed 20 characters")
    String unit,
    
    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.00", message = "Unit cost cannot be negative")
    @DecimalMax(value = "99999.99", message = "Unit cost cannot exceed 99999.99")
    BigDecimal unitCost,
    
    @NotNull(message = "Total cost is required")
    @DecimalMin(value = "0.00", message = "Total cost cannot be negative")
    @DecimalMax(value = "99999.99", message = "Total cost cannot exceed 99999.99")
    BigDecimal totalCost,
    
    Instant consumedAt,
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes,
    
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Calculate total cost based on quantity and unit cost
     */
    public BigDecimal calculateTotalCost() {
        if (quantity == null || unitCost == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(unitCost);
    }
    
    /**
     * Check if material has been consumed
     */
    public boolean isConsumed() {
        return consumedAt != null;
    }
}