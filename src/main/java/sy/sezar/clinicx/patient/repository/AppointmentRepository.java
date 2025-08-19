package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Finds all appointments for a specific patient with pagination.
     *
     * @param patientId The ID of the patient.
     * @param pageable  Pagination and sorting information.
     * @return A Page of appointments for the given patient.
     */
    Page<Appointment> findByPatientIdOrderByAppointmentDatetimeDesc(UUID patientId, Pageable pageable);

    /**
     * Finds all appointments for a specific doctor within a date range.
     *
     * @param doctorId      The ID of the doctor.
     * @param startDateTime The start datetime of the range.
     * @param endDateTime   The end datetime of the range.
     * @return A list of appointments for the given doctor in the date range.
     */
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient WHERE a.doctor.id = :doctorId AND a.appointmentDatetime BETWEEN :startDateTime AND :endDateTime ORDER BY a.appointmentDatetime")
    List<Appointment> findByDoctorIdAndAppointmentDatetimeBetween(
        @Param("doctorId") UUID doctorId,
        @Param("startDateTime") Instant startDateTime,
        @Param("endDateTime") Instant endDateTime
    );
}
