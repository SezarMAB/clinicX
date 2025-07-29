# Feature Modularization Implementation Guide for ClinicX

## Overview
This guide details how to transform ClinicX into a feature-modular architecture that supports different clinic types through feature flags.

## Step 1: Add Feature Flag Framework

### 1.1 Add Dependencies
```groovy
// build.gradle
dependencies {
    // Feature flags
    implementation 'org.togglz:togglz-spring-boot-starter:4.4.0'
    implementation 'org.togglz:togglz-console:4.4.0'
    
    // Multi-tenancy support
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.hibernate:hibernate-core'
}
```

### 1.2 Create Feature Enum
```java
// src/main/java/sy/sezar/clinicx/core/features/ClinicFeatures.java
package sy.sezar.clinicx.core.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum ClinicFeatures implements Feature {
    
    @Label("Dental Module - Enables teeth charts, dental procedures")
    DENTAL_MODULE,
    
    @Label("Treatment Plans - Complex treatment planning")
    TREATMENT_PLANS,
    
    @Label("Lab Integration - Lab request management")
    LAB_REQUESTS,
    
    @Label("Advanced Financial - Installments, payment plans")
    ADVANCED_FINANCIAL,
    
    @Label("Document Management - File uploads and storage")
    DOCUMENT_MANAGEMENT,
    
    @Label("SMS Notifications")
    SMS_NOTIFICATIONS,
    
    @Label("Email Reminders")
    EMAIL_REMINDERS;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
```

### 1.3 Feature Configuration
```java
// src/main/java/sy/sezar/clinicx/core/features/FeatureConfiguration.java
package sy.sezar.clinicx.core.features;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import javax.sql.DataSource;

@Configuration
public class FeatureConfiguration {
    
    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(ClinicFeatures.class);
    }
    
    @Bean
    public StateRepository stateRepository(DataSource dataSource) {
        return JDBCStateRepository.newBuilder(dataSource)
            .tableName("FEATURE_TOGGLES")
            .createTable(true)
            .build();
    }
}
```

## Step 2: Create Tenant Context

### 2.1 Tenant Model
```java
// src/main/java/sy/sezar/clinicx/core/tenant/Tenant.java
package sy.sezar.clinicx.core.tenant;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tenants")
@Data
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String tenantKey;
    
    private String clinicName;
    
    @Enumerated(EnumType.STRING)
    private ClinicType clinicType;
    
    private String schemaName;
    
    @ElementCollection
    @CollectionTable(name = "tenant_features")
    private Set<String> enabledFeatures = new HashSet<>();
    
    private boolean active = true;
}

enum ClinicType {
    DENTAL_CLINIC,
    GENERAL_CLINIC,
    SPECIALIST_CLINIC
}
```

### 2.2 Tenant Context Holder
```java
// src/main/java/sy/sezar/clinicx/core/tenant/TenantContext.java
package sy.sezar.clinicx.core.tenant;

public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }
    
    public static String getCurrentTenant() {
        return currentTenant.get();
    }
    
    public static void clear() {
        currentTenant.remove();
    }
}
```

### 2.3 Tenant Interceptor
```java
// src/main/java/sy/sezar/clinicx/core/tenant/TenantInterceptor.java
package sy.sezar.clinicx.core.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    private static final String TENANT_HEADER = "X-Tenant-ID";
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        }
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) {
        TenantContext.clear();
    }
}
```

## Step 3: Modularize Features

### 3.1 Module Structure
```
src/main/java/sy/sezar/clinicx/
├── core/                      # Always loaded
│   ├── model/
│   ├── security/
│   └── tenant/
├── appointments/              # Always loaded
│   ├── model/
│   ├── service/
│   └── controller/
├── patient/                   # Always loaded
│   ├── model/
│   ├── service/
│   └── controller/
├── modules/                   # Feature-specific
│   ├── dental/               # DENTAL_MODULE
│   │   ├── model/
│   │   ├── service/
│   │   └── controller/
│   ├── lab/                  # LAB_REQUESTS
│   │   ├── model/
│   │   ├── service/
│   │   └── controller/
│   └── financial/            # ADVANCED_FINANCIAL
│       ├── model/
│       ├── service/
│       └── controller/
```

### 3.2 Conditional Module Loading

#### Dental Module Configuration
```java
// src/main/java/sy/sezar/clinicx/modules/dental/DentalModuleConfig.java
package sy.sezar.clinicx.modules.dental;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(
    name = "clinicx.modules.dental.enabled",
    havingValue = "true"
)
@ComponentScan(basePackages = "sy.sezar.clinicx.modules.dental")
@EnableJpaRepositories(basePackages = "sy.sezar.clinicx.modules.dental.repository")
public class DentalModuleConfig {
    // Dental module specific beans
}
```

#### Feature-Aware Controller
```java
// src/main/java/sy/sezar/clinicx/modules/dental/controller/DentalChartController.java
package sy.sezar.clinicx.modules.dental.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.core.features.ClinicFeatures;
import sy.sezar.clinicx.core.security.FeatureRequired;

@RestController
@RequestMapping("/api/v1/dental")
@ConditionalOnProperty(
    name = "clinicx.modules.dental.enabled",
    havingValue = "true"
)
public class DentalChartController {
    
    @GetMapping("/charts/{patientId}")
    @FeatureRequired(ClinicFeatures.DENTAL_MODULE)
    public ResponseEntity<DentalChartDto> getDentalChart(@PathVariable UUID patientId) {
        // Implementation
    }
}
```

