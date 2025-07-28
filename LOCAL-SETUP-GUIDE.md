# Local Development Setup Guide for ClinicX with Angular

This guide walks you through setting up the complete ClinicX stack locally with multi-tenant Keycloak authentication.

## Prerequisites

- Docker and Docker Compose
- Java 17+
- Node.js 18+ and npm
- Angular CLI (`npm install -g @angular/cli`)
- Maven

## Step 1: Start Infrastructure Services

### 1.1 Start Keycloak and PostgreSQL

```bash
# Start Keycloak and PostgreSQL
docker-compose up -d

# Verify services are running
docker ps

# Check Keycloak logs
docker logs -f keycloak
```

Wait for Keycloak to start completely. You should see:
```
Running the server in development mode. DO NOT use this configuration in production.
```

### 1.2 Access Keycloak Admin Console

1. Open browser: http://localhost:18081
2. Login with:
   - Username: `admin`
   - Password: `admin`

## Step 2: Start Spring Boot Backend

### 2.1 Configure Application

Ensure your `application.yml` has the correct Keycloak URL:

```yaml
keycloak:
  auth-server-url: http://localhost:18081
  admin-username: admin
  admin-password: admin
```

### 2.2 Start the Backend

```bash
# Using H2 database for development
./mvnw spring-boot:run -Dspring.profiles.active=h2

# Or using PostgreSQL
./mvnw spring-boot:run -Dspring.profiles.active=postgres
```

The backend will start on http://localhost:8080

## Step 3: Create a Test Tenant

### 3.1 Create Tenant via API

```bash
# Create a test tenant "Smile Dental"
curl -X POST http://localhost:8080/api/v1/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smile Dental Clinic",
    "subdomain": "smile-dental",
    "contactEmail": "admin@smile-dental.com",
    "contactPhone": "+1234567890",
    "address": "123 Main St, City, Country",
    "adminUsername": "admin",
    "adminEmail": "admin@smile-dental.com",
    "adminFirstName": "John",
    "adminLastName": "Doe",
    "adminPassword": "Admin123!",
    "subscriptionPlan": "PREMIUM",
    "maxUsers": 50,
    "maxPatients": 1000
  }'
```

Save the response - it contains important information like `tenantId` and `realmName`.

### 3.2 Verify in Keycloak

1. Go to Keycloak Admin Console
2. You should see a new realm: `clinic-smile-dental`
3. Check that the realm has:
   - Roles: ADMIN, DOCTOR, NURSE, RECEPTIONIST, ACCOUNTANT
   - Client: `clinicx-frontend` (public)
   - Client: `clinicx-backend` (confidential)
   - User: `admin` with tenant attributes

## Step 4: Set Up Angular Frontend

### 4.1 Create/Configure Angular App

If you don't have an Angular app yet:

```bash
# Create new Angular app
ng new clinicx-frontend --routing --style=scss
cd clinicx-frontend

# Install Keycloak adapter
npm install keycloak-angular keycloak-js
```

### 4.2 Configure Keycloak in Angular

Create `src/app/init/keycloak-init.factory.ts`:

```typescript
import { KeycloakService } from 'keycloak-angular';

export function initializeKeycloak(keycloak: KeycloakService) {
  // Extract subdomain from URL
  const hostname = window.location.hostname;
  const subdomain = hostname.split('.')[0];
  
  // For localhost development, use a default or from environment
  const realm = hostname === 'localhost' 
    ? 'clinic-smile-dental'  // Default for local dev
    : `clinic-${subdomain}`;

  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:18081',
        realm: realm,
        clientId: 'clinicx-frontend'
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html'
      },
      shouldAddToken: (request) => {
        // Add token to API requests
        const { method, url } = request;
        const isApiCall = url.includes('localhost:8080/api');
        return isApiCall;
      }
    });
}
```

### 4.3 Update App Module

```typescript
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { initializeKeycloak } from './init/keycloak-init.factory';

@NgModule({
  imports: [
    KeycloakAngularModule,
    // ... other imports
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    }
  ]
})
export class AppModule { }
```

### 4.4 Create Auth Guard

```typescript
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected readonly router: Router,
    protected readonly keycloak: KeycloakService
  ) {
    super(router, keycloak);
  }

  async isAccessAllowed(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    if (!this.authenticated) {
      await this.keycloak.login({
        redirectUri: window.location.origin + state.url
      });
    }

    const requiredRoles = route.data['roles'];
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    return requiredRoles.every((role: string) => this.roles.includes(role));
  }
}
```

