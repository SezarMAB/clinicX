package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;

/**
 * Used in the finance tab showing patient balance summary card.
 */
public record PatientBalanceSummaryDto(
    BigDecimal totalBalance,
    String balanceStatus,
    String balanceDescription
) {}
