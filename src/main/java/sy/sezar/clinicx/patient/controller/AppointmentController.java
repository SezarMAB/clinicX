package sy.sezar.clinicx.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.UpcomingAppointmentDto;
import sy.sezar.clinicx.patient.service.AppointmentService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Appointments", description = "Operations related to appointment management")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/date-range")
    @Operation(
        summary = "Get appointments by date range",
        description = "Retrieves appointments within a specific date/time range for daily view in sidebar."
    )
    @ApiResponse(responseCode = "200", description = "Appointments retrieved")
    public ResponseEntity<List<AppointmentCardDto>> getAppointmentsByDateRange(
            @Parameter(name = "startDateTime", description = "Start date and time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDateTime,
            @Parameter(name = "endDateTime", description = "End date and time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDateTime) {
        log.info("Retrieving appointments between {} and {}", startDateTime, endDateTime);
        List<AppointmentCardDto> appointments = appointmentService.getAppointmentsByDateRange(startDateTime, endDateTime);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date/{date}")
    @Operation(
        summary = "Get appointments for specific date",
        description = "Retrieves all appointments for a specific date (today's appointments)."
    )
    @ApiResponse(responseCode = "200", description = "Appointments retrieved")
    public ResponseEntity<List<AppointmentCardDto>> getAppointmentsForDate(
            @Parameter(name = "date", description = "Date in YYYY-MM-DD format", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Retrieving appointments for date: {}", date);
        List<AppointmentCardDto> appointments = appointmentService.getAppointmentsForDate(date);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/patient/{patientId}/upcoming")
    @Operation(
        summary = "Get upcoming appointments for patient",
        description = "Retrieves upcoming appointments for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Upcoming appointments retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<List<UpcomingAppointmentDto>> getUpcomingAppointmentsForPatient(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId) {
        log.info("Retrieving upcoming appointments for patient ID: {}", patientId);
        List<UpcomingAppointmentDto> appointments = appointmentService.getUpcomingAppointmentsForPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get all appointments for patient",
        description = "Retrieves paginated list of all appointments for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Patient appointments retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<Page<AppointmentCardDto>> getPatientAppointments(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            Pageable pageable) {
        log.info("Retrieving appointments for patient ID: {} with pagination: {}", patientId, pageable);
        Page<AppointmentCardDto> appointments = appointmentService.getPatientAppointments(patientId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get appointment by ID",
        description = "Retrieves a specific appointment by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Appointment found",
                content = @Content(schema = @Schema(implementation = AppointmentCardDto.class)))
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    public ResponseEntity<AppointmentCardDto> getAppointmentById(
            @Parameter(name = "id", description = "Appointment UUID", required = true)
            @PathVariable UUID id) {
        log.info("Retrieving appointment with ID: {}", id);
        AppointmentCardDto appointment = appointmentService.findAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }
}
