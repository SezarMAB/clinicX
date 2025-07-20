package sy.sezar.clinicx.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import sy.sezar.clinicx.patient.model.enums.TreatmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Advanced search criteria for treatments")
public record TreatmentSearchCriteria(
        
        @Schema(description = "Filter by patient ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID patientId,
        
        @Schema(description = "Filter by doctor ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID doctorId,
        
        @Schema(description = "Filter by procedure ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID procedureId,
        
        @Schema(description = "Filter by treatment status")
        List<TreatmentStatus> statuses,
        
        @Schema(description = "Filter by tooth number", example = "11")
        Integer toothNumber,
        
        @Schema(description = "Filter by tooth numbers")
        List<Integer> toothNumbers,
        
        @Schema(description = "Filter by treatment date from", example = "2024-01-01")
        LocalDate treatmentDateFrom,
        
        @Schema(description = "Filter by treatment date to", example = "2024-12-31")
        LocalDate treatmentDateTo,
        
        @Schema(description = "Minimum treatment cost", example = "100.00")
        @DecimalMin("0.0")
        BigDecimal costFrom,
        
        @Schema(description = "Maximum treatment cost", example = "500.00")
        @DecimalMin("0.0")
        BigDecimal costTo,
        
        @Schema(description = "Search in treatment notes", example = "root canal")
        String notesContain,
        
        @Schema(description = "Filter by procedure name", example = "filling")
        String procedureName,
        
        @Schema(description = "Filter by doctor name", example = "Dr. Smith")
        String doctorName,
        
        @Schema(description = "Filter by patient name", example = "John Doe")
        String patientName,
        
        @Schema(description = "Filter treatments with materials used", example = "true")
        Boolean hasMaterials,
        
        @Schema(description = "Filter by creation date from", example = "2024-01-01")
        LocalDate createdFrom,
        
        @Schema(description = "Filter by creation date to", example = "2024-12-31")
        LocalDate createdTo
) {
}