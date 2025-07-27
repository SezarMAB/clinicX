package sy.sezar.clinicx.patient.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a read-only view of upcoming appointments (v_upcoming_appointments).
 * This entity provides a simplified list of future appointments, including patient
 * and doctor details, for easy display and scheduling management.
 */
@Entity
@Table(name = "v_upcoming_appointments")
@Immutable
@Getter
public class UpcomingAppointmentsView {

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    private UUID id;

    @Column(name = "appointment_datetime", insertable = false, updatable = false)
    private OffsetDateTime appointmentDatetime;

    @Column(name = "duration_minutes", insertable = false, updatable = false)
    private Integer durationMinutes;

    @Column(name = "status", insertable = false, updatable = false)
    private String status;

    @Column(name = "patient_name", insertable = false, updatable = false)
    private String patientName;

    @Column(name = "patient_id", insertable = false, updatable = false)
    private String publicFacingPatientId;

    @Column(name = "patient_phone", insertable = false, updatable = false)
    private String patientPhone;

    @Column(name = "doctor_name", insertable = false, updatable = false)
    private String doctorName;

    @Column(name = "specialty", insertable = false, updatable = false)
    private String specialty;
}

