package sy.sezar.clinicx.tenant.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.tenant.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.tenant.model.SpecialtyType;

import java.util.List;

/**
 * REST controller API for managing specialty types (admin only).
 */
@RestController
@RequestMapping("/api/admin/specialties")
@Tag(name = "Specialty Management", description = "APIs for managing specialty types")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
public interface SpecialtyAdminControllerApi {
    
    @GetMapping
    @Operation(summary = "Get all specialties", 
               description = "Retrieve all active specialty types")
    ResponseEntity<List<SpecialtyType>> getAllSpecialties();
    
    @GetMapping("/{code}")
    @Operation(summary = "Get specialty by code", 
               description = "Retrieve a specific specialty type by its code")
    ResponseEntity<SpecialtyType> getSpecialtyByCode(@PathVariable String code);
    
    @PostMapping
    @Operation(summary = "Register new specialty", 
               description = "Register a new specialty type for dynamic realm creation")
    ResponseEntity<SpecialtyType> registerSpecialty(@Valid @RequestBody SpecialtyCreateRequest request);
    
    @DeleteMapping("/{code}")
    @Operation(summary = "Deactivate specialty", 
               description = "Deactivate a specialty type (soft delete)")
    ResponseEntity<Void> deactivateSpecialty(@PathVariable String code);
    
    @GetMapping("/{code}/features")
    @Operation(summary = "Get specialty features", 
               description = "Get the list of features available for a specialty")
    ResponseEntity<String[]> getSpecialtyFeatures(@PathVariable String code);
}