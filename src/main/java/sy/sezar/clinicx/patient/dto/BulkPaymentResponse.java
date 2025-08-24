package sy.sezar.clinicx.patient.dto;

import java.util.List;

/**
 * Response DTO for bulk payment processing results.
 */
public record BulkPaymentResponse(
    int totalRequested,
    int successCount,
    int failureCount,
    List<PaymentDto> successfulPayments,
    List<BulkPaymentError> errors,
    String summary
) {
    public record BulkPaymentError(
        int index,
        String patientId,
        String invoiceNumber,
        String errorMessage,
        String errorCode
    ) {}
}