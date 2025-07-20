package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for comprehensive patient financial summary.
 */
public record PatientFinancialSummaryDto(
    UUID patientId,
    String patientName,
    BigDecimal currentBalance,
    BigDecimal totalInvoiced,
    BigDecimal totalPaid,
    BigDecimal totalOutstanding,
    String balanceStatus,
    LocalDate lastPaymentDate,
    LocalDate lastInvoiceDate,
    List<RecentFinancialActivityDto> recentActivity,
    List<OutstandingInvoiceDto> outstandingInvoices
) {
    /**
     * Inner record for recent financial activity.
     */
    public record RecentFinancialActivityDto(
        UUID id,
        String type, // "INVOICE" or "PAYMENT"
        LocalDate date,
        BigDecimal amount,
        String description,
        String status
    ) {}

    /**
     * Inner record for outstanding invoices.
     */
    public record OutstandingInvoiceDto(
        UUID invoiceId,
        String invoiceNumber,
        LocalDate invoiceDate,
        LocalDate dueDate,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal outstandingAmount,
        Integer daysPastDue,
        String status
    ) {}
}
