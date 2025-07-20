package sy.sezar.clinicx.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sy.sezar.clinicx.clinic.model.ClinicInfo;

@Repository
public interface ClinicInfoRepository extends JpaRepository<ClinicInfo, Boolean> {
}
