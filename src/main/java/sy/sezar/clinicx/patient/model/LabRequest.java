package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;

import java.time.LocalDate;
import sy.sezar.clinicx.patient.model.enums.LabRequestStatus;

@Entity
@Table(name = "lab_requests")
@Getter
@Setter
public class LabRequest extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Size(max = 50)
    @Column(name = "order_number", unique = true, length = 50)
    private String orderNumber;

    @NotNull
    @Column(name = "item_description", nullable = false)
    private String itemDescription;

    @Column(name = "tooth_number")
    private Integer toothNumber;

    @Column(name = "date_sent")
    private LocalDate dateSent;

    @Column(name = "date_due")
    private LocalDate dateDue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private LabRequestStatus status = LabRequestStatus.PENDING;

    @Size(max = 255)
    @Column(name = "lab_name", length = 255)
    private String labName;
}

