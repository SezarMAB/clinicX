package sy.sezar.clinicx.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

/**
 * Base entity class for all tenant-aware entities.
 * Provides automatic tenant filtering at the database level.
 */
@MappedSuperclass
@Getter
@Setter
@FilterDef(
    name = "tenantFilter",
    parameters = @ParamDef(name = "tenantId", type = String.class),
    defaultCondition = "tenant_id = :tenantId"
)
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public abstract class TenantAwareEntity extends BaseEntity {
    
    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;
    
    /**
     * Set the tenant ID before persisting.
     * This is called automatically by JPA.
     */
    @PrePersist
    @PreUpdate
    public void setTenantBeforeSave() {
        if (this.tenantId == null || this.tenantId.isBlank()) {
            // Get tenant from context
            String currentTenant = sy.sezar.clinicx.tenant.TenantContext.getCurrentTenant();
            if (currentTenant != null && !currentTenant.isBlank()) {
                this.tenantId = currentTenant;
            }
        }
    }
    
    /**
     * Validate tenant consistency.
     * Ensures that entities cannot be saved with incorrect tenant ID.
     */
    @PostLoad
    @PostPersist
    @PostUpdate
    public void validateTenant() {
        String currentTenant = sy.sezar.clinicx.tenant.TenantContext.getCurrentTenant();
        if (currentTenant != null && !currentTenant.isBlank() && 
            !currentTenant.equals(this.tenantId)) {
            throw new SecurityException(
                String.format("Tenant mismatch: Entity belongs to tenant '%s' but current tenant is '%s'",
                            this.tenantId, currentTenant)
            );
        }
    }
}