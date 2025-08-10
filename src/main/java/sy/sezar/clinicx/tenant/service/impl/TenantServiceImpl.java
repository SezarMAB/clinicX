package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.mapper.TenantMapper;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.service.TenantService;
import sy.sezar.clinicx.tenant.spec.TenantSpecifications;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.clinic.model.Staff;
import org.springframework.beans.factory.annotation.Value;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the Tenant Service.
 * Handles business logic for tenant management operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final KeycloakAdminService keycloakAdminService;
    private final StaffRepository staffRepository;
    
    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Override
    public TenantCreationResponseDto createTenant(TenantCreateRequest request) {
        log.info("Creating tenant with subdomain: {}", request.subdomain());

        // Validate subdomain uniqueness
        if (tenantRepository.existsBySubdomain(request.subdomain())) {
            throw new BusinessRuleException("Subdomain already exists: " + request.subdomain());
        }

        // Generate unique tenant ID and realm name
        String tenantId = generateTenantId(request.subdomain());
        String realmName = "clinic-" + request.subdomain();
//TODO copy the clint with secret from the template
        // Check if realm already exists
        if (keycloakAdminService.realmExists(realmName)) {
            throw new BusinessRuleException("Realm already exists in Keycloak: " + realmName);
        }

        try {
            // Create realm in Keycloak
            keycloakAdminService.createRealm(realmName, request.name());

            // Create admin user in the new realm with tenant attributes
            keycloakAdminService.createUserWithTenantInfo(
                realmName,
                request.adminUsername(),
                request.adminEmail(),
                request.adminFirstName(),
                request.adminLastName(),
                request.adminPassword(),
                Arrays.asList("ADMIN"),
                tenantId,
                request.name(), // clinic name
                request.subscriptionPlan() != null ? request.subscriptionPlan() : "GENERAL" // clinic type defaults to GENERAL
            );

            // Create tenant entity
            Tenant tenant = new Tenant();
            tenant.setTenantId(tenantId);
            tenant.setName(request.name());
            tenant.setSubdomain(request.subdomain());
            tenant.setRealmName(realmName);
            tenant.setSpecialty(request.specialty() != null ? request.specialty() : "CLINIC");
            tenant.setActive(true);
            tenant.setContactEmail(request.contactEmail());
            tenant.setContactPhone(request.contactPhone());
            tenant.setAddress(request.address());
            tenant.setSubscriptionPlan(request.subscriptionPlan());
            tenant.setMaxUsers(request.maxUsers());
            tenant.setMaxPatients(request.maxPatients());
            tenant.setSubscriptionStartDate(Instant.now());

            // Set subscription end date based on plan (example: 1 year)
            tenant.setSubscriptionEndDate(Instant.now().plusSeconds(365 * 24 * 60 * 60));

            tenant = tenantRepository.save(tenant);
            
            // Get the backend client secret
            String backendClientSecret = keycloakAdminService.getClientSecret(realmName, "clinicx-backend");

            log.info("Successfully created tenant with ID: {}", tenant.getId());
            
            // Return the creation response with client configuration
            return new TenantCreationResponseDto(
                tenant.getId(),
                tenant.getTenantId(),
                tenant.getName(),
                tenant.getSubdomain(),
                tenant.getRealmName(),
                "clinicx-backend",
                backendClientSecret,
                "clinicx-frontend",
                keycloakServerUrl,
                request.adminUsername()
            );

        } catch (Exception e) {
            log.error("Failed to create tenant, rolling back", e);
            // Rollback Keycloak changes if tenant creation fails
            try {
                keycloakAdminService.deleteRealm(realmName);
            } catch (Exception ex) {
                log.error("Failed to rollback Keycloak realm creation", ex);
            }
            throw new BusinessRuleException("Failed to create tenant: " + e.getMessage());
        }
    }

    @Override
    public TenantDetailDto getTenantById(UUID id) {
        log.debug("Fetching tenant by ID: {}", id);
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));

        return enrichWithUsageStats(tenantMapper.toDetailDto(tenant));
    }

    @Override
    public TenantDetailDto getTenantByTenantId(String tenantId) {
        log.debug("Fetching tenant by tenantId: {}", tenantId);
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found with tenantId: " + tenantId));

        return enrichWithUsageStats(tenantMapper.toDetailDto(tenant));
    }

    @Override
    public TenantDetailDto getTenantBySubdomain(String subdomain) {
        log.debug("Fetching tenant by subdomain: {}", subdomain);
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new NotFoundException("Tenant not found with subdomain: " + subdomain));

        return enrichWithUsageStats(tenantMapper.toDetailDto(tenant));
    }

    @Override
    public Page<TenantSummaryDto> getAllTenants(String searchTerm, Boolean isActive, Pageable pageable) {
        return searchTenants(searchTerm, isActive, pageable);
    }

    @Override
    public Page<TenantSummaryDto> searchTenants(String searchTerm, Boolean isActive, Pageable pageable) {
        log.debug("Searching tenants with searchTerm: {}, isActive: {}", searchTerm, isActive);

        Specification<Tenant> spec = TenantSpecifications.buildSearchSpecification(searchTerm, isActive);

        return tenantRepository.findAll(spec, pageable)
                .map(tenant -> {
                    // Get usage stats
                    int currentUsers = getCurrentUserCount(tenant.getRealmName());
                    int currentPatients = getCurrentPatientCount(tenant.getTenantId());

                    return tenantMapper.toSummaryDto(tenant, currentUsers, currentPatients);
                });
    }

    @Override
    public TenantDetailDto updateTenant(UUID id, TenantUpdateRequest request) {
        log.info("Updating tenant with ID: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));

        // Update allowed fields
        if (request.name() != null) {
            tenant.setName(request.name());
        }
        if (request.contactEmail() != null) {
            tenant.setContactEmail(request.contactEmail());
        }
        if (request.contactPhone() != null) {
            tenant.setContactPhone(request.contactPhone());
        }
        if (request.address() != null) {
            tenant.setAddress(request.address());
        }
        if (request.maxUsers() != null) {
            tenant.setMaxUsers(request.maxUsers());
        }
        if (request.maxPatients() != null) {
            tenant.setMaxPatients(request.maxPatients());
        }
        if (request.subscriptionPlan() != null) {
            tenant.setSubscriptionPlan(request.subscriptionPlan());
        }
        if (request.subscriptionEndDate() != null) {
            tenant.setSubscriptionEndDate(request.subscriptionEndDate());
        }

        tenant = tenantRepository.save(tenant);

        log.info("Successfully updated tenant with ID: {}", id);
        return enrichWithUsageStats(tenantMapper.toDetailDto(tenant));
    }

    @Override
    public void activateTenant(UUID id) {
        log.info("Activating tenant with ID: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));

        String tenantId = tenant.getTenantId();
        String realmName = tenant.getRealmName();
        
        // Activate the tenant
        tenant.setActive(true);
        
        // Always only enable users belonging to this specific tenant
        log.info("Enabling users belonging to tenant '{}' in realm '{}'", tenantId, realmName);
        try {
            // Get all users belonging to this tenant based on tenant_id attribute
            List<UserRepresentation> tenantUsers = keycloakAdminService.getUsersByTenantId(realmName, tenantId);
            log.info("Found {} users belonging to tenant '{}' in realm '{}'", tenantUsers.size(), tenantId, realmName);
            
            // Enable each user in Keycloak
            for (UserRepresentation user : tenantUsers) {
                try {
                    keycloakAdminService.enableUser(realmName, user.getId());
                    log.debug("Enabled user '{}' ({}) in Keycloak", user.getUsername(), user.getId());
                } catch (Exception e) {
                    log.error("Failed to enable user '{}' in Keycloak: {}", user.getUsername(), e.getMessage());
                }
            }
            log.info("Enabled {} users in Keycloak for tenant '{}'", tenantUsers.size(), tenantId);
        } catch (Exception e) {
            log.error("Failed to enable tenant users in Keycloak: {}", e.getMessage());
            // Continue with tenant activation even if Keycloak operation fails
        }
        
        // Activate all staff records for this tenant in database
        try {
            List<Staff> staffList = staffRepository.findByTenantId(tenantId);
            for (Staff staff : staffList) {
                staff.setActive(true);
            }
            staffRepository.saveAll(staffList);
            log.info("Activated {} staff records for tenant '{}' in database", staffList.size(), tenantId);
        } catch (Exception e) {
            log.error("Failed to activate staff records for tenant '{}': {}", tenantId, e.getMessage());
            // Continue with tenant activation even if staff activation fails
        }
        
        // Save the activated tenant
        tenantRepository.save(tenant);

        log.info("Successfully activated tenant with ID: {} (tenantId: {})", id, tenantId);
    }

    @Override
    public void deactivateTenant(UUID id) {
        log.info("Deactivating tenant with ID: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));

        tenant.setActive(false);
        tenantRepository.save(tenant);

        log.info("Successfully deactivated tenant with ID: {}", id);
    }

    @Override
    public void deleteTenant(UUID id) {
        log.warn("Soft deleting (deactivating) tenant with ID: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));

        String tenantId = tenant.getTenantId();
        String realmName = tenant.getRealmName();
        
        // Deactivate the tenant
        tenant.setActive(false);
        
        // Always only disable users belonging to this specific tenant
        log.info("Disabling users belonging to tenant '{}' in realm '{}'", tenantId, realmName);
        try {
            // Get all users belonging to this tenant based on tenant_id attribute
            List<UserRepresentation> tenantUsers = keycloakAdminService.getUsersByTenantId(realmName, tenantId);
            log.info("Found {} users belonging to tenant '{}' in realm '{}'", tenantUsers.size(), tenantId, realmName);
            
            // Disable each user in Keycloak
            for (UserRepresentation user : tenantUsers) {
                try {
                    keycloakAdminService.disableUser(realmName, user.getId());
                    log.debug("Disabled user '{}' ({}) in Keycloak", user.getUsername(), user.getId());
                } catch (Exception e) {
                    log.error("Failed to disable user '{}' in Keycloak: {}", user.getUsername(), e.getMessage());
                }
            }
            log.info("Disabled {} users in Keycloak for tenant '{}'", tenantUsers.size(), tenantId);
        } catch (Exception e) {
            log.error("Failed to disable tenant users in Keycloak: {}", e.getMessage());
            // Continue with tenant deactivation even if Keycloak operation fails
        }

        // Deactivate all staff records for this tenant in database
        try {
            List<Staff> staffList = staffRepository.findByTenantId(tenantId);
            for (Staff staff : staffList) {
                staff.setActive(false);
            }
            staffRepository.saveAll(staffList);
            log.info("Deactivated {} staff records for tenant '{}' in database", staffList.size(), tenantId);
        } catch (Exception e) {
            log.error("Failed to deactivate staff records for tenant '{}': {}", tenantId, e.getMessage());
            // Continue with tenant deactivation even if staff deactivation fails
        }

        // Save the deactivated tenant
        tenantRepository.save(tenant);

        log.warn("Successfully soft deleted (deactivated) tenant with ID: {} (tenantId: {})", id, tenantId);
    }

    @Override
    public boolean isTenantActive(String tenantId) {
        return tenantRepository.findByTenantId(tenantId)
                .map(Tenant::isActive)
                .orElse(false);
    }

    @Override
    public SubdomainAvailabilityDto checkSubdomainAvailability(String subdomain) {
        log.debug("Checking availability for subdomain: {}", subdomain);

        boolean isAvailable = !tenantRepository.existsBySubdomain(subdomain);
        String message = isAvailable
            ? "Subdomain is available"
            : "Subdomain is already taken";

        return new SubdomainAvailabilityDto(subdomain, isAvailable, message);
    }

    @Override
    public void updateTenantUsageStats(String tenantId) {
        log.debug("Updating usage stats for tenant: {}", tenantId);
        // This method can be called periodically to update usage statistics
        // Implementation depends on specific requirements
    }

    private String generateTenantId(String subdomain) {
        return subdomain + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private TenantDetailDto enrichWithUsageStats(TenantDetailDto dto) {
        int currentUsers = getCurrentUserCount(dto.realmName());
        int currentPatients = getCurrentPatientCount(dto.tenantId());

        return new TenantDetailDto(
            dto.id(),
            dto.tenantId(),
            dto.name(),
            dto.subdomain(),
            dto.realmName(),
            dto.specialty(),
            dto.isActive(),
            dto.contactEmail(),
            dto.contactPhone(),
            dto.address(),
            dto.subscriptionStartDate(),
            dto.subscriptionEndDate(),
            dto.subscriptionPlan(),
            dto.maxUsers(),
            dto.maxPatients(),
            currentUsers,
            currentPatients,
            dto.createdAt(),
            dto.updatedAt(),
            dto.createdBy(),
            dto.updatedBy()
        );
    }

    private int getCurrentUserCount(String realmName) {
        try {
            List<?> users = keycloakAdminService.getRealmUsers(realmName);
            return users.size();
        } catch (Exception e) {
            log.error("Failed to get user count for realm: {}", realmName, e);
            return 0;
        }
    }

    private int getCurrentPatientCount(String tenantId) {
        // This would need to be implemented based on your patient repository
        // For now, returning 0 as placeholder
        return 0;
    }

    @Override
    public void resetTenantAdminPassword(UUID tenantId, String adminUsername, String newPassword) {
        log.info("Resetting admin password for tenant ID: {} and username: {}", tenantId, adminUsername);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + tenantId));

        try {
            // Reset the password in Keycloak
            keycloakAdminService.resetUserPassword(tenant.getRealmName(), adminUsername, newPassword);
            log.info("Successfully reset password for admin user '{}' in tenant '{}'", adminUsername, tenant.getName());
        } catch (Exception e) {
            log.error("Failed to reset password for admin user '{}' in realm '{}'", adminUsername, tenant.getRealmName(), e);
            throw new BusinessRuleException("Failed to reset admin password: " + e.getMessage());
        }
    }
}
