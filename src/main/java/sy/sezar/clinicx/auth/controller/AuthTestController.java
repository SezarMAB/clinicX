package sy.sezar.clinicx.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sy.sezar.clinicx.core.security.SecurityUtils;
import sy.sezar.clinicx.core.tenant.TenantContext;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Test controller to verify Keycloak authentication and authorization.
 * This controller can be removed in production.
 */
@RestController
@RequestMapping("/api/auth/test")
@Tag(name = "Authentication Test", description = "Endpoints to test authentication and authorization")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class AuthTestController {

    @GetMapping("/public")
    @Operation(summary = "Public endpoint", description = "This endpoint is accessible without authentication")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint - no authentication required");
        response.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authenticated")
    @Operation(summary = "Authenticated endpoint", description = "Requires valid JWT token")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Map<String, Object>> authenticatedEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You are authenticated!");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        // Add JWT claims if available
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            response.put("jwt_claims", jwt.getClaims());
        }
        
        // Add tenant info
        response.put("current_tenant", TenantContext.getCurrentTenant());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @Operation(summary = "Admin only endpoint", description = "Requires ADMIN role")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have ADMIN access!");
        response.put("user", SecurityUtils.getCurrentUsername().orElse("Unknown"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor")
    @Operation(summary = "Doctor only endpoint", description = "Requires DOCTOR role")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires DOCTOR role")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, String>> doctorEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have DOCTOR access!");
        response.put("user", SecurityUtils.getCurrentUsername().orElse("Unknown"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/staff")
    @Operation(summary = "Staff only endpoint", description = "Requires STAFF role")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires STAFF role")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Map<String, String>> staffEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have STAFF access!");
        response.put("user", SecurityUtils.getCurrentUsername().orElse("Unknown"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/multi-role")
    @Operation(summary = "Multi-role endpoint", description = "Requires either ADMIN or DOCTOR role")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN or DOCTOR role")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Map<String, String>> multiRoleEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have either ADMIN or DOCTOR access!");
        response.put("user", SecurityUtils.getCurrentUsername().orElse("Unknown"));
        response.put("isAdmin", String.valueOf(SecurityUtils.hasRole("ADMIN")));
        response.put("isDoctor", String.valueOf(SecurityUtils.hasRole("DOCTOR")));
        return ResponseEntity.ok(response);
    }
}