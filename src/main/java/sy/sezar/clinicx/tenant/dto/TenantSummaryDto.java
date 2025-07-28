package sy.sezar.clinicx.tenant.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Summary DTO for tenant listing views
 */
public record TenantSummaryDto(
    UUID id,
    String tenantId,
    String name,
    String subdomain,
    Boolean isActive,
    String contactEmail,
    String subscriptionPlan,
    Instant subscriptionEndDate,
    Integer currentUsers,
    Integer currentPatients,
    boolean hasAlert
) {}