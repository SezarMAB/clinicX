# ClinicX - Dental Clinic Management System

A comprehensive Spring Boot 3.x application for dental clinic management, built with PostgreSQL and featuring a complete treatment materials tracking system.

## 🏗️ Architecture

- **Backend**: Spring Boot 3.5.x with Java 21
- **Database**: PostgreSQL with Flyway migrations
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Mapping**: MapStruct for DTO conversions
- **Build Tool**: Gradle

## 🚀 Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 12+
- Gradle 8.0+

### Setup Database
```sql
CREATE DATABASE clinicx;
CREATE USER clinicx_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE clinicx TO clinicx_user;
```

### Application Configuration
Create `src/main/resources/application-local.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinicx
spring.datasource.username=clinicx_user
spring.datasource.password=your_password
```

### Run Application
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Access Swagger UI
Navigate to: http://localhost:8080/swagger-ui.html

## 📊 Database Migration

The application uses Flyway for database migrations:

- **V1**: Initial MVP schema with core functionality
- **V2**: Treatment materials system and UI mockup alignment

Migrations run automatically on startup. To view migration status:
```bash
./gradlew flywayInfo
```

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Tests by Category
```bash
# Unit tests only
./gradlew test --tests "*Test" --exclude-tests "*IntegrationTest"

# Integration tests only  
./gradlew test --tests "*IntegrationTest"

# Specific component tests
./gradlew test --tests "*TreatmentMaterial*"
```

### Test Coverage
The application includes comprehensive test coverage:
- Unit tests with Mockito
- Repository tests with @DataJpaTest
- Controller tests with @WebMvcTest
- Integration tests with Testcontainers

