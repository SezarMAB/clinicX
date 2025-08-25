package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for revenue trends.
 */
public record RevenueTrendDto(
    LocalDate date,
    BigDecimal revenue,
    BigDecimal invoices,
    BigDecimal payments,
    BigDecimal outstanding
) {}
