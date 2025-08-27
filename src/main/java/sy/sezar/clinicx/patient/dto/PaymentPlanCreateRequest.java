package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new payment plan.
 */
public record PaymentPlanCreateRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,

    @NotNull(message = "Plan name is required")
    @Size(max = 100, message = "Plan name cannot exceed 100 characters")
    String planName,

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    BigDecimal totalAmount,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @Positive(message = "Installment count must be positive")
    Integer installmentCount,

    // For fixed amount installments
    BigDecimal installmentAmount,

    // For variable amount installments
    List<BigDecimal> variableInstallmentAmounts,

    // For custom due dates
    List<LocalDate> customDueDates,

    Integer frequencyDays,

    @Size(max = 500) String notes
) {}