## erDiagram
```mermaid
erDiagram
    clinic_info {
        BOOLEAN id PK "Primary key, always TRUE"
        VARCHAR name "Clinic name"
        TEXT address "Clinic address"
        VARCHAR phone_number "Contact phone"
        VARCHAR email "Contact email"
        VARCHAR timezone "Clinic timezone"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    specialties {
        UUID id PK "Primary key"
        VARCHAR name UK "Unique specialty name"
        TEXT description "Specialty description"
        BOOLEAN is_active "Active status"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    staff {
        UUID id PK "Primary key"
        VARCHAR full_name "Staff member name"
        VARCHAR role "Staff role"
        VARCHAR email UK "Unique email"
        VARCHAR phone_number "Contact phone"
        BOOLEAN is_active "Active status"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    staff_specialties {
        UUID staff_id PK,FK "References staff"
        UUID specialty_id PK,FK "References specialties"
    }
    patients {
        UUID id PK "Primary key"
        VARCHAR public_facing_id UK "Public patient ID"
        VARCHAR full_name "Patient name"
        DATE date_of_birth "DOB"
        VARCHAR gender "Patient gender"
        VARCHAR phone_number "Contact phone"
        VARCHAR email "Email address"
        TEXT address "Home address"
        VARCHAR insurance_provider "Insurance company"
        VARCHAR insurance_number "Insurance ID"
        TEXT important_medical_notes "Medical notes"
        DECIMAL balance "Account balance"
        BOOLEAN is_active "Active status"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
        UUID created_by FK "References staff"
    }
    procedures {
        UUID id PK "Primary key"
        UUID specialty_id FK "References specialties"
        VARCHAR procedure_code UK "Unique procedure code"
        VARCHAR name "Procedure name"
        TEXT description "Procedure description"
        DECIMAL default_cost "Default cost"
        INT default_duration_minutes "Default duration"
        BOOLEAN is_active "Active status"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    appointments {
        UUID id PK "Primary key"
        UUID specialty_id FK "References specialties"
        UUID patient_id FK "References patients"
        UUID doctor_id FK "References staff"
        TIMESTAMPTZ appointment_datetime "Appointment date/time"
        INT duration_minutes "Duration in minutes"
        VARCHAR status "Appointment status"
        TEXT notes "Appointment notes"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
        UUID created_by FK "References staff"
    }
    treatments {
        UUID id PK "Primary key"
        UUID appointment_id FK "References appointments"
        UUID patient_id FK "References patients"
        UUID procedure_id FK "References procedures"
        UUID doctor_id FK "References staff"
        INT tooth_number "Tooth number (11-48)"
        VARCHAR status "Treatment status"
        DECIMAL cost "Treatment cost"
        TEXT treatment_notes "Treatment notes"
        DATE treatment_date "Treatment date"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
        UUID created_by FK "References staff"
    }
    treatment_materials {
        UUID id PK "Primary key"
        UUID treatment_id FK "References treatments"
        VARCHAR material_name "Material name"
        DECIMAL quantity "Quantity used"
        VARCHAR unit "Measurement unit"
        DECIMAL cost_per_unit "Unit cost"
        DECIMAL total_cost "Total cost"
        VARCHAR supplier "Supplier name"
        VARCHAR batch_number "Batch number"
        TEXT notes "Material notes"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    tooth_conditions {
        UUID id PK "Primary key"
        VARCHAR code UK "Unique condition code"
        VARCHAR name "Condition name"
        TEXT description "Condition description"
        VARCHAR color_hex "Display color"
        BOOLEAN is_active "Active status"
        TIMESTAMPTZ created_at "Creation timestamp"
    }
    patient_teeth {
        UUID id PK "Primary key"
        UUID patient_id FK "References patients"
        INT tooth_number "Tooth number (11-48)"
        UUID current_condition_id FK "References tooth_conditions"
        TEXT notes "Tooth notes"
        DATE last_treatment_date "Last treatment date"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    tooth_history {
        UUID id PK "Primary key"
        UUID patient_tooth_id FK "References patient_teeth"
        UUID patient_id FK "References patients"
        INT tooth_number "Tooth number"
        UUID condition_id FK "References tooth_conditions"
        UUID treatment_id FK "References treatments"
        TIMESTAMPTZ change_date "Change date"
        TEXT notes "History notes"
        UUID recorded_by FK "References staff"
        TIMESTAMPTZ created_at "Creation timestamp"
    }
    invoices {
        UUID id PK "Primary key"
        UUID patient_id FK "References patients"
        VARCHAR invoice_number UK "Unique invoice number"
        DATE issue_date "Issue date"
        DATE due_date "Due date"
        DECIMAL total_amount "Total amount"
        VARCHAR status "Invoice status"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
        UUID created_by FK "References staff"
    }
    invoice_items {
        UUID id PK "Primary key"
        UUID invoice_id FK "References invoices"
        UUID treatment_id UK,FK "References treatments"
        VARCHAR description "Item description"
        DECIMAL amount "Item amount"
        TIMESTAMPTZ created_at "Creation timestamp"
    }
    payments {
        UUID id PK "Primary key"
        UUID invoice_id FK "References invoices"
        UUID patient_id FK "References patients"
        DATE payment_date "Payment date"
        DECIMAL amount "Payment amount"
        VARCHAR payment_method "Payment method"
        VARCHAR type "Payment type"
        VARCHAR description "Payment description"
        TIMESTAMPTZ created_at "Creation timestamp"
        UUID created_by FK "References staff"
    }
    lab_requests {
        UUID id PK "Primary key"
        UUID patient_id FK "References patients"
        VARCHAR order_number UK "Unique order number"
        TEXT item_description "Item description"
        INT tooth_number "Tooth number"
        DATE date_sent "Date sent"
        DATE date_due "Due date"
        VARCHAR status "Request status"
        VARCHAR lab_name "Lab name"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    documents {
        UUID id PK "Primary key"
        UUID patient_id FK "References patients"
        UUID uploaded_by_staff_id FK "References staff"
        VARCHAR file_name "File name"
        TEXT file_path "File path"
        BIGINT file_size_bytes "File size"
        VARCHAR mime_type "MIME type"
        VARCHAR type "Document type"
        TIMESTAMPTZ created_at "Creation timestamp"
    }
    notes {
        UUID id PK "Primary key"
        UUID patient_id FK "References patients"
        TEXT content "Note content"
        UUID created_by FK "References staff"
        TIMESTAMPTZ note_date "Note date"
        TIMESTAMPTZ created_at "Creation timestamp"
        TIMESTAMPTZ updated_at "Last update timestamp"
    }
    staff ||--o{ staff_specialties : "has"
    specialties ||--o{ staff_specialties : "assigned to"
    staff ||--o{ patients : "creates"
    staff ||--o{ appointments : "performs"
    staff ||--o{ appointments : "creates"
    staff ||--o{ treatments : "performs"
    staff ||--o{ treatments : "creates"
    staff ||--o{ tooth_history : "records"
    staff ||--o{ invoices : "creates"
    staff ||--o{ payments : "creates"
    staff ||--o{ documents : "uploads"
    staff ||--o{ notes : "creates"
    specialties ||--o{ procedures : "contains"
    specialties ||--o{ appointments : "for"
    patients ||--o{ appointments : "has"
    patients ||--o{ treatments : "receives"
    patients ||--o{ patient_teeth : "has"
    patients ||--o{ tooth_history : "has"
    patients ||--o{ invoices : "billed"
    patients ||--o{ payments : "makes"
    patients ||--o{ lab_requests : "has"
    patients ||--o{ documents : "has"
    patients ||--o{ notes : "has"
    procedures ||--o{ treatments : "performed"
    appointments ||--o{ treatments : "contains"
    treatments ||--o{ invoice_items : "billed"
    treatments ||--o{ tooth_history : "recorded"
    treatments ||--o{ treatment_materials : "uses"
    tooth_conditions ||--o{ patient_teeth : "current"
    tooth_conditions ||--o{ tooth_history : "changed to"
    patient_teeth ||--o{ tooth_history : "tracked"
    invoices ||--o{ invoice_items : "contains"
    invoices ||--o{ payments : "paid by"

```
## 📚 API Documentation

