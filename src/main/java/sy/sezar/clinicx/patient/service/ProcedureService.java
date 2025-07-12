package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.ProcedureSummaryDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing dental procedures.
 */
public interface ProcedureService {

    /**
     * Gets all active procedures with pagination.
     */
    Page<ProcedureSummaryDto> getAllProcedures(Pageable pageable);

    /**
     * Gets all active procedures (for dropdowns).
     */
    List<ProcedureSummaryDto> getAllActiveProcedures();

    /**
     * Finds a procedure by ID.
     */
    ProcedureSummaryDto findProcedureById(UUID procedureId);

    /**
     * Searches procedures by name or code.
     */
    List<ProcedureSummaryDto> searchProcedures(String searchTerm);
}
