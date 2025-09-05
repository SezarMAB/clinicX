package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Invoice entities with comprehensive payment and reporting capabilities.
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

    /**
     * Finds an invoice by invoice number.
     *
     * @param invoiceNumber The invoice number.
     * @return Optional containing the invoice if found.
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Finds all invoices by status.
     *
     * @param status The invoice status.
     * @param pageable Pagination and sorting information.
     * @return A Page of invoices with the given status.
     */
    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);

    /**
     * Finds invoices by patient and status.
     *
     * @param patientId The UUID of the patient.
     * @param statuses List of invoice statuses.
     * @param pageable Pagination and sorting information.
     * @return A Page of invoices matching criteria.
     */
    Page<Invoice> findByPatientIdAndStatusIn(UUID patientId, List<InvoiceStatus> statuses, Pageable pageable);

    /**
     * Finds overdue invoices.
     *
     * @param status The invoice status (typically UNPAID or PARTIALLY_PAID).
     * @param date The date to compare against due date.
     * @return List of overdue invoices.
     */
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);

    /**
     * Finds all unpaid invoices (including partially paid).
     *
     * @param statuses List of unpaid statuses.
     * @param pageable Pagination and sorting information.
     * @return A Page of unpaid invoices.
     */
    @Query("SELECT i FROM Invoice i WHERE i.status IN :statuses ORDER BY i.dueDate ASC")
    Page<Invoice> findUnpaidInvoices(@Param("statuses") List<InvoiceStatus> statuses, Pageable pageable);

    /**
     * Calculates total amount by status.
     *
     * @param status The invoice status.
     * @return Total amount for the given status.
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.status = :status")
    BigDecimal calculateTotalByStatus(@Param("status") InvoiceStatus status);

    /**
     * Calculates outstanding balance for a patient.
     *
     * @param patientId The UUID of the patient.
     * @return Outstanding balance amount.
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount - COALESCE((SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = i), 0)), 0) " +
           "FROM Invoice i WHERE i.patient.id = :patientId AND i.status IN ('UNPAID', 'PARTIALLY_PAID', 'OVERDUE')")
    BigDecimal calculateOutstandingBalance(@Param("patientId") UUID patientId);

    /**
     * Gets outstanding balances grouped by patient.
     *
     * @return List of patient IDs and their outstanding balances.
     */
    @Query("SELECT i.patient.id, i.patient.fullName, " +
           "SUM(i.totalAmount - COALESCE((SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = i), 0)) as balance " +
           "FROM Invoice i WHERE i.status IN ('UNPAID', 'PARTIALLY_PAID', 'OVERDUE') " +
           "GROUP BY i.patient.id, i.patient.fullName " +
           "HAVING SUM(i.totalAmount - COALESCE((SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = i), 0)) > 0")
    List<Object[]> calculateOutstandingBalancesByPatient();

    /**
     * Generates aging report data.
     *
     * @return Aging report summary.
     */
    @Query("SELECT " +
           "SUM(CASE WHEN (CURRENT_DATE - i.dueDate) <= 30 THEN " +
           "    i.totalAmount - COALESCE((SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = i), 0) ELSE 0 END) as current, " +
           "SUM(CASE WHEN (CURRENT_DATE - i.dueDate) BETWEEN 31 AND 60 THEN " +
           "    i.totalAmount - COALESCE((SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = i), 0) ELSE 0 END) as days30, " +
           "SUM(CASE WHEN (CURRENT_DATE - i.dueDate) BETWEEN 61 AND 90 THEN " +
           "    i.totalAmount - COALESCE((SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = i), 0) ELSE 0 END) as days60, " +
           "SUM(CASE WHEN (CURRENT_DATE - i.dueDate) > 90 THEN " +
           "    i.totalAmount - COALESCE((SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = i), 0) ELSE 0 END) as days90 " +
           "FROM Invoice i WHERE i.status IN ('UNPAID', 'PARTIALLY_PAID', 'OVERDUE')")
    Object[] calculateAgingReport();

    /**
     * Gets the next invoice number.
     *
     * @return The next invoice number in sequence.
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.invoiceNumber, 4) AS integer)), 0) + 1 " +
           "FROM Invoice i WHERE i.invoiceNumber LIKE 'INV%'")
    Integer getNextInvoiceNumber();

    /**
     * Finds invoices created within a date range.
     *
     * @param startDate Start date (inclusive).
     * @param endDate End date (inclusive).
     * @param pageable Pagination and sorting information.
     * @return A Page of invoices within the date range.
     */
    Page<Invoice> findByIssueDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Counts invoices by status for a patient.
     *
     * @param patientId The UUID of the patient.
     * @param status The invoice status.
     * @return Count of invoices.
     */
    Integer countByPatientIdAndStatus(UUID patientId, InvoiceStatus status);

    /**
     * Gets total revenue within a date range.
     *
     * @param startDate Start date (inclusive).
     * @param endDate End date (inclusive).
     * @return Total revenue amount.
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
           "WHERE i.issueDate BETWEEN :startDate AND :endDate AND i.status != 'CANCELLED'")
    BigDecimal calculateRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find invoices by issue date range.
     */
    List<Invoice> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Count invoices by issue date range.
     */
    long countByIssueDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Count invoices by status list.
     */
    long countByStatusIn(List<InvoiceStatus> statuses);
    
    /**
     * Count invoices by due date before and status list.
     */
    long countByDueDateBeforeAndStatusIn(LocalDate date, List<InvoiceStatus> statuses);
    
    /**
     * Find invoices by status and issue date range.
     */
    List<Invoice> findByStatusAndIssueDateBetween(InvoiceStatus status, LocalDate startDate, LocalDate endDate);
    
    /**
     * Count invoices by status and issue date range.
     */
    long countByStatusAndIssueDateBetween(InvoiceStatus status, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find invoices by patient and issue date range.
     */
    List<Invoice> findByPatientIdAndIssueDateBetween(UUID patientId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find first invoice by patient ordered by issue date.
     */
    Optional<Invoice> findFirstByPatientIdOrderByIssueDateAsc(UUID patientId);
    
    /**
     * Count invoices by due date range and status list.
     */
    long countByDueDateBetweenAndStatusIn(LocalDate startDate, LocalDate endDate, List<InvoiceStatus> statuses);
}

