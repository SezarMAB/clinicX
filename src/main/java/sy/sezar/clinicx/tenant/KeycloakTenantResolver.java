package sy.sezar.clinicx.tenant;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.repository.TenantRepository;

import java.util.Optional;

/**
 * Resolves tenant from multiple sources:
 * 1. X-Tenant-ID header
 * 2. Subdomain from request
 * 3. JWT token claims
 * 4. Default tenant fallback
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakTenantResolver implements TenantResolver {

    @Autowired
    private TenantRepository tenantRepository;

    @Value("${app.tenant.mode:single}")
    private String tenantMode;

    @Value("${app.tenant.default-tenant}")
    private String defaultTenantId;

    @Value("${app.multi-tenant.enabled:true}")
    private boolean multiTenantEnabled;

    @Value("${app.domain}")
    private String appDomain;

    @Override
    public String resolveTenant() {
        // In single-tenant mode or if multi-tenancy is disabled, always return default
        if ("single".equalsIgnoreCase(tenantMode) || !multiTenantEnabled) {
            return defaultTenantId;
        }

        HttpServletRequest request = getCurrentRequest();

        // 1. Try to get tenant from header first (useful for API clients)
        if (request != null) {
            String tenantHeader = request.getHeader("X-Tenant-ID");
            if (tenantHeader != null && !tenantHeader.isEmpty()) {
                return validateAndReturnTenant(tenantHeader);
            }

            // 2. Extract subdomain from host
            String host = request.getServerName();
            String subdomain = extractSubdomain(host);

            if (subdomain != null && !subdomain.isEmpty()) {
                Optional<Tenant> tenant = tenantRepository.findBySubdomain(subdomain);
                if (tenant.isPresent() && tenant.get().isActive()) {
                    return tenant.get().getTenantId();
                }
            }
        }

        // 3. Try to extract from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // First try to get active_tenant_id (for multi-tenant users)
            String activeTenantId = jwt.getClaimAsString("active_tenant_id");
            if (activeTenantId != null && !activeTenantId.isEmpty()) {
                log.debug("Resolved active tenant from JWT claim: {}", activeTenantId);
                return validateAndReturnTenant(activeTenantId);
            }
            
            // Then try to get tenant_id from custom claim
            String tenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId != null && !tenantId.isEmpty()) {
                log.debug("Resolved tenant from JWT claim: {}", tenantId);
                return tenantId;
            }

            // Fallback: extract from realm name if using realm-per-tenant
            String issuer = jwt.getIssuer().toString();
            if (issuer.contains("/realms/clinic-")) {
                // Extract tenant from realm name: clinic-subdomain -> subdomain
                String realmName = issuer.substring(issuer.lastIndexOf("/realms/") + 8);
                if (realmName.startsWith("clinic-") && !realmName.equals("clinicx-dev")) {
                    String subdomain = realmName.substring(7); // Remove "clinic-" prefix
                    Optional<Tenant> tenant = tenantRepository.findBySubdomain(subdomain);
                    if (tenant.isPresent() && tenant.get().isActive()) {
                        log.debug("Resolved tenant from realm name: {}", tenant.get().getTenantId());
                        return tenant.get().getTenantId();
                    }
                }
            }
        }

        // Default fallback
        log.debug("Using default tenant: {}", defaultTenantId);
        return defaultTenantId;
    }

    @Override
    public boolean isMultiTenant() {
        return "multi".equalsIgnoreCase(tenantMode) && multiTenantEnabled;
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String extractSubdomain(String host) {
        if (host == null || host.isEmpty()) {
            return null;
        }

        // Remove port if present
        int portIndex = host.indexOf(':');
        if (portIndex > 0) {
            host = host.substring(0, portIndex);
        }

        // Handle localhost
        if (host.equals("localhost") || host.equals("127.0.0.1")) {
            return null;
        }

        // Extract subdomain
        String domainPattern = "." + appDomain;
        if (host.endsWith(domainPattern)) {
            int subdomainEndIndex = host.length() - domainPattern.length();
            return host.substring(0, subdomainEndIndex);
        }

        // For development, check if host contains a subdomain pattern
        int firstDot = host.indexOf('.');
        if (firstDot > 0) {
            return host.substring(0, firstDot);
        }

        return null;
    }

    private String validateAndReturnTenant(String tenantId) {
        Optional<Tenant> tenant = tenantRepository.findByTenantId(tenantId);
        if (tenant.isPresent() && tenant.get().isActive()) {
            return tenantId;
        }
        return defaultTenantId;
    }
}
