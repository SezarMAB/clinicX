package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.view.DentalChartView;
import sy.sezar.clinicx.patient.view.DentalChartViewId;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for the {@link DentalChartView} entity.
 * Provides read-only access to the v_dental_chart view.
 */
public interface DentalChartViewRepository extends JpaRepository<DentalChartView, DentalChartViewId> {

    /**
     * Finds all dental chart entries for a specific patient, ordered by tooth number.
     *
     * @param patientId The UUID of the patient.
     * @return A list of {@link DentalChartView} objects for the given patient.
     */
    List<DentalChartView> findByPatientIdOrderByToothNumber(UUID patientId);

    /**
     * Finds all dental chart entries with a specific condition code.
     * This can be useful for finding all patients with a particular dental issue.
     *
     * @param conditionCode The condition code to search for (e.g., 'CAVITY').
     * @return A list of {@link DentalChartView} objects matching the condition.
     */
    List<DentalChartView> findByConditionCode(String conditionCode);
}

