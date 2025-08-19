package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.AppointmentCardDto;
import sy.sezar.clinicx.patient.dto.AppointmentCreateRequest;
import sy.sezar.clinicx.patient.dto.UpcomingAppointmentDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing appointments.
 */
public interface AppointmentService {

    /**
     * Creates a new appointment.
     */
    AppointmentCardDto createAppointment(AppointmentCreateRequest request);

    /**
     * Gets appointments for a specific date range (for daily view in sidebar).
     */
    List<AppointmentCardDto> getAppointmentsByDateRange(Instant startDateTime, Instant endDateTime);

    /**
     * Gets appointments for a specific date based on the current user's role.
     * For DOCTOR role: returns only their appointments for the given date.
     * For NURSE/ASSISTANT/ADMIN roles: returns all appointments for the given date.
     *
     * @param date The date to get appointments for
     * @return List of appointments for the given date based on user's role
     */
    List<AppointmentCardDto> getAppointmentsForDate(LocalDate date);

    /**
     * Gets upcoming appointments for a patient.
     */
    List<UpcomingAppointmentDto> getUpcomingAppointmentsForPatient(UUID patientId);

    /**
     * Gets all appointments for a patient with pagination.
     */
    Page<AppointmentCardDto> getPatientAppointments(UUID patientId, Pageable pageable);

    /**
     * Finds appointment by ID.
     */
    AppointmentCardDto findAppointmentById(UUID appointmentId);

    /**
     * Gets today's appointments based on the current user's role.
     * For DOCTOR role: returns only their appointments.
     * For NURSE/ASSISTANT/ADMIN roles: returns all appointments for today.
     *
     * @return List of appointments for today based on user's role
     */
    List<AppointmentCardDto> getTodayAppointmentsForCurrentUser();
    /**
     * Gets today's appointments for the current user based on the provided date.
     *
     * @param today The date to get today's appointments for
     * @return List of today's appointments for the current user
     */
    List<AppointmentCardDto> getTodayAppointmentsForCurrentUser(LocalDate today);
}
