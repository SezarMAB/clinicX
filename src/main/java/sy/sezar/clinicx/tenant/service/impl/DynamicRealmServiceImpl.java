package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.tenant.config.SpecialtyRealmMappingConfig;
import sy.sezar.clinicx.tenant.dto.TenantCreateRequest;
import sy.sezar.clinicx.tenant.model.SpecialtyType;
import sy.sezar.clinicx.tenant.repository.SpecialtyTypeRepository;
import sy.sezar.clinicx.tenant.service.DynamicRealmService;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of dynamic realm creation service.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DynamicRealmServiceImpl implements DynamicRealmService {
    
    private final KeycloakAdminService keycloakAdminService;
    private final SpecialtyTypeRepository specialtyTypeRepository;
    
    @Autowired
    private SpecialtyRealmMappingConfig mappingConfig;
    
    @Override
    public String resolveRealmForTenant(TenantCreateRequest request) {
        // Check if specialty-realm mapping is enabled
        if (!mappingConfig.isEnabled()) {
            log.debug("Specialty-realm mapping is disabled, using traditional realm-per-tenant");
            return "clinic-" + request.subdomain();
        }
        
        String specialty = request.specialty() != null ? request.specialty() : SpecialtyType.CLINIC;
        
        // Check if specialty exists in registry
        SpecialtyType specialtyType = specialtyTypeRepository.findByCode(specialty)
            .orElseThrow(() -> new BusinessRuleException("Unknown specialty: " + specialty));
        
        String realmName = specialtyType.getRealmName();
        
        // Create realm if it doesn't exist and auto-create is enabled
        if (!keycloakAdminService.realmExists(realmName)) {
            if (mappingConfig.isAutoCreateRealm()) {
                log.info("Auto-creating new realm for specialty {}: {}", specialty, realmName);
                keycloakAdminService.createRealm(realmName, specialtyType.getName() + " Realm");
                configureRealmForSpecialty(realmName, specialty);
            } else {
                throw new BusinessRuleException("Realm does not exist for specialty " + specialty + 
                    " and auto-creation is disabled");
            }
        }
        
        return realmName;
    }
    
    @Override
    public void configureRealmForSpecialty(String realmName, String specialty) {
        log.info("Configuring realm {} for specialty {}", realmName, specialty);
        
        // Create standard realm roles
        createRealmRoles(realmName, specialty);
        
        // TODO: Copy clients from template realm when that functionality is implemented
        // For now, we'll need to manually configure clients in Keycloak
        
        // Ensure protocol mappers are configured
        ensureProtocolMappers(realmName, "clinicx-backend");
        ensureProtocolMappers(realmName, "clinicx-frontend");
    }
    
    @Override
    public boolean hasRealmForSpecialty(String specialty) {
        return specialtyTypeRepository.findByCode(specialty)
            .map(st -> keycloakAdminService.realmExists(st.getRealmName()))
            .orElse(false);
    }
    
    @Override
    public String getRealmNameForSpecialty(String specialty) {
        return specialtyTypeRepository.findByCode(specialty)
            .map(SpecialtyType::getRealmName)
            .orElseThrow(() -> new BusinessRuleException("Unknown specialty: " + specialty));
    }
    
    @Override
    public void ensureProtocolMappers(String realmName, String clientId) {
        log.debug("Ensuring protocol mappers for realm {} and client {}", realmName, clientId);
        
        // These mappers ensure tenant attributes appear in JWT tokens
        List<String> requiredMappers = Arrays.asList(
            "tenant_id",
            "clinic_name", 
            "clinic_type",
            "primary_tenant_id",
            "accessible_tenants",
            "active_tenant_id"
        );
        
        // Note: The actual implementation of protocol mapper creation
        // would require additional methods in KeycloakAdminService
        // For now, these need to be configured manually in Keycloak
        log.info("Protocol mappers need to be configured manually in Keycloak for attributes: {}", requiredMappers);
    }
    
    private void createRealmRoles(String realmName, String specialty) {
        // Create standard roles
        List<String> standardRoles = Arrays.asList("ADMIN", "DOCTOR", "STAFF", "RECEPTIONIST");
        
        // Add specialty-specific roles
        if (SpecialtyType.DENTAL.equals(specialty)) {
            standardRoles = new ArrayList<>(standardRoles);
            standardRoles.add("DENTIST");
            standardRoles.add("HYGIENIST");
        }
        
        for (String role : standardRoles) {
            try {
                // This would need to be implemented in KeycloakAdminService
                log.info("Creating role {} in realm {}", role, realmName);
                // keycloakAdminService.createRealmRole(realmName, role);
            } catch (Exception e) {
                log.warn("Role {} may already exist in realm {}: {}", role, realmName, e.getMessage());
            }
        }
    }
}