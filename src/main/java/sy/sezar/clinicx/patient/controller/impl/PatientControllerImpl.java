package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.patient.controller.api.PatientControllerApi;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.service.PatientService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientControllerImpl implements PatientControllerApi {

    private final PatientService patientService;

    @Override
    public ResponseEntity<PatientSummaryDto> getPatientById(UUID id) {
        log.info("Retrieving patient with ID: {}", id);
        PatientSummaryDto patient = patientService.findPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @Override
    public ResponseEntity<Page<PatientSummaryDto>> getAllPatients(String searchTerm, Pageable pageable) {
        log.info("Retrieving patients with search term: {} and pagination: {}", searchTerm, pageable);
        Page<PatientSummaryDto> patients = patientService.findAllPatients(searchTerm, pageable);
        return ResponseEntity.ok(patients);
    }

    @Override
    public ResponseEntity<Page<PatientSummaryDto>> searchPatients(PatientSearchCriteria criteria, Pageable pageable) {
        log.info("Advanced search for patients with criteria: {}", criteria);
        Page<PatientSummaryDto> patients = patientService.searchPatients(criteria, pageable);
        return ResponseEntity.ok(patients);
    }

    @Override
    public ResponseEntity<PatientSummaryDto> createPatient(PatientCreateRequest request) {
        log.info("Creating new patient with name: {}", request.fullName());
        PatientSummaryDto patient = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @Override
    public ResponseEntity<PatientSummaryDto> updatePatient(UUID id, PatientUpdateRequest request) {
        log.info("Updating patient with ID: {}", id);
        PatientSummaryDto patient = patientService.updatePatient(id, request);
        return ResponseEntity.ok(patient);
    }

    @Override
    public ResponseEntity<Void> deletePatient(UUID id) {
        log.info("Deleting patient with ID: {}", id);
        patientService.deactivatePatient(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Page<DocumentSummaryDto>> getPatientDocuments(UUID id, Pageable pageable) {
        log.info("Retrieving documents for patient ID: {} with pagination: {}", id, pageable);
        Page<DocumentSummaryDto> documents = patientService.getPatientDocuments(id, pageable);
        return ResponseEntity.ok(documents);
    }

    @Override
    public ResponseEntity<Page<NoteSummaryDto>> getPatientNotes(UUID id, Pageable pageable) {
        log.info("Retrieving notes for patient ID: {} with pagination: {}", id, pageable);
        Page<NoteSummaryDto> notes = patientService.getPatientNotes(id, pageable);
        return ResponseEntity.ok(notes);
    }

    @Override
    public ResponseEntity<Page<TreatmentLogDto>> getPatientTreatmentHistory(UUID id, Pageable pageable) {
        log.info("Retrieving treatment history for patient ID: {} with pagination: {}", id, pageable);
        Page<TreatmentLogDto> treatments = patientService.getPatientTreatmentHistory(id, pageable);
        return ResponseEntity.ok(treatments);
    }

    @Override
    public ResponseEntity<Page<LabRequestDto>> getPatientLabRequests(UUID id, Pageable pageable) {
        log.info("Retrieving lab requests for patient ID: {} with pagination: {}", id, pageable);
        Page<LabRequestDto> labRequests = patientService.getPatientLabRequests(id, pageable);
        return ResponseEntity.ok(labRequests);
    }

    @Override
    public ResponseEntity<Page<FinancialRecordDto>> getPatientFinancialRecords(UUID id, Pageable pageable) {
        log.info("Retrieving financial records for patient ID: {} with pagination: {}", id, pageable);
        Page<FinancialRecordDto> records = patientService.getPatientFinancialRecords(id, pageable);
        return ResponseEntity.ok(records);
    }
}