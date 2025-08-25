package sy.sezar.clinicx.patient.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.patient.controller.api.LedgerControllerApi;
import sy.sezar.clinicx.patient.dto.LedgerEntryDto;
import sy.sezar.clinicx.patient.service.LedgerService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LedgerControllerImpl implements LedgerControllerApi {

    private final LedgerService ledgerService;

    @Override
    public ResponseEntity<Page<LedgerEntryDto>> getPatientLedger(UUID patientId, Pageable pageable) {
        log.debug("Getting ledger for patient {}", patientId);
        return ResponseEntity.ok(ledgerService.getPatientLedger(patientId, pageable));
    }
}


