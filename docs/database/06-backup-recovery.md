# Backup and Recovery Strategy for Schema-Per-Tenant

## Overview

This document outlines comprehensive backup and disaster recovery procedures for ClinicX's schema-per-tenant PostgreSQL architecture, ensuring data protection and business continuity.

## Backup Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Backup Strategy                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Continuous Backups          Scheduled Backups              │
│  ┌─────────────────┐        ┌──────────────────┐           │
│  │   WAL Archive   │        │  Daily Full      │           │
│  │   (Real-time)   │        │  (Off-hours)     │           │
│  └────────┬────────┘        └────────┬─────────┘           │
│           │                          │                       │
│           ▼                          ▼                       │
│  ┌─────────────────────────────────────────────┐           │
│  │         Backup Storage (S3/GCS)              │           │
│  │  ├── wal-archive/                            │           │
│  │  ├── daily/                                  │           │
│  │  ├── weekly/                                 │           │
│  │  └── monthly/                                │           │
│  └─────────────────────────────────────────────┘           │
│                                                              │
│  Per-Tenant Backups         System Backups                  │
│  ┌─────────────────┐        ┌──────────────────┐           │
│  │  Schema-level   │        │  Public Schema   │           │
│  │   (On-demand)   │        │  (Continuous)    │           │
│  └─────────────────┘        └──────────────────┘           │
└──────────────────────────────────────────────────────────────┘
```

## Backup Strategies

### 1. Continuous WAL Archiving

#### PostgreSQL Configuration
```ini
# postgresql.conf
wal_level = replica
archive_mode = on
archive_command = 'test ! -f /backup/wal/%f && cp %p /backup/wal/%f'
archive_timeout = 300  # Force WAL switch every 5 minutes

# For cloud storage
archive_command = 'wal-g wal-push %p'
```

#### WAL-G Configuration
```yaml
# /etc/wal-g/config.yaml
WALG_S3_PREFIX: "s3://clinicx-backups/postgres"
AWS_ACCESS_KEY_ID: "your-access-key"
AWS_SECRET_ACCESS_KEY: "your-secret-key"
AWS_REGION: "us-east-1"
WALG_COMPRESSION_METHOD: "brotli"
WALG_DELTA_MAX_STEPS: "5"
PGDATA: "/var/lib/postgresql/data"
```

### 2. Full Database Backups

#### Backup Script
```bash
#!/bin/bash
# backup-database.sh

set -euo pipefail

# Configuration
BACKUP_DIR="/backup/full"
S3_BUCKET="s3://clinicx-backups/full"
DB_NAME="clinicx"
DB_USER="postgres"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_NAME="clinicx_full_${TIMESTAMP}"

# Create backup directory
mkdir -p "${BACKUP_DIR}"

# Perform backup with parallel jobs
echo "Starting full backup: ${BACKUP_NAME}"
pg_dump \
    -h localhost \
    -U ${DB_USER} \
    -d ${DB_NAME} \
    --verbose \
    --format=directory \
    --jobs=4 \
    --file="${BACKUP_DIR}/${BACKUP_NAME}"

# Compress backup
echo "Compressing backup..."
tar -czf "${BACKUP_DIR}/${BACKUP_NAME}.tar.gz" \
    -C "${BACKUP_DIR}" \
    "${BACKUP_NAME}"

# Upload to S3
echo "Uploading to S3..."
aws s3 cp \
    "${BACKUP_DIR}/${BACKUP_NAME}.tar.gz" \
    "${S3_BUCKET}/${BACKUP_NAME}.tar.gz" \
    --storage-class STANDARD_IA

# Cleanup local files
rm -rf "${BACKUP_DIR}/${BACKUP_NAME}"
rm -f "${BACKUP_DIR}/${BACKUP_NAME}.tar.gz"

echo "Backup completed: ${BACKUP_NAME}"
```

### 3. Per-Tenant Schema Backups

#### Individual Schema Backup
```bash
#!/bin/bash
# backup-tenant-schema.sh

TENANT_ID=$1
SCHEMA_NAME=$2
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_NAME="${TENANT_ID}_${TIMESTAMP}"

# Backup specific schema
pg_dump \
    -h localhost \
    -U postgres \
    -d clinicx \
    --schema="${SCHEMA_NAME}" \
    --format=custom \
    --file="/backup/tenants/${BACKUP_NAME}.dump"

