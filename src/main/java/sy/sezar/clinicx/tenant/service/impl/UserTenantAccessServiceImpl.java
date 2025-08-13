package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.core.exception.ResourceNotFoundException;
import sy.sezar.clinicx.core.exception.ConflictException;
import sy.sezar.clinicx.tenant.dto.CreateUserTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.UpdateUserTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.UserTenantAccessDto;
import sy.sezar.clinicx.tenant.mapper.UserTenantAccessMapper;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.UserTenantAccessRepository;
import sy.sezar.clinicx.tenant.repository.TenantRepository;
import sy.sezar.clinicx.tenant.service.UserTenantAccessService;
import sy.sezar.clinicx.tenant.service.KeycloakAdminService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTenantAccessServiceImpl implements UserTenantAccessService {

    private final UserTenantAccessRepository userTenantAccessRepository;
    private final TenantRepository tenantRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final UserTenantAccessMapper mapper;

    @Override
    @Transactional
    public UserTenantAccessDto grantAccess(CreateUserTenantAccessRequest request) {
        log.info("Granting access for user {} to tenant {}", request.getUserId(), request.getTenantId());
        
        // Check if tenant exists
        if (!tenantRepository.existsByTenantId(request.getTenantId())) {
            throw new ResourceNotFoundException("Tenant not found: " + request.getTenantId());
        }
        
        // Check if access already exists
        if (userTenantAccessRepository.existsByUserIdAndTenantIdAndIsActiveTrue(
                request.getUserId(), request.getTenantId())) {
            throw new ConflictException("User already has active access to this tenant");
        }
        
        // If setting as primary, unset other primary tenants for this user
        if (request.isPrimary()) {
            userTenantAccessRepository.findByUserId(request.getUserId())
                .stream()
                .filter(UserTenantAccess::isPrimary)
                .forEach(access -> {
                    access.setPrimary(false);
                    userTenantAccessRepository.save(access);
                });
        }
        
        UserTenantAccess access = UserTenantAccess.builder()
            .userId(request.getUserId())
            .tenantId(request.getTenantId())
            .role(request.getRole())
            .isPrimary(request.isPrimary())
            .isActive(request.isActive())
            .build();
        
        access = userTenantAccessRepository.save(access);
        log.info("Access granted successfully with ID: {}", access.getId());
        
        return mapper.toDto(access);
    }

    @Override
    @Transactional
    public UserTenantAccessDto updateAccess(UUID id, UpdateUserTenantAccessRequest request) {
        log.info("Updating access with ID: {}", id);
        
        UserTenantAccess access = userTenantAccessRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Access not found: " + id));
        
        if (request.getRole() != null) {
            access.setRole(request.getRole());
        }
        
        if (request.getIsPrimary() != null) {
            if (request.getIsPrimary() && !access.isPrimary()) {
                // Unset other primary tenants for this user
                userTenantAccessRepository.findByUserId(access.getUserId())
                    .stream()
                    .filter(a -> !a.getId().equals(id) && a.isPrimary())
                    .forEach(a -> {
                        a.setPrimary(false);
                        userTenantAccessRepository.save(a);
                    });
            }
            access.setPrimary(request.getIsPrimary());
        }
        
        if (request.getIsActive() != null) {
            access.setActive(request.getIsActive());
        }
        
        access = userTenantAccessRepository.save(access);
        log.info("Access updated successfully");
        
        return mapper.toDto(access);
    }

    @Override
    @Transactional
    public void revokeAccess(String userId, String tenantId) {
        log.info("Revoking access for user {} from tenant {}", userId, tenantId);
        
        UserTenantAccess access = userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Access not found"));
        
        access.setActive(false);
        userTenantAccessRepository.save(access);
        
        log.info("Access revoked successfully");
    }

    @Override
    @Transactional
    public void revokeAccessById(UUID id) {
        log.info("Revoking access with ID: {}", id);
        
        UserTenantAccess access = userTenantAccessRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Access not found: " + id));
        
        access.setActive(false);
        userTenantAccessRepository.save(access);
        
        log.info("Access revoked successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public UserTenantAccessDto getAccess(String userId, String tenantId) {
        return userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .map(mapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Access not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTenantAccessDto> getUserAccesses(String userId) {
        return userTenantAccessRepository.findByUserId(userId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTenantAccessDto> getTenantAccesses(String tenantId) {
        return userTenantAccessRepository.findByTenantId(tenantId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTenantAccessDto> getActiveUserAccesses(String userId) {
        return userTenantAccessRepository.findByUserIdAndIsActiveTrue(userId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTenantAccessDto> getActiveTenantAccesses(String tenantId) {
        return userTenantAccessRepository.findByTenantIdAndIsActiveTrue(tenantId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccess(String userId, String tenantId) {
        return userTenantAccessRepository.existsByUserIdAndTenantIdAndIsActiveTrue(userId, tenantId);
    }

    @Override
    @Transactional
    public UserTenantAccessDto setPrimaryTenant(String userId, String tenantId) {
        log.info("Setting primary tenant {} for user {}", tenantId, userId);
        
        UserTenantAccess access = userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Access not found"));
        
        final UUID accessId = access.getId();
        
        // Unset other primary tenants
        userTenantAccessRepository.findByUserId(userId)
            .stream()
            .filter(a -> !a.getId().equals(accessId) && a.isPrimary())
            .forEach(a -> {
                a.setPrimary(false);
                userTenantAccessRepository.save(a);
            });
        
        access.setPrimary(true);
        access = userTenantAccessRepository.save(access);
        
        log.info("Primary tenant set successfully");
        return mapper.toDto(access);
    }

    @Override
    @Transactional(readOnly = true)
    public UserTenantAccessDto getPrimaryAccess(String userId) {
        return userTenantAccessRepository.findPrimaryAccessForUser(userId)
            .map(mapper::toDto)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers(String tenantId) {
        return userTenantAccessRepository.countActiveUsersByTenant(tenantId);
    }

    @Override
    @Transactional
    public void createAdminAccess(String userId, String tenantId) {
        log.info("Creating admin access for user {} to tenant {}", userId, tenantId);
        
        CreateUserTenantAccessRequest request = CreateUserTenantAccessRequest.builder()
            .userId(userId)
            .tenantId(tenantId)
            .role("ADMIN")
            .isPrimary(true)
            .isActive(true)
            .build();
        
        grantAccess(request);
    }

    @Override
    @Transactional
    public void syncWithKeycloak(String tenantId) {
        log.info("Syncing user access with Keycloak for tenant {}", tenantId);
        
        // Get tenant to find realm name
        var tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));
        
        // Get all users from Keycloak with tenant_id attribute
        var keycloakUsers = keycloakAdminService.getUsersByTenantId(tenant.getRealmName(), tenantId);
        
        for (var user : keycloakUsers) {
            String userId = user.getId();
            
            // Check if access exists
            if (!userTenantAccessRepository.existsByUserIdAndTenantIdAndIsActiveTrue(userId, tenantId)) {
                // Create access record
                CreateUserTenantAccessRequest request = CreateUserTenantAccessRequest.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .role("USER") // Default role
                    .isPrimary(false)
                    .isActive(true)
                    .build();
                
                try {
                    grantAccess(request);
                } catch (ConflictException e) {
                    log.debug("Access already exists for user {} in tenant {}", userId, tenantId);
                }
            }
        }
        
        log.info("Sync completed for tenant {}", tenantId);
    }

    @Override
    @Transactional
    public void removeAllTenantAccesses(String tenantId) {
        log.info("Removing all accesses for tenant {}", tenantId);
        userTenantAccessRepository.deleteByTenantId(tenantId);
        log.info("All accesses removed for tenant {}", tenantId);
    }

    @Override
    @Transactional
    public void reactivateAccess(String userId, String tenantId) {
        log.info("Reactivating access for user {} to tenant {}", userId, tenantId);
        
        var access = userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Access not found for user " + userId + " in tenant " + tenantId));
        
        if (access.isActive()) {
            log.debug("Access is already active for user {} in tenant {}", userId, tenantId);
            return;
        }
        
        access.setActive(true);
        userTenantAccessRepository.save(access);
        log.info("Reactivated access for user {} in tenant {}", userId, tenantId);
    }

    @Override
    @Transactional
    public void updateAccessRole(String userId, String tenantId, String role) {
        log.info("Updating role for user {} in tenant {} to {}", userId, tenantId, role);
        
        var access = userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Access not found for user " + userId + " in tenant " + tenantId));
        
        access.setRole(role);
        userTenantAccessRepository.save(access);
        log.info("Updated role for user {} in tenant {} to {}", userId, tenantId, role);
    }

    @Override
    @Transactional
    public void revokeAllAccess(String userId) {
        log.info("Revoking all tenant accesses for user {}", userId);
        
        var accesses = userTenantAccessRepository.findByUserId(userId);
        
        if (accesses.isEmpty()) {
            log.debug("No accesses found for user {}", userId);
            return;
        }
        
        // Deactivate all accesses
        accesses.forEach(access -> {
            access.setActive(false);
            userTenantAccessRepository.save(access);
        });
        
        log.info("Revoked {} accesses for user {}", accesses.size(), userId);
    }
}