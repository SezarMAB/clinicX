package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for processing an approved refund.
 */
public record ProcessRefundRequest(
    @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    String transactionId,
    
    @NotBlank(message = "Refund method is required")
    @Size(max = 50, message = "Refund method cannot exceed 50 characters")
    String refundMethod,
    
    @Size(max = 500, message = "Processing notes cannot exceed 500 characters")
    String processingNotes
) {}