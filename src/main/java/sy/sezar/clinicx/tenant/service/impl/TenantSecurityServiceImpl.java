package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.tenant.service.TenantAccessValidator;
import sy.sezar.clinicx.tenant.service.TenantSecurityService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of centralized tenant security service.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TenantSecurityServiceImpl implements TenantSecurityService {
    
    private final TenantAccessValidator tenantAccessValidator;
    private final StaffRepository staffRepository;
    
    // Role hierarchy definitions
    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
        "ADMIN", Set.of("ALL_PERMISSIONS"),
        "DOCTOR", Set.of("VIEW_PATIENTS", "CREATE_APPOINTMENTS", "UPDATE_MEDICAL_RECORDS", "VIEW_APPOINTMENTS"),
        "RECEPTIONIST", Set.of("VIEW_PATIENTS", "CREATE_APPOINTMENTS", "VIEW_APPOINTMENTS", "MANAGE_SCHEDULE"),
        "STAFF", Set.of("VIEW_APPOINTMENTS", "VIEW_SCHEDULE"),
        "DENTIST", Set.of("VIEW_PATIENTS", "CREATE_APPOINTMENTS", "UPDATE_DENTAL_RECORDS", "VIEW_APPOINTMENTS"),
        "HYGIENIST", Set.of("VIEW_PATIENTS", "UPDATE_HYGIENE_RECORDS", "VIEW_APPOINTMENTS")
    );
    
    @Override
    public boolean canPerformAction(String action) {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return false;
        }
        return canPerformActionInTenant(tenantId, action);
    }
    
    @Override
    public boolean canPerformActionInTenant(String tenantId, String action) {
        Set<String> permissions = getTenantPermissions(tenantId);
        return permissions.contains("ALL_PERMISSIONS") || permissions.contains(action);
    }
    
    @Override
    public Set<String> getCurrentTenantPermissions() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return Collections.emptySet();
        }
        return getTenantPermissions(tenantId);
    }
    
    @Override
    public Set<String> getTenantPermissions(String tenantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptySet();
        }
        
        // Check if user is super admin
        if (hasRole(authentication, "ROLE_SUPER_ADMIN")) {
            return Set.of("ALL_PERMISSIONS");
        }
        
        // Get user's role in the tenant
        String role = tenantAccessValidator.getUserRoleInTenant(tenantId);
        if (role == null) {
            return Collections.emptySet();
        }
        
        // Return permissions for the role
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }
    
    @Override
    public boolean isTenantAdmin() {
        String tenantId = TenantContext.getCurrentTenant();
        return tenantId != null && isTenantAdmin(tenantId);
    }
    
    @Override
    public boolean isTenantAdmin(String tenantId) {
        String role = tenantAccessValidator.getUserRoleInTenant(tenantId);
        return "ADMIN".equals(role);
    }
    
    @Override
    public List<String> getAccessibleTenants() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }
        
        String userId = extractUserId(authentication);
        if (userId == null) {
            return Collections.emptyList();
        }
        
        // Get all staff records for the user
        List<Staff> staffList = staffRepository.findByUserId(userId);
        
        return staffList.stream()
            .filter(Staff::isActive)
            .map(Staff::getTenantId)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean canAccessUserResource(String resourceOwnerId, String resourceType) {
        // Check if user is admin
        if (isTenantAdmin()) {
            return true;
        }
        
        // Check if user owns the resource
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = extractUserId(authentication);
        if (currentUserId != null && currentUserId.equals(resourceOwnerId)) {
            return true;
        }
        
        // Check specific permissions based on resource type
        switch (resourceType) {
            case "PATIENT_RECORD":
                return canPerformAction("VIEW_PATIENTS");
            case "APPOINTMENT":
                return canPerformAction("VIEW_APPOINTMENTS");
            case "MEDICAL_RECORD":
                return canPerformAction("VIEW_MEDICAL_RECORDS");
            default:
                return false;
        }
    }
    
    @Override
    public boolean canAssignRole(String userId, String tenantId, String role) {
        // Only admins can assign roles
        if (!isTenantAdmin(tenantId)) {
            return false;
        }
        
        // Validate that the role exists
        return ROLE_PERMISSIONS.containsKey(role);
    }
    
    @Override
    public String getPrimaryTenant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        // Try to get from JWT claim
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String primaryTenantId = jwt.getClaimAsString("primary_tenant_id");
            if (primaryTenantId != null) {
                return primaryTenantId;
            }
        }
        
        // Fallback to first accessible tenant
        List<String> tenants = getAccessibleTenants();
        return tenants.isEmpty() ? null : tenants.get(0);
    }
    
    @Override
    public boolean hasValidTenantContext() {
        String tenantId = TenantContext.getCurrentTenant();
        return tenantId != null && tenantAccessValidator.validateAccess(tenantId);
    }
    
    @Override
    public void enforceTenantAccess() {
        if (!hasValidTenantContext()) {
            throw new AccessDeniedException("No valid tenant context or access denied");
        }
    }
    
    @Override
    public void enforceTenantRole(String role) {
        enforceTenantAccess();
        
        String tenantId = TenantContext.getCurrentTenant();
        if (!tenantAccessValidator.validateRole(tenantId, role)) {
            throw new AccessDeniedException("Insufficient role privileges in tenant");
        }
    }
    
    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> authority.equals(role));
    }
    
    private String extractUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Try different claims for user ID
            String userId = jwt.getClaimAsString("sub");
            if (userId == null) {
                userId = jwt.getClaimAsString("user_id");
            }
            if (userId == null) {
                userId = jwt.getClaimAsString("preferred_username");
            }
            return userId;
        }
        
        return authentication.getName();
    }
}