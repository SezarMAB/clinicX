package sy.sezar.clinicx.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.LabRequestCreateRequest;
import sy.sezar.clinicx.patient.dto.LabRequestDto;
import sy.sezar.clinicx.patient.dto.LabRequestUpdateRequest;
import sy.sezar.clinicx.patient.service.LabRequestService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lab-requests")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Lab Requests", description = "Operations related to laboratory request management")
public class LabRequestController {

    private final LabRequestService labRequestService;

    @PostMapping
    @Operation(
        summary = "Create new lab request",
        description = "Creates a new laboratory request for a patient."
    )
    @ApiResponse(responseCode = "201", description = "Lab request created",
                content = @Content(schema = @Schema(implementation = LabRequestDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<LabRequestDto> createLabRequest(
            @Valid @RequestBody LabRequestCreateRequest request) {
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

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient lab requests",
        description = "Retrieves paginated list of lab requests for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Lab requests retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<LabRequestDto>> getPatientLabRequests(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            Pageable pageable) {
        log.info("Retrieving lab requests for patient ID: {} with pagination: {}", patientId, pageable);
        Page<LabRequestDto> labRequests = labRequestService.getPatientLabRequests(patientId, pageable);
        return ResponseEntity.ok(labRequests);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get lab request by ID",
        description = "Retrieves a specific lab request by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Lab request found",
                content = @Content(schema = @Schema(implementation = LabRequestDto.class)))
    @ApiResponse(responseCode = "404", description = "Lab request not found")
    public ResponseEntity<LabRequestDto> getLabRequestById(
            @Parameter(name = "id", description = "Lab request UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving lab request with ID: {}", id);
        LabRequestDto labRequest = labRequestService.findLabRequestById(id);
        return ResponseEntity.ok(labRequest);
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Update lab request status",
        description = "Updates the status of an existing lab request."
    )
    @ApiResponse(responseCode = "200", description = "Lab request status updated",
                content = @Content(schema = @Schema(implementation = LabRequestDto.class)))
    @ApiResponse(responseCode = "404", description = "Lab request not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<LabRequestDto> updateLabRequestStatus(
            @Parameter(name = "id", description = "Lab request UUID", required = true)
            @PathVariable UUID id,
            @Parameter(name = "status", description = "New status", required = true)
            @RequestParam String status) {
        log.info("Updating lab request status with ID: {} to status: {}", id, status);
        LabRequestDto labRequest = labRequestService.updateLabRequestStatus(id, status);
        return ResponseEntity.ok(labRequest);
    }
}
