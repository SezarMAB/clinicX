package sy.sezar.clinicx.tenant.filter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.TenantContext;

/**
 * Hibernate filter configuration for tenant-based data isolation.
 * This component enables Hibernate filters on a per-request basis to ensure
 * that all database queries are automatically filtered by the current tenant.
 */
@Slf4j
@Component
public class HibernateTenantFilter implements ServletRequestListener {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.multi-tenant.enabled:true}")
    private boolean multiTenantEnabled;

    @Value("${app.multi-tenant.filter.enabled:true}")
    private boolean tenantFilterEnabled;

    /**
     * Enable tenant filter for the current request.
     * This method is called at the beginning of each request.
     */
    public void enableTenantFilter() {
        if (!multiTenantEnabled || !tenantFilterEnabled) {
            return;
        }

        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null || tenantId.isBlank()) {
            log.debug("No tenant context set, skipping Hibernate filter");
            return;
        }

        try {
            Session session = entityManager.unwrap(Session.class);
            if (session != null) {
                // Enable the tenant filter
                org.hibernate.Filter filter = session.enableFilter("tenantFilter");
                filter.setParameter("tenantId", tenantId);

                log.debug("Enabled Hibernate tenant filter for tenant: {}", tenantId);
            }
        } catch (Exception e) {
            log.error("Failed to enable Hibernate tenant filter", e);
        }
    }

    /**
     * Disable tenant filter after request completion.
     * This method is called at the end of each request.
     */
    public void disableTenantFilter() {
        if (!multiTenantEnabled || !tenantFilterEnabled) {
            return;
        }

        try {
            Session session = entityManager.unwrap(Session.class);
            if (session != null) {
                session.disableFilter("tenantFilter");
                log.debug("Disabled Hibernate tenant filter");
            }
        } catch (Exception e) {
            log.error("Failed to disable Hibernate tenant filter", e);
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        enableTenantFilter();
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        disableTenantFilter();
    }

    /**
     * Utility method to check if tenant filter is currently active.
     */
    public boolean isFilterActive() {
        try {
            Session session = entityManager.unwrap(Session.class);
            if (session != null) {
                org.hibernate.Filter filter = session.getEnabledFilter("tenantFilter");
                return filter != null;
            }
        } catch (Exception e) {
            log.error("Failed to check filter status", e);
        }
        return false;
    }

    /**
     * Get the current tenant ID from the active filter.
     */
    public String getFilteredTenantId() {
        // Return the current tenant from context since Hibernate Filter
        // doesn't expose parameter values after being set
        return TenantContext.getCurrentTenant();
    }
}
