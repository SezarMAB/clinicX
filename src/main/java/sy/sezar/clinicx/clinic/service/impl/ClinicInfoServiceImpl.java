package sy.sezar.clinicx.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.dto.ClinicInfoDto;
import sy.sezar.clinicx.clinic.dto.ClinicInfoUpdateRequest;
import sy.sezar.clinicx.clinic.mapper.ClinicInfoMapper;
import sy.sezar.clinicx.clinic.model.ClinicInfo;
import sy.sezar.clinicx.clinic.repository.ClinicInfoRepository;
import sy.sezar.clinicx.clinic.service.ClinicInfoService;
import sy.sezar.clinicx.core.exception.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClinicInfoServiceImpl implements ClinicInfoService {
    
    private final ClinicInfoRepository clinicInfoRepository;
    private final ClinicInfoMapper clinicInfoMapper;
    
    @Override
    @Transactional(readOnly = true)
    public ClinicInfoDto getClinicInfo() {
        log.info("Retrieving clinic information");
        
        ClinicInfo clinicInfo = clinicInfoRepository.findById(true)
                .orElseThrow(() -> {
                    log.error("Clinic information not found");
                    return new NotFoundException("Clinic information not found");
                });
        
        log.debug("Retrieved clinic info: {}", clinicInfo.getName());
        return clinicInfoMapper.toDto(clinicInfo);
    }
    
    @Override
    @Transactional
    public ClinicInfoDto updateClinicInfo(ClinicInfoUpdateRequest request) {
        log.info("Updating clinic information");
        log.debug("Update request: name={}, timezone={}", request.getName(), request.getTimezone());
        
        ClinicInfo clinicInfo = clinicInfoRepository.findById(true)
                .orElseThrow(() -> {
                    log.error("Clinic information not found for update");
                    return new NotFoundException("Clinic information not found");
                });
        
        clinicInfoMapper.updateFromRequest(request, clinicInfo);
        clinicInfo = clinicInfoRepository.save(clinicInfo);
        
        log.info("Successfully updated clinic information: {}", clinicInfo.getName());
        return clinicInfoMapper.toDto(clinicInfo);
    }
    
    @Override
    @Transactional
    public void initializeClinicInfoIfNotExists() {
        log.info("Checking if clinic info needs initialization");
        
        if (!clinicInfoRepository.existsById(true)) {
            log.info("Clinic info not found, initializing default clinic information");
            
            ClinicInfo clinicInfo = new ClinicInfo();
            clinicInfo.setId(true);
            clinicInfo.setName("Default Clinic");
            clinicInfo.setTimezone("UTC");
            clinicInfoRepository.save(clinicInfo);
            
            log.info("Successfully initialized default clinic information");
        } else {
            log.debug("Clinic info already exists, skipping initialization");
        }
    }
}
