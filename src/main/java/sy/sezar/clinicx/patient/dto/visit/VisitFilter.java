package sy.sezar.clinicx.patient.dto.visit;

import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Filter criteria for searching visits.
 * Used for advanced search functionality with multiple optional criteria.
 * Immutable record with validation.
 */
public record VisitFilter(
    UUID patientId,
    UUID providerId,
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateFrom,
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateTo,
    
    List<String> procedureCodes,
    List<String> procedureStatuses,
    String visitStatus,
    
    BigDecimal minCost,
    BigDecimal maxCost,
    
    Boolean hasLabWork,
    Boolean hasPendingProcedures,
    
    @Size(max = 100, message = "Search term cannot exceed 100 characters")
    String searchTerm
) {
    /**
     * Validate date range
     */
    public VisitFilter {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("Date from must be before or equal to date to");
        }
        
        if (minCost != null && maxCost != null && minCost.compareTo(maxCost) > 0) {
            throw new IllegalArgumentException("Min cost must be less than or equal to max cost");
        }
    }

    /**
     * Check if any filters are applied
     */
    public boolean hasFilters() {
        return patientId != null ||
               providerId != null ||
               dateFrom != null ||
               dateTo != null ||
               (procedureCodes != null && !procedureCodes.isEmpty()) ||
               (procedureStatuses != null && !procedureStatuses.isEmpty()) ||
               visitStatus != null ||
               minCost != null ||
               maxCost != null ||
               hasLabWork != null ||
               hasPendingProcedures != null ||
               (searchTerm != null && !searchTerm.isBlank());
    }

    /**
     * Check if date filters are applied
     */
    public boolean hasDateFilters() {
        return dateFrom != null || dateTo != null;
    }

    /**
     * Check if financial filters are applied
     */
    public boolean hasFinancialFilters() {
        return minCost != null || maxCost != null;
    }
}