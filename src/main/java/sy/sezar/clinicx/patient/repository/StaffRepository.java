package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.staff.model.Staff;

import java.util.UUID;

/**
 * Repository for managing Staff entities.
 */
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    /**
     * Finds staff by email address.
     *
     * @param email The email address.
     * @return Staff member if found.
     */
    Staff findByEmail(String email);
}
