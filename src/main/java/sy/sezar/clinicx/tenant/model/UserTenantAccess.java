package sy.sezar.clinicx.tenant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import sy.sezar.clinicx.core.model.BaseEntity;

@Entity
@Table(name = "user_tenant_access", 
       schema = "public",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "tenant_id"})
       })
@Getter
@Setter
@Builder
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

    @NotNull
    @Size(max = 50)
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean isPrimary = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", 
                insertable = false, updatable = false)
    private Tenant tenant;
}