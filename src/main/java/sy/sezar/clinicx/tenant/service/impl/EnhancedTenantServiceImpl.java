package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.mapper.TenantMapper;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.repository.UserTenantAccessRepository;
import sy.sezar.clinicx.tenant.service.DynamicRealmService;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.service.TenantService;

import java.time.Instant;
import java.util.*;

/**
 * Enhanced tenant service that supports realm-per-type architecture.
 */
@Primary
@Service("enhancedTenantService")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnhancedTenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    private DynamicRealmService dynamicRealmService;

    @Autowired
    private UserTenantAccessRepository userTenantAccessRepository;

    @Value("${app.multi-tenant.realm-per-type:true}")
    private boolean realmPerTypeEnabled;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Override
    public TenantCreationResponseDto createTenant(TenantCreateRequest request) {
        log.info("Creating tenant with subdomain: {} and specialty: {}",
                request.subdomain(), request.specialty());

        // Validate subdomain uniqueness
        if (tenantRepository.existsBySubdomain(request.subdomain())) {
            throw new BusinessRuleException("Subdomain already exists: " + request.subdomain());
        }

        String realmName;
        String specialty = request.specialty() != null ? request.specialty() : "CLINIC";

        // Determine realm based on configuration
        if (realmPerTypeEnabled) {
            // Use realm-per-type approach
            realmName = dynamicRealmService.resolveRealmForTenant(request);
            log.info("Using shared realm {} for specialty {}", realmName, specialty);
        } else {
            // Use traditional realm-per-tenant approach
            realmName = "clinic-" + request.subdomain();
            if (keycloakAdminService.realmExists(realmName)) {
                throw new BusinessRuleException("Realm already exists in Keycloak: " + realmName);
            }
            keycloakAdminService.createRealm(realmName, request.name());
        }

        try {
            // Generate unique tenant ID
            String tenantId = generateTenantId(request.subdomain());

            // Create admin user in the realm with tenant attributes
            keycloakAdminService.createUserWithTenantInfo(
                realmName,
                request.adminUsername(),
                request.adminEmail(),
                request.adminFirstName(),
                request.adminLastName(),
                request.adminPassword(),
                List.of("ADMIN"),
                tenantId,
                request.name(),
                specialty
            );

            // For multi-tenant support, update user attributes
            if (realmPerTypeEnabled) {
                Map<String, List<String>> attributes = new HashMap<>();
                attributes.put("primary_tenant_id", Arrays.asList(tenantId));
                attributes.put("accessible_tenants", Arrays.asList(
                    tenantId + "|" + request.name() + "|ADMIN"
                ));
                attributes.put("active_tenant_id", Arrays.asList(tenantId));

                keycloakAdminService.updateUserAttributes(realmName, request.adminUsername(), attributes);
            }

            // Create tenant entity
            Tenant tenant = new Tenant();
            tenant.setTenantId(tenantId);
            tenant.setName(request.name());
            tenant.setSubdomain(request.subdomain());
            tenant.setRealmName(realmName);
            tenant.setSpecialty(specialty);
            tenant.setActive(true);
            tenant.setContactEmail(request.contactEmail());
            tenant.setContactPhone(request.contactPhone());
            tenant.setAddress(request.address());
            tenant.setSubscriptionPlan(request.subscriptionPlan());
            tenant.setMaxUsers(request.maxUsers());
            tenant.setMaxPatients(request.maxPatients());
            tenant.setSubscriptionStartDate(Instant.now());
            tenant.setSubscriptionEndDate(Instant.now().plusSeconds(365 * 24 * 60 * 60));

            tenant = tenantRepository.save(tenant);

            // Create user-tenant access record
            if (realmPerTypeEnabled) {
                UserTenantAccess userAccess = new UserTenantAccess();
                userAccess.setUserId(request.adminUsername()); // In production, use actual Keycloak user ID
                userAccess.setTenantId(tenantId);
                userAccess.setRole("ADMIN");
                userAccess.setPrimary(true);
                userTenantAccessRepository.save(userAccess);
            }

            // Get the backend client secret
            String backendClientSecret = "";
            try {
                backendClientSecret = keycloakAdminService.getClientSecret(realmName, "clinicx-backend");
            } catch (Exception e) {
                log.warn("Could not retrieve client secret: {}", e.getMessage());
            }

            log.info("Successfully created tenant with ID: {} in realm: {}", tenant.getId(), realmName);

            // Return the creation response
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
            if (!realmPerTypeEnabled) {
                try {
                    keycloakAdminService.deleteRealm(realmName);
                } catch (Exception ex) {
                    log.error("Failed to rollback Keycloak realm creation", ex);
                }
            }
            throw new BusinessRuleException("Failed to create tenant: " + e.getMessage());
        }
    }

    // Delegate other methods to the original implementation
    @Autowired
    private TenantServiceImpl originalService;

    @Override
    public TenantDetailDto getTenantById(UUID id) {
        return originalService.getTenantById(id);
    }

    @Override
    public TenantDetailDto getTenantByTenantId(String tenantId) {
        return originalService.getTenantByTenantId(tenantId);
    }

    @Override
    public TenantDetailDto getTenantBySubdomain(String subdomain) {
        return originalService.getTenantBySubdomain(subdomain);
    }

    @Override
    public Page<TenantSummaryDto> getAllTenants(String searchTerm, Boolean isActive, Pageable pageable) {
        return originalService.getAllTenants(searchTerm, isActive, pageable);
    }

    @Override
    public Page<TenantSummaryDto> searchTenants(String searchTerm, Boolean isActive, Pageable pageable) {
        return originalService.searchTenants(searchTerm, isActive, pageable);
    }

    @Override
    public TenantDetailDto updateTenant(UUID id, TenantUpdateRequest request) {
        return originalService.updateTenant(id, request);
    }

    @Override
    public void activateTenant(UUID id) {
        originalService.activateTenant(id);
    }

    @Override
    public void deactivateTenant(UUID id) {
        originalService.deactivateTenant(id);
    }

    @Override
    public void deleteTenant(UUID id) {
        originalService.deleteTenant(id);
    }

    @Override
    public boolean isTenantActive(String tenantId) {
        return originalService.isTenantActive(tenantId);
    }

    @Override
    public SubdomainAvailabilityDto checkSubdomainAvailability(String subdomain) {
        return originalService.checkSubdomainAvailability(subdomain);
    }

    @Override
    public void updateTenantUsageStats(String tenantId) {
        originalService.updateTenantUsageStats(tenantId);
    }

    private String generateTenantId(String subdomain) {
        return subdomain + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
