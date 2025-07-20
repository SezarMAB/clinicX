package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.Invoice;

import java.util.UUID;

/**
 * Repository for Invoice entities.
 */
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    /**
     * Finds all invoices for a given patient, fetching associated payments and invoice items
     * to prevent N+1 issues in the finance tab.
     *
     * @param patientId The UUID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A Page of invoices with their details.
     */
    @EntityGraph(attributePaths = {"payments", "items"})
    Page<Invoice> findByPatientId(UUID patientId, Pageable pageable);
}

