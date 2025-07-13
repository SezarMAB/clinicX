package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.view.DentalChartView;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing patients and their related data.
 */
public interface PatientService {

    /**
     * Creates a new patient with auto-generated public facing ID and initializes patient teeth.
     */
    PatientSummaryDto createPatient(PatientCreateRequest request);

    /**
     * Updates an existing patient's information.
     */
    PatientSummaryDto updatePatient(UUID patientId, PatientUpdateRequest request);

    /**
     * Finds a patient by their ID.
     */
    PatientSummaryDto findPatientById(UUID patientId);

    /**
     * Finds all patients with optional search filtering and pagination.
     */
    Page<PatientSummaryDto> findAllPatients(String searchTerm, Pageable pageable);

    /**
     * Advanced search for patients with multiple criteria.
     */
    Page<PatientSummaryDto> searchPatients(PatientSearchCriteria criteria, Pageable pageable);

    /**
     * Gets the patient's balance summary.
     */
    PatientBalanceSummaryDto getPatientBalance(UUID patientId);

    /**
     * Gets the patient's dental chart.
     */
    DentalChartDto getPatientDentalChart(UUID patientId);

    /**
     * Gets upcoming appointments for a patient.
     */
    List<UpcomingAppointmentDto> getUpcomingAppointments(UUID patientId);

    /**
     * Gets patient documents with pagination.
     */
    Page<DocumentSummaryDto> getPatientDocuments(UUID patientId, Pageable pageable);

    /**
     * Gets patient notes with pagination.
     */
    Page<NoteSummaryDto> getPatientNotes(UUID patientId, Pageable pageable);

    /**
     * Gets patient treatment history with pagination.
     */
    Page<TreatmentLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable);

    /**
     * Gets patient lab requests with pagination.
     */
    Page<LabRequestDto> getPatientLabRequests(UUID patientId, Pageable pageable);

    /**
     * Gets patient financial records with pagination.
     */
    Page<FinancialRecordDto> getPatientFinancialRecords(UUID patientId, Pageable pageable);

    /**
     * Deactivates a patient (soft delete).
     */
    void deactivatePatient(UUID patientId);
}
