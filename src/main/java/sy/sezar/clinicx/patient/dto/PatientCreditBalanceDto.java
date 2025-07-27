package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing a patient's credit balance from advance payments.
 */
public record PatientCreditBalanceDto(
    UUID patientId,
    String patientName,
    BigDecimal totalCredits,
    BigDecimal appliedCredits,
    BigDecimal availableCredits,
    int totalAdvancePayments,
    int unappliedAdvancePayments
) {}