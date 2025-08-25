package sy.sezar.clinicx.clinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.util.HashSet;
import java.util.Set;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

@Entity
@Table(name = "staff")
@Getter
@Setter
@Slf4j
public class Staff extends BaseEntity {

    @NotNull
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @ElementCollection(targetClass = StaffRole.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "staff_roles", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Set<StaffRole> roles = new HashSet<>();

    @Email
    @NotNull
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 30)
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Link to Keycloak user (loose coupling with user_tenant_access)
    @Size(max = 255)
    @Column(name = "keycloak_user_id", length = 255)
    private String keycloakUserId;

    // Tenant context - will be removed in schema-per-tenant
    @Size(max = 255)
    @Column(name = "tenant_id", length = 255)
    private String tenantId;

    // Source realm - tracks the Keycloak realm where the user originally exists
    // This is crucial for cross-realm access scenarios where users from one realm
    // can access tenants in another realm
    @Size(max = 255)
    @Column(name = "source_realm", length = 255)
    private String sourceRealm;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "staff_specialties",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private Set<Specialty> specialties = new HashSet<>();
}

