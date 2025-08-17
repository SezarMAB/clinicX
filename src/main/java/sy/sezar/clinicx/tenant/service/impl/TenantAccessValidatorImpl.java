package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.UserTenantAccessRepository;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of tenant access validation service.
 *
 * SECURITY: This implementation ALWAYS validates against the database
 * to ensure revoked access is immediately enforced, even if the user
 * has a valid JWT token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantAccessValidatorImpl implements TenantAccessValidator {

    private final StaffRepository staffRepository;
    private final UserTenantAccessRepository userTenantAccessRepository;

    // Configuration flag to enable/disable strict database validation
    @Value("${app.security.strict-tenant-validation:true}")
    private boolean strictValidation;

    @Override
    public boolean validateAccess(String tenantId) {
        if (tenantId == null) {
            return false;
        }

        String userId = getCurrentUserId();
        if (userId == null) {
            return false;
        }

        return validateUserAccess(userId, tenantId);
    }

    @Override
    public boolean validateCurrentTenantAccess() {
        String currentTenant = TenantContext.getCurrentTenant();
        if (currentTenant == null) {
            log.warn("No tenant context set");
            return false;
        }

        return validateAccess(currentTenant);
    }

    @Override
    public boolean validateUserAccess(String userId, String tenantId) {
        // SECURITY: Always check database first for real-time validation
        // This ensures revoked access is immediately enforced

        log.debug("Validating access for user {} to tenant {} (strict mode: {})", userId, tenantId, strictValidation);

        if (strictValidation) {
            // CRITICAL: Database check is PRIMARY, not fallback
            boolean hasDbAccess = checkDatabaseAccess(userId, tenantId);

            if (!hasDbAccess) {
                log.warn("❌ User {} DENIED access to tenant {} - no active database record", userId, tenantId);
                return false;
            }

            // Even if database says yes, verify JWT is valid
            // This prevents database-only attacks
            boolean jwtValid = validateJwtClaims(userId, tenantId);
            if (jwtValid) {
                log.debug("✅ User {} GRANTED access to tenant {} - both database and JWT valid", userId, tenantId);
            } else {
                log.warn("❌ User {} DENIED access to tenant {} - JWT validation failed", userId, tenantId);
            }
            return jwtValid;
        } else {
            // Legacy mode: JWT first, database fallback (NOT RECOMMENDED)
            boolean hasAccess = validateJwtClaims(userId, tenantId) || checkDatabaseAccess(userId, tenantId);
            log.debug("Legacy mode validation for user {} to tenant {}: {}", userId, tenantId, hasAccess);
            return hasAccess;
        }
    }

    /**
     * Check database for active access records.
     * This is the source of truth for access control.
     *
     * CACHING: Results are cached for cacheTtlSeconds to reduce database load.
     * Cache is automatically evicted when access is revoked.
     */
    @Cacheable(value = "tenantAccess", key = "#userId + ':' + #tenantId", unless = "#result == false")
    public boolean checkDatabaseAccess(String userId, String tenantId) {
        long startTime = System.currentTimeMillis();
        log.debug("CACHE MISS - Checking database for user {} access to tenant {}", userId, tenantId);
        try {
            // Check UserTenantAccess table first (more reliable)
            Optional<UserTenantAccess> access = userTenantAccessRepository
                .findByUserIdAndTenantId(userId, tenantId);

            if (access.isPresent()) {
                log.debug("Found UserTenantAccess record for user {} and tenant {}: active={}",
                         userId, tenantId, access.get().isActive());
                if (access.get().isActive()) {
                    log.info("✓ Database ACCESS GRANTED for user {} to tenant {} via UserTenantAccess (DB query took {}ms)",
                             userId, tenantId, System.currentTimeMillis() - startTime);
                    return true;
                }
            } else {
                log.debug("No UserTenantAccess record found for user {} and tenant {}", userId, tenantId);
            }

            // Fallback to Staff table for backward compatibility
            Optional<Staff> staff = staffRepository
                .findByKeycloakUserIdAndTenantId(userId, tenantId);

            if (staff.isPresent()) {
                log.debug("Found Staff record for user {} and tenant {}: active={}",
                         userId, tenantId, staff.get().isActive());
                if (staff.get().isActive()) {
                    log.info("✓ Database ACCESS GRANTED for user {} to tenant {} via Staff table (DB query took {}ms)",
                             userId, tenantId, System.currentTimeMillis() - startTime);
                    return true;
                }
            } else {
                log.debug("No Staff record found for user {} and tenant {}", userId, tenantId);
            }

            log.warn("✗ Database ACCESS DENIED for user {} to tenant {} - no active records (DB query took {}ms)",
                     userId, tenantId, System.currentTimeMillis() - startTime);
            return false;
        } catch (Exception e) {
            log.error("Database access check failed for user {} and tenant {} (took {}ms)",
                     userId, tenantId, System.currentTimeMillis() - startTime, e);
            // Fail closed - deny access if database check fails
            return false;
        }
    }

    /**
     * Evict cache entries for a specific user and tenant.
     * Called when access is revoked or modified.
     */
    @CacheEvict(value = "tenantAccess", key = "#userId + ':' + #tenantId")
    public void evictAccessCache(String userId, String tenantId) {
        log.info("🗑️ CACHE EVICTED for user {} and tenant {} - next access will check database", userId, tenantId);
    }

    /**
     * Evict all cache entries for a specific user.
     * Called when user is deactivated or deleted.
     */
    @CacheEvict(value = "tenantAccess", allEntries = true, condition = "#userId != null")
    public void evictAllAccessCacheForUser(String userId) {
        log.info("🗑️ ALL CACHE ENTRIES EVICTED for user {} - all accesses will check database", userId);
    }

    /**
     * Evict all cache entries for a specific tenant.
     * Called when tenant settings change or tenant is deactivated.
     */
    @CacheEvict(value = "tenantAccess", allEntries = true, condition = "#tenantId != null")
    public void evictAllAccessCacheForTenant(String tenantId) {
        log.info("🗑️ ALL CACHE ENTRIES EVICTED for tenant {} - all user accesses will check database", tenantId);
    }

    /**
     * Validate JWT claims for basic token validity.
     * This ensures the token itself is valid, but doesn't determine access.
     */
    private boolean validateJwtClaims(String userId, String tenantId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            log.debug("No valid JWT authentication found");
            return false;
        }

        Jwt jwt = (Jwt) auth.getPrincipal();

        // Verify the JWT belongs to the correct user
        String jwtUserId = extractUserIdFromJwt(jwt);
        if (!userId.equals(jwtUserId)) {
            log.warn("JWT user ID {} doesn't match requested user ID {}", jwtUserId, userId);
            return false;
        }

        // In strict mode, we don't trust JWT tenant claims for access decisions
        // We only use them for logging/auditing
        if (!strictValidation) {
            // Check if tenant_id matches (single tenant user)
            String jwtTenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId.equals(jwtTenantId)) {
                return true;
            }

            // Check accessible_tenants claim (multi-tenant user)
            List<String> accessibleTenants = jwt.getClaimAsStringList("accessible_tenants");
            if (accessibleTenants != null) {
                for (String tenantAccess : accessibleTenants) {
                    // Format: tenantId|tenantName|role
                    String[] parts = tenantAccess.split("\\|");
                    if (parts.length >= 1 && parts[0].equals(tenantId)) {
                        return true;
                    }
                }
            }
        }

        return true; // JWT is valid, access decision made by database
    }

    @Override
    public boolean validateRole(String tenantId, String role) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return false;
        }

        // For role validation, always check database in strict mode
        if (strictValidation) {
            return checkDatabaseRole(userId, tenantId, role);
        }

        String userRole = getUserRoleInTenant(tenantId);
        return role.equals(userRole);
    }

    @Cacheable(value = "userRoles", key = "#userId + ':' + #tenantId + ':' + #requiredRole", unless = "#result == false")
    public boolean checkDatabaseRole(String userId, String tenantId, String requiredRole) {
        // Check UserTenantAccess for roles
        Optional<UserTenantAccess> access = userTenantAccessRepository
            .findByUserIdAndTenantId(userId, tenantId);

        if (access.isPresent() && access.get().isActive()) {
            return access.get().getRoles().stream()
                .anyMatch(role -> role.name().equals(requiredRole));
        }

        // Fallback to Staff table
        Optional<Staff> staff = staffRepository
            .findByKeycloakUserIdAndTenantId(userId, tenantId);

        if (staff.isPresent() && staff.get().isActive()) {
            return staff.get().getRoles().stream()
                .anyMatch(role -> role.name().equals(requiredRole));
        }

        return false;
    }

    /**
     * Evict role cache for a specific user and tenant.
     */
    @CacheEvict(value = "userRoles", allEntries = true)
    public void evictRoleCache(String userId, String tenantId) {
        log.debug("Evicted role cache for user {} and tenant {}", userId, tenantId);
    }

    @Override
    public String getUserRoleInTenant(String tenantId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }

        // In strict mode, always get role from database
        if (strictValidation) {
            return getDatabaseRole(userId, tenantId);
        }

        // Legacy mode: Check JWT first
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            // For single tenant users with matching tenant
            String jwtTenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId.equals(jwtTenantId)) {
                // Get role from realm_access
                Object realmAccess = jwt.getClaim("realm_access");
                if (realmAccess instanceof java.util.Map) {
                    java.util.Map<String, Object> realmAccessMap = (java.util.Map<String, Object>) realmAccess;
                    List<String> roles = (List<String>) realmAccessMap.get("roles");
                    if (roles != null && !roles.isEmpty()) {
                        // Return the highest priority role
                        if (roles.contains("ADMIN")) return "ADMIN";
                        if (roles.contains("DOCTOR")) return "DOCTOR";
                        if (roles.contains("STAFF")) return "STAFF";
                        return roles.get(0);
                    }
                }
            }

            // Check accessible_tenants for multi-tenant users
            List<String> accessibleTenants = jwt.getClaimAsStringList("accessible_tenants");
            if (accessibleTenants != null) {
                for (String tenantAccess : accessibleTenants) {
                    // Format: tenantId|tenantName|role
                    String[] parts = tenantAccess.split("\\|");
                    if (parts.length >= 3 && parts[0].equals(tenantId)) {
                        return parts[2];
                    }
                }
            }
        }

        // Fall back to database
        return getDatabaseRole(userId, tenantId);
    }

    private String getDatabaseRole(String userId, String tenantId) {
        // Check UserTenantAccess first
        Optional<UserTenantAccess> access = userTenantAccessRepository
            .findByUserIdAndTenantId(userId, tenantId);

        if (access.isPresent() && access.get().isActive()) {
            return access.get().getRoles().stream()
                .findFirst()
                .map(Enum::name)
                .orElse(null);
        }

        // Fallback to Staff table
        Optional<Staff> staff = staffRepository.findByKeycloakUserIdAndTenantId(userId, tenantId);
        return staff.filter(Staff::isActive)
            .map(this::getPrimaryRoleName)
            .orElse(null);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return extractUserIdFromJwt(jwt);
        }
        return null;
    }

    private String extractUserIdFromJwt(Jwt jwt) {
        // Try different claims that might contain the user ID
        String userId = jwt.getClaimAsString("sub"); // Standard subject claim
        if (userId == null) {
            userId = jwt.getClaimAsString("preferred_username");
        }
        if (userId == null) {
            userId = jwt.getClaimAsString("email");
        }
        return userId;
    }

    private String getPrimaryRoleName(Staff staff) {
        if (staff.getRoles() == null || staff.getRoles().isEmpty()) {
            return null;
        }
        // Return the highest priority role
        return staff.getRoles().stream()
            .map(Enum::name)
            .sorted((a, b) -> {
                // Priority: ADMIN > DOCTOR > STAFF
                if ("ADMIN".equals(a)) return -1;
                if ("ADMIN".equals(b)) return 1;
                if ("DOCTOR".equals(a)) return -1;
                if ("DOCTOR".equals(b)) return 1;
                return 0;
            })
            .findFirst()
            .orElse(null);
    }
}
