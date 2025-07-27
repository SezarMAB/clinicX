package sy.sezar.clinicx.clinic.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.clinic.controller.api.SpecialtyControllerApi;
import sy.sezar.clinicx.clinic.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.clinic.dto.SpecialtyDto;
import sy.sezar.clinicx.clinic.dto.SpecialtyUpdateRequest;
import sy.sezar.clinicx.clinic.service.SpecialtyService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialtyControllerImpl implements SpecialtyControllerApi {
    
    private final SpecialtyService specialtyService;
    
    @Override
    public ResponseEntity<SpecialtyDto> getSpecialtyById(UUID id) {
        log.info("Retrieving specialty with ID: {}", id);
        
        try {
            SpecialtyDto specialty = specialtyService.findSpecialtyById(id);
            log.info("Successfully retrieved specialty with ID: {} - Status: 200 OK", id);
            return ResponseEntity.ok(specialty);
        } catch (Exception e) {
            log.error("Failed to retrieve specialty with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public ResponseEntity<Page<SpecialtyDto>> getAllSpecialties(Pageable pageable) {
        log.info("Retrieving all specialties with pagination: {}", pageable);
        
        try {
            Page<SpecialtyDto> specialties = specialtyService.findAllSpecialties(pageable);
            log.info("Successfully retrieved {} specialties - Status: 200 OK", specialties.getNumberOfElements());
            return ResponseEntity.ok(specialties);
        } catch (Exception e) {
            log.error("Failed to retrieve specialties - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public ResponseEntity<Page<SpecialtyDto>> getActiveSpecialties(Pageable pageable) {
        log.info("Retrieving active specialties with pagination: {}", pageable);
        
        try {
            Page<SpecialtyDto> specialties = specialtyService.findAllActiveSpecialties(pageable);
            log.info("Successfully retrieved {} active specialties - Status: 200 OK", specialties.getNumberOfElements());
            return ResponseEntity.ok(specialties);
        } catch (Exception e) {
            log.error("Failed to retrieve active specialties - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public ResponseEntity<Page<SpecialtyDto>> searchSpecialties(String searchTerm, Pageable pageable) {
        log.info("Searching specialties with term: '{}' and pagination: {}", searchTerm, pageable);
        
        try {
            Page<SpecialtyDto> specialties = specialtyService.searchSpecialties(searchTerm, pageable);
            log.info("Successfully found {} specialties for search term '{}' - Status: 200 OK", 
                    specialties.getNumberOfElements(), searchTerm);
            return ResponseEntity.ok(specialties);
        } catch (Exception e) {
            log.error("Failed to search specialties with term: '{}' - Error: {}", searchTerm, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public ResponseEntity<SpecialtyDto> createSpecialty(SpecialtyCreateRequest request) {
        log.info("Creating new specialty with name: {}", request.getName());
        log.debug("Specialty creation request: {}", request);
        
        try {
            SpecialtyDto specialty = specialtyService.createSpecialty(request);
            log.info("Successfully created specialty: {} - Status: 201 CREATED", specialty.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(specialty);
        } catch (Exception e) {
            log.error("Failed to create specialty with name: {} - Error: {}", request.getName(), e.getMessage());
            throw e;
        }
    }
    
    @Override
    public ResponseEntity<SpecialtyDto> updateSpecialty(UUID id, SpecialtyUpdateRequest request) {
        log.info("Updating specialty with ID: {}", id);
        log.debug("Specialty update request: {}", request);
        
        try {
            SpecialtyDto specialty = specialtyService.updateSpecialty(id, request);
            log.info("Successfully updated specialty with ID: {} - Status: 200 OK", id);
            return ResponseEntity.ok(specialty);
        } catch (Exception e) {
            log.error("Failed to update specialty with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public ResponseEntity<Void> deleteSpecialty(UUID id) {
        log.info("Deactivating specialty with ID: {}", id);
        
        try {
            specialtyService.deactivateSpecialty(id);
            log.info("Successfully deactivated specialty with ID: {} - Status: 204 NO CONTENT", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to deactivate specialty with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }
}
