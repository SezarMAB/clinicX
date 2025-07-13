package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import sy.sezar.clinicx.patient.controller.api.ProcedureControllerApi;
import sy.sezar.clinicx.patient.dto.ProcedureSummaryDto;
import sy.sezar.clinicx.patient.service.ProcedureService;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProcedureControllerImpl implements ProcedureControllerApi {

    private final ProcedureService procedureService;

    @Override
    public ResponseEntity<Page<ProcedureSummaryDto>> getAllProcedures(Pageable pageable) {
        log.info("Retrieving all procedures with pagination: {}", pageable);
        Page<ProcedureSummaryDto> procedures = procedureService.getAllProcedures(pageable);
        return ResponseEntity.ok(procedures);
    }

    @Override
    public ResponseEntity<List<ProcedureSummaryDto>> getActiveProcedures() {
        log.info("Retrieving all active procedures");
        List<ProcedureSummaryDto> procedures = procedureService.getAllActiveProcedures();
        return ResponseEntity.ok(procedures);
    }

    @Override
    public ResponseEntity<ProcedureSummaryDto> getProcedureById(UUID id) {
        log.info("Retrieving procedure with ID: {}", id);
        ProcedureSummaryDto procedure = procedureService.findProcedureById(id);
        return ResponseEntity.ok(procedure);
    }

    @Override
    public ResponseEntity<List<ProcedureSummaryDto>> searchProcedures(String searchTerm) {
        log.info("Searching procedures with term: {}", searchTerm);
        List<ProcedureSummaryDto> procedures = procedureService.searchProcedures(searchTerm);
        return ResponseEntity.ok(procedures);
    }
}