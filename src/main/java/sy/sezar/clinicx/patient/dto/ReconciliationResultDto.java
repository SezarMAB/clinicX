package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for payment reconciliation results.
 */
public record ReconciliationResultDto(
    int totalPayments,
    int reconciledCount,
    int failedCount,
    BigDecimal totalAmount,
    LocalDate reconciliationDate,
    String referenceNumber,
    List<String> errors,
    String status
) {}