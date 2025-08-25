package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.ConflictException;
import sy.sezar.clinicx.core.exception.ResourceNotFoundException;
import sy.sezar.clinicx.tenant.constants.TenantConstants;
import sy.sezar.clinicx.tenant.model.UserTenantAccess;
import sy.sezar.clinicx.tenant.repository.UserTenantAccessRepository;
import sy.sezar.clinicx.tenant.service.UserAccessManagementService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of UserAccessManagementService.
 * Manages user access control across multiple tenants.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccessManagementServiceImpl implements UserAccessManagementService {
    
    private final UserTenantAccessRepository userTenantAccessRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserTenantAccess> findByUserIdAndTenantId(String userId, String tenantId) {
        return userTenantAccessRepository.findByUserIdAndTenantId(userId, tenantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserTenantAccess getAccess(String userId, String tenantId) {
        return findByUserIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("No access found for user %s in tenant %s", userId, tenantId)));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserTenantAccess> findByUserId(String userId) {
        return userTenantAccessRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserTenantAccess> findActiveAccessByUserId(String userId) {
        return userTenantAccessRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveAccessForUser(String userId) {
        return userTenantAccessRepository.countByUserIdAndIsActiveTrue(userId);
    }
    
    @Override
    @Transactional
    public UserTenantAccess createAccess(String userId, String tenantId, Set<StaffRole> roles, boolean isPrimary) {
        // Check if access already exists
        Optional<UserTenantAccess> existing = findByUserIdAndTenantId(userId, tenantId);
        if (existing.isPresent() && existing.get().isActive()) {
            throw new ConflictException(TenantConstants.ERROR_USER_ALREADY_HAS_ACCESS);
        }
        
        // If setting as primary, unset other primary tenants
        if (isPrimary) {
            updatePrimaryTenant(userId, tenantId);
        }
        
        UserTenantAccess access = new UserTenantAccess();
        access.setUserId(userId);
        access.setTenantId(tenantId);
        access.setRoles(roles != null ? new HashSet<>(roles) : new HashSet<>());
        access.setPrimary(isPrimary);
        access.setActive(true);
        
        UserTenantAccess savedAccess = userTenantAccessRepository.save(access);
        log.info(TenantConstants.LOG_CREATED_ACCESS, savedAccess.getId(), userId, tenantId);
        return savedAccess;
    }
    
    @Override
    @Transactional
    public UserTenantAccess updateRoles(String userId, String tenantId, Set<StaffRole> roles) {
        UserTenantAccess access = getAccess(userId, tenantId);
        access.setRoles(roles != null ? new HashSet<>(roles) : new HashSet<>());
        UserTenantAccess updatedAccess = userTenantAccessRepository.save(access);
        log.info("Updated roles for user {} in tenant {}", userId, tenantId);
        return updatedAccess;
    }
    
    @Override
    @Transactional
    public UserTenantAccess deactivateAccess(String userId, String tenantId) {
        UserTenantAccess access = getAccess(userId, tenantId);
        validateRevocation(access);
        
        access.setActive(false);
        UserTenantAccess deactivatedAccess = userTenantAccessRepository.save(access);
        log.info(TenantConstants.LOG_DEACTIVATED_ACCESS, userId, tenantId);
        return deactivatedAccess;
    }
    
    @Override
    @Transactional
    public UserTenantAccess activateAccess(String userId, String tenantId) {
        UserTenantAccess access = getAccess(userId, tenantId);
        access.setActive(true);
        UserTenantAccess activatedAccess = userTenantAccessRepository.save(access);
        log.info(TenantConstants.LOG_REACTIVATED_ACCESS, userId, tenantId);
        return activatedAccess;
    }
    
    @Override
    @Transactional
    public void deactivateAllAccessForUser(String userId) {
        List<UserTenantAccess> accessList = findByUserId(userId);
        for (UserTenantAccess access : accessList) {
            access.setActive(false);
        }
        userTenantAccessRepository.saveAll(accessList);
        log.info("Deactivated all access for user {}", userId);
    }
    
    @Override
    @Transactional
    public UserTenantAccess createOrReactivateAccess(String userId, String tenantId, 
                                                    Set<StaffRole> roles, boolean isPrimary) {
        Optional<UserTenantAccess> existing = findByUserIdAndTenantId(userId, tenantId);
        
        if (existing.isPresent()) {
            UserTenantAccess access = existing.get();
            if (!access.isActive()) {
                access.setActive(true);
                access.setRoles(roles != null ? new HashSet<>(roles) : new HashSet<>());
                if (isPrimary) {
                    updatePrimaryTenant(userId, tenantId);
                    access.setPrimary(true);
                }
                UserTenantAccess reactivated = userTenantAccessRepository.save(access);
                log.info(TenantConstants.LOG_REACTIVATED_ACCESS, userId, tenantId);
                return reactivated;
            }
            throw new ConflictException(TenantConstants.ERROR_USER_ALREADY_HAS_ACCESS);
        }
        
        return createAccess(userId, tenantId, roles, isPrimary);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveAccess(String userId, String tenantId) {
        return userTenantAccessRepository.existsByUserIdAndTenantIdAndIsActiveTrue(userId, tenantId);
    }
    
    @Override
    public void validateRevocation(UserTenantAccess access) {
        if (access.isPrimary()) {
            throw new BusinessRuleException(TenantConstants.ERROR_CANNOT_REVOKE_PRIMARY);
        }
        
        // Check if this is the only active tenant for the user
        long activeCount = countActiveAccessForUser(access.getUserId());
        if (activeCount <= 1) {
            throw new BusinessRuleException(TenantConstants.ERROR_CANNOT_REVOKE_ONLY_TENANT);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserTenantAccess> getPrimaryAccessForUser(String userId) {
        return userTenantAccessRepository.findByUserIdAndIsPrimaryTrueAndIsActiveTrue(userId);
    }
    
    @Override
    @Transactional
    public void updatePrimaryTenant(String userId, String newPrimaryTenantId) {
        // Unset all current primary tenants for this user
        List<UserTenantAccess> accessList = findByUserId(userId);
        for (UserTenantAccess access : accessList) {
            if (access.isPrimary() && !access.getTenantId().equals(newPrimaryTenantId)) {
                access.setPrimary(false);
                userTenantAccessRepository.save(access);
            }
        }
        log.info("Updated primary tenant for user {} to {}", userId, newPrimaryTenantId);
    }
}