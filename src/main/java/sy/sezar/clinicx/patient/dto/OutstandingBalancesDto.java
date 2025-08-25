package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for outstanding balances.
 */
public record OutstandingBalancesDto(
    BigDecimal threshold,
    BigDecimal totalOutstanding,
    int totalAccounts,
    List<OutstandingBalanceDetailDto> outstandingAccounts,
    BigDecimal averageOutstanding,
    BigDecimal medianOutstanding,
    BigDecimal highestOutstanding,
    BigDecimal lowestOutstanding
) {}
