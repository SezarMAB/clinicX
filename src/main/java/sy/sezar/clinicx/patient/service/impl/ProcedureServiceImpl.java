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
import sy.sezar.clinicx.patient.repository.ProcedureRepository;
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

    private final ProcedureRepository procedureRepository;
    private final ProcedureSummaryMapper procedureMapper;

    @Override
    public Page<ProcedureSummaryDto> getAllProcedures(Pageable pageable) {
        log.info("Getting all procedures with pagination: {}", pageable);

        Page<Procedure> procedures = procedureRepository.findAllByOrderByName(pageable);
        log.info("Found {} procedures (page {} of {})",
                procedures.getNumberOfElements(), procedures.getNumber() + 1, procedures.getTotalPages());

        return procedures.map(procedureMapper::toProcedureSummaryDto);
    }

    @Override
    public List<ProcedureSummaryDto> getAllActiveProcedures() {
        log.info("Getting all active procedures");

        List<Procedure> procedures = procedureRepository.findAllActive();
        log.info("Found {} active procedures", procedures.size());
        log.debug("Active procedures retrieved: {}",
                procedures.stream().map(Procedure::getName).toList());

        return procedureMapper.toProcedureSummaryDtoList(procedures);
    }

    @Override
    public ProcedureSummaryDto findProcedureById(UUID procedureId) {
        log.info("Finding procedure by ID: {}", procedureId);

        if (procedureId == null) {
            log.error("Procedure ID cannot be null");
            throw new IllegalArgumentException("Procedure ID cannot be null");
        }

        Procedure procedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> {
                    log.error("Procedure not found with ID: {}", procedureId);
                    return new NotFoundException("Procedure not found with ID: " + procedureId);
                });

        log.debug("Found procedure: {} (code: {}, cost: {})",
                procedure.getName(), procedure.getProcedureCode(), procedure.getDefaultCost());

        return procedureMapper.toProcedureSummaryDto(procedure);
    }

    @Override
    public List<ProcedureSummaryDto> searchProcedures(String searchTerm) {
        log.info("Searching procedures with term: '{}'", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            log.warn("Empty or null search term provided for procedure search");
            return List.of();
        }

        String trimmedSearchTerm = searchTerm.trim();
        log.debug("Executing procedure search with trimmed term: '{}'", trimmedSearchTerm);

        List<Procedure> procedures = procedureRepository.searchByNameOrCode(trimmedSearchTerm);
        log.info("Procedure search for '{}' returned {} results", trimmedSearchTerm, procedures.size());

        if (log.isDebugEnabled() && !procedures.isEmpty()) {
            log.debug("Search results: {}",
                    procedures.stream().map(p -> p.getName() + " (" + p.getProcedureCode() + ")").toList());
        }

        return procedureMapper.toProcedureSummaryDtoList(procedures);
    }
}
