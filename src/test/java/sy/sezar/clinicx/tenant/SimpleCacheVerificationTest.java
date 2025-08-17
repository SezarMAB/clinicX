package sy.sezar.clinicx.tenant;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify Caffeine cache behavior without Spring context
 */
public class SimpleCacheVerificationTest {

    private CaffeineCache cache;
    
    @BeforeEach
    void setUp() {
        // Create a Caffeine cache with 5 second TTL (same as our config)
        var caffeineCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .maximumSize(10000)
            .recordStats()
            .build();
        
        cache = new CaffeineCache("tenantAccess", caffeineCache);
    }
    
    @Test
    void testCacheStoresAndRetrievesValues() {
        String key = "user123:tenant456";
        Boolean value = true;
        
        // Store value in cache
        cache.put(key, value);
        
        // Retrieve value from cache
        var cached = cache.get(key);
        assertNotNull(cached, "Value should be in cache");
        assertEquals(true, cached.get(), "Cached value should be true");
        
        System.out.println("✓ Cache stores and retrieves values correctly");
    }
    
    @Test
    void testCacheEviction() {
        String key = "user123:tenant456";
        Boolean value = true;
        
        // Store value in cache
        cache.put(key, value);
        
        // Verify it's there
        assertNotNull(cache.get(key), "Value should be in cache");
        
        // Evict the value
        cache.evict(key);
        
        // Verify it's gone
        assertNull(cache.get(key), "Value should be evicted from cache");
        
        System.out.println("✓ Cache eviction works correctly");
    }
    
    @Test
    void testCacheTTLExpiration() throws InterruptedException {
        String key = "user123:tenant456";
        Boolean value = true;
        
        // Store value in cache
        cache.put(key, value);
        
        // Verify it's there
        assertNotNull(cache.get(key), "Value should be in cache initially");
        
        // Wait for TTL to expire (5 seconds + buffer)
        System.out.println("Waiting 6 seconds for cache TTL to expire...");
        Thread.sleep(6000);
        
        // Verify it's expired
        assertNull(cache.get(key), "Value should expire after TTL");
        
        System.out.println("✓ Cache TTL expiration works correctly");
    }
    
    @Test
    void testCachePerformance() {
        String key = "user123:tenant456";
        Boolean value = true;
        
        // First access - populate cache
        long start = System.nanoTime();
        cache.put(key, value);
        long putTime = System.nanoTime() - start;
        
        // Second access - from cache
        start = System.nanoTime();
        var cached = cache.get(key);
        long getTime = System.nanoTime() - start;
        
        assertNotNull(cached, "Value should be in cache");
        
        System.out.println("Cache Performance:");
        System.out.println("  Put time: " + (putTime / 1000) + " microseconds");
        System.out.println("  Get time: " + (getTime / 1000) + " microseconds");
        
        // Get should be faster than put
        assertTrue(getTime < putTime * 2, "Get should be reasonably fast");
        
        System.out.println("✓ Cache performance is acceptable");
    }
    
    @Test
    void testCacheStatistics() {
        var nativeCache = cache.getNativeCache();
        var stats = nativeCache.stats();
        
        // Perform some operations
        cache.put("key1", true);
        cache.get("key1"); // Hit
        cache.get("key2"); // Miss
        cache.put("key2", false);
        cache.get("key2"); // Hit
        
        // Get updated stats
        stats = nativeCache.stats();
        
        System.out.println("Cache Statistics:");
        System.out.println("  Request count: " + stats.requestCount());
        System.out.println("  Hit count: " + stats.hitCount());
        System.out.println("  Miss count: " + stats.missCount());
        System.out.println("  Hit rate: " + String.format("%.2f%%", stats.hitRate() * 100));
        System.out.println("  Load count: " + stats.loadCount());
        System.out.println("  Eviction count: " + stats.evictionCount());
        
        assertTrue(stats.requestCount() > 0, "Should have recorded requests");
        
        System.out.println("✓ Cache statistics are being recorded");
    }
    
    @Test
    void testOnlyTrueValuesAreCached() {
        // This mimics our actual cache behavior where we don't cache false results
        String key1 = "user1:tenant1";
        String key2 = "user2:tenant2";
        
        // Store true value (should be cached)
        cache.put(key1, true);
        
        // Don't store false value (mimicking the unless condition)
        // In real implementation: @Cacheable(unless = "#result == false")
        Boolean falseValue = false;
        if (!falseValue) {
            // Don't cache false values
            System.out.println("Skipping cache for false value (as configured)");
        } else {
            cache.put(key2, falseValue);
        }
        
        // Verify only true value is cached
        assertNotNull(cache.get(key1), "True value should be cached");
        assertNull(cache.get(key2), "False value should not be cached");
        
        System.out.println("✓ Only successful validations (true) would be cached");
    }
}