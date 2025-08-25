package sy.sezar.clinicx.tenant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sy.sezar.clinicx.core.model.BaseEntity;

/**
 * Entity representing specialty types for realm-per-type architecture.
 * Each specialty defines the type of clinic and its available features.
 */
@Entity
@Table(name = "specialty_types")
@Getter
@Setter
public class SpecialtyType extends BaseEntity {

    @NotNull
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 255)
    @Column(name = "features", length = 255)
    private String features;

    @Size(max = 100)
    @Column(name = "realm_name", length = 100)
    private String realmName;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * Pre-defined specialty type constants
     */
    public static final String CLINIC = "CLINIC";
    public static final String DENTAL = "DENTAL";
    public static final String APPOINTMENTS = "APPOINTMENTS";

    /*
     * Get features as array by splitting the comma-separated string
     */
    public String[] getFeatures() {
        if (features == null || features.trim().isEmpty()) {
            return new String[0];
        }
        return features.split(",");
    }

    /**
     * Set features from array by joining with commas
     */
    public void setFeatures(String[] featuresArray) {
        if (featuresArray == null || featuresArray.length == 0) {
            this.features = null;
        } else {
            this.features = String.join(",", featuresArray);
        }
    }

}
