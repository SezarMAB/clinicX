# Database Documentation

This folder contains comprehensive documentation for ClinicX database architecture and implementation strategies.

## Document Structure

### Core Architecture
- [01-architecture-overview.md](./01-architecture-overview.md) - Complete database architecture including resource requirements and scaling strategies

### Implementation Guides
- [02-schema-per-tenant-implementation.md](./02-schema-per-tenant-implementation.md) - Step-by-step implementation guide for schema-based multi-tenancy
- [03-migration-strategy.md](./03-migration-strategy.md) - Migration from current state to schema-per-tenant
- [04-connection-pooling.md](./04-connection-pooling.md) - Database connection pooling strategies
- [05-backup-recovery.md](./05-backup-recovery.md) - Backup and disaster recovery procedures

### Performance & Scaling
- [06-performance-tuning.md](./06-performance-tuning.md) - PostgreSQL performance optimization
- [07-monitoring-metrics.md](./07-monitoring-metrics.md) - Key metrics and monitoring setup
- [08-sharding-strategy.md](./08-sharding-strategy.md) - Database sharding for 500+ tenants

### Security
- [09-security-policies.md](./09-security-policies.md) - Row-level security and access policies
- [10-audit-compliance.md](./10-audit-compliance.md) - Audit trails and compliance requirements

## Quick Reference

### Current State
- Single PostgreSQL database
- Logical tenant isolation (planned)
- One Keycloak realm per tenant

### Target Architecture
- Schema-per-tenant within single database
- 200-500 concurrent tenants capacity
- Kubernetes auto-scaling
- Optional sharding for 1000+ tenants

### Key Decisions
1. **Schema-per-tenant** chosen over database-per-tenant for better resource utilization
2. **PostgreSQL** as primary database for ACID compliance
3. **Hibernate multi-tenancy** with SCHEMA strategy
4. **PgBouncer** for connection pooling at scale

## Navigation Guide

Start with:
1. **Architecture Overview** - Understand the complete picture
2. **Implementation Guide** - Follow step-by-step implementation
3. **Migration Strategy** - Plan your migration path
4. **Performance Tuning** - Optimize for your workload

## Contributing

When adding new documentation:
- Use sequential numbering (XX-topic-name.md)
- Include practical examples
- Add to this README index
- Keep documents focused on single topics