package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new invoice.
 */
public record InvoiceCreateRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Invoice date is required")
    LocalDate invoiceDate,

    @NotNull(message = "Due date is required")
    LocalDate dueDate,

    @NotNull(message = "Invoice items are required")
    List<InvoiceItemRequest> items,

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes
) {
    /**
     * Inner record for invoice items.
     */
    public record InvoiceItemRequest(
        @NotNull(message = "Procedure ID is required")
        UUID procedureId,

        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be positive")
        BigDecimal unitPrice,

        @Size(max = 200, message = "Description cannot exceed 200 characters")
        String description
    ) {}
}
