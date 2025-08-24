package sy.sezar.clinicx.patient.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for adding items to an invoice.
 */
public record AddInvoiceItemsRequest(
    @NotEmpty(message = "At least one item is required")
    @Valid
    List<InvoiceItemRequest> items
) {
    public record InvoiceItemRequest(
        UUID treatmentId,
        String description,
        BigDecimal amount,
        Integer quantity,
        BigDecimal unitPrice
    ) {}
}