package sy.sezar.clinicx.clinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID>, JpaSpecificationExecutor<Staff> {

    Optional<Staff> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT s FROM Staff s WHERE s.isActive = true")
    Page<Staff> findAllActive(Pageable pageable);

    @Query("SELECT DISTINCT s FROM Staff s JOIN s.roles r WHERE r = :role")
    Page<Staff> findByRole(@Param("role") StaffRole role, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Staff s JOIN s.roles r WHERE r IN :roles")
    Page<Staff> findByRolesIn(@Param("roles") List<StaffRole> roles, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Staff s WHERE SIZE(s.roles) > 0")
    Page<Staff> findAllWithRoles(Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Staff> searchStaff(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT s FROM Staff s JOIN s.specialties sp WHERE sp.id = :specialtyId")
    Page<Staff> findBySpecialtyId(@Param("specialtyId") UUID specialtyId, Pageable pageable);

    // Methods updated for new architecture with keycloak_user_id
    List<Staff> findByKeycloakUserId(String keycloakUserId);
    List<Staff> findByKeycloakUserIdAndIsActiveIsTrue(String keycloakUserId);

    List<Staff> findByTenantId(String tenantId);

    Optional<Staff> findByKeycloakUserIdAndTenantId(String keycloakUserId, String tenantId);

    boolean existsByKeycloakUserIdAndTenantId(String keycloakUserId, String tenantId);

    @Query("SELECT s.tenantId FROM Staff s WHERE s.keycloakUserId = :keycloakUserId")
    List<String> findTenantIdsByKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);

    void deleteByKeycloakUserId(String keycloakUserId);

    @Modifying
    @Query("UPDATE Staff s SET s.phoneNumber = :phoneNumber " +
        "WHERE s.keycloakUserId = :keycloakUserId AND s.tenantId = :tenantId")
    int updatePhoneNumberByKeycloakUserIdAndTenantId(
            @Param("keycloakUserId") String keycloakUserId,
            @Param("tenantId") String tenantId,
            @Param("phoneNumber") String phoneNumber);
}
