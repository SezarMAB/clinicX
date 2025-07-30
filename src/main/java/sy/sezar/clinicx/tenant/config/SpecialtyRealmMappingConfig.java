package sy.sezar.clinicx.tenant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for specialty-realm mapping behavior.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.tenant.specialty-realm-mapping")
public class SpecialtyRealmMappingConfig {
    
    /**
     * Whether specialty-realm mapping is enabled.
     * When true, tenants are grouped by specialty in shared realms.
     */
    private boolean enabled = true;
    
    /**
     * Whether to automatically create realms for new specialties.
     * When true, the first tenant of a new specialty triggers realm creation.
     */
    private boolean autoCreateRealm = true;
    
    /**
     * Default features that all specialties should have.
     * These are added to every specialty's feature list.
     */
    private List<String> defaultFeatures = new ArrayList<>();
}