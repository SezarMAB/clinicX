package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Used in the finance tab table showing invoices and payments with expandable installments.
 */
public record FinancialRecordDto(
    UUID recordId,
    String invoiceNumber,
    LocalDate issueDate,
    LocalDate dueDate,
    BigDecimal amount,
    InvoiceStatus status,
    List<PaymentInstallmentDto> installments
) {}
