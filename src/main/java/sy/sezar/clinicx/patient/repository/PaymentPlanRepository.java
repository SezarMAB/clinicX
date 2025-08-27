package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.patient.model.PaymentPlan;
import sy.sezar.clinicx.patient.model.enums.PaymentPlanStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, UUID> {

    /**
     * Find payment plans by patient ID.
     */
    Page<PaymentPlan> findByPatientId(UUID patientId, Pageable pageable);

    /**
     * Find payment plans by invoice ID.
     */
    List<PaymentPlan> findByInvoiceId(UUID invoiceId);

    /**
     * Find payment plans by status.
     */
    Page<PaymentPlan> findByStatus(PaymentPlanStatus status, Pageable pageable);

    /**
     * Find payment plans by patient ID and status.
     */
    Page<PaymentPlan> findByPatientIdAndStatus(UUID patientId, PaymentPlanStatus status, Pageable pageable);

    /**
     * Find active payment plans for a patient.
     */
    @Query("SELECT pp FROM PaymentPlan pp WHERE pp.patient.id = :patientId AND pp.status = 'ACTIVE'")
    List<PaymentPlan> findActivePaymentPlansByPatient(@Param("patientId") UUID patientId);

    /**
     * Find payment plans with overdue installments.
     */
    @Query("SELECT DISTINCT pp FROM PaymentPlan pp " +
           "JOIN pp.installments i " +
           "WHERE i.status = 'PENDING' AND i.dueDate < :today AND pp.status = 'ACTIVE'")
    List<PaymentPlan> findPaymentPlansWithOverdueInstallments(@Param("today") LocalDate today);

    /**
     * Count active payment plans by patient.
     */
    @Query("SELECT COUNT(pp) FROM PaymentPlan pp WHERE pp.patient.id = :patientId AND pp.status = 'ACTIVE'")
    long countActivePaymentPlansByPatient(@Param("patientId") UUID patientId);

    /**
     * Find payment plans expiring within specified days.
     */
    @Query("SELECT pp FROM PaymentPlan pp WHERE pp.endDate BETWEEN :startDate AND :endDate AND pp.status = 'ACTIVE'")
    List<PaymentPlan> findPaymentPlansExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);

    /**
     * Get payment plan statistics for a patient.
     */
    @Query("SELECT COUNT(pp) as totalPlans, " +
           "SUM(CASE WHEN pp.status = 'ACTIVE' THEN 1 ELSE 0 END) as activePlans, " +
           "SUM(CASE WHEN pp.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedPlans, " +
           "SUM(CASE WHEN pp.status = 'DEFAULTED' THEN 1 ELSE 0 END) as defaultedPlans " +
           "FROM PaymentPlan pp WHERE pp.patient.id = :patientId")
    Object[] getPaymentPlanStatistics(@Param("patientId") UUID patientId);
}
