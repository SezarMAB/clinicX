package sy.sezar.clinicx.tenant.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.tenant.dto.*;

import java.util.List;

/**
 * Service interface for managing users within a tenant.
 */
public interface TenantUserService {
    
    /**
     * Get all users in a tenant.
     * 
     * @param tenantId the tenant ID
     * @param includeExternal whether to include external users
     * @param pageable pagination information
     * @return page of users
     */
    Page<TenantUserDto> getTenantUsers(String tenantId, boolean includeExternal, Pageable pageable);
    
    /**
     * Search users by username, email, or name.
     * 
     * @param tenantId the tenant ID
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching users
     */
    Page<TenantUserDto> searchUsers(String tenantId, String searchTerm, Pageable pageable);
    
    /**
     * Get a specific user.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     * @return user details
     */
    TenantUserDto getUser(String tenantId, String userId);
    
    /**
     * Create a new user in the tenant.
     * 
     * @param tenantId the tenant ID
     * @param request creation request
     * @return created user
     */
    TenantUserDto createUser(String tenantId, TenantUserCreateRequest request);
    
    /**
     * Update an existing user.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     * @param request update request
     * @return updated user
     */
    TenantUserDto updateUser(String tenantId, String userId, TenantUserUpdateRequest request);
    
    /**
     * Deactivate a user.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     */
    void deactivateUser(String tenantId, String userId);
    
    /**
     * Activate a user.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     */
    void activateUser(String tenantId, String userId);
    
    /**
     * Delete a user permanently.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     */
    void deleteUser(String tenantId, String userId);
    
    /**
     * Update user roles.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     * @param roles new roles
     * @return updated user
     */
    TenantUserDto updateUserRoles(String tenantId, String userId, List<String> roles);
    
    /**
     * Grant an external user access to the tenant.
     * 
     * @param tenantId the tenant ID
     * @param username the external user's username
     * @param roles roles to grant
     * @return user with access granted
     */
    TenantUserDto grantExternalUserAccess(String tenantId, String username, List<String> roles);
    
    /**
     * Revoke an external user's access to the tenant.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     */
    void revokeExternalUserAccess(String tenantId, String userId);
    
    /**
     * Get user activity logs.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of activity logs
     */
    Page<UserActivityDto> getUserActivity(String tenantId, String userId, Pageable pageable);
    
    /**
     * Reset user password.
     * 
     * @param tenantId the tenant ID
     * @param userId the user ID
     * @param newPassword new password
     * @param temporary whether password is temporary
     */
    void resetUserPassword(String tenantId, String userId, String newPassword, boolean temporary);
}