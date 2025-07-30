package sy.sezar.clinicx.tenant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.tenant.model.SpecialtyType;
import sy.sezar.clinicx.tenant.service.SpecialtyRegistry;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing specialty types (admin only).
 */
@RestController
@RequestMapping("/api/admin/specialties")
@Tag(name = "Specialty Management", description = "APIs for managing specialty types")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class SpecialtyAdminController {
    
    private final SpecialtyRegistry specialtyRegistry;
    
    @GetMapping
    @Operation(summary = "Get all specialties", 
               description = "Retrieve all active specialty types")
    public ResponseEntity<List<SpecialtyType>> getAllSpecialties() {
        log.debug("Getting all active specialties");
        List<SpecialtyType> specialties = specialtyRegistry.getActiveSpecialties();
        return ResponseEntity.ok(specialties);
    }
    
    @GetMapping("/{code}")
    @Operation(summary = "Get specialty by code", 
               description = "Retrieve a specific specialty type by its code")
    public ResponseEntity<SpecialtyType> getSpecialtyByCode(@PathVariable String code) {
        log.debug("Getting specialty by code: {}", code);
        SpecialtyType specialty = specialtyRegistry.getSpecialtyByCode(code);
        return ResponseEntity.ok(specialty);
    }
    
    @PostMapping
    @Operation(summary = "Register new specialty", 
               description = "Register a new specialty type for dynamic realm creation")
    public ResponseEntity<SpecialtyType> registerSpecialty(@Valid @RequestBody SpecialtyCreateRequest request) {
        log.info("Registering new specialty: {}", request.code());
        
        SpecialtyType specialty = specialtyRegistry.registerSpecialty(
            request.code(),
            request.name(),
            request.features(),
            request.realmName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(specialty);
    }
    
    @DeleteMapping("/{code}")
    @Operation(summary = "Deactivate specialty", 
               description = "Deactivate a specialty type (soft delete)")
    public ResponseEntity<Void> deactivateSpecialty(@PathVariable String code) {
        log.info("Deactivating specialty: {}", code);
        specialtyRegistry.deactivateSpecialty(code);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{code}/features")
    @Operation(summary = "Get specialty features", 
               description = "Get the list of features available for a specialty")
    public ResponseEntity<String[]> getSpecialtyFeatures(@PathVariable String code) {
        log.debug("Getting features for specialty: {}", code);
        String[] features = specialtyRegistry.getSpecialtyFeatures(code);
        return ResponseEntity.ok(features);
    }
}