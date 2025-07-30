package sy.sezar.clinicx.tenant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for specialty types.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "specialty")
public class SpecialtyTypeConfiguration {
    
    private List<SpecialtyTypeConfig> types = new ArrayList<>();
    
    @Data
    public static class SpecialtyTypeConfig {
        private String code;
        private String name;
        private String realmName;
        private List<String> features = new ArrayList<>();
    }
}