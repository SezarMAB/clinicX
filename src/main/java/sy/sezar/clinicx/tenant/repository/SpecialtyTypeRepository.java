package sy.sezar.clinicx.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.tenant.model.SpecialtyType;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing specialty types.
 */
@Repository
public interface SpecialtyTypeRepository extends JpaRepository<SpecialtyType, UUID> {
    
    /**
     * Find a specialty type by its code.
     * 
     * @param code the specialty code (e.g., "DENTAL", "CLINIC")
     * @return Optional containing the specialty type if found
     */
    Optional<SpecialtyType> findByCode(String code);
    
    /**
     * Check if a specialty type exists by code.
     * 
     * @param code the specialty code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(String code);
    
    /**
     * Find a specialty type by its realm name.
     * 
     * @param realmName the Keycloak realm name
     * @return Optional containing the specialty type if found
     */
    Optional<SpecialtyType> findByRealmName(String realmName);
}