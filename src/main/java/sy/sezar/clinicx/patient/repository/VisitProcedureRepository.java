package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.VisitProcedure;

import java.util.UUID;

/**
 * Repository for accessing VisitProcedure records.
 */
public interface VisitProcedureRepository extends JpaRepository<VisitProcedure, UUID> {
}

