package sy.sezar.clinicx.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the complete dental chart payload stored as JSONB in PostgreSQL.
 * This structure matches the frontend's dental chart format.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartPayload {
    
    private Meta meta = new Meta();
    private Map<String, Tooth> teeth = new LinkedHashMap<>();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private String version = "1.0";
        private String lastUpdated;
        private String updatedBy;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tooth {
        @JsonProperty("tooth_id")
        private String toothId;
        
        private String condition = "healthy";
        private Map<String, Surface> surfaces = new LinkedHashMap<>();
        private String notes;
        private Flags flags = new Flags();
        
        @JsonProperty("last_treatment_date")
        private String lastTreatmentDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Surface {
        private String condition = "healthy";
        private String treatment;
        
        @JsonProperty("treatment_date")
        private String treatmentDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Flags {
        private boolean impacted = false;
        private boolean mobile = false;
        private boolean periapical = false;
        private boolean abscess = false;
    }
    
    /**
     * Initialize with all 32 teeth in healthy state
     */
    public static ChartPayload createDefault() {
        ChartPayload chart = new ChartPayload();
        
        // Initialize all 32 teeth (FDI notation)
        String[] toothIds = {
            "11", "12", "13", "14", "15", "16", "17", "18", // Upper right
            "21", "22", "23", "24", "25", "26", "27", "28", // Upper left
            "31", "32", "33", "34", "35", "36", "37", "38", // Lower left
            "41", "42", "43", "44", "45", "46", "47", "48"  // Lower right
        };
        
        String[] surfaces = {"mesial", "distal", "occlusal", "buccal", "lingual", "incisal", "cervical", "root"};
        
        for (String toothId : toothIds) {
            Tooth tooth = new Tooth();
            tooth.setToothId(toothId);
            
            // Initialize all surfaces
            for (String surfaceName : surfaces) {
                tooth.getSurfaces().put(surfaceName, new Surface());
            }
            
            chart.getTeeth().put(toothId, tooth);
        }
        
        return chart;
    }
}