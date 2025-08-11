package sy.sezar.clinicx.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.clinic.dto.SpecialtyDto;
import sy.sezar.clinicx.clinic.dto.SpecialtyUpdateRequest;
import sy.sezar.clinicx.clinic.mapper.SpecialtyMapper;
import sy.sezar.clinicx.clinic.model.Specialty;
import sy.sezar.clinicx.clinic.repository.SpecialtyRepository;
import sy.sezar.clinicx.clinic.service.SpecialtyService;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {
    
    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;
    
    @Override
    @Transactional
    public SpecialtyDto createSpecialty(SpecialtyCreateRequest request) {
        log.info("Creating new specialty with name: {}", request.name());
        
        // Check if specialty name already exists
        if (specialtyRepository.existsByNameIgnoreCase(request.name())) {
            log.error("Specialty with name '{}' already exists", request.name());
            throw new BusinessRuleException("Specialty with name '" + request.name() + "' already exists");
        }
        
        Specialty specialty = specialtyMapper.toEntity(request);
        specialty = specialtyRepository.save(specialty);
        
        log.info("Successfully created specialty with ID: {} and name: {}", 
                specialty.getId(), specialty.getName());
        return specialtyMapper.toDto(specialty);
    }
    
    @Override
    @Transactional
    public SpecialtyDto updateSpecialty(UUID id, SpecialtyUpdateRequest request) {
        log.info("Updating specialty with ID: {}", id);
        log.debug("Update request: name={}, active={}", request.name(), request.isActive());
        
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Specialty not found with ID: {}", id);
                    return new NotFoundException("Specialty not found with id: " + id);
                });
        
        // Check if another specialty with the same name exists
        specialtyRepository.findByNameIgnoreCase(request.name())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        log.error("Another specialty with name '{}' already exists", request.name());
                        throw new BusinessRuleException("Specialty with name '" + request.name() + "' already exists");
                    }
                });
        
        specialtyMapper.updateFromRequest(request, specialty);
        specialty = specialtyRepository.save(specialty);
        
        log.info("Successfully updated specialty with ID: {}", id);
        return specialtyMapper.toDto(specialty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SpecialtyDto findSpecialtyById(UUID id) {
        log.info("Finding specialty by ID: {}", id);
        
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Specialty not found with ID: {}", id);
                    return new NotFoundException("Specialty not found with id: " + id);
                });
        
        log.debug("Found specialty: {}", specialty.getName());
        return specialtyMapper.toDto(specialty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyDto> findAllSpecialties(Pageable pageable) {
        log.info("Finding all specialties with pagination: {}", pageable);
        
        Page<Specialty> specialties = specialtyRepository.findAll(pageable);
        log.info("Found {} specialties (page {} of {})",
                specialties.getNumberOfElements(), 
                specialties.getNumber() + 1, 
                specialties.getTotalPages());
        
        return specialties.map(specialtyMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyDto> findAllActiveSpecialties(Pageable pageable) {
        log.info("Finding all active specialties with pagination: {}", pageable);
        
        Page<Specialty> specialties = specialtyRepository.findAllActive(pageable);
        log.info("Found {} active specialties (page {} of {})",
                specialties.getNumberOfElements(), 
                specialties.getNumber() + 1, 
                specialties.getTotalPages());
        
        return specialties.map(specialtyMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyDto> searchSpecialties(String searchTerm, Pageable pageable) {
        log.info("Searching specialties with term: '{}' and pagination: {}", searchTerm, pageable);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            log.debug("Empty search term, returning all specialties");
            return findAllSpecialties(pageable);
        }
        
        Page<Specialty> specialties = specialtyRepository.searchSpecialties(searchTerm.trim(), pageable);
        log.info("Search found {} specialties (page {} of {})",
                specialties.getNumberOfElements(), 
                specialties.getNumber() + 1, 
                specialties.getTotalPages());
        
        return specialties.map(specialtyMapper::toDto);
    }
    
    @Override
    @Transactional
    public void deactivateSpecialty(UUID id) {
        log.info("Deactivating specialty with ID: {}", id);
        
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Specialty not found for deactivation with ID: {}", id);
                    return new NotFoundException("Specialty not found with id: " + id);
                });
        
        specialty.setActive(false);
        specialtyRepository.save(specialty);
        
        log.info("Successfully deactivated specialty with ID: {} and name: {}", id, specialty.getName());
    }
}
