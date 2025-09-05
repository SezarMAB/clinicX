package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WriteOffRequest(
        @NotNull @Positive BigDecimal amount,
        String reason
) {}


