# Cross-Schema Patient Sharing Architecture

## Executive Summary

This document outlines the architecture for sharing patient data across multiple database schemas in a multi-tenant SaaS healthcare platform where each tenant/specialty operates in its own schema while maintaining data isolation and preventing patient duplication.

## System Overview Diagram

```mermaid
graph TB
    subgraph "Patient Access Layer"
        PP[Patient Portal]
        CA[Clinic Apps]
        API[API Gateway]
    end
    
    subgraph "Authentication Layer"
        KC[Keycloak]
        PR[Patient Realm]
        DR[Dental Realm]
        CR[Clinic Realm]
        AR[Appointment Realm]
    end
    
    subgraph "Data Layer"
        SPS[(Shared Patient Schema)]
        DS[(Dental Schema)]
        CS[(Clinic Schema)]
        AS[(Appointment Schema)]
    end
    
    PP --> API
    CA --> API
    API --> KC
    KC --> PR
    KC --> DR
    KC --> CR
    KC --> AR
    
    API --> SPS
    API --> DS
    API --> CS
    API --> AS
    
    DS -.-> SPS
    CS -.-> SPS
    AS -.-> SPS
    
    style SPS fill:#f9f,stroke:#333,stroke-width:4px
    style PP fill:#bbf,stroke:#333,stroke-width:2px
```

## Current Architecture

### Multi-Tenancy Model
- **Schema per Tenant**: Each tenant has its own database schema
- **Realm per Specialty**: Each specialty (Dental, Clinic, Appointments) has its own Keycloak realm
- **Data Isolation**: Complete data separation between tenants at the schema level

## Proposed Cross-Schema Patient Sharing Architecture

### Core Principles
1. **Single Source of Truth**: One master patient record across all schemas
2. **Data Sovereignty**: Each schema maintains ownership of its specific data
3. **Controlled Access**: Explicit permission required for cross-schema access
4. **Audit Trail**: Complete logging of all cross-schema operations
5. **Performance**: Minimize cross-schema queries through caching and materialization

## Architecture Options

### Option 1: Centralized Patient Master Schema (Recommended)

```mermaid
graph TD
    subgraph "Shared Patient Data Schema"
        PM[Patient Master Table]
        CSV[Cross Schema Visibility]
        SR[Schema Registry]
        PAL[Patient Audit Log]
    end
    
    subgraph "Dental Schema"
        DP[Dental Procedures]
        DC[Dental Charts]
        DX[X-Rays]
        DI[Dental Invoices]
    end
    
    subgraph "Clinic Schema"
        MR[Medical Records]
        LR[Lab Results]
        PR[Prescriptions]
        CI[Clinic Invoices]
    end
    
    subgraph "Appointment Schema"
        AS[Appointment Slots]
        AB[Bookings]
        AR[Reminders]
        AC[Calendar]
    end
    
    PM --> CSV
    CSV --> SR
    
    DP --> PM
    MR --> PM
    AS --> PM
    
    style PM fill:#f9f,stroke:#333,stroke-width:4px
    style CSV fill:#bbf,stroke:#333,stroke-width:2px
```

#### Advantages
- Single source of truth for patient demographics
- Centralized access control
- Easier to maintain data consistency
- Simplified backup and recovery

#### Disadvantages
- Single point of failure (mitigated by replication)
- Requires cross-schema queries
- Network latency for distributed systems

### Option 2: Federated Model with Synchronization

```mermaid
graph TB
    subgraph "Sync Orchestrator"
        SO[Event Stream Processor]
        CR[Conflict Resolver]
        SQ[Sync Queue]
    end
    
    subgraph "Dental Schema"
        DP1[Patients Local]
        DD[Dental Data]
    end
    
    subgraph "Clinic Schema"
        CP1[Patients Local]
        CD[Clinic Data]
    end
    
    subgraph "Appointment Schema"
        AP1[Patients Local]
        AD[Appointment Data]
    end
    
    SO --> CR
    CR --> SQ
    
    SQ <--> DP1
    SQ <--> CP1
    SQ <--> AP1
    
    DP1 --> DD
    CP1 --> CD
    AP1 --> AD
    
    style SO fill:#faa,stroke:#333,stroke-width:2px
```

