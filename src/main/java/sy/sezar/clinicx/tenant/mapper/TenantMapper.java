package sy.sezar.clinicx.tenant.mapper;

import org.mapstruct.*;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.model.Tenant;

/**
 * Mapper for converting between Tenant entities and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TenantMapper {

    /**
     * Converts a Tenant entity to a summary DTO.
     *
     * @param tenant The tenant entity.
     * @return The tenant summary DTO.
     */
    @Mapping(target = "currentUsers", ignore = true)
    @Mapping(target = "currentPatients", ignore = true)
    @Mapping(target = "hasAlert", expression = "java(checkHasAlert(tenant))")
    @Mapping(source = "active", target = "isActive")
    TenantSummaryDto toSummaryDto(Tenant tenant);

    /**
     * Converts a Tenant entity to a detailed DTO.
     *
     * @param tenant The tenant entity.
     * @return The tenant detail DTO.
     */
    @Mapping(target = "currentUsers", ignore = true)
    @Mapping(target = "currentPatients", ignore = true)
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TenantDetailDto toDetailDto(Tenant tenant);

    /**
     * Converts the old TenantDto to the new TenantDetailDto.
     *
     * @param dto The old tenant DTO.
     * @return The tenant detail DTO.
     */
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TenantDetailDto toDetailDto(TenantDto dto);

    /**
     * Updates a Tenant entity from an update request.
     *
     * @param request The update request.
     * @param tenant The tenant entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "subdomain", ignore = true)
    @Mapping(target = "realmName", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "subscriptionStartDate", ignore = true)
    void updateTenantFromRequest(TenantUpdateRequest request, @MappingTarget Tenant tenant);

    /**
     * Creates a new Tenant entity from a create request.
     *
     * @param request The create request.
     * @return The new tenant entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "realmName", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "subscriptionStartDate", ignore = true)
    @Mapping(target = "subscriptionEndDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Tenant toEntity(TenantCreateRequest request);

    /**
     * Creates a summary DTO with usage stats.
     *
     * @param tenant The tenant entity.
     * @param currentUsers Current user count.
     * @param currentPatients Current patient count.
     * @return The tenant summary DTO.
     */
    default TenantSummaryDto toSummaryDto(Tenant tenant, int currentUsers, int currentPatients) {
        TenantSummaryDto dto = toSummaryDto(tenant);
        return new TenantSummaryDto(
            dto.id(),
            dto.tenantId(),
            dto.name(),
            dto.subdomain(),
            dto.isActive(),
            dto.contactEmail(),
            dto.subscriptionPlan(),
            dto.subscriptionEndDate(),
            currentUsers,
            currentPatients,
            dto.createdAt(),
            dto.hasAlert()
        );
    }

    /**
     * Checks if the tenant has any alerts (e.g., expiring subscription).
     *
     * @param tenant The tenant entity.
     * @return true if the tenant has alerts, false otherwise.
     */
    default boolean checkHasAlert(Tenant tenant) {
        if (!tenant.isActive()) {
            return true;
        }

        if (tenant.getSubscriptionEndDate() != null) {
            // Alert if subscription expires within 30 days
            return tenant.getSubscriptionEndDate().isBefore(
                java.time.Instant.now().plus(30, java.time.temporal.ChronoUnit.DAYS)
            );
        }

        return false;
    }
}
