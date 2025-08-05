# Connection Pooling Strategy for Multi-Tenant Architecture

## Overview

This document outlines connection pooling strategies for ClinicX's schema-per-tenant architecture, covering application-level pooling, database-level pooling, and optimization techniques.

## Connection Pool Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Application Layer                      │
├─────────────────────────────────────────────────────────┤
│  Pod 1         Pod 2         Pod 3         Pod N        │
│  HikariCP      HikariCP      HikariCP      HikariCP     │
│  (20 conn)     (20 conn)     (20 conn)     (20 conn)    │
└────┬──────────────┬──────────────┬──────────────┬──────┘
     │              │              │              │
     └──────────────┴──────────────┴──────────────┘
                          │
                          ▼
              ┌───────────────────────┐
              │      PgBouncer       │
              │  (Connection Pooler) │
              │   Transaction Mode   │
              └──────────┬───────────┘
                         │
                         ▼
              ┌───────────────────────┐
              │     PostgreSQL       │
              │   max_connections:   │
              │        500           │
              └─────────────────────┘
```

## Application-Level Pooling (HikariCP)

### 1. Basic Configuration

```yaml
# application.yml
spring:
  datasource:
    hikari:
      # Connection pool sizing
      maximum-pool-size: 20
      minimum-idle: 5
      
      # Connection timeout settings
      connection-timeout: 30000        # 30 seconds
      idle-timeout: 600000            # 10 minutes
      max-lifetime: 1800000           # 30 minutes
      
      # Validation
      validation-timeout: 5000         # 5 seconds
      connection-test-query: "SELECT 1"
      
      # Pool behavior
      pool-name: "ClinicX-Pool"
      register-mbeans: true
      auto-commit: false              # Important for schema switching
      
      # Schema switching optimization
      data-source-properties:
        prepareThreshold: 0           # Disable prepared statement caching
        prepareSql: false
```

### 2. Dynamic Pool Sizing

```java
@Configuration
@ConfigurationProperties(prefix = "app.datasource")
@Slf4j
public class DynamicPoolConfiguration {
    
    @Value("${app.tenant.expected-count:100}")
    private int expectedTenantCount;
    
    @Value("${app.tenant.concurrent-factor:0.2}")
    private double concurrentFactor;
    
    @Bean
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // Dynamic pool sizing based on expected load
        int poolSize = calculateOptimalPoolSize();
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(Math.max(5, poolSize / 4));
        
        // Configure for schema switching
        config.setAutoCommit(false);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        // Important: Disable prepared statement caching for schema switching
        config.addDataSourceProperty("cachePrepStmts", "false");
        config.addDataSourceProperty("prepStmtCacheSize", "0");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "0");
        
        log.info("Configured HikariCP with pool size: {}", poolSize);
        
        return new HikariDataSource(config);
    }
    
    private int calculateOptimalPoolSize() {
        // Formula: (expected_concurrent_tenants * queries_per_tenant) + buffer
        int concurrentTenants = (int) (expectedTenantCount * concurrentFactor);
        int queriesPerTenant = 2; // Average concurrent queries
        int buffer = 5; // Safety buffer
        
        return Math.min(50, concurrentTenants * queriesPerTenant + buffer);
    }
}
```

### 3. Per-Tenant Connection Management

```java
@Component
@Slf4j
public class TenantConnectionManager {
    
    @Autowired
    private HikariDataSource dataSource;
    
    private final Map<String, Integer> tenantConnectionCount = new ConcurrentHashMap<>();
    
    public Connection getConnection(String tenantId) throws SQLException {
        // Track connections per tenant
        tenantConnectionCount.merge(tenantId, 1, Integer::sum);
        
        try {
            Connection connection = dataSource.getConnection();
            setSchemaForTenant(connection, tenantId);
            return new TenantAwareConnection(connection, tenantId, this);
        } catch (SQLException e) {
            tenantConnectionCount.merge(tenantId, -1, Integer::sum);
            throw e;
        }
    }
    
    public void releaseConnection(String tenantId) {
        tenantConnectionCount.merge(tenantId, -1, Integer::sum);
    }
    
