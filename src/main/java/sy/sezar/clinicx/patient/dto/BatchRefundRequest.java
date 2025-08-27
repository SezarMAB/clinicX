package sy.sezar.clinicx.patient.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for batch refund processing.
 */
public record BatchRefundRequest(
    @NotEmpty(message = "At least one refund request is required")
    @Valid
    List<RefundRequest> refunds,
    
    boolean stopOnError,
    String batchDescription
) {}