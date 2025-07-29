# Angular 18 JWT Implementation Prompt for ClinicX

## Context
I have an Angular 18 frontend application that needs to integrate with a Spring Boot backend secured with Keycloak. The backend uses JWT tokens for authentication and supports multi-tenant SaaS architecture.

## Keycloak Configuration
- Keycloak URL: `http://localhost:18081`
- Realm: `clinicx-dev`
- Client ID: `clinicx-backend`
- Client Secret: `tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk`
- Token Endpoint: `http://localhost:18081/realms/clinicx-dev/protocol/openid-connect/token`
- Backend API: `http://localhost:8080`

## Requirements

### 1. Create a Secure Authentication Service
Create an Angular service that implements these security best practices:
- Store tokens in memory (not localStorage) for maximum security
- Implement automatic token refresh 30 seconds before expiration
- Handle token expiration gracefully
- Clear tokens on logout
- Implement refresh token rotation
- Add activity timeout (15 minutes of inactivity = auto logout)

### 2. Create HTTP Interceptor
Implement an interceptor that:
- Adds Bearer token to all API requests
- Handles 401 errors by attempting token refresh
- Queues failed requests during token refresh
- Redirects to login on refresh failure
- Excludes Keycloak endpoints from token injection

### 3. Create Auth Guard
Implement guards that:
- Protect routes requiring authentication
- Support role-based access (ADMIN, DOCTOR, STAFF)
- Check token validity before allowing access
- Handle deep linking after login

### 4. Create Login Component
Build a login component that:
- Uses reactive forms with validation
- Shows loading states during authentication
- Handles error messages appropriately
- Supports "Remember Me" (using sessionStorage instead of localStorage)
- Implements CSRF protection

### 5. Token Storage Strategy
Implement a secure token manager:
- Primary: In-memory storage (survives route changes, not page refresh)
- Optional: Encrypted sessionStorage for "Remember Me"
- Never use localStorage for medical data application
- Implement secure token serialization

### 6. Models and Interfaces
Create TypeScript interfaces for:
```typescript
interface TokenResponse {
  access_token: string;
  expires_in: number;
  refresh_expires_in: number;
  refresh_token: string;
  token_type: string;
  session_state?: string;
  scope?: string;
}

interface JwtPayload {
  sub: string;
  exp: number;
  iat: number;
  preferred_username: string;
  email?: string;
  realm_access?: { roles: string[] };
  resource_access?: { [key: string]: { roles: string[] } };
  tenant_id?: string;
}

interface User {
  id: string;
  username: string;
  email?: string;
  roles: string[];
  tenantId?: string;
}
```

### 7. Activity Monitoring
Implement user activity detection:
- Reset timeout on user interactions
- Warn user before auto-logout (2 minutes before)
- Save work before logout
- Clear sensitive data from memory

### 8. Security Headers
Configure Angular to send appropriate headers:
- Content-Type for token requests
- X-Requested-With for CSRF protection
- Avoid storing sensitive data in browser storage

### 9. Error Handling
Implement comprehensive error handling:
- Network errors
- Invalid credentials
- Token expiration
- Keycloak server errors
- Rate limiting responses

### 10. Module Structure
```
src/app/
├── auth/
│   ├── services/
│   │   ├── auth.service.ts
│   │   ├── token-manager.service.ts
│   │   └── activity-monitor.service.ts
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   ├── role.guard.ts
│   │   └── guest.guard.ts
│   ├── interceptors/
│   │   ├── auth.interceptor.ts
│   │   └── error.interceptor.ts
│   ├── components/
│   │   ├── login/
│   │   ├── logout/
│   │   └── session-timeout-warning/
│   ├── models/
│   │   └── auth.models.ts
│   └── auth.module.ts
```

### 11. Testing Requirements
Include unit tests for:
- Token refresh logic
- Interceptor behavior
- Guard functionality
- Service methods
- Activity timeout

### 12. Additional Security Features
- Implement device fingerprinting (optional)
- Add login attempt limiting
- Log security events
- Implement secure password reset flow
- Add two-factor authentication support (future)

## Code Examples Needed

### 1. Secure Token Manager
```typescript
// Example structure
class SecureTokenManager {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private tokenExpiry: Date | null = null;
  
  // Methods for secure token handling
}
```

### 2. Auto-Refresh Implementation
```typescript
// Example structure
private scheduleTokenRefresh(expiresIn: number): void {
  // Refresh 30 seconds before expiration
  const refreshTime = (expiresIn - 30) * 1000;
  // Implementation details
}
```

### 3. Queue for Failed Requests
```typescript
// Example structure
private refreshTokenSubject = new BehaviorSubject<string | null>(null);
private requestQueue: Array<() => Observable<any>> = [];
```

## Important Notes
1. Never log tokens to console
2. Implement proper CSP headers
3. Use HTTPS in production
4. Sanitize all user inputs
5. Implement rate limiting on login attempts
6. Add audit logging for security events
7. Consider implementing WebAuthn for passwordless authentication in the future

## Expected Deliverables
1. Complete auth module with all services, guards, and interceptors
2. Secure token management implementation
3. Activity monitoring with auto-logout
4. Comprehensive error handling
5. Unit tests with at least 80% coverage
6. Documentation for security best practices
7. Example environment configuration

Please implement this with a focus on security, user experience, and maintainability. The medical nature of the application requires the highest security standards.