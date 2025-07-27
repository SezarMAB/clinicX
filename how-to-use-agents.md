How Subagents Work

1. Automatic Activation: Subagents activate automatically when you're working on tasks that match their expertise. You don't need to explicitly call them.
2. Proactive Assistance: When marked with "Use PROACTIVELY", the agent will jump in to help before you ask, recognizing patterns in your work.

Usage Examples

1. Creating a New API Endpoint

When you type: "Create an endpoint to search treatments by date range"
- spring-api-architect will help design the REST controller with proper DTOs
- jpa-data-architect will create the repository query
- dto-mapper-specialist will set up MapStruct mappings

2. Adding Keycloak Authentication

When you type: "Integrate Keycloak authentication"
- spring-security-architect will configure Spring Security with Keycloak
- Provide SecurityConfig, JWT decoder, and role mapping

3. Creating a Financial Feature

When you type: "Add installment payment feature"
- financial-module-architect will design payment entities and workflows
- flyway-migration-expert will create the database migrations
- spring-test-engineer will generate comprehensive tests

4. Implementing Dental Features

When you type: "Create tooth chart visualization endpoint"
- dental-domain-expert will validate terminology and workflow
- jpa-data-architect will optimize the dental chart queries

Best Practices

1. Be Specific: Instead of "fix the search", say "optimize patient search to handle 10k+ records"
2. Mention Context: "Add appointment conflict detection for multi-practitioner clinic"
3. Request Reviews: "Review this invoice calculation for financial accuracy"
4. Chain Tasks: Subagents work together automatically
   "Create a complete payment module with:
- REST endpoints for payment processing
- Database schema for installments
- Integration tests"

Special Commands

You can explicitly invoke a subagent using:
/spring-api-architect design REST API for lab results upload

Workflow Example

You: "I need to add a feature for tracking treatment materials with inventory"

What happens:
1. spring-api-architect designs the API structure
2. jpa-data-architect creates entities and repositories
3. dental-domain-expert validates the clinical workflow
4. dto-mapper-specialist creates the mappings
5. flyway-migration-expert generates migrations
6. spring-test-engineer writes tests

The subagents collaborate automatically to deliver a complete, well-architected solution following Spring Boot best practices and your project's patterns.

