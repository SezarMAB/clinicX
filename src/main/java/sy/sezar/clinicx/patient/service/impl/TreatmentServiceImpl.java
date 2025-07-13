package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.TreatmentCreateRequest;
import sy.sezar.clinicx.patient.dto.TreatmentLogDto;
import sy.sezar.clinicx.patient.dto.TreatmentSearchCriteria;
import sy.sezar.clinicx.patient.mapper.TreatmentMapper;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Procedure;
import sy.sezar.clinicx.patient.model.Treatment;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.repository.ProcedureRepository;
import sy.sezar.clinicx.patient.repository.TreatmentRepository;
import sy.sezar.clinicx.patient.service.TreatmentService;
import sy.sezar.clinicx.patient.spec.TreatmentSpecifications;
import sy.sezar.clinicx.staff.model.Staff;
import sy.sezar.clinicx.patient.repository.StaffRepository;

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
    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional
    public TreatmentLogDto createTreatment(UUID patientId, TreatmentCreateRequest request) {
        log.info("Creating new treatment for patient: {} with procedure: {}", patientId, request.procedureId());
        log.debug("Treatment creation request: {}", request);

        Treatment treatment = treatmentMapper.toTreatment(request);

        // Set patient, procedure, doctor from request IDs
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {} during treatment creation", patientId);
                    return new NotFoundException("Patient not found with ID: " + patientId);
                });
        treatment.setPatient(patient);
        log.debug("Set patient: {} for treatment", patient.getFullName());

        Procedure procedure = procedureRepository.findById(request.procedureId())
                .orElseThrow(() -> {
                    log.error("Procedure not found with ID: {} during treatment creation", request.procedureId());
                    return new NotFoundException("Procedure not found with ID: " + request.procedureId());
                });
        treatment.setProcedure(procedure);
        log.debug("Set procedure: {} for treatment", procedure.getName());

        Staff doctor = staffRepository.findById(request.doctorId())
                .orElseThrow(() -> {
                    log.error("Doctor not found with ID: {} during treatment creation", request.doctorId());
                    return new NotFoundException("Doctor not found with ID: " + request.doctorId());
                });
        treatment.setDoctor(doctor);
        log.debug("Set doctor: {} for treatment", doctor.getFullName());

        Treatment savedTreatment = treatmentRepository.save(treatment);
        log.info("Successfully created treatment with ID: {} for patient: {} (procedure: {}, cost: {})",
                savedTreatment.getId(), patientId, procedure.getName(), savedTreatment.getCost());

        return treatmentMapper.toTreatmentLogDto(savedTreatment);
    }

    @Override
    public Page<TreatmentLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable) {
        log.info("Getting treatment history for patient: {} with pagination: {}", patientId, pageable);

        Page<Treatment> treatments = treatmentRepository.findByPatientIdOrderByTreatmentDateDesc(patientId, pageable);
        log.info("Found {} treatments (page {} of {}) for patient: {}",
                treatments.getNumberOfElements(), treatments.getNumber() + 1, treatments.getTotalPages(), patientId);

        return treatments.map(treatmentMapper::toTreatmentLogDto);
    }

    @Override
    public TreatmentLogDto findTreatmentById(UUID treatmentId) {
        log.info("Finding treatment by ID: {}", treatmentId);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> {
                    log.error("Treatment not found with ID: {}", treatmentId);
                    return new NotFoundException("Treatment not found with ID: " + treatmentId);
                });

        log.debug("Found treatment: {} for patient: {} performed on: {}",
                treatment.getProcedure().getName(), treatment.getPatient().getId(), treatment.getTreatmentDate());

        return treatmentMapper.toTreatmentLogDto(treatment);
    }

    @Override
    @Transactional
    public TreatmentLogDto updateTreatment(UUID treatmentId, TreatmentCreateRequest request) {
        log.info("Updating treatment with ID: {} - new cost: {}, status: {}", treatmentId, request.cost(), request.status());
        log.debug("Treatment update request: {}", request);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> {
                    log.error("Treatment not found with ID: {} during update", treatmentId);
                    return new NotFoundException("Treatment not found with ID: " + treatmentId);
                });

        log.debug("Original treatment - Cost: {}, Status: {}, Procedure: {}",
                treatment.getCost(), treatment.getStatus(), treatment.getProcedure().getName());

        // Update treatment fields from request
        treatment.setTreatmentDate(request.treatmentDate());
        treatment.setCost(request.cost());
        treatment.setStatus(request.status());
        treatment.setTreatmentNotes(request.treatmentNotes());
        treatment.setToothNumber(request.toothNumber());

        // Update relationships if IDs changed
        if (request.procedureId() != null && !request.procedureId().equals(treatment.getProcedure().getId())) {
            log.debug("Updating procedure from {} to {}", treatment.getProcedure().getId(), request.procedureId());
            Procedure procedure = procedureRepository.findById(request.procedureId())
                    .orElseThrow(() -> {
                        log.error("Procedure not found with ID: {} during treatment update", request.procedureId());
                        return new NotFoundException("Procedure not found with ID: " + request.procedureId());
                    });
            treatment.setProcedure(procedure);
            log.debug("Updated to procedure: {}", procedure.getName());
        }

        if (request.doctorId() != null && !request.doctorId().equals(treatment.getDoctor().getId())) {
            log.debug("Updating doctor from {} to {}", treatment.getDoctor().getId(), request.doctorId());
            Staff doctor = staffRepository.findById(request.doctorId())
                    .orElseThrow(() -> {
                        log.error("Doctor not found with ID: {} during treatment update", request.doctorId());
                        return new NotFoundException("Doctor not found with ID: " + request.doctorId());
                    });
            treatment.setDoctor(doctor);
            log.debug("Updated to doctor: {}", doctor.getFullName());
        }

        Treatment updatedTreatment = treatmentRepository.save(treatment);
        log.info("Successfully updated treatment with ID: {} - Final cost: {}, status: {}",
                treatmentId, updatedTreatment.getCost(), updatedTreatment.getStatus());

        return treatmentMapper.toTreatmentLogDto(updatedTreatment);
    }

    @Override
    @Transactional
    public void deleteTreatment(UUID treatmentId) {
        log.info("Deleting treatment with ID: {}", treatmentId);

        if (!treatmentRepository.existsById(treatmentId)) {
            log.error("Cannot delete - treatment not found with ID: {}", treatmentId);
            throw new NotFoundException("Treatment not found with ID: " + treatmentId);
        }

        treatmentRepository.deleteById(treatmentId);
        log.info("Successfully deleted treatment with ID: {}", treatmentId);
    }

    @Override
    public Page<TreatmentLogDto> searchTreatments(TreatmentSearchCriteria criteria, Pageable pageable) {
        log.info("Searching treatments with criteria: {}", criteria);
        log.debug("Search pagination: {}", pageable);

        Specification<Treatment> spec = TreatmentSpecifications.byAdvancedCriteria(criteria);
        Page<Treatment> treatments = treatmentRepository.findAll(spec, pageable);

        log.info("Treatment search found {} results (page {} of {})",
                treatments.getNumberOfElements(), treatments.getNumber() + 1, treatments.getTotalPages());

        return treatments.map(treatmentMapper::toTreatmentLogDto);
    }
}
