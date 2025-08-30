package sy.sezar.clinicx.patient.dto.visit;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Visit details DTO - includes full procedure information.
 * Used for detail views where all procedure information is needed.
 * Extends the summary with complete procedure list.
 */
public record VisitDetailsDto(
    UUID id,
    UUID patientId,
    String patientName,
    UUID appointmentId,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date,
    @JsonFormat(pattern = "HH:mm")
    LocalTime time,
    UUID providerId,
    String providerName,
    String notes,
    List<ProcedureDto> procedures,
    BigDecimal totalCost,
    String overallStatus,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant updatedAt
) {
    /**
     * Calculate total cost from procedures if not provided
     */
    public BigDecimal calculateTotalCost() {
        if (procedures == null || procedures.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return procedures.stream()
                .map(p -> p.unitFee().multiply(BigDecimal.valueOf(p.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Determine overall status based on procedure statuses
     */
    public String deriveOverallStatus() {
        if (procedures == null || procedures.isEmpty()) {
            return "NO_PROCEDURES";
        }

        boolean allCompleted = procedures.stream()
                .allMatch(p -> "COMPLETED".equals(p.status()));
        if (allCompleted) {
            return "COMPLETED";
        }

        boolean anyCancelled = procedures.stream()
                .anyMatch(p -> "CANCELLED".equals(p.status()));
        if (anyCancelled) {
            return "PARTIALLY_CANCELLED";
        }

        boolean anyInProgress = procedures.stream()
                .anyMatch(p -> "IN_PROGRESS".equals(p.status()) || 
                             "SENT_TO_LAB".equals(p.status()) ||
                             "RECEIVED_FROM_LAB".equals(p.status()));
        if (anyInProgress) {
            return "IN_PROGRESS";
        }

        return "PLANNED";
    }
}