    private void setSchemaForTenant(Connection connection, String tenantId) throws SQLException {
        String schema = resolveSchema(tenantId);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET search_path TO " + schema + ", public");
        }
    }
    
    // Monitoring method
    public Map<String, Integer> getConnectionDistribution() {
        return new HashMap<>(tenantConnectionCount);
    }
}
```

## Database-Level Pooling (PgBouncer)

### 1. PgBouncer Configuration

```ini
# pgbouncer.ini
[databases]
# Main database configuration
clinicx = host=postgres-primary.db.svc.cluster.local port=5432 dbname=clinicx

# Read replica for reporting (optional)
clinicx_read = host=postgres-replica.db.svc.cluster.local port=5432 dbname=clinicx

[pgbouncer]
# Connection pooling mode
pool_mode = transaction

# Pool sizing
default_pool_size = 25
min_pool_size = 10
reserve_pool_size = 5
reserve_pool_timeout = 3

# Connection limits
max_client_conn = 1000
max_db_connections = 100

# Timeouts
server_idle_timeout = 600
server_lifetime = 3600
server_connect_timeout = 15
query_timeout = 0
query_wait_timeout = 120
client_idle_timeout = 0
client_login_timeout = 60

# Authentication
auth_type = md5
auth_file = /etc/pgbouncer/userlist.txt

# Logging
log_connections = 1
log_disconnections = 1
log_pooler_errors = 1
stats_period = 60

# Performance
tcp_keepalive = 1
tcp_keepcnt = 3
tcp_keepidle = 30
tcp_keepintvl = 10

# Admin access
admin_users = pgbouncer_admin
stats_users = pgbouncer_stats
```

### 2. Kubernetes Deployment

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: pgbouncer-config
data:
  pgbouncer.ini: |
    [databases]
    clinicx = host=postgres-service port=5432 dbname=clinicx
    
    [pgbouncer]
    pool_mode = transaction
    max_client_conn = 1000
    default_pool_size = 25
    # ... rest of configuration
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pgbouncer
spec:
  replicas: 2  # HA configuration
  selector:
    matchLabels:
      app: pgbouncer
  template:
    metadata:
      labels:
        app: pgbouncer
    spec:
      containers:
      - name: pgbouncer
        image: pgbouncer/pgbouncer:1.19.0
        ports:
        - containerPort: 6432
          name: pgbouncer
        env:
        - name: DATABASES_HOST
          value: "postgres-service"
        - name: DATABASES_PORT
          value: "5432"
        - name: DATABASES_DATABASE
          value: "clinicx"
        - name: POOL_MODE
          value: "transaction"
        volumeMounts:
        - name: config
          mountPath: /etc/pgbouncer
          readOnly: true
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          tcpSocket:
            port: 6432
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          tcpSocket:
            port: 6432
          initialDelaySeconds: 5
          periodSeconds: 5
      volumes:
      - name: config
        configMap:
          name: pgbouncer-config
---
apiVersion: v1
kind: Service
metadata:
  name: pgbouncer-service
spec:
  selector:
    app: pgbouncer
  ports:
  - port: 6432
    targetPort: 6432
  type: ClusterIP
```

## Connection Pool Optimization

### 1. Pool Sizing Formula

```java
@Component
public class PoolSizeCalculator {
    
    public PoolSizeRecommendation calculateOptimalPoolSize(
            int expectedConcurrentTenants,
            double avgQueriesPerSecondPerTenant,
            double avgQueryDurationSeconds) {
        
        // Little's Law: L = λ * W
        // L = number of connections needed
        // λ = arrival rate (queries/second)
        // W = average time in system (seconds)
        
        double totalQPS = expectedConcurrentTenants * avgQueriesPerSecondPerTenant;
        double connectionsNeeded = totalQPS * avgQueryDurationSeconds;
        
        // Add safety factor
        double safetyFactor = 1.5;
        int recommendedPoolSize = (int) Math.ceil(connectionsNeeded * safetyFactor);
        
        // Apply bounds
        int minPoolSize = 10;
        int maxPoolSize = 50; // Per application instance
        
        return PoolSizeRecommendation.builder()
            .recommendedSize(Math.max(minPoolSize, Math.min(maxPoolSize, recommendedPoolSize)))
            .calculatedSize(connectionsNeeded)
            .totalQPS(totalQPS)
            .build();
    }
}
```

