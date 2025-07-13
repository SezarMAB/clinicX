package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.LabRequestCreateRequest;
import sy.sezar.clinicx.patient.dto.LabRequestDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lab-requests")
@Tag(name = "Lab Requests", description = "Operations related to laboratory request management")
public interface LabRequestControllerApi {

    @PostMapping
    @Operation(
        summary = "Create new lab request",
        description = "Creates a new laboratory request for a patient."
    )
    @ApiResponse(responseCode = "201", description = "Lab request created",
                content = @Content(schema = @Schema(implementation = LabRequestDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<LabRequestDto> createLabRequest(
            @Valid @RequestBody LabRequestCreateRequest request);

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient lab requests",
        description = "Retrieves paginated list of lab requests for a specific patient.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: requestDate", example = "requestDate")
        }
    )
    @ApiResponse(responseCode = "200", description = "Lab requests retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<LabRequestDto>> getPatientLabRequests(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "requestDate", direction = Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}")
    @Operation(
        summary = "Get lab request by ID",
        description = "Retrieves a specific lab request by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Lab request found",
                content = @Content(schema = @Schema(implementation = LabRequestDto.class)))
    @ApiResponse(responseCode = "404", description = "Lab request not found")
    ResponseEntity<LabRequestDto> getLabRequestById(
            @Parameter(name = "id", description = "Lab request UUID", required = true)
            @PathVariable UUID id);

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Update lab request status",
        description = "Updates the status of an existing lab request."
    )
    @ApiResponse(responseCode = "200", description = "Lab request status updated",
                content = @Content(schema = @Schema(implementation = LabRequestDto.class)))
    @ApiResponse(responseCode = "404", description = "Lab request not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    ResponseEntity<LabRequestDto> updateLabRequestStatus(
            @Parameter(name = "id", description = "Lab request UUID", required = true)
            @PathVariable UUID id,
            @Parameter(name = "status", description = "New status", required = true)
            @RequestParam String status);
}