package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for patient financial statement.
 */
public record PatientStatementDto(
    UUID patientId,
    String patientName,
    String patientPublicId,
    String address,
    String phoneNumber,
    String email,
    LocalDate statementDate,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal openingBalance,
    BigDecimal totalCharges,
    BigDecimal totalPayments,
    BigDecimal totalRefunds,
    BigDecimal closingBalance,
    List<StatementLine> transactions
) {
    public record StatementLine(
        LocalDate date,
        String type,
        String description,
        String referenceNumber,
        BigDecimal charges,
        BigDecimal payments,
        BigDecimal balance
    ) {}
}