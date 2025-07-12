package sy.sezar.clinicx.patient.projection;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Projection for patient search results, displaying essential information for a list view.
 */
public interface PatientSearchResultProjection {
    UUID getId();
    String getPublicFacingId();
    String getFullName();
    LocalDate getDateOfBirth();
    String getPhoneNumber();
    String getInsuranceProvider();
}

