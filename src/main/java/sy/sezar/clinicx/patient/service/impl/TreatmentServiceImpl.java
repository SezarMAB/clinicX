package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;
import sy.sezar.clinicx.patient.mapper.TreatmentMapper;
import sy.sezar.clinicx.patient.model.Treatment;
import sy.sezar.clinicx.patient.repository.TreatmentRepository;
import sy.sezar.clinicx.patient.service.TreatmentService;

import java.util.UUID;

/**
 * Implementation of TreatmentService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper treatmentMapper;

    @Override
    @Transactional
    public TreatmentLogDto createTreatment(TreatmentCreateRequest request) {
        log.info("Creating new treatment for patient");

        Treatment treatment = treatmentMapper.toTreatment(request);
        // TODO: Set patient, procedure, doctor, appointment from request IDs when repositories are available

        Treatment savedTreatment = treatmentRepository.save(treatment);
        log.info("Created treatment with ID: {}", savedTreatment.getId());

        return treatmentMapper.toTreatmentLogDto(savedTreatment);
    }

    @Override
    public Page<TreatmentLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable) {
        log.debug("Getting treatment history for patient: {}", patientId);

        // TODO: Implement when TreatmentRepository has findByPatientId method
        throw new UnsupportedOperationException("Patient treatment history not yet implemented");
    }

    @Override
    public TreatmentLogDto findTreatmentById(UUID treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new NotFoundException("Treatment not found with ID: " + treatmentId));

        return treatmentMapper.toTreatmentLogDto(treatment);
    }

    @Override
    @Transactional
    public TreatmentLogDto updateTreatment(UUID treatmentId, TreatmentCreateRequest request) {
        log.info("Updating treatment with ID: {}", treatmentId);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new NotFoundException("Treatment not found with ID: " + treatmentId));

        // TODO: Update treatment fields from request
        Treatment updatedTreatment = treatmentRepository.save(treatment);

        return treatmentMapper.toTreatmentLogDto(updatedTreatment);
    }

    @Override
    @Transactional
    public void deleteTreatment(UUID treatmentId) {
        log.info("Deleting treatment with ID: {}", treatmentId);

        if (!treatmentRepository.existsById(treatmentId)) {
            throw new NotFoundException("Treatment not found with ID: " + treatmentId);
        }

        treatmentRepository.deleteById(treatmentId);
    }
}
