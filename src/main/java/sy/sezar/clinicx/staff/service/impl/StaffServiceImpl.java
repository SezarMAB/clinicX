package sy.sezar.clinicx.staff.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.model.Specialty;
import sy.sezar.clinicx.clinic.repository.SpecialtyRepository;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.staff.dto.StaffCreateRequest;
import sy.sezar.clinicx.staff.dto.StaffDto;
import sy.sezar.clinicx.staff.dto.StaffSearchCriteria;
import sy.sezar.clinicx.staff.dto.StaffUpdateRequest;
import sy.sezar.clinicx.staff.mapper.StaffMapper;
import sy.sezar.clinicx.staff.model.Staff;
import sy.sezar.clinicx.staff.model.enums.StaffRole;
import sy.sezar.clinicx.staff.repository.StaffRepository;
import sy.sezar.clinicx.staff.service.StaffService;
import sy.sezar.clinicx.staff.spec.StaffSpecifications;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {
    
    private final StaffRepository staffRepository;
    private final SpecialtyRepository specialtyRepository;
    private final StaffMapper staffMapper;
    
    @Override
    @Transactional
    public StaffDto createStaff(StaffCreateRequest request) {
        log.info("Creating new staff member with name: {} and role: {}", request.getFullName(), request.getRole());
        
        // Check if email already exists
        if (staffRepository.existsByEmailIgnoreCase(request.getEmail())) {
            log.error("Staff member with email '{}' already exists", request.getEmail());
            throw new BusinessRuleException("Staff member with email '" + request.getEmail() + "' already exists");
        }
        
        Staff staff = staffMapper.toEntity(request);
        
        // Set specialties if provided
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            log.debug("Setting {} specialties for staff member", request.getSpecialtyIds().size());
            Set<Specialty> specialties = new HashSet<>(specialtyRepository.findAllById(request.getSpecialtyIds()));
            if (specialties.size() != request.getSpecialtyIds().size()) {
                log.error("One or more specialty IDs are invalid");
                throw new BusinessRuleException("One or more specialty IDs are invalid");
            }
            staff.setSpecialties(specialties);
        }
        
        staff = staffRepository.save(staff);
        log.info("Successfully created staff member with ID: {} and email: {}", staff.getId(), staff.getEmail());
        return staffMapper.toDto(staff);
    }
    
    @Override
    @Transactional
    public StaffDto updateStaff(UUID id, StaffUpdateRequest request) {
        log.info("Updating staff member with ID: {}", id);
        log.debug("Update request: name={}, role={}, active={}", 
                request.getFullName(), request.getRole(), request.isActive());
        
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Staff member not found with ID: {}", id);
                    return new NotFoundException("Staff member not found with id: " + id);
                });
        
        // Check if another staff member with the same email exists
        staffRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        log.error("Another staff member with email '{}' already exists", request.getEmail());
                        throw new BusinessRuleException("Staff member with email '" + request.getEmail() + "' already exists");
                    }
                });
        
        staffMapper.updateFromRequest(request, staff);
        
        // Update specialties if provided
        if (request.getSpecialtyIds() != null) {
            log.debug("Updating specialties for staff member, new count: {}", request.getSpecialtyIds().size());
            Set<Specialty> specialties = new HashSet<>(specialtyRepository.findAllById(request.getSpecialtyIds()));
            if (specialties.size() != request.getSpecialtyIds().size()) {
                log.error("One or more specialty IDs are invalid");
                throw new BusinessRuleException("One or more specialty IDs are invalid");
            }
            staff.setSpecialties(specialties);
        }
        
        staff = staffRepository.save(staff);
        log.info("Successfully updated staff member with ID: {}", id);
        return staffMapper.toDto(staff);
    }
    
    @Override
    public StaffDto findStaffById(UUID id) {
        log.info("Finding staff member by ID: {}", id);
        
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Staff member not found with ID: {}", id);
                    return new NotFoundException("Staff member not found with id: " + id);
                });
        
        log.debug("Found staff member: {} with role: {}", staff.getFullName(), staff.getRole());
        return staffMapper.toDto(staff);
    }
    
    @Override
    public Page<StaffDto> findAllStaff(Pageable pageable) {
        log.info("Finding all staff members with pagination: {}", pageable);
        
        Page<Staff> staffPage = staffRepository.findAll(pageable);
        log.info("Found {} staff members (page {} of {})",
                staffPage.getNumberOfElements(), 
                staffPage.getNumber() + 1, 
                staffPage.getTotalPages());
        
        return staffPage.map(staffMapper::toDto);
    }
    
    @Override
    public Page<StaffDto> findAllActiveStaff(Pageable pageable) {
        log.info("Finding all active staff members with pagination: {}", pageable);
        
        Page<Staff> staffPage = staffRepository.findAllActive(pageable);
        log.info("Found {} active staff members (page {} of {})",
                staffPage.getNumberOfElements(), 
                staffPage.getNumber() + 1, 
                staffPage.getTotalPages());
        
        return staffPage.map(staffMapper::toDto);
    }
    
    @Override
    public Page<StaffDto> findStaffByRole(StaffRole role, Pageable pageable) {
        log.info("Finding staff members by role: {} with pagination: {}", role, pageable);
        
        Page<Staff> staffPage = staffRepository.findByRole(role, pageable);
        log.info("Found {} staff members with role {} (page {} of {})",
                staffPage.getNumberOfElements(), 
                role,
                staffPage.getNumber() + 1, 
                staffPage.getTotalPages());
        
        return staffPage.map(staffMapper::toDto);
    }
    
    @Override
    public Page<StaffDto> searchStaff(StaffSearchCriteria criteria, Pageable pageable) {
        log.info("Searching staff members with advanced criteria: {}", criteria);
        log.debug("Search pagination: {}", pageable);
        
        Specification<Staff> spec = StaffSpecifications.withCriteria(criteria);
        Page<Staff> staffPage = staffRepository.findAll(spec, pageable);
        
        log.info("Advanced search found {} staff members (page {} of {})",
                staffPage.getNumberOfElements(), 
                staffPage.getNumber() + 1, 
                staffPage.getTotalPages());
        
        return staffPage.map(staffMapper::toDto);
    }
    
    @Override
    public Page<StaffDto> searchStaff(String searchTerm, Pageable pageable) {
        log.info("Searching staff members with term: '{}' and pagination: {}", searchTerm, pageable);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            log.debug("Empty search term, returning all staff members");
            return findAllStaff(pageable);
        }
        
        Page<Staff> staffPage = staffRepository.searchStaff(searchTerm.trim(), pageable);
        log.info("Search found {} staff members (page {} of {})",
                staffPage.getNumberOfElements(), 
                staffPage.getNumber() + 1, 
                staffPage.getTotalPages());
        
        return staffPage.map(staffMapper::toDto);
    }
    
    @Override
    @Transactional
    public void deactivateStaff(UUID id) {
        log.info("Deactivating staff member with ID: {}", id);
        
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Staff member not found for deactivation with ID: {}", id);
                    return new NotFoundException("Staff member not found with id: " + id);
                });
        
        staff.setActive(false);
        staffRepository.save(staff);
        
        log.info("Successfully deactivated staff member with ID: {} and name: {}", id, staff.getFullName());
    }
}
