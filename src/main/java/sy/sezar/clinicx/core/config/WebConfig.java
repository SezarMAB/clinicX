package sy.sezar.clinicx.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sy.sezar.clinicx.core.converter.StringToInstantConverter;
import sy.sezar.clinicx.core.tenant.TenantInterceptor;

/**
 * Web MVC configuration.
 * Registers interceptors and configures web-related settings.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final TenantInterceptor tenantInterceptor;
    private final StringToInstantConverter stringToInstantConverter;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add tenant interceptor to all requests except static resources
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/public/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                );
    }
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Register custom converter for Instant to handle various date formats
        registry.addConverter(stringToInstantConverter);
    }
}