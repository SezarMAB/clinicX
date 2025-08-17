package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.tenant.constants.TenantConstants;
import sy.sezar.clinicx.tenant.dto.TenantUserDto;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.service.UserAccessManagementService;
import sy.sezar.clinicx.tenant.service.UserMappingService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of UserMappingService.
 * Handles complex mapping between entities and DTOs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMappingServiceImpl implements UserMappingService {
    
    private final UserAccessManagementService userAccessManagementService;
    private final TenantRepository tenantRepository;
    
    @Override
    public TenantUserDto mapToDto(UserRepresentation keycloakUser, String tenantId, Staff staff) {
        return mapToDto(keycloakUser, tenantId, staff, null);
    }
    
    @Override
    public TenantUserDto mapToDto(UserRepresentation keycloakUser, String tenantId, UserTenantAccess access) {
        return mapToDto(keycloakUser, tenantId, null, access);
    }
    
    @Override
    public TenantUserDto mapToDto(UserRepresentation keycloakUser, String tenantId, 
                                 Staff staff, UserTenantAccess access) {
        if (keycloakUser == null) {
            return null;
        }
        
        // Extract basic information
        String userId = keycloakUser.getId();
        String username = keycloakUser.getUsername();
        String email = keycloakUser.getEmail();
        String firstName = keycloakUser.getFirstName();
        String lastName = keycloakUser.getLastName();
        boolean enabled = Boolean.TRUE.equals(keycloakUser.isEnabled());
        boolean emailVerified = Boolean.TRUE.equals(keycloakUser.isEmailVerified());
        
        // Get roles
        Set<StaffRole> staffRoles = new HashSet<>();
        if (access != null && access.getRoles() != null) {
            staffRoles = access.getRoles();
        } else if (staff != null && staff.getRoles() != null) {
            staffRoles = staff.getRoles();
        }
        List<String> roles = rolesToStringList(staffRoles);
        
        // Get tenant information
        String primaryTenantId = extractPrimaryTenantId(keycloakUser);
        String activeTenantId = tenantId;
        
        // Determine if external user
        boolean isExternal = !tenantId.equals(primaryTenantId);
        
        // Build accessible tenants
        List<TenantUserDto.TenantAccessInfo> accessibleTenants = buildAccessibleTenants(userId);
        
        // Get user attributes
        Map<String, List<String>> attributes = keycloakUser.getAttributes();
        
        // Extract timestamps
        Instant createdAt = extractTimestamp(keycloakUser.getCreatedTimestamp());
        Instant lastLogin = null; // Would need to be tracked separately
        
        // Determine user type
        StaffRole userType = determineUserType(keycloakUser, staffRoles);
        
        // Check if user is active
        boolean isUserActive = staff != null ? staff.isActive() : 
                              (access != null ? access.isActive() : true);
        
        // Get phone number
        String phoneNumber = staff != null ? staff.getPhoneNumber() : null;
        
        return new TenantUserDto(
            userId,
            username,
            email,
            firstName,
            lastName,
            enabled,
            emailVerified,
            roles,
            primaryTenantId,
            activeTenantId,
            isExternal,
            accessibleTenants,
            attributes,
            createdAt,
            lastLogin,
            userType,
            isUserActive,
            phoneNumber
        );
    }
    
    @Override
    public TenantUserDto createBasicDto(UserRepresentation keycloakUser, String tenantId) {
        return mapToDto(keycloakUser, tenantId, null, null);
    }
    
    @Override
    public List<TenantUserDto.TenantAccessInfo> buildAccessibleTenants(String userId) {
        List<TenantUserDto.TenantAccessInfo> accessibleTenants = new ArrayList<>();
        
        try {
            List<UserTenantAccess> accessList = userAccessManagementService.findActiveAccessByUserId(userId);
            
            for (UserTenantAccess access : accessList) {
                Optional<Tenant> tenantOpt = tenantRepository.findByTenantId(access.getTenantId());
                if (tenantOpt.isPresent()) {
                    Tenant tenant = tenantOpt.get();
                    List<String> roleNames = rolesToStringList(access.getRoles());
                    
                    accessibleTenants.add(new TenantUserDto.TenantAccessInfo(
                        tenant.getTenantId(),
                        tenant.getName(),
                        tenant.getSpecialty(),
                        roleNames,
                        access.isPrimary()
                    ));
                }
            }
        } catch (Exception e) {
            log.debug("Could not build accessible tenants for user {}: {}", userId, e.getMessage());
        }
        
        return accessibleTenants;
    }
    
    @Override
    public StaffRole determineUserType(UserRepresentation keycloakUser, Set<StaffRole> roles) {
        // Check if super admin
        if (isSuperAdmin(keycloakUser)) {
            return StaffRole.SUPER_ADMIN;
        }
        
        // Check if external user
        if (isExternalUser(keycloakUser)) {
            return StaffRole.EXTERNAL;
        }
        
        // Get primary role from roles set
        if (roles != null && !roles.isEmpty()) {
            return getPrimaryRole(roles);
        }
        
        // Default to INTERNAL
        return StaffRole.INTERNAL;
    }
    
    @Override
    public String getPrimaryRoleName(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return StaffRole.ASSISTANT.name();
        }
        
        StaffRole primaryRole = getPrimaryRole(roles);
        return primaryRole.name();
    }
    
    @Override
    public Set<StaffRole> mapToStaffRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of(StaffRole.ASSISTANT);
        }
        
        return roleNames.stream()
            .map(roleName -> {
                try {
                    return StaffRole.valueOf(roleName);
                } catch (IllegalArgumentException e) {
                    log.warn(TenantConstants.WARN_UNKNOWN_ROLE, roleName);
                    return StaffRole.ASSISTANT;
                }
            })
            .collect(Collectors.toSet());
    }
    
    @Override
    public List<String> rolesToStringList(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        
        return roles.stream()
            .map(StaffRole::name)
            .collect(Collectors.toList());
    }
    
    @Override
    public void enhanceWithStaffData(UserRepresentation user, Staff staff) {
        if (user == null || staff == null) {
            return;
        }
        
        // Ensure attributes map exists
        if (user.getAttributes() == null) {
            user.setAttributes(new HashMap<>());
        }
        
        // Add staff-related attributes
        Map<String, List<String>> attributes = user.getAttributes();
        attributes.put("staff_id", List.of(staff.getId().toString()));
        attributes.put("full_name", List.of(staff.getFullName()));
        
        if (staff.getPhoneNumber() != null) {
            attributes.put("phone_number", List.of(staff.getPhoneNumber()));
        }
        
        if (staff.getRoles() != null && !staff.getRoles().isEmpty()) {
            List<String> roleNames = rolesToStringList(staff.getRoles());
            attributes.put("staff_roles", roleNames);
        }
        
        attributes.put("is_active", List.of(String.valueOf(staff.isActive())));
    }
    
    @Override
    public void updateUserAttributes(UserRepresentation user, String tenantId, List<String> roles) {
        if (user == null) {
            return;
        }
        
        // Ensure attributes map exists
        if (user.getAttributes() == null) {
            user.setAttributes(new HashMap<>());
        }
        
        Map<String, List<String>> attributes = user.getAttributes();
        
        // Update tenant-related attributes
        if (tenantId != null) {
            attributes.put(TenantConstants.ATTR_ACTIVE_TENANT_ID, List.of(tenantId));
        }
        
        // Update roles
        if (roles != null && !roles.isEmpty()) {
            attributes.put(TenantConstants.ATTR_USER_TENANT_ROLES, roles);
        }
    }
    
    // Helper methods
    
    private String extractPrimaryTenantId(UserRepresentation user) {
        if (user.getAttributes() != null) {
            List<String> primaryRealm = user.getAttributes().get(TenantConstants.ATTR_PRIMARY_REALM);
            if (primaryRealm != null && !primaryRealm.isEmpty()) {
                return primaryRealm.get(0);
            }
        }
        return null;
    }
    
    private Instant extractTimestamp(Long timestamp) {
        if (timestamp != null) {
            return Instant.ofEpochMilli(timestamp);
        }
        return null;
    }
    
    private boolean isSuperAdmin(UserRepresentation user) {
        if (user.getRealmRoles() != null) {
            return user.getRealmRoles().contains("SUPER_ADMIN");
        }
        return false;
    }
    
    private boolean isExternalUser(UserRepresentation user) {
        if (user.getAttributes() != null) {
            List<String> userType = user.getAttributes().get("user_type");
            return userType != null && userType.contains("EXTERNAL");
        }
        return false;
    }
    
    private StaffRole getPrimaryRole(Set<StaffRole> roles) {
        // Return the role with highest hierarchy
        return roles.stream()
            .max(Comparator.comparing(StaffRole::getHierarchyLevel))
            .orElse(StaffRole.ASSISTANT);
    }
}