### 2. Connection Pool Monitoring

```java
@Component
@Slf4j
public class ConnectionPoolMonitor {
    
    @Autowired
    private HikariDataSource dataSource;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Scheduled(fixedDelay = 30000) // Every 30 seconds
    public void publishMetrics() {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
        
        // Pool metrics
        meterRegistry.gauge("db.pool.total", poolMXBean.getTotalConnections());
        meterRegistry.gauge("db.pool.active", poolMXBean.getActiveConnections());
        meterRegistry.gauge("db.pool.idle", poolMXBean.getIdleConnections());
        meterRegistry.gauge("db.pool.waiting", poolMXBean.getThreadsAwaitingConnection());
        
        // Connection acquisition
        meterRegistry.timer("db.pool.acquisition.time")
            .record(Duration.ofMillis(poolMXBean.getConnectionAcquisition97thPercentile()));
        
        // Health check
        if (poolMXBean.getThreadsAwaitingConnection() > 0) {
            log.warn("Threads waiting for connection: {}", 
                poolMXBean.getThreadsAwaitingConnection());
        }
    }
    
    @EventListener
    public void handleConnectionAcquisitionEvent(ConnectionAcquisitionEvent event) {
        if (event.getAcquisitionTime() > 1000) { // More than 1 second
            log.warn("Slow connection acquisition: {} ms for tenant: {}", 
                event.getAcquisitionTime(), event.getTenantId());
        }
    }
}
```

### 3. Dynamic Pool Adjustment

```java
@Component
@Slf4j
public class DynamicPoolAdjuster {
    
    @Autowired
    private HikariDataSource dataSource;
    
    @Autowired
    private ConnectionPoolMonitor monitor;
    
    @Scheduled(fixedDelay = 300000) // Every 5 minutes
    public void adjustPoolSize() {
        HikariPoolMXBean pool = dataSource.getHikariPoolMXBean();
        
        double utilizationRate = (double) pool.getActiveConnections() / pool.getTotalConnections();
        int waitingThreads = pool.getThreadsAwaitingConnection();
        
        HikariConfig config = dataSource.getHikariConfig();
        int currentMax = config.getMaximumPoolSize();
        
        if (utilizationRate > 0.8 && waitingThreads > 0) {
            // Increase pool size
            int newSize = Math.min(currentMax + 5, 50);
            if (newSize != currentMax) {
                config.setMaximumPoolSize(newSize);
                log.info("Increased pool size from {} to {}", currentMax, newSize);
            }
        } else if (utilizationRate < 0.3 && currentMax > 20) {
            // Decrease pool size
            int newSize = Math.max(currentMax - 5, 20);
            config.setMaximumPoolSize(newSize);
            log.info("Decreased pool size from {} to {}", currentMax, newSize);
        }
    }
}
```

## Schema Switching Optimization

### 1. Connection Wrapper

```java
public class SchemaAwareConnection implements Connection {
    
    private final Connection delegate;
    private final String currentSchema;
    private boolean schemaSet = false;
    
    public SchemaAwareConnection(Connection delegate, String schema) {
        this.delegate = delegate;
        this.currentSchema = schema;
    }
    
    private void ensureSchema() throws SQLException {
        if (!schemaSet) {
            try (Statement stmt = delegate.createStatement()) {
                stmt.execute("SET search_path TO " + currentSchema + ", public");
                schemaSet = true;
            }
        }
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        ensureSchema();
        return delegate.createStatement();
    }
    
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        ensureSchema();
        return delegate.prepareStatement(sql);
    }
    
    // Delegate other methods...
}
```

### 2. Connection Pool per Schema (Alternative)

