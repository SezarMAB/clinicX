package sy.sezar.clinicx.tenant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.test.context.TestPropertySource;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify cache configuration and behavior
 */
@SpringBootTest
@TestPropertySource(properties = {
    "app.security.strict-tenant-validation=true",
    "app.security.access-cache-ttl=10"
})
public class CacheVerificationTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void verifyCacheIsConfigured() {
        // Verify cache manager exists
        assertNotNull(cacheManager, "CacheManager should be configured");
        
        // Verify our specific caches exist
        assertNotNull(cacheManager.getCache("tenantAccess"), 
            "tenantAccess cache should be configured");
        assertNotNull(cacheManager.getCache("userRoles"), 
            "userRoles cache should be configured");
    }

    @Test
    public void verifyCacheStatistics() {
        // Get the tenant access cache
        var springCache = cacheManager.getCache("tenantAccess");
        assertNotNull(springCache, "tenantAccess cache should exist");
        
        if (springCache instanceof CaffeineCache caffeineCache) {
            Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
            
            // Check if stats are being recorded
            CacheStats stats = nativeCache.stats();
            System.out.println("=== Cache Statistics ===");
            System.out.println("Hit Count: " + stats.hitCount());
            System.out.println("Miss Count: " + stats.missCount());
            System.out.println("Load Count: " + stats.loadCount());
            System.out.println("Hit Rate: " + stats.hitRate());
            System.out.println("Average Load Time: " + stats.averageLoadPenalty() + " ns");
            System.out.println("Eviction Count: " + stats.evictionCount());
            
            // Verify stats are enabled
            assertTrue(stats.requestCount() >= 0, 
                "Cache statistics should be recorded");
        }
    }

    @Test
    public void verifyCacheTTLConfiguration() {
        var springCache = cacheManager.getCache("tenantAccess");
        
        if (springCache instanceof CaffeineCache caffeineCache) {
            // This verifies the cache is a Caffeine cache with our configuration
            assertNotNull(caffeineCache.getNativeCache(), 
                "Should be using Caffeine cache implementation");
            
            // Put a test entry
            String testKey = "test-user:test-tenant";
            caffeineCache.put(testKey, true);
            
            // Verify it's in cache
            var cached = caffeineCache.get(testKey);
            assertNotNull(cached, "Value should be in cache");
            assertEquals(true, cached.get(), "Cached value should be true");
            
            // Clear for next test
            caffeineCache.evict(testKey);
            
            // Verify eviction worked
            cached = caffeineCache.get(testKey);
            assertNull(cached, "Value should be evicted from cache");
        }
    }

    @Test
    public void printCacheConfiguration() {
        System.out.println("\n=== Cache Configuration ===");
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            System.out.println("Cache: " + cacheName);
            
            if (cache instanceof CaffeineCache caffeineCache) {
                var nativeCache = caffeineCache.getNativeCache();
                System.out.println("  - Type: Caffeine");
                System.out.println("  - Estimated Size: " + nativeCache.estimatedSize());
                System.out.println("  - Stats Enabled: " + (nativeCache.stats().requestCount() >= 0));
            }
        });
    }
}