package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.LabRequestDto;
import sy.sezar.clinicx.patient.mapper.LabRequestMapper;
import sy.sezar.clinicx.patient.model.LabRequest;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.enums.LabRequestStatus;
import sy.sezar.clinicx.patient.repository.LabRequestRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.service.LabRequestService;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Implementation of LabRequestService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabRequestServiceImpl implements LabRequestService {

    private final LabRequestRepository labRequestRepository;
    private final PatientRepository patientRepository;
    private final LabRequestMapper labRequestMapper;

    @Override
    public Page<LabRequestDto> getPatientLabRequests(UUID patientId, Pageable pageable) {
        log.info("Getting lab requests for patient: {} with pagination: {}", patientId, pageable);

        Page<LabRequest> labRequests = labRequestRepository.findByPatientIdOrderByDateSentDesc(patientId, pageable);
        log.info("Found {} lab requests (page {} of {}) for patient: {}",
                labRequests.getNumberOfElements(), labRequests.getNumber() + 1,
                labRequests.getTotalPages(), patientId);

        return labRequests.map(labRequestMapper::toLabRequestDto);
    }

    @Override
    public LabRequestDto findLabRequestById(UUID labRequestId) {
        log.info("Finding lab request by ID: {}", labRequestId);

        LabRequest labRequest = labRequestRepository.findById(labRequestId)
                .orElseThrow(() -> {
                    log.error("Lab request not found with ID: {}", labRequestId);
                    return new NotFoundException("Lab request not found with ID: " + labRequestId);
                });

        log.debug("Found lab request: {} for patient: {} (status: {})",
                labRequest.getOrderNumber(), labRequest.getPatient().getId(), labRequest.getStatus());

        return labRequestMapper.toLabRequestDto(labRequest);
    }

    @Override
    @Transactional
    public LabRequestDto createLabRequest(UUID patientId, LabRequestDto request) {
        log.info("Creating lab request for patient: {} (order: {}, item: {})",
                patientId, request.orderNumber(), request.itemDescription());
        log.debug("Lab request creation details: {}", request);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {} during lab request creation", patientId);
                    return new NotFoundException("Patient not found with ID: " + patientId);
                });

        log.debug("Found patient: {} for lab request creation", patient.getFullName());

        // Set default values and log business logic decisions
        LocalDate dateSent = request.dateSent() != null ? request.dateSent() : LocalDate.now();
        LabRequestStatus status = request.status() != null ? request.status() : LabRequestStatus.PENDING;

        if (request.dateSent() == null) {
            log.debug("No date sent provided, using current date: {}", dateSent);
        }
        if (request.status() == null) {
            log.debug("No status provided, setting to PENDING");
        }

        LabRequest labRequest = new LabRequest();
        labRequest.setPatient(patient);
        labRequest.setOrderNumber(request.orderNumber());
        labRequest.setItemDescription(request.itemDescription());
        labRequest.setToothNumber(request.toothNumber());
        labRequest.setDateSent(dateSent);
        labRequest.setDateDue(request.dateDue());
        labRequest.setStatus(status);

        LabRequest savedLabRequest = labRequestRepository.save(labRequest);
        log.info("Successfully created lab request with ID: {} for patient: {} (order: {}, status: {})",
                savedLabRequest.getId(), patientId, savedLabRequest.getOrderNumber(), savedLabRequest.getStatus());

        return labRequestMapper.toLabRequestDto(savedLabRequest);
    }

    @Override
    @Transactional
    public LabRequestDto updateLabRequestStatus(UUID labRequestId, String status) {
        log.info("Updating lab request {} status to: {}", labRequestId, status);

        LabRequest labRequest = labRequestRepository.findById(labRequestId)
                .orElseThrow(() -> {
                    log.error("Lab request not found with ID: {} during status update", labRequestId);
                    return new NotFoundException("Lab request not found with ID: " + labRequestId);
                });

        LabRequestStatus oldStatus = labRequest.getStatus();
        LabRequestStatus newStatus;

        try {
            newStatus = LabRequestStatus.valueOf(status.toUpperCase());
            log.debug("Status transition for lab request {}: {} -> {}", labRequestId, oldStatus, newStatus);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status '{}' provided for lab request: {}", status, labRequestId);
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        labRequest.setStatus(newStatus);
        LabRequest updatedLabRequest = labRequestRepository.save(labRequest);

        log.info("Successfully updated lab request {} status from {} to {} (order: {})",
                labRequestId, oldStatus, newStatus, updatedLabRequest.getOrderNumber());

        return labRequestMapper.toLabRequestDto(updatedLabRequest);
    }
}
