package sy.sezar.clinicx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import sy.sezar.clinicx.core.tenant.TenantResolver;

/**
 * Test configuration for web tests.
 * Provides mock implementations of required beans.
 */
@TestConfiguration
@Profile("test")
public class TestWebConfig {

    @Bean
    public TenantResolver tenantResolver() {
        return new TenantResolver() {
            @Override
            public String resolveTenant() {
                return "test-tenant";
            }

            @Override
            public boolean isMultiTenant() {
                return false;
            }
        };
    }
}