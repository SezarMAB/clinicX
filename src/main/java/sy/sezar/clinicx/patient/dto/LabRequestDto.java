package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.LabRequestStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Used in the lab requests tab table showing laboratory orders.
 */
public record LabRequestDto(
    UUID labRequestId,
    String orderNumber,
    String itemDescription,
    Integer toothNumber,
    LocalDate dateSent,
    LocalDate dateDue,
    LabRequestStatus status,
    String labName
) {}
