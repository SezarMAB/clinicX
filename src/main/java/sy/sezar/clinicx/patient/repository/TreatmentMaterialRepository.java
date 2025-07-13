package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.patient.model.TreatmentMaterial;

import java.util.List;
import java.util.UUID;

@Repository
public interface TreatmentMaterialRepository extends JpaRepository<TreatmentMaterial, UUID> {

    List<TreatmentMaterial> findByTreatmentId(UUID treatmentId);
    
    Page<TreatmentMaterial> findByTreatmentId(UUID treatmentId, Pageable pageable);

    @Query("SELECT tm FROM TreatmentMaterial tm WHERE tm.treatment.patient.id = :patientId")
    List<TreatmentMaterial> findByPatientId(@Param("patientId") UUID patientId);

    @Query("SELECT tm FROM TreatmentMaterial tm WHERE tm.treatment.patient.id = :patientId")
    Page<TreatmentMaterial> findByPatientId(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(tm.totalCost), 0) FROM TreatmentMaterial tm WHERE tm.treatment.id = :treatmentId")
    java.math.BigDecimal getTotalMaterialCostByTreatmentId(@Param("treatmentId") UUID treatmentId);

    @Query("SELECT COALESCE(SUM(tm.totalCost), 0) FROM TreatmentMaterial tm WHERE tm.treatment.patient.id = :patientId")
    java.math.BigDecimal getTotalMaterialCostByPatientId(@Param("patientId") UUID patientId);
}