### 3.3 Feature-Aware Service
```java
// src/main/java/sy/sezar/clinicx/patient/service/impl/PatientServiceImpl.java
@Service
@Transactional
public class PatientServiceImpl implements PatientService {
    
    @Autowired(required = false)
    private DentalChartService dentalChartService;
    
    @Override
    public PatientDetailsDto getPatientDetails(UUID patientId) {
        PatientDetailsDto details = new PatientDetailsDto();
        // Base patient info - always loaded
        
        // Conditional dental info
        if (ClinicFeatures.DENTAL_MODULE.isActive() && dentalChartService != null) {
            details.setDentalChart(dentalChartService.getChart(patientId));
        }
        
        return details;
    }
}
```

## Step 4: Database Modularization

### 4.1 Conditional Flyway Migrations
```sql
-- src/main/resources/db/migration/core/V1__core_schema.sql
-- Always executed
CREATE TABLE patients (
    id UUID PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    -- core fields
);

CREATE TABLE appointments (
    id UUID PRIMARY KEY,
    patient_id UUID REFERENCES patients(id),
    -- core appointment fields
);
```

```sql
-- src/main/resources/db/migration/dental/V100__dental_module.sql
-- Only executed if dental module is enabled
CREATE TABLE IF NOT EXISTS tooth_conditions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS patient_teeth (
    patient_id UUID,
    tooth_number INTEGER,
    condition_id UUID,
    PRIMARY KEY (patient_id, tooth_number),
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);
```

### 4.2 Conditional Migration Configuration
```java
// src/main/java/sy/sezar/clinicx/core/db/ModularFlywayConfig.java
@Configuration
public class ModularFlywayConfig {
    
    @Bean
    @Primary
    public Flyway flyway(DataSource dataSource, 
                        @Value("${clinicx.modules.dental.enabled:false}") boolean dentalEnabled,
                        @Value("${clinicx.modules.lab.enabled:false}") boolean labEnabled) {
        
        List<String> locations = new ArrayList<>();
        locations.add("classpath:db/migration/core");
        
        if (dentalEnabled) {
            locations.add("classpath:db/migration/dental");
        }
        if (labEnabled) {
            locations.add("classpath:db/migration/lab");
        }
        
        return Flyway.configure()
            .dataSource(dataSource)
            .locations(locations.toArray(new String[0]))
            .load();
    }
}
```

## Step 5: API Versioning and Documentation

### 5.1 Feature-Aware OpenAPI
```java
// src/main/java/sy/sezar/clinicx/core/config/OpenApiConfig.java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ClinicX API")
                .version("1.0")
                .description("Multi-tenant clinic management system"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
            .addTagsItem(new Tag().name("Core").description("Always available"))
            .addTagsItem(new Tag().name("Dental").description("Requires DENTAL_MODULE feature"));
    }
}
```

### 5.2 Feature Documentation
```java
@Operation(
    summary = "Get dental chart",
    description = "Requires DENTAL_MODULE feature to be enabled",
    tags = {"Dental"}
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chart retrieved"),
    @ApiResponse(responseCode = "403", description = "Feature not enabled")
})
```

## Step 6: Testing Strategy

### 6.1 Feature Flag Testing
```java
@SpringBootTest
@ActiveProfiles("test")
class DentalModuleTest {
    
    @Test
    @WithFeature(ClinicFeatures.DENTAL_MODULE)
    void shouldLoadDentalEndpoints() {
        // Test dental endpoints are available
    }
    
    @Test
    @WithoutFeature(ClinicFeatures.DENTAL_MODULE)
    void shouldReturn404ForDentalEndpoints() {
        // Test dental endpoints return 404
    }
}
```

### 6.2 Module Integration Test
```java
@SpringBootTest(
    properties = {
        "clinicx.modules.dental.enabled=true",
        "clinicx.modules.lab.enabled=false"
    }
)
class ModuleIntegrationTest {
    @Autowired(required = false)
    private DentalChartController dentalController;
    
    @Autowired(required = false)
    private LabRequestController labController;
    
    @Test
    void shouldLoadOnlyEnabledModules() {
        assertNotNull(dentalController);
        assertNull(labController);
    }
}
```

## Step 7: Configuration Management

### 7.1 Application Properties
```yaml
# application.yml
clinicx:
  modules:
    dental:
      enabled: ${DENTAL_MODULE_ENABLED:false}
    lab:
      enabled: ${LAB_MODULE_ENABLED:false}
    financial:
      advanced: ${ADVANCED_FINANCIAL_ENABLED:false}
  
  features:
    default-activation-strategy: PER_TENANT
    console-enabled: true
    console-path: /admin/features
```

### 7.2 Tenant-Specific Configuration
```yaml
# application-tenant-dental.yml
clinicx:
  modules:
    dental:
      enabled: true
    lab:
      enabled: true
    financial:
      advanced: true

# application-tenant-general.yml
clinicx:
  modules:
    dental:
      enabled: false
    lab:
      enabled: true
    financial:
      advanced: false
```

## Implementation Checklist

- [ ] Add feature flag dependencies
- [ ] Create feature enum and configuration
- [ ] Implement tenant context and interceptor
- [ ] Reorganize code into modules
- [ ] Add @ConditionalOnProperty annotations
- [ ] Create modular Flyway migrations
- [ ] Update controllers with feature checks
- [ ] Implement feature-aware services
- [ ] Add feature documentation
- [ ] Create feature-specific tests
- [ ] Configure module properties
- [ ] Test with different feature combinations
- [ ] Create tenant onboarding process
- [ ] Document feature dependencies

## Next Steps

1. **Start Small**: Begin with one module (e.g., Dental)
2. **Test Thoroughly**: Ensure feature flags work correctly
3. **Monitor Performance**: Check impact of conditional loading
4. **Gather Feedback**: Deploy to pilot tenants
5. **Iterate**: Refine module boundaries based on usage