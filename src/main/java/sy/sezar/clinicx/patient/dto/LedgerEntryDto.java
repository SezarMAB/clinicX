package sy.sezar.clinicx.patient.dto;

import sy.sezar.clinicx.patient.model.enums.LedgerEntryType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntryDto(
        UUID id,
        UUID patientId,
        UUID invoiceId,
        UUID paymentId,
        LedgerEntryType entryType,
        BigDecimal amount,
        Instant occurredAt,
        String description
) {}