#### Advantages
- Better performance (local queries)
- Schema autonomy
- Works well in distributed environments

#### Disadvantages
- Complex synchronization logic
- Potential for data conflicts
- Higher storage requirements

### Option 3: Hybrid Approach with Caching

```mermaid
graph TD
    SPM[Shared Patient Master]
    
    subgraph "Cache Layer"
        DC2[Dental Cache]
        CC2[Clinic Cache]
        AC2[Appointment Cache]
    end
    
    subgraph "Schema Layer"
        DS2[Dental Schema]
        CS2[Clinic Schema]
        AS2[Appointment Schema]
    end
    
    SPM --> DC2
    SPM --> CC2
    SPM --> AC2
    
    DC2 --> DS2
    CC2 --> CS2
    AC2 --> AS2
    
    style SPM fill:#f9f,stroke:#333,stroke-width:4px
    style DC2 fill:#ffd,stroke:#333,stroke-width:1px
    style CC2 fill:#ffd,stroke:#333,stroke-width:1px
    style AC2 fill:#ffd,stroke:#333,stroke-width:1px
```

## Patient Journey Flow

```mermaid
journey
    title Patient Cross-Clinic Journey
    section First Visit (Dental)
      Register at Dental Clinic: 5: Patient
      Create Patient Record: 5: Dental Staff
      Store in Shared Schema: 5: System
      Dental Treatment: 5: Dentist
    section Second Visit (Medical)
      Search Existing Patient: 5: Medical Staff
      Request Access: 3: Medical Staff
      Grant Access: 5: Dental Staff
      View Basic Info: 5: Medical Staff
      Add Medical Records: 5: Doctor
    section Patient Portal Access
      Register Portal Account: 5: Patient
      Link to Patient ID: 5: System
      View All Records: 5: Patient
      Share with New Provider: 5: Patient
```

## Data Access Flow Diagram

```mermaid
flowchart LR
    subgraph "Patient Registration"
        NP[New Patient] --> Check{Exists in<br/>Shared DB?}
        Check -->|No| Create[Create New Record]
        Check -->|Yes| Request[Request Access]
        Create --> Store[Store in Shared Schema]
        Request --> Approve{Approved?}
        Approve -->|Yes| Grant[Grant Access]
        Approve -->|No| Deny[Create Duplicate<br/>Prevention Alert]
    end
    
    subgraph "Data Access"
        Grant --> Read[Read Patient Data]
        Store --> Own[Owner Access]
        Own --> Full[Full Data Access]
        Read --> Limited[Limited Data Access]
    end
    
    style Check fill:#ffd,stroke:#333,stroke-width:2px
    style Approve fill:#ffd,stroke:#333,stroke-width:2px
```

## Implementation Strategy

### Phase 1: Foundation (Weeks 1-2)
1. **Create Shared Schema**
   - Design shared_patient_data schema
   - Define access control tables
   - Set up schema registry

2. **Establish Connectivity**
   - Configure Foreign Data Wrappers (PostgreSQL FDW)
   - Set up cross-schema permissions
   - Create connection pooling

### Phase 2: Migration (Weeks 3-4)
1. **Data Migration**
   - Identify and merge duplicate patients
   - Migrate existing patients to shared schema
   - Create reference mappings

2. **Access Control Setup**
   - Define access matrices per specialty
   - Implement Row Level Security (RLS)
   - Set up audit logging

### Phase 3: Integration (Weeks 5-6)
1. **Application Layer**
   - Update repositories for cross-schema queries
   - Implement caching layer
   - Add patient search across schemas

