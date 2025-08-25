package sy.sezar.clinicx.clinic.dto;

import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;
import java.util.UUID;

public record StaffSearchCriteria(
    String searchTerm,
    StaffRole role,
    Set<UUID> specialtyIds,
    Boolean isActive
) {}
