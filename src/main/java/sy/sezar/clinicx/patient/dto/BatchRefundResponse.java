package sy.sezar.clinicx.patient.dto;

import java.util.List;

/**
 * Response DTO for batch refund processing.
 */
public record BatchRefundResponse(
    int totalRequested,
    int successCount,
    int failureCount,
    List<PaymentDto> processedRefunds,
    List<RefundError> errors
) {
    public record RefundError(
        int index,
        String patientId,
        String errorMessage,
        String errorCode
    ) {}
}