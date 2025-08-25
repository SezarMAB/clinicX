package sy.sezar.clinicx.patient.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Used in the patient header summary card displaying basic patient information.
 */
public record PatientSummaryDto(
    UUID id,
    String publicFacingId,
    String fullName,
    LocalDate dateOfBirth,
    Integer age,
    String gender,
    String phoneNumber,
    String email,
    String address,
    String insuranceProvider,
    String insuranceNumber,
    String importantMedicalNotes,
    BigDecimal balance,
    boolean hasAlert
) {}
