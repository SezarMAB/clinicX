package sy.sezar.clinicx.tenant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.event.TenantAccessEvent;
import sy.sezar.clinicx.tenant.event.TenantSwitchEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for auditing security events in a multi-tenant context.
 * Tracks authentication, authorization, and tenant access events for security monitoring.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantSecurityAuditService {
    
    // Track failed access attempts per user per tenant
    private final Map<String, Map<String, AtomicInteger>> failedAccessAttempts = new ConcurrentHashMap<>();
    
    // Track suspicious activities
    private final Map<String, Instant> suspiciousActivities = new ConcurrentHashMap<>();
    
    /**
     * Log successful authentication event.
     */
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        String username = auth.getName();
        String tenantId = TenantContext.getCurrentTenant();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "AUTHENTICATION_SUCCESS");
        auditData.put("username", username);
        auditData.put("tenant", tenantId);
        auditData.put("timestamp", Instant.now());
        auditData.put("ip", getClientIp());
        
        log.info("SECURITY_AUDIT: Authentication success - User: {} | Tenant: {} | Data: {}", 
                username, tenantId, auditData);
        
        // Reset failed attempts on successful authentication
        resetFailedAttempts(username, tenantId);
    }
    
    /**
     * Log authentication failure event.
     */
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "AUTHENTICATION_FAILURE");
        auditData.put("username", username);
        auditData.put("tenant", tenantId);
        auditData.put("reason", event.getException().getMessage());
        auditData.put("timestamp", Instant.now());
        auditData.put("ip", getClientIp());
        
        log.warn("SECURITY_AUDIT: Authentication failure - User: {} | Tenant: {} | Data: {}", 
                username, tenantId, auditData);
        
        // Track failed attempts
        incrementFailedAttempts(username, tenantId);
    }
    
    /**
     * Log authorization failure event.
     */
    @EventListener
    public void handleAuthorizationFailure(AuthorizationFailureEvent event) {
        Authentication auth = event.getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        String tenantId = TenantContext.getCurrentTenant();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "AUTHORIZATION_FAILURE");
        auditData.put("username", username);
        auditData.put("tenant", tenantId);
        auditData.put("resource", event.getSource());
        auditData.put("timestamp", Instant.now());
        auditData.put("ip", getClientIp());
        
        // Check if this is a cross-tenant access attempt
        if (isCrossTenantAccessAttempt(auth, tenantId)) {
            auditData.put("security_alert", "CROSS_TENANT_ACCESS_ATTEMPT");
            log.error("SECURITY_ALERT: Cross-tenant access attempt - User: {} | Tenant: {} | Data: {}", 
                    username, tenantId, auditData);
            
            // Track suspicious activity
            trackSuspiciousActivity(username);
        } else {
            log.warn("SECURITY_AUDIT: Authorization failure - User: {} | Tenant: {} | Data: {}", 
                    username, tenantId, auditData);
        }
    }
    
    /**
     * Log tenant access event.
     */
    @EventListener
    public void handleTenantAccessEvent(TenantAccessEvent event) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "TENANT_ACCESS");
        auditData.put("username", event.getUsername());
        auditData.put("tenant", event.getTenantId());
        auditData.put("resource", event.getResource());
        auditData.put("access_type", event.getAccessType());
        auditData.put("reason", event.getReason());
        auditData.put("timestamp", event.getEventTimestamp());
        
        if (event.getAccessType() == TenantAccessEvent.AccessType.GRANTED) {
            log.info("SECURITY_AUDIT: Tenant access granted - User: {} | Tenant: {} | Data: {}", 
                    event.getUsername(), event.getTenantId(), auditData);
        } else {
            log.warn("SECURITY_AUDIT: Tenant access denied - User: {} | Tenant: {} | Data: {}", 
                    event.getUsername(), event.getTenantId(), auditData);
        }
    }
    
    /**
     * Log tenant switch event.
     */
    @EventListener
    public void handleTenantSwitchEvent(TenantSwitchEvent event) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "TENANT_SWITCH");
        auditData.put("username", event.getUsername());
        auditData.put("from_tenant", event.getFromTenantId());
        auditData.put("to_tenant", event.getToTenantId());
        auditData.put("timestamp", event.getEventTimestamp());
        auditData.put("ip", getClientIp());
        
        log.info("SECURITY_AUDIT: Tenant switch - User: {} | From: {} | To: {} | Data: {}", 
                event.getUsername(), event.getFromTenantId(), event.getToTenantId(), auditData);
    }
    
    /**
     * Log admin operation in tenant context.
     */
    public void logAdminOperation(String operation, String resource, Map<String, Object> details) {
        String username = getCurrentUsername();
        String tenantId = TenantContext.getCurrentTenant();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "ADMIN_OPERATION");
        auditData.put("username", username);
        auditData.put("tenant", tenantId);
        auditData.put("operation", operation);
        auditData.put("resource", resource);
        auditData.put("details", details);
        auditData.put("timestamp", Instant.now());
        auditData.put("ip", getClientIp());
        
        log.info("SECURITY_AUDIT: Admin operation - User: {} | Tenant: {} | Operation: {} | Data: {}", 
                username, tenantId, operation, auditData);
    }
    
    /**
     * Log data access event for sensitive operations.
     */
    public void logDataAccess(String entityType, String entityId, String action) {
        String username = getCurrentUsername();
        String tenantId = TenantContext.getCurrentTenant();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "DATA_ACCESS");
        auditData.put("username", username);
        auditData.put("tenant", tenantId);
        auditData.put("entity_type", entityType);
        auditData.put("entity_id", entityId);
        auditData.put("action", action);
        auditData.put("timestamp", Instant.now());
        
        log.debug("SECURITY_AUDIT: Data access - User: {} | Tenant: {} | Entity: {}:{} | Action: {}", 
                username, tenantId, entityType, entityId, action);
    }
    
    /**
     * Check if there are suspicious activities for a user.
     */
    public boolean hasSuspiciousActivity(String username) {
        Instant lastActivity = suspiciousActivities.get(username);
        if (lastActivity != null) {
            // Consider activity suspicious if it occurred within the last hour
            return lastActivity.isAfter(Instant.now().minusSeconds(3600));
        }
        return false;
    }
    
    /**
     * Get failed attempt count for a user in a tenant.
     */
    public int getFailedAttemptCount(String username, String tenantId) {
        return failedAccessAttempts
                .getOrDefault(username, new HashMap<>())
                .getOrDefault(tenantId, new AtomicInteger(0))
                .get();
    }
    
    private void incrementFailedAttempts(String username, String tenantId) {
        failedAccessAttempts
                .computeIfAbsent(username, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(tenantId, k -> new AtomicInteger(0))
                .incrementAndGet();
        
        // Alert if too many failed attempts
        int attempts = getFailedAttemptCount(username, tenantId);
        if (attempts > 5) {
            log.error("SECURITY_ALERT: Multiple failed access attempts - User: {} | Tenant: {} | Count: {}", 
                    username, tenantId, attempts);
        }
    }
    
    private void resetFailedAttempts(String username, String tenantId) {
        Map<String, AtomicInteger> userAttempts = failedAccessAttempts.get(username);
        if (userAttempts != null) {
            userAttempts.remove(tenantId);
        }
    }
    
    private void trackSuspiciousActivity(String username) {
        suspiciousActivities.put(username, Instant.now());
    }
    
    private boolean isCrossTenantAccessAttempt(Authentication auth, String requestedTenant) {
        if (auth == null || requestedTenant == null) {
            return false;
        }
        
        if (auth.getPrincipal() instanceof Jwt jwt) {
            // Check if user has any roles in the requested tenant
            Object tenantRoles = jwt.getClaim("user_tenant_roles");
            if (tenantRoles instanceof Map<?, ?> rolesMap) {
                return !rolesMap.containsKey(requestedTenant);
            }
        }
        
        return false;
    }
    
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getPrincipal() instanceof Jwt jwt) {
                return jwt.getClaimAsString("preferred_username");
            }
            return auth.getName();
        }
        return "anonymous";
    }
    
    private String getClientIp() {
        // In a real implementation, extract from request context
        // For now, return a placeholder
        return "0.0.0.0";
    }
}