2. **Testing**
   - Performance testing
   - Security testing
   - Data integrity validation

## Technical Components

### 1. Database Layer

#### Shared Schema Structure
```sql
shared_patient_data
├── patients (master table)
├── cross_schema_visibility
├── schema_registry
├── patient_sharing_audit
└── data_access_rules
```

#### Access Methods
- **PostgreSQL FDW**: For direct cross-schema access
- **Materialized Views**: For read-heavy operations
- **Event Streaming**: For real-time synchronization

### 2. Application Layer

#### Service Architecture
```
┌─────────────────────────────────────────┐
│         PatientSharingService           │
├─────────────────────────────────────────┤
│ + findOrCreatePatient()                 │
│ + sharePatientAcrossSchemas()          │
│ + revokeSchemaAccess()                 │
│ + getAccessiblePatients()              │
└─────────────────────────────────────────┘
                    │
    ┌───────────────┼───────────────┐
    ▼               ▼               ▼
┌──────────┐  ┌──────────┐  ┌──────────┐
│ Schema   │  │ Patient  │  │ Access   │
│ Registry │  │ Cache    │  │ Control  │
└──────────┘  └──────────┘  └──────────┘
```

#### Key Components
- **SchemaContextHolder**: Manages current schema context
- **CrossSchemaRepository**: Handles cross-schema queries
- **PatientCacheManager**: Manages patient data caching
- **AuditService**: Logs all cross-schema operations

### 3. Security Layer

```mermaid
sequenceDiagram
    participant User
    participant API
    participant Auth as Keycloak
    participant SPS as Shared Patient Schema
    participant TS as Target Schema
    
    User->>API: Request Patient Data
    API->>Auth: Validate JWT Token
    Auth-->>API: Token Valid + Claims
    API->>SPS: Check Access Rights
    SPS-->>API: Access Level (READ/WRITE/OWNER)
    
    alt Has Access
        API->>TS: Fetch Schema Data
        TS-->>API: Patient Records
        API-->>User: Combined Data
    else No Access
        API-->>User: 403 Forbidden
    end
```

#### Access Control Matrix
| Operation | Owner Schema | Shared Schema | Other Schemas |
|-----------|--------------|---------------|---------------|
| Create Patient | ✅ WRITE | ❌ | ❌ |
| Read Basic Info | ✅ READ | ✅ READ | ✅ READ* |
| Update Demographics | ✅ WRITE | ❌ | ❌ |
| View Medical Records | ✅ READ | ❌ | ❌ |
| Share Patient | ✅ WRITE | ❌ | ❌ |

*With explicit permission

#### Permission Levels
- **OWNER**: Full control, can share with others
- **WRITE**: Can modify shared data categories
- **READ**: Can view permitted data categories
- **NONE**: No access

### 4. Caching Strategy

#### Cache Layers
1. **L1 Cache**: Application-level (Caffeine/Redis)
   - TTL: 5 minutes
   - Scope: Basic patient info

2. **L2 Cache**: Database materialized views
   - Refresh: Every 15 minutes
   - Scope: Frequently accessed patients

3. **L3 Cache**: Schema-local copies
   - Sync: Event-driven
   - Scope: Active patients only

## API Design

### Patient Sharing Endpoints

```yaml
POST /api/v1/patients/share
  Request:
    patientId: UUID
    targetSchema: string
    targetTenant: string
    accessLevel: READ|WRITE
    dataCategories: string[]
    expiresAt: datetime (optional)
    reason: string

GET /api/v1/patients/shared
  Response:
    patients: [
      id: UUID
      name: string
      sharedFrom: string
      accessLevel: string
      dataCategories: string[]
    ]

POST /api/v1/patients/find-or-create
  Request:
    phoneNumber: string
    email: string
    demographics: object
  Response:
    patientId: UUID
    isNew: boolean
    sharedFrom: string (if existing)
```

## Data Governance