# Upload to tenant-specific location
aws s3 cp \
    "/backup/tenants/${BACKUP_NAME}.dump" \
    "s3://clinicx-backups/tenants/${TENANT_ID}/${BACKUP_NAME}.dump"
```

#### Automated Tenant Backup Service
```java
@Service
@Slf4j
public class TenantBackupService {
    
    @Value("${backup.command.path}")
    private String backupScriptPath;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Scheduled(cron = "0 0 3 * * *") // Daily at 3 AM
    public void performScheduledBackups() {
        List<Tenant> tenants = tenantRepository.findAllActive();
        
        tenants.parallelStream()
            .forEach(this::backupTenant);
    }
    
    public BackupResult backupTenant(Tenant tenant) {
        String tenantId = tenant.getTenantId();
        String schemaName = tenant.getDatabaseSchema();
        
        try {
            ProcessBuilder pb = new ProcessBuilder(
                backupScriptPath,
                tenantId,
                schemaName
            );
            
            Process process = pb.start();
            boolean completed = process.waitFor(30, TimeUnit.MINUTES);
            
            if (completed && process.exitValue() == 0) {
                log.info("Backup completed for tenant: {}", tenantId);
                return BackupResult.success(tenantId);
            } else {
                log.error("Backup failed for tenant: {}", tenantId);
                return BackupResult.failure(tenantId, "Process failed");
            }
            
        } catch (Exception e) {
            log.error("Backup error for tenant: {}", tenantId, e);
            return BackupResult.failure(tenantId, e.getMessage());
        }
    }
}
```

### 4. Incremental Backups

#### Using pg_basebackup
```bash
#!/bin/bash
# incremental-backup.sh

# Take base backup
pg_basebackup \
    -h localhost \
    -U replicator \
    -D /backup/base/latest \
    --checkpoint=fast \
    --write-recovery-conf \
    --wal-method=stream \
    --format=tar \
    --gzip \
    --progress \
    --verbose

# Archive with timestamp
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
mv /backup/base/latest /backup/base/backup_${TIMESTAMP}
```

## Recovery Procedures

### 1. Point-in-Time Recovery (PITR)

#### Recovery Configuration
```bash
#!/bin/bash
# pitr-recovery.sh

RECOVERY_TIME=$1  # Format: "2024-01-15 14:30:00"
BACKUP_BASE="/backup/base/latest"
WAL_ARCHIVE="/backup/wal"

# Stop PostgreSQL
systemctl stop postgresql

# Clear data directory
rm -rf /var/lib/postgresql/data/*

# Restore base backup
tar -xzf ${BACKUP_BASE}/base.tar.gz -C /var/lib/postgresql/data/

# Create recovery configuration
cat > /var/lib/postgresql/data/recovery.conf << EOF
restore_command = 'cp ${WAL_ARCHIVE}/%f %p'
recovery_target_time = '${RECOVERY_TIME}'
recovery_target_action = 'promote'
recovery_target_timeline = 'latest'
EOF

# Start PostgreSQL
systemctl start postgresql

# Monitor recovery
tail -f /var/lib/postgresql/data/log/postgresql.log
```

### 2. Single Tenant Recovery

#### Restore Specific Schema
```java
@Service
@Transactional
public class TenantRecoveryService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void restoreTenant(String tenantId, String backupPath) {
        String schemaName = resolveSchemaName(tenantId);
        
        try {
            // 1. Drop existing schema
            dropSchema(schemaName);
            
            // 2. Create new schema
            createSchema(schemaName);
            
            // 3. Restore from backup
            restoreSchema(schemaName, backupPath);
            
            // 4. Verify restoration
            verifyRestoration(schemaName);
            
            // 5. Update tenant status
            updateTenantStatus(tenantId, "ACTIVE");
            
            log.info("Tenant restoration completed: {}", tenantId);
            
        } catch (Exception e) {
            log.error("Tenant restoration failed: {}", tenantId, e);
            throw new RecoveryException("Failed to restore tenant", e);
        }
    }
    
    private void restoreSchema(String schemaName, String backupPath) {
        String command = String.format(
            "pg_restore -h localhost -U postgres -d clinicx --schema=%s %s",
            schemaName, backupPath
        );
        
        executeSystemCommand(command);
    }
}
```

### 3. Disaster Recovery

#### Full Database Recovery
```bash
#!/bin/bash
# disaster-recovery.sh

# Configuration
S3_BACKUP="s3://clinicx-backups/full/latest.tar.gz"
WAL_BACKUP="s3://clinicx-backups/wal/"
RECOVERY_DIR="/recovery"

# Download latest full backup
echo "Downloading full backup..."
aws s3 cp ${S3_BACKUP} ${RECOVERY_DIR}/full_backup.tar.gz

# Extract backup
tar -xzf ${RECOVERY_DIR}/full_backup.tar.gz -C ${RECOVERY_DIR}

# Restore database
pg_restore \
    -h localhost \
    -U postgres \
    -d postgres \
    --create \
    --jobs=4 \
    ${RECOVERY_DIR}/clinicx_full_*

# Sync WAL files for PITR
aws s3 sync ${WAL_BACKUP} /backup/wal/

echo "Disaster recovery completed"
```

## Backup Policies

### 1. Retention Policies

```yaml
backup_retention:
  full_backups:
    daily: 7        # Keep 7 daily backups
    weekly: 4       # Keep 4 weekly backups
    monthly: 12     # Keep 12 monthly backups
    yearly: 5       # Keep 5 yearly backups
    
  wal_archives:
    retention_days: 7
    
  tenant_backups:
    on_demand: 30   # Keep for 30 days
    scheduled: 90   # Keep for 90 days
```

### 2. Backup Schedule

```yaml
schedules:
  system_backup:
    full_backup:
      schedule: "0 2 * * *"      # Daily at 2 AM
      type: "full"
      
  tenant_backups:
    high_priority:
      schedule: "0 */6 * * *"    # Every 6 hours
      tenants: ["enterprise-*"]
      
    standard:
      schedule: "0 3 * * *"      # Daily at 3 AM
      tenants: ["*"]
      
  wal_archive:
    continuous: true
    max_lag: "5 minutes"
