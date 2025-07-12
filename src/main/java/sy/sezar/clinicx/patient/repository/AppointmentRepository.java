package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sy.sezar.clinicx.patient.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository for managing Appointment entities.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Finds all appointments within a given date range, ordering them by date and start time.
     * It fetches the associated Patient to avoid N+1 queries in the appointment list view.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of appointments with their patient data.
     */
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient WHERE a.appointmentDate BETWEEN :startDate AND :endDate ORDER BY a.appointmentDate, a.startTime")
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(LocalDate startDate, LocalDate endDate);

    /**
     * Finds all appointments for a specific patient.
     *
     * @param patientId The ID of the patient.
     * @return A list of appointments for the given patient.
     */
    List<Appointment> findByPatientId(UUID patientId);
}

