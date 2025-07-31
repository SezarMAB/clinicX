package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of tenant access validation service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantAccessValidatorImpl implements TenantAccessValidator {
    
    private final StaffRepository staffRepository;
    
    @Override
    public boolean validateAccess(String tenantId) {
        if (tenantId == null) {
            return false;
        }
        
        String userId = getCurrentUserId();
        if (userId == null) {
            return false;
        }
        
        return validateUserAccess(userId, tenantId);
    }
    
    @Override
    public boolean validateCurrentTenantAccess() {
        String currentTenant = TenantContext.getCurrentTenant();
        if (currentTenant == null) {
            log.warn("No tenant context set");
            return false;
        }
        
        return validateAccess(currentTenant);
    }
    
    @Override
    public boolean validateUserAccess(String userId, String tenantId) {
        // First check JWT claims for accessible tenants
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            // Check if tenant_id matches (single tenant user)
            String jwtTenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId.equals(jwtTenantId)) {
                return true;
            }
            
            // Check accessible_tenants claim (multi-tenant user)
            List<String> accessibleTenants = jwt.getClaimAsStringList("accessible_tenants");
            if (accessibleTenants != null) {
                for (String tenantAccess : accessibleTenants) {
                    // Format: tenantId|tenantName|role
                    String[] parts = tenantAccess.split("\\|");
                    if (parts.length >= 1 && parts[0].equals(tenantId)) {
                        return true;
                    }
                }
            }
        }
        
        // Fall back to database check
        return staffRepository.existsByUserIdAndTenantId(userId, tenantId);
    }
    
    @Override
    public boolean validateRole(String tenantId, String role) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return false;
        }
        
        String userRole = getUserRoleInTenant(tenantId);
        return role.equals(userRole);
    }
    
    @Override
    public String getUserRoleInTenant(String tenantId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }
        
        // First check JWT claims
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            // For single tenant users with matching tenant
            String jwtTenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId.equals(jwtTenantId)) {
                // Get role from realm_access
                Object realmAccess = jwt.getClaim("realm_access");
                if (realmAccess instanceof java.util.Map) {
                    java.util.Map<String, Object> realmAccessMap = (java.util.Map<String, Object>) realmAccess;
                    List<String> roles = (List<String>) realmAccessMap.get("roles");
                    if (roles != null && !roles.isEmpty()) {
                        // Return the highest priority role
                        if (roles.contains("ADMIN")) return "ADMIN";
                        if (roles.contains("DOCTOR")) return "DOCTOR";
                        if (roles.contains("STAFF")) return "STAFF";
                        return roles.get(0);
                    }
                }
            }
            
            // Check accessible_tenants for multi-tenant users
            List<String> accessibleTenants = jwt.getClaimAsStringList("accessible_tenants");
            if (accessibleTenants != null) {
                for (String tenantAccess : accessibleTenants) {
                    // Format: tenantId|tenantName|role
                    String[] parts = tenantAccess.split("\\|");
                    if (parts.length >= 3 && parts[0].equals(tenantId)) {
                        return parts[2];
                    }
                }
            }
        }
        
        // Fall back to database
        Optional<Staff> staff = staffRepository.findByUserIdAndTenantId(userId, tenantId);
        return staff.map(s -> s.getRole().name()).orElse(null);
    }
    
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            // Try different claims that might contain the username
            String userId = jwt.getClaimAsString("preferred_username");
            if (userId == null) {
                userId = jwt.getClaimAsString("sub");
            }
            if (userId == null) {
                userId = jwt.getClaimAsString("email");
            }
            return userId;
        }
        return null;
    }
}