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
        log.debug("Getting all procedures with pagination");

        Page<Procedure> procedures = procedureRepository.findAllByOrderByName(pageable);
        return procedures.map(procedureMapper::toProcedureSummaryDto);
    }

    @Override
    public List<ProcedureSummaryDto> getAllActiveProcedures() {
        log.debug("Getting all active procedures");

        List<Procedure> procedures = procedureRepository.findAllActive();
        return procedureMapper.toProcedureSummaryDtoList(procedures);
    }

    @Override
    public ProcedureSummaryDto findProcedureById(UUID procedureId) {
        log.debug("Finding procedure by ID: {}", procedureId);

        Procedure procedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new NotFoundException("Procedure not found with ID: " + procedureId));

        return procedureMapper.toProcedureSummaryDto(procedure);
    }

    @Override
    public List<ProcedureSummaryDto> searchProcedures(String searchTerm) {
        log.debug("Searching procedures with term: {}", searchTerm);

        List<Procedure> procedures = procedureRepository.searchByNameOrCode(searchTerm);
        return procedureMapper.toProcedureSummaryDtoList(procedures);
    }
}
