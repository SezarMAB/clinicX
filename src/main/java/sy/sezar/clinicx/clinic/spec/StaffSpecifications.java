package sy.sezar.clinicx.clinic.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import sy.sezar.clinicx.clinic.model.Specialty;
import sy.sezar.clinicx.clinic.dto.StaffSearchCriteria;
import sy.sezar.clinicx.clinic.model.Staff;

import java.util.ArrayList;
import java.util.List;

public class StaffSpecifications {

    public static Specification<Staff> withCriteria(StaffSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search term (name, email, phone)
            if (criteria.searchTerm() != null && !criteria.searchTerm().trim().isEmpty()) {
                String searchPattern = "%" + criteria.searchTerm().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), searchPattern),
                        cb.like(cb.lower(root.get("email")), searchPattern),
                        cb.like(cb.lower(root.get("phoneNumber")), searchPattern)
                ));
            }

            // Role filter
            if (criteria.role() != null) {
                predicates.add(cb.equal(root.get("role"), criteria.role()));
            }

            // Active status filter
            if (criteria.isActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), criteria.isActive()));
            }

            // Specialty filter
            if (criteria.specialtyIds() != null && !criteria.specialtyIds().isEmpty()) {
                Join<Staff, Specialty> specialtyJoin = root.join("specialties", JoinType.INNER);
                predicates.add(specialtyJoin.get("id").in(criteria.specialtyIds()));
                query.distinct(true); // Avoid duplicates when joining
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<Staff> byTenantId(String tenantId) {
        return (root, query, cb) -> {
            if (tenantId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("tenantId"), tenantId);
        };
    }
}
