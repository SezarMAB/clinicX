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
import sy.sezar.clinicx.core.exception.ResourceNotFoundException;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.dto.CreateUserTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.UserTenantAccessDto;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;
import sy.sezar.clinicx.tenant.service.TenantAuditService;
import sy.sezar.clinicx.tenant.service.TenantUserService;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.tenant.service.UserTenantAccessService;

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
    private final UserTenantAccessService userTenantAccessService;

    @Override
    public Page<TenantUserDto> getTenantUsers(String tenantId, boolean includeExternal, Pageable pageable) {
        log.info("Getting users for tenant {} (includeExternal: {})", tenantId, includeExternal);

        Tenant tenant = getTenant(tenantId);

        // Get Staff records for this tenant
        List<Staff> staffList = staffRepository.findByTenantId(tenantId);

        // Filter based on includeExternal flag
        if (!includeExternal) {
            // Filter for primary users using user_tenant_access table
            staffList = staffList.stream()
                .filter(staff -> {
                    if (staff.getKeycloakUserId() != null) {
                        try {
                            UserTenantAccessDto access = userTenantAccessService.getAccess(
                                staff.getKeycloakUserId(),
                                tenantId
                            );
                            return access.isPrimary() && access.isActive();
                        } catch (Exception e) {
                            // If no access record, check staff status
                            return staff.isActive();
                        }
                    }
                    return staff.isActive();
                })
                .toList();
        }

        // Get Keycloak users for each Staff record
        List<TenantUserDto> tenantUsers = new ArrayList<>();
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());

        for (Staff staff : staffList) {
            try {
                UserRepresentation user = realmResource.users().get(staff.getKeycloakUserId()).toRepresentation();
                TenantUserDto dto = mapToDto(user, tenantId, staff);

                // Enhance with Staff data
                // Note: For records, we cannot modify after creation.
                // The dto already has the userId from the mapToDto method
                tenantUsers.add(dto);
            } catch (Exception e) {
                log.warn("Could not find Keycloak user for Staff record: {}", staff.getKeycloakUserId());
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
        List<Staff> staffList = staffRepository.findByTenantId(tenantId);
        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());
        UsersResource usersResource = realmResource.users();

        // Search users by username, email, or name
        List<UserRepresentation> searchResults = usersResource.search(searchTerm);

        // Filter by tenant access
        List<TenantUserDto> tenantUsers = searchResults.stream()
            .filter(user -> hasAccessToTenant(user, tenantId))
            .map(user -> mapToDto(user, tenantId,
                staffList.stream()
                    .filter(staff -> staff.getKeycloakUserId().equals(user.getId()))
                    .findFirst()
                    .orElse(null) // If no Staff record, use null
            ))
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
            Staff staff = staffRepository.findByKeycloakUserIdAndTenantId(userId, tenantId)
                .orElse(null); // If no Staff record, use null
            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }

            return mapToDto(user, tenantId,staff);
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
        staff.setKeycloakUserId(user.getId());
        staff.setTenantId(tenantId);
        staff.setFullName(request.firstName() + " " + request.lastName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());
        staff.setRoles(mapToStaffRoles(request.roles()));
        staff.setActive(true);

        staffRepository.save(staff);
        log.info("Created Staff record for user {} with ID {}", request.username(), staff.getId());

        // Create user_tenant_access record
        try {
            CreateUserTenantAccessRequest accessRequest = CreateUserTenantAccessRequest.builder()
                .userId(user.getId())
                .tenantId(tenantId)
                .roles(mapToStaffRoles(request.roles()))
                .isPrimary(true) // New users are primary by default
                .isActive(true)
                .build();

            UserTenantAccessDto accessDto = userTenantAccessService.grantAccess(accessRequest);
            log.info("Created user_tenant_access with ID {} for user {} in tenant {}",
                accessDto.id(), user.getId(), tenantId);
        } catch (Exception e) {
            log.error("Failed to create user_tenant_access record: {}", e.getMessage());
            throw new BusinessRuleException("Failed to create user access record: " + e.getMessage());
        }

        // Log the user creation
        // TODO: Implement audit logging
        // auditService.logUserCreation(tenantId, user.getId(), request.username());

        return mapToDto(user, tenantId,staff);
    }

    @Override
    @Transactional
    public TenantUserDto updateUser(String tenantId, String userId, TenantUserUpdateRequest request) {
        log.info("Updating user {} in tenant {}", userId, tenantId);

        int updatedRows = staffRepository
            .updatePhoneNumberByKeycloakUserIdAndTenantId(userId, tenantId, request.phoneNumber());

        Staff staff = null;
        if (updatedRows > 0) {
            staff =  staffRepository.findByKeycloakUserIdAndTenantId(userId, tenantId).get();
        }

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

            return mapToDto(user, tenantId, staff);

        } catch (Exception e) {
            throw new BusinessRuleException("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deactivateUser(String tenantId, String userId) {
        log.info("Deactivating user {} in tenant {}", userId, tenantId);

        UserTenantAccessDto access = userTenantAccessService.getAccess(userId, tenantId);

        if( access.roles().contains(StaffRole.ADMIN)) {
            log.info("Cannot deactivate primary tenant user {} in tenant {}", userId, tenantId);
            throw new BusinessRuleException("Cannot deactivate primary tenant user");
        }

        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());

        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();

            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }

            if(access.isPrimary()) {
                // Disable the user in Keycloak
                user.setEnabled(false);
                realmResource.users().get(userId).update(user);
            }

            // Update Staff record
            Optional<Staff> staffOpt = staffRepository.findByKeycloakUserIdAndTenantId(userId, tenantId);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                staff.setActive(false);
                staffRepository.save(staff);
                log.info("Deactivated Staff record for user {}", userId);
            }

            // Update user_tenant_access record
            try {
                userTenantAccessService.revokeAccess(userId, tenantId);
                log.info("Deactivated user_tenant_access for user {} in tenant {}", userId, tenantId);
            } catch (Exception e) {
                log.warn("Could not update user_tenant_access for deactivation: {}", e.getMessage());
            }

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
            Optional<Staff> staffOpt = staffRepository.findByKeycloakUserIdAndTenantId(userId, tenantId);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                staff.setActive(true);
                staffRepository.save(staff);
                log.info("Activated Staff record for user {}", userId);
            }

            // Reactivate or create user_tenant_access record
            try {
                // Try to reactivate existing access
                userTenantAccessService.reactivateAccess(userId, tenantId);
                log.info("Reactivated user_tenant_access for user {} in tenant {}", userId, tenantId);
            } catch (NotFoundException e) {
                // If not found, create new access
                CreateUserTenantAccessRequest accessRequest = CreateUserTenantAccessRequest.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .roles(staffOpt.map(Staff::getRoles).orElse(Set.of(StaffRole.ASSISTANT)))
                    .isPrimary(false)
                    .isActive(true)
                    .build();

                userTenantAccessService.grantAccess(accessRequest);
                log.info("Created new user_tenant_access for user {} in tenant {}", userId, tenantId);
            } catch (Exception e) {
                log.warn("Could not update user_tenant_access for activation: {}", e.getMessage());
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

            // Check if this is the user's primary tenant using user_tenant_access
            boolean isPrimary = false;
            try {
                UserTenantAccessDto access = userTenantAccessService.getAccess(userId, tenantId);
                isPrimary = access.isPrimary();
            } catch (Exception e) {
                // Fall back to old check if access not found
                isPrimary = isPrimaryTenant(user, tenantId);
            }

            if (!isPrimary) {
                // If not primary tenant, just revoke access
                revokeExternalUserAccess(tenantId, userId);
                return;
            }

            // Delete Staff records for this user
            List<Staff> staffRecords = staffRepository.findByKeycloakUserId(userId);
            if (!staffRecords.isEmpty()) {
                staffRepository.deleteAll(staffRecords);
                log.info("Deleted {} Staff records for user {}", staffRecords.size(), userId);
            }

            // Revoke all user_tenant_access records for this user
            try {
                userTenantAccessService.revokeAllAccess(userId);
                log.info("Revoked all tenant access for user {}", userId);
            } catch (Exception e) {
                log.warn("Could not revoke user_tenant_access records: {}", e.getMessage());
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
    public TenantUserDto updateUserRoles(String tenantId, String userId, Set<StaffRole> newRoles) {
        log.info("Updating roles for user {} in tenant {}: {}", userId, tenantId, newRoles);

        Tenant tenant = getTenant(tenantId);
        RealmResource realmResource = keycloakAdminService.getKeycloakInstance().realm(tenant.getRealmName());

        // Convert Set<StaffRole> to List<String> for helper methods
        List<String> newRoleStrings = newRoles.stream()
                .map(StaffRole::getRole)
                .toList();

        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();

            // Verify user has access to this tenant
            if (!hasAccessToTenant(user, tenantId)) {
                throw new NotFoundException("User not found in this tenant");
            }

            // Update user_tenant_roles attribute
            updateUserTenantRoles(user, tenantId, newRoleStrings);

            // Update in Keycloak
            realmResource.users().get(userId).update(user);

            // Update realm roles
            updateRealmRoles(realmResource, userId, newRoleStrings);

            // Update Staff record
            Optional<Staff> staffOpt = staffRepository.findByKeycloakUserIdAndTenantId(userId, tenantId);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                staff.setRoles(newRoles);
                staffRepository.save(staff);
                log.info("Updated Staff role for user {}", userId);
            }

            // Update user_tenant_access roles
            try {
                userTenantAccessService.updateAccessRoles(userId, tenantId, newRoles);
                log.info("Updated user_tenant_access roles for user {} in tenant {}", userId, tenantId);
            } catch (ResourceNotFoundException e) {
                // If no user_tenant_access record exists, create one
                log.info("Creating new user_tenant_access record for user {} in tenant {}", userId, tenantId);
                CreateUserTenantAccessRequest accessRequest = CreateUserTenantAccessRequest.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .roles(newRoles)
                    .isPrimary(false)
                    .isActive(true)
                    .build();
                userTenantAccessService.grantAccess(accessRequest);
                log.info("Created user_tenant_access record for user {} in tenant {}", userId, tenantId);
            }

            // Log the role update
            // TODO: Implement audit logging
            // auditService.logRoleUpdate(tenantId, userId, user.getUsername(), newRoles);

            return mapToDto(user, tenantId, staffOpt.get());

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
        Optional<Staff> existingStaff = staffRepository.findByKeycloakUserIdAndTenantId(user.getId(), tenantId);
        if (existingStaff.isPresent()) {
            // Check if it's just inactive, then reactivate it
            Staff staff = existingStaff.get();
            if (!staff.isActive()) {
                staff.setActive(true);
                staff.setRoles(mapToStaffRoles(roles));
                staffRepository.save(staff);

                // Reactivate or create user_tenant_access
                try {
                    userTenantAccessService.reactivateAccess(user.getId(), tenantId);
                } catch (Exception e) {
                    // If not found, create new
                    CreateUserTenantAccessRequest accessRequest = CreateUserTenantAccessRequest.builder()
                        .userId(user.getId())
                        .tenantId(tenantId)
                        .roles(mapToStaffRoles(roles))
                        .isPrimary(false)
                        .isActive(true)
                        .build();
                    userTenantAccessService.grantAccess(accessRequest);
                }

                log.info("Reactivated existing access for user {} in tenant {}", username, tenantId);
                return mapToDto(user, tenantId , staff);
            }
            throw new BusinessRuleException("User already has active access to this tenant");
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
        staff.setKeycloakUserId(user.getId());
        staff.setTenantId(tenantId);
        staff.setFullName(user.getFirstName() + " " + user.getLastName());
        staff.setEmail(user.getEmail());
        //TODO set the phone number if available
        staff.setRoles(mapToStaffRoles(roles));
        staff.setActive(true);

        Staff savedStaff = staffRepository.save(staff);
        log.info("Created Staff record for external user {} in tenant {}", username, tenantId);

        // Create user_tenant_access record for external user
        try {
            CreateUserTenantAccessRequest accessRequest = CreateUserTenantAccessRequest.builder()
                .userId(user.getId())
                .tenantId(tenantId)
                .roles(mapToStaffRoles(roles))
                .isPrimary(false) // External users are never primary
                .isActive(true)
                .build();

            UserTenantAccessDto accessDto = userTenantAccessService.grantAccess(accessRequest);
            log.info("Created user_tenant_access for external user {} in tenant {}", username, tenantId);
        } catch (Exception e) {
            log.error("Failed to create user_tenant_access for external user: {}", e.getMessage());
            throw new BusinessRuleException("Failed to create access record: " + e.getMessage());
        }

        // Log the access grant
        // TODO: Implement audit logging
        // auditService.logAccessGrant(tenantId, user.getId(), username, roles);

        return mapToDto(user, tenantId, savedStaff);
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

        // Check if this is primary tenant using user_tenant_access
        try {
            UserTenantAccessDto access = userTenantAccessService.getAccess(userId, tenantId);
            if (access.isPrimary()) {
                throw new BusinessRuleException("Cannot revoke access to primary tenant");
            }
        } catch (NotFoundException e) {
            // If no access record, check using Staff records
            List<Staff> allUserStaff = staffRepository.findByKeycloakUserId(userId);
            if (allUserStaff.size() <= 1) {
                throw new BusinessRuleException("Cannot revoke access to the only tenant");
            }
        }

        // Deactivate Staff record for this user in this tenant (soft delete)
        Optional<Staff> staffOpt = staffRepository.findByKeycloakUserIdAndTenantId(userId, tenantId);
        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            staff.setActive(false);
            staffRepository.save(staff);
            log.info("Deactivated Staff record for external user {} in tenant {}", userId, tenantId);
        }

        // Revoke user_tenant_access
        try {
            userTenantAccessService.revokeAccess(userId, tenantId);
            log.info("Revoked user_tenant_access for user {} in tenant {}", userId, tenantId);
        } catch (Exception e) {
            log.warn("Could not revoke user_tenant_access: {}", e.getMessage());
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
        // First try to check using user_tenant_access
        try {
            UserTenantAccessDto access = userTenantAccessService.getAccess(user.getId(), tenantId);
            return access.isPrimary();
        } catch (Exception e) {
            // Fall back to attribute check
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                return false;
            }

            List<String> primaryTenantIds = attributes.get("tenant_id");
            return primaryTenantIds != null && primaryTenantIds.contains(tenantId);
        }
    }

    private TenantUserDto mapToDto(UserRepresentation user, String tenantId, Staff staff) {
        Map<String, List<String>> attributes = user.getAttributes();

        String primaryTenantId = null;
        String activeTenantId = null;
        boolean isExternal = false;
        List<String> roles = new ArrayList<>();
        List<TenantUserDto.TenantAccessInfo> accessibleTenants = new ArrayList<>();

        if (attributes != null) {
            primaryTenantId = getFirstAttribute(attributes, "tenant_id");
            activeTenantId = getFirstAttribute(attributes, "active_tenant_id");

            // Check if external using user_tenant_access
            try {
                UserTenantAccessDto access = userTenantAccessService.getAccess(user.getId(), tenantId);
                isExternal = !access.isPrimary();
                // Use roles from user_tenant_access if available
                if (access.roles() != null && !access.roles().isEmpty()) {
                    roles = access.roles().stream().map(StaffRole::name).collect(Collectors.toList());
                } else {
                    roles = getRolesForTenant(user, tenantId);
                }
            } catch (Exception e) {
                // Fall back to attribute check
                isExternal = !isPrimaryTenant(user, tenantId);
                roles = getRolesForTenant(user, tenantId);
            }

            accessibleTenants = parseAccessibleTenants(attributes);
        }

        // Determine user type
        StaffRole userType;
        if (roles.contains("SUPER_ADMIN")) {
            userType = StaffRole.SUPER_ADMIN;
        } else if (isExternal) {
            userType = StaffRole.EXTERNAL;
        } else {
            userType = StaffRole.INTERNAL;
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
            userType,
            staff.isActive(),
            staff.getPhoneNumber()
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

    /**
     * Maps a list of role strings to a Set of StaffRole enums
     */
    private Set<StaffRole> mapToStaffRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(StaffRole.ASSISTANT);
        }

        return roles.stream()
            .map(roleName -> {
                try {
                    return StaffRole.valueOf(roleName);
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown role: {}, defaulting to ASSISTANT", roleName);
                    return StaffRole.ASSISTANT;
                }
            })
            .collect(Collectors.toSet());
    }

    /**
     * Gets the primary role name from a set of roles
     * Priority: SUPER_ADMIN > ADMIN > DOCTOR > NURSE > RECEPTIONIST > ACCOUNTANT > ASSISTANT > STAFF
     */
    private String getPrimaryRoleName(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return StaffRole.ASSISTANT.name();
        }

        // Priority order based on hierarchy
        if (roles.contains(StaffRole.SUPER_ADMIN)) return StaffRole.SUPER_ADMIN.name();
        if (roles.contains(StaffRole.ADMIN)) return StaffRole.ADMIN.name();
        if (roles.contains(StaffRole.DOCTOR)) return StaffRole.DOCTOR.name();
        if (roles.contains(StaffRole.NURSE)) return StaffRole.NURSE.name();
        if (roles.contains(StaffRole.RECEPTIONIST)) return StaffRole.RECEPTIONIST.name();
        if (roles.contains(StaffRole.ACCOUNTANT)) return StaffRole.ACCOUNTANT.name();
        if (roles.contains(StaffRole.ASSISTANT)) return StaffRole.ASSISTANT.name();

        // Return the first role if none of the standard ones are found
        return roles.iterator().next().name();
    }
}
