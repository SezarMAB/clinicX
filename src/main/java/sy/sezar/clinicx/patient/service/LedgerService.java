package sy.sezar.clinicx.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.patient.dto.LedgerEntryDto;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.model.enums.LedgerEntryType;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerService {
    void record(UUID patientId, Invoice invoice, Payment payment, LedgerEntryType type, BigDecimal amount, String description);
    Page<LedgerEntryDto> getPatientLedger(UUID patientId, Pageable pageable);
}


