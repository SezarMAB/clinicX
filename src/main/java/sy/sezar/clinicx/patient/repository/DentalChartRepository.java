package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.patient.model.DentalChart;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DentalChart entity with JSONB operations.
 */
@Repository
public interface DentalChartRepository extends JpaRepository<DentalChart, UUID> {
    
    /**
     * Find dental chart by patient ID
     */
    Optional<DentalChart> findByPatientId(UUID patientId);
    
    /**
     * Update specific tooth condition using JSONB operations
     */
    @Modifying
    @Query(value = """
        UPDATE dental_charts 
        SET chart_data = jsonb_set(
            chart_data,
            ARRAY['teeth', :toothId, 'condition'],
            to_jsonb(:condition::text),
            true
        ),
        updated_at = CURRENT_TIMESTAMP
        WHERE patient_id = :patientId
        """, nativeQuery = true)
    void updateToothCondition(@Param("patientId") UUID patientId, 
                              @Param("toothId") String toothId, 
                              @Param("condition") String condition);
    
    /**
     * Update specific tooth surface condition
     */
    @Modifying
    @Query(value = """
        UPDATE dental_charts 
        SET chart_data = jsonb_set(
            chart_data,
            ARRAY['teeth', :toothId, 'surfaces', :surface, 'condition'],
            to_jsonb(:condition::text),
            true
        ),
        updated_at = CURRENT_TIMESTAMP
        WHERE patient_id = :patientId
        """, nativeQuery = true)
    void updateToothSurfaceCondition(@Param("patientId") UUID patientId,
                                      @Param("toothId") String toothId,
                                      @Param("surface") String surface,
                                      @Param("condition") String condition);
    
    /**
     * Update tooth notes
     */
    @Modifying
    @Query(value = """
        UPDATE dental_charts 
        SET chart_data = jsonb_set(
            chart_data,
            ARRAY['teeth', :toothId, 'notes'],
            to_jsonb(:notes::text),
            true
        ),
        updated_at = CURRENT_TIMESTAMP
        WHERE patient_id = :patientId
        """, nativeQuery = true)
    void updateToothNotes(@Param("patientId") UUID patientId,
                          @Param("toothId") String toothId,
                          @Param("notes") String notes);
    
    /**
     * Get specific tooth data
     */
    @Query(value = """
        SELECT chart_data->'teeth'->:toothId
        FROM dental_charts
        WHERE patient_id = :patientId
        """, nativeQuery = true)
    String getToothData(@Param("patientId") UUID patientId, @Param("toothId") String toothId);
    
    /**
     * Check if patient has dental chart
     */
    boolean existsByPatientId(UUID patientId);
}