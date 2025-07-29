package sy.sezar.clinicx.core.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Maps the `cors` section from application.yml.
 */
@Component
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsProperties {

    private List<String> allowedOrigins     = new ArrayList<>();
    private List<String> allowedMethods     = new ArrayList<>();
    private List<String> allowedHeaders     = new ArrayList<>();
    private List<String> exposedHeaders     = new ArrayList<>();
    private boolean      allowCredentials   = true;

}
