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
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.service.PatientService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Patients", description = "Operations related to patient management")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(
        summary = "Create a new patient",
        description = "Generates a publicFacingId, initialises dental chart records, and returns the created patient."
    )
    @ApiResponse(responseCode = "201", description = "Patient created",
                content = @Content(schema = @Schema(implementation = PatientSummaryDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<PatientSummaryDto> createPatient(
            @Valid @RequestBody PatientCreateRequest request) {
        log.info("Creating new patient with name: {}", request.fullName());
        PatientSummaryDto patient = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update patient information",
        description = "Updates an existing patient's information by ID."
    )
    @ApiResponse(responseCode = "200", description = "Patient updated",
                content = @Content(schema = @Schema(implementation = PatientSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<PatientSummaryDto> updatePatient(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody PatientUpdateRequest request) {
        log.info("Updating patient with ID: {}", id);
        PatientSummaryDto patient = patientService.updatePatient(id, request);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get patient by ID",
        description = "Retrieves a patient's basic information by their UUID."
    )
    @ApiResponse(responseCode = "200", description = "Patient found",
                content = @Content(schema = @Schema(implementation = PatientSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<PatientSummaryDto> getPatientById(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving patient with ID: {}", id);
        PatientSummaryDto patient = patientService.findPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    @Operation(
        summary = "Get all patients",
        description = "Retrieves paginated list of patients with optional search filtering."
    )
    @ApiResponse(responseCode = "200", description = "Patients retrieved")
    public ResponseEntity<Page<PatientSummaryDto>> getAllPatients(
            @Parameter(name = "searchTerm", description = "Search term for filtering patients")
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        log.info("Retrieving patients with search term: {} and pagination: {}", searchTerm, pageable);
        Page<PatientSummaryDto> patients = patientService.findAllPatients(searchTerm, pageable);
        return ResponseEntity.ok(patients);
    }

    @PostMapping("/search")
    @Operation(
        summary = "Advanced patient search",
        description = "Search patients with multiple criteria and filters."
    )
    @ApiResponse(responseCode = "200", description = "Patients retrieved")
    public ResponseEntity<Page<PatientSummaryDto>> searchPatients(
            @Valid @RequestBody PatientSearchCriteria criteria,
            Pageable pageable) {
        log.info("Advanced search for patients with criteria: {}", criteria);
        Page<PatientSummaryDto> patients = patientService.searchPatients(criteria, pageable);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}/balance")
    @Operation(
        summary = "Get patient balance summary",
        description = "Retrieves the financial balance summary for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Balance retrieved",
                content = @Content(schema = @Schema(implementation = PatientBalanceSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<PatientBalanceSummaryDto> getPatientBalance(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving balance for patient ID: {}", id);
        PatientBalanceSummaryDto balance = patientService.getPatientBalance(id);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{id}/dental-chart")
    @Operation(
        summary = "Get patient dental chart",
        description = "Retrieves the dental chart showing tooth conditions for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Dental chart retrieved",
                content = @Content(schema = @Schema(implementation = DentalChartDto.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<DentalChartDto> getPatientDentalChart(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving dental chart for patient ID: {}", id);
        DentalChartDto dentalChart = patientService.getPatientDentalChart(id);
        return ResponseEntity.ok(dentalChart);
    }

    @GetMapping("/{id}/upcoming-appointments")
    @Operation(
        summary = "Get patient upcoming appointments",
        description = "Retrieves upcoming appointments for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Upcoming appointments retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<List<UpcomingAppointmentDto>> getUpcomingAppointments(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving upcoming appointments for patient ID: {}", id);
        List<UpcomingAppointmentDto> appointments = patientService.getUpcomingAppointments(id);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}/documents")
    @Operation(
        summary = "Get patient documents",
        description = "Retrieves paginated list of documents for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Documents retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<DocumentSummaryDto>> getPatientDocuments(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id,
            Pageable pageable) {
        log.info("Retrieving documents for patient ID: {} with pagination: {}", id, pageable);
        Page<DocumentSummaryDto> documents = patientService.getPatientDocuments(id, pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}/notes")
    @Operation(
        summary = "Get patient notes",
        description = "Retrieves paginated list of notes for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Notes retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<NoteSummaryDto>> getPatientNotes(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id,
            Pageable pageable) {
        log.info("Retrieving notes for patient ID: {} with pagination: {}", id, pageable);
        Page<NoteSummaryDto> notes = patientService.getPatientNotes(id, pageable);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}/treatments")
    @Operation(
        summary = "Get patient treatment history",
        description = "Retrieves paginated treatment history for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Treatment history retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<TreatmentLogDto>> getPatientTreatmentHistory(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id,
            Pageable pageable) {
        log.info("Retrieving treatment history for patient ID: {} with pagination: {}", id, pageable);
        Page<TreatmentLogDto> treatments = patientService.getPatientTreatmentHistory(id, pageable);
        return ResponseEntity.ok(treatments);
    }

    @GetMapping("/{id}/lab-requests")
    @Operation(
        summary = "Get patient lab requests",
        description = "Retrieves paginated lab requests for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Lab requests retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<LabRequestDto>> getPatientLabRequests(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id,
            Pageable pageable) {
        log.info("Retrieving lab requests for patient ID: {} with pagination: {}", id, pageable);
        Page<LabRequestDto> labRequests = patientService.getPatientLabRequests(id, pageable);
        return ResponseEntity.ok(labRequests);
    }

    @GetMapping("/{id}/financial-records")
    @Operation(
        summary = "Get patient financial records",
        description = "Retrieves paginated financial records for a patient."
    )
    @ApiResponse(responseCode = "200", description = "Financial records retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<FinancialRecordDto>> getPatientFinancialRecords(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id,
            Pageable pageable) {
        log.info("Retrieving financial records for patient ID: {} with pagination: {}", id, pageable);
        Page<FinancialRecordDto> financialRecords = patientService.getPatientFinancialRecords(id, pageable);
        return ResponseEntity.ok(financialRecords);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deactivate patient",
        description = "Deactivates a patient (soft delete) by setting their status to inactive."
    )
    @ApiResponse(responseCode = "204", description = "Patient deactivated")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Void> deactivatePatient(
            @Parameter(name = "id", description = "Patient UUID", required = true)
            @PathVariable UUID id) {
        log.info("Deactivating patient with ID: {}", id);
        patientService.deactivatePatient(id);
        return ResponseEntity.noContent().build();
    }
}
