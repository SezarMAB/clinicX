package sy.sezar.clinicx.tenant.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.tenant.dto.*;

import java.util.UUID;

/**
 * Service interface for managing tenants.
 */
public interface TenantService {
    
    /**
     * Creates a new tenant with Keycloak realm and admin user.
     *
     * @param request The tenant creation request.
     * @return The created tenant with client configuration details.
     */
    TenantCreationResponseDto createTenant(TenantCreateRequest request);
    
    /**
     * Retrieves a tenant by ID.
     *
     * @param id The tenant ID.
     * @return The tenant details.
     */
    TenantDetailDto getTenantById(UUID id);
    
    /**
     * Retrieves a tenant by tenant ID.
     *
     * @param tenantId The unique tenant identifier.
     * @return The tenant details.
     */
    TenantDetailDto getTenantByTenantId(String tenantId);
    
    /**
     * Retrieves a tenant by subdomain.
     *
     * @param subdomain The tenant subdomain.
     * @return The tenant details.
     */
    TenantDetailDto getTenantBySubdomain(String subdomain);
    
    /**
     * Retrieves all tenants with filtering and pagination.
     *
     * @param searchTerm The search term for filtering.
     * @param isActive Filter by active status.
     * @param pageable Pagination information.
     * @return Page of tenant summaries.
     */
    Page<TenantSummaryDto> getAllTenants(String searchTerm, Boolean isActive, Pageable pageable);
    
    /**
     * Searches tenants with filtering and pagination.
     * Alias for getAllTenants to match controller method name.
     *
     * @param searchTerm The search term for filtering.
     * @param isActive Filter by active status.
     * @param pageable Pagination information.
     * @return Page of tenant summaries.
     */
    Page<TenantSummaryDto> searchTenants(String searchTerm, Boolean isActive, Pageable pageable);
    
    /**
     * Updates tenant information.
     *
     * @param id The tenant ID.
     * @param request The update request.
     * @return The updated tenant details.
     */
    TenantDetailDto updateTenant(UUID id, TenantUpdateRequest request);
    
    /**
     * Activates a tenant.
     *
     * @param id The tenant ID.
     */
    void activateTenant(UUID id);
    
    /**
     * Deactivates a tenant.
     *
     * @param id The tenant ID.
     */
    void deactivateTenant(UUID id);
    
    /**
     * Deletes a tenant and its Keycloak realm.
     *
     * @param id The tenant ID.
     */
    void deleteTenant(UUID id);
    
    /**
     * Checks if a tenant is active.
     *
     * @param tenantId The tenant ID.
     * @return true if active, false otherwise.
     */
    boolean isTenantActive(String tenantId);
    
    /**
     * Checks subdomain availability.
     *
     * @param subdomain The subdomain to check.
     * @return Availability information.
     */
    SubdomainAvailabilityDto checkSubdomainAvailability(String subdomain);
    
    /**
     * Updates tenant usage statistics.
     *
     * @param tenantId The tenant ID.
     */
    void updateTenantUsageStats(String tenantId);
}