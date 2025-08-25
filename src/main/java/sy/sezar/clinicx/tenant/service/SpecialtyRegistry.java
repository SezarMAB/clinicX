package sy.sezar.clinicx.tenant.service;

import sy.sezar.clinicx.tenant.model.SpecialtyType;

import java.util.List;

/**
 * Service for managing specialty types registry.
 */
public interface SpecialtyRegistry {
    
    /**
     * Register a new specialty type.
     * 
     * @param code the specialty code
     * @param name the specialty name
     * @param features the features available for this specialty
     * @param realmName the realm name for this specialty
     * @return the created specialty type
     */
    SpecialtyType registerSpecialty(String code, String name, String[] features, String realmName);
    
    /**
     * Get all active specialty types.
     * 
     * @return list of active specialty types
     */
    List<SpecialtyType> getActiveSpecialties();
    
    /**
     * Get a specialty type by code.
     * 
     * @param code the specialty code
     * @return the specialty type
     */
    SpecialtyType getSpecialtyByCode(String code);
    
    /**
     * Check if a specialty code exists.
     * 
     * @param code the specialty code
     * @return true if exists
     */
    boolean specialtyExists(String code);
    
    /**
     * Deactivate a specialty type.
     * 
     * @param code the specialty code
     */
    void deactivateSpecialty(String code);
    
    /**
     * Get features for a specialty.
     * 
     * @param code the specialty code
     * @return array of feature codes
     */
    String[] getSpecialtyFeatures(String code);
}