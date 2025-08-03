package sy.sezar.clinicx.tenant.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.dto.StaffCreateRequest;
import sy.sezar.clinicx.clinic.dto.StaffDto;
import sy.sezar.clinicx.clinic.mapper.StaffMapper;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.Specialty;
import sy.sezar.clinicx.clinic.repository.SpecialtyRepository;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.tenant.controller.api.StaffKeycloakSyncService;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;

import java.util.*;

/**
 * Implementation of StaffKeycloakSyncService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffKeycloakSyncServiceImpl implements StaffKeycloakSyncService {

    private final StaffRepository staffRepository;
    private final SpecialtyRepository specialtyRepository;
    private final TenantRepository tenantRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final StaffMapper staffMapper;

    @Override
    @Transactional
    public StaffDto createStaffWithKeycloakUser(StaffCreateRequest request, String password, boolean createKeycloakUser) {
        log.info("Creating staff with Keycloak user: {} (createKeycloakUser: {})", request.getEmail(), createKeycloakUser);

        // Check if email already exists
        if (staffRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessRuleException("Staff member with email '" + request.getEmail() + "' already exists");
        }

        String currentTenantId = TenantContext.getCurrentTenant();
        Tenant tenant = tenantRepository.findByTenantId(currentTenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + currentTenantId));

        // Create Staff entity
        Staff staff = staffMapper.toEntity(request);
        staff.setTenantId(currentTenantId);
        staff.setPrimary(true);

        // Set specialties if provided
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            Set<Specialty> specialties = new HashSet<>(specialtyRepository.findAllById(request.getSpecialtyIds()));
            if (specialties.size() != request.getSpecialtyIds().size()) {
                throw new BusinessRuleException("One or more specialty IDs are invalid");
            }
            staff.setSpecialties(specialties);
        }

        String userId = null;

        if (createKeycloakUser) {
            // Create Keycloak user
            try {
                // Generate username from email if not provided
                String username = request.getEmail().split("@")[0];

                // Extract first and last name from full name
                String[] nameParts = request.getFullName().trim().split("\\s+", 2);
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                UserRepresentation user = keycloakAdminService.createUserWithTenantInfo(
                    tenant.getRealmName(),
                    username,
                    request.getEmail(),
                    firstName,
                    lastName,
                    password,
                    Arrays.asList(request.getRole().name()),
                    currentTenantId,
                    tenant.getName(),
                    tenant.getSpecialty()
                );

                userId = user.getId();
                log.info("Created Keycloak user {} with ID {}", username, userId);

            } catch (Exception e) {
                log.error("Failed to create Keycloak user", e);
                throw new BusinessRuleException("Failed to create user account: " + e.getMessage());
            }
        }

        // Set Keycloak user ID if created
        if (userId != null) {
            staff.setUserId(userId);
        }

        // Save Staff record
        staff = staffRepository.save(staff);
        log.info("Created Staff record with ID {} for {}", staff.getId(), staff.getEmail());

        return staffMapper.toDto(staff);
    }

    @Override
    @Transactional
    public String syncStaffToKeycloak(Staff staff, String password) {
        log.info("Syncing Staff {} to Keycloak", staff.getEmail());

        if (staff.getUserId() != null) {
            log.warn("Staff {} already has Keycloak user ID: {}", staff.getEmail(), staff.getUserId());
            return staff.getUserId();
        }

        String tenantId = staff.getTenantId();
        if (tenantId == null) {
            tenantId = TenantContext.getCurrentTenant();
        }

        final String finalTenantId = tenantId;
        Tenant tenant = tenantRepository.findByTenantId(finalTenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + finalTenantId));

        try {
            // Generate username from email
            String username = staff.getEmail().split("@")[0];

            // Extract first and last name from full name
            String[] nameParts = staff.getFullName().trim().split("\\s+", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            UserRepresentation user = keycloakAdminService.createUserWithTenantInfo(
                tenant.getRealmName(),
                username,
                staff.getEmail(),
                firstName,
                lastName,
                password,
                Arrays.asList(staff.getRole().name()),
                tenantId,
                tenant.getName(),
                tenant.getSpecialty()
            );

            // Update Staff with Keycloak user ID
            staff.setUserId(user.getId());
            staffRepository.save(staff);

            log.info("Successfully synced Staff {} to Keycloak with user ID {}", staff.getEmail(), user.getId());
            return user.getId();

        } catch (Exception e) {
            log.error("Failed to sync Staff to Keycloak", e);
            throw new BusinessRuleException("Failed to create user account: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateStaffFromKeycloak(String userId, String tenantId) {
        log.info("Updating Staff from Keycloak user {} in tenant {}", userId, tenantId);

        Optional<Staff> staffOpt = staffRepository.findByUserIdAndTenantId(userId, tenantId);
        if (staffOpt.isEmpty()) {
            log.warn("No Staff record found for Keycloak user {} in tenant {}", userId, tenantId);
            return;
        }

        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + tenantId));

        try {
            UserRepresentation user = keycloakAdminService.getKeycloakInstance()
                .realm(tenant.getRealmName())
                .users()
                .get(userId)
                .toRepresentation();

            Staff staff = staffOpt.get();
            staff.setEmail(user.getEmail());
            staff.setFullName(user.getFirstName() + " " + user.getLastName());
            staff.setActive(user.isEnabled());

            staffRepository.save(staff);
            log.info("Updated Staff record from Keycloak user {}", userId);

        } catch (Exception e) {
            log.error("Failed to update Staff from Keycloak", e);
        }
    }

    @Override
    public boolean hasKeycloakUser(String staffId) {
        return staffRepository.findById(UUID.fromString(staffId))
            .map(staff -> staff.getUserId() != null)
            .orElse(false);
    }
}
