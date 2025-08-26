package sy.sezar.clinicx.patient.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sy.sezar.clinicx.patient.dto.TreatmentSearchCriteria;
import sy.sezar.clinicx.patient.model.*;
import sy.sezar.clinicx.clinic.model.Staff;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides reusable specifications for querying Visit entities.
 */
public final class TreatmentSpecifications {

    private TreatmentSpecifications() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a comprehensive specification based on advanced search criteria.
     *
     * @param criteria The search criteria
     * @return A Specification for Treatments
     */
    public static Specification<Visit> byAdvancedCriteria(TreatmentSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Patient ID filter
            if (criteria.patientId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("patient").get("id"), criteria.patientId()));
            }

            // Doctor ID filter
            if (criteria.doctorId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("doctor").get("id"), criteria.doctorId()));
            }

            // Procedure ID filter
            if (criteria.procedureId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("procedure").get("id"), criteria.procedureId()));
            }

            // Status filter
            if (criteria.statuses() != null && !criteria.statuses().isEmpty()) {
                predicates.add(root.get("status").in(criteria.statuses()));
            }

            // Tooth number filter
            if (criteria.toothNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get("toothNumber"), criteria.toothNumber()));
            }

            // Tooth numbers filter
            if (criteria.toothNumbers() != null && !criteria.toothNumbers().isEmpty()) {
                predicates.add(root.get("toothNumber").in(criteria.toothNumbers()));
            }

            // Visit date range
            if (criteria.treatmentDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("treatmentDate"), criteria.treatmentDateFrom()));
            }
            if (criteria.treatmentDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("treatmentDate"), criteria.treatmentDateTo()));
            }

            // Cost range
            if (criteria.costFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), criteria.costFrom()));
            }
            if (criteria.costTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("cost"), criteria.costTo()));
            }

            // Notes search
            if (StringUtils.hasText(criteria.notesContain())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("treatmentNotes")),
                    "%" + criteria.notesContain().toLowerCase() + "%"
                ));
            }

            // Procedure name filter
            if (StringUtils.hasText(criteria.procedureName())) {
                Join<Visit, Procedure> procedureJoin = root.join("procedure", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(procedureJoin.get("name")),
                    "%" + criteria.procedureName().toLowerCase() + "%"
                ));
            }

            // Doctor name filter
            if (StringUtils.hasText(criteria.doctorName())) {
                Join<Visit, Staff> doctorJoin = root.join("doctor", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(doctorJoin.get("fullName")),
                    "%" + criteria.doctorName().toLowerCase() + "%"
                ));
            }

            // Patient name filter
            if (StringUtils.hasText(criteria.patientName())) {
                Join<Visit, Patient> patientJoin = root.join("patient", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(patientJoin.get("fullName")),
                    "%" + criteria.patientName().toLowerCase() + "%"
                ));
            }

            // Has materials filter
            if (criteria.hasMaterials() != null) {
                Join<Visit, TreatmentMaterial> materialJoin = root.join("materials", JoinType.LEFT);
                if (criteria.hasMaterials()) {
                    predicates.add(criteriaBuilder.isNotNull(materialJoin.get("id")));
                    query.distinct(true);
                } else {
                    predicates.add(criteriaBuilder.isNull(materialJoin.get("id")));
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

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification to find treatments by patient ID.
     *
     * @param patientId The patient ID
     * @return A Specification for Treatments
     */
    public static Specification<Visit> byPatientId(java.util.UUID patientId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("patient").get("id"), patientId);
    }

    /**
     * Creates a specification to find treatments by doctor ID.
     *
     * @param doctorId The doctor ID
     * @return A Specification for Treatments
     */
    public static Specification<Visit> byDoctorId(java.util.UUID doctorId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("doctor").get("id"), doctorId);
    }

    /**
     * Creates a specification to find treatments by tooth number.
     *
     * @param toothNumber The tooth number
     * @return A Specification for Treatments
     */
    public static Specification<Visit> byToothNumber(Integer toothNumber) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("toothNumber"), toothNumber);
    }

    /**
     * Creates a specification to find treatments within a date range.
     *
     * @param from Start date (inclusive)
     * @param to End date (inclusive)
     * @return A Specification for Treatments
     */
    public static Specification<Visit> byDateRange(LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("treatmentDate"), from));
            }
            if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("treatmentDate"), to));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
