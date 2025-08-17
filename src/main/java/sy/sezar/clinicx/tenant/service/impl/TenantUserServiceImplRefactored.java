package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.ResourceNotFoundException;
import sy.sezar.clinicx.tenant.constants.TenantConstants;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.exception.TenantAccessException;
import sy.sezar.clinicx.tenant.exception.UserManagementException;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Refactored implementation of TenantUserService using specialized services.
 * This implementation follows SOLID principles and clean architecture.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class TenantUserServiceImplRefactored implements TenantUserService {

    private final KeycloakUserService keycloakUserService;
    private final StaffManagementService staffManagementService;
    private final UserAccessManagementService userAccessManagementService;
    private final UserMappingService userMappingService;
    private final RoleManagementService roleManagementService;
    private final TenantRepository tenantRepository;
    private final TenantAuditService auditService;
    private final KeycloakAdminService keycloakAdminService;
    private final UserTenantAccessService userTenantAccessService;
    private final TenantAccessValidatorImpl tenantAccessValidator;

    @Override
    @Transactional(readOnly = true)
    public Page<TenantUserDto> getTenantUsers(String tenantId, boolean includeExternal, Pageable pageable) {
        log.info("Getting users for tenant {} (includeExternal: {})", tenantId, includeExternal);

        Tenant tenant = getTenant(tenantId);
        List<Staff> staffList = staffManagementService.findByTenantId(tenantId);

        // CRITICAL: Filter out inactive staff records (revoked users)
        staffList = staffList.stream()
            .filter(staff -> {
                if (!staff.isActive()) {
                    log.debug("Filtering out inactive staff record for user {} in tenant {}", 
                             staff.getKeycloakUserId(), tenantId);
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());

        // Filter based on includeExternal flag
        if (!includeExternal) {
            staffList = filterPrimaryUsers(staffList, tenantId);
        }

        // Map to DTOs
        List<TenantUserDto> tenantUsers = staffList.stream()
            .map(staff -> createUserDto(staff, tenant))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return createPage(tenantUsers, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TenantUserDto> searchUsers(String tenantId, String searchTerm, Pageable pageable) {
        log.info("Searching users in tenant {} with term: {}", tenantId, searchTerm);

        Tenant tenant = getTenant(tenantId);
        List<UserRepresentation> searchResults = keycloakUserService.searchUsers(
            tenant.getRealmName(), searchTerm);

        // Filter by tenant access and map to DTOs
        List<TenantUserDto> tenantUsers = searchResults.stream()
            .filter(user -> hasAccessToTenant(user.getId(), tenantId))
            .map(user -> createUserDtoFromKeycloak(user, tenantId))
            .collect(Collectors.toList());

        return createPage(tenantUsers, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantUserDto getUser(String tenantId, String userId) {
        log.info("Getting user {} in tenant {}", userId, tenantId);

        validateUserAccess(userId, tenantId);

        Tenant tenant = getTenant(tenantId);
        UserRepresentation user = keycloakUserService.getUser(tenant.getRealmName(), userId);

        return createUserDtoFromKeycloak(user, tenantId);
    }

    @Override
    @Transactional
    public TenantUserDto createUser(String tenantId, TenantUserCreateRequest request) {
        log.info("Creating user {} in tenant {}", request.username(), tenantId);

        // Validate request
        validateCreateUserRequest(request);

        Tenant tenant = getTenant(tenantId);
        Set<StaffRole> roles = roleManagementService.parseRoles(request.roles());

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

        // Create Staff record with source realm (same as tenant's realm for local users)
        Staff staff = staffManagementService.createStaff(
            tenantId,
            user.getId(),
            request.firstName() + " " + request.lastName(),
            request.email(),
            request.phoneNumber(),
            roles,
            tenant.getRealmName() // For local users, source realm is the tenant's realm
        );

        // Create access record
        userAccessManagementService.createAccess(
            user.getId(),
            tenantId,
            roles,
            true // Primary tenant for new users
        );

        // Audit user creation
        // TODO: Get admin username from security context
        auditService.auditUserAddedToTenant("system", user.getId(), tenantId,
            roleManagementService.rolesToStrings(roles).stream().findFirst().orElse("USER"));

        return userMappingService.mapToDto(user, tenantId, staff);
    }

    @Override
    @Transactional
    public TenantUserDto updateUser(String tenantId, String userId, TenantUserUpdateRequest request) {
        log.info("Updating user {} in tenant {}", userId, tenantId);

        validateUserAccess(userId, tenantId);

        Tenant tenant = getTenant(tenantId);
        UserRepresentation user = keycloakUserService.getUser(tenant.getRealmName(), userId);

        // Update Keycloak user
        updateKeycloakUser(user, request);
        keycloakUserService.updateUser(tenant.getRealmName(), userId, user);

        // Update Staff record if phone number changed
        if (request.phoneNumber() != null) {
            staffManagementService.updatePhoneNumber(userId, tenantId, request.phoneNumber());
        }

        // Audit user update
        auditService.auditTenantModified("system", tenantId, "Updated user: " + userId);

        return createUserDtoFromKeycloak(user, tenantId);
    }

    @Override
    @Transactional
    public void deactivateUser(String tenantId, String userId) {
        log.info("Deactivating user {} in tenant {}", userId, tenantId);

        validateUserAccess(userId, tenantId);

        // Deactivate Staff record
        staffManagementService.deactivateStaff(userId, tenantId);

        // Deactivate access
        try {
            userAccessManagementService.deactivateAccess(userId, tenantId);
        } catch (Exception e) {
            log.warn(TenantConstants.WARN_COULD_NOT_UPDATE_ACCESS, e.getMessage());
        }

        // Check if user should be deactivated in Keycloak
        if (shouldDeactivateInKeycloak(userId)) {
            Tenant tenant = getTenant(tenantId);
            keycloakUserService.setUserEnabled(tenant.getRealmName(), userId, false);
            log.info(TenantConstants.LOG_DEACTIVATED_IN_KEYCLOAK, userId);
        }
        
        // Evict cache to ensure immediate access revocation
        tenantAccessValidator.evictAccessCache(userId, tenantId);
        tenantAccessValidator.evictRoleCache(userId, tenantId);

        // Audit user deactivation
        auditService.auditTenantModified("system", tenantId, "Deactivated user: " + userId);
    }

    @Override
    @Transactional
    public void activateUser(String tenantId, String userId) {
        log.info("Activating user {} in tenant {}", userId, tenantId);

        validateUserExists(userId, tenantId);

        // Activate Staff record
        staffManagementService.activateStaff(userId, tenantId);

        // Activate access
        try {
            userAccessManagementService.activateAccess(userId, tenantId);
        } catch (Exception e) {
            log.warn("Could not activate user_tenant_access: {}", e.getMessage());
        }

        // Enable in Keycloak
        Tenant tenant = getTenant(tenantId);
        keycloakUserService.setUserEnabled(tenant.getRealmName(), userId, true);

        // Audit user activation
        auditService.auditTenantModified("system", tenantId, "Activated user: " + userId);
    }

    @Override
    @Transactional
    public void deleteUser(String tenantId, String userId) {
        log.info("Soft deleting user {} in all tenants", userId);

        // Deactivate all Staff records
        staffManagementService.deactivateAllStaffForUser(userId);

        // Deactivate all access records
        userAccessManagementService.deactivateAllAccessForUser(userId);

        // Deactivate in Keycloak (across all realms)
        UserRepresentation user = keycloakUserService.findUserById(userId);
        if (user != null) {
            List<String> realmNames = keycloakUserService.getAllRealmNames();
            for (String realmName : realmNames) {
                try {
                    keycloakUserService.setUserEnabled(realmName, userId, false);
                } catch (Exception e) {
                    log.debug("User not in realm {}", realmName);
                }
            }
        }

        // Audit user deletion
        auditService.auditUserRemovedFromTenant("system", userId, tenantId);
    }

    @Override
    @Transactional
    public TenantUserDto updateUserRoles(String tenantId, String userId, Set<StaffRole> newRoles) {
        log.info("Updating roles for user {} in tenant {}: {}", userId, tenantId, newRoles);

        validateUserAccess(userId, tenantId);
        validateRoleAssignment(newRoles);

        // Update Staff roles
        staffManagementService.updateStaffRoles(userId, tenantId, newRoles);

        // Update access roles
        try {
            userAccessManagementService.updateRoles(userId, tenantId, newRoles);
        } catch (Exception e) {
            log.warn("Could not update user_tenant_access roles: {}", e.getMessage());
        }

        // Update Keycloak roles
        Tenant tenant = getTenant(tenantId);
        List<String> roleNames = roleManagementService.rolesToStrings(newRoles);
        keycloakUserService.updateRealmRoles(tenant.getRealmName(), userId, roleNames);

        // Audit role update
        auditService.auditTenantModified("system", tenantId,
            "Updated roles for user " + userId + ": " + String.join(", ", roleNames));

        return getUser(tenantId, userId);
    }

    @Override
    @Transactional
    public TenantUserDto grantExternalUserAccess(String tenantId, String username, List<String> roles) {
        log.info("Granting external user {} access to tenant {} with roles: {}",
                username, tenantId, roles);

        // Find user across all realms
        UserRepresentation user = keycloakUserService.findUserByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + username);
        }

        // Find which realm the user belongs to
        String userRealm = keycloakUserService.findUserRealm(user.getId());
        if (userRealm == null) {
            log.warn("Could not determine source realm for user {}, using null", user.getId());
        } else {
            log.info("User {} found in realm {}", user.getId(), userRealm);
        }

        Set<StaffRole> staffRoles = roleManagementService.parseRoles(roles);

        // Create or reactivate Staff record with source realm tracking
        Staff staff = staffManagementService.createOrReactivateExternalStaff(
            tenantId,
            user.getId(),
            user.getFirstName() + " " + user.getLastName(),
            user.getEmail(),
            null, // Phone number not available from external user
            staffRoles,
            userRealm // Track the source realm
        );

        // Create or reactivate access
        userAccessManagementService.createOrReactivateAccess(
            user.getId(),
            tenantId,
            staffRoles,
            false // Not primary for external users
        );

        // Update user's Keycloak attributes to include the new tenant
        Tenant tenant = getTenant(tenantId);
        List<String> roleStrings = roleManagementService.rolesToStrings(staffRoles);

        // Update the user's accessible_tenants and user_tenant_roles in their source realm
        if (userRealm != null) {
            try {
              keycloakAdminService.grantAdditionalTenantAccessByUserName(
                    userRealm,
                    user.getUsername(),
                    tenantId,
                    tenant.getName(),
                    tenant.getSpecialty(),
                    roleStrings
                );
                log.info("Updated Keycloak attributes for user {} to include tenant {}", user.getId(), tenantId);
            } catch (Exception e) {
                log.warn("Could not update Keycloak attributes for external user {}: {}", user.getId(), e.getMessage());
                // Continue even if attribute update fails - the database records are created
            }
        }

        // Audit external user access
        auditService.auditUserAddedToTenant("system", user.getId(), tenantId,
            roleStrings.stream().findFirst().orElse("EXTERNAL"));

        return userMappingService.mapToDto(user, tenantId, staff);
    }

    @Override
    @Transactional
    public void revokeExternalUserAccess(String tenantId, String userId) {
        log.info("Revoking external user {} access to tenant {}", userId, tenantId);

        validateUserAccess(userId, tenantId);

        // Validate this is not the only tenant
        UserTenantAccess access = userAccessManagementService.getAccess(userId, tenantId);
        userAccessManagementService.validateRevocation(access);

        // Deactivate Staff record
        staffManagementService.deactivateStaff(userId, tenantId);

        // Deactivate access
        userAccessManagementService.deactivateAccess(userId, tenantId);
        
        // CRITICAL: Evict cache to ensure immediate access revocation
        tenantAccessValidator.evictAccessCache(userId, tenantId);
        tenantAccessValidator.evictRoleCache(userId, tenantId);
        log.info("Evicted access cache for user {} and tenant {} - access revoked immediately", userId, tenantId);

        // Remove tenant from user's Keycloak attributes
        String userRealm = keycloakUserService.findUserRealm(userId);
        if (userRealm != null) {
            try {
              keycloakAdminService.revokeTenantAccess(userRealm, userId, tenantId);
                log.info("Removed tenant {} from Keycloak attributes for user {}", tenantId, userId);
            } catch (Exception e) {
                log.warn("Could not remove tenant from Keycloak attributes for user {}: {}", userId, e.getMessage());
                // Continue even if attribute update fails - the database records are deactivated
            }
        }

        // Audit external user revocation
        auditService.auditUserRemovedFromTenant("system", userId, tenantId);
    }

    @Override
    public Page<UserActivityDto> getUserActivity(String tenantId, String userId, Pageable pageable) {
        log.info("Getting activity for user {} in tenant {}", userId, tenantId);

        validateUserAccess(userId, tenantId);

        // This would typically query an audit log or activity tracking service
        // For now, returning empty page
        return Page.empty(pageable);
    }

    @Override
    @Transactional
    public void resetUserPassword(String tenantId, String userId, String newPassword, boolean temporary) {
        log.info("Resetting password for user {} in tenant {}", userId, tenantId);

        validateUserAccess(userId, tenantId);

        Tenant tenant = getTenant(tenantId);
        keycloakUserService.resetPassword(tenant.getRealmName(), userId, newPassword, temporary);

        // Audit password reset
        auditService.auditTenantModified("system", tenantId, "Password reset for user: " + userId);
    }

    // Helper methods

    private Tenant getTenant(String tenantId) {
        return tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(TenantConstants.ERROR_TENANT_NOT_FOUND, tenantId)));
    }

    private List<Staff> filterPrimaryUsers(List<Staff> staffList, String tenantId) {
        return staffList.stream()
            .filter(staff -> {
                if (staff.getKeycloakUserId() != null) {
                    try {
                        Optional<UserTenantAccess> access = userAccessManagementService
                            .findByUserIdAndTenantId(staff.getKeycloakUserId(), tenantId);
                        return access.map(a -> a.isPrimary() && a.isActive())
                            .orElse(staff.isActive());
                    } catch (Exception e) {
                        return staff.isActive();
                    }
                }
                return staff.isActive();
            })
            .collect(Collectors.toList());
    }

    private TenantUserDto createUserDto(Staff staff, Tenant tenant) {
        try {
            UserRepresentation user;

            // If staff has a source realm, fetch user from that realm
            // This handles cross-realm access scenarios
            if (staff.getSourceRealm() != null && !staff.getSourceRealm().isEmpty()) {
                log.debug("Fetching user {} from source realm {}",
                         staff.getKeycloakUserId(), staff.getSourceRealm());
                user = keycloakUserService.getUser(staff.getSourceRealm(), staff.getKeycloakUserId());
            } else {
                // Fallback: try tenant's realm first, then search all realms
                try {
                    user = keycloakUserService.getUser(tenant.getRealmName(), staff.getKeycloakUserId());
                } catch (Exception tenantRealmEx) {
                    log.debug("User not in tenant realm, searching all realms for user {}",
                             staff.getKeycloakUserId());
                    user = keycloakUserService.getUserFromAnyRealm(staff.getKeycloakUserId());
                }
            }

            return userMappingService.mapToDto(user, tenant.getTenantId(), staff);
        } catch (Exception e) {
            log.warn(TenantConstants.LOG_USER_NOT_FOUND_IN_KEYCLOAK + " - {}",
                    staff.getKeycloakUserId(), e.getMessage());
            return null;
        }
    }

    private TenantUserDto createUserDtoFromKeycloak(UserRepresentation user, String tenantId) {
        Optional<Staff> staff = staffManagementService
            .findByKeycloakUserIdAndTenantId(user.getId(), tenantId);
        Optional<UserTenantAccess> access = userAccessManagementService
            .findByUserIdAndTenantId(user.getId(), tenantId);

        if (staff.isPresent() && access.isPresent()) {
            return userMappingService.mapToDto(user, tenantId, staff.get(), access.get());
        } else if (staff.isPresent()) {
            return userMappingService.mapToDto(user, tenantId, staff.get());
        } else if (access.isPresent()) {
            return userMappingService.mapToDto(user, tenantId, access.get());
        } else {
            return userMappingService.createBasicDto(user, tenantId);
        }
    }

    private boolean hasAccessToTenant(String userId, String tenantId) {
        return userAccessManagementService.hasActiveAccess(userId, tenantId) ||
               staffManagementService.existsByKeycloakUserIdAndTenantId(userId, tenantId);
    }

    private void validateUserAccess(String userId, String tenantId) {
        if (!hasAccessToTenant(userId, tenantId)) {
            throw TenantAccessException.noAccess(userId, tenantId);
        }
    }

    private void validateUserExists(String userId, String tenantId) {
        if (!staffManagementService.existsByKeycloakUserIdAndTenantId(userId, tenantId)) {
            throw new ResourceNotFoundException(TenantConstants.ERROR_USER_NOT_IN_TENANT);
        }
    }

    private void validateCreateUserRequest(TenantUserCreateRequest request) {
        if (staffManagementService.existsByEmail(request.email())) {
            throw UserManagementException.userAlreadyExists(request.email());
        }
    }

    private void validateRoleAssignment(Set<StaffRole> roles) {
        // TODO: Get current user's roles from security context
        // For now, just validate roles are not empty
        if (roles == null || roles.isEmpty()) {
            throw new BusinessRuleException("At least one role must be assigned");
        }
    }

    private void updateKeycloakUser(UserRepresentation user, TenantUserUpdateRequest request) {
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
    }

    private boolean shouldDeactivateInKeycloak(String userId) {
        // Check if user has any other active tenant access
        long activeAccessCount = userAccessManagementService.countActiveAccessForUser(userId);
        return activeAccessCount == 0;
    }

    private Page<TenantUserDto> createPage(List<TenantUserDto> users, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), users.size());

        if (start > users.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, users.size());
        }

        List<TenantUserDto> pageContent = users.subList(start, end);
        return new PageImpl<>(pageContent, pageable, users.size());
    }
}
