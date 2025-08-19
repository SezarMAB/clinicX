package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.core.exception.NotValidValueException;
import sy.sezar.clinicx.core.security.SecurityUtils;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.AppointmentCreateRequest;
import sy.sezar.clinicx.patient.dto.UpcomingAppointmentDto;
import sy.sezar.clinicx.patient.mapper.AppointmentMapper;
import sy.sezar.clinicx.patient.model.Appointment;
import sy.sezar.clinicx.patient.repository.AppointmentRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.service.AppointmentService;
import sy.sezar.clinicx.clinic.repository.SpecialtyRepository;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.tenant.TenantContext;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of AppointmentService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;
    private final SpecialtyRepository specialtyRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional
    public AppointmentCardDto createAppointment(AppointmentCreateRequest request) {
        log.info("Creating new appointment for patient: {}", request.patientId());

        // Validate appointment date/time
        validateAppointmentDateTime(request.appointmentDatetime());

        // TODO: Add availability validation when doctor schedules are implemented
        // validateAppointmentAvailability(request);

        Appointment appointment = appointmentMapper.toEntity(request);

        appointment.setPatient(patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found with id: " + request.patientId())));

        appointment.setSpecialty(specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new NotFoundException("Specialty not found with id: " + request.specialtyId())));

        if (request.doctorId() != null) {
            appointment.setDoctor(staffRepository.findById(request.doctorId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found with id: " + request.doctorId())));
        }

        if (request.createdById() != null) {
            appointment.setCreatedBy(staffRepository.findById(request.createdById())
                    .orElseThrow(() -> new NotFoundException("Staff not found with id: " + request.createdById())));
        }

        if (request.status() == null) {
            appointment.setStatus(sy.sezar.clinicx.patient.model.enums.AppointmentStatus.SCHEDULED);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Created appointment with id: {}", savedAppointment.getId());

        return appointmentMapper.toAppointmentCardDto(savedAppointment);
    }

    @Override
    public List<AppointmentCardDto> getAppointmentsByDateRange(Instant startDateTime, Instant endDateTime) {
        log.info("Getting appointments between {} and {}", startDateTime, endDateTime);

        // Validate date range
        validateDateRange(startDateTime, endDateTime);

        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDatetimeBetweenOrderByAppointmentDatetimeAsc(startDateTime, endDateTime);

        log.info("Found {} appointments in date range", appointments.size());
        log.debug("Appointment date range query returned {} results", appointments.size());

        return appointmentMapper.toAppointmentCardDtoList(appointments);
    }

    @Override
    public List<AppointmentCardDto> getAppointmentsForDate(LocalDate date) {
        log.info("Getting appointments for specific date: {}", date);

        Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        log.debug("Date range converted to: {} - {}", startOfDay, endOfDay);

        List<AppointmentCardDto> appointments = getAppointmentsByDateRange(startOfDay, endOfDay);
        log.info("Retrieved {} appointments for date: {}", appointments.size(), date);

        return appointments;
    }

    @Override
    public List<UpcomingAppointmentDto> getUpcomingAppointmentsForPatient(UUID patientId) {
        log.info("Getting upcoming appointments for patient: {}", patientId);

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        log.info("Found {} upcoming appointments for patient: {}", appointments.size(), patientId);
        log.debug("Patient {} has appointments: {}", patientId,
                appointments.stream().map(Appointment::getId).toList());

        return appointmentMapper.toUpcomingAppointmentDtoList(appointments);
    }

    @Override
    public Page<AppointmentCardDto> getPatientAppointments(UUID patientId, Pageable pageable) {
        log.info("Getting patient appointments with pagination for patient: {} with pagination: {}", patientId, pageable);

        Page<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDatetimeDesc(patientId, pageable);
        log.info("Found {} appointments (page {} of {}) for patient: {}",
                appointments.getNumberOfElements(), appointments.getNumber() + 1,
                appointments.getTotalPages(), patientId);

        return appointments.map(appointmentMapper::toAppointmentCardDto);
    }

    @Override
    public AppointmentCardDto findAppointmentById(UUID appointmentId) {
        log.info("Finding appointment by ID: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.error("Appointment not found with ID: {}", appointmentId);
                    return new NotFoundException("Appointment not found with ID: " + appointmentId);
                });

        log.debug("Found appointment for patient: {} at: {}",
                appointment.getPatient().getId(), appointment.getAppointmentDatetime());

        return appointmentMapper.toAppointmentCardDto(appointment);
    }

    /**
     * Validates appointment date/time according to business rules.
     */
    private void validateAppointmentDateTime(Instant appointmentDateTime) {
        if (appointmentDateTime == null) {
            throw new NotValidValueException("appointmentDatetime", null,
                "ISO 8601 format (e.g., 2024-01-15T10:30:00Z)",
                "Appointment date/time is required");
        }

        Instant now = Instant.now();
        if (appointmentDateTime.isBefore(now)) {
            throw new NotValidValueException("appointmentDatetime",
                appointmentDateTime,
                "Future date/time in ISO 8601 format",
                "Appointment must be scheduled in the future");
        }

        // Check if appointment is on weekend (using system default timezone)
        ZonedDateTime appointmentZoned = appointmentDateTime.atZone(ZoneId.systemDefault());
        DayOfWeek dayOfWeek = appointmentZoned.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new BusinessRuleException("Appointments cannot be scheduled on weekends");
        }

        // Check if appointment is too far in the future (e.g., more than 6 months)
        Instant sixMonthsFromNow = now.plus(Duration.ofDays(180));
        if (appointmentDateTime.isAfter(sixMonthsFromNow)) {
            throw new BusinessRuleException("Appointments cannot be scheduled more than 6 months in advance");
        }
    }

    /**
     * Validates date range for queries.
     */
    private void validateDateRange(Instant startDateTime, Instant endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Date range parameters cannot be null");
        }

        if (startDateTime.isAfter(endDateTime)) {
            throw new NotValidValueException("startDateTime", startDateTime,
                "Must be before endDateTime",
                String.format("Invalid date range: start date %s is after end date %s",
                    startDateTime, endDateTime));
        }

        // Business rule: Can't query more than 1 year of data
        long daysBetween = Duration.between(startDateTime, endDateTime).toDays();
        if (daysBetween > 365) {
            throw new BusinessRuleException(
                String.format("Date range cannot exceed 365 days. Requested range is %d days", daysBetween));
        }
    }

    /**
     * Validates appointment availability (to be implemented when doctor schedules are available).
     */
    private void validateAppointmentAvailability(AppointmentCreateRequest request) {
        // TODO: Implement when doctor schedule functionality is available
        // - Check if doctor is available at the requested time
        // - Check for conflicts with existing appointments
        // - Check clinic operating hours
        log.debug("Appointment availability validation not yet implemented");
    }

    @Override
    public List<AppointmentCardDto> getTodayAppointmentsForCurrentUser() {
        log.info("Getting today's appointments for current user");
        
        // Get current user's Keycloak ID
        String keycloakUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("No authenticated user found"));
        
        // Get current tenant
        String currentTenantId = TenantContext.getCurrentTenant();
        if (currentTenantId == null) {
            throw new BusinessRuleException("No tenant context found");
        }
        
        // Find the staff member for this user in the current tenant
        Staff currentStaff = staffRepository.findByKeycloakUserIdAndTenantId(keycloakUserId, currentTenantId)
                .orElseThrow(() -> new AccessDeniedException("Current user is not a staff member in this tenant"));
        
        log.debug("Found staff member {} with roles: {}", currentStaff.getFullName(), currentStaff.getRoles());
        
        // Get today's date range
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        List<Appointment> appointments;
        
        // Check user's role and fetch appointments accordingly
        if (currentStaff.getRoles().contains(StaffRole.DOCTOR)) {
            // Doctor: Get only their appointments
            log.info("User is a DOCTOR, fetching only their appointments for today");
            appointments = appointmentRepository.findByDoctorIdAndAppointmentDatetimeBetween(
                    currentStaff.getId(), startOfDay, endOfDay);
        } else if (currentStaff.getRoles().contains(StaffRole.NURSE) || 
                   currentStaff.getRoles().contains(StaffRole.ASSISTANT) ||
                   currentStaff.getRoles().contains(StaffRole.ADMIN) ||
                   currentStaff.getRoles().contains(StaffRole.SUPER_ADMIN)) {
            // Nurse/Assistant/Admin: Get all appointments for today
            log.info("User is NURSE/ASSISTANT/ADMIN, fetching all appointments for today");
            appointments = appointmentRepository
                    .findByAppointmentDatetimeBetweenOrderByAppointmentDatetimeAsc(startOfDay, endOfDay);
        } else {
            // User doesn't have appropriate role
            throw new AccessDeniedException("User does not have permission to view appointments. Required roles: DOCTOR, NURSE, or ASSISTANT");
        }
        
        log.info("Found {} appointments for today", appointments.size());
        return appointmentMapper.toAppointmentCardDtoList(appointments);
    }
}
