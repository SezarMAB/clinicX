package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.view.UpcomingAppointmentsView;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for the {@link UpcomingAppointmentsView} entity.
 * Provides read-only access to the v_upcoming_appointments view.
 */
public interface UpcomingAppointmentsViewRepository extends JpaRepository<UpcomingAppointmentsView, UUID> {

    /**
     * Finds all upcoming appointments scheduled within a given date range.
     *
     * @param start The start of the date range.
     * @param end The end of the date range.
     * @return A list of {@link UpcomingAppointmentsView} objects within the specified range.
     */
    List<UpcomingAppointmentsView> findByAppointmentDatetimeBetween(OffsetDateTime start, OffsetDateTime end);

    /**
     * Finds all upcoming appointments for a specific doctor.
     *
     * @param doctorName The full name of the doctor.
     * @return A list of {@link UpcomingAppointmentsView} for the specified doctor.
     */
    List<UpcomingAppointmentsView> findByDoctorName(String doctorName);
}