### Core Endpoints

#### Patient Management
- `GET /api/v1/patients` - List patients with search and pagination
- `POST /api/v1/patients` - Create new patient
- `GET /api/v1/patients/{id}` - Get patient details
- `PUT /api/v1/patients/{id}` - Update patient information

#### Treatment Management
- `POST /api/v1/treatments` - Create treatment record
- `GET /api/v1/treatments/patient/{patientId}` - Get treatment history
- `GET /api/v1/treatment-materials/treatment/{treatmentId}` - Get materials used

#### Financial Management
- `POST /api/v1/invoices` - Create invoice
- `GET /api/v1/invoices/patient/{patientId}` - Get patient invoices
- `POST /api/v1/invoices/{invoiceId}/payments` - Record payment

#### Treatment Materials (New)
- `POST /api/v1/treatment-materials` - Create material record
- `GET /api/v1/treatment-materials/treatment/{treatmentId}` - Materials by treatment
- `GET /api/v1/treatment-materials/patient/{patientId}/total-cost` - Total material costs

Full API documentation available at `/swagger-ui.html` when running.

## 🏥 Domain Model

### Core Entities
- **Patient**: Patient demographics and medical information
- **Appointment**: Scheduled patient visits
- **Treatment**: Medical procedures performed
- **TreatmentMaterial**: Materials used in treatments (New in V2)
- **Invoice/Payment**: Financial transactions
- **DentalChart**: Tooth-specific records and history

### Key Features
- Complete dental chart with 32-tooth tracking
- Financial management with balance tracking
- Lab request management
- Document storage and retrieval
- Treatment materials tracking with cost analysis
- Staff management with role-based access

## 🔧 Development

### Code Style
- Java 21 features and patterns
- Spring Boot 3.x best practices
- MapStruct for entity-DTO mapping
- Lombok for reducing boilerplate
- Bean Validation for input validation

### Project Structure
```
src/main/java/sy/sezar/clinicx/
├── core/                 # Common utilities and base classes
├── patient/              # Patient domain (main business logic)
│   ├── controller/       # REST endpoints
│   ├── service/          # Business logic
│   ├── repository/       # Data access
│   ├── model/            # JPA entities
│   ├── dto/              # Data transfer objects
│   └── mapper/           # MapStruct mappers
├── staff/                # Staff management
└── clinic/               # Clinic configuration
```

### Adding New Features
1. Create entity in `model/` package
2. Add repository with necessary queries
3. Create DTOs for API contracts
4. Implement service layer with business logic
5. Add REST controller with proper validation
6. Create MapStruct mapper for conversions
7. Write comprehensive tests
8. Add Flyway migration if schema changes needed

## 🛠️ Configuration

### Profiles
- `local` - Local development with PostgreSQL
- `test` - Testing with H2 database
- `prod` - Production configuration

### Key Properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/clinicx

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 📈 Recent Updates (V2)

### New Features
- **Treatment Materials System**: Complete material tracking with cost analysis
- **Enhanced Appointments**: AppointmentCreateRequest DTO for better API consistency
- **Database Optimization**: New indexes and views for improved performance
- **Comprehensive Testing**: 100% test coverage for new components

### Database Enhancements
- Material usage tracking and cost calculations
- Automatic total cost computation with triggers
- Enhanced financial summaries including material costs
- Performance optimizations with strategic indexing

### API Improvements
- New treatment materials endpoints
- Enhanced cost calculation endpoints
- Improved pagination support
- Comprehensive Swagger documentation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Maintain test coverage above 90%
- Follow existing code patterns and conventions
- Add appropriate Javadoc for public APIs
- Include database migrations for schema changes
- Update API documentation for new endpoints

## 📄 License

This project is proprietary software. All rights reserved.

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Contact the development team
- Check the implementation summary in `IMPLEMENTATION_SUMMARY.md`

---

Built with ❤️ using Spring Boot 3.x and Java 21
