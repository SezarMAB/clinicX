package sy.sezar.clinicx.patient.dto.visit;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Visit summary DTO - header information without procedure details.
 * Used for list views and summary displays.
 * Immutable record following Java best practices.
 */
public record VisitDto(
    UUID id,
    UUID patientId,
    String patientName,
    UUID appointmentId,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date,
    @JsonFormat(pattern = "HH:mm")
    LocalTime time,
    UUID providerId,
    String providerName,
    String notes,
    Integer procedureCount,
    BigDecimal totalCost,
    String overallStatus,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant updatedAt
) {
    /**
     * Builder pattern for complex object creation
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID patientId;
        private String patientName;
        private UUID appointmentId;
        private LocalDate date;
        private LocalTime time;
        private UUID providerId;
        private String providerName;
        private String notes;
        private Integer procedureCount;
        private BigDecimal totalCost;
        private String overallStatus;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder patientName(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public Builder appointmentId(UUID appointmentId) {
            this.appointmentId = appointmentId;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder time(LocalTime time) {
            this.time = time;
            return this;
        }

        public Builder providerId(UUID providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder procedureCount(Integer procedureCount) {
            this.procedureCount = procedureCount;
            return this;
        }

        public Builder totalCost(BigDecimal totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public Builder overallStatus(String overallStatus) {
            this.overallStatus = overallStatus;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public VisitDto build() {
            return new VisitDto(id, patientId, patientName, appointmentId, date, time,
                    providerId, providerName, notes, procedureCount, totalCost,
                    overallStatus, createdAt, updatedAt);
        }
    }
}