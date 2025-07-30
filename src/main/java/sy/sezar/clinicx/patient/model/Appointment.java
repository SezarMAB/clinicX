package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.clinic.model.Specialty;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.patient.model.enums.AppointmentStatus;
import sy.sezar.clinicx.clinic.model.Staff;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Staff doctor;

    @NotNull
    @Column(name = "appointment_datetime", nullable = false)
    private Instant appointmentDatetime;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 30;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;


    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Staff createdBy;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Treatment> treatments = new HashSet<>();
}

