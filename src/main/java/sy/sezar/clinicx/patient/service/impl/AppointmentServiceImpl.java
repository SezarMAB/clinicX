package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.UpcomingAppointmentDto;
import sy.sezar.clinicx.patient.mapper.AppointmentMapper;
import sy.sezar.clinicx.patient.model.Appointment;
import sy.sezar.clinicx.patient.repository.AppointmentRepository;
import sy.sezar.clinicx.patient.service.AppointmentService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

    @Override
    public List<AppointmentCardDto> getAppointmentsByDateRange(Instant startDateTime, Instant endDateTime) {
        log.info("Getting appointments between {} and {}", startDateTime, endDateTime);

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
}
