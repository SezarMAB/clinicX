package sy.sezar.clinicx.core.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Resolves tenant from Keycloak JWT token.
 * Falls back to default tenant for development mode.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakTenantResolver implements TenantResolver {
    
    @Value("${clinicx.tenant.mode:single}")
    private String tenantMode;
    
    @Value("${clinicx.tenant.default-id:default-tenant}")
    private String defaultTenantId;
    
    @Override
    public String resolveTenant() {
        // In single-tenant mode, always return default
        if ("single".equalsIgnoreCase(tenantMode)) {
            return defaultTenantId;
        }
        
        // Try to extract from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            // First try to get tenant_id from custom claim
            String tenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId != null && !tenantId.isEmpty()) {
                log.debug("Resolved tenant from JWT claim: {}", tenantId);
                return tenantId;
            }
            
            // Fallback: extract from realm name if using realm-per-tenant
            String issuer = jwt.getIssuer().toString();
            if (issuer.contains("/realms/clinicx-")) {
                // Extract tenant from realm name: clinicx-tenant-001 -> tenant-001
                String realmName = issuer.substring(issuer.lastIndexOf("/realms/") + 8);
                if (realmName.startsWith("clinicx-") && !realmName.equals("clinicx-dev")) {
                    String extractedTenant = realmName.substring(8); // Remove "clinicx-" prefix
                    log.debug("Resolved tenant from realm name: {}", extractedTenant);
                    return extractedTenant;
                }
            }
        }
        
        // Default fallback
        log.debug("Using default tenant: {}", defaultTenantId);
        return defaultTenantId;
    }
    
    @Override
    public boolean isMultiTenant() {
        return "multi".equalsIgnoreCase(tenantMode);
    }
}