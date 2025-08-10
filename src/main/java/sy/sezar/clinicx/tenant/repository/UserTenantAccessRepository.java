package sy.sezar.clinicx.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTenantAccessRepository extends JpaRepository<UserTenantAccess, UUID>, 
                                                    JpaSpecificationExecutor<UserTenantAccess> {

    Optional<UserTenantAccess> findByUserIdAndTenantId(String userId, String tenantId);

    List<UserTenantAccess> findByUserId(String userId);

    List<UserTenantAccess> findByTenantId(String tenantId);

    List<UserTenantAccess> findByUserIdAndIsActiveTrue(String userId);

    List<UserTenantAccess> findByTenantIdAndIsActiveTrue(String tenantId);

    @Query("SELECT uta FROM UserTenantAccess uta WHERE uta.userId = :userId AND uta.isPrimary = true")
    Optional<UserTenantAccess> findPrimaryAccessForUser(@Param("userId") String userId);

    @Query("SELECT COUNT(uta) FROM UserTenantAccess uta WHERE uta.tenantId = :tenantId AND uta.isActive = true")
    long countActiveUsersByTenant(@Param("tenantId") String tenantId);

    @Query("SELECT uta FROM UserTenantAccess uta WHERE uta.tenantId = :tenantId AND uta.role = :role AND uta.isActive = true")
    List<UserTenantAccess> findByTenantIdAndRole(@Param("tenantId") String tenantId, @Param("role") String role);

    boolean existsByUserIdAndTenantIdAndIsActiveTrue(String userId, String tenantId);

    void deleteByTenantId(String tenantId);

    void deleteByUserIdAndTenantId(String userId, String tenantId);
}