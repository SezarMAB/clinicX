package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Used in expandable financial record details showing payment installments.
 */
public record PaymentInstallmentDto(
    String description,
    LocalDate paymentDate,
    BigDecimal amount
) {}
