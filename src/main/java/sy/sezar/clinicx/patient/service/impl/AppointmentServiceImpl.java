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
        log.debug("Getting appointments between {} and {}", startDateTime, endDateTime);

        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDatetimeBetweenOrderByAppointmentDatetimeAsc(startDateTime, endDateTime);

        return appointmentMapper.toAppointmentCardDtoList(appointments);
    }

    @Override
    public List<AppointmentCardDto> getAppointmentsForDate(LocalDate date) {
        Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return getAppointmentsByDateRange(startOfDay, endOfDay);
    }

    @Override
    public List<UpcomingAppointmentDto> getUpcomingAppointmentsForPatient(UUID patientId) {
        log.debug("Getting upcoming appointments for patient: {}", patientId);

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointmentMapper.toUpcomingAppointmentDtoList(appointments);
    }

    @Override
    public Page<AppointmentCardDto> getPatientAppointments(UUID patientId, Pageable pageable) {
        log.debug("Getting patient appointments with pagination for patient: {}", patientId);

        Page<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDatetimeDesc(patientId, pageable);
        return appointments.map(appointmentMapper::toAppointmentCardDto);
    }

    @Override
    public AppointmentCardDto findAppointmentById(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        return appointmentMapper.toAppointmentCardDto(appointment);
    }
}
