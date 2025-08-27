package sy.sezar.clinicx.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.LedgerEntry;

import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    Page<LedgerEntry> findByPatientIdOrderByOccurredAtAsc(UUID patientId, Pageable pageable);
}


