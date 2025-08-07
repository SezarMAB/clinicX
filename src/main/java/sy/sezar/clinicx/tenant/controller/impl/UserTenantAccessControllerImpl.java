package sy.sezar.clinicx.tenant.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.controller.api.UserTenantAccessControllerApi;
import sy.sezar.clinicx.tenant.dto.GrantTenantAccessRequest;
import sy.sezar.clinicx.tenant.dto.TenantAccessDto;
import sy.sezar.clinicx.tenant.service.TenantSwitchingService;

import java.util.List;

/**
 * Implementation for managing user access to multiple tenants.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserTenantAccessControllerImpl implements UserTenantAccessControllerApi {

    private final TenantSwitchingService tenantSwitchingService;

    @Override
    public ResponseEntity<Void> grantTenantAccess(String userId, GrantTenantAccessRequest request) {
        log.info("Granting user {} access to tenant {} with role {}",
                userId, request.tenantId(), request.role());

        tenantSwitchingService.grantUserTenantAccess(
            userId,
            request.tenantId(),
            request.role(),
            request.isPrimary()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> revokeTenantAccess(String userId, String tenantId) {
        log.info("Revoking user {} access to tenant {}", userId, tenantId);
        tenantSwitchingService.revokeUserTenantAccess(tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TenantAccessDto>> getUserTenantAccess(String userId) {
        log.info("Getting tenant access for user {}", userId);
        List<TenantAccessDto> tenantAccess = tenantSwitchingService.getUserTenants(userId);
        return ResponseEntity.ok(tenantAccess);
    }
}