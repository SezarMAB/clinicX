package sy.sezar.clinicx.patient.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.patient.dto.LedgerEntryDto;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.LedgerEntry;
import sy.sezar.clinicx.patient.model.Patient;
import sy.sezar.clinicx.patient.model.Payment;
import sy.sezar.clinicx.patient.model.enums.LedgerEntryType;
import sy.sezar.clinicx.patient.repository.LedgerEntryRepository;
import sy.sezar.clinicx.patient.repository.PatientRepository;
import sy.sezar.clinicx.patient.service.LedgerService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public void record(UUID patientId, Invoice invoice, Payment payment, LedgerEntryType type, BigDecimal amount, String description) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        LedgerEntry entry = new LedgerEntry();
        entry.setPatient(patient);
        entry.setInvoice(invoice);
        entry.setPayment(payment);
        entry.setEntryType(type);
        entry.setAmount(amount);
        entry.setDescription(description);

        ledgerEntryRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LedgerEntryDto> getPatientLedger(UUID patientId, Pageable pageable) {
        return ledgerEntryRepository.findByPatientIdOrderByOccurredAtAsc(patientId, pageable)
                .map(e -> new LedgerEntryDto(
                        e.getId(),
                        e.getPatient().getId(),
                        e.getInvoice() != null ? e.getInvoice().getId() : null,
                        e.getPayment() != null ? e.getPayment().getId() : null,
                        e.getEntryType(),
                        e.getAmount(),
                        e.getOccurredAt(),
                        e.getDescription()
                ));
    }
}


