package sy.sezar.clinicx.patient.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sy.sezar.clinicx.patient.dto.PatientSearchCriteria;
import sy.sezar.clinicx.patient.model.Appointment;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Treatment;

import java.time.LocalDate;
import java.time.Period;
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

    /**
     * Creates a comprehensive specification based on advanced search criteria.
     *
     * @param criteria The search criteria
     * @return A Specification for Patients
     */
    public static Specification<Patient> byAdvancedCriteria(PatientSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // General search term
            if (StringUtils.hasText(criteria.searchTerm())) {
                String likePattern = "%" + criteria.searchTerm().toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("publicFacingId")), likePattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), likePattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("insuranceNumber")), likePattern));
                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }

            // Name filter
            if (StringUtils.hasText(criteria.name())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")), 
                    "%" + criteria.name().toLowerCase() + "%"
                ));
            }

            // Public facing ID filter
            if (StringUtils.hasText(criteria.publicFacingId())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("publicFacingId")), 
                    "%" + criteria.publicFacingId().toLowerCase() + "%"
                ));
            }

            // Phone number filter
            if (StringUtils.hasText(criteria.phoneNumber())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("phoneNumber")), 
                    "%" + criteria.phoneNumber().toLowerCase() + "%"
                ));
            }

            // Email filter
            if (StringUtils.hasText(criteria.email())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), 
                    "%" + criteria.email().toLowerCase() + "%"
                ));
            }

            // Gender filter
            if (StringUtils.hasText(criteria.gender())) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("gender")), 
                    criteria.gender().toLowerCase()
                ));
            }

            // Insurance provider filter
            if (StringUtils.hasText(criteria.insuranceProvider())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("insuranceProvider")), 
                    "%" + criteria.insuranceProvider().toLowerCase() + "%"
                ));
            }

            // Insurance number filter
            if (StringUtils.hasText(criteria.insuranceNumber())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("insuranceNumber")), 
                    "%" + criteria.insuranceNumber().toLowerCase() + "%"
                ));
            }

            // Date of birth range
            if (criteria.dateOfBirthFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfBirth"), criteria.dateOfBirthFrom()));
            }
            if (criteria.dateOfBirthTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), criteria.dateOfBirthTo()));
            }

            // Age range (calculated from date of birth)
            if (criteria.ageFrom() != null || criteria.ageTo() != null) {
                LocalDate today = LocalDate.now();
                if (criteria.ageFrom() != null) {
                    LocalDate maxBirthDate = today.minus(Period.ofYears(criteria.ageFrom()));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), maxBirthDate));
                }
                if (criteria.ageTo() != null) {
                    LocalDate minBirthDate = today.minus(Period.ofYears(criteria.ageTo() + 1));
                    predicates.add(criteriaBuilder.greaterThan(root.get("dateOfBirth"), minBirthDate));
                }
            }

            // Balance range
            if (criteria.balanceFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), criteria.balanceFrom()));
            }
            if (criteria.balanceTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("balance"), criteria.balanceTo()));
            }

            // Active status
            if (criteria.isActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), criteria.isActive()));
            }

            // Has medical notes
            if (criteria.hasMedicalNotes() != null) {
                if (criteria.hasMedicalNotes()) {
                    predicates.add(criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("importantMedicalNotes")),
                        criteriaBuilder.notEqual(criteriaBuilder.trim(root.get("importantMedicalNotes")), "")
                    ));
                } else {
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get("importantMedicalNotes")),
                        criteriaBuilder.equal(criteriaBuilder.trim(root.get("importantMedicalNotes")), "")
                    ));
                }
            }

            // Has outstanding balance
            if (criteria.hasOutstandingBalance() != null) {
                if (criteria.hasOutstandingBalance()) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("balance"), 0));
                } else {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("balance"), 0));
                }
            }

            // Creation date range
            if (criteria.createdFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    criteriaBuilder.function("DATE", LocalDate.class, root.get("createdAt")), 
                    criteria.createdFrom()
                ));
            }
            if (criteria.createdTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    criteriaBuilder.function("DATE", LocalDate.class, root.get("createdAt")), 
                    criteria.createdTo()
                ));
            }

            // Has appointments
            if (criteria.hasAppointments() != null) {
                Join<Patient, Appointment> appointmentJoin = root.join("appointments", JoinType.LEFT);
                if (criteria.hasAppointments()) {
                    predicates.add(criteriaBuilder.isNotNull(appointmentJoin.get("id")));
                    query.distinct(true);
                } else {
                    predicates.add(criteriaBuilder.isNull(appointmentJoin.get("id")));
                }
            }

            // Has treatments
            if (criteria.hasTreatments() != null) {
                Join<Patient, Treatment> treatmentJoin = root.join("treatments", JoinType.LEFT);
                if (criteria.hasTreatments()) {
                    predicates.add(criteriaBuilder.isNotNull(treatmentJoin.get("id")));
                    query.distinct(true);
                } else {
                    predicates.add(criteriaBuilder.isNull(treatmentJoin.get("id")));
                }
            }

            // Address filter
            if (StringUtils.hasText(criteria.address())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("address")), 
                    "%" + criteria.address().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

