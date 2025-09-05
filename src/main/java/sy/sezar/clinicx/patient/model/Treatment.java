package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "treatments", indexes = {
        @Index(name = "ux_treatments_patient", columnList = "patient_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"patient", "visits"})
@EqualsAndHashCode(callSuper = true, exclude = {"patient", "visits"})
public class Treatment extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "treatment", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Visit> visits = new HashSet<>();
}

