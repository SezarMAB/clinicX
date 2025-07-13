package sy.sezar.clinicx.staff.dto;

import lombok.Data;
import sy.sezar.clinicx.staff.model.enums.StaffRole;

import java.util.Set;
import java.util.UUID;

@Data
public class StaffSearchCriteria {
    private String searchTerm;
    private StaffRole role;
    private Set<UUID> specialtyIds;
    private Boolean isActive;
}
