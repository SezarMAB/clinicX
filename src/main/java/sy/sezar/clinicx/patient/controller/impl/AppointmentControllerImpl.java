package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.AppointmentControllerApi;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.UpcomingAppointmentDto;
import sy.sezar.clinicx.patient.service.AppointmentService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class AppointmentControllerImpl implements AppointmentControllerApi {

    private final AppointmentService appointmentService;

    @Override
    public ResponseEntity<List<AppointmentCardDto>> getAppointmentsByDateRange(Instant startDateTime, Instant endDateTime) {
        log.info("Retrieving appointments between {} and {}", startDateTime, endDateTime);
        List<AppointmentCardDto> appointments = appointmentService.getAppointmentsByDateRange(startDateTime, endDateTime);
        return ResponseEntity.ok(appointments);
    }

    @Override
    public ResponseEntity<List<AppointmentCardDto>> getAppointmentsForDate(LocalDate date) {
        log.info("Retrieving appointments for date: {}", date);
        List<AppointmentCardDto> appointments = appointmentService.getAppointmentsForDate(date);
        return ResponseEntity.ok(appointments);
    }

    @Override
    public ResponseEntity<List<UpcomingAppointmentDto>> getUpcomingAppointmentsForPatient(UUID patientId) {
        log.info("Retrieving upcoming appointments for patient ID: {}", patientId);
        List<UpcomingAppointmentDto> appointments = appointmentService.getUpcomingAppointmentsForPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @Override
    public ResponseEntity<Page<AppointmentCardDto>> getPatientAppointments(UUID patientId, Pageable pageable) {
        log.info("Retrieving appointments for patient ID: {} with pagination: {}", patientId, pageable);
        Page<AppointmentCardDto> appointments = appointmentService.getPatientAppointments(patientId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @Override
    public ResponseEntity<AppointmentCardDto> getAppointmentById(UUID id) {
        log.info("Retrieving appointment with ID: {}", id);
        AppointmentCardDto appointment = appointmentService.findAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }
}