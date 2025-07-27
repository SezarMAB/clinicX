package sy.sezar.clinicx.core.tenant;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * Test implementation of TenantContext for unit tests.
 */
@Component
public class TestTenantContext {
    
    @PostConstruct
    public void init() {
        // Initialize TenantContext with test tenant
        TenantContext.setCurrentTenant("test-tenant");
    }
}