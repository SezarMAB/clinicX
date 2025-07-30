package sy.sezar.clinicx.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing user-tenant access relationships.
 */
@Repository
public interface UserTenantAccessRepository extends JpaRepository<UserTenantAccess, UUID> {
    
    /**
     * Find all tenant accesses for a specific user.
     * 
     * @param userId the Keycloak user ID
     * @return list of tenant accesses
     */
    List<UserTenantAccess> findByUserId(String userId);
    
    /**
     * Find all users with access to a specific tenant.
     * 
     * @param tenantId the tenant ID
     * @return list of user accesses
     */
    List<UserTenantAccess> findByTenantId(String tenantId);
    
    /**
     * Find specific user access for a tenant.
     * 
     * @param userId the user ID
     * @param tenantId the tenant ID
     * @return Optional containing the access if found
     */
    Optional<UserTenantAccess> findByUserIdAndTenantId(String userId, String tenantId);
    
    /**
     * Check if user has access to a tenant.
     * 
     * @param userId the user ID
     * @param tenantId the tenant ID
     * @return true if access exists
     */
    boolean existsByUserIdAndTenantId(String userId, String tenantId);
    
    /**
     * Find the primary tenant for a user.
     * 
     * @param userId the user ID
     * @return Optional containing the primary tenant access
     */
    Optional<UserTenantAccess> findByUserIdAndIsPrimaryTrue(String userId);
    
    /**
     * Get all accessible tenant IDs for a user.
     * 
     * @param userId the user ID
     * @return list of tenant IDs
     */
    @Query("SELECT uta.tenantId FROM UserTenantAccess uta WHERE uta.userId = :userId")
    List<String> findTenantIdsByUserId(@Param("userId") String userId);
    
    /**
     * Delete all accesses for a user.
     * 
     * @param userId the user ID
     */
    void deleteByUserId(String userId);
}