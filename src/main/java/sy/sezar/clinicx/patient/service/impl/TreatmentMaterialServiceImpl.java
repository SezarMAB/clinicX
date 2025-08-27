package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import sy.sezar.clinicx.patient.model.Visit;
import sy.sezar.clinicx.patient.model.TreatmentMaterial;
import sy.sezar.clinicx.patient.repository.TreatmentMaterialRepository;
import sy.sezar.clinicx.patient.repository.VisitRepository;
import sy.sezar.clinicx.patient.service.TreatmentMaterialService;
import sy.sezar.clinicx.patient.spec.TreatmentMaterialSpecifications;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TreatmentMaterialServiceImpl implements TreatmentMaterialService {

    private final TreatmentMaterialRepository treatmentMaterialRepository;
    private final VisitRepository visitRepository;
    private final TreatmentMaterialMapper treatmentMaterialMapper;

    @Override
    @Transactional
    public TreatmentMaterialDto create(TreatmentMaterialCreateRequest request) {
        log.info("Creating visit material for visit ID: {} with material: {}",
                request.treatmentId(), request.materialName());
        log.debug("Visit material create request details: {}", request);

        Visit visit = visitRepository.findById(request.treatmentId())
                .orElseThrow(() -> {
                    log.error("Visit not found with ID: {}", request.treatmentId());
                    return new NotFoundException("Visit not found with id: " + request.treatmentId());
                });

        TreatmentMaterial treatmentMaterial = treatmentMaterialMapper.toEntity(request);
        treatmentMaterial.setVisit(visit);

        TreatmentMaterial saved = treatmentMaterialRepository.save(treatmentMaterial);
        log.info("Successfully created visit material with ID: {} for visit: {}",
                saved.getId(), request.treatmentId());
        log.debug("Created visit material: {} quantity: {} cost per unit: {}",
                saved.getMaterialName(), saved.getQuantity(), saved.getCostPerUnit());

        return treatmentMaterialMapper.toDto(saved);
    }

    @Override
    public TreatmentMaterialDto findById(UUID id) {
        log.debug("Finding treatment material by ID: {}", id);

        TreatmentMaterial treatmentMaterial = treatmentMaterialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Visit material not found with ID: {}", id);
                    return new NotFoundException("Visit material not found with id: " + id);
                });

        log.debug("Found treatment material: {} for treatment: {}",
                treatmentMaterial.getMaterialName(), treatmentMaterial.getVisit().getId());
        return treatmentMaterialMapper.toDto(treatmentMaterial);
    }

    @Override
    public List<TreatmentMaterialDto> findByTreatmentId(UUID treatmentId) {
        log.debug("Finding treatment materials for treatment ID: {}", treatmentId);

        List<TreatmentMaterial> materials = treatmentMaterialRepository.findByVisitId(treatmentId);
        log.debug("Found {} treatment materials for treatment: {}", materials.size(), treatmentId);

        return treatmentMaterialMapper.toDtoList(materials);
    }

    @Override
    public Page<TreatmentMaterialDto> findByTreatmentId(UUID treatmentId, Pageable pageable) {
        log.debug("Finding treatment materials for treatment ID: {} with pagination: {}", treatmentId, pageable);

        Page<TreatmentMaterial> materialsPage = treatmentMaterialRepository.findByVisitId(treatmentId, pageable);
        log.debug("Found {} treatment materials (page {} of {}) for treatment: {}",
                materialsPage.getNumberOfElements(), materialsPage.getNumber() + 1,
                materialsPage.getTotalPages(), treatmentId);

        return materialsPage.map(treatmentMaterialMapper::toDto);
    }

    @Override
    public List<TreatmentMaterialDto> findByPatientId(UUID patientId) {
        log.debug("Finding treatment materials for patient ID: {}", patientId);

        List<TreatmentMaterial> materials = treatmentMaterialRepository.findByPatientId(patientId);
        log.debug("Found {} treatment materials for patient: {}", materials.size(), patientId);

        return treatmentMaterialMapper.toDtoList(materials);
    }

    @Override
    public Page<TreatmentMaterialDto> findByPatientId(UUID patientId, Pageable pageable) {
        log.debug("Finding treatment materials for patient ID: {} with pagination: {}", patientId, pageable);

        Page<TreatmentMaterial> materialsPage = treatmentMaterialRepository.findByPatientId(patientId, pageable);
        log.debug("Found {} treatment materials (page {} of {}) for patient: {}",
                materialsPage.getNumberOfElements(), materialsPage.getNumber() + 1,
                materialsPage.getTotalPages(), patientId);

        return materialsPage.map(treatmentMaterialMapper::toDto);
    }

    @Override
    @Transactional
    public TreatmentMaterialDto update(UUID id, TreatmentMaterialCreateRequest request) {
        log.info("Updating visit material with ID: {} for visit: {}", id, request.treatmentId());
        log.debug("Visit material update request: {}", request);

        TreatmentMaterial existing = treatmentMaterialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Visit material not found with ID: {}", id);
                    return new NotFoundException("Visit material not found with id: " + id);
                });

        Visit visit = visitRepository.findById(request.treatmentId())
                .orElseThrow(() -> {
                    log.error("Visit not found with ID: {} during material update", request.treatmentId());
                    return new NotFoundException("Visit not found with id: " + request.treatmentId());
                });

        existing.setVisit(visit);
        existing.setMaterialName(request.materialName());
        existing.setQuantity(request.quantity());
        existing.setUnit(request.unit());
        existing.setCostPerUnit(request.costPerUnit());
        existing.setSupplier(request.supplier());
        existing.setBatchNumber(request.batchNumber());
        existing.setNotes(request.notes());

        TreatmentMaterial updated = treatmentMaterialRepository.save(existing);
        log.info("Successfully updated visit material with ID: {}", id);
        log.debug("Updated visit material: {} quantity: {} cost: {}",
                updated.getMaterialName(), updated.getQuantity(), updated.getCostPerUnit());

        return treatmentMaterialMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting treatment material with ID: {}", id);

        if (!treatmentMaterialRepository.existsById(id)) {
            log.error("Cannot delete - treatment material not found with ID: {}", id);
            throw new NotFoundException("Visit material not found with id: " + id);
        }

        treatmentMaterialRepository.deleteById(id);
        log.info("Successfully deleted treatment material with ID: {}", id);
    }

    @Override
    public BigDecimal getTotalMaterialCostByTreatmentId(UUID treatmentId) {
        log.debug("Calculating total material cost for treatment ID: {}", treatmentId);

        BigDecimal totalCost = treatmentMaterialRepository.getTotalMaterialCostByTreatmentId(treatmentId);
        log.debug("Total material cost for treatment {}: {}", treatmentId, totalCost);

        return totalCost;
    }

    @Override
    public BigDecimal getTotalMaterialCostByPatientId(UUID patientId) {
        log.debug("Calculating total material cost for patient ID: {}", patientId);

        BigDecimal totalCost = treatmentMaterialRepository.getTotalMaterialCostByPatientId(patientId);
        log.debug("Total material cost for patient {}: {}", patientId, totalCost);

        return totalCost;
    }

    @Override
    public Page<TreatmentMaterialDto> searchMaterials(TreatmentMaterialSearchCriteria criteria, Pageable pageable) {
        log.info("Searching treatment materials with criteria: {}", criteria);
        log.debug("Search pagination: {}", pageable);

        Specification<TreatmentMaterial> spec = TreatmentMaterialSpecifications.byAdvancedCriteria(criteria);
        Page<TreatmentMaterial> materials = treatmentMaterialRepository.findAll(spec, pageable);

        log.info("Found {} treatment materials matching search criteria (page {} of {})",
                materials.getNumberOfElements(), materials.getNumber() + 1, materials.getTotalPages());

        return materials.map(treatmentMaterialMapper::toDto);
    }
}
