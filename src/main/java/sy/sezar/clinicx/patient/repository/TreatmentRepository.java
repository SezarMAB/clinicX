package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sy.sezar.clinicx.patient.model.Treatment;

import java.util.Optional;
import java.util.UUID;

public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {
    Optional<Treatment> findByPatientId(UUID patientId);
}

