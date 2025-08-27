package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.patient.model.PaymentPlanInstallment;
import sy.sezar.clinicx.patient.model.enums.InstallmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentPlanInstallmentRepository extends JpaRepository<PaymentPlanInstallment, UUID> {

    /**
     * Find installments by payment plan ID.
     */
    List<PaymentPlanInstallment> findByPaymentPlanIdOrderByInstallmentNumber(UUID paymentPlanId);

    /**
     * Find installments by status.
     */
    Page<PaymentPlanInstallment> findByStatus(InstallmentStatus status, Pageable pageable);

    /**
     * Find overdue installments.
     */
    @Query("SELECT i FROM PaymentPlanInstallment i WHERE i.status = 'PENDING' AND i.dueDate < :today")
    List<PaymentPlanInstallment> findOverdueInstallments(@Param("today") LocalDate today);

    /**
     * Find installments due within a date range.
     */
    @Query("SELECT i FROM PaymentPlanInstallment i WHERE i.dueDate BETWEEN :startDate AND :endDate")
    List<PaymentPlanInstallment> findInstallmentsDueBetween(@Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);

    /**
     * Find installments by payment plan ID and status.
     */
    List<PaymentPlanInstallment> findByPaymentPlanIdAndStatus(UUID paymentPlanId, InstallmentStatus status);

    /**
     * Find next due installment for a payment plan.
     */
    @Query("SELECT i FROM PaymentPlanInstallment i WHERE i.paymentPlan.id = :paymentPlanId " +
           "AND i.status = 'PENDING' ORDER BY i.dueDate ASC")
    List<PaymentPlanInstallment> findNextDueInstallments(@Param("paymentPlanId") UUID paymentPlanId);

    /**
     * Count pending installments by payment plan.
     */
    @Query("SELECT COUNT(i) FROM PaymentPlanInstallment i WHERE i.paymentPlan.id = :paymentPlanId AND i.status = 'PENDING'")
    long countPendingInstallmentsByPaymentPlan(@Param("paymentPlanId") UUID paymentPlanId);

    /**
     * Get installment statistics for a payment plan.
     */
    @Query("SELECT COUNT(i) as totalInstallments, " +
           "SUM(CASE WHEN i.status = 'PAID' THEN 1 ELSE 0 END) as paidInstallments, " +
           "SUM(CASE WHEN i.status = 'PENDING' THEN 1 ELSE 0 END) as pendingInstallments, " +
           "SUM(CASE WHEN i.status = 'OVERDUE' THEN 1 ELSE 0 END) as overdueInstallments, " +
           "SUM(i.amount) as totalAmount, " +
           "SUM(i.paidAmount) as totalPaidAmount " +
           "FROM PaymentPlanInstallment i WHERE i.paymentPlan.id = :paymentPlanId")
    Object[] getInstallmentStatistics(@Param("paymentPlanId") UUID paymentPlanId);
}
