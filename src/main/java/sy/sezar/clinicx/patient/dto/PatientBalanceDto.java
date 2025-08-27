package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for patient balance information.
 */
public record PatientBalanceDto(
    UUID patientId,
    String patientName,
    String patientPublicId,
    String phoneNumber,
    String email,
    BigDecimal totalBalance,
    BigDecimal totalInvoiced,
    BigDecimal totalPaid,
    BigDecimal totalCredits,
    LocalDate lastPaymentDate,
    LocalDate oldestUnpaidInvoiceDate,
    int unpaidInvoiceCount,
    int daysSinceLastPayment
) {}