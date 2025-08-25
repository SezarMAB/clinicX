package sy.sezar.clinicx.tenant.dto;

/**
 * DTO for subdomain availability check response
 */
public record SubdomainAvailabilityDto(
    String subdomain,
    boolean available,
    String message
) {}