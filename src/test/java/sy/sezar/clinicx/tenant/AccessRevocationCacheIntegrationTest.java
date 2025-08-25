package sy.sezar.clinicx.tenant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;
import sy.sezar.clinicx.tenant.service.impl.TenantAccessValidatorImpl;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.UserTenantAccessRepository;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify cache behavior during access revocation
 */
@SpringBootTest
@TestPropertySource(properties = {
    "app.security.strict-tenant-validation=true",
    "app.security.access-cache-ttl=5", // Short TTL for testing
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class AccessRevocationCacheIntegrationTest {

    @Autowired
    private TenantAccessValidator tenantAccessValidator;
    
    @Autowired
    private TenantAccessValidatorImpl tenantAccessValidatorImpl;
    
    @Autowired
    private UserTenantAccessRepository userTenantAccessRepository;
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private CacheManager cacheManager;
    
    private String testUserId;
    private String testTenantId;
    
    @BeforeEach
    void setUp() {
        // Clear cache before each test
        cacheManager.getCache("tenantAccess").clear();
        cacheManager.getCache("userRoles").clear();
        
        testUserId = UUID.randomUUID().toString();
        testTenantId = "test-tenant-" + UUID.randomUUID();
    }
    
    @Test
    void testCacheEvictionOnAccessRevocation() {
        // Step 1: Create active access
        createActiveAccess();
        
        // Step 2: First validation should hit database and cache result
        long startTime = System.currentTimeMillis();
        boolean hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        assertTrue(hasAccess, "User should have access initially");
        System.out.println("First call (DB hit) took: " + firstCallTime + "ms");
        
        // Step 3: Second validation should use cache (much faster)
        startTime = System.currentTimeMillis();
        hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        assertTrue(hasAccess, "User should still have access");
        System.out.println("Second call (Cache hit) took: " + secondCallTime + "ms");
        
        // Cache should be significantly faster
        assertTrue(secondCallTime < firstCallTime / 2, 
            "Cached call should be at least 2x faster than DB call");
        
        // Step 4: Revoke access
        revokeAccess();
        
        // Step 5: Evict cache
        tenantAccessValidatorImpl.evictAccessCache(testUserId, testTenantId);
        
        // Step 6: Next validation should hit database and return false
        startTime = System.currentTimeMillis();
        hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        long afterRevocationTime = System.currentTimeMillis() - startTime;
        
        assertFalse(hasAccess, "User should NOT have access after revocation");
        System.out.println("After revocation (DB hit) took: " + afterRevocationTime + "ms");
        
        // Should hit DB again (not use stale cache)
        assertTrue(afterRevocationTime > secondCallTime, 
            "Should hit database after cache eviction");
    }
    
    @Test
    void testCacheTTLExpiration() throws InterruptedException {
        // Create active access
        createActiveAccess();
        
        // First call - caches the result
        boolean hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        assertTrue(hasAccess, "Should have access initially");
        
        // Revoke access but DON'T evict cache
        revokeAccess();
        
        // Immediate check - should still return true (stale cache)
        hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        assertTrue(hasAccess, "Should still return true due to cached value");
        
        // Wait for cache TTL to expire (5 seconds + buffer)
        System.out.println("Waiting for cache TTL to expire (6 seconds)...");
        Thread.sleep(6000);
        
        // After TTL - should hit database and return false
        hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        assertFalse(hasAccess, "Should return false after cache TTL expires");
    }
    
    @Test
    void testOnlySuccessfulValidationsAreCached() {
        // No access exists - should return false
        boolean hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        assertFalse(hasAccess, "Should not have access when no records exist");
        
        // Create access
        createActiveAccess();
        
        // Next call should hit database (failed validations aren't cached)
        long startTime = System.currentTimeMillis();
        hasAccess = tenantAccessValidatorImpl.checkDatabaseAccess(testUserId, testTenantId);
        long dbHitTime = System.currentTimeMillis() - startTime;
        
        assertTrue(hasAccess, "Should have access after creation");
        assertTrue(dbHitTime > 0, "Should take time to hit database");
        
        System.out.println("Confirmed: Failed validations are not cached");
    }
    
    private void createActiveAccess() {
        // Create UserTenantAccess
        UserTenantAccess access = new UserTenantAccess();
        access.setUserId(testUserId);
        access.setTenantId(testTenantId);
        access.setRoles(Set.of(StaffRole.ADMIN));
        access.setActive(true);
        access.setPrimary(false);
        userTenantAccessRepository.save(access);
        
        // Create Staff record
        Staff staff = new Staff();
        staff.setKeycloakUserId(testUserId);
        staff.setTenantId(testTenantId);
        staff.setFullName("Test User");
        staff.setEmail("test@example.com");
        staff.setRoles(Set.of(StaffRole.ADMIN));
        staff.setActive(true);
        staffRepository.save(staff);
    }
    
    private void revokeAccess() {
        // Deactivate UserTenantAccess
        userTenantAccessRepository.findByUserIdAndTenantId(testUserId, testTenantId)
            .ifPresent(access -> {
                access.setActive(false);
                userTenantAccessRepository.save(access);
            });
        
        // Deactivate Staff
        staffRepository.findByKeycloakUserIdAndTenantId(testUserId, testTenantId)
            .ifPresent(staff -> {
                staff.setActive(false);
                staffRepository.save(staff);
            });
    }
}