package sy.sezar.clinicx.tenant.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.controller.api.SpecialtyAdminControllerApi;
import sy.sezar.clinicx.tenant.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.tenant.model.SpecialtyType;
import sy.sezar.clinicx.tenant.service.SpecialtyRegistry;

import java.util.List;

/**
 * Implementation for managing specialty types.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialtyAdminControllerImpl implements SpecialtyAdminControllerApi {
    
    private final SpecialtyRegistry specialtyRegistry;
    
    @Override
    public ResponseEntity<List<SpecialtyType>> getAllSpecialties() {
        log.debug("Getting all active specialties");
        List<SpecialtyType> specialties = specialtyRegistry.getActiveSpecialties();
        return ResponseEntity.ok(specialties);
    }
    
    @Override
    public ResponseEntity<SpecialtyType> getSpecialtyByCode(String code) {
        log.debug("Getting specialty by code: {}", code);
        SpecialtyType specialty = specialtyRegistry.getSpecialtyByCode(code);
        return ResponseEntity.ok(specialty);
    }
    
    @Override
    public ResponseEntity<SpecialtyType> registerSpecialty(SpecialtyCreateRequest request) {
        log.info("Registering new specialty: {}", request.code());
        
        SpecialtyType specialty = specialtyRegistry.registerSpecialty(
            request.code(),
            request.name(),
            request.features(),
            request.realmName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(specialty);
    }
    
    @Override
    public ResponseEntity<Void> deactivateSpecialty(String code) {
        log.info("Deactivating specialty: {}", code);
        specialtyRegistry.deactivateSpecialty(code);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<String[]> getSpecialtyFeatures(String code) {
        log.debug("Getting features for specialty: {}", code);
        String[] features = specialtyRegistry.getSpecialtyFeatures(code);
        return ResponseEntity.ok(features);
    }
}