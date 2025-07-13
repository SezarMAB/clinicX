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