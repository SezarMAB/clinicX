package sy.sezar.clinicx.tenant.service;

import sy.sezar.clinicx.tenant.dto.UserTenantAccessDto;
import sy.sezar.clinicx.tenant.dto.CreateUserTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.UpdateUserTenantAccessRequest;

import java.util.List;
import java.util.UUID;

public interface UserTenantAccessService {

    UserTenantAccessDto grantAccess(CreateUserTenantAccessRequest request);

    UserTenantAccessDto updateAccess(UUID id, UpdateUserTenantAccessRequest request);

    void revokeAccess(String userId, String tenantId);

    void revokeAccessById(UUID id);

    UserTenantAccessDto getAccess(String userId, String tenantId);

    List<UserTenantAccessDto> getUserAccesses(String userId);

    List<UserTenantAccessDto> getTenantAccesses(String tenantId);

    List<UserTenantAccessDto> getActiveUserAccesses(String userId);

    List<UserTenantAccessDto> getActiveTenantAccesses(String tenantId);

    boolean hasAccess(String userId, String tenantId);

    UserTenantAccessDto setPrimaryTenant(String userId, String tenantId);

    UserTenantAccessDto getPrimaryAccess(String userId);

    long countActiveUsers(String tenantId);

    void createAdminAccess(String userId, String tenantId);

    void syncWithKeycloak(String tenantId);

    void removeAllTenantAccesses(String tenantId);

    void reactivateAccess(String userId, String tenantId);

    void updateAccessRole(String userId, String tenantId, String role);

    void revokeAllAccess(String userId);
}