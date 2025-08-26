package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import sy.sezar.clinicx.patient.model.enums.TreatmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Request DTO for creating/updating treatment records from the treatment form.
 */
public record VisitCreateRequest(
    @NotNull
    LocalDate visitDate,

    @NotNull
    LocalTime visitTime,

    Integer toothNumber,

    @NotNull
    UUID procedureId,

    String materialUsed,

    @Positive
    Integer quantity,

    @NotNull
    @Positive
    BigDecimal cost,

    @NotNull
    TreatmentStatus status,

    @NotNull
    UUID doctorId,

    String assistantName,
    String sessionNumber,

    @Positive
    Integer durationMinutes,

    String visitNotes,
    String postVisitInstructions
) {}
