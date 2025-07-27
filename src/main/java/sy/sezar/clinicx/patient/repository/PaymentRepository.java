package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.model.enums.PaymentType;

import java.math.BigDecimal;
import java.util.List;
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

    /**
     * Finds all advance payments (credits) for a patient.
     *
     * @param patientId The UUID of the patient.
     * @param type      The payment type (CREDIT).
     * @param pageable  Pagination and sorting information.
     * @return A Page of advance payments for the given patient.
     */
    Page<Payment> findByPatientIdAndTypeOrderByPaymentDateDesc(UUID patientId, PaymentType type, Pageable pageable);

    /**
     * Finds all unapplied advance payments for a patient.
     *
     * @param patientId The UUID of the patient.
     * @param type      The payment type (CREDIT).
     * @return A list of unapplied advance payments.
     */
    @Query("SELECT p FROM Payment p WHERE p.patient.id = :patientId AND p.type = :type AND p.invoice IS NULL")
    List<Payment> findUnappliedAdvancePayments(@Param("patientId") UUID patientId, @Param("type") PaymentType type);

    /**
     * Calculates the total available credit balance for a patient.
     *
     * @param patientId The UUID of the patient.
     * @return The total available credit balance.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.patient.id = :patientId AND p.type = 'CREDIT' AND p.invoice IS NULL")
    BigDecimal calculateAvailableCredit(@Param("patientId") UUID patientId);

    /**
     * Calculates the total credit amount for a patient (applied and unapplied).
     *
     * @param patientId The UUID of the patient.
     * @return The total credit amount.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.patient.id = :patientId AND p.type = 'CREDIT'")
    BigDecimal calculateTotalCredit(@Param("patientId") UUID patientId);
}
