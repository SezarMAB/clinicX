package sy.sezar.clinicx.patient.dto.visit;

import com.fasterxml.jackson.annotation.JsonFormat;
import sy.sezar.clinicx.patient.model.enums.ProcedureStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Procedure DTO representing a billable clinical line item.
 * Immutable record with all procedure details including optional lab case.
 */
public record ProcedureDto(
    UUID id,
    UUID visitId,
    String code,
    String name,
    Integer toothNumber,
    List<String> surfaces,
    Integer quantity,
    BigDecimal unitFee,
    Integer durationMinutes,
    UUID performedById,
    String performedByName,
    String status,
    Boolean billable,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant startedAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant completedAt,
    LabCaseDto labCase,
    List<ProcedureMaterialDto> materials,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant updatedAt
) {
    /**
     * Calculate total fee for this procedure
     */
    public BigDecimal calculateTotalFee() {
        if (unitFee == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitFee.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Calculate total material cost
     */
    public BigDecimal calculateMaterialCost() {
        if (materials == null || materials.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return materials.stream()
                .map(ProcedureMaterialDto::totalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if procedure has lab work
     */
    public boolean hasLabWork() {
        return labCase != null;
    }

    /**
     * Check if procedure is completed
     */
    public boolean isCompleted() {
        return ProcedureStatus.COMPLETED.name().equals(status);
    }

    /**
     * Check if procedure can be modified
     */
    public boolean canModify() {
        return !ProcedureStatus.COMPLETED.name().equals(status) && 
               !ProcedureStatus.CANCELLED.name().equals(status);
    }
}