package sy.sezar.clinicx.patient.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import sy.sezar.clinicx.core.model.BaseEntity;
import sy.sezar.clinicx.patient.dto.ChartPayload;

/**
 * DentalChart entity storing the complete dental chart as JSONB in PostgreSQL.
 * Replaces the normalized PatientTooth entity with a document-based approach.
 */
@Entity
@Table(name = "dental_charts", indexes = {
    @Index(name = "idx_dental_charts_patient_id", columnList = "patient_id")
})
@Getter
@Setter
public class DentalChart extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;
    
    @Type(JsonType.class)
    @Column(name = "chart_data", columnDefinition = "jsonb", nullable = false)
    private ChartPayload chartData;
    
    @PrePersist
    public void prePersist() {
        if (chartData == null) {
            chartData = ChartPayload.createDefault();
        }
        if (chartData.getMeta() != null) {
            chartData.getMeta().setLastUpdated(java.time.Instant.now().toString());
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        if (chartData != null && chartData.getMeta() != null) {
            chartData.getMeta().setLastUpdated(java.time.Instant.now().toString());
        }
    }
}