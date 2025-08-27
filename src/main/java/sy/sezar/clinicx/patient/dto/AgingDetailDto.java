package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for aging details.
 */
public record AgingDetailDto(
    UUID patientId,
    String patientName,
    UUID invoiceId,
    String invoiceNumber,
    LocalDate invoiceDate,
    LocalDate dueDate,
    BigDecimal amount,
    BigDecimal paidAmount,
    BigDecimal outstandingAmount,
    int daysOverdue,
    String agingBucket
) {}
