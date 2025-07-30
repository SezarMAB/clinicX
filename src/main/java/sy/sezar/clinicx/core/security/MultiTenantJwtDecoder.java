package sy.sezar.clinicx.core.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import sy.sezar.clinicx.tenant.TenantContext;
import sy.sezar.clinicx.tenant.model.Tenant;
import sy.sezar.clinicx.tenant.repository.TenantRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantJwtDecoder implements JwtDecoder {
    
    private TenantRepository tenantRepository;
    private String keycloakBaseUrl;
    private boolean multiTenantEnabled;
    private String defaultRealm;
    
    private final Map<String, JwtDecoder> decoders = new ConcurrentHashMap<>();
    
    @Override
    public Jwt decode(String token) throws JwtException {
        if (!multiTenantEnabled) {
            return getDecoder(defaultRealm).decode(token);
        }
        
        try {
            // Parse token to get issuer
            JWT jwt = JWTParser.parse(token);
            String issuer = jwt.getJWTClaimsSet().getIssuer();
            
            // Extract realm from issuer
            String realm = extractRealmFromIssuer(issuer);
            
            // Special handling for master and default realms
            if (realm.equals(defaultRealm) || realm.equals("master")) {
                // For default/master realm, set the default tenant context
                TenantContext.setCurrentTenant(defaultRealm);
            } else {
                // In realm-per-type architecture, get tenant_id from the token claims
                String tenantId = (String) jwt.getJWTClaimsSet().getClaim("tenant_id");
                
                if (tenantId == null) {
                    throw new JwtException("No tenant_id found in token");
                }
                
                // Validate tenant exists and is active
                Optional<Tenant> tenant = tenantRepository.findByTenantId(tenantId);
                if (tenant.isEmpty() || !tenant.get().isActive()) {
                    throw new JwtException("Invalid or inactive tenant");
                }
                
                // Set tenant context from token
                TenantContext.setCurrentTenant(tenantId);
            }
            
            // Get or create decoder for this realm
            JwtDecoder decoder = getDecoder(realm);
            
            return decoder.decode(token);
            
        } catch (Exception e) {
            throw new JwtException("Failed to decode JWT", e);
        }
    }
    
    private JwtDecoder getDecoder(String realm) {
        return decoders.computeIfAbsent(realm, r -> {
            String jwkSetUri = buildJwkSetUri(r);
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        });
    }
    
    private String buildJwkSetUri(String realm) {
        return String.format("%s/realms/%s/protocol/openid-connect/certs", keycloakBaseUrl, realm);
    }
    
    private String extractRealmFromIssuer(String issuer) {
        // Issuer format: http://localhost:8080/realms/clinic-123
        if (issuer == null || !issuer.contains("/realms/")) {
            return defaultRealm;
        }
        
        int realmStart = issuer.indexOf("/realms/") + 8;
        int realmEnd = issuer.indexOf("/", realmStart);
        
        if (realmEnd == -1) {
            return issuer.substring(realmStart);
        } else {
            return issuer.substring(realmStart, realmEnd);
        }
    }
    
    public void clearCache() {
        decoders.clear();
    }
    
    public void clearCacheForRealm(String realm) {
        decoders.remove(realm);
    }
    
    // Setter methods for dependency injection
    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }
    
    public void setKeycloakBaseUrl(String keycloakBaseUrl) {
        this.keycloakBaseUrl = keycloakBaseUrl;
    }
    
    public void setMultiTenantEnabled(boolean multiTenantEnabled) {
        this.multiTenantEnabled = multiTenantEnabled;
    }
    
    public void setDefaultRealm(String defaultRealm) {
        this.defaultRealm = defaultRealm;
    }
}