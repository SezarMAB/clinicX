# Angular-Spring Boot Integration Guide

## Issue
Your Angular app is making requests to `http://localhost:4200/api/v1/patients/search` instead of the Spring Boot backend at `http://localhost:8080`, causing 401 Unauthorized errors.

## Solution

### 1. Configure Angular Proxy

The `angular-proxy-config.json` file has been created in the project root. To use it:

**In your Angular project's `angular.json`:**
```json
"serve": {
  "builder": "@angular-devkit/build-angular:dev-server",
  "options": {
    "browserTarget": "your-app:build",
    "proxyConfig": "proxy.conf.json"
  }
}
```

**Create `proxy.conf.json` in your Angular project root:**
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  },
  "/swagger-ui": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  },
  "/v3/api-docs": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

### 2. Start Angular with Proxy

```bash
ng serve --proxy-config proxy.conf.json
```

Or if configured in `angular.json`:
```bash
ng serve
```

### 3. JWT Token Issues

The JWT token from your request shows:
- **Issuer**: `http://localhost:18081/realms/master`
- **Client**: `clickx-frontend` (public client)
- **Audience**: Includes multiple realms

The Spring Boot backend expects:
- Tokens from `clinicx-backend` client (for backend-to-backend)
- Or tokens that include `clinicx-backend` in the audience

### 4. Keycloak Client Configuration

You need to ensure your Keycloak clients are configured correctly:

#### Frontend Client (clickx-frontend)
- **Access Type**: public
- **Valid Redirect URIs**: `http://localhost:4200/*`
- **Web Origins**: `http://localhost:4200`

#### Backend Client (clinicx-backend)
- **Access Type**: confidential
- **Valid Redirect URIs**: `http://localhost:8080/*`
- **Web Origins**: `+` (to allow all origins from Valid Redirect URIs)

### 5. Update Angular Service Configuration

Ensure your Angular service includes the backend in the audience:

```typescript
// In your Keycloak initialization
const keycloakConfig = {
  url: 'http://localhost:18081',
  realm: 'master',
  clientId: 'clickx-frontend'
};

// When getting token, ensure it includes backend audience
const initOptions = {
  onLoad: 'login-required',
  checkLoginIframe: false,
  enableLogging: true,
  // Request token with backend audience
  scope: 'openid profile email',
  // Add this to include backend in audience
  responseMode: 'query'
};
```

### 6. Alternative: Resource Server Configuration

Update the Spring Boot configuration to accept tokens from the frontend client:

```yaml
# application-dev.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          audiences:
            - clickx-frontend
            - clinicx-backend
            - account
```

### 7. Testing the Integration

1. **Clear browser cache and cookies**
2. **Login again** to get a fresh token
3. **Check Network tab** to ensure requests go to `localhost:8080`
4. **Verify token** contains proper audience

### 8. Debug Tips

Enable debug logging to see JWT validation details:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
    sy.sezar.clinicx.core.security: DEBUG
```

Check Spring Boot logs for specific JWT validation errors.

### 9. Common Issues and Solutions

**Issue**: 401 Unauthorized
- Check token expiration
- Verify issuer matches configuration
- Ensure audience includes expected client

**Issue**: CORS errors
- Verify Spring Boot CORS configuration includes `http://localhost:4200`
- Check browser console for specific CORS headers missing

**Issue**: Proxy not working
- Ensure Angular dev server is using the proxy config
- Check console for proxy debug messages
- Verify target URL is correct

## Quick Test

After setting up the proxy, test with:

```bash
# This should now work (proxied to localhost:8080)
curl http://localhost:4200/api/v1/patients/search \
  -H "Authorization: Bearer YOUR_TOKEN"
```