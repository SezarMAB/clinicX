package sy.sezar.clinicx.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import sy.sezar.clinicx.patient.model.enums.AppointmentStatus;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Request to create a new appointment")
public record AppointmentCreateRequest(
        
        @NotNull
        @Schema(description = "Specialty ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        UUID specialtyId,
        
        @NotNull
        @Schema(description = "Patient ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        UUID patientId,
        
        @Schema(description = "Doctor ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID doctorId,
        
        @NotNull
        @Schema(description = "Appointment date and time", example = "2024-07-15T10:30:00Z", required = true)
        Instant appointmentDatetime,
        
        @NotNull
        @Positive
        @Schema(description = "Duration in minutes", example = "30", required = true)
        Integer durationMinutes,
        
        @Schema(description = "Appointment status", example = "SCHEDULED")
        AppointmentStatus status,
        
        @Schema(description = "Additional notes", example = "Regular checkup")
        String notes,
        
        @Schema(description = "Staff member who created the appointment")
        UUID createdById
) {
}