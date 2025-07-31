package sy.sezar.clinicx.clinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT s FROM Staff s WHERE s.role = :role")
    Page<Staff> findByRole(@Param("role") StaffRole role, Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Staff> searchStaff(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT s FROM Staff s JOIN s.specialties sp WHERE sp.id = :specialtyId")
    Page<Staff> findBySpecialtyId(@Param("specialtyId") UUID specialtyId, Pageable pageable);
    
    // Methods from UserTenantAccessRepository
    List<Staff> findByUserId(String userId);
    
    List<Staff> findByTenantId(String tenantId);
    
    Optional<Staff> findByUserIdAndTenantId(String userId, String tenantId);
    
    boolean existsByUserIdAndTenantId(String userId, String tenantId);
    
    Optional<Staff> findByUserIdAndIsPrimaryTrue(String userId);
    
    @Query("SELECT s.tenantId FROM Staff s WHERE s.userId = :userId")
    List<String> findTenantIdsByUserId(@Param("userId") String userId);
    
    void deleteByUserId(String userId);
}
