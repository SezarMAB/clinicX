package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialDto;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialSearchCriteria;
import sy.sezar.clinicx.patient.mapper.TreatmentMaterialMapper;
import sy.sezar.clinicx.patient.model.Treatment;
import sy.sezar.clinicx.patient.model.TreatmentMaterial;
import sy.sezar.clinicx.patient.repository.TreatmentMaterialRepository;
import sy.sezar.clinicx.patient.repository.TreatmentRepository;
import sy.sezar.clinicx.patient.service.TreatmentMaterialService;
import sy.sezar.clinicx.patient.spec.TreatmentMaterialSpecifications;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TreatmentMaterialServiceImpl implements TreatmentMaterialService {

    private final TreatmentMaterialRepository treatmentMaterialRepository;
    private final TreatmentRepository treatmentRepository;
    private final TreatmentMaterialMapper treatmentMaterialMapper;

    @Override
    @Transactional
    public TreatmentMaterialDto create(TreatmentMaterialCreateRequest request) {
        Treatment treatment = treatmentRepository.findById(request.treatmentId())
                .orElseThrow(() -> new NotFoundException("Treatment not found with id: " + request.treatmentId()));

        TreatmentMaterial treatmentMaterial = treatmentMaterialMapper.toEntity(request);
        treatmentMaterial.setTreatment(treatment);
        
        TreatmentMaterial saved = treatmentMaterialRepository.save(treatmentMaterial);
        return treatmentMaterialMapper.toDto(saved);
    }

    @Override
    public TreatmentMaterialDto findById(UUID id) {
        TreatmentMaterial treatmentMaterial = treatmentMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Treatment material not found with id: " + id));
        return treatmentMaterialMapper.toDto(treatmentMaterial);
    }

    @Override
    public List<TreatmentMaterialDto> findByTreatmentId(UUID treatmentId) {
        List<TreatmentMaterial> materials = treatmentMaterialRepository.findByTreatmentId(treatmentId);
        return treatmentMaterialMapper.toDtoList(materials);
    }

    @Override
    public Page<TreatmentMaterialDto> findByTreatmentId(UUID treatmentId, Pageable pageable) {
        Page<TreatmentMaterial> materialsPage = treatmentMaterialRepository.findByTreatmentId(treatmentId, pageable);
        return materialsPage.map(treatmentMaterialMapper::toDto);
    }

    @Override
    public List<TreatmentMaterialDto> findByPatientId(UUID patientId) {
        List<TreatmentMaterial> materials = treatmentMaterialRepository.findByPatientId(patientId);
        return treatmentMaterialMapper.toDtoList(materials);
    }

    @Override
    public Page<TreatmentMaterialDto> findByPatientId(UUID patientId, Pageable pageable) {
        Page<TreatmentMaterial> materialsPage = treatmentMaterialRepository.findByPatientId(patientId, pageable);
        return materialsPage.map(treatmentMaterialMapper::toDto);
    }

    @Override
    @Transactional
    public TreatmentMaterialDto update(UUID id, TreatmentMaterialCreateRequest request) {
        TreatmentMaterial existing = treatmentMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Treatment material not found with id: " + id));

        Treatment treatment = treatmentRepository.findById(request.treatmentId())
                .orElseThrow(() -> new NotFoundException("Treatment not found with id: " + request.treatmentId()));

        existing.setTreatment(treatment);
        existing.setMaterialName(request.materialName());
        existing.setQuantity(request.quantity());
        existing.setUnit(request.unit());
        existing.setCostPerUnit(request.costPerUnit());
        existing.setSupplier(request.supplier());
        existing.setBatchNumber(request.batchNumber());
        existing.setNotes(request.notes());

        TreatmentMaterial updated = treatmentMaterialRepository.save(existing);
        return treatmentMaterialMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!treatmentMaterialRepository.existsById(id)) {
            throw new NotFoundException("Treatment material not found with id: " + id);
        }
        treatmentMaterialRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalMaterialCostByTreatmentId(UUID treatmentId) {
        return treatmentMaterialRepository.getTotalMaterialCostByTreatmentId(treatmentId);
    }

    @Override
    public BigDecimal getTotalMaterialCostByPatientId(UUID patientId) {
        return treatmentMaterialRepository.getTotalMaterialCostByPatientId(patientId);
    }

    @Override
    public Page<TreatmentMaterialDto> searchMaterials(TreatmentMaterialSearchCriteria criteria, Pageable pageable) {
        Specification<TreatmentMaterial> spec = TreatmentMaterialSpecifications.byAdvancedCriteria(criteria);
        Page<TreatmentMaterial> materials = treatmentMaterialRepository.findAll(spec, pageable);
        return materials.map(treatmentMaterialMapper::toDto);
    }
}