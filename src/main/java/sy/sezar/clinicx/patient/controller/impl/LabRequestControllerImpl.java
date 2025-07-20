package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.LabRequestControllerApi;
import sy.sezar.clinicx.patient.dto.LabRequestCreateRequest;
import sy.sezar.clinicx.patient.dto.LabRequestDto;
import sy.sezar.clinicx.patient.service.LabRequestService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class LabRequestControllerImpl implements LabRequestControllerApi {

    private final LabRequestService labRequestService;

    @Override
    public ResponseEntity<LabRequestDto> createLabRequest(LabRequestCreateRequest request) {
        log.info("Creating new lab request for patient ID: {}", request.patientId());
        // Convert request to LabRequestDto to match service signature
        LabRequestDto labRequestDto = new LabRequestDto(
            null, // ID will be generated
            null, // Order number will be generated
            request.testType() + " - " + (request.instructions() != null ? request.instructions() : ""), // itemDescription
            null, // toothNumber - not provided in request
            request.requestDate(), // dateSent
            request.expectedCompletionDate(), // dateDue
            sy.sezar.clinicx.patient.model.enums.LabRequestStatus.PENDING, // Default status
            request.labName() // labName
        );
        LabRequestDto labRequest = labRequestService.createLabRequest(request.patientId(), labRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(labRequest);
    }

    @Override
    public ResponseEntity<Page<LabRequestDto>> getPatientLabRequests(UUID patientId, Pageable pageable) {
        log.info("Retrieving lab requests for patient ID: {} with pagination: {}", patientId, pageable);
        Page<LabRequestDto> labRequests = labRequestService.getPatientLabRequests(patientId, pageable);
        return ResponseEntity.ok(labRequests);
    }

    @Override
    public ResponseEntity<LabRequestDto> getLabRequestById(UUID id) {
        log.info("Retrieving lab request with ID: {}", id);
        LabRequestDto labRequest = labRequestService.findLabRequestById(id);
        return ResponseEntity.ok(labRequest);
    }

    @Override
    public ResponseEntity<LabRequestDto> updateLabRequestStatus(UUID id, String status) {
        log.info("Updating lab request status with ID: {} to status: {}", id, status);
        LabRequestDto labRequest = labRequestService.updateLabRequestStatus(id, status);
        return ResponseEntity.ok(labRequest);
    }
}