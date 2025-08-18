package sy.sezar.clinicx.core.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.tenant.TenantContext;

import java.util.*;

/**
 * Tenant-aware JWT authorities converter that strictly filters roles based on the current tenant context.
 * This converter COMPLETELY IGNORES realm_access and resource_access claims to prevent cross-tenant 
 * privilege leakage. Only tenant-scoped roles from user_tenant_roles claim are mapped.
 * 
 * Key Security Features:
 * - Realm roles are NEVER mapped (except explicit GLOBAL_* roles)
 * - Resource/client roles are NEVER mapped
 * - Only roles for the current tenant context are converted to authorities
 * - Prevents any form of cross-tenant privilege escalation
 */
@Slf4j
@Component
public class TenantAwareJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String GLOBAL_PREFIX = "GLOBAL_";
    private static final String CLAIM_TENANT_ROLES = "user_tenant_roles"; // JSON object preferred
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_KEY = "roles";
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String tenantId = TenantContext.getCurrentTenant();
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        log.debug("Converting authorities for tenant: {} | User: {}", tenantId, jwt.getSubject());
        
        // CRITICAL: Do NOT read jwt.getClaim("realm_access") or jwt.getClaim("resource_access") 
        // for regular roles. This prevents realm role leakage across tenants.
        
        // Optional: Allow ONLY explicit global roles with GLOBAL_ prefix from realm_access
        keepGlobalRolesOnly(jwt, authorities);
        
        // Only map roles that are scoped to the current tenant
        if (tenantId != null && !tenantId.isBlank()) {
            Object tenantsObj = jwt.getClaim(CLAIM_TENANT_ROLES);
            
            if (tenantsObj instanceof Map<?, ?> map) {
                // Direct JSON object in claim (preferred)
                Object rolesObj = map.get(tenantId);
                if (rolesObj instanceof Collection<?> roles) {
                    for (Object role : roles) {
                        if (role instanceof String r && !r.isBlank()) {
                            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + r.toUpperCase()));
                            log.trace("Added tenant role: {} for tenant: {}", r, tenantId);
                        }
                    }
                }
            } else if (tenantsObj instanceof String json) {
                // Backward compatibility: JSON string that needs parsing
                try {
                    Map<String, List<String>> parsed = objectMapper.readValue(
                        json, 
                        new TypeReference<Map<String, List<String>>>() {}
                    );
                    List<String> roles = parsed.get(tenantId);
                    if (roles != null) {
                        roles.forEach(r -> {
                            if (r != null && !r.isBlank()) {
                                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + r.toUpperCase()));
                                log.trace("Added tenant role from JSON: {} for tenant: {}", r, tenantId);
                            }
                        });
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse user_tenant_roles JSON string: {}", e.getMessage());
                }
            }
            
            if (authorities.isEmpty()) {
                log.warn("No roles found for user {} in tenant {} - user may have no access", 
                        jwt.getSubject(), tenantId);
            }
        } else {
            log.debug("No tenant context set - no tenant roles will be added");
        }
        
        log.debug("Total authorities granted: {} for user {} in tenant {}", 
                 authorities.size(), jwt.getSubject(), tenantId);
        
        return authorities;
    }
    
    /**
     * Optional: Allow ONLY explicit global roles with GLOBAL_ prefix from realm_access.
     * Everything else from realm_access is completely ignored.
     * This ensures realm roles cannot cause cross-tenant privilege leakage.
     * 
     * @param jwt The JWT token
     * @param authorities The set to add global authorities to
     */
    @SuppressWarnings("unchecked")
    private void keepGlobalRolesOnly(Jwt jwt, Set<GrantedAuthority> authorities) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
        if (realmAccess != null) {
            Object rolesObj = realmAccess.get(ROLES_KEY);
            if (rolesObj instanceof Collection<?>) {
                ((Collection<String>) rolesObj).stream()
                    .filter(r -> r != null && r.startsWith(GLOBAL_PREFIX))
                    .forEach(r -> {
                        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + r));
                        log.trace("Added global role: {} (explicitly allowed)", r);
                    });
                
                // Log warning if non-global realm roles are detected (for migration tracking)
                long nonGlobalCount = ((Collection<String>) rolesObj).stream()
                    .filter(r -> r != null && !r.startsWith(GLOBAL_PREFIX))
                    .count();
                
                if (nonGlobalCount > 0) {
                    log.debug("Ignored {} non-global realm roles for user {} - these do not grant any authorities", 
                            nonGlobalCount, jwt.getSubject());
                }
            }
        }
        
        // CRITICAL: We do NOT process resource_access at all - client roles are completely ignored
    }
}