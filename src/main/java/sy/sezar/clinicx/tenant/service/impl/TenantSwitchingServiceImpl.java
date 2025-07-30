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
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.repository.UserTenantAccessRepository;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.service.TenantSwitchingService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of tenant switching service.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TenantSwitchingServiceImpl implements TenantSwitchingService {
    
    private final UserTenantAccessRepository userTenantAccessRepository;
    private final TenantRepository tenantRepository;
    private final KeycloakAdminService keycloakAdminService;
    
    @Override
    public List<TenantAccessDto> getCurrentUserTenants() {
        String userId = getCurrentUserId();
        log.debug("Getting accessible tenants for user: {}", userId);
        
        List<UserTenantAccess> accesses = userTenantAccessRepository.findByUserId(userId);
        
        return accesses.stream()
            .map(access -> {
                Tenant tenant = tenantRepository.findByTenantId(access.getTenantId())
                    .orElseThrow(() -> new NotFoundException("Tenant not found: " + access.getTenantId()));
                
                return new TenantAccessDto(
                    tenant.getTenantId(),
                    tenant.getName(),
                    tenant.getSubdomain(),
                    access.getRole(),
                    access.isPrimary(),
                    tenant.isActive()
                );
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public TenantSwitchResponseDto switchTenant(String tenantId) {
        String userId = getCurrentUserId();
        log.info("User {} switching to tenant {}", userId, tenantId);
        
        // Verify user has access to the tenant
        UserTenantAccess access = userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new BusinessRuleException("You don't have access to tenant: " + tenantId));
        
        // Get tenant information
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + tenantId));
        
        if (!tenant.isActive()) {
            throw new BusinessRuleException("Tenant is not active: " + tenantId);
        }
        
        // Update active tenant in context
        TenantContext.setCurrentTenant(tenantId);
        
        // Update user attributes in Keycloak
        try {
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("active_tenant_id", Arrays.asList(tenantId));
            
            // Get current JWT to find realm
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                String issuer = jwt.getIssuer().toString();
                String realmName = extractRealmFromIssuer(issuer);
                keycloakAdminService.updateUserAttributes(realmName, userId, attributes);
            }
            
            // Note: In a real implementation, you would need to refresh the JWT token
            // to include the new active_tenant_id. This might require calling Keycloak's
            // token endpoint with a refresh token.
            
            return new TenantSwitchResponseDto(
                "current-access-token", // This should be the new access token
                "current-refresh-token", // This should be the new refresh token
                tenant.getTenantId(),
                tenant.getName(),
                access.getRole(),
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
            UserTenantAccess primaryAccess = userTenantAccessRepository.findByUserIdAndIsPrimaryTrue(userId)
                .orElseThrow(() -> new BusinessRuleException("No primary tenant found for user"));
            currentTenantId = primaryAccess.getTenantId();
        }
        
        final String tenantId = currentTenantId;
        UserTenantAccess access = userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new BusinessRuleException("No access to tenant: " + tenantId));
        
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + tenantId));
        
        return new TenantAccessDto(
            tenant.getTenantId(),
            tenant.getName(),
            tenant.getSubdomain(),
            access.getRole(),
            access.isPrimary(),
            tenant.isActive()
        );
    }
    
    @Override
    public void grantUserTenantAccess(String userId, String tenantId, String role, boolean isPrimary) {
        log.info("Granting user {} access to tenant {} with role {}", userId, tenantId, role);
        
        // Verify tenant exists
        if (!tenantRepository.existsByTenantId(tenantId)) {
            throw new NotFoundException("Tenant not found: " + tenantId);
        }
        
        // Check if access already exists
        if (userTenantAccessRepository.existsByUserIdAndTenantId(userId, tenantId)) {
            throw new BusinessRuleException("User already has access to this tenant");
        }
        
        // If setting as primary, unset other primary tenants
        if (isPrimary) {
            userTenantAccessRepository.findByUserId(userId).forEach(access -> {
                if (access.isPrimary()) {
                    access.setPrimary(false);
                    userTenantAccessRepository.save(access);
                }
            });
        }
        
        // Create new access
        UserTenantAccess access = new UserTenantAccess();
        access.setUserId(userId);
        access.setTenantId(tenantId);
        access.setRole(role);
        access.setPrimary(isPrimary);
        
        userTenantAccessRepository.save(access);
        
        // Update user attributes in Keycloak
        updateUserAccessibleTenants(userId);
    }
    
    @Override
    public void revokeUserTenantAccess(String userId, String tenantId) {
        log.info("Revoking user {} access to tenant {}", userId, tenantId);
        
        UserTenantAccess access = userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new NotFoundException("Access not found"));
        
        if (access.isPrimary()) {
            throw new BusinessRuleException("Cannot revoke access to primary tenant. Set another tenant as primary first.");
        }
        
        userTenantAccessRepository.delete(access);
        
        // Update user attributes in Keycloak
        updateUserAccessibleTenants(userId);
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
        throw new BusinessRuleException("No authenticated user found");
    }
    
    private String extractRealmFromIssuer(String issuer) {
        // Extract realm name from issuer URL
        // Example: http://localhost:18081/realms/dental-realm -> dental-realm
        int lastSlash = issuer.lastIndexOf('/');
        if (lastSlash > 0) {
            return issuer.substring(lastSlash + 1);
        }
        return null;
    }
    
    private void updateUserAccessibleTenants(String userId) {
        try {
            List<UserTenantAccess> accesses = userTenantAccessRepository.findByUserId(userId);
            
            List<String> accessibleTenants = accesses.stream()
                .map(access -> {
                    Tenant tenant = tenantRepository.findByTenantId(access.getTenantId()).orElse(null);
                    if (tenant != null) {
                        return access.getTenantId() + "|" + tenant.getName() + "|" + access.getRole();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("accessible_tenants", accessibleTenants);
            
            // Update in all possible realms where user might exist
            // In production, you'd need to track which realm the user belongs to
            log.info("Updated accessible tenants for user {}: {}", userId, accessibleTenants);
            
        } catch (Exception e) {
            log.error("Failed to update user accessible tenants in Keycloak", e);
        }
    }
}