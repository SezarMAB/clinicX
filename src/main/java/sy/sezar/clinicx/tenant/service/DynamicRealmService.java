package sy.sezar.clinicx.tenant.service;

import sy.sezar.clinicx.tenant.dto.TenantCreateRequest;
import sy.sezar.clinicx.tenant.model.SpecialtyType;

/**
 * Service for managing dynamic realm creation based on specialty types.
 */
public interface DynamicRealmService {
    
    /**
     * Resolve the realm name for a tenant based on its specialty.
     * Creates the realm if it doesn't exist.
     * 
     * @param request the tenant creation request
     * @return the realm name to use
     */
    String resolveRealmForTenant(TenantCreateRequest request);
    
    /**
     * Configure a realm for a specific specialty type.
     * 
     * @param realmName the realm name
     * @param specialty the specialty type
     */
    void configureRealmForSpecialty(String realmName, String specialty);
    
    /**
     * Check if a realm exists for a specialty.
     * 
     * @param specialty the specialty code
     * @return true if realm exists
     */
    boolean hasRealmForSpecialty(String specialty);
    
    /**
     * Get the realm name for a specialty.
     * 
     * @param specialty the specialty code
     * @return the realm name
     */
    String getRealmNameForSpecialty(String specialty);
    
    /**
     * Create protocol mappers for multi-tenant attributes.
     * 
     * @param realmName the realm name
     * @param clientId the client ID
     */
    void ensureProtocolMappers(String realmName, String clientId);
}