### Privacy Considerations
1. **Data Minimization**: Share only necessary data categories
2. **Purpose Limitation**: Access tied to specific use cases
3. **Consent Management**: Track patient consent for sharing
4. **Right to be Forgotten**: Cascade deletion across schemas

### Compliance Requirements
- **HIPAA**: Maintain audit logs for all access
- **GDPR**: Implement data portability and deletion
- **Regional Laws**: Respect data residency requirements

## Performance Considerations

### Query Optimization
1. **Index Strategy**
   - Index on global_patient_id
   - Composite index on (schema, tenant, patient)
   - Partial indexes for active records

2. **Query Patterns**
   ```sql
   -- Efficient: Use indexed global ID
   SELECT * FROM shared_patient_data.patients 
   WHERE global_patient_id = ?
   
   -- Avoid: Cross-schema joins
   SELECT * FROM dental_schema.records r
   JOIN shared_patient_data.patients p ON ...
   ```

3. **Connection Pooling**
   - Separate pools per schema
   - Maximum 10 connections per schema
   - Connection timeout: 30 seconds

### Monitoring Metrics
- Cross-schema query latency
- Cache hit rates
- Failed access attempts
- Data synchronization lag

## Deployment Architecture

```mermaid
graph TB
    subgraph "Production Environment"
        subgraph "Application Tier"
            LB[Load Balancer]
            API1[API Server 1]
            API2[API Server 2]
            API3[API Server 3]
        end
        
        subgraph "Cache Tier"
            RC[Redis Cluster]
            MC[Memcached]
        end
        
        subgraph "Database Tier"
            subgraph "Primary DB"
                MDB[(Master DB)]
                SPS2[(Shared Patient Schema)]
            end
            
            subgraph "Read Replicas"
                RR1[(Replica 1)]
                RR2[(Replica 2)]
            end
            
            subgraph "Tenant Schemas"
                TS1[(Dental Schemas)]
                TS2[(Clinic Schemas)]
                TS3[(Appointment Schemas)]
            end
        end
        
        subgraph "Monitoring"
            PROM[Prometheus]
            GRAF[Grafana]
            ELK[ELK Stack]
        end
    end
    
    LB --> API1
    LB --> API2
    LB --> API3
    
    API1 --> RC
    API2 --> RC
    API3 --> RC
    
    API1 --> MDB
    API2 --> RR1
    API3 --> RR2
    
    MDB --> SPS2
    SPS2 --> TS1
    SPS2 --> TS2
    SPS2 --> TS3
    
    API1 --> PROM
    PROM --> GRAF
    API1 --> ELK
    
    style LB fill:#faa,stroke:#333,stroke-width:2px
    style MDB fill:#f9f,stroke:#333,stroke-width:3px
    style SPS2 fill:#bbf,stroke:#333,stroke-width:3px
```

## Performance Optimization Flow

```mermaid
flowchart TD
    Query[Patient Query Request] --> CacheCheck{In Cache?}
    CacheCheck -->|Yes| ReturnCache[Return Cached Data]
    CacheCheck -->|No| CheckLocal{Local Schema?}
    
    CheckLocal -->|Yes| LocalQuery[Query Local Schema]
    CheckLocal -->|No| CheckShared{In Shared Schema?}
    
    CheckShared -->|Yes| SharedQuery[Query Shared Schema]
    CheckShared -->|No| FederatedQuery[Federated Query]
    
    LocalQuery --> UpdateCache[Update Cache]
    SharedQuery --> UpdateCache
    FederatedQuery --> UpdateCache
    
    UpdateCache --> ReturnData[Return Data]
    ReturnCache --> Complete[Request Complete]
    ReturnData --> Complete
    
    style CacheCheck fill:#ffd,stroke:#333,stroke-width:2px
    style CheckLocal fill:#ffd,stroke:#333,stroke-width:2px
    style CheckShared fill:#ffd,stroke:#333,stroke-width:2px
```

