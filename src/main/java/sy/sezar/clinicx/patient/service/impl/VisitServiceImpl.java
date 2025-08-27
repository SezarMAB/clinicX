package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.VisitCreateRequest;
import sy.sezar.clinicx.patient.dto.VisitLogDto;
import sy.sezar.clinicx.patient.dto.VisitSearchCriteria;
import sy.sezar.clinicx.patient.mapper.VisitMapper;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Procedure;
import sy.sezar.clinicx.patient.model.Visit;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.repository.ProcedureRepository;
import sy.sezar.clinicx.patient.repository.VisitRepository;
import sy.sezar.clinicx.patient.service.VisitService;
import sy.sezar.clinicx.patient.spec.VisitSpecifications;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.repository.StaffRepository;

import java.util.UUID;

/**
 * Implementation of VisitService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional
    public VisitLogDto createTreatment(UUID patientId, VisitCreateRequest request) {
        log.info("Creating new visit for patient: {} with procedure: {}", patientId, request.procedureId());
        log.debug("Visit creation request: {}", request);

        Visit visit = visitMapper.toTreatment(request);

        // Set patient, procedure, doctor from request IDs
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {} during visit creation", patientId);
                    return new NotFoundException("Patient not found with ID: " + patientId);
                });
        visit.setPatient(patient);
        log.debug("Set patient: {} for visit", patient.getFullName());

        Procedure procedure = procedureRepository.findById(request.procedureId())
                .orElseThrow(() -> {
                    log.error("Procedure not found with ID: {} during visit creation", request.procedureId());
                    return new NotFoundException("Procedure not found with ID: " + request.procedureId());
                });
        visit.setProcedure(procedure);
        log.debug("Set procedure: {} for visit", procedure.getName());

        Staff doctor = staffRepository.findById(request.doctorId())
                .orElseThrow(() -> {
                    log.error("Doctor not found with ID: {} during visit creation", request.doctorId());
                    return new NotFoundException("Doctor not found with ID: " + request.doctorId());
                });
        visit.setDoctor(doctor);
        log.debug("Set doctor: {} for visit", doctor.getFullName());

        Visit savedVisit = visitRepository.save(visit);
        log.info("Successfully created visit with ID: {} for patient: {} (procedure: {}, cost: {})",
                savedVisit.getId(), patientId, procedure.getName(), savedVisit.getCost());

        return visitMapper.toTreatmentLogDto(savedVisit);
    }

    @Override
    public Page<VisitLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable) {
        log.info("Getting treatment history for patient: {} with pagination: {}", patientId, pageable);

        Page<Visit> treatments = visitRepository.findByPatientIdOrderByVisitDateDesc(patientId, pageable);
        log.info("Found {} treatments (page {} of {}) for patient: {}",
                treatments.getNumberOfElements(), treatments.getNumber() + 1, treatments.getTotalPages(), patientId);

        return treatments.map(visitMapper::toTreatmentLogDto);
    }

    @Override
    public VisitLogDto findTreatmentById(UUID treatmentId) {
        log.info("Finding visit by ID: {}", treatmentId);

        Visit visit = visitRepository.findById(treatmentId)
                .orElseThrow(() -> {
                    log.error("Visit not found with ID: {}", treatmentId);
                    return new NotFoundException("Visit not found with ID: " + treatmentId);
                });

        log.debug("Found visit: {} for patient: {} performed on: {}",
                visit.getProcedure().getName(), visit.getPatient().getId(), visit.getVisitDate());

        return visitMapper.toTreatmentLogDto(visit);
    }

    @Override
    @Transactional
    public VisitLogDto updateTreatment(UUID treatmentId, VisitCreateRequest request) {
        log.info("Updating visit with ID: {} - new cost: {}, status: {}", treatmentId, request.cost(), request.status());
        log.debug("Visit update request: {}", request);

        Visit visit = visitRepository.findById(treatmentId)
                .orElseThrow(() -> {
                    log.error("Visit not found with ID: {} during update", treatmentId);
                    return new NotFoundException("Visit not found with ID: " + treatmentId);
                });

        log.debug("Original visit - Cost: {}, Status: {}, Procedure: {}",
                visit.getCost(), visit.getStatus(), visit.getProcedure().getName());

        // Update visit fields from request
        visit.setVisitDate(request.visitDate());
        visit.setCost(request.cost());
        visit.setStatus(request.status());
        visit.setVisitNotes(request.visitNotes());
        visit.setToothNumber(request.toothNumber());

        // Update relationships if IDs changed
        if (request.procedureId() != null && !request.procedureId().equals(visit.getProcedure().getId())) {
            log.debug("Updating procedure from {} to {}", visit.getProcedure().getId(), request.procedureId());
            Procedure procedure = procedureRepository.findById(request.procedureId())
                    .orElseThrow(() -> {
                        log.error("Procedure not found with ID: {} during visit update", request.procedureId());
                        return new NotFoundException("Procedure not found with ID: " + request.procedureId());
                    });
            visit.setProcedure(procedure);
            log.debug("Updated to procedure: {}", procedure.getName());
        }

        if (request.doctorId() != null && !request.doctorId().equals(visit.getDoctor().getId())) {
            log.debug("Updating doctor from {} to {}", visit.getDoctor().getId(), request.doctorId());
            Staff doctor = staffRepository.findById(request.doctorId())
                    .orElseThrow(() -> {
                        log.error("Doctor not found with ID: {} during visit update", request.doctorId());
                        return new NotFoundException("Doctor not found with ID: " + request.doctorId());
                    });
            visit.setDoctor(doctor);
            log.debug("Updated to doctor: {}", doctor.getFullName());
        }

        Visit updatedVisit = visitRepository.save(visit);
        log.info("Successfully updated visit with ID: {} - Final cost: {}, status: {}",
                treatmentId, updatedVisit.getCost(), updatedVisit.getStatus());

        return visitMapper.toTreatmentLogDto(updatedVisit);
    }

    @Override
    @Transactional
    public void deleteTreatment(UUID treatmentId) {
        log.info("Deleting treatment with ID: {}", treatmentId);

        if (!visitRepository.existsById(treatmentId)) {
            log.error("Cannot delete - treatment not found with ID: {}", treatmentId);
            throw new NotFoundException("Visit not found with ID: " + treatmentId);
        }

        visitRepository.deleteById(treatmentId);
        log.info("Successfully deleted treatment with ID: {}", treatmentId);
    }

    @Override
    public Page<VisitLogDto> searchTreatments(VisitSearchCriteria criteria, Pageable pageable) {
        log.info("Searching treatments with criteria: {}", criteria);
        log.debug("Search pagination: {}", pageable);

        Specification<Visit> spec = VisitSpecifications.byAdvancedCriteria(criteria);
        Page<Visit> treatments = visitRepository.findAll(spec, pageable);

        log.info("Visit search found {} results (page {} of {})",
                treatments.getNumberOfElements(), treatments.getNumber() + 1, treatments.getTotalPages());

        return treatments.map(visitMapper::toTreatmentLogDto);
    }
}
