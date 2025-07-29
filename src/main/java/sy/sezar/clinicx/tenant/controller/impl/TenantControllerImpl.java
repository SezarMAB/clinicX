package sy.sezar.clinicx.tenant.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import sy.sezar.clinicx.tenant.controller.api.TenantControllerApi;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.service.TenantService;

import java.util.UUID;

/**
 * Implementation of the Tenant Controller API.
 * Handles HTTP requests for tenant management operations.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class TenantControllerImpl implements TenantControllerApi {

    private final TenantService tenantService;

    @Override
    public ResponseEntity<TenantCreationResponseDto> createTenant(TenantCreateRequest request) {
        log.info("Creating new tenant with name: {}", request.name());
        TenantCreationResponseDto tenant = tenantService.createTenant(request);
        log.info("Successfully created tenant with ID: {}", tenant.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
    }

    @Override
    public ResponseEntity<Page<TenantSummaryDto>> getAllTenants(String searchTerm, Boolean isActive, Pageable pageable) {
        log.debug("Fetching tenants - searchTerm: {}, isActive: {}, page: {}, size: {}", 
            searchTerm, isActive, pageable.getPageNumber(), pageable.getPageSize());
        Page<TenantSummaryDto> tenants = tenantService.searchTenants(searchTerm, isActive, pageable);
        return ResponseEntity.ok(tenants);
    }

    @Override
    public ResponseEntity<TenantDetailDto> getTenantById(UUID id) {
        log.debug("Fetching tenant with ID: {}", id);
        TenantDetailDto tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }

    @Override
    public ResponseEntity<TenantDetailDto> getTenantBySubdomain(String subdomain) {
        log.debug("Fetching tenant with subdomain: {}", subdomain);
        TenantDetailDto tenant = tenantService.getTenantBySubdomain(subdomain);
        return ResponseEntity.ok(tenant);
    }

    @Override
    public ResponseEntity<TenantDetailDto> updateTenant(UUID id, TenantUpdateRequest request) {
        log.info("Updating tenant with ID: {}", id);
        TenantDetailDto updatedTenant = tenantService.updateTenant(id, request);
        log.info("Successfully updated tenant with ID: {}", id);
        return ResponseEntity.ok(updatedTenant);
    }

    @Override
    public ResponseEntity<Void> activateTenant(UUID id) {
        log.info("Activating tenant with ID: {}", id);
        tenantService.activateTenant(id);
        log.info("Successfully activated tenant with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deactivateTenant(UUID id) {
        log.info("Deactivating tenant with ID: {}", id);
        tenantService.deactivateTenant(id);
        log.info("Successfully deactivated tenant with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteTenant(UUID id) {
        log.warn("Deleting tenant with ID: {}", id);
        tenantService.deleteTenant(id);
        log.warn("Successfully deleted tenant with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SubdomainAvailabilityDto> checkSubdomainAvailability(String subdomain) {
        log.debug("Checking availability for subdomain: {}", subdomain);
        SubdomainAvailabilityDto availability = tenantService.checkSubdomainAvailability(subdomain);
        return ResponseEntity.ok(availability);
    }
}