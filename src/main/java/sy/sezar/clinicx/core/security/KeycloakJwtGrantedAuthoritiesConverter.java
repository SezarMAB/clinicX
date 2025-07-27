package sy.sezar.clinicx.core.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Converts Keycloak JWT roles to Spring Security authorities.
 * Extracts roles from both realm_access and resource_access claims.
 */
public class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String CLIENT_ID = "clinicx-backend";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Extract realm roles
        authorities.addAll(extractRealmRoles(jwt));
        
        // Extract client roles
        authorities.addAll(extractClientRoles(jwt));
        
        // Extract tenant information (preparation for multi-tenancy)
        extractTenantAuthority(jwt).ifPresent(authorities::add);

        return authorities;
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS_CLAIM);
        if (realmAccess == null || !realmAccess.containsKey(ROLES_CLAIM)) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get(ROLES_CLAIM);
        
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
            .collect(Collectors.toList());
    }

    private Collection<GrantedAuthority> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);
        if (resourceAccess == null || !resourceAccess.containsKey(CLIENT_ID)) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
        if (clientAccess == null || !clientAccess.containsKey(ROLES_CLAIM)) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) clientAccess.get(ROLES_CLAIM);
        
        return roles.stream()
            .filter(role -> role.startsWith("FEATURE_"))
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toList());
    }

    private Optional<GrantedAuthority> extractTenantAuthority(Jwt jwt) {
        String tenantId = jwt.getClaimAsString("tenant_id");
        if (tenantId != null && !tenantId.isEmpty()) {
            return Optional.of(new SimpleGrantedAuthority("TENANT_" + tenantId));
        }
        return Optional.empty();
    }
}