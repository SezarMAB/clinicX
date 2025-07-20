package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.Payment;

import java.util.UUID;

/**
 * Repository for managing Payment entities.
 */
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Finds all payments for a specific invoice with pagination.
     *
     * @param invoiceId The UUID of the invoice.
     * @param pageable  Pagination and sorting information.
     * @return A Page of payments for the given invoice.
     */
    Page<Payment> findByInvoiceIdOrderByPaymentDateDesc(UUID invoiceId, Pageable pageable);
}
