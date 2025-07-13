package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.UpcomingAppointmentDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Operations related to appointment management")
public interface AppointmentControllerApi {

    @GetMapping("/date-range")
    @Operation(
        summary = "Get appointments by date range",
        description = "Retrieves appointments within a specific date/time range for daily view in sidebar."
    )
    @ApiResponse(responseCode = "200", description = "Appointments retrieved")
    ResponseEntity<List<AppointmentCardDto>> getAppointmentsByDateRange(
            @Parameter(name = "startDateTime", description = "Start date and time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDateTime,
            @Parameter(name = "endDateTime", description = "End date and time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDateTime);

    @GetMapping("/date/{date}")
    @Operation(
        summary = "Get appointments for specific date",
        description = "Retrieves all appointments for a specific date (today's appointments)."
    )
    @ApiResponse(responseCode = "200", description = "Appointments retrieved")
    ResponseEntity<List<AppointmentCardDto>> getAppointmentsForDate(
            @Parameter(name = "date", description = "Date in YYYY-MM-DD format", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @GetMapping("/patient/{patientId}/upcoming")
    @Operation(
        summary = "Get upcoming appointments for patient",
        description = "Retrieves upcoming appointments for a specific patient."
    )
    @ApiResponse(responseCode = "200", description = "Upcoming appointments retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<List<UpcomingAppointmentDto>> getUpcomingAppointmentsForPatient(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId);

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get all appointments for patient",
        description = "Retrieves paginated list of all appointments for a specific patient.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: appointmentDateTime", example = "appointmentDateTime")
        }
    )
    @ApiResponse(responseCode = "200", description = "Patient appointments retrieved")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    ResponseEntity<Page<AppointmentCardDto>> getPatientAppointments(
            @Parameter(name = "patientId", description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "appointmentDateTime", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable);

    @GetMapping("/{id}")
    @Operation(
        summary = "Get appointment by ID",
        description = "Retrieves a specific appointment by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Appointment found",
                content = @Content(schema = @Schema(implementation = AppointmentCardDto.class)))
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    ResponseEntity<AppointmentCardDto> getAppointmentById(
            @Parameter(name = "id", description = "Appointment UUID", required = true)
            @PathVariable UUID id);
}