package sy.sezar.clinicx.patient.dto.visit;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for adding a procedure to a visit.
 * Supports both simple procedures and complex procedures with lab work.
 * Immutable record with comprehensive validation.
 */
public record CreateProcedureRequest(
    @NotBlank(message = "Procedure code is required")
    @Size(max = 20, message = "Procedure code cannot exceed 20 characters")
    String code,
    
    @NotBlank(message = "Procedure name is required")
    @Size(max = 255, message = "Procedure name cannot exceed 255 characters")
    String name,
    
    @Min(value = 11, message = "Invalid tooth number (FDI notation: 11-48)")
    @Max(value = 48, message = "Invalid tooth number (FDI notation: 11-48)")
    Integer toothNumber,
    
    List<@Pattern(regexp = "^[MODBL]$", message = "Invalid surface code (M,O,D,B,L)") String> surfaces,
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 32, message = "Quantity cannot exceed 32")
    Integer quantity,
    
    @NotNull(message = "Unit fee is required")
    @DecimalMin(value = "0.00", message = "Unit fee cannot be negative")
    @DecimalMax(value = "999999.99", message = "Unit fee exceeds maximum allowed")
    @Digits(integer = 6, fraction = 2, message = "Invalid fee format")
    BigDecimal unitFee,
    
    @Min(value = 5, message = "Duration must be at least 5 minutes")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes (8 hours)")
    Integer durationMinutes,
    
    @NotNull(message = "Performed by ID is required")
    UUID performedById,
    
    @NotNull(message = "Billable flag is required")
    Boolean billable,
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes,
    
    @Valid
    CreateLabCaseRequest labCase
) {
    /**
     * Nested request for optional lab case
     */
    public record CreateLabCaseRequest(
        @NotBlank(message = "Lab name is required")
        @Size(max = 255, message = "Lab name cannot exceed 255 characters")
        String labName,
        
        @NotNull(message = "Sent date is required")
        @PastOrPresent(message = "Sent date cannot be in the future")
        LocalDate sentDate,
        
        @NotNull(message = "Due date is required")
        @Future(message = "Due date must be in the future")
        LocalDate dueDate,
        
        @Size(max = 100, message = "Tracking number cannot exceed 100 characters")
        String trackingNumber,
        
        @Size(max = 100, message = "Technician name cannot exceed 100 characters")
        String technicianName,
        
        @Size(max = 50, message = "Shade cannot exceed 50 characters")
        String shade,
        
        @Size(max = 100, message = "Material type cannot exceed 100 characters")
        String materialType,
        
        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes
    ) {
        /**
         * Validate date logic
         */
        public CreateLabCaseRequest {
            if (sentDate != null && dueDate != null && !dueDate.isAfter(sentDate)) {
                throw new IllegalArgumentException("Due date must be after sent date");
            }
        }
    }
}