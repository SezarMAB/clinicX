# ClinicX - Claude Code Assistant Documentation


## Rules
- Before you do any work, MUST view files in • claude/tasks/context_session_.md file to get the
  full context (x being the id of the session we are operate, if file doesnt exist, then create
  one)
- context_session_x.md should contain most of context of what we did, overall plan, and sub
  agents will continusly add context to the file
- After you finish the work, MUST update the .claude/tasks/context_session_x.md file to make
  sure others can get full context of what you did


## Project Overview

ClinicX is a sophisticated multi-tenant clinic management system built with Spring Boot 3.5.3 and Java 21. The application features comprehensive patient management, appointment scheduling, financial tracking, and dental chart management with enterprise-grade security and multi-tenant architecture.

## Architecture

### Multi-Tenant Architecture
- **Realm-per-Type**: Each specialty type gets its own Keycloak realm
- **Tenant Isolation**: Complete data separation between tenants
- **Dynamic Realm Management**: Automatic realm creation and configuration
- **Tenant Context**: Thread-local tenant resolution and validation

### Security Model
- **Keycloak Integration**: OAuth2/OpenID Connect with JWT tokens
- **Role-Based Access Control**: SUPER_ADMIN > ADMIN > DOCTOR > STAFF
- **Multi-Tenant JWT**: Dynamic JWT validation across multiple realms
- **Access Validation**: Comprehensive tenant access validation at multiple layers

## Key Components

### Core Security (`src/main/java/sy/sezar/clinicx/core/security/`)
- `SecurityConfig.java` - Main security configuration
- `MultiTenantJwtDecoder.java` - Dynamic JWT validation for multiple realms
- `KeycloakJwtGrantedAuthoritiesConverter.java` - JWT claims to Spring authorities conversion

### Tenant Management (`src/main/java/sy/sezar/clinicx/tenant/`)
- `TenantContext.java` - Thread-local tenant storage
- `TenantAuthorizationFilter.java` - Request-level tenant validation
- `TenantAccessValidator.java` - Comprehensive access validation logic
- `KeycloakAdminService.java` - Programmatic Keycloak realm/user management

### Domain Modules
- **Clinic Management**: Staff, specialties, clinic information
- **Patient Management**: Patient records, appointments, treatments
- **Financial Module**: Invoicing, payments, advance payments
- **Dental Module**: Dental charts with JSONB tooth condition tracking

## Development Guidelines

### Running the Application

#### Local Development (H2)
```bash
SPRING_PROFILES_ACTIVE=h2 ./gradlew bootRun
```

#### Local Development (PostgreSQL)
```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

#### Realm-per-Type Architecture
```bash
SPRING_PROFILES_ACTIVE=h2,realm-per-type APP_DOMAIN=example.com ./gradlew bootRun
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "sy.sezar.clinicx.security.KeycloakSecurityIntegrationTest"
```

### Database Management
- **Flyway Migrations**: `src/main/resources/db/migration/`
- **H2 Migrations**: `src/main/resources/db/migration-h2/`
- **Demo Data**: `src/main/resources/db/local_demo_data/`

## Security Best Practices

### Authentication & Authorization
- All endpoints require JWT authentication except public endpoints
- Multi-tenant access validation on every request
- Role-based method security using `@PreAuthorize`
- Tenant isolation enforced at filter level

### Data Protection
- Parameterized queries only (no SQL injection risk)
- Comprehensive input validation using Bean Validation
- No sensitive data in logs
- Proper error handling without information disclosure

### Multi-Tenant Security
- Tenant context validation on every request
- JWT-based tenant access claims validation
- Database fallback for access validation
- Audit logging for security events

## API Documentation

### Swagger/OpenAPI
- Available at: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

### Key Endpoints
- **Authentication Test**: `/api/auth/test/*`
- **Patient Management**: `/api/v1/patients/*`
- **Staff Management**: `/api/v1/staff/*`
- **Tenant Management**: `/api/tenants/*` (Super Admin only)
- **Tenant Switching**: `/api/v1/tenant-switch/*`

## Common Development Tasks

### Adding New Entities
1. Create JPA entity in appropriate model package
2. Add repository interface extending `JpaRepository`
3. Create DTOs for API requests/responses
4. Implement MapStruct mapper
5. Add service layer with validation
6. Create REST controller with proper security annotations
7. Add Flyway migration script

### Security Configuration
- Tenant-specific endpoints: Use `@RequiresTenant` annotation
- Role-based access: Use `@PreAuthorize("hasRole('ROLE_NAME')")`
- Public endpoints: Add to excluded paths in `SecurityConfig`

### Multi-Tenant Development
- Always use `TenantContext.getCurrentTenant()` for tenant-aware operations
- Validate tenant access using `TenantAccessValidator`
- Add tenant auditing for sensitive operations
- Test tenant isolation thoroughly

## Configuration

### Environment Variables
- `TENANT_MODE`: single|multi (default: single)
- `MULTI_TENANT_ENABLED`: true|false (default: true)
- `REALM_PER_TYPE`: true|false (default: false)
- `APP_DOMAIN`: Domain for tenant subdomains
- `KEYCLOAK_ISSUER_URI`: Keycloak issuer URL
- `DEFAULT_TENANT_ID`: Default tenant identifier

### Profiles
- `h2`: H2 database for development
- `local`: PostgreSQL for local development
- `postgres`: PostgreSQL for production
- `realm-per-type`: Enable realm-per-type architecture

## Troubleshooting

### Common Issues

#### Tenant Context Not Set
- Ensure `TenantInterceptor` is properly configured
- Check JWT token contains valid tenant claims
- Verify tenant exists and is active

#### Authentication Failures
- Validate Keycloak realm configuration
- Check JWT token expiration
- Verify client configuration in Keycloak

#### Database Connection Issues
- Check datasource configuration in active profile
- Ensure database is running and accessible
- Verify Flyway migration status

### Security Testing
- Use provided HTTP client files in `security_testing/`
- Test multi-tenant isolation with different JWT tokens
- Validate role-based access control
- Check CORS configuration for frontend integration

## Documentation References

See `docs/` directory for detailed documentation:
- `MULTI-TENANT-SECURITY-ANALYSIS.md` - Security architecture analysis
- `KEYCLOAK-SETUP.md` - Keycloak configuration guide
- `LOCAL-SETUP-GUIDE.md` - Local development setup
- `api-architecture/` - API design patterns and guidelines

## Security Review Status

**Last Security Review**: 2025-01-14
**Security Score**: 8.5/10
**Key Strengths**: 
- Excellent multi-tenant security isolation
- Comprehensive JWT authentication
- Strong input validation
- No SQL injection vulnerabilities

**Recommendations**:
- Implement additional HTTP security headers
- Add rate limiting for authentication endpoints
- Expand security test coverage

## Maintenance Commands

### Gradle Tasks
```bash
# Build project
./gradlew build

# Run tests
./gradlew test

# Check dependencies
./gradlew dependencies

# Generate test report
./gradlew test jacocoTestReport
```

### Docker Operations
```bash
# Start infrastructure
cd docker-traefik-arch && ./start-dev.sh

# Stop infrastructure
docker-compose down

# View logs
docker-compose logs -f keycloak
```

---

*This document is maintained by Claude Code Assistant. Last updated: 2025-01-14*
