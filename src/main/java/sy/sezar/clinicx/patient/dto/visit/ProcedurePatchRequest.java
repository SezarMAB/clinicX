package sy.sezar.clinicx.patient.dto.visit;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for partially updating a procedure.
 * All fields are optional - only provided fields will be updated.
 * Follows PATCH semantics for partial updates.
 */
public record ProcedurePatchRequest(
    @Pattern(regexp = "^(PLANNED|IN_PROGRESS|SENT_TO_LAB|RECEIVED_FROM_LAB|COMPLETED|CANCELLED)$",
            message = "Invalid status")
    String status,
    
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 32, message = "Quantity cannot exceed 32")
    Integer quantity,
    
    @DecimalMin(value = "0.00", message = "Unit fee cannot be negative")
    @DecimalMax(value = "999999.99", message = "Unit fee exceeds maximum allowed")
    @Digits(integer = 6, fraction = 2, message = "Invalid fee format")
    BigDecimal unitFee,
    
    @Min(value = 5, message = "Duration must be at least 5 minutes")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes")
    Integer durationMinutes,
    
    UUID performedById,
    
    Boolean billable,
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes
) {
    /**
     * Check if any field is being updated
     */
    public boolean hasUpdates() {
        return status != null || 
               quantity != null || 
               unitFee != null || 
               durationMinutes != null || 
               performedById != null ||
               billable != null ||
               notes != null;
    }

    /**
     * Check if status is being changed
     */
    public boolean hasStatusChange() {
        return status != null && !status.isBlank();
    }

    /**
     * Check if financial fields are being updated
     */
    public boolean hasFinancialChanges() {
        return unitFee != null || quantity != null || billable != null;
    }
}