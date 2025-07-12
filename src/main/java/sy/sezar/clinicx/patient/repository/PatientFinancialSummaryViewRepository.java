package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sy.sezar.clinicx.patient.view.PatientFinancialSummaryView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for the {@link PatientFinancialSummaryView} entity.
 * Provides read-only access to the v_patient_financial_summary view.
 */
public interface PatientFinancialSummaryViewRepository extends JpaRepository<PatientFinancialSummaryView, UUID> {

    /**
     * Finds patients whose balance is greater than a specified amount.
     *
     * @param amount The minimum balance amount.
     * @return A list of {@link PatientFinancialSummaryView} for patients with a balance greater than the specified amount.
     */
    List<PatientFinancialSummaryView> findByBalanceGreaterThan(BigDecimal amount);

    /**
     * Finds patients who have at least one unpaid invoice.
     *
     * @return A list of {@link PatientFinancialSummaryView} for patients with unpaid invoices.
     */
    List<PatientFinancialSummaryView> findByUnpaidInvoicesGreaterThan(Long count);

    /**
     * Finds a patient's financial summary by their public-facing ID.
     *
     * @param publicFacingId The public-facing ID of the patient.
     * @return The {@link PatientFinancialSummaryView} for the specified patient.
     */
    PatientFinancialSummaryView findByPublicFacingId(String publicFacingId);
}

