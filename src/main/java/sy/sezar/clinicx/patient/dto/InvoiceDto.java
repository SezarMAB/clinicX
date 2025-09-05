package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Invoice entity.
 */
public record InvoiceDto(
    UUID id,
    UUID patientId,
    String patientName,
    String patientPublicId,
    String invoiceNumber,
    LocalDate issueDate,
    LocalDate dueDate,
    BigDecimal totalAmount,
    BigDecimal paidAmount,
    BigDecimal balanceAmount,
    InvoiceStatus status,
    String notes,
    List<InvoiceItemDto> items,
    List<PaymentDto> payments,
    UUID createdById,
    String createdByName,
    Instant createdAt,
    Instant updatedAt,
    boolean isOverdue,
    int daysPastDue
) {}