### 4.5 Create API Service

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080/api/v1';

  constructor(
    private http: HttpClient,
    private keycloak: KeycloakService
  ) {}

  // Get current user's tenant info from token
  getTenantInfo() {
    const tokenParsed = this.keycloak.getKeycloakInstance().tokenParsed;
    return {
      tenantId: tokenParsed?.['tenant_id'],
      clinicName: tokenParsed?.['clinic_name'],
      clinicType: tokenParsed?.['clinic_type']
    };
  }

  // Example API calls
  getPatients(): Observable<any> {
    return this.http.get(`${this.apiUrl}/patients`);
  }

  getAppointments(): Observable<any> {
    return this.http.get(`${this.apiUrl}/appointments`);
  }
}
```

### 4.6 Update Angular Routes

```typescript
const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'patients',
        component: PatientsComponent,
        data: { roles: ['ADMIN', 'DOCTOR', 'NURSE'] }
      },
      {
        path: 'appointments',
        component: AppointmentsComponent,
        data: { roles: ['ADMIN', 'DOCTOR', 'RECEPTIONIST'] }
      }
    ]
  }
];
```

## Step 5: Run Everything Together

### 5.1 Start All Services

```bash
# Terminal 1: Infrastructure
docker-compose up -d

# Terminal 2: Backend
./mvnw spring-boot:run -Dspring.profiles.active=h2

# Terminal 3: Frontend
cd clinicx-frontend
ng serve
```

### 5.2 Access the Application

1. Open http://localhost:4200
2. You'll be redirected to Keycloak login
3. Login with:
   - Username: `admin`
   - Password: `Admin123!`
4. After login, you'll be redirected back to Angular app
5. Check browser console for JWT token with tenant information

## Step 6: Test Multi-Tenancy

### 6.1 Create Another Tenant

```bash
curl -X POST http://localhost:8080/api/v1/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Happy Teeth Clinic",
    "subdomain": "happy-teeth",
    "contactEmail": "admin@happy-teeth.com",
    "contactPhone": "+1234567890",
    "address": "456 Oak St, City, Country",
    "adminUsername": "admin",
    "adminEmail": "admin@happy-teeth.com",
    "adminFirstName": "Jane",
    "adminLastName": "Smith",
    "adminPassword": "Admin123!",
    "subscriptionPlan": "BASIC",
    "maxUsers": 10,
    "maxPatients": 200
  }'
```

### 6.2 Simulate Different Tenants

For local development, you can:

1. **Update your hosts file** (recommended for testing):
   ```bash
   # Add to /etc/hosts (Mac/Linux) or C:\Windows\System32\drivers\etc\hosts (Windows)
   127.0.0.1   smile-dental.localhost
   127.0.0.1   happy-teeth.localhost
   ```

2. **Access different tenants**:
   - http://smile-dental.localhost:4200
   - http://happy-teeth.localhost:4200

3. **Or modify keycloak-init.factory.ts** for development:
   ```typescript
   // Add URL parameter support for testing
   const urlParams = new URLSearchParams(window.location.search);
   const tenantParam = urlParams.get('tenant');
   
   const realm = tenantParam 
     ? `clinic-${tenantParam}`
     : hostname === 'localhost' 
       ? 'clinic-smile-dental'
       : `clinic-${subdomain}`;
   ```
   
   Then access: http://localhost:4200?tenant=happy-teeth

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure your Spring Boot CORS configuration includes `http://localhost:4200`
   - Check Keycloak client Web Origins settings

2. **Token Not Included in API Calls**
   - Verify Angular HTTP interceptor is configured
   - Check KeycloakService initialization

3. **Tenant Attributes Missing in Token**
   - Verify protocol mappers are configured in Keycloak
   - Check user has tenant attributes set
   - Try logging out and back in

### Debug Tips

1. **Check JWT Token Contents**:
   ```typescript
   // In Angular component
   constructor(private keycloak: KeycloakService) {
     const token = this.keycloak.getKeycloakInstance().tokenParsed;
     console.log('Token claims:', token);
     console.log('Tenant ID:', token?.['tenant_id']);
   }
   ```

2. **Enable Keycloak Debug Logging**:
   ```typescript
   Keycloak.prototype.logInfo = console.info;
   Keycloak.prototype.logWarn = console.warn;
   Keycloak.prototype.logError = console.error;
   ```

3. **Check Backend Logs**:
   - Spring Security debug: Add to `application.yml`:
     ```yaml
     logging:
       level:
         org.springframework.security: DEBUG
     ```

## Next Steps

1. Implement proper error handling in Angular
2. Add token refresh logic
3. Create tenant-specific UI components
4. Implement data filtering by tenant
5. Add user management features
6. Set up production deployment configuration