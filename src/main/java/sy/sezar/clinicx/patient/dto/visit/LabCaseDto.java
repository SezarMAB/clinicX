package sy.sezar.clinicx.patient.dto.visit;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Lab case DTO for tracking external dental lab work.
 * Immutable record with lab work details and tracking information.
 */
public record LabCaseDto(
    UUID id,
    UUID procedureId,
    String labName,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate sentDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dueDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate receivedDate,
    String trackingNumber,
    String status,
    String technicianName,
    String shade,
    String materialType,
    String notes
) {
    /**
     * Check if lab case is overdue
     */
    public boolean isOverdue() {
        if (receivedDate != null || dueDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Check if lab case has been received
     */
    public boolean isReceived() {
        return receivedDate != null;
    }

    /**
     * Calculate days until due or overdue
     */
    public Long daysUntilDue() {
        if (receivedDate != null || dueDate == null) {
            return null;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        return days;
    }

    /**
     * Get status display text with overdue indicator
     */
    public String getStatusDisplay() {
        if (isOverdue()) {
            return status + " (OVERDUE)";
        }
        return status;
    }
}