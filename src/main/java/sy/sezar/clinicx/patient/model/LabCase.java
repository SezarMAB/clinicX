package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.patient.model.enums.LabCaseStatus;

import java.time.LocalDate;

/**
 * Represents a lab case for external dental laboratory work.
 * Tracks items sent to labs for procedures like crowns, bridges, dentures.
 * Follows Single Responsibility Principle - manages lab case data only.
 */
@Entity
@Table(name = "lab_cases", indexes = {
    @Index(name = "idx_lab_cases_status", columnList = "status"),
    @Index(name = "idx_lab_cases_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"procedure"})
@EqualsAndHashCode(callSuper = true, exclude = {"procedure"})
public class LabCase extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", nullable = false, unique = true)
    private VisitProcedure procedure;

    @NotBlank
    @Size(max = 255)
    @Column(name = "lab_name", nullable = false)
    private String labName;

    @NotNull
    @Column(name = "sent_date", nullable = false)
    private LocalDate sentDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Size(max = 100)
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private LabCaseStatus status = LabCaseStatus.SENT;

    @Size(max = 100)
    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Size(max = 50)
    @Column(name = "shade", length = 50)
    private String shade;

    @Size(max = 100)
    @Column(name = "material_type", length = 100)
    private String materialType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Check if lab case is overdue
     */
    @Transient
    public boolean isOverdue() {
        if (receivedDate != null || dueDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Check if lab case has been received
     */
    @Transient
    public boolean isReceived() {
        return receivedDate != null || status.isComplete();
    }

    /**
     * Calculate days until due or overdue
     */
    @Transient
    public Long getDaysUntilDue() {
        if (isReceived() || dueDate == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Mark as received with date and status update
     */
    public void markAsReceived(LocalDate receivedDate) {
        this.receivedDate = receivedDate != null ? receivedDate : LocalDate.now();
        this.status = LabCaseStatus.RECEIVED;
    }

    /**
     * Update status with validation
     */
    public void updateStatus(LabCaseStatus newStatus) {
        if (this.status != null && !this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }

    /**
     * Validate dates
     */
    @PrePersist
    @PreUpdate
    protected void validateDates() {
        if (sentDate != null && dueDate != null && sentDate.isAfter(dueDate)) {
            throw new IllegalArgumentException("Sent date must be before or equal to due date");
        }
        if (sentDate != null && receivedDate != null && sentDate.isAfter(receivedDate)) {
            throw new IllegalArgumentException("Sent date must be before received date");
        }
    }
}