```

### 3. Backup Lifecycle Management

```java
@Component
@Slf4j
public class BackupLifecycleManager {
    
    @Autowired
    private S3Client s3Client;
    
    @Value("${backup.bucket}")
    private String backupBucket;
    
    @Scheduled(cron = "0 0 4 * * *") // Daily at 4 AM
    public void cleanupOldBackups() {
        // Daily backups older than 7 days
        deleteBackupsOlderThan("daily/", 7);
        
        // Weekly backups older than 28 days
        deleteBackupsOlderThan("weekly/", 28);
        
        // Monthly backups older than 365 days
        deleteBackupsOlderThan("monthly/", 365);
        
        // Tenant backups based on retention policy
        cleanupTenantBackups();
    }
    
    private void deleteBackupsOlderThan(String prefix, int days) {
        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);
        
        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(backupBucket)
            .prefix(prefix)
            .build();
            
        s3Client.listObjectsV2(request).contents().stream()
            .filter(obj -> obj.lastModified().isBefore(cutoff))
            .forEach(obj -> {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(backupBucket)
                    .key(obj.key())
                    .build());
                log.info("Deleted old backup: {}", obj.key());
            });
    }
}
```

## Monitoring and Verification

### 1. Backup Verification

```java
@Service
@Slf4j
public class BackupVerificationService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Scheduled(cron = "0 0 5 * * SUN") // Weekly on Sunday
    public void verifyBackups() {
        List<BackupRecord> recentBackups = getRecentBackups();
        
        for (BackupRecord backup : recentBackups) {
            verifyBackup(backup);
        }
    }
    
    private void verifyBackup(BackupRecord backup) {
        try {
            // 1. Check backup file exists
            boolean exists = checkBackupExists(backup.getPath());
            
            // 2. Verify backup integrity
            boolean valid = verifyBackupIntegrity(backup.getPath());
            
            // 3. Test restore (on separate instance)
            boolean restorable = testRestore(backup);
            
            // 4. Update verification status
            backup.setVerified(exists && valid && restorable);
            backup.setVerificationDate(Instant.now());
            
            saveBackupRecord(backup);
            
        } catch (Exception e) {
            log.error("Backup verification failed: {}", backup.getId(), e);
            alertOncall("Backup verification failed", e);
        }
    }
}
```

### 2. Recovery Testing

```bash
#!/bin/bash
# test-recovery.sh

# Monthly recovery drill
TENANT_ID="test-tenant"
BACKUP_DATE=$(date -d "yesterday" +%Y%m%d)

# Create test database
createdb clinicx_recovery_test

# Restore backup
pg_restore \
    -h localhost \
    -U postgres \
    -d clinicx_recovery_test \
    /backup/tenants/${TENANT_ID}_${BACKUP_DATE}.dump

