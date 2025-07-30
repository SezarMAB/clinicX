package sy.sezar.clinicx.clinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.util.HashSet;
import java.util.Set;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;

@Entity
@Table(name = "staff")
@Getter
@Setter
public class Staff extends BaseEntity {

    @NotNull
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotNull
    @Size(max = 50)
    @Column(name = "role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private StaffRole role;

    @Email
    @NotNull
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 30)
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "staff_specialties",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private Set<Specialty> specialties = new HashSet<>();
}

