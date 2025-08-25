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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Copy clients from template realm or master realm
        String templateRealm = mappingConfig.getTemplateRealm() != null ?
            mappingConfig.getTemplateRealm() : "master";

        try {
            log.info("Copying clients from template realm {} to {}", templateRealm, realmName);
            keycloakAdminService.copyClientsFromRealm(templateRealm, realmName);
        } catch (Exception e) {
            log.error("Failed to copy clients from template realm: {}", e.getMessage());
            // Continue with manual client creation as fallback
        }

        // Ensure protocol mappers are configured for both clients
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
//        Map<String, String> mapperAttributes = new HashMap<>();
//        mapperAttributes.put("tenant_id", "tenant_id");
//        mapperAttributes.put("clinic_name", "clinic_name");
//        mapperAttributes.put("clinic_type", "clinic_type");
//        mapperAttributes.put("active_tenant_id", "active_tenant_id");
//        mapperAttributes.put("accessible_tenants", "accessible_tenants");
//        mapperAttributes.put("user_tenant_roles", "user_tenant_roles");
//        mapperAttributes.put("specialty", "clinic_type"); // Map clinic_type to specialty claim
//
//        // Ensure each mapper exists
//        for (Map.Entry<String, String> entry : mapperAttributes.entrySet()) {
//            String mapperName = entry.getKey() + "-mapper";
//            String attributeName = entry.getValue();
//
//            try {
//                log.debug("Ensuring protocol mapper {} for attribute {}", mapperName, attributeName);
//                keycloakAdminService.ensureProtocolMapper(realmName, clientId, mapperName, attributeName);
//            } catch (Exception e) {
//                log.error("Failed to ensure protocol mapper {} for client {}: {}",
//                    mapperName, clientId, e.getMessage());
//            }
//        }
//
//        log.info("Completed protocol mapper configuration for client {} in realm {}", clientId, realmName);
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
                log.info("Creating role {} in realm {}", role, realmName);
                keycloakAdminService.createRealmRole(realmName, role, "Role for " + role.toLowerCase());
            } catch (Exception e) {
                log.warn("Role {} may already exist in realm {}: {}", role, realmName, e.getMessage());
            }
        }
    }
}
