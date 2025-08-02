package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.dto.TenantAccessDto;
import sy.sezar.clinicx.tenant.dto.TenantSwitchResponseDto;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.service.TenantSwitchingService;

import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of tenant switching service.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TenantSwitchingServiceImpl implements TenantSwitchingService {
    
    private final StaffRepository staffRepository;
    private final TenantRepository tenantRepository;
    private final KeycloakAdminService keycloakAdminService;
    
    @Override
    public List<TenantAccessDto> getCurrentUserTenants() {
        String userId = getCurrentUserId();
        log.debug("Getting accessible tenants for user: {}", userId);
        
        List<Staff> staffList = staffRepository.findByUserId(userId);
        
        // If multiple staff records exist, sync them to Keycloak
        if (staffList.size() > 1) {
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                    String issuer = jwt.getIssuer().toString();
                    String realmName = extractRealmFromIssuer(issuer);
                    String username = jwt.getClaimAsString("preferred_username");
                    if (username != null) {
                        syncUserTenantsToKeycloak(userId, realmName, username);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to sync tenants to Keycloak during getCurrentUserTenants", e);
                // Continue with the operation
            }
        }
        
        return staffList.stream()
            .map(staff -> {
                Tenant tenant = tenantRepository.findByTenantId(staff.getTenantId())
                    .orElseThrow(() -> new NotFoundException("Tenant not found: " + staff.getTenantId()));
                
                return new TenantAccessDto(
                    tenant.getTenantId(),
                    tenant.getName(),
                    tenant.getSubdomain(),
                    staff.getRole().name(),
                    staff.isPrimary(),
                    tenant.isActive(),
                    tenant.getSpecialty() != null ? tenant.getSpecialty() : "CLINIC"
                );
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public TenantSwitchResponseDto switchTenant(String tenantId) {
        String userId = getCurrentUserId();
        log.info("User {} switching to tenant {}", userId, tenantId);
        
        // Verify user has access to the tenant
        Staff staff = staffRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new BusinessRuleException("You don't have access to tenant: " + tenantId));
        
        // Get tenant information
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + tenantId));
        
        if (!tenant.isActive()) {
            throw new BusinessRuleException("Tenant is not active: " + tenantId);
        }
        
        // Update active tenant in context
        TenantContext.setCurrentTenant(tenantId);
        
        // Update user attributes in Keycloak using the dedicated method
        try {
            // Get current JWT to find realm and username
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                String issuer = jwt.getIssuer().toString();
                String realmName = extractRealmFromIssuer(issuer);
                String username = jwt.getClaimAsString("preferred_username");
                if (username == null) {
                    username = userId; // Fallback to userId
                }
                
                // Use the dedicated method for updating active tenant
                keycloakAdminService.updateUserActiveTenant(realmName, username, tenantId);
            }
            
            // Note: In a real implementation, you would need to refresh the JWT token
            // to include the new active_tenant_id. This might require calling Keycloak's
            // token endpoint with a refresh token.
            
            return new TenantSwitchResponseDto(
                "current-access-token", // This should be the new access token
                "current-refresh-token", // This should be the new refresh token
                tenant.getTenantId(),
                tenant.getName(),
                staff.getRole().name(),
                "Successfully switched to tenant: " + tenant.getName()
            );
            
        } catch (Exception e) {
            log.error("Failed to switch tenant", e);
            throw new BusinessRuleException("Failed to switch tenant: " + e.getMessage());
        }
    }
    
    @Override
    public TenantAccessDto getCurrentActiveTenant() {
        String userId = getCurrentUserId();
        String currentTenantId = TenantContext.getCurrentTenant();
        
        if (currentTenantId == null) {
            // Try to get from JWT
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                currentTenantId = jwt.getClaimAsString("active_tenant_id");
                if (currentTenantId == null) {
                    currentTenantId = jwt.getClaimAsString("tenant_id");
                }
            }
        }
        
        if (currentTenantId == null) {
            // Fall back to primary tenant
            Staff primaryStaff = staffRepository.findByUserIdAndIsPrimaryTrue(userId)
                .orElseThrow(() -> new BusinessRuleException("No primary tenant found for user"));
            currentTenantId = primaryStaff.getTenantId();
        }
        
        final String tenantId = currentTenantId;
        Staff staff = staffRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new BusinessRuleException("No access to tenant: " + tenantId));
        
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + tenantId));
        
        return new TenantAccessDto(
            tenant.getTenantId(),
            tenant.getName(),
            tenant.getSubdomain(),
            staff.getRole().name(),
            staff.isPrimary(),
            tenant.isActive(),
            tenant.getSpecialty() != null ? tenant.getSpecialty() : "CLINIC"
        );
    }
    
    @Override
    public void grantUserTenantAccess(String userId, String tenantId, String role, boolean isPrimary) {
        log.info("Granting user {} access to tenant {} with role {}", userId, tenantId, role);
        
        // Verify tenant exists
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + tenantId));
        
        // Check if access already exists
        if (staffRepository.existsByUserIdAndTenantId(userId, tenantId)) {
            throw new BusinessRuleException("User already has access to this tenant");
        }
        
        // If setting as primary, unset other primary tenants
        if (isPrimary) {
            staffRepository.findByUserId(userId).forEach(staff -> {
                if (staff.isPrimary()) {
                    staff.setPrimary(false);
                    staffRepository.save(staff);
                }
            });
        }
        
        // Create new staff entry
        Staff staff = new Staff();
        staff.setUserId(userId);
        staff.setTenantId(tenantId);
        staff.setRole(StaffRole.valueOf(role));
        staff.setPrimary(isPrimary);
        staff.setActive(true);
        staff.setFullName("User " + userId); // This should be fetched from Keycloak
        staff.setEmail(userId + "@temp.com"); // This should be fetched from Keycloak
        
        staffRepository.save(staff);
        
        // Update user attributes in Keycloak using the dedicated method
        try {
            // Get realm from current authentication context or tenant configuration
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                String issuer = jwt.getIssuer().toString();
                String realmName = extractRealmFromIssuer(issuer);
                
                // Grant additional tenant access in Keycloak
                keycloakAdminService.grantAdditionalTenantAccess(
                    realmName,
                    userId, // This should be the username, not user ID
                    tenantId,
                    tenant.getName(),
                    tenant.getSpecialty(),
                    Arrays.asList(role)
                );
            }
        } catch (Exception e) {
            log.error("Failed to update Keycloak attributes", e);
            // Don't fail the operation, but log the error
        }
        
        log.info("Successfully granted access to tenant {}", tenantId);
    }
    
    @Override
    public void revokeUserTenantAccess(String userId, String tenantId) {
        log.info("Revoking user {} access to tenant {}", userId, tenantId);
        
        Staff staff = staffRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new NotFoundException("User tenant access not found"));
        
        if (staff.isPrimary()) {
            throw new BusinessRuleException("Cannot revoke access to primary tenant");
        }
        
        staffRepository.delete(staff);
        
        // Update Keycloak attributes using the dedicated method
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                String issuer = jwt.getIssuer().toString();
                String realmName = extractRealmFromIssuer(issuer);
                
                // Revoke tenant access in Keycloak
                // TODO: Implement revokeAdditionalTenantAccess in KeycloakAdminService
                log.warn("Keycloak tenant access revocation not implemented yet");
            }
        } catch (Exception e) {
            log.error("Failed to update Keycloak attributes", e);
            // Don't fail the operation, but log the error
        }
        
        log.info("Successfully revoked access to tenant {}", tenantId);
    }
    
    // This method was removed from the interface
    // Keeping implementation for future use if needed
    private void updateUserAccessibleTenants(String userId, List<String> accessibleTenants) {
        log.info("Updating accessible tenants for user {}: {}", userId, accessibleTenants);
        
        // Get realm from current authentication context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String issuer = jwt.getIssuer().toString();
            String realmName = extractRealmFromIssuer(issuer);
            
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("accessible_tenants", accessibleTenants);
            
            // Update user attributes in Keycloak
            keycloakAdminService.updateUserAttributes(realmName, userId, attributes);
        }
    }
    
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            // Try to get user ID from token
            String userId = jwt.getClaimAsString("sub");
            if (userId == null) {
                userId = jwt.getClaimAsString("preferred_username");
            }
            return userId;
        }
        throw new BusinessRuleException("No authenticated user found");
    }
    
    private String extractRealmFromIssuer(String issuer) {
        // Issuer format: http://localhost:8080/realms/clinic-123
        if (issuer == null || !issuer.contains("/realms/")) {
            throw new BusinessRuleException("Invalid issuer format");
        }
        
        int realmStart = issuer.indexOf("/realms/") + 8;
        int realmEnd = issuer.indexOf("/", realmStart);
        
        if (realmEnd == -1) {
            return issuer.substring(realmStart);
        } else {
            return issuer.substring(realmStart, realmEnd);
        }
    }
    
    @Override
    public List<TenantAccessDto> getUserTenants(String userId) {
        log.info("Getting all tenants for user {}", userId);
        
        // Get all staff records for this user
        List<Staff> staffRecords = staffRepository.findByUserId(userId);
        
        if (staffRecords.isEmpty()) {
            log.warn("No tenant access found for user {}", userId);
            return List.of();
        }
        
        // Convert to TenantAccessDto list
        return staffRecords.stream()
            .map(staff -> {
                Tenant tenant = tenantRepository.findByTenantId(staff.getTenantId())
                    .orElseThrow(() -> new NotFoundException("Tenant not found: " + staff.getTenantId()));
                
                return new TenantAccessDto(
                    tenant.getTenantId(),
                    tenant.getName(),
                    tenant.getSubdomain(),
                    staff.getRole().name(),
                    staff.isPrimary(),
                    false, // isActive - would need to check against current context
                    tenant.getSpecialty() != null ? tenant.getSpecialty() : "CLINIC"
                );
            })
            .toList();
    }
    
    @Override
    public void syncUserTenantsToKeycloak(String userId, String realmName, String username) {
        log.info("Syncing user {} tenants to Keycloak", userId);
        
        try {
            // Get all staff records for this user
            List<Staff> staffRecords = staffRepository.findByUserId(userId);
            
            if (staffRecords.isEmpty()) {
                log.warn("No staff records found for user {}", userId);
                return;
            }
            
            // Build accessible_tenants list
            List<Map<String, Object>> accessibleTenants = new ArrayList<>();
            Map<String, List<String>> userTenantRoles = new HashMap<>();
            
            for (Staff staff : staffRecords) {
                Tenant tenant = tenantRepository.findByTenantId(staff.getTenantId())
                    .orElseThrow(() -> new NotFoundException("Tenant not found: " + staff.getTenantId()));
                
                // Add to accessible tenants
                Map<String, Object> tenantAccess = new HashMap<>();
                tenantAccess.put("tenant_id", tenant.getTenantId());
                tenantAccess.put("clinic_name", tenant.getName());
                tenantAccess.put("clinic_type", tenant.getSpecialty() != null ? tenant.getSpecialty() : "CLINIC");
                tenantAccess.put("specialty", tenant.getSpecialty() != null ? tenant.getSpecialty() : "CLINIC");
                tenantAccess.put("roles", Arrays.asList(staff.getRole().name()));
                accessibleTenants.add(tenantAccess);
                
                // Add to user tenant roles
                userTenantRoles.put(tenant.getTenantId(), Arrays.asList(staff.getRole().name()));
            }
            
            // Find primary tenant
            Staff primaryStaff = staffRecords.stream()
                .filter(Staff::isPrimary)
                .findFirst()
                .orElse(staffRecords.get(0)); // Fallback to first if no primary
            
            // Update Keycloak user attributes
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("accessible_tenants", Arrays.asList(toJsonString(accessibleTenants)));
            attributes.put("user_tenant_roles", Arrays.asList(toJsonString(userTenantRoles)));
            attributes.put("tenant_id", Arrays.asList(primaryStaff.getTenantId()));
            attributes.put("active_tenant_id", Arrays.asList(primaryStaff.getTenantId()));
            
            keycloakAdminService.updateUserAttributes(realmName, username, attributes);
            log.info("Successfully synced user {} tenants to Keycloak", userId);
            
        } catch (Exception e) {
            log.error("Failed to sync user tenants to Keycloak", e);
            throw new BusinessRuleException("Failed to sync user tenants: " + e.getMessage());
        }
    }
    
    /**
     * Convert object to JSON string
     */
    private String toJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Failed to convert to JSON", e);
            return obj.toString();
        }
    }
}