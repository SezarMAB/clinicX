package sy.sezar.clinicx.tenant.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.controller.api.TenantUserApi;
import sy.sezar.clinicx.tenant.dto.*;
import sy.sezar.clinicx.tenant.service.TenantUserService;

/**
 * Implementation of the TenantUserApi for managing users within tenants.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class TenantUserController implements TenantUserApi {
    
    private final TenantUserService tenantUserService;
    
    @Override
    public ResponseEntity<Page<TenantUserDto>> getAllUsers(boolean includeExternal, Pageable pageable) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Retrieving users for tenant {} (includeExternal: {})", currentTenant, includeExternal);
        
        Page<TenantUserDto> users = tenantUserService.getTenantUsers(currentTenant, includeExternal, pageable);
        return ResponseEntity.ok(users);
    }
    
    @Override
    public ResponseEntity<Page<TenantUserDto>> searchUsers(String searchTerm, Pageable pageable) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Searching users in tenant {} with term: {}", currentTenant, searchTerm);
        
        Page<TenantUserDto> users = tenantUserService.searchUsers(currentTenant, searchTerm, pageable);
        return ResponseEntity.ok(users);
    }
    
    @Override
    public ResponseEntity<TenantUserDto> getUser(String userId) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Retrieving user {} in tenant {}", userId, currentTenant);
        
        TenantUserDto user = tenantUserService.getUser(currentTenant, userId);
        return ResponseEntity.ok(user);
    }
    
    @Override
    public ResponseEntity<TenantUserDto> createUser(TenantUserCreateRequest request) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Creating new user {} in tenant {}", request.username(), currentTenant);
        
        TenantUserDto user = tenantUserService.createUser(currentTenant, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @Override
    public ResponseEntity<TenantUserDto> updateUser(String userId, TenantUserUpdateRequest request) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Updating user {} in tenant {}", userId, currentTenant);
        
        TenantUserDto user = tenantUserService.updateUser(currentTenant, userId, request);
        return ResponseEntity.ok(user);
    }
    
    @Override
    public ResponseEntity<Void> deactivateUser(String userId) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Deactivating user {} in tenant {}", userId, currentTenant);
        
        tenantUserService.deactivateUser(currentTenant, userId);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<Void> activateUser(String userId) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Activating user {} in tenant {}", userId, currentTenant);
        
        tenantUserService.activateUser(currentTenant, userId);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Permanently deleting user {} from tenant {}", userId, currentTenant);
        
        tenantUserService.deleteUser(currentTenant, userId);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<TenantUserDto> updateUserRoles(String userId, UpdateUserRolesRequest request) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Updating roles for user {} in tenant {}: {}", userId, currentTenant, request.roles());
        
        TenantUserDto user = tenantUserService.updateUserRoles(currentTenant, userId, request.roles());
        return ResponseEntity.ok(user);
    }
    
    @Override
    public ResponseEntity<TenantUserDto> grantExternalUserAccess(GrantExternalAccessRequest request) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Granting external user {} access to tenant {} with roles: {}", 
                request.username(), currentTenant, request.roles());
        
        TenantUserDto user = tenantUserService.grantExternalUserAccess(
            currentTenant, 
            request.username(), 
            request.roles()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @Override
    public ResponseEntity<Void> revokeExternalUserAccess(String userId) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Revoking external user {} access to tenant {}", userId, currentTenant);
        
        tenantUserService.revokeExternalUserAccess(currentTenant, userId);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<Page<UserActivityDto>> getUserActivity(String userId, Pageable pageable) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Retrieving activity for user {} in tenant {}", userId, currentTenant);
        
        Page<UserActivityDto> activity = tenantUserService.getUserActivity(currentTenant, userId, pageable);
        return ResponseEntity.ok(activity);
    }
    
    @Override
    public ResponseEntity<Void> resetUserPassword(String userId, ResetPasswordRequest request) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("Resetting password for user {} in tenant {}", userId, currentTenant);
        
        tenantUserService.resetUserPassword(currentTenant, userId, request.newPassword(), request.temporary());
        return ResponseEntity.noContent().build();
    }
}