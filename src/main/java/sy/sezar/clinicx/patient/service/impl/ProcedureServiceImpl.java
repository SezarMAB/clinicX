package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.patient.dto.ProcedureSummaryDto;
import sy.sezar.clinicx.patient.mapper.ProcedureSummaryMapper;
import sy.sezar.clinicx.patient.model.Procedure;
import sy.sezar.clinicx.patient.service.ProcedureService;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of ProcedureService with business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcedureServiceImpl implements ProcedureService {

    private final ProcedureSummaryMapper procedureMapper;
    // TODO: Inject ProcedureRepository when available

    @Override
    public Page<ProcedureSummaryDto> getAllProcedures(Pageable pageable) {
        log.debug("Getting all procedures with pagination");

        // TODO: Implement when ProcedureRepository is available
        throw new UnsupportedOperationException("Procedure repository not yet implemented");
    }

    @Override
    public List<ProcedureSummaryDto> getAllActiveProcedures() {
        log.debug("Getting all active procedures");

        // TODO: Implement when ProcedureRepository is available
        throw new UnsupportedOperationException("Procedure repository not yet implemented");
    }

    @Override
    public ProcedureSummaryDto findProcedureById(UUID procedureId) {
        log.debug("Finding procedure by ID: {}", procedureId);

        // TODO: Implement when ProcedureRepository is available
        throw new UnsupportedOperationException("Procedure repository not yet implemented");
    }

    @Override
    public List<ProcedureSummaryDto> searchProcedures(String searchTerm) {
        log.debug("Searching procedures with term: {}", searchTerm);

        // TODO: Implement when ProcedureRepository is available with search capability
        throw new UnsupportedOperationException("Procedure search not yet implemented");
    }
}