# Run verification queries
psql -d clinicx_recovery_test -c "
    SELECT COUNT(*) as patient_count FROM tenant_test.patients;
    SELECT COUNT(*) as appointment_count FROM tenant_test.appointments;
"

# Cleanup
dropdb clinicx_recovery_test
```

### 3. Monitoring Metrics

```yaml
# Prometheus metrics
metrics:
  - name: backup_size_bytes
    type: gauge
    labels: [backup_type, tenant_id]
    
  - name: backup_duration_seconds
    type: histogram
    labels: [backup_type]
    
  - name: backup_last_success_timestamp
    type: gauge
    labels: [backup_type, tenant_id]
    
  - name: backup_failures_total
    type: counter
    labels: [backup_type, reason]
    
  - name: recovery_time_seconds
    type: histogram
    labels: [recovery_type]
```

## Security Considerations

### 1. Backup Encryption

```bash
# Encrypt backup before upload
openssl enc -aes-256-cbc \
    -salt \
    -in backup.tar.gz \
    -out backup.tar.gz.enc \
    -pass file:/etc/backup/encryption.key

# Decrypt for restoration
openssl enc -aes-256-cbc \
    -d \
    -in backup.tar.gz.enc \
    -out backup.tar.gz \
    -pass file:/etc/backup/encryption.key
```

### 2. Access Control

```yaml
# S3 Bucket Policy
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:role/BackupRole"
      },
      "Action": [
        "s3:PutObject",
        "s3:PutObjectAcl"
      ],
      "Resource": "arn:aws:s3:::clinicx-backups/*"
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:role/RestoreRole"
      },
      "Action": [
        "s3:GetObject"
      ],
      "Resource": "arn:aws:s3:::clinicx-backups/*"
    }
  ]
}
```

## Recovery Time Objectives (RTO) and Recovery Point Objectives (RPO)

### Service Level Agreements

| Scenario | RPO | RTO | Method |
|----------|-----|-----|--------|
| Single Tenant Recovery | 5 minutes | 30 minutes | WAL + Schema restore |
| Full Database Recovery | 5 minutes | 2 hours | Full backup + WAL |
| Disaster Recovery | 1 hour | 4 hours | Replicated backup site |
| Point-in-Time Recovery | 5 minutes | 1 hour | Base backup + WAL |

### Recovery Procedures by Priority

```yaml
recovery_priorities:
  critical:
    tenants: ["enterprise-*", "premium-*"]
    rto: 30 minutes
    method: "hot-standby"
    
  high:
    tenants: ["standard-*"]
    rto: 2 hours
    method: "warm-standby"
    
  standard:
    tenants: ["trial-*", "free-*"]
    rto: 4 hours
    method: "cold-backup"
```

## Automation Scripts

### 1. Kubernetes CronJob for Backups

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: postgres-backup
spec:
  schedule: "0 2 * * *"  # Daily at 2 AM
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: postgres-backup
            image: clinicx/postgres-backup:latest
            env:
            - name: PGHOST
              value: postgres-service
            - name: PGDATABASE
              value: clinicx
            - name: AWS_ACCESS_KEY_ID
              valueFrom:
                secretKeyRef:
                  name: backup-credentials
                  key: aws-access-key
            - name: AWS_SECRET_ACCESS_KEY
              valueFrom:
                secretKeyRef:
                  name: backup-credentials
                  key: aws-secret-key
            command:
            - /scripts/backup-database.sh
          restartPolicy: OnFailure
```

### 2. Backup Status Dashboard

```sql
-- Backup monitoring queries
CREATE VIEW backup_status AS
SELECT 
    t.tenant_id,
    t.name as tenant_name,
    b.backup_date,
    b.backup_size,
    b.duration_seconds,
    b.status,
    b.verified,
    NOW() - b.backup_date as age
FROM tenants t
LEFT JOIN backup_records b ON t.tenant_id = b.tenant_id
WHERE b.backup_date = (
    SELECT MAX(backup_date) 
    FROM backup_records 
    WHERE tenant_id = t.tenant_id
)
ORDER BY b.backup_date DESC;
```

## Conclusion

A robust backup and recovery strategy for schema-per-tenant architecture requires:
- Multiple backup methods (full, incremental, WAL)
- Automated backup scheduling and verification
- Granular per-tenant recovery capabilities
- Regular recovery testing
- Clear RTO/RPO objectives
- Comprehensive monitoring and alerting