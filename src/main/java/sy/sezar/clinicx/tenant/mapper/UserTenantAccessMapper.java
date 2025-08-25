package sy.sezar.clinicx.tenant.mapper;

import org.mapstruct.*;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.tenant.dto.CreateUserTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.UpdateUserTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.UserTenantAccessDto;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserTenantAccessMapper {

    /**
     * Maps UserTenantAccess entity to UserTenantAccessDto
     * Handles roles collection mapping with null safety
     */
    @Mapping(target = "tenantName", source = "tenant.name")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "copyRolesSet")
    UserTenantAccessDto toDto(UserTenantAccess entity);

    /**
     * Creates a new UserTenantAccess entity from CreateUserTenantAccessRequest
     * Manual creation due to inheritance issues with @Builder
     */
    default UserTenantAccess toEntity(CreateUserTenantAccessRequest request) {
        UserTenantAccess entity = new UserTenantAccess();
        entity.setUserId(request.getUserId());
        entity.setTenantId(request.getTenantId());
        entity.setRoles(initializeRolesSet(request.getRoles()));
        entity.setPrimary(request.isPrimary());
        entity.setActive(request.isActive());
        return entity;
    }

    /**
     * Updates existing UserTenantAccess entity from UpdateUserTenantAccessRequest
     * Properly handles roles collection update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "updateRolesSet")
    void updateFromRequest(UpdateUserTenantAccessRequest request, @MappingTarget UserTenantAccess entity);

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
     * Returns null if input is null to preserve existing roles when not updating
     * Otherwise returns a new set with the provided roles
     */
    @Named("updateRolesSet")
    default Set<StaffRole> updateRolesSet(Set<StaffRole> newRoles) {
        if (newRoles == null) {
            return null; // Don't update roles if null in update request
        }
        return new HashSet<>(newRoles);
    }

    /**
     * Before mapping callback for entity updates
     * Ensures roles collection is properly initialized before update
     */
    @BeforeMapping
    default void prepareRolesForUpdate(@MappingTarget UserTenantAccess entity) {
        if (entity.getRoles() == null) {
            entity.setRoles(new HashSet<>());
        }
    }

    /**
     * Utility method to get primary role from roles set
     * Returns the role with highest priority for compatibility with existing code
     */
    default StaffRole getPrimaryRole(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        
        // Priority order: SUPER_ADMIN > ADMIN > DOCTOR > others
        if (roles.contains(StaffRole.SUPER_ADMIN)) {
            return StaffRole.SUPER_ADMIN;
        }
        if (roles.contains(StaffRole.ADMIN)) {
            return StaffRole.ADMIN;
        }
        if (roles.contains(StaffRole.DOCTOR)) {
            return StaffRole.DOCTOR;
        }
        
        // Return the first role if no priority roles found
        return roles.iterator().next();
    }
}