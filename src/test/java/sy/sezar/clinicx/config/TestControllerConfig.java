package sy.sezar.clinicx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Test configuration for controllers.
 */
@TestConfiguration
@ComponentScan(
    basePackages = {"sy.sezar.clinicx.auth.controller", "sy.sezar.clinicx.core.security", "sy.sezar.clinicx.core.tenant"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Config$")
)
public class TestControllerConfig {
}