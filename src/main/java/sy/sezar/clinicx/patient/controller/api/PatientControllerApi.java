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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Operations related to patient management")
public interface PatientControllerApi {

    @GetMapping("/{id}")
    @Operation(
        summary = "Get patient by ID",
        description = "Retrieves a patient by their unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Patient found",
                content = @Content(schema = @Schema(implementation = PatientSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<PatientSummaryDto> getPatientById(
            @Parameter(description = "Patient ID") @PathVariable UUID id);

    @GetMapping
    @Operation(
        summary = "Get all patients",
        description = "Retrieves paginated list of patients with optional search filtering.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: fullName", example = "fullName")
        }
    )
    @ApiResponse(responseCode = "200", description = "Patients retrieved")
    ResponseEntity<Page<PatientSummaryDto>> getAllPatients(
            @Parameter(name = "searchTerm", description = "Search term for filtering patients")
            @RequestParam(required = false) String searchTerm,
            @Parameter(hidden = true) @PageableDefault(sort = "fullName") Pageable pageable);

    @PostMapping("/search")
    @Operation(
        summary = "Advanced patient search",
        description = "Search patients with multiple criteria and filters.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: fullName", example = "fullName")
        }
    )
    @ApiResponse(responseCode = "200", description = "Patients retrieved")
    ResponseEntity<Page<PatientSummaryDto>> searchPatients(
            @Valid @RequestBody PatientSearchCriteria criteria,
            @Parameter(hidden = true) @PageableDefault(sort = "fullName") Pageable pageable);

    @PostMapping
    @Operation(
        summary = "Create new patient",
        description = "Creates a new patient record in the system."
    )
    @ApiResponse(responseCode = "201", description = "Patient created",
                content = @Content(schema = @Schema(implementation = PatientSummaryDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    ResponseEntity<PatientSummaryDto> createPatient(
            @Valid @RequestBody PatientCreateRequest request);

    @PutMapping("/{id}")
    @Operation(
        summary = "Update patient",
        description = "Updates an existing patient record."
    )
    @ApiResponse(responseCode = "200", description = "Patient updated",
                content = @Content(schema = @Schema(implementation = PatientSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    ResponseEntity<PatientSummaryDto> updatePatient(
            @Parameter(description = "Patient ID") @PathVariable UUID id,
            @Valid @RequestBody PatientUpdateRequest request);

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete patient",
        description = "Deletes a patient record by setting them as inactive."
    )
    @ApiResponse(responseCode = "204", description = "Patient deleted")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Void> deletePatient(
            @Parameter(description = "Patient ID") @PathVariable UUID id);

    @GetMapping("/{id}/documents")
    @Operation(
        summary = "Get patient documents",
        description = "Retrieves paginated list of documents for a specific patient.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: createdAt", example = "createdAt")
        }
    )
    @ApiResponse(responseCode = "200", description = "Documents retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<DocumentSummaryDto>> getPatientDocuments(
            @Parameter(description = "Patient ID") @PathVariable UUID id,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}/notes")
    @Operation(
        summary = "Get patient notes",
        description = "Retrieves paginated list of notes for a specific patient.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: createdAt", example = "createdAt")
        }
    )
    @ApiResponse(responseCode = "200", description = "Notes retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<NoteSummaryDto>> getPatientNotes(
            @Parameter(description = "Patient ID") @PathVariable UUID id,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}/treatments")
    @Operation(
        summary = "Get patient treatment history",
        description = "Retrieves paginated treatment history for a specific patient.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: treatmentDate", example = "treatmentDate")
        }
    )
    @ApiResponse(responseCode = "200", description = "Treatment history retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<TreatmentLogDto>> getPatientTreatmentHistory(
            @Parameter(description = "Patient ID") @PathVariable UUID id,
            @Parameter(hidden = true) @PageableDefault(sort = "treatmentDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}/lab-requests")
    @Operation(
        summary = "Get patient lab requests",
        description = "Retrieves paginated list of lab requests for a specific patient.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: requestDate", example = "requestDate")
        }
    )
    @ApiResponse(responseCode = "200", description = "Lab requests retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<LabRequestDto>> getPatientLabRequests(
            @Parameter(description = "Patient ID") @PathVariable UUID id,
            @Parameter(hidden = true) @PageableDefault(sort = "requestDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);

    @GetMapping("/{id}/financial-records")
    @Operation(
        summary = "Get patient financial records",
        description = "Retrieves paginated financial records for a specific patient.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: invoiceDate", example = "invoiceDate")
        }
    )
    @ApiResponse(responseCode = "200", description = "Financial records retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<FinancialRecordDto>> getPatientFinancialRecords(
            @Parameter(description = "Patient ID") @PathVariable UUID id,
            @Parameter(hidden = true) @PageableDefault(sort = "invoiceDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable);
}