# Database Documentation

This folder contains comprehensive documentation for ClinicX database architecture and implementation strategies.

## Document Structure

### Core Architecture
- [01-architecture-overview.md](./01-architecture-overview.md) - Complete database architecture including resource requirements and scaling strategies

### Implementation Guides
- [02-schema-per-tenant-implementation.md](./02-schema-per-tenant-implementation.md) - Step-by-step implementation guide for schema-based multi-tenancy
- [03-kubernetes-multi-instance-architecture.md](./03-kubernetes-multi-instance-architecture.md) - Running multiple backend instances in Kubernetes with single database
- [04-migration-strategy.md](./04-migration-strategy.md) - Migration from current state to schema-per-tenant
- [05-connection-pooling.md](./05-connection-pooling.md) - Database connection pooling strategies
- [06-backup-recovery.md](./06-backup-recovery.md) - Backup and disaster recovery procedures

### Performance & Scaling
- [07-performance-tuning.md](./07-performance-tuning.md) - PostgreSQL performance optimization
- [08-monitoring-metrics.md](./08-monitoring-metrics.md) - Key metrics and monitoring setup
- [09-sharding-strategy.md](./09-sharding-strategy.md) - Database sharding for 500+ tenants

### Security
- [10-security-policies.md](./10-security-policies.md) - Row-level security and access policies
- [11-audit-compliance.md](./11-audit-compliance.md) - Audit trails and compliance requirements

## Quick Reference

### Current State
- Single PostgreSQL database
- Logical tenant isolation (planned)
- One Keycloak realm per tenant

### Target Architecture
- Schema-per-tenant within single database
- 200-500 concurrent tenants capacity
- Kubernetes auto-scaling with multiple backend instances
- Single database with dynamic schema switching
- Optional sharding for 1000+ tenants

### Key Decisions
1. **Schema-per-tenant** chosen over database-per-tenant for better resource utilization
2. **PostgreSQL** as primary database for ACID compliance
3. **Hibernate multi-tenancy** with SCHEMA strategy
4. **PgBouncer** for connection pooling at scale
5. **Stateless backend pods** - any pod can handle any tenant request
6. **Dynamic schema switching** - schema set per request based on tenant context

## Navigation Guide

Start with:
1. **Architecture Overview** - Understand the complete picture
2. **Implementation Guide** - Follow step-by-step implementation
3. **Kubernetes Multi-Instance** - Learn how to scale with multiple pods
4. **Migration Strategy** - Plan your migration path
5. **Performance Tuning** - Optimize for your workload

## Contributing

When adding new documentation:
- Use sequential numbering (XX-topic-name.md)
- Include practical examples
- Add to this README index
- Keep documents focused on single topics