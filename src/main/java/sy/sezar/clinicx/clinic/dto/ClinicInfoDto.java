package sy.sezar.clinicx.clinic.dto;

import java.time.Instant;

public record ClinicInfoDto(
    Boolean id,
    String name,
    String address,
    String phoneNumber,
    String email,
    String timezone,
    Instant createdAt,
    Instant updatedAt
) {}
