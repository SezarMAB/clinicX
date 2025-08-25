package sy.sezar.clinicx.patient.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sy.sezar.clinicx.patient.dto.TreatmentMaterialSearchCriteria;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Treatment;
import sy.sezar.clinicx.patient.model.TreatmentMaterial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides reusable specifications for querying TreatmentMaterial entities.
 */
public final class TreatmentMaterialSpecifications {

    private TreatmentMaterialSpecifications() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a comprehensive specification based on advanced search criteria.
     *
     * @param criteria The search criteria
     * @return A Specification for TreatmentMaterials
     */
    public static Specification<TreatmentMaterial> byAdvancedCriteria(TreatmentMaterialSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Treatment ID filter
            if (criteria.treatmentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("treatment").get("id"), criteria.treatmentId()));
            }

            // Patient ID filter
            if (criteria.patientId() != null) {
                Join<TreatmentMaterial, Treatment> treatmentJoin = root.join("treatment", JoinType.LEFT);
                Join<Treatment, Patient> patientJoin = treatmentJoin.join("patient", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(patientJoin.get("id"), criteria.patientId()));
            }

            // Material name exact match
            if (StringUtils.hasText(criteria.materialName())) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("materialName")), 
                    criteria.materialName().toLowerCase()
                ));
            }

            // Material names filter
            if (criteria.materialNames() != null && !criteria.materialNames().isEmpty()) {
                List<String> lowerCaseNames = criteria.materialNames().stream()
                    .map(String::toLowerCase)
                    .toList();
                predicates.add(criteriaBuilder.lower(root.get("materialName")).in(lowerCaseNames));
            }

            // Material name contains
            if (StringUtils.hasText(criteria.materialNameContains())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("materialName")), 
                    "%" + criteria.materialNameContains().toLowerCase() + "%"
                ));
            }

            // Supplier filter
            if (StringUtils.hasText(criteria.supplier())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("supplier")), 
                    "%" + criteria.supplier().toLowerCase() + "%"
                ));
            }

            // Suppliers filter
            if (criteria.suppliers() != null && !criteria.suppliers().isEmpty()) {
                List<String> lowerCaseSuppliers = criteria.suppliers().stream()
                    .map(String::toLowerCase)
                    .toList();
                predicates.add(criteriaBuilder.lower(root.get("supplier")).in(lowerCaseSuppliers));
            }

            // Batch number filter
            if (StringUtils.hasText(criteria.batchNumber())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("batchNumber")), 
                    "%" + criteria.batchNumber().toLowerCase() + "%"
                ));
            }

            // Unit filter
            if (StringUtils.hasText(criteria.unit())) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("unit")), 
                    criteria.unit().toLowerCase()
                ));
            }

            // Quantity range
            if (criteria.quantityFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), criteria.quantityFrom()));
            }
            if (criteria.quantityTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("quantity"), criteria.quantityTo()));
            }

            // Cost per unit range
            if (criteria.costPerUnitFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("costPerUnit"), criteria.costPerUnitFrom()));
            }
            if (criteria.costPerUnitTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("costPerUnit"), criteria.costPerUnitTo()));
            }

            // Total cost range
            if (criteria.totalCostFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalCost"), criteria.totalCostFrom()));
            }
            if (criteria.totalCostTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalCost"), criteria.totalCostTo()));
            }

            // Notes search
            if (StringUtils.hasText(criteria.notesContain())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("notes")), 
                    "%" + criteria.notesContain().toLowerCase() + "%"
                ));
            }

            // Used date range (based on treatment date)
            if (criteria.usedFrom() != null || criteria.usedTo() != null) {
                Join<TreatmentMaterial, Treatment> treatmentJoin = root.join("treatment", JoinType.LEFT);
                if (criteria.usedFrom() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(treatmentJoin.get("treatmentDate"), criteria.usedFrom()));
                }
                if (criteria.usedTo() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(treatmentJoin.get("treatmentDate"), criteria.usedTo()));
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
     * Creates a specification to find materials by treatment ID.
     *
     * @param treatmentId The treatment ID
     * @return A Specification for TreatmentMaterials
     */
    public static Specification<TreatmentMaterial> byTreatmentId(java.util.UUID treatmentId) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("treatment").get("id"), treatmentId);
    }

    /**
     * Creates a specification to find materials by material name.
     *
     * @param materialName The material name
     * @return A Specification for TreatmentMaterials
     */
    public static Specification<TreatmentMaterial> byMaterialName(String materialName) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("materialName")), 
                "%" + materialName.toLowerCase() + "%"
            );
    }

    /**
     * Creates a specification to find materials by supplier.
     *
     * @param supplier The supplier name
     * @return A Specification for TreatmentMaterials
     */
    public static Specification<TreatmentMaterial> bySupplier(String supplier) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("supplier")), 
                "%" + supplier.toLowerCase() + "%"
            );
    }

    /**
     * Creates a specification to find materials within a cost range.
     *
     * @param minCost Minimum total cost
     * @param maxCost Maximum total cost
     * @return A Specification for TreatmentMaterials
     */
    public static Specification<TreatmentMaterial> byCostRange(java.math.BigDecimal minCost, java.math.BigDecimal maxCost) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minCost != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalCost"), minCost));
            }
            if (maxCost != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalCost"), maxCost));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}