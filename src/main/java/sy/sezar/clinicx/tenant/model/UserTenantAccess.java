package sy.sezar.clinicx.tenant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_tenant_access", 
       schema = "public",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "tenant_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTenantAccess extends BaseEntity {

    @NotNull
    @Size(max = 255)
    @Column(name = "user_id", nullable = false, length = 255)
    private String userId; // Keycloak user ID

    @NotNull
    @Size(max = 255)
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;

    @ElementCollection(targetClass = StaffRole.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_tenant_access_roles", joinColumns = @JoinColumn(name = "user_tenant_access_id"))
    @Column(name = "role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Set<StaffRole> roles = new HashSet<>();

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", 
                insertable = false, updatable = false)
    private Tenant tenant;
}