package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for insurance claim information.
 */
public record InsuranceClaimDto(
    UUID id,
    UUID patientId,
    String patientName,
    String insuranceProvider,
    String insuranceNumber,
    String claimNumber,
    LocalDate claimDate,
    LocalDate serviceDate,
    BigDecimal claimAmount,
    BigDecimal approvedAmount,
    BigDecimal paidAmount,
    String status,
    String denialReason,
    LocalDate paymentDate,
    String paymentReference
) {}