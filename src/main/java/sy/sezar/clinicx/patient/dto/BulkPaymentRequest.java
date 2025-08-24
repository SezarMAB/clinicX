package sy.sezar.clinicx.patient.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request DTO for processing multiple payments in bulk.
 */
public record BulkPaymentRequest(
    @NotNull(message = "Payments list is required")
    @NotEmpty(message = "At least one payment is required")
    @Valid
    List<BulkPaymentItem> payments,
    
    boolean validateBalance,
    boolean stopOnError,
    String batchDescription
) {
    public BulkPaymentRequest {
        // Set defaults
        if (payments == null) {
            payments = List.of();
        }
    }
    
    // Constructor with defaults
    public BulkPaymentRequest(List<BulkPaymentItem> payments) {
        this(payments, true, false, null);
    }
}