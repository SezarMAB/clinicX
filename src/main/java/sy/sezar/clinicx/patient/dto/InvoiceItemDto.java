package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for Invoice Item.
 */
public record InvoiceItemDto(
    UUID id,
    UUID invoiceId,
    UUID procedureId,
    String description,
    BigDecimal amount,
    Integer quantity,
    BigDecimal unitPrice,
    String procedureName,
    String procedureCode,
    Integer toothNumber,
    Instant createdAt
) {}
