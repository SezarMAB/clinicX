package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request DTO for applying discount to invoice.
 */
public record DiscountRequest(
    @NotNull(message = "Discount type is required")
    DiscountType type,
    
    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    BigDecimal value,
    
    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    String reason
) {
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
}