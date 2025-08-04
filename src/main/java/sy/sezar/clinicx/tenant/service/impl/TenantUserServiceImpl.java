package sy.sezar.clinicx.tenant.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.NotFoundException;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.service.TenantAuditService;
import sy.sezar.clinicx.tenant.service.TenantUserService;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.clinic.repository.StaffRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of TenantUserService for managing users within tenants.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantUserServiceImpl implements TenantUserService {
    
    private final KeycloakAdminService keycloakAdminService;
    private final TenantRepository tenantRepository;
    private final TenantAuditService auditService;
    private final ObjectMapper objectMapper;
    private final StaffRepository staffRepository;
    
    @Override
    public Page<TenantUserDto> getTenantUsers(String tenantId, boolean includeExternal, Pageable pageable) {
        log.info("Getting users for tenant {} (includeExternal: {})", tenantId, includeExternal);
        
        Tenant tenant = getTenant(tenantId);
        
        // Get Staff records for this tenant
        List<Staff> staffList = staffRepository.findByTenantId(tenantId);
        
        // Filter based on includeExternal flag
        if (!includeExternal) {
            staffList = staffList.stream()
                .filter(Staff::isPrimary)
                .collect(Collectors.toList());
        }
        
        // Get Keycloak users for each Staff record
        List<TenantUserDto> tenantUsers = new ArrayList<>();
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        for (Staff staff : staffList) {
            try {
                UserRepresentation user = realmResource.users().get(staff.getUserId()).toRepresentation();
                TenantUserDto dto = mapToDto(user, tenantId);
                // Enhance with Staff data
                // Note: For records, we cannot modify after creation. 
                // The dto already has the userId from the mapToDto method
                tenantUsers.add(dto);
            } catch (Exception e) {
                log.warn("Could not find Keycloak user for Staff record: {}", staff.getUserId());
            }
        }
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), tenantUsers.size());
        List<TenantUserDto> pageContent = tenantUsers.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, tenantUsers.size());
    }
    
    @Override
    public Page<TenantUserDto> searchUsers(String tenantId, String searchTerm, Pageable pageable) {
        log.info("Searching users in tenant {} with term: {}", tenantId, searchTerm);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        UsersResource usersResource = realmResource.users();
        
        // Search users by username, email, or name
        List<UserRepresentation> searchResults = usersResource.search(searchTerm);
        
        // Filter by tenant access
        List<TenantUserDto> tenantUsers = searchResults.stream()
            .filter(user -> hasAccessToTenant(user, tenantId))
            .map(user -> mapToDto(user, tenantId))
            .collect(Collectors.toList());
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), tenantUsers.size());
        List<TenantUserDto> pageContent = tenantUsers.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, tenantUsers.size());
    }
    
    @Override
    public TenantUserDto getUser(String tenantId, String userId) {
        log.info("Getting user {} in tenant {}", userId, tenantId);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }
            
            return mapToDto(user, tenantId);
        } catch (Exception e) {
            throw new NotFoundException("User not found: " + userId);
        }
    }
    
    @Override
    @Transactional
    public TenantUserDto createUser(String tenantId, TenantUserCreateRequest request) {
        log.info("Creating user {} in tenant {}", request.username(), tenantId);
        
        Tenant tenant = getTenant(tenantId);
        
        // Check if user already exists in Staff table with this email
        if (staffRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessRuleException("User with email " + request.email() + " already exists");
        }
        
        // Create user in Keycloak
        UserRepresentation user = keycloakAdminService.createUserWithTenantInfo(
            tenant.getRealmName(),
            request.username(),
            request.email(),
            request.firstName(),
            request.lastName(),
            request.password(),
            request.roles(),
            tenantId,
            tenant.getName(),
            tenant.getSpecialty()
        );
        
        // Create Staff record
        Staff staff = new Staff();
        staff.setUserId(user.getId());
        staff.setTenantId(tenantId);
        staff.setFullName(request.firstName() + " " + request.lastName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());
        staff.setRole(mapToStaffRole(request.roles()));
        staff.setActive(true);
        staff.setPrimary(true); // This is the primary tenant for the new user
        
        staffRepository.save(staff);
        log.info("Created Staff record for user {} with ID {}", request.username(), staff.getId());
        
        // Log the user creation
        // TODO: Implement audit logging
        // auditService.logUserCreation(tenantId, user.getId(), request.username());
        
        return mapToDto(user, tenantId);
    }
    
    @Override
    @Transactional
    public TenantUserDto updateUser(String tenantId, String userId, TenantUserUpdateRequest request) {
        log.info("Updating user {} in tenant {}", userId, tenantId);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }
            
            // Update user fields
            if (request.email() != null) {
                user.setEmail(request.email());
            }
            if (request.firstName() != null) {
                user.setFirstName(request.firstName());
            }
            if (request.lastName() != null) {
                user.setLastName(request.lastName());
            }
            if (request.enabled() != null) {
                user.setEnabled(request.enabled());
            }
            
            // Update attributes
            if (request.attributes() != null) {
                Map<String, List<String>> attributes = user.getAttributes();
                if (attributes == null) {
                    attributes = new HashMap<>();
                }
                for (Map.Entry<String, String> entry : request.attributes().entrySet()) {
                    attributes.put(entry.getKey(), Arrays.asList(entry.getValue()));
                }
                user.setAttributes(attributes);
            }
            
            // Update in Keycloak
            realmResource.users().get(userId).update(user);
            
            // Log the update
            // TODO: Implement audit logging
            // auditService.logUserUpdate(tenantId, userId, user.getUsername());
            
            return mapToDto(user, tenantId);
            
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to update user: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void deactivateUser(String tenantId, String userId) {
        log.info("Deactivating user {} in tenant {}", userId, tenantId);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }
            
            // Disable the user in Keycloak
            user.setEnabled(false);
            realmResource.users().get(userId).update(user);
            
            // Update Staff record
            Optional<Staff> staffOpt = staffRepository.findByUserIdAndTenantId(userId, tenantId);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                staff.setActive(false);
                staffRepository.save(staff);
                log.info("Deactivated Staff record for user {}", userId);
            }
            
            // Log the deactivation
            // TODO: Implement audit logging
            // auditService.logUserDeactivation(tenantId, userId, user.getUsername());
            
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to deactivate user: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void activateUser(String tenantId, String userId) {
        log.info("Activating user {} in tenant {}", userId, tenantId);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }
            
            // Enable the user in Keycloak
            user.setEnabled(true);
            realmResource.users().get(userId).update(user);
            
            // Update Staff record
            Optional<Staff> staffOpt = staffRepository.findByUserIdAndTenantId(userId, tenantId);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                staff.setActive(true);
                staffRepository.save(staff);
                log.info("Activated Staff record for user {}", userId);
            }
            
            // Log the activation
            // TODO: Implement audit logging
            // auditService.logUserActivation(tenantId, userId, user.getUsername());
            
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to activate user: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void deleteUser(String tenantId, String userId) {
        log.info("Deleting user {} from tenant {}", userId, tenantId);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            // Check if this is the user's primary tenant
            if (!isPrimaryTenant(user, tenantId)) {
                // If not primary tenant, just revoke access
                revokeExternalUserAccess(tenantId, userId);
                return;
            }
            
            // Delete Staff records for this user
            List<Staff> staffRecords = staffRepository.findByUserId(userId);
            if (!staffRecords.isEmpty()) {
                staffRepository.deleteAll(staffRecords);
                log.info("Deleted {} Staff records for user {}", staffRecords.size(), userId);
            }
            
            // Log before deletion
            // TODO: Implement audit logging
            // auditService.logUserDeletion(tenantId, userId, user.getUsername());
            
            // Delete the user from Keycloak
            realmResource.users().delete(userId);
            
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to delete user: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public TenantUserDto updateUserRoles(String tenantId, String userId, List<String> newRoles) {
        log.info("Updating roles for user {} in tenant {}: {}", userId, tenantId, newRoles);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }
            
            // Update user_tenant_roles attribute
            updateUserTenantRoles(user, tenantId, newRoles);
            
            // Update in Keycloak
            realmResource.users().get(userId).update(user);
            
            // Update realm roles
            updateRealmRoles(realmResource, userId, newRoles);
            
            // Update Staff record
            Optional<Staff> staffOpt = staffRepository.findByUserIdAndTenantId(userId, tenantId);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                staff.setRole(mapToStaffRole(newRoles));
                staffRepository.save(staff);
                log.info("Updated Staff role for user {}", userId);
            }
            
            // Log the role update
            // TODO: Implement audit logging
            // auditService.logRoleUpdate(tenantId, userId, user.getUsername(), newRoles);
            
            return mapToDto(user, tenantId);
            
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to update user roles: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public TenantUserDto grantExternalUserAccess(String tenantId, String username, List<String> roles) {
        log.info("Granting external user {} access to tenant {} with roles: {}", username, tenantId, roles);
        
        Tenant tenant = getTenant(tenantId);
        
        // Find the user in any realm
        UserRepresentation user = findUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found: " + username);
        }
        
        // Check if Staff record already exists for this user in this tenant
        Optional<Staff> existingStaff = staffRepository.findByUserIdAndTenantId(user.getId(), tenantId);
        if (existingStaff.isPresent()) {
            throw new BusinessRuleException("User already has access to this tenant");
        }
        
        // Grant additional tenant access in Keycloak
        keycloakAdminService.grantAdditionalTenantAccessByUserName(
            user.getAttributes().get("primary_realm").get(0), // User's primary realm
            username,
            tenantId,
            tenant.getName(),
            tenant.getSpecialty(),
            roles
        );
        
        // Create Staff record for external user in this tenant
        Staff staff = new Staff();
        staff.setUserId(user.getId());
        staff.setTenantId(tenantId);
        staff.setFullName(user.getFirstName() + " " + user.getLastName());
        staff.setEmail(user.getEmail());
        staff.setRole(mapToStaffRole(roles));
        staff.setActive(true);
        staff.setPrimary(false); // This is NOT the primary tenant for the external user
        
        staffRepository.save(staff);
        log.info("Created Staff record for external user {} in tenant {}", username, tenantId);
        
        // Log the access grant
        // TODO: Implement audit logging
        // auditService.logAccessGrant(tenantId, user.getId(), username, roles);
        
        return mapToDto(user, tenantId);
    }
    
    @Override
    @Transactional
    public void revokeExternalUserAccess(String tenantId, String userId) {
        log.info("Revoking external user {} access to tenant {}", userId, tenantId);
        
        // Find user's primary realm
        UserRepresentation user = findUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found: " + userId);
        }
        
        // Delete Staff record for this user in this tenant
        Optional<Staff> staffOpt = staffRepository.findByUserIdAndTenantId(userId, tenantId);
        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            if (staff.isPrimary()) {
                throw new BusinessRuleException("Cannot revoke access to primary tenant");
            }
            staffRepository.delete(staff);
            log.info("Deleted Staff record for external user {} in tenant {}", userId, tenantId);
        }
        
        // Revoke tenant access in Keycloak
        keycloakAdminService.revokeTenantAccess(
            user.getAttributes().get("primary_realm").get(0),
            user.getUsername(),
            tenantId
        );
        
        // Log the access revocation
        // TODO: Implement audit logging
        // auditService.logAccessRevocation(tenantId, userId, user.getUsername());
    }
    
    @Override
    public Page<UserActivityDto> getUserActivity(String tenantId, String userId, Pageable pageable) {
        log.info("Getting activity for user {} in tenant {}", userId, tenantId);
        
        // This would typically query an audit log table
        // For now, returning a placeholder implementation
        List<UserActivityDto> activities = new ArrayList<>();
        
        // Create some sample activities
        activities.add(new UserActivityDto(
            UUID.randomUUID().toString(),
            userId,
            "user",
            UserActivityDto.ActivityType.LOGIN,
            "User logged in",
            null, // ipAddress
            null, // userAgent
            Instant.now().minusSeconds(3600),
            tenantId,
            null, // details
            true
        ));
        
        return new PageImpl<>(activities, pageable, activities.size());
    }
    
    @Override
    @Transactional
    public void resetUserPassword(String tenantId, String userId, String newPassword, boolean temporary) {
        log.info("Resetting password for user {} in tenant {}", userId, tenantId);
        
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }
            
            // Set new password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(temporary);
            
            realmResource.users().get(userId).resetPassword(credential);
            
            // Log the password reset
            // TODO: Implement audit logging
            // auditService.logPasswordReset(tenantId, userId, user.getUsername());
            
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to reset password: " + e.getMessage());
        }
    }
    
    // Helper methods
    
    private Tenant getTenant(String tenantId) {
        return tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new NotFoundException("Tenant not found: " + tenantId));
    }
    
    private boolean hasAccessToTenant(UserRepresentation user, String tenantId) {
        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null) {
            return false;
        }
        
        // Check accessible_tenants attribute
        List<String> accessibleTenants = attributes.get("accessible_tenants");
        if (accessibleTenants != null && !accessibleTenants.isEmpty()) {
            try {
                String accessibleTenantsJson = accessibleTenants.get(0);
                List<Map<String, Object>> tenantList = objectMapper.readValue(
                    accessibleTenantsJson, 
                    new TypeReference<List<Map<String, Object>>>() {}
                );
                
                return tenantList.stream()
                    .anyMatch(t -> tenantId.equals(t.get("tenant_id")));
            } catch (Exception e) {
                log.error("Failed to parse accessible_tenants", e);
            }
        }
        
        // Fall back to checking tenant_id
        List<String> tenantIds = attributes.get("tenant_id");
        return tenantIds != null && tenantIds.contains(tenantId);
    }
    
    private boolean isPrimaryTenant(UserRepresentation user, String tenantId) {
        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null) {
            return false;
        }
        
        List<String> primaryTenantIds = attributes.get("tenant_id");
        return primaryTenantIds != null && primaryTenantIds.contains(tenantId);
    }
    
    private TenantUserDto mapToDto(UserRepresentation user, String tenantId) {
        Map<String, List<String>> attributes = user.getAttributes();
        
        String primaryTenantId = null;
        String activeTenantId = null;
        boolean isExternal = false;
        List<String> roles = new ArrayList<>();
        List<TenantUserDto.TenantAccessInfo> accessibleTenants = new ArrayList<>();
        
        if (attributes != null) {
            primaryTenantId = getFirstAttribute(attributes, "tenant_id");
            activeTenantId = getFirstAttribute(attributes, "active_tenant_id");
            isExternal = !isPrimaryTenant(user, tenantId);
            roles = getRolesForTenant(user, tenantId);
            accessibleTenants = parseAccessibleTenants(attributes);
        }
        
        // Determine user type
        TenantUserDto.UserType userType;
        if (roles.contains("SUPER_ADMIN")) {
            userType = TenantUserDto.UserType.SUPER_ADMIN;
        } else if (isExternal) {
            userType = TenantUserDto.UserType.EXTERNAL;
        } else {
            userType = TenantUserDto.UserType.INTERNAL;
        }
        
        return new TenantUserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.isEnabled(),
            user.isEmailVerified(),
            roles,
            primaryTenantId,
            activeTenantId,
            isExternal,
            accessibleTenants,
            attributes,
            user.getCreatedTimestamp() != null ? Instant.ofEpochMilli(user.getCreatedTimestamp()) : null,
            null, // lastLogin
            userType
        );
    }
    
    private String getFirstAttribute(Map<String, List<String>> attributes, String key) {
        List<String> values = attributes.get(key);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }
    
    private List<String> getRolesForTenant(UserRepresentation user, String tenantId) {
        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null) {
            return new ArrayList<>();
        }
        
        List<String> userTenantRoles = attributes.get("user_tenant_roles");
        if (userTenantRoles != null && !userTenantRoles.isEmpty()) {
            try {
                String rolesJson = userTenantRoles.get(0);
                Map<String, List<String>> rolesMap = objectMapper.readValue(
                    rolesJson, 
                    new TypeReference<Map<String, List<String>>>() {}
                );
                
                return rolesMap.getOrDefault(tenantId, new ArrayList<>());
            } catch (Exception e) {
                log.error("Failed to parse user_tenant_roles", e);
            }
        }
        
        return new ArrayList<>();
    }
    
    private List<TenantUserDto.TenantAccessInfo> parseAccessibleTenants(Map<String, List<String>> attributes) {
        List<String> accessibleTenants = attributes.get("accessible_tenants");
        if (accessibleTenants == null || accessibleTenants.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            String accessibleTenantsJson = accessibleTenants.get(0);
            List<Map<String, Object>> tenantList = objectMapper.readValue(
                accessibleTenantsJson, 
                new TypeReference<List<Map<String, Object>>>() {}
            );
            
            return tenantList.stream()
                .map(t -> new TenantUserDto.TenantAccessInfo(
                    (String) t.get("tenant_id"),
                    (String) t.get("clinic_name"),
                    (String) t.get("clinic_type"),
                    (List<String>) t.get("roles"),
                    false // Would need to check against primary tenant
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to parse accessible_tenants", e);
            return new ArrayList<>();
        }
    }
    
    private void updateUserTenantRoles(UserRepresentation user, String tenantId, List<String> newRoles) {
        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
            user.setAttributes(attributes);
        }
        
        try {
            // Get current user_tenant_roles
            List<String> userTenantRoles = attributes.get("user_tenant_roles");
            Map<String, List<String>> rolesMap;
            
            if (userTenantRoles != null && !userTenantRoles.isEmpty()) {
                rolesMap = objectMapper.readValue(
                    userTenantRoles.get(0), 
                    new TypeReference<Map<String, List<String>>>() {}
                );
            } else {
                rolesMap = new HashMap<>();
            }
            
            // Update roles for this tenant
            rolesMap.put(tenantId, newRoles);
            
            // Convert back to JSON
            String updatedRolesJson = objectMapper.writeValueAsString(rolesMap);
            attributes.put("user_tenant_roles", Arrays.asList(updatedRolesJson));
            
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to update user tenant roles: " + e.getMessage());
        }
    }
    
    private void updateRealmRoles(RealmResource realmResource, String userId, List<String> newRoles) {
        try {
            // Get all realm roles
            List<RoleRepresentation> allRoles = realmResource.roles().list();
            
            // Get current user roles
            List<RoleRepresentation> currentRoles = realmResource.users().get(userId)
                .roles().realmLevel().listEffective();
            
            // Remove all current roles
            realmResource.users().get(userId).roles().realmLevel().remove(currentRoles);
            
            // Add new roles
            List<RoleRepresentation> rolesToAdd = allRoles.stream()
                .filter(role -> newRoles.contains(role.getName()))
                .collect(Collectors.toList());
            
            realmResource.users().get(userId).roles().realmLevel().add(rolesToAdd);
            
        } catch (Exception e) {
            log.error("Failed to update realm roles", e);
            throw new BusinessRuleException("Failed to update user roles: " + e.getMessage());
        }
    }
    
    private UserRepresentation findUserByUsername(String username) {
        // Search across all realms
        // This is a simplified implementation - in production you might want to optimize this
        List<String> realmNames = keycloakAdminService.getKeycloakInstance().realms()
            .findAll().stream()
            .map(realm -> realm.getRealm())
            .collect(Collectors.toList());
        
        for (String realmName : realmNames) {
            try {
                List<UserRepresentation> users = keycloakAdminService.getKeycloakInstance()
                    .realm(realmName).users().search(username);
                
                if (!users.isEmpty()) {
                    UserRepresentation user = users.get(0);
                    // Add primary realm info
                    if (user.getAttributes() == null) {
                        user.setAttributes(new HashMap<>());
                    }
                    user.getAttributes().put("primary_realm", Arrays.asList(realmName));
                    return user;
                }
            } catch (Exception e) {
                log.debug("Error searching in realm {}: {}", realmName, e.getMessage());
            }
        }
        
        return null;
    }
    
    private UserRepresentation findUserById(String userId) {
        // Search across all realms
        List<String> realmNames = keycloakAdminService.getKeycloakInstance().realms()
            .findAll().stream()
            .map(realm -> realm.getRealm())
            .collect(Collectors.toList());
        
        for (String realmName : realmNames) {
            try {
                UserRepresentation user = keycloakAdminService.getKeycloakInstance()
                    .realm(realmName).users().get(userId).toRepresentation();
                
                if (user != null) {
                    // Add primary realm info
                    if (user.getAttributes() == null) {
                        user.setAttributes(new HashMap<>());
                    }
                    user.getAttributes().put("primary_realm", Arrays.asList(realmName));
                    return user;
                }
            } catch (Exception e) {
                log.debug("User not found in realm {}", realmName);
            }
        }
        
        return null;
    }
    
    private StaffRole mapToStaffRole(List<String> roles) {
        // Map the highest role to StaffRole enum
        if (roles.contains("SUPER_ADMIN")) {
            return StaffRole.SUPER_ADMIN;
        } else if (roles.contains("ADMIN")) {
            return StaffRole.ADMIN;
        } else if (roles.contains("DOCTOR")) {
            return StaffRole.DOCTOR;
        } else if (roles.contains("NURSE")) {
            return StaffRole.NURSE;
        } else if (roles.contains("RECEPTIONIST")) {
            return StaffRole.RECEPTIONIST;
        } else if (roles.contains("ACCOUNTANT")) {
            return StaffRole.ACCOUNTANT;
        } else if (roles.contains("ASSISTANT")) {
            return StaffRole.ASSISTANT;
        } else {
            // Default to ASSISTANT if no matching role
            return StaffRole.ASSISTANT;
        }
    }
}