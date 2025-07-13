package sy.sezar.clinicx.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Advanced search criteria for patients")
public record PatientSearchCriteria(
        
        @Schema(description = "General search term (name, ID, phone, email)", example = "John Doe")
        String searchTerm,
        
        @Schema(description = "Filter by patient name", example = "John")
        String name,
        
        @Schema(description = "Filter by public facing ID", example = "P001")
        String publicFacingId,
        
        @Schema(description = "Filter by phone number", example = "123456789")
        String phoneNumber,
        
        @Schema(description = "Filter by email address", example = "john@example.com")
        String email,
        
        @Schema(description = "Filter by gender", example = "Male")
        String gender,
        
        @Schema(description = "Filter by insurance provider", example = "BlueCross")
        String insuranceProvider,
        
        @Schema(description = "Filter by insurance number", example = "INS123456")
        String insuranceNumber,
        
        @Schema(description = "Filter by birth date from (inclusive)", example = "1980-01-01")
        LocalDate dateOfBirthFrom,
        
        @Schema(description = "Filter by birth date to (inclusive)", example = "1990-12-31")
        LocalDate dateOfBirthTo,
        
        @Schema(description = "Minimum age", example = "18")
        @Min(0)
        Integer ageFrom,
        
        @Schema(description = "Maximum age", example = "65")
        @Min(0)
        Integer ageTo,
        
        @Schema(description = "Minimum balance", example = "0.00")
        @DecimalMin("0.0")
        BigDecimal balanceFrom,
        
        @Schema(description = "Maximum balance", example = "1000.00")
        @DecimalMin("0.0")
        BigDecimal balanceTo,
        
        @Schema(description = "Filter by active status", example = "true")
        Boolean isActive,
        
        @Schema(description = "Filter patients with medical notes", example = "true")
        Boolean hasMedicalNotes,
        
        @Schema(description = "Filter patients with outstanding balance", example = "true")
        Boolean hasOutstandingBalance,
        
        @Schema(description = "Filter by creation date from", example = "2024-01-01")
        LocalDate createdFrom,
        
        @Schema(description = "Filter by creation date to", example = "2024-12-31")
        LocalDate createdTo,
        
        @Schema(description = "Filter patients who had appointments", example = "true")
        Boolean hasAppointments,
        
        @Schema(description = "Filter patients who had treatments", example = "true")
        Boolean hasTreatments,
        
        @Schema(description = "Filter by city or address", example = "New York")
        String address
) {
}