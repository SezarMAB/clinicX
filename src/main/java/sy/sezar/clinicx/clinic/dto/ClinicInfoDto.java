package sy.sezar.clinicx.clinic.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ClinicInfoDto {
    private Boolean id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String timezone;
    private Instant createdAt;
    private Instant updatedAt;
}
