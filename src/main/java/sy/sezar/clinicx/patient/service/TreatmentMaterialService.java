package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TreatmentMaterialService {

    TreatmentMaterialDto create(TreatmentMaterialCreateRequest request);
    
    TreatmentMaterialDto findById(UUID id);
    
    List<TreatmentMaterialDto> findByTreatmentId(UUID treatmentId);
    
    Page<TreatmentMaterialDto> findByTreatmentId(UUID treatmentId, Pageable pageable);
    
    List<TreatmentMaterialDto> findByPatientId(UUID patientId);
    
    Page<TreatmentMaterialDto> findByPatientId(UUID patientId, Pageable pageable);
    
    TreatmentMaterialDto update(UUID id, TreatmentMaterialCreateRequest request);
    
    void delete(UUID id);
    
    BigDecimal getTotalMaterialCostByTreatmentId(UUID treatmentId);
    
    BigDecimal getTotalMaterialCostByPatientId(UUID patientId);
}