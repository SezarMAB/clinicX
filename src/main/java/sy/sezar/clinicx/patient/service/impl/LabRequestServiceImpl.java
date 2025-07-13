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
        log.debug("Getting lab requests for patient: {}", patientId);

        Page<LabRequest> labRequests = labRequestRepository.findByPatientIdOrderByDateSentDesc(patientId, pageable);
        return labRequests.map(labRequestMapper::toLabRequestDto);
    }

    @Override
    public LabRequestDto findLabRequestById(UUID labRequestId) {
        log.debug("Finding lab request by ID: {}", labRequestId);

        LabRequest labRequest = labRequestRepository.findById(labRequestId)
                .orElseThrow(() -> new NotFoundException("Lab request not found with ID: " + labRequestId));

        return labRequestMapper.toLabRequestDto(labRequest);
    }

    @Override
    @Transactional
    public LabRequestDto createLabRequest(UUID patientId, LabRequestDto request) {
        log.info("Creating lab request for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        LabRequest labRequest = new LabRequest();
        labRequest.setPatient(patient);
        labRequest.setOrderNumber(request.orderNumber());
        labRequest.setItemDescription(request.itemDescription());
        labRequest.setToothNumber(request.toothNumber());
        labRequest.setDateSent(request.dateSent() != null ? request.dateSent() : LocalDate.now());
        labRequest.setDateDue(request.dateDue());
        labRequest.setStatus(request.status() != null ? request.status() : LabRequestStatus.PENDING);

        LabRequest savedLabRequest = labRequestRepository.save(labRequest);
        return labRequestMapper.toLabRequestDto(savedLabRequest);
    }

    @Override
    @Transactional
    public LabRequestDto updateLabRequestStatus(UUID labRequestId, String status) {
        log.info("Updating lab request {} status to: {}", labRequestId, status);

        LabRequest labRequest = labRequestRepository.findById(labRequestId)
                .orElseThrow(() -> new NotFoundException("Lab request not found with ID: " + labRequestId));

        labRequest.setStatus(LabRequestStatus.valueOf(status.toUpperCase()));
        LabRequest updatedLabRequest = labRequestRepository.save(labRequest);

        return labRequestMapper.toLabRequestDto(updatedLabRequest);
    }
}
