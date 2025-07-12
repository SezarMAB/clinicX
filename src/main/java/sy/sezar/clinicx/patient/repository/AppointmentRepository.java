package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sy.sezar.clinicx.patient.model.Appointment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for managing Appointment entities.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Finds all appointments within a given datetime range, ordering them by datetime.
     * It fetches the associated Patient to avoid N+1 queries in the appointment list view.
     *
     * @param startDateTime The start datetime of the range.
     * @param endDateTime   The end datetime of the range.
     * @return A list of appointments with their patient data.
     */
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient WHERE a.appointmentDatetime BETWEEN :startDateTime AND :endDateTime ORDER BY a.appointmentDatetime")
    List<Appointment> findByAppointmentDatetimeBetweenOrderByAppointmentDatetimeAsc(
        Instant startDateTime,
        Instant endDateTime
    );

    /**
     * Finds all appointments for a specific patient.
     *
     * @param patientId The ID of the patient.
     * @return A list of appointments for the given patient.
     */
    List<Appointment> findByPatientId(UUID patientId);
}
