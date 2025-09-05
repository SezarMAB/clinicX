package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for cash flow details.
 */
public record CashFlowDetailDto(
    LocalDate date,
    String category,
    String description,
    BigDecimal amount,
    String type,
    BigDecimal runningBalance
) {}