## Disaster Recovery

### Backup Strategy
1. **Shared Schema**: Daily backups with point-in-time recovery
2. **Schema References**: Backup reference mappings
3. **Access Control**: Export permissions weekly

### Failover Plan
1. **Primary Failure**: Promote read replica
2. **Schema Isolation**: Fall back to local data
3. **Cache Warming**: Pre-populate after recovery

## Implementation Roadmap

```mermaid
gantt
    title Cross-Schema Patient Sharing Implementation
    dateFormat  YYYY-MM-DD
    section Phase 1 Foundation
    Shared Schema Design           :done, p1_1, 2025-01-15, 5d
    Access Control Tables          :done, p1_2, after p1_1, 3d
    FDW Configuration             :active, p1_3, after p1_2, 4d
    Schema Registry Setup         :p1_4, after p1_3, 2d
    
    section Phase 2 Migration
    Data Audit & Deduplication    :p2_1, 2025-02-01, 7d
    Patient Data Migration        :p2_2, after p2_1, 5d
    Reference Mapping Creation    :p2_3, after p2_2, 3d
    Validation & Testing          :p2_4, after p2_3, 5d
    
    section Phase 3 Integration
    Service Layer Implementation  :p3_1, 2025-02-20, 10d
    API Endpoints Development     :p3_2, after p3_1, 7d
    Caching Layer Setup          :p3_3, after p3_2, 4d
    Integration Testing          :p3_4, after p3_3, 5d
    
    section Phase 4 Patient Portal
    Portal Authentication        :p4_1, 2025-03-15, 7d
    Record Aggregation Service   :p4_2, after p4_1, 10d
    UI Development              :p4_3, after p4_2, 14d
    Portal Testing              :p4_4, after p4_3, 7d
    
    section Phase 5 Deployment
    Production Setup            :p5_1, 2025-04-15, 3d
    Data Migration Prod         :p5_2, after p5_1, 2d
    Go-Live                    :milestone, p5_3, after p5_2, 0d
    Monitoring & Support        :p5_4, after p5_3, 30d
```

## Migration Plan

### Prerequisites
- PostgreSQL 12+ with FDW support
- Sufficient database permissions
- Application downtime window (4 hours)

### Migration Steps
1. **Preparation** (T-2 weeks)
   - Audit existing patient data
   - Identify duplicates
   - Plan merge strategy

2. **Execution** (Day of migration)
   - Create shared schema
   - Migrate patient data
   - Update application configuration
   - Verify data integrity

3. **Validation** (T+1 week)
   - Monitor performance
   - Check access logs
   - Gather user feedback

## Risks and Mitigation

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Performance degradation | High | Medium | Implement caching, optimize queries |
| Data inconsistency | High | Low | Use transactions, implement validation |
| Security breach | High | Low | Encrypt connections, audit all access |
| Schema drift | Medium | Medium | Automated schema validation |
| Complexity overhead | Medium | High | Comprehensive documentation, training |

## Cost Analysis

### Infrastructure Costs
- Additional database storage: ~20% increase
- Network transfer: Minimal for same-region
- Caching infrastructure: Redis cluster ($200/month)

### Development Costs
- Initial implementation: 3 developers × 6 weeks
- Testing and validation: 2 QA engineers × 4 weeks
- Documentation and training: 1 week

### Operational Costs
- Monitoring and maintenance: 0.5 FTE ongoing
- Regular audits: Quarterly reviews

## Success Criteria

1. **Functional Requirements**
   - ✅ No duplicate patients across schemas
   - ✅ Sub-second patient lookup
   - ✅ Granular access control
   - ✅ Complete audit trail

2. **Non-Functional Requirements**
   - ✅ 99.9% availability
   - ✅ <100ms query latency (p95)
   - ✅ Zero data loss
   - ✅ HIPAA compliance maintained

## Patient Portal Integration (Post-MVP)

