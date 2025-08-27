package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for invoice aging report.
 */
public record InvoiceAgingReportDto(
    BigDecimal current,
    BigDecimal days30,
    BigDecimal days60,
    BigDecimal days90Plus,
    BigDecimal total,
    LocalDate asOfDate,
    List<AgingDetail> details
) {
    public record AgingDetail(
        String patientName,
        String patientPublicId,
        String invoiceNumber,
        LocalDate issueDate,
        LocalDate dueDate,
        BigDecimal amount,
        BigDecimal balance,
        int daysPastDue,
        String agingBucket
    ) {}
}