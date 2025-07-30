package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.tenant.event.*;
import sy.sezar.clinicx.tenant.service.TenantAuditService;

import java.time.LocalDateTime;

/**
 * Implementation of tenant audit service.
 * Logs tenant-related security events and publishes them as Spring events.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TenantAuditServiceImpl implements TenantAuditService {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public void auditAccessGranted(String username, String tenantId, String resource) {
        log.info("ACCESS_GRANTED: User={}, Tenant={}, Resource={}, Time={}", 
            username, tenantId, resource, LocalDateTime.now());
        
        TenantAccessEvent event = TenantAccessEvent.builder()
            .username(username)
            .tenantId(tenantId)
            .resource(resource)
            .accessType(TenantAccessEvent.AccessType.GRANTED)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void auditAccessDenied(String username, String tenantId, String resource, String reason) {
        log.warn("ACCESS_DENIED: User={}, Tenant={}, Resource={}, Reason={}, Time={}", 
            username, tenantId, resource, reason, LocalDateTime.now());
        
        TenantAccessEvent event = TenantAccessEvent.builder()
            .username(username)
            .tenantId(tenantId)
            .resource(resource)
            .accessType(TenantAccessEvent.AccessType.DENIED)
            .reason(reason)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void auditTenantSwitch(String username, String fromTenantId, String toTenantId) {
        log.info("TENANT_SWITCH: User={}, From={}, To={}, Time={}", 
            username, fromTenantId, toTenantId, LocalDateTime.now());
        
        TenantSwitchEvent event = TenantSwitchEvent.builder()
            .username(username)
            .fromTenantId(fromTenantId)
            .toTenantId(toTenantId)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void auditTenantCreated(String username, String tenantId, String tenantName) {
        log.info("TENANT_CREATED: User={}, TenantId={}, TenantName={}, Time={}", 
            username, tenantId, tenantName, LocalDateTime.now());
        
        TenantManagementEvent event = TenantManagementEvent.builder()
            .username(username)
            .tenantId(tenantId)
            .tenantName(tenantName)
            .action(TenantManagementEvent.Action.CREATED)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void auditTenantModified(String username, String tenantId, String changes) {
        log.info("TENANT_MODIFIED: User={}, TenantId={}, Changes={}, Time={}", 
            username, tenantId, changes, LocalDateTime.now());
        
        TenantManagementEvent event = TenantManagementEvent.builder()
            .username(username)
            .tenantId(tenantId)
            .action(TenantManagementEvent.Action.MODIFIED)
            .details(changes)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void auditUserAddedToTenant(String adminUsername, String userId, String tenantId, String role) {
        log.info("USER_ADDED_TO_TENANT: Admin={}, User={}, Tenant={}, Role={}, Time={}", 
            adminUsername, userId, tenantId, role, LocalDateTime.now());
        
        UserTenantEvent event = UserTenantEvent.builder()
            .adminUsername(adminUsername)
            .userId(userId)
            .tenantId(tenantId)
            .role(role)
            .action(UserTenantEvent.Action.USER_ADDED)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void auditUserRemovedFromTenant(String adminUsername, String userId, String tenantId) {
        log.info("USER_REMOVED_FROM_TENANT: Admin={}, User={}, Tenant={}, Time={}", 
            adminUsername, userId, tenantId, LocalDateTime.now());
        
        UserTenantEvent event = UserTenantEvent.builder()
            .adminUsername(adminUsername)
            .userId(userId)
            .tenantId(tenantId)
            .action(UserTenantEvent.Action.USER_REMOVED)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventPublisher.publishEvent(event);
    }
}