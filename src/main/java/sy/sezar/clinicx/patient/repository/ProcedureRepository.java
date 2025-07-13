package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sy.sezar.clinicx.patient.model.Procedure;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing Procedure entities.
 */
public interface ProcedureRepository extends JpaRepository<Procedure, UUID> {

    /**
     * Finds all procedures with pagination.
     *
     * @param pageable Pagination and sorting information.
     * @return A Page of procedures.
     */
    Page<Procedure> findAllByOrderByName(Pageable pageable);

    /**
     * Finds all active procedures for dropdowns.
     *
     * @return List of active procedures.
     */
    @Query("SELECT p FROM Procedure p ORDER BY p.name")
    List<Procedure> findAllActive();

    /**
     * Searches procedures by name or code.
     *
     * @param searchTerm The search term.
     * @return List of matching procedures.
     */
    @Query("SELECT p FROM Procedure p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.procedureCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY p.name")
    List<Procedure> searchByNameOrCode(@Param("searchTerm") String searchTerm);
}
