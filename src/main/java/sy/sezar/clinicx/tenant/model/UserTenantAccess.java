package sy.sezar.clinicx.tenant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;

/**
 * Entity representing user access to multiple tenants.
 * Allows users to belong to multiple clinics with different roles.
 */
@Entity
@Table(name = "user_tenant_access", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tenant_id"}))
@Getter
@Setter
public class UserTenantAccess extends BaseEntity {

    @NotNull
    @Size(max = 255)
    @Column(name = "user_id", nullable = false, length = 255)
    private String userId; // Keycloak user ID

    @NotNull
    @Size(max = 255)
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;

    @NotNull
    @Size(max = 50)
    @Column(name = "role", nullable = false, length = 50)
    private String role; // Role in this specific tenant

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", 
                insertable = false, updatable = false)
    private Tenant tenant;
}