```java
@Configuration
public class PerSchemaPoolConfiguration {
    
    private final Map<String, HikariDataSource> schemaPools = new ConcurrentHashMap<>();
    
    public DataSource getDataSourceForSchema(String schema) {
        return schemaPools.computeIfAbsent(schema, this::createPoolForSchema);
    }
    
    private HikariDataSource createPoolForSchema(String schema) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/clinicx?currentSchema=" + schema);
        config.setMaximumPoolSize(5); // Smaller pool per schema
        config.setMinimumIdle(1);
        config.setPoolName("Pool-" + schema);
        
        return new HikariDataSource(config);
    }
}
```

## Performance Best Practices

### 1. Connection Lifecycle

```java
@Service
public class TenantQueryService {
    
    @Autowired
    private TenantConnectionManager connectionManager;
    
    public <T> T executeInTenantContext(String tenantId, ConnectionCallback<T> callback) {
        Connection connection = null;
        try {
            connection = connectionManager.getConnection(tenantId);
            connection.setAutoCommit(false);
            
            T result = callback.doInConnection(connection);
            
            connection.commit();
            return result;
            
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Rollback failed", ex);
                }
            }
            throw new DataAccessException("Query failed for tenant: " + tenantId, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Connection close failed", e);
                }
            }
        }
    }
}
```

### 2. Statement Caching

```java
@Configuration
public class StatementCacheConfiguration {
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // PostgreSQL specific optimizations
        config.addDataSourceProperty("prepareThreshold", "0"); // Disable server-side prepare
        config.addDataSourceProperty("preparedStatementCacheQueries", "0");
        config.addDataSourceProperty("preparedStatementCacheSizeMiB", "0");
        
        // Use client-side statement pooling instead
        config.addDataSourceProperty("statementPoolingCacheSize", "256");
        
        return new HikariDataSource(config);
    }
}
```

## Monitoring and Alerts

### 1. Grafana Dashboard Queries

```sql
-- Active connections by schema
SELECT 
    schemaname,
    COUNT(*) as active_connections
FROM pg_stat_activity
WHERE state = 'active'
GROUP BY schemaname
ORDER BY active_connections DESC;

-- Connection wait time
SELECT 
    extract(epoch from (now() - query_start)) as wait_seconds,
    query,
    state,
    wait_event_type,
    wait_event
FROM pg_stat_activity
WHERE wait_event IS NOT NULL
ORDER BY wait_seconds DESC;

-- Pool efficiency
SELECT 
    numbackends as total_connections,
    (SELECT setting::int FROM pg_settings WHERE name = 'max_connections') as max_connections,
    round(100.0 * numbackends / (SELECT setting::int FROM pg_settings WHERE name = 'max_connections'), 2) as usage_percent
FROM pg_stat_database
WHERE datname = 'clinicx';
```

### 2. Alert Configuration

```yaml
alerts:
  - name: ConnectionPoolExhaustion
    expr: db_pool_waiting > 0
    for: 1m
    severity: warning
    annotations:
      summary: "Connection pool has waiting threads"
      
  - name: HighConnectionUsage
    expr: (db_pool_active / db_pool_total) > 0.9
    for: 5m
    severity: critical
    annotations:
      summary: "Connection pool usage above 90%"
      
  - name: DatabaseConnectionLimit
    expr: (pg_stat_database_numbackends / pg_settings_max_connections) > 0.8
    for: 5m
    severity: warning
    annotations:
      summary: "Database approaching connection limit"
```

## Troubleshooting

### Common Issues

1. **Connection Timeout**
   ```java
   // Solution: Increase timeout or pool size
   hikari:
     connection-timeout: 60000  # Increase to 60 seconds
     maximum-pool-size: 30     # Increase pool size
   ```

2. **Schema Not Set**
   ```java
   // Solution: Ensure schema is set before queries
   @Transactional
   public void ensureSchemaSet(Connection conn, String schema) {
       conn.createStatement().execute("SET search_path TO " + schema);
   }
   ```

3. **Pool Exhaustion**
   ```java
   // Solution: Add PgBouncer or increase pool size
   // Monitor with: dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
   ```

## Conclusion

Effective connection pooling for schema-per-tenant requires:
- Properly sized application pools (HikariCP)
- Optional database pooling (PgBouncer) for scale
- Monitoring and dynamic adjustment
- Careful schema switching optimization
- Regular performance analysis