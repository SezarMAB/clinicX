package sy.sezar.clinicx.tenant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import sy.sezar.clinicx.tenant.model.Tenant;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Tenant entities, supporting search, pagination, and multi-tenancy operations.
 */
public interface TenantRepository extends JpaRepository<Tenant, UUID>, JpaSpecificationExecutor<Tenant> {
    
    /**
     * Finds a tenant by their unique tenant ID.
     *
     * @param tenantId The unique tenant identifier.
     * @return An Optional containing the Tenant if found.
     */
    Optional<Tenant> findByTenantId(String tenantId);
    
    /**
     * Finds a tenant by their subdomain.
     *
     * @param subdomain The subdomain of the tenant.
     * @return An Optional containing the Tenant if found.
     */
    Optional<Tenant> findBySubdomain(String subdomain);
    
    /**
     * Finds a tenant by their Keycloak realm name.
     *
     * @param realmName The Keycloak realm name.
     * @return An Optional containing the Tenant if found.
     */
    Optional<Tenant> findByRealmName(String realmName);
    
    /**
     * Checks if a tenant exists with the given tenant ID.
     *
     * @param tenantId The tenant ID to check.
     * @return true if exists, false otherwise.
     */
    boolean existsByTenantId(String tenantId);
    
    /**
     * Checks if a tenant exists with the given subdomain.
     *
     * @param subdomain The subdomain to check.
     * @return true if exists, false otherwise.
     */
    boolean existsBySubdomain(String subdomain);
    
    /**
     * Checks if a tenant exists with the given realm name.
     *
     * @param realmName The realm name to check.
     * @return true if exists, false otherwise.
     */
    boolean existsByRealmName(String realmName);
    
    /**
     * Counts active tenants.
     *
     * @return The count of active tenants.
     */
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.isActive = true")
    long countActiveTenants();
    
    /**
     * Finds active tenants with subscription ending before or on the given date.
     *
     * @param expiryDate The expiry date to check against.
     * @param pageable Pagination information.
     * @return Page of tenants with expiring subscriptions.
     */
    Page<Tenant> findByIsActiveTrueAndSubscriptionEndDateLessThanEqual(java.time.Instant expiryDate, Pageable pageable);
    
    /**
     * Counts the number of tenants using the same realm name.
     * Used to determine if a realm can be safely deleted from Keycloak.
     *
     * @param realmName The realm name to count tenants for.
     * @return The count of tenants using this realm.
     */
    long countByRealmName(String realmName);
}