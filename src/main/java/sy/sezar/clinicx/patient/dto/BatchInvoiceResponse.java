package sy.sezar.clinicx.patient.dto;

import java.util.List;

/**
 * Response DTO for batch invoice creation.
 */
public record BatchInvoiceResponse(
    int totalRequested,
    int successCount,
    int failureCount,
    List<InvoiceDto> createdInvoices,
    List<BatchInvoiceError> errors
) {
    public record BatchInvoiceError(
        String patientId,
        String patientName,
        String errorMessage,
        String errorCode
    ) {}
}