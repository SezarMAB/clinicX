package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.model.*;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;

/**
 * Centralized mapper for all Patient-related DTOs and entities.
 */
@Mapper(componentModel = "spring")
public interface PatientCentralMapper {
    // Patient <-> PatientSummaryDto
    @Mapping(target = "age", expression = "java(calculateAge(patient.getDateOfBirth()))")
    @Mapping(target = "hasAlert", ignore = true)
    PatientSummaryDto toPatientSummaryDto(Patient patient);
    List<PatientSummaryDto> toPatientSummaryDtoList(List<Patient> patients);

    // Patient <-> PatientBalanceSummaryDto
    @Mapping(target = "totalBalance", ignore = true)
    @Mapping(target = "balanceStatus", ignore = true)
    @Mapping(target = "balanceDescription", ignore = true)
    PatientBalanceSummaryDto toPatientBalanceSummaryDto(Patient patient);

    // Patient <-> PatientCreateRequest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publicFacingId", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "labRequests", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "patientTeeth", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    Patient toPatient(PatientCreateRequest request);

    // Patient <-> PatientUpdateRequest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publicFacingId", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "labRequests", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "patientTeeth", ignore = true)
    void updatePatientFromRequest(PatientUpdateRequest request, @MappingTarget Patient patient);

    default Integer calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        
        LocalDate today = LocalDate.now();
        
        // Check if birthday hasn't occurred this year yet
        if (today.getMonthValue() < dateOfBirth.getMonthValue() ||
            (today.getMonthValue() == dateOfBirth.getMonthValue() && 
             today.getDayOfMonth() < dateOfBirth.getDayOfMonth())) {
            // Birthday hasn't occurred yet this year
            return today.getYear() - dateOfBirth.getYear() - 1;
        } else {
            // Birthday has occurred this year
            return today.getYear() - dateOfBirth.getYear();
        }
    }
}
