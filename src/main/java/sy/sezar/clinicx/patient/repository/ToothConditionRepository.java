package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.patient.model.ToothCondition;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing ToothCondition entities.
 */
@Repository
public interface ToothConditionRepository extends JpaRepository<ToothCondition, UUID> {

    /**
     * Finds the default healthy tooth condition.
     *
     * @return Optional ToothCondition representing healthy/normal state.
     */
    @Query("SELECT tc FROM ToothCondition tc WHERE LOWER(tc.name) = 'healthy' OR LOWER(tc.name) = 'normal' ORDER BY tc.name LIMIT 1")
    Optional<ToothCondition> findDefaultHealthyCondition();

    /**
     * Finds a tooth condition by its code.
     *
     * @param code The condition code.
     * @return Optional ToothCondition if found.
     */
    Optional<ToothCondition> findByCode(String code);

    /**
     * Finds a tooth condition by its name (case-insensitive).
     *
     * @param name The condition name.
     * @return Optional ToothCondition if found.
     */
    Optional<ToothCondition> findByNameIgnoreCase(String name);
}
