package sy.sezar.clinicx.tenant.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.model.SpecialtyType;
import sy.sezar.clinicx.tenant.repository.SpecialtyTypeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes specialty types from configuration on application startup.
 * Only runs when realm-per-type is enabled.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.multi-tenant.realm-per-type", havingValue = "true")
public class SpecialtyTypeInitializer implements CommandLineRunner {
    
    private final SpecialtyTypeConfiguration specialtyConfig;
    private final SpecialtyTypeRepository specialtyTypeRepository;
    
    @Override
    public void run(String... args) {
        log.info("Initializing specialty types from configuration...");
        
        for (SpecialtyTypeConfiguration.SpecialtyTypeConfig config : specialtyConfig.getTypes()) {
            try {
                // Check if specialty already exists
                if (!specialtyTypeRepository.existsByCode(config.getCode())) {
                    SpecialtyType specialtyType = new SpecialtyType();
                    specialtyType.setCode(config.getCode());
                    specialtyType.setName(config.getName());
                    specialtyType.setRealmName(config.getRealmName());
                    specialtyType.setFeatures(config.getFeatures().toArray(new String[0]));
                    specialtyType.setActive(true);
                    
                    specialtyTypeRepository.save(specialtyType);
                    log.info("Created specialty type: {} - {}", config.getCode(), config.getName());
                } else {
                    // Update existing specialty if needed
                    specialtyTypeRepository.findByCode(config.getCode()).ifPresent(existing -> {
                        boolean updated = false;
                        
                        if (!existing.getName().equals(config.getName())) {
                            existing.setName(config.getName());
                            updated = true;
                        }
                        
                        if (!existing.getRealmName().equals(config.getRealmName())) {
                            existing.setRealmName(config.getRealmName());
                            updated = true;
                        }
                        
                        String[] configFeatures = config.getFeatures().toArray(new String[0]);
                        if (!java.util.Arrays.equals(existing.getFeatures(), configFeatures)) {
                            existing.setFeatures(configFeatures);
                            updated = true;
                        }
                        
                        if (updated) {
                            specialtyTypeRepository.save(existing);
                            log.info("Updated specialty type: {} - {}", config.getCode(), config.getName());
                        }
                    });
                }
            } catch (Exception e) {
                log.error("Failed to initialize specialty type: {}", config.getCode(), e);
            }
        }
        
        log.info("Specialty type initialization completed");
    }
}