package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.*;
import sy.sezar.clinicx.patient.mapper.*;
import sy.sezar.clinicx.patient.model.*;
import sy.sezar.clinicx.patient.repository.*;
import sy.sezar.clinicx.patient.service.PatientService;
import sy.sezar.clinicx.patient.spec.PatientSpecifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import sy.sezar.clinicx.patient.view.DentalChartView;

/**
 * Implementation of PatientService with business logic and transaction management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DocumentRepository documentRepository;
    private final TreatmentRepository treatmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final NoteRepository noteRepository;
    private final LabRequestRepository labRequestRepository;
    private final InvoiceRepository invoiceRepository;
    private final PatientToothRepository patientToothRepository;
    private final ToothConditionRepository toothConditionRepository;
    private final UpcomingAppointmentsViewRepository upcomingAppointmentsViewRepository;
    private final DentalChartViewRepository dentalChartViewRepository;
    private final PatientFinancialSummaryViewRepository financialSummaryViewRepository;
    private final PatientCentralMapper patientMapper;
    private final DocumentMapper documentMapper;
    private final TreatmentMapper treatmentMapper;
    private final AppointmentMapper appointmentMapper;
    private final DentalChartMapper dentalChartMapper;
    private final NoteSummaryMapper noteSummaryMapper;
    private final LabRequestMapper labRequestMapper;

    @Override
    @Transactional
    public PatientSummaryDto createPatient(PatientCreateRequest request) {
        log.info("Creating new patient with name: {}", request.fullName());

        Patient patient = patientMapper.toPatient(request);
        patient.setPublicFacingId(generatePublicFacingId());
        patient.setActive(true);

        Patient savedPatient = patientRepository.save(patient);

        // Initialize 32 patient teeth rows
        initializePatientTeeth(savedPatient);

        log.info("Created patient with ID: {} and public ID: {}", savedPatient.getId(), savedPatient.getPublicFacingId());
        return patientMapper.toPatientSummaryDto(savedPatient);
    }

    @Override
    @Transactional
    public PatientSummaryDto updatePatient(UUID patientId, PatientUpdateRequest request) {
        log.info("Updating patient with ID: {}", patientId);

        Patient patient = findPatientEntityById(patientId);
        patientMapper.updatePatientFromRequest(request, patient);

        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toPatientSummaryDto(updatedPatient);
    }

    @Override
    public PatientSummaryDto findPatientById(UUID patientId) {
        Patient patient = findPatientEntityById(patientId);
        return patientMapper.toPatientSummaryDto(patient);
    }

    @Override
    public Page<PatientSummaryDto> findAllPatients(String searchTerm, Pageable pageable) {
        Specification<Patient> spec = null;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            spec = PatientSpecifications.bySearchTerm(searchTerm.trim());
        }

        Page<Patient> patients = patientRepository.findAll(spec, pageable);
        return patients.map(patientMapper::toPatientSummaryDto);
    }

    @Override
    public PatientBalanceSummaryDto getPatientBalance(UUID patientId) {
        Patient patient = findPatientEntityById(patientId);
        return patientMapper.toPatientBalanceSummaryDto(patient);
    }

    @Override
    public DentalChartDto getPatientDentalChart(UUID patientId) {
        List<DentalChartView> teeth = dentalChartViewRepository.findByPatientIdOrderByToothNumber(patientId);
        return dentalChartMapper.toDentalChartDtoFromView(teeth);
    }

    @Override
    public List<UpcomingAppointmentDto> getUpcomingAppointments(UUID patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointmentMapper.toUpcomingAppointmentDtoList(appointments);
    }

    @Override
    public Page<DocumentSummaryDto> getPatientDocuments(UUID patientId, Pageable pageable) {
        Page<Document> documents = documentRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
        return documents.map(documentMapper::toDocumentSummaryDto);
    }

    @Override
    public Page<NoteSummaryDto> getPatientNotes(UUID patientId, Pageable pageable) {
        Page<Note> notes = noteRepository.findByPatientIdOrderByNoteDateDesc(patientId, pageable);
        return notes.map(noteSummaryMapper::toNoteSummaryDto);
    }

    @Override
    public Page<TreatmentLogDto> getPatientTreatmentHistory(UUID patientId, Pageable pageable) {
        Page<Treatment> treatments = treatmentRepository.findByPatientIdOrderByTreatmentDateDesc(patientId, pageable);
        return treatments.map(treatmentMapper::toTreatmentLogDto);
    }

    @Override
    public Page<LabRequestDto> getPatientLabRequests(UUID patientId, Pageable pageable) {
        Page<LabRequest> labRequests = labRequestRepository.findByPatientIdOrderByDateSentDesc(patientId, pageable);
        return labRequests.map(labRequestMapper::toLabRequestDto);
    }

    @Override
    public Page<FinancialRecordDto> getPatientFinancialRecords(UUID patientId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByPatientId(patientId, pageable);
        return invoices.map(this::mapToFinancialRecordDto);
    }

    @Override
    @Transactional
    public void deactivatePatient(UUID patientId) {
        log.info("Deactivating patient with ID: {}", patientId);

        Patient patient = findPatientEntityById(patientId);
        patient.setActive(false);
        patientRepository.save(patient);
    }

    private Patient findPatientEntityById(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));
    }

    private String generatePublicFacingId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "P" + timestamp;
    }

    private void initializePatientTeeth(Patient patient) {
        log.debug("Initializing 32 teeth for patient ID: {}", patient.getId());

        // Get default healthy tooth condition, or create a fallback
        ToothCondition defaultCondition = toothConditionRepository.findDefaultHealthyCondition()
                .orElseGet(() -> {
                    log.warn("No default healthy tooth condition found, creating fallback");
                    // Return null for now - in production this should create a default condition
                    return null;
                });

        List<PatientTooth> teethToSave = new ArrayList<>();
        for (int toothNumber = 1; toothNumber <= 32; toothNumber++) {
            PatientTooth tooth = new PatientTooth();
            tooth.setPatient(patient);
            tooth.setToothNumber(toothNumber);
            tooth.setCurrentCondition(defaultCondition);
            teethToSave.add(tooth);
        }

        patientToothRepository.saveAll(teethToSave);
        log.debug("Patient teeth initialization completed for patient ID: {}, saved {} teeth",
                  patient.getId(), teethToSave.size());
    }

    private FinancialRecordDto mapToFinancialRecordDto(Invoice invoice) {
        return new FinancialRecordDto(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                null // installments will be mapped when needed
        );
    }
}
