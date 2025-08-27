package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for cancelling an invoice.
 */
public record CancelInvoiceRequest(
    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    String reason,
    
    boolean notifyPatient
) {}