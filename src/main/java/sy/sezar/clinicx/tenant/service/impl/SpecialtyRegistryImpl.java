package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.tenant.config.SpecialtyRealmMappingConfig;
import sy.sezar.clinicx.tenant.model.SpecialtyType;
import sy.sezar.clinicx.tenant.repository.SpecialtyTypeRepository;
import sy.sezar.clinicx.tenant.service.SpecialtyRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of specialty registry service.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SpecialtyRegistryImpl implements SpecialtyRegistry {
    
    private final SpecialtyTypeRepository specialtyTypeRepository;
    
    @Autowired
    private SpecialtyRealmMappingConfig mappingConfig;
    
    @Override
    @CacheEvict(value = "specialties", allEntries = true)
    public SpecialtyType registerSpecialty(String code, String name, String[] features, String realmName) {
        log.info("Registering new specialty: {} - {}", code, name);
        
        // Check if specialty already exists
        if (specialtyTypeRepository.existsByCode(code)) {
            throw new BusinessRuleException("Specialty already exists: " + code);
        }
        
        SpecialtyType specialtyType = new SpecialtyType();
        specialtyType.setCode(code.toUpperCase());
        specialtyType.setName(name);
        
        // Merge provided features with default features
        List<String> allFeatures = new ArrayList<>(Arrays.asList(features));
        if (mappingConfig != null && mappingConfig.getDefaultFeatures() != null) {
            for (String defaultFeature : mappingConfig.getDefaultFeatures()) {
                if (!allFeatures.contains(defaultFeature)) {
                    allFeatures.add(defaultFeature);
                }
            }
        }
        specialtyType.setFeatures(allFeatures.toArray(new String[0]));
        
        specialtyType.setRealmName(realmName != null ? realmName : code.toLowerCase() + "-realm");
        specialtyType.setActive(true);
        
        log.info("Registering specialty {} with features: {}", code, allFeatures);
        
        return specialtyTypeRepository.save(specialtyType);
    }
    
    @Override
    @Cacheable(value = "specialties")
    public List<SpecialtyType> getActiveSpecialties() {
        log.debug("Fetching all active specialties");
        return specialtyTypeRepository.findAll().stream()
            .filter(SpecialtyType::isActive)
            .toList();
    }
    
    @Override
    @Cacheable(value = "specialty", key = "#code")
    public SpecialtyType getSpecialtyByCode(String code) {
        log.debug("Fetching specialty by code: {}", code);
        return specialtyTypeRepository.findByCode(code.toUpperCase())
            .orElseThrow(() -> new NotFoundException("Specialty not found: " + code));
    }
    
    @Override
    public boolean specialtyExists(String code) {
        return specialtyTypeRepository.existsByCode(code.toUpperCase());
    }
    
    @Override
    @CacheEvict(value = {"specialties", "specialty"}, allEntries = true)
    public void deactivateSpecialty(String code) {
        log.info("Deactivating specialty: {}", code);
        
        SpecialtyType specialtyType = getSpecialtyByCode(code);
        specialtyType.setActive(false);
        specialtyTypeRepository.save(specialtyType);
    }
    
    @Override
    public String[] getSpecialtyFeatures(String code) {
        SpecialtyType specialtyType = getSpecialtyByCode(code);
        return specialtyType.getFeatures() != null ? specialtyType.getFeatures() : new String[0];
    }
}