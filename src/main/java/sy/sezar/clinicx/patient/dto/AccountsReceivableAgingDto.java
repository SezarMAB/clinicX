package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for accounts receivable aging.
 */
public record AccountsReceivableAgingDto(
    LocalDate asOfDate,
    BigDecimal totalReceivables,
    Map<String, BigDecimal> agingBuckets,
    List<AgingDetailDto> agingDetails,
    BigDecimal currentAmount,
    BigDecimal days30Amount,
    BigDecimal days60Amount,
    BigDecimal days90Amount,
    BigDecimal over90Amount,
    int totalAccounts,
    int currentAccounts,
    int overdueAccounts
) {}
