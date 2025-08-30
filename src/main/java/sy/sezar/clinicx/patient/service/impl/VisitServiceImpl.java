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
import sy.sezar.clinicx.patient.model.VisitProcedure;
import sy.sezar.clinicx.patient.model.enums.ProcedureStatus;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.repository.ProcedureRepository;
import sy.sezar.clinicx.patient.repository.VisitRepository;
import sy.sezar.clinicx.patient.service.VisitService;
import sy.sezar.clinicx.patient.spec.VisitSpecifications;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.repository.StaffRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageImpl;

/**
 * Implementation of VisitService with business logic.
 * Temporarily simplified for new Visit model migration.
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
    public VisitLogDto createVisit(UUID patientId, VisitCreateRequest request) {
        log.info("Creating new visit for patient: {}", patientId);
        
        // Create new Visit (header only)
        Visit visit = visitMapper.toVisit(request);
        
        // Set patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found: " + patientId));
        visit.setPatient(patient);
        
        // Set provider (doctor)
        Staff provider = staffRepository.findById(request.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found: " + request.doctorId()));
        visit.setProvider(provider);
        
        // Set visit date and time
        visit.setDate(request.visitDate());
        visit.setTime(request.visitTime());
        visit.setNotes(request.visitNotes());
        
        // Create procedure from old request structure
        if (request.procedureId() != null) {
            Procedure procedure = procedureRepository.findById(request.procedureId())
                    .orElseThrow(() -> new NotFoundException("Procedure not found: " + request.procedureId()));
            
            // Create VisitProcedure
            VisitProcedure visitProcedure = VisitProcedure.builder()
                .code(procedure.getProcedureCode())
                .name(procedure.getName())
                .toothNumber(request.toothNumber())
                .quantity(request.quantity() != null ? request.quantity() : 1)
                .unitFee(request.cost())
                .durationMinutes(request.durationMinutes())
                .performedBy(provider)
                .status(mapTreatmentStatusToProcedureStatus(request.status()))
                .billable(true)
                .notes(request.visitNotes())
                .build();
            
            visit.addProcedure(visitProcedure);
        }
        
        Visit savedVisit = visitRepository.save(visit);
        
        log.info("Successfully created visit: {} for patient: {}", 
                savedVisit.getId(), patientId);
        
        return visitMapper.toVisitLogDto(savedVisit);
    }

    @Override
    public Page<VisitLogDto> getPatientVisitHistory(UUID patientId, Pageable pageable) {
        log.debug("Fetching visits for patient: {} with pageable: {}", patientId, pageable);
        
        // Use findAll with specifications or manual filtering for now
        List<Visit> allVisits = visitRepository.findByPatientId(patientId);
        
        // Convert to page manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allVisits.size());
        
        List<Visit> pageContent = allVisits.subList(start, end);
        Page<Visit> visits = new PageImpl<>(pageContent, pageable, allVisits.size());
        
        log.debug("Found {} visits for patient: {}", visits.getTotalElements(), patientId);
        
        return visits.map(visitMapper::toVisitLogDto);
    }

    @Override
    public VisitLogDto findVisitById(UUID visitId) {
        log.debug("Fetching visit details for: {}", visitId);
        
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new NotFoundException("Visit not found: " + visitId));
        
        log.debug("Found visit: {} for patient: {}", 
                visit.getId(), visit.getPatient().getId());
        
        return visitMapper.toVisitLogDto(visit);
    }

    @Override
    @Transactional
    public VisitLogDto updateVisit(UUID visitId, VisitCreateRequest request) {
        log.info("Updating visit: {}", visitId);
        log.debug("Update request: {}", request);
        
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new NotFoundException("Visit not found: " + visitId));
        
        // Update visit header fields
        visit.setDate(request.visitDate());
        visit.setTime(request.visitTime());
        visit.setNotes(request.visitNotes());
        
        // Update provider if changed
        if (request.doctorId() != null && !request.doctorId().equals(visit.getProvider().getId())) {
            Staff provider = staffRepository.findById(request.doctorId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found: " + request.doctorId()));
            visit.setProvider(provider);
        }
        
        // For now, update the first procedure if exists
        if (!visit.getProcedures().isEmpty() && request.procedureId() != null) {
            VisitProcedure firstProcedure = visit.getProcedures().iterator().next();
            
            // Update procedure details
            firstProcedure.setToothNumber(request.toothNumber());
            firstProcedure.setQuantity(request.quantity() != null ? request.quantity() : 1);
            firstProcedure.setUnitFee(request.cost());
            firstProcedure.setStatus(mapTreatmentStatusToProcedureStatus(request.status()));
            firstProcedure.setNotes(request.visitNotes());
            
            if (request.procedureId() != null) {
                Procedure procedure = procedureRepository.findById(request.procedureId())
                        .orElseThrow(() -> new NotFoundException("Procedure not found: " + request.procedureId()));
                firstProcedure.setCode(procedure.getProcedureCode());
                firstProcedure.setName(procedure.getName());
            }
        }
        
        Visit updatedVisit = visitRepository.save(visit);
        
        log.info("Successfully updated visit: {}", visitId);
        
        return visitMapper.toVisitLogDto(updatedVisit);
    }

    @Override
    @Transactional
    public void deleteVisit(UUID visitId) {
        log.info("Deleting visit: {}", visitId);
        
        if (!visitRepository.existsById(visitId)) {
            throw new NotFoundException("Visit not found: " + visitId);
        }
        
        visitRepository.deleteById(visitId);
        
        log.info("Successfully deleted visit: {}", visitId);
    }

    @Override
    public Page<VisitLogDto> searchVisits(VisitSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching visits with criteria: {} and pageable: {}", criteria, pageable);
        
        // Simplified implementation - just return all visits for now
        // This would need proper implementation with specifications
        Page<Visit> visits = visitRepository.findAll(pageable);
        
        log.debug("Found {} visits matching criteria", visits.getTotalElements());
        
        return visits.map(visitMapper::toVisitLogDto);
    }
    
    /**
     * Helper method to map old TreatmentStatus to new ProcedureStatus
     */
    private ProcedureStatus mapTreatmentStatusToProcedureStatus(sy.sezar.clinicx.patient.model.enums.TreatmentStatus treatmentStatus) {
        if (treatmentStatus == null) {
            return ProcedureStatus.PLANNED;
        }
        
        switch (treatmentStatus) {
            case COMPLETED:
                return ProcedureStatus.COMPLETED;
            case IN_PROGRESS:
                return ProcedureStatus.IN_PROGRESS;
            case CANCELLED:
                return ProcedureStatus.CANCELLED;
            case PLANNED:
            default:
                return ProcedureStatus.PLANNED;
        }
    }
}