### Overview
The patient portal will provide patients with unified access to their medical records across all clinics and specialties they've visited, creating a comprehensive health history view.

### Architecture Design

```mermaid
graph TB
    subgraph "Patient Portal Layer"
        PWA[Patient Web App]
        PMA[Patient Mobile App]
        PAG[Portal API Gateway]
    end
    
    subgraph "Portal Services"
        AS[Auth Service]
        RS[Record Service]
        CS[Consent Service]
        NS[Notification Service]
    end
    
    subgraph "Data Aggregation"
        AGG[Aggregation Engine]
        CACHE[Redis Cache]
        QUEUE[Message Queue]
    end
    
    subgraph "Schema Access"
        FDW1[Dental FDW]
        FDW2[Clinic FDW]
        FDW3[Appointment FDW]
    end
    
    PWA --> PAG
    PMA --> PAG
    PAG --> AS
    PAG --> RS
    PAG --> CS
    PAG --> NS
    
    RS --> AGG
    AGG --> CACHE
    AGG --> QUEUE
    
    AGG --> FDW1
    AGG --> FDW2
    AGG --> FDW3
    
    style PWA fill:#bbf,stroke:#333,stroke-width:2px
    style PMA fill:#bbf,stroke:#333,stroke-width:2px
    style AGG fill:#f9f,stroke:#333,stroke-width:3px
```

### Key Features

#### 1. Patient Authentication
- **Self-Registration**: Using phone/email verification
- **Secure Access**: Multi-factor authentication
- **Account Recovery**: OTP-based password reset
- **Keycloak Realm**: Dedicated `patient-portal` realm

#### 2. Unified Medical Records View
```
Patient Dashboard
├── Personal Information
│   └── Editable demographics
├── Medical Timeline
│   ├── Dental visits
│   ├── Clinical consultations
│   └── Appointments
├── Documents
│   ├── X-rays & Imaging
│   ├── Lab Results
│   ├── Prescriptions
│   └── Invoices
├── Health Summary
│   ├── Allergies
│   ├── Medications
│   └── Conditions
└── Appointment Management
    ├── Upcoming appointments
    ├── Booking requests
    └── Cancellations
```

#### 3. Data Access Control
- **Patient-Initiated Access**: Patients explicitly grant portal access
- **Granular Permissions**: Choose what to share per clinic
- **Time-Limited Sharing**: Temporary access for referrals
- **Audit Trail**: Track who accessed what and when

### Implementation Approach

#### Phase 1: Foundation (Months 1-2)
1. **Patient Identity Management**
   ```yaml
   patient_portal_users:
     - patient_id: UUID (links to shared_patient_data)
     - portal_username: string (email/phone)
     - activated: boolean
     - consent_given: timestamp
     - last_login: timestamp
   ```

2. **Access Token Management**
   ```yaml
   patient_access_tokens:
     - patient_id: UUID
     - clinic_schema: string
     - granted_at: timestamp
     - expires_at: timestamp
     - access_scope: string[] (records, appointments, billing)
   ```

#### Phase 2: Data Aggregation (Months 3-4)
1. **Record Collection Service**
   - Federated queries across schemas
   - Data transformation and normalization
   - Caching for performance

2. **API Design**
   ```yaml
   GET /api/patient-portal/v1/my-records
     Headers:
       Authorization: Bearer {patient_jwt}
     Response:
       records: [
         {
           source: "Smile Dental Clinic"
           type: "dental_procedure"
           date: "2024-01-15"
           details: {...}
         },
         {
           source: "City Medical Center"
           type: "lab_result"
           date: "2024-02-20"
           details: {...}
         }
       ]
   ```

#### Phase 3: Interactive Features (Months 5-6)
1. **Appointment Booking**
   - View available slots across clinics
   - Book appointments directly
   - Manage existing appointments

2. **Document Sharing**
   - Share records with other healthcare providers
   - Generate shareable links with expiration
   - Download medical history reports

### Security Considerations

#### Authentication Flow

```mermaid
stateDiagram-v2
    [*] --> Login
    Login --> OTPSent: Send OTP
    OTPSent --> Verified: Valid OTP
    OTPSent --> Login: Invalid OTP
    Verified --> Authenticated: Generate JWT
    Authenticated --> ConsentCheck: Request Records
    ConsentCheck --> HasConsent: Consent Exists
    ConsentCheck --> RequestConsent: No Consent
    RequestConsent --> PendingApproval: Send to Clinic
    PendingApproval --> HasConsent: Approved
    PendingApproval --> Denied: Rejected
    HasConsent --> DataAccess: Fetch Records
    DataAccess --> DisplayRecords: Success
    Denied --> [*]
    DisplayRecords --> [*]
```

#### Privacy Controls
1. **Data Minimization**: Only show data patient consents to
2. **Access Logging**: Complete audit trail of all access
3. **Encryption**: End-to-end encryption for sensitive data
4. **Session Management**: Auto-logout after inactivity

### Technical Implementation

#### Database Schema Extensions
```sql
-- Patient portal specific tables
CREATE SCHEMA patient_portal;

-- Patient portal accounts
CREATE TABLE patient_portal.accounts (
    id UUID PRIMARY KEY,
    patient_id UUID REFERENCES shared_patient_data.patients(id),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255),
    is_active BOOLEAN DEFAULT FALSE,
    activation_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    last_login TIMESTAMP
);

-- Patient consent records
CREATE TABLE patient_portal.consent_records (
    id UUID PRIMARY KEY,
    patient_id UUID,
    clinic_schema VARCHAR(100),
    consent_type VARCHAR(50), -- 'full', 'appointments_only', 'read_only'
    granted_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP
);

-- Access logs for compliance
CREATE TABLE patient_portal.access_logs (
    id UUID PRIMARY KEY,
    patient_id UUID,
    accessed_schema VARCHAR(100),
    accessed_data_type VARCHAR(50),
    ip_address INET,
    user_agent TEXT,
    accessed_at TIMESTAMP DEFAULT NOW()
);
```

#### API Gateway Configuration
- Rate limiting per patient
- Request validation
- Response caching
- Circuit breaker for schema connections

### Benefits for Patients
1. **Complete Health Picture**: All medical records in one place
2. **Convenience**: No need to request records from multiple clinics
3. **Control**: Decide what information to share and with whom
4. **Accessibility**: Access records 24/7 from anywhere
5. **Portability**: Easy to switch providers or get second opinions

### Benefits for Clinics
1. **Reduced Administrative Load**: Fewer record requests to process
2. **Better Patient Engagement**: Patients more involved in their care
3. **Improved Continuity of Care**: Complete patient history available
4. **Competitive Advantage**: Modern patient experience

### Success Metrics
- Patient portal adoption rate > 60%
- Average session duration > 5 minutes
- Monthly active users > 40%
- Patient satisfaction score > 4.5/5
- Support ticket reduction by 30%

## Conclusion

The centralized patient master schema approach (Option 1) is recommended for its simplicity, maintainability, and strong consistency guarantees. This architecture effectively prevents patient duplication while maintaining strict data isolation between tenants/specialties. The patient portal extension will provide significant value by empowering patients with unified access to their health records across all participating clinics. The implementation should be phased, starting with a proof of concept in a development environment before rolling out to production.

## Appendices

### A. Database Scripts
- Schema creation scripts
- FDW configuration
- RLS policies

### B. API Documentation
- OpenAPI specification
- Integration examples

### C. Security Policies
- Access control matrices
- Encryption standards
- Audit requirements

### D. Performance Benchmarks
- Query performance tests
- Load testing results
- Capacity planning

---

*Document Version: 1.0*  
*Last Updated: 2025-01-12*  
*Author: ClinicX Architecture Team*