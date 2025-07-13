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
        log.info("Creating new treatment for patient: {}", patientId);

        Treatment treatment = treatmentMapper.toTreatment(request);

        // Set patient, procedure, doctor from request IDs
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));
        treatment.setPatient(patient);

        Procedure procedure = procedureRepository.findById(request.procedureId())
                .orElseThrow(() -> new NotFoundException("Procedure not found with ID: " + request.procedureId()));
        treatment.setProcedure(procedure);

        Staff doctor = staffRepository.findById(request.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + request.doctorId()));
        treatment.setDoctor(doctor);

        Treatment savedTreatment = treatmentRepository.save(treatment);
        log.info("Created treatment with ID: {}", savedTreatment.getId());

        return treatmentMapper.toTreatmentLogDto(savedTreatment);
    }

    @Override
    public Page<TreatmentLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable) {
        log.debug("Getting treatment history for patient: {}", patientId);

        Page<Treatment> treatments = treatmentRepository.findByPatientIdOrderByTreatmentDateDesc(patientId, pageable);
        return treatments.map(treatmentMapper::toTreatmentLogDto);
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

        // Update treatment fields from request
        treatment.setTreatmentDate(request.treatmentDate());
        treatment.setCost(request.cost());
        treatment.setStatus(request.status());
        treatment.setTreatmentNotes(request.treatmentNotes());
        treatment.setToothNumber(request.toothNumber());

        // Update relationships if IDs changed
        if (request.procedureId() != null && !request.procedureId().equals(treatment.getProcedure().getId())) {
            Procedure procedure = procedureRepository.findById(request.procedureId())
                    .orElseThrow(() -> new NotFoundException("Procedure not found with ID: " + request.procedureId()));
            treatment.setProcedure(procedure);
        }

        if (request.doctorId() != null && !request.doctorId().equals(treatment.getDoctor().getId())) {
            Staff doctor = staffRepository.findById(request.doctorId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + request.doctorId()));
            treatment.setDoctor(doctor);
        }

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

    @Override
    public Page<TreatmentLogDto> searchTreatments(TreatmentSearchCriteria criteria, Pageable pageable) {
        log.info("Searching treatments with criteria: {}", criteria);
        Specification<Treatment> spec = TreatmentSpecifications.byAdvancedCriteria(criteria);
        Page<Treatment> treatments = treatmentRepository.findAll(spec, pageable);
        return treatments.map(treatmentMapper::toTreatmentLogDto);
    }
}
