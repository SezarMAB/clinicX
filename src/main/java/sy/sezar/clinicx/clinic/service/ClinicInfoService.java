package sy.sezar.clinicx.clinic.service;

import sy.sezar.clinicx.clinic.dto.ClinicInfoDto;
import sy.sezar.clinicx.clinic.dto.ClinicInfoUpdateRequest;

/**
 * Service interface for managing clinic information.
 */
public interface ClinicInfoService {
    
    /**
     * Gets the clinic information.
     * @return ClinicInfoDto containing clinic information
     */
    ClinicInfoDto getClinicInfo();
    
    /**
     * Updates the clinic information.
     * @param request Update request containing new clinic information
     * @return Updated ClinicInfoDto
     */
    ClinicInfoDto updateClinicInfo(ClinicInfoUpdateRequest request);
    
    /**
     * Initializes clinic info if it doesn't exist.
     */
    void initializeClinicInfoIfNotExists();
}
