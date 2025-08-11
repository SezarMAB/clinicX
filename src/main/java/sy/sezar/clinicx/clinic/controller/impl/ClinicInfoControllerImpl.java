package sy.sezar.clinicx.clinic.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.clinic.controller.api.ClinicInfoControllerApi;
import sy.sezar.clinicx.clinic.dto.ClinicInfoDto;
import sy.sezar.clinicx.clinic.dto.ClinicInfoUpdateRequest;
import sy.sezar.clinicx.clinic.service.ClinicInfoService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClinicInfoControllerImpl implements ClinicInfoControllerApi {
    
    private final ClinicInfoService clinicInfoService;
    
    @Override
    public ResponseEntity<ClinicInfoDto> getClinicInfo() {
        log.info("Retrieving clinic information");
        
        try {
            ClinicInfoDto clinicInfo = clinicInfoService.getClinicInfo();
            log.info("Successfully retrieved clinic information - Status: 200 OK");
            return ResponseEntity.ok(clinicInfo);
        } catch (Exception e) {
            log.error("Failed to retrieve clinic information - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public ResponseEntity<ClinicInfoDto> updateClinicInfo(ClinicInfoUpdateRequest request) {
        log.info("Updating clinic information");
        log.debug("Update request: name={}, email={}, phone={}", 
                request.name(), request.email(), request.phoneNumber());
        
        try {
            ClinicInfoDto clinicInfo = clinicInfoService.updateClinicInfo(request);
            log.info("Successfully updated clinic information - Status: 200 OK");
            return ResponseEntity.ok(clinicInfo);
        } catch (Exception e) {
            log.error("Failed to update clinic information - Error: {}", e.getMessage());
            throw e;
        }
    }
}
