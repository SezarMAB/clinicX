package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.projection.PatientSearchResultProjection;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Patient entities, supporting search, pagination, and detailed fetching.
 */
public interface PatientRepository extends JpaRepository<Patient, UUID>, JpaSpecificationExecutor<Patient> {

    /**
     * Finds a patient by their ID, eagerly fetching related data required for the main patient view.
     * This helps to avoid N+1 query problems when loading the patient dashboard.
     *
     * @param id The UUID of the patient.
     * @return An Optional containing the Patient with fetched associations.
     */
    @Override
    @EntityGraph(attributePaths = {"appointments", "notes", "invoices"})
    Optional<Patient> findById(UUID id);

    /**
     * Finds all patients matching the given specification and returns a paginated result
     * with a projection suitable for list views.
     *
     * @param spec       The specification to filter patients (e.g., by search term).
     * @param pageable   The pagination and sorting information.
     * @return A Page of PatientSearchResultProjection.
     */
    Page<PatientSearchResultProjection> findBy(Specification<Patient> spec, Pageable pageable);
}

