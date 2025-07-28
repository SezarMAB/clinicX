package sy.sezar.clinicx.tenant.dto;

import java.time.Instant;
import java.util.UUID;

public record TenantDto(
    UUID id,
    String tenantId,
    String name,
    String subdomain,
    String realmName,
    Boolean isActive,
    String contactEmail,
    String contactPhone,
    String address,
    Instant subscriptionStartDate,
    Instant subscriptionEndDate,
    String subscriptionPlan,
    Integer maxUsers,
    Integer maxPatients,
    Integer currentUsers,
    Integer currentPatients,
    Instant createdAt,
    Instant updatedAt
) {}
