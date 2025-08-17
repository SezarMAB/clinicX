package sy.sezar.clinicx.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for the application.
 * Uses Caffeine cache for high-performance caching with TTL support.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Value("${app.security.access-cache-ttl:30}")
    private int accessCacheTtlSeconds;
    
    /**
     * Configure cache manager with specific cache settings.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure specific caches with different TTLs
        cacheManager.registerCustomCache("tenantAccess",
            Caffeine.newBuilder()
                .expireAfterWrite(accessCacheTtlSeconds, TimeUnit.SECONDS)
                .maximumSize(10000)
                .recordStats()
                .build());
        
        cacheManager.registerCustomCache("userRoles",
            Caffeine.newBuilder()
                .expireAfterWrite(accessCacheTtlSeconds, TimeUnit.SECONDS)
                .maximumSize(10000)
                .recordStats()
                .build());
        
        // Default cache configuration for other caches
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .recordStats());
        
        return cacheManager;
    }
}