# Audit Events Guide for ClinicX

## Table of Contents
1. [Current State](#current-state)
2. [Event Types](#event-types)
3. [Accessing Audit Events](#accessing-audit-events)
4. [Database Schema](#database-schema)
5. [Implementation Roadmap](#implementation-roadmap)
6. [Best Practices](#best-practices)

## Current State

The ClinicX application uses `TenantAuditServiceImpl` to generate audit events for tenant-related security and administrative actions. Currently, these events are:

- **Logged to console** via SLF4J at INFO/WARN levels
- **Published as Spring events** but not persisted
- **Not stored in the database** despite having an `audit_log` table available

### Architecture Overview

```
User Action → TenantAuditService → 
    ├── SLF4J Logger (Console/File)
    └── ApplicationEventPublisher (Spring Events)
        └── [No Listeners Currently]
```

## Event Types

### 1. TenantAccessEvent
Tracks tenant access attempts and results.

| Event | Log Level | Description |
|-------|-----------|-------------|
| ACCESS_GRANTED | INFO | User successfully accessed a tenant resource |
| ACCESS_DENIED | WARN | User was denied access to a tenant resource |

**Log Format:**
```
ACCESS_GRANTED: User={username}, Tenant={tenantId}, Resource={resource}, Time={timestamp}
ACCESS_DENIED: User={username}, Tenant={tenantId}, Resource={resource}, Reason={reason}, Time={timestamp}
```

### 2. TenantSwitchEvent
Records when users switch between tenants.

| Event | Log Level | Description |
|-------|-----------|-------------|
| TENANT_SWITCH | INFO | User switched from one tenant to another |

**Log Format:**
```
TENANT_SWITCH: User={username}, From={fromTenantId}, To={toTenantId}, Time={timestamp}
```

### 3. TenantManagementEvent
Captures tenant lifecycle operations.

| Event | Log Level | Description |
|-------|-----------|-------------|
| TENANT_CREATED | INFO | New tenant was created |
| TENANT_MODIFIED | INFO | Tenant configuration was modified |

**Log Format:**
```
TENANT_CREATED: User={username}, TenantId={tenantId}, TenantName={tenantName}, Time={timestamp}
TENANT_MODIFIED: User={username}, TenantId={tenantId}, Changes={changes}, Time={timestamp}
```

### 4. UserTenantEvent
Tracks user-tenant relationship changes.

| Event | Log Level | Description |
|-------|-----------|-------------|
| USER_ADDED_TO_TENANT | INFO | User granted access to tenant |
| USER_REMOVED_FROM_TENANT | INFO | User access revoked from tenant |

**Log Format:**
```
USER_ADDED_TO_TENANT: Admin={adminUsername}, User={userId}, Tenant={tenantId}, Role={role}, Time={timestamp}
USER_REMOVED_FROM_TENANT: Admin={adminUsername}, User={userId}, Tenant={tenantId}, Time={timestamp}
```

## Accessing Audit Events

### Method 1: Console Logs (Current)

When running the application, audit events appear in the console output:

```bash
# Start application with local profile
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun

# View audit events in real-time
# They appear with DEBUG level for sy.sezar.clinicx package
```

### Method 2: File Logging (Configuration Required)

Add to `application.yml` or `application-local.yml`:

```yaml
logging:
  file:
    name: logs/clinicx.log
    path: logs/
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30
  level:
    sy.sezar.clinicx.tenant.service.impl.TenantAuditServiceImpl: INFO
```

Then access logs:
```bash
# View all audit events
grep -E "ACCESS_|TENANT_|USER_" logs/clinicx.log

# View only access denials
grep "ACCESS_DENIED" logs/clinicx.log

# Follow audit events in real-time
tail -f logs/clinicx.log | grep -E "ACCESS_|TENANT_|USER_"
```

### Method 3: Database Query (Not Yet Implemented)

Once implemented, you would query:
```sql
-- View recent audit events
SELECT * FROM audit_log 
WHERE changed_at > NOW() - INTERVAL '24 hours'
ORDER BY changed_at DESC;

-- View events for specific tenant
SELECT * FROM audit_log 
WHERE new_values->>'tenantId' = 'tenant-001'
ORDER BY changed_at DESC;
```

## Database Schema

### Existing `audit_log` Table

The database already has an `audit_log` table (created in V2__add_advanced_features.sql):

```sql
CREATE TABLE audit_log (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    table_name  VARCHAR(50)  NOT NULL,
    record_id   UUID         NOT NULL,
    action      VARCHAR(20)  NOT NULL,  -- INSERT, UPDATE, DELETE
    changed_by  UUID         REFERENCES staff (id) ON DELETE SET NULL,
    changed_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    ip_address  INET,
    user_agent  TEXT,
    old_values  JSONB,
    new_values  JSONB,
    details     TEXT
);

-- Indexes for performance
CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at DESC);
CREATE INDEX idx_audit_log_changed_by ON audit_log(changed_by);
```

### Proposed `tenant_audit_log` Table

For tenant-specific audit events:

```sql
CREATE TABLE tenant_audit_log (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type    VARCHAR(50) NOT NULL,  -- ACCESS_GRANTED, ACCESS_DENIED, etc.
    event_name    VARCHAR(100) NOT NULL, -- Human-readable event name
    username      VARCHAR(255),
    user_id       VARCHAR(255),
    tenant_id     VARCHAR(100),
    resource      TEXT,
    action        VARCHAR(50),
    reason        TEXT,
    ip_address    INET,
    user_agent    TEXT,
    event_data    JSONB,                 -- Flexible field for event-specific data
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for querying
CREATE INDEX idx_tenant_audit_event_type ON tenant_audit_log(event_type);
CREATE INDEX idx_tenant_audit_tenant_id ON tenant_audit_log(tenant_id);
CREATE INDEX idx_tenant_audit_user_id ON tenant_audit_log(user_id);
CREATE INDEX idx_tenant_audit_created_at ON tenant_audit_log(created_at DESC);
```

## Implementation Roadmap

### Phase 1: Persist Events to Database

#### Step 1: Create Event Listener

Create `TenantAuditEventListener.java`:

```java
package sy.sezar.clinicx.tenant.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.tenant.event.*;
import sy.sezar.clinicx.tenant.repository.TenantAuditLogRepository;
import sy.sezar.clinicx.tenant.model.TenantAuditLog;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantAuditEventListener {
    
    private final TenantAuditLogRepository auditLogRepository;
    
    @EventListener
    @Transactional
    public void handleTenantAccessEvent(TenantAccessEvent event) {
        TenantAuditLog log = TenantAuditLog.builder()
            .eventType(event.getAccessType().name())
            .eventName("Tenant Access " + event.getAccessType())
            .username(event.getUsername())
            .tenantId(event.getTenantId())
            .resource(event.getResource())
            .reason(event.getReason())
            .createdAt(event.getTimestamp())
            .build();
        
        auditLogRepository.save(log);
        log.debug("Persisted tenant access event: {}", event);
    }
    
    @EventListener
    @Transactional
    public void handleTenantSwitchEvent(TenantSwitchEvent event) {
        TenantAuditLog log = TenantAuditLog.builder()
            .eventType("TENANT_SWITCH")
            .eventName("Tenant Switch")
            .username(event.getUsername())
            .tenantId(event.getToTenantId())
            .eventData(Map.of(
                "fromTenantId", event.getFromTenantId(),
                "toTenantId", event.getToTenantId()
            ))
            .createdAt(event.getTimestamp())
            .build();
        
        auditLogRepository.save(log);
    }
    
    // Similar handlers for TenantManagementEvent and UserTenantEvent
}
```

#### Step 2: Create JPA Entity

Create `TenantAuditLog.java`:

```java
package sy.sezar.clinicx.tenant.model;

import jakarta.persistence.*;
import lombok.*;
import sy.sezar.clinicx.core.model.BaseEntity;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "tenant_audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantAuditLog extends BaseEntity {
    
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "tenant_id", length = 100)
    private String tenantId;
    
    @Column(name = "resource", columnDefinition = "TEXT")
    private String resource;
    
    @Column(name = "action", length = 50)
    private String action;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Type(JsonType.class)
    @Column(name = "event_data", columnDefinition = "jsonb")
    private Map<String, Object> eventData;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
```

### Phase 2: Query Service and REST API

#### Step 1: Create Query Service

```java
package sy.sezar.clinicx.tenant.service;

@Service
@RequiredArgsConstructor
public class TenantAuditQueryService {
    
    private final TenantAuditLogRepository repository;
    
    public Page<TenantAuditLog> getAuditEvents(
            String tenantId, 
            String userId,
            String eventType,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {
        
        Specification<TenantAuditLog> spec = Specification.where(null);
        
        if (tenantId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("tenantId"), tenantId));
        }
        
        if (userId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("userId"), userId));
        }
        
        if (eventType != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("eventType"), eventType));
        }
        
        if (from != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("createdAt"), from));
        }
        
        if (to != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("createdAt"), to));
        }
        
        return repository.findAll(spec, pageable);
    }
}
```

#### Step 2: Create REST Controller

```java
package sy.sezar.clinicx.tenant.controller;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TenantAuditController {
    
    private final TenantAuditQueryService queryService;
    
    @GetMapping("/events")
    public ResponseEntity<Page<TenantAuditLogDto>> getAuditEvents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        
        Page<TenantAuditLog> events = queryService.getAuditEvents(
            tenantId, userId, eventType, from, to, pageable);
        
        return ResponseEntity.ok(events.map(this::toDto));
    }
    
    @GetMapping("/events/export")
    public ResponseEntity<Resource> exportAuditEvents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String format) {
        
        // Export to CSV, JSON, or PDF
        // Implementation details...
    }
}
```

### Phase 3: External Integration

#### Option 1: ELK Stack Integration

```yaml
# logback-spring.xml
<configuration>
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>localhost:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"app":"clinicx","type":"audit"}</customFields>
        </encoder>
    </appender>
    
    <logger name="sy.sezar.clinicx.tenant.service.impl.TenantAuditServiceImpl" level="INFO">
        <appender-ref ref="LOGSTASH"/>
    </logger>
</configuration>
```

#### Option 2: Kafka Event Streaming

```java
@Component
@RequiredArgsConstructor
public class KafkaAuditEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @EventListener
    public void handleAuditEvent(TenantAccessEvent event) {
        kafkaTemplate.send("audit-events", event.getTenantId(), event);
    }
}
```

## Best Practices

### 1. What to Audit

**Always Audit:**
- Authentication attempts (success/failure)
- Authorization failures
- Tenant switching
- User role changes
- Data exports
- Configuration changes
- Administrative actions

**Consider Auditing:**
- Data access patterns
- API usage statistics
- Performance metrics
- Error patterns

### 2. Security Considerations

- **Never log sensitive data** (passwords, SSNs, credit cards)
- **Encrypt audit logs** at rest and in transit
- **Restrict access** to audit logs (admin-only)
- **Implement tamper detection** (checksums, digital signatures)
- **Regular backups** of audit data

### 3. Retention Policies

```java
@Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
public void cleanupOldAuditLogs() {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
    
    // Archive old logs
    List<TenantAuditLog> oldLogs = repository.findByCreatedAtBefore(cutoffDate);
    archiveService.archive(oldLogs);
    
    // Delete archived logs
    repository.deleteByCreatedAtBefore(cutoffDate);
}
```

### 4. Performance Considerations

- **Asynchronous processing** - Use `@Async` for audit operations
- **Batch inserts** - Collect and insert audit logs in batches
- **Proper indexing** - Index frequently queried columns
- **Partitioning** - Partition audit tables by date for large datasets
- **Read replicas** - Query audit logs from read-only replicas

```java
@Configuration
@EnableAsync
public class AuditConfiguration {
    
    @Bean
    public Executor auditExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("audit-");
        executor.initialize();
        return executor;
    }
}
```

### 5. Monitoring and Alerting

Set up alerts for:
- Excessive authorization failures
- Unusual access patterns
- Bulk data exports
- Configuration changes
- System errors in audit logging

```java
@Component
public class AuditAlertService {
    
    @EventListener
    public void checkForAnomalies(TenantAccessEvent event) {
        if (event.getAccessType() == AccessType.DENIED) {
            int recentFailures = countRecentFailures(event.getUsername());
            if (recentFailures > 5) {
                alertService.sendAlert(
                    "Multiple access failures for user: " + event.getUsername()
                );
            }
        }
    }
}
```

## Testing Audit Events

### Manual Testing

```bash
# 1. Start the application
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun

# 2. Perform actions that trigger audit events
# - Login to different tenants
# - Switch between tenants
# - Add/remove users
# - Modify configurations

# 3. Check logs
grep "TENANT_" logs/clinicx.log
grep "ACCESS_" logs/clinicx.log
grep "USER_" logs/clinicx.log

# 4. Query database (once implemented)
psql -d clinic_x -c "SELECT * FROM tenant_audit_log ORDER BY created_at DESC LIMIT 10;"
```

### Automated Testing

```java
@SpringBootTest
@ActiveProfiles("test")
class TenantAuditServiceTest {
    
    @Autowired
    private TenantAuditService auditService;
    
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    
    @Test
    void shouldPublishAccessGrantedEvent() {
        // Given
        String username = "testuser";
        String tenantId = "tenant-001";
        String resource = "/api/patients";
        
        // When
        auditService.auditAccessGranted(username, tenantId, resource);
        
        // Then
        ArgumentCaptor<TenantAccessEvent> captor = 
            ArgumentCaptor.forClass(TenantAccessEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        
        TenantAccessEvent event = captor.getValue();
        assertThat(event.getUsername()).isEqualTo(username);
        assertThat(event.getTenantId()).isEqualTo(tenantId);
        assertThat(event.getAccessType()).isEqualTo(AccessType.GRANTED);
    }
}
```

## Troubleshooting

### Common Issues

1. **Audit events not appearing in logs**
   - Check logging level: `sy.sezar.clinicx.tenant.service.impl.TenantAuditServiceImpl` should be INFO or DEBUG
   - Verify the service is being called
   - Check for exceptions in audit service

2. **Database not storing events**
   - Ensure event listener is registered as Spring component
   - Check transaction boundaries
   - Verify database permissions

3. **Performance degradation**
   - Implement asynchronous processing
   - Add appropriate indexes
   - Consider archiving old data

4. **Missing context information**
   - Ensure SecurityContext is available
   - Pass request context through MDC
   - Include correlation IDs

## References

- [Spring Events Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events)
- [SLF4J Logging](http://www.slf4j.org/manual.html)
- [PostgreSQL JSONB](https://www.postgresql.org/docs/current/datatype-json.html)
- [OWASP Logging Guide](https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html)