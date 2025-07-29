# ClinicX Project Memory

## Project Overview
ClinicX is a multi-tenant SaaS clinic management system using Spring Boot and Keycloak.

## Architecture Decisions
- **Multi-tenancy**: One Keycloak realm per tenant approach
- **Authentication**: Each tenant has their own realm (e.g., `clinic-smile-dental`)
- **Database**: PostgreSQL with tenant_id column for data isolation
- **Frontend**: Subdomain-based tenant identification (e.g., `smile-dental.clinicx.com`)

## Recent Work
- Implemented Keycloak 26 user profile configuration for custom attributes
- Added protocol mappers for tenant_id, clinic_name, and clinic_type in JWT tokens
- Created comprehensive multi-tenant authentication documentation

## Keycloak Configuration
- Version: Keycloak 26
- User attributes are configured via User Profile (new in v26)
- Protocol mappers ensure attributes appear in JWT tokens
- Each realm has two clients:
  - `clinicx-backend`: Confidential client with generated secret
  - `clinicx-frontend`: Public client for frontend authentication

## Important Commands
- Run with H2: `./mvnw spring-boot:run -Dspring.profiles.active=h2`
- Run with PostgreSQL: `./mvnw spring-boot:run -Dspring.profiles.active=postgres`
- Run tests: `./mvnw test`

## Code Patterns
- Use Java records for DTOs
- Use MapStruct for entity-to-DTO mapping
- Use JPA Specifications for complex queries
- Follow the patient module implementation as reference

## TODO
- Copy the client with secret from template when creating new tenants
- Update all modules based on patient module patterns
- Complete database migrations for all modules