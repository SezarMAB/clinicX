package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentAllocationItem(
        @NotNull UUID invoiceId,
        @NotNull @Positive BigDecimal amount
) {}


