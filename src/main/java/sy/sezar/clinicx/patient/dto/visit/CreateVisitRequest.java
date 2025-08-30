package sy.sezar.clinicx.patient.dto.visit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Request DTO for creating a new visit.
 * Creates the visit header, procedures are added separately.
 * Immutable record with comprehensive validation.
 */
public record CreateVisitRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,
    
    UUID appointmentId,
    
    @NotNull(message = "Provider ID is required")
    UUID providerId,
    
    @NotNull(message = "Visit date is required")
    @FutureOrPresent(message = "Visit date cannot be in the past")
    LocalDate date,
    
    LocalTime time,
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {
    /**
     * Validate business rules
     */
    public CreateVisitRequest {
        // Compact constructor for additional validation
        if (date != null && date.isAfter(LocalDate.now().plusMonths(6))) {
            throw new IllegalArgumentException("Visit cannot be scheduled more than 6 months in advance");
        }
    }
}