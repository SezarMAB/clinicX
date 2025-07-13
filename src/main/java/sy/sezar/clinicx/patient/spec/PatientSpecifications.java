package sy.sezar.clinicx.patient.spec;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sy.sezar.clinicx.patient.model.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides reusable specifications for querying Patient entities.
 */
public final class PatientSpecifications {

    private PatientSpecifications() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a specification to find patients by a general search term.
     * The term is matched against full name, public facing ID, and insurance number.
     *
     * @param searchTerm The term to search for. Can be null or empty.
     * @return A Specification for Patients, or null if the search term is blank.
     */
    public static Specification<Patient> bySearchTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return criteriaBuilder.conjunction(); // or null, depending on desired behavior for empty search
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("publicFacingId")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("insuranceNumber")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.function("TO_CHAR", String.class, root.get("dateOfBirth"), criteriaBuilder.literal("YYYY-MM-DD")), likePattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}

