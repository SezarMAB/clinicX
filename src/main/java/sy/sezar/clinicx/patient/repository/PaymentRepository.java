package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    /**
     * Finds all payments for a specific patient with pagination.
     *
     * @param patientId The UUID of the patient.
     * @param pageable Pagination and sorting information.
     * @return A Page of payments for the given patient.
     */
    Page<Payment> findByPatientIdOrderByPaymentDateDesc(UUID patientId, Pageable pageable);

    /**
     * Finds all payments for a specific patient.
     *
     * @param patientId The UUID of the patient.
     * @return A list of payments for the given patient.
     */
    List<Payment> findByPatientIdOrderByPaymentDateDesc(UUID patientId);

    /**
     * Calculates the total payment amount for a patient by payment type.
     *
     * @param patientId The UUID of the patient.
     * @param type The payment type.
     * @return The total amount for the given type.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.patient.id = :patientId AND p.type = :type")
    BigDecimal calculateTotalByType(@Param("patientId") UUID patientId, @Param("type") PaymentType type);

    /**
     * Counts the number of payments for a patient.
     *
     * @param patientId The UUID of the patient.
     * @return The count of payments.
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.patient.id = :patientId")
    Integer countPaymentsByPatientId(@Param("patientId") UUID patientId);

    /**
     * Finds the most recent payment date for a patient.
     *
     * @param patientId The UUID of the patient.
     * @return The most recent payment date, or null if no payments exist.
     */
    @Query("SELECT MAX(p.paymentDate) FROM Payment p WHERE p.patient.id = :patientId")
    LocalDate findLastPaymentDate(@Param("patientId") UUID patientId);

    /**
     * Calculates payment method breakdown for a patient.
     *
     * @param patientId The UUID of the patient.
     * @return A list of payment method and total amount pairs.
     */
    @Query("SELECT p.paymentMethod, COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.patient.id = :patientId AND p.type = 'PAYMENT' " +
           "GROUP BY p.paymentMethod")
    List<Object[]> calculatePaymentMethodBreakdown(@Param("patientId") UUID patientId);

    /**
     * Finds payments within a date range for a patient.
     *
     * @param patientId The UUID of the patient.
     * @param startDate The start date.
     * @param endDate The end date.
     * @param pageable Pagination and sorting information.
     * @return A Page of payments within the date range.
     */
    Page<Payment> findByPatientIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            UUID patientId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Finds payments by patient ID and type.
     */
    Page<Payment> findByPatientIdAndType(UUID patientId, PaymentType type, Pageable pageable);
    
    /**
     * Finds all payments by type.
     */
    Page<Payment> findByType(PaymentType type, Pageable pageable);
    
    /**
     * Finds payments by type and date range.
     */
    List<Payment> findByTypeAndPaymentDateBetween(PaymentType type, LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds all payments by invoice ID.
     */
    List<Payment> findByInvoiceId(UUID invoiceId);
    
    /**
     * Finds payments by payment date.
     */
    List<Payment> findByPaymentDate(LocalDate date);
    
    /**
     * Finds payments between dates.
     */
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds payments by patient ID and date range.
     */
    List<Payment> findByPatientIdAndPaymentDateBetween(UUID patientId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate total paid by patient.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.patient.id = :patientId AND p.type = 'PAYMENT'")
    BigDecimal calculateTotalPaidByPatient(@Param("patientId") UUID patientId);
    
    /**
     * Calculate total paid before date.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.patient.id = :patientId AND p.paymentDate < :beforeDate AND p.type = 'PAYMENT'")
    BigDecimal calculateTotalPaidBeforeDate(@Param("patientId") UUID patientId, @Param("beforeDate") LocalDate beforeDate);
    
    /**
     * Find top payment by patient ordered by date.
     */
    Optional<Payment> findTopByPatientIdOrderByPaymentDateDesc(UUID patientId);
    
    /**
     * Count payments by type and date range.
     */
    long countByTypeAndPaymentDateBetween(PaymentType type, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find payments by patient, type and date range.
     */
    List<Payment> findByPatientIdAndTypeAndPaymentDateBetween(
        UUID patientId, PaymentType type, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find first payment by patient ordered by date.
     */
    Optional<Payment> findFirstByPatientIdOrderByPaymentDateAsc(UUID patientId);
}
