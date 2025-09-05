package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for outstanding balance details.
 */
public record OutstandingBalanceDetailDto(
    UUID patientId,
    String patientName,
    String patientPublicId,
    BigDecimal outstandingBalance,
    LocalDate lastPaymentDate,
    LocalDate lastInvoiceDate,
    int daysSinceLastPayment,
    int daysSinceLastInvoice,
    int totalInvoices,
    int overdueInvoices
) {}
