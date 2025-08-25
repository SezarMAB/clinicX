package sy.sezar.clinicx.clinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.clinic.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.clinic.dto.SpecialtyDto;
import sy.sezar.clinicx.clinic.dto.SpecialtyUpdateRequest;

import java.util.UUID;

/**
 * Service interface for managing specialties.
 */
public interface SpecialtyService {
    
    /**
     * Creates a new specialty.
     * @param request Create request containing specialty information
     * @return Created SpecialtyDto
     */
    SpecialtyDto createSpecialty(SpecialtyCreateRequest request);
    
    /**
     * Updates an existing specialty.
     * @param id Specialty ID
     * @param request Update request containing new specialty information
     * @return Updated SpecialtyDto
     */
    SpecialtyDto updateSpecialty(UUID id, SpecialtyUpdateRequest request);
    
    /**
     * Finds a specialty by ID.
     * @param id Specialty ID
     * @return SpecialtyDto
     */
    SpecialtyDto findSpecialtyById(UUID id);
    
    /**
     * Finds all specialties with pagination.
     * @param pageable Pagination information
     * @return Page of SpecialtyDto
     */
    Page<SpecialtyDto> findAllSpecialties(Pageable pageable);
    
    /**
     * Finds all active specialties with pagination.
     * @param pageable Pagination information
     * @return Page of SpecialtyDto
     */
    Page<SpecialtyDto> findAllActiveSpecialties(Pageable pageable);
    
    /**
     * Searches specialties by name or description.
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of SpecialtyDto
     */
    Page<SpecialtyDto> searchSpecialties(String searchTerm, Pageable pageable);
    
    /**
     * Deactivates a specialty (soft delete).
     * @param id Specialty ID
     */
    void deactivateSpecialty(UUID id);
}
