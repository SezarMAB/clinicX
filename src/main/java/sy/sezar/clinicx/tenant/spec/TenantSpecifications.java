package sy.sezar.clinicx.tenant.spec;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import sy.sezar.clinicx.tenant.model.Tenant;

import java.util.ArrayList;
import java.util.List;

/**
 * Specifications for querying Tenant entities.
 */
public class TenantSpecifications {
    
    private TenantSpecifications() {
        // Utility class
    }
    
    /**
     * Creates a specification for searching tenants by multiple fields.
     *
     * @param searchTerm The search term to match against tenant fields.
     * @return A Specification for the search.
     */
    public static Specification<Tenant> searchByTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            
            List<Predicate> predicates = new ArrayList<>();
            
            // Search in name
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                searchPattern
            ));
            
            // Search in tenant ID
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("tenantId")), 
                searchPattern
            ));
            
            // Search in subdomain
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("subdomain")), 
                searchPattern
            ));
            
            // Search in contact email
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("contactEmail")), 
                searchPattern
            ));
            
            // Search in subscription plan
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("subscriptionPlan")), 
                searchPattern
            ));
            
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Creates a specification for filtering tenants by active status.
     *
     * @param isActive The active status to filter by.
     * @return A Specification for the filter.
     */
    public static Specification<Tenant> hasActiveStatus(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }
    
    /**
     * Creates a specification for filtering tenants by subscription plan.
     *
     * @param subscriptionPlan The subscription plan to filter by.
     * @return A Specification for the filter.
     */
    public static Specification<Tenant> hasSubscriptionPlan(String subscriptionPlan) {
        return (root, query, criteriaBuilder) -> {
            if (subscriptionPlan == null || subscriptionPlan.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("subscriptionPlan"), subscriptionPlan);
        };
    }
    
    /**
     * Combines multiple specifications for complex queries.
     *
     * @param searchTerm The search term.
     * @param isActive The active status filter.
     * @return A combined Specification.
     */
    public static Specification<Tenant> buildSearchSpecification(String searchTerm, Boolean isActive) {
        Specification<Tenant> spec = searchByTerm(searchTerm);
        if (isActive != null) {
            spec = spec.and(hasActiveStatus(isActive));
        }
        return spec;
    }
}