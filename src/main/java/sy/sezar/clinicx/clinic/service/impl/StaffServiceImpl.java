package sy.sezar.clinicx.clinic.service.impl;

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
import sy.sezar.clinicx.clinic.dto.StaffCreateRequest;
import sy.sezar.clinicx.clinic.dto.StaffDto;
import sy.sezar.clinicx.clinic.dto.StaffSearchCriteria;
import sy.sezar.clinicx.clinic.dto.StaffUpdateRequest;
import sy.sezar.clinicx.clinic.dto.StaffWithAccessDto;
import sy.sezar.clinicx.clinic.mapper.StaffMapper;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.clinic.service.StaffService;
import sy.sezar.clinicx.clinic.spec.StaffSpecifications;
import sy.sezar.clinicx.tenant.service.UserTenantAccessService;
import sy.sezar.clinicx.tenant.dto.CreateUserTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.UserTenantAccessDto;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import org.keycloak.representations.idm.UserRepresentation;

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
    private final UserTenantAccessService userTenantAccessService;
    private final KeycloakAdminService keycloakAdminService;
    private final TenantRepository tenantRepository;

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
        
        // Set tenant context
        String currentTenantId = TenantContext.getCurrentTenant();
        if (currentTenantId == null) {
            throw new BusinessRuleException("No tenant context available");
        }
        staff.setTenantId(currentTenantId);
        
        // Handle Keycloak user creation or linking
        String keycloakUserId = null;
        if (request.isCreateKeycloakUser()) {
            // Create new Keycloak user
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new BusinessRuleException("Password is required when creating a Keycloak user");
            }
            
            // Get tenant to find realm name
            var tenant = tenantRepository.findByTenantId(currentTenantId)
                .orElseThrow(() -> new BusinessRuleException("Tenant not found: " + currentTenantId));
            
            // Extract first and last name
            String firstName = request.getFirstName();
            String lastName = request.getLastName();
            if (firstName == null || lastName == null) {
                String[] nameParts = request.getFullName().split(" ", 2);
                firstName = firstName != null ? firstName : nameParts[0];
                lastName = lastName != null ? lastName : (nameParts.length > 1 ? nameParts[1] : "");
            }
            
            String username = request.getUsername() != null ? request.getUsername() : request.getEmail();
            
            try {
                // Create user in Keycloak with tenant info
                UserRepresentation createdUser = keycloakAdminService.createUserWithTenantInfo(
                    tenant.getRealmName(),
                    username,
                    request.getEmail(),
                    firstName,
                    lastName,
                    request.getPassword(),
                    java.util.List.of(request.getRole().name()),
                    currentTenantId,
                    tenant.getName(),
                    tenant.getSpecialty()
                );
                keycloakUserId = createdUser.getId();
                log.info("Created Keycloak user with ID: {} for staff member", keycloakUserId);
            } catch (Exception e) {
                log.error("Failed to create Keycloak user: {}", e.getMessage());
                throw new BusinessRuleException("Failed to create Keycloak user: " + e.getMessage());
            }
        } else if (request.getKeycloakUserId() != null) {
            // Use provided Keycloak user ID
            keycloakUserId = request.getKeycloakUserId();
        }
        
        // Set Keycloak user ID if available
        if (keycloakUserId != null) {
            staff.setKeycloakUserId(keycloakUserId);
        }

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
        
        // Create user_tenant_access record if Keycloak user ID is provided
        if (staff.getKeycloakUserId() != null) {
            try {
                CreateUserTenantAccessRequest accessRequest = CreateUserTenantAccessRequest.builder()
                    .userId(staff.getKeycloakUserId())
                    .tenantId(currentTenantId)
                    .role(request.getAccessRole() != null ? request.getAccessRole() : request.getRole().name())
                    .isPrimary(request.isPrimaryTenant())
                    .isActive(true)
                    .build();
                
                UserTenantAccessDto accessDto = userTenantAccessService.grantAccess(accessRequest);
                log.info("Created user_tenant_access with ID {} for user {} in tenant {}", 
                    accessDto.getId(), staff.getKeycloakUserId(), currentTenantId);
            } catch (Exception e) {
                log.error("Failed to create user_tenant_access record: {}", e.getMessage());
                // If we created a Keycloak user but failed to create access, we should rollback
                if (request.isCreateKeycloakUser()) {
                    throw new BusinessRuleException("Failed to create user access record: " + e.getMessage());
                }
                // For existing users, just log the warning
                log.warn("Could not create user_tenant_access record for existing user: {}", e.getMessage());
            }
        }
        
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
    
    @Override
    public StaffWithAccessDto getStaffWithAccess(UUID id) {
        log.info("Getting staff member with access information for ID: {}", id);
        
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Staff member not found with id: " + id));
        
        String currentTenantId = TenantContext.getCurrentTenant();
        
        // Build the response DTO
        StaffWithAccessDto.StaffWithAccessDtoBuilder builder = StaffWithAccessDto.builder()
            .id(staff.getId())
            .fullName(staff.getFullName())
            .role(staff.getRole())
            .email(staff.getEmail())
            .phoneNumber(staff.getPhoneNumber())
            .isActive(staff.isActive())
            .specialties(staffMapper.toDto(staff).getSpecialties())
            .keycloakUserId(staff.getKeycloakUserId())
            .tenantId(staff.getTenantId())
            .createdAt(staff.getCreatedAt())
            .updatedAt(staff.getUpdatedAt());
        
        // Try to get access information if Keycloak user ID exists
        if (staff.getKeycloakUserId() != null && currentTenantId != null) {
            try {
                UserTenantAccessDto access = userTenantAccessService.getAccess(
                    staff.getKeycloakUserId(), 
                    currentTenantId
                );
                builder.accessRole(access.getRole())
                       .isPrimary(access.isPrimary())
                       .accessActive(access.isActive());
            } catch (Exception e) {
                log.debug("No access information found for staff member: {}", e.getMessage());
                // Set defaults if no access found
                builder.accessRole(staff.getRole().name())
                       .isPrimary(false)
                       .accessActive(staff.isActive());
            }
        }
        
        return builder.build();
    }
    
    @Override
    public Page<StaffWithAccessDto> getAllStaffWithAccess(Pageable pageable) {
        log.info("Getting all staff members with access information");
        
        String currentTenantId = TenantContext.getCurrentTenant();
        if (currentTenantId == null) {
            throw new BusinessRuleException("No tenant context available");
        }
        
        // Get staff for current tenant
        Specification<Staff> spec = StaffSpecifications.byTenantId(currentTenantId);
        Page<Staff> staffPage = staffRepository.findAll(spec, pageable);
        
        return staffPage.map(staff -> {
            StaffWithAccessDto.StaffWithAccessDtoBuilder builder = StaffWithAccessDto.builder()
                .id(staff.getId())
                .fullName(staff.getFullName())
                .role(staff.getRole())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .isActive(staff.isActive())
                .specialties(staffMapper.toDto(staff).getSpecialties())
                .keycloakUserId(staff.getKeycloakUserId())
                .tenantId(staff.getTenantId())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt());
            
            // Try to get access information
            if (staff.getKeycloakUserId() != null) {
                try {
                    UserTenantAccessDto access = userTenantAccessService.getAccess(
                        staff.getKeycloakUserId(), 
                        currentTenantId
                    );
                    builder.accessRole(access.getRole())
                           .isPrimary(access.isPrimary())
                           .accessActive(access.isActive());
                } catch (Exception e) {
                    // Use defaults if no access found
                    builder.accessRole(staff.getRole().name())
                           .isPrimary(false)
                           .accessActive(staff.isActive());
                }
            }
            
            return builder.build();
        });
    }
}
