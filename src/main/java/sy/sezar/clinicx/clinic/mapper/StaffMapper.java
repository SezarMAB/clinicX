package sy.sezar.clinicx.clinic.mapper;

import org.mapstruct.*;
import sy.sezar.clinicx.clinic.dto.StaffCreateRequest;
import sy.sezar.clinicx.clinic.dto.StaffDto;
import sy.sezar.clinicx.clinic.dto.StaffUpdateRequest;
import sy.sezar.clinicx.clinic.dto.StaffWithAccessDto;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", 
        uses = {SpecialtyMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StaffMapper {

    /**
     * Maps Staff entity to StaffDto
     * Handles roles collection mapping with null safety
     */
    @Mapping(target = "accessRole", ignore = true)
    @Mapping(target = "isPrimary", ignore = true)
    @Mapping(target = "accessActive", ignore = true)
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "copyRolesSet")
    StaffDto toDto(Staff staff);

    /**
     * Maps Staff entity to StaffWithAccessDto
     * This mapping assumes access fields will be populated separately
     */
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "copyRolesSet")
    @Mapping(target = "accessRole", ignore = true)
    @Mapping(target = "isPrimary", ignore = true)
    @Mapping(target = "accessActive", ignore = true)
    StaffWithAccessDto toWithAccessDto(Staff staff);

    /**
     * Maps StaffCreateRequest to Staff entity
     * Initializes roles collection properly
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "initializeRolesSet")
    Staff toEntity(StaffCreateRequest request);

    /**
     * Updates existing Staff entity from StaffUpdateRequest
     * Properly handles roles collection update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "updateRolesSet")
    void updateFromRequest(StaffUpdateRequest request, @MappingTarget Staff staff);

    /**
     * Custom mapping method to safely copy roles set
     * Handles null input by returning empty set
     */
    @Named("copyRolesSet")
    default Set<StaffRole> copyRolesSet(Set<StaffRole> roles) {
        if (roles == null) {
            return new HashSet<>();
        }
        return new HashSet<>(roles);
    }

    /**
     * Custom mapping method to initialize roles set for new entities
     * Ensures non-null collection with proper initialization
     */
    @Named("initializeRolesSet")
    default Set<StaffRole> initializeRolesSet(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(roles);
    }

    /**
     * Custom mapping method to update roles set in existing entities
     * Clears existing roles and adds new ones to maintain collection integrity
     */
    @Named("updateRolesSet")
    default Set<StaffRole> updateRolesSet(Set<StaffRole> newRoles) {
        if (newRoles == null) {
            return new HashSet<>();
        }
        return new HashSet<>(newRoles);
    }

    /**
     * Before mapping callback for entity updates
     * Ensures roles collection is properly initialized before update
     */
    @BeforeMapping
    default void prepareRolesForUpdate(@MappingTarget Staff staff) {
        if (staff.getRoles() == null) {
            staff.setRoles(new HashSet<>());
        }
    }
}
