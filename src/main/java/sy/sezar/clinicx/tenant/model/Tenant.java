package sy.sezar.clinicx.tenant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.time.Instant;

@Entity
@Table(name = "tenants")
@Getter
@Setter
public class Tenant extends BaseEntity {

    @NotNull
    @Size(max = 100)
    @Column(name = "tenant_id", nullable = false, unique = true, length = 100)
    private String tenantId;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
    @Column(name = "subdomain", nullable = false, unique = true, length = 50)
    private String subdomain;

    @NotNull
    @Size(max = 100)
    @Column(name = "realm_name", nullable = false, unique = true, length = 100)
    private String realmName;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Email
    @Size(max = 255)
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Size(max = 50)
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "address")
    private String address;

    @Column(name = "subscription_start_date")
    private Instant subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private Instant subscriptionEndDate;

    @Size(max = 50)
    @Column(name = "subscription_plan", length = 50)
    private String subscriptionPlan;

    @Column(name = "max_users")
    private Integer maxUsers = 10;

    @Column(name = "max_patients")
    private Integer maxPatients = 1000;

    @NotNull
    @Size(max = 50)
    @Column(name = "specialty", nullable = false, length = 50)
    private String specialty = "CLINIC";

}
