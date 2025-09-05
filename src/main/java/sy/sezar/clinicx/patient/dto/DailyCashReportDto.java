package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for daily cash report.
 */
public record DailyCashReportDto(
    LocalDate reportDate,
    BigDecimal openingBalance,
    BigDecimal totalCashReceived,
    BigDecimal totalCashRefunded,
    BigDecimal totalNonCashReceived,
    BigDecimal closingBalance,
    int cashTransactionCount,
    int nonCashTransactionCount,
    Map<String, CashByStaff> byStaff,
    List<CashTransaction> transactions
) {
    public record CashByStaff(
        String staffName,
        BigDecimal cashCollected,
        BigDecimal nonCashCollected,
        int transactionCount
    ) {}
    
    public record CashTransaction(
        String time,
        String patientName,
        String type,
        BigDecimal amount,
        String paymentMethod,
        String collectedBy
    ) {}
}