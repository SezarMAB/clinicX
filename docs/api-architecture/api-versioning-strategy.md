# API Versioning Strategy Implementation Guide

## Executive Summary
This document outlines a comprehensive API versioning strategy for ClinicX, transitioning from the current hardcoded v1 approach to a flexible, maintainable versioning system that supports backward compatibility and graceful deprecation.

## Current State
- **Current Implementation**: Hardcoded `/api/v1` in all endpoints
- **Issues**: No version negotiation, no backward compatibility strategy, no deprecation policy
- **Risk**: Breaking changes affect all API consumers immediately

## Proposed Versioning Strategy

### 1. Versioning Approaches

#### 1.1 URL Path Versioning (Primary - Currently Used)
```
/api/v1/patients
/api/v2/patients
```

#### 1.2 Header-Based Versioning (Secondary - For Advanced Clients)
```
GET /api/patients
Headers: API-Version: 2
```

#### 1.3 Content Negotiation (For Response Format Evolution)
```
Accept: application/vnd.clinicx.v2+json
```

## Implementation Todos

### Phase 1: Foundation (Week 1)

#### TODO 1: Create Version Configuration Infrastructure
**What it does**: Establishes centralized version management and configuration
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/ApiVersioningConfig.java
@Configuration
@ConfigurationProperties(prefix = "api.versioning")
public class ApiVersioningConfig {
    private String defaultVersion = "v1";
    private List<String> supportedVersions = List.of("v1", "v2");
    private Map<String, LocalDate> deprecationDates;
    private Map<String, String> versionAliases; // latest -> v2
    private boolean enableHeaderVersioning = true;
    private boolean enableContentNegotiation = false;
}
```
**Benefits**:
- Centralized version management
- Easy to add/deprecate versions
- Configuration-driven approach

#### TODO 2: Implement Version Resolver
**What it does**: Automatically detects and resolves API version from request
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/ApiVersionResolver.java
@Component
public class ApiVersionResolver {
    public String resolveVersion(HttpServletRequest request) {
        // 1. Check URL path
        // 2. Check header
        // 3. Check accept header
        // 4. Return default
    }
}
```
**Benefits**:
- Flexible version detection
- Support multiple versioning strategies
- Fallback to default version

#### TODO 3: Create Version Interceptor
**What it does**: Intercepts requests to validate and inject version context
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/ApiVersionInterceptor.java
@Component
public class ApiVersionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        String version = versionResolver.resolveVersion(request);
        
        // Validate version is supported
        if (!config.getSupportedVersions().contains(version)) {
            throw new UnsupportedApiVersionException(version);
        }
        
        // Check deprecation
        if (isDeprecated(version)) {
            response.addHeader("X-API-Deprecation", getDeprecationDate(version));
            response.addHeader("X-API-Sunset", getSunsetDate(version));
        }
        
        // Store in context
        ApiVersionContext.setVersion(version);
        return true;
    }
}
```
**Benefits**:
- Version validation
- Deprecation warnings
- Version context management

### Phase 2: Controller Evolution (Week 2)

#### TODO 4: Create Versioned Base Controllers
**What it does**: Provides base classes for version-specific controller implementations
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/VersionedController.java
@RestController
public abstract class VersionedController {
    
    @GetMapping(value = "${api.base-path}/{version}/**")
    public ResponseEntity<?> handleVersionedRequest(
            @PathVariable String version,
            HttpServletRequest request) {
        return routeToVersionHandler(version, request);
    }
}

// V1 Base Controller
public abstract class V1Controller extends VersionedController {
    // V1 specific behavior
}

// V2 Base Controller  
public abstract class V2Controller extends VersionedController {
    // V2 specific behavior
    // Can include breaking changes
}
```
**Benefits**:
- Clean separation of versions
- Inheritance for common functionality
- Version-specific customization

#### TODO 5: Implement Version-Specific DTOs
**What it does**: Manages different data structures for different API versions
```java
// File: src/main/java/sy/sezar/clinicx/patient/dto/v1/PatientDtoV1.java
public class PatientDtoV1 {
    private UUID id;
    private String fullName; // V1 uses fullName
}

// File: src/main/java/sy/sezar/clinicx/patient/dto/v2/PatientDtoV2.java
public class PatientDtoV2 {
    private UUID id;
    private String firstName; // V2 splits name
    private String lastName;
    private String middleName;
}
```
**Benefits**:
- Version-specific data structures
- Backward compatibility
- Clear migration path

#### TODO 6: Create Version Adapter Pattern
**What it does**: Converts between different version representations
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/VersionAdapter.java
@Component
public class PatientVersionAdapter {
    
    public PatientDtoV2 adaptV1ToV2(PatientDtoV1 v1) {
        PatientDtoV2 v2 = new PatientDtoV2();
        String[] names = v1.getFullName().split(" ");
        v2.setFirstName(names[0]);
        v2.setLastName(names[names.length - 1]);
        // ... other mappings
        return v2;
    }
    
    public PatientDtoV1 adaptV2ToV1(PatientDtoV2 v2) {
        PatientDtoV1 v1 = new PatientDtoV1();
        v1.setFullName(v2.getFirstName() + " " + v2.getLastName());
        return v1;
    }
}
```
**Benefits**:
- Seamless version translation
- Support multiple versions simultaneously
- Reduces code duplication

### Phase 3: Advanced Features (Week 3)

#### TODO 7: Implement Version Negotiation
**What it does**: Automatically selects best API version based on client capabilities
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/VersionNegotiator.java
@Component
public class VersionNegotiator {
    
    public String negotiate(HttpServletRequest request) {
        // Check client capabilities
        String clientVersion = request.getHeader("X-Client-Version");
        String acceptVersion = parseAcceptHeader(request.getHeader("Accept"));
        
        // Find best match
        return findBestMatch(clientVersion, acceptVersion, supportedVersions);
    }
}
```
**Benefits**:
- Smart version selection
- Client compatibility
- Gradual migration support

#### TODO 8: Add Version Discovery Endpoint
**What it does**: Provides API version information and capabilities
```java
// File: src/main/java/sy/sezar/clinicx/core/api/VersionController.java
@RestController
@RequestMapping("/api/versions")
public class VersionController {
    
    @GetMapping
    public VersionInfo getVersionInfo() {
        return VersionInfo.builder()
            .current("v2")
            .supported(List.of("v1", "v2"))
            .deprecated(Map.of("v1", "2025-06-01"))
            .sunset(Map.of("v1", "2025-12-01"))
            .features(Map.of(
                "v1", List.of("basic-crud", "search"),
                "v2", List.of("basic-crud", "advanced-search", "bulk-operations")
            ))
            .build();
    }
}
```
**Benefits**:
- API discovery
- Version capabilities documentation
- Migration planning information

#### TODO 9: Implement Version-Specific Rate Limiting
**What it does**: Applies different rate limits based on API version
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/VersionedRateLimiter.java
@Component
public class VersionedRateLimiter {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    public void checkLimit(String tenantId, String version) {
        String key = tenantId + ":" + version;
        RateLimiter limiter = limiters.computeIfAbsent(key, 
            k -> createLimiter(version));
        
        if (!limiter.tryAcquire()) {
            throw new RateLimitExceededException(version);
        }
    }
    
    private RateLimiter createLimiter(String version) {
        // V1: 100 requests/second
        // V2: 200 requests/second (optimized)
        int rate = "v2".equals(version) ? 200 : 100;
        return RateLimiter.create(rate);
    }
}
```
**Benefits**:
- Version-specific performance tuning
- Encourage migration to newer versions
- Resource management

### Phase 4: Documentation & Migration (Week 4)

#### TODO 10: Generate Version-Specific OpenAPI Documentation
**What it does**: Creates separate API documentation for each version
```java
// File: src/main/java/sy/sezar/clinicx/core/config/OpenApiVersionConfig.java
@Configuration
public class OpenApiVersionConfig {
    
    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
            .group("v1")
            .pathsToMatch("/api/v1/**")
            .addOpenApiCustomiser(openApi -> 
                openApi.info(new Info()
                    .title("ClinicX API v1")
                    .version("1.0")
                    .description("Legacy API - Deprecated")))
            .build();
    }
    
    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
            .group("v2")
            .pathsToMatch("/api/v2/**")
            .addOpenApiCustomiser(openApi -> 
                openApi.info(new Info()
                    .title("ClinicX API v2")
                    .version("2.0")
                    .description("Current stable API")))
            .build();
    }
}
```
**Benefits**:
- Clear version documentation
- Separate API specs
- Migration guides

#### TODO 11: Create Version Migration Service
**What it does**: Helps clients migrate from older to newer API versions
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/MigrationService.java
@Service
public class MigrationService {
    
    public MigrationGuide getMigrationGuide(String fromVersion, String toVersion) {
        return MigrationGuide.builder()
            .fromVersion(fromVersion)
            .toVersion(toVersion)
            .breakingChanges(getBreakingChanges(fromVersion, toVersion))
            .newFeatures(getNewFeatures(toVersion))
            .deprecatedEndpoints(getDeprecatedEndpoints(fromVersion))
            .migrationSteps(getMigrationSteps(fromVersion, toVersion))
            .codeExamples(getCodeExamples(fromVersion, toVersion))
            .build();
    }
}
```
**Benefits**:
- Guided migration process
- Clear upgrade path
- Reduced migration friction

#### TODO 12: Implement Version Analytics
**What it does**: Tracks API version usage for informed deprecation decisions
```java
// File: src/main/java/sy/sezar/clinicx/core/api/versioning/VersionAnalytics.java
@Component
public class VersionAnalytics {
    
    @EventListener
    public void trackApiCall(ApiCallEvent event) {
        metrics.counter("api.calls", 
            "version", event.getVersion(),
            "tenant", event.getTenantId(),
            "endpoint", event.getEndpoint()
        ).increment();
    }
    
    public VersionUsageReport getUsageReport() {
        return VersionUsageReport.builder()
            .versionDistribution(getVersionDistribution())
            .deprecatedVersionUsage(getDeprecatedUsage())
            .migrationProgress(getMigrationProgress())
            .build();
    }
}
```
**Benefits**:
- Data-driven deprecation
- Usage insights
- Migration tracking

## Implementation Checklist

### Week 1: Foundation
- [ ] TODO 1: Create ApiVersioningConfig
- [ ] TODO 2: Implement ApiVersionResolver
- [ ] TODO 3: Create ApiVersionInterceptor

### Week 2: Controller Evolution  
- [ ] TODO 4: Create VersionedController base classes
- [ ] TODO 5: Implement version-specific DTOs
- [ ] TODO 6: Create VersionAdapter pattern

### Week 3: Advanced Features
- [ ] TODO 7: Implement VersionNegotiator
- [ ] TODO 8: Add version discovery endpoint
- [ ] TODO 9: Implement versioned rate limiting

### Week 4: Documentation & Migration
- [ ] TODO 10: Generate version-specific OpenAPI docs
- [ ] TODO 11: Create MigrationService
- [ ] TODO 12: Implement VersionAnalytics

## Configuration Example

```yaml
# application.yml
api:
  versioning:
    enabled: true
    default-version: v2
    supported-versions:
      - v1
      - v2
    deprecated-versions:
      v1:
        deprecated-date: 2025-06-01
        sunset-date: 2025-12-01
        migration-guide-url: /api/migration/v1-to-v2
    header-versioning:
      enabled: true
      header-name: API-Version
    content-negotiation:
      enabled: true
      vendor-prefix: vnd.clinicx
    rate-limits:
      v1: 100  # requests per second
      v2: 200
    analytics:
      enabled: true
      retention-days: 90
```

## Testing Strategy

### Unit Tests
```java
@Test
void testVersionResolution() {
    // Test URL path versioning
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v2/patients");
    assertEquals("v2", resolver.resolveVersion(request));
    
    // Test header versioning
    request.setRequestURI("/api/patients");
    request.addHeader("API-Version", "v1");
    assertEquals("v1", resolver.resolveVersion(request));
}

@Test
void testVersionAdapter() {
    PatientDtoV1 v1 = new PatientDtoV1();
    v1.setFullName("John Doe");
    
    PatientDtoV2 v2 = adapter.adaptV1ToV2(v1);
    assertEquals("John", v2.getFirstName());
    assertEquals("Doe", v2.getLastName());
}
```

### Integration Tests
```java
@Test
void testVersionedEndpoints() {
    // Test v1 endpoint
    mockMvc.perform(get("/api/v1/patients/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName").exists());
    
    // Test v2 endpoint
    mockMvc.perform(get("/api/v2/patients/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").exists())
        .andExpect(jsonPath("$.lastName").exists());
}

@Test
void testDeprecationHeaders() {
    mockMvc.perform(get("/api/v1/patients"))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-API-Deprecation"))
        .andExpect(header().string("X-API-Sunset", "2025-12-01"));
}
```

## Migration Path Example

### Client Migration from V1 to V2

#### Step 1: Update Client Version Header
```javascript
// Old client
fetch('/api/v1/patients', {
    headers: {
        'Authorization': 'Bearer token'
    }
});

// Transitional client (supports both)
fetch('/api/patients', {
    headers: {
        'Authorization': 'Bearer token',
        'API-Version': '2',
        'Accept': 'application/vnd.clinicx.v2+json'
    }
});
```

#### Step 2: Update Data Models
```typescript
// V1 Model
interface PatientV1 {
    id: string;
    fullName: string;
}

// V2 Model
interface PatientV2 {
    id: string;
    firstName: string;
    lastName: string;
    middleName?: string;
}

// Adapter function
function adaptPatientV1ToV2(v1: PatientV1): PatientV2 {
    const names = v1.fullName.split(' ');
    return {
        id: v1.id,
        firstName: names[0],
        lastName: names[names.length - 1],
        middleName: names.length > 2 ? names.slice(1, -1).join(' ') : undefined
    };
}
```

## Success Metrics

| Metric | Target | Measurement |
|--------|--------|------------|
| Version Migration Rate | 80% in 6 months | Analytics dashboard |
| API Breaking Changes | 0 for existing versions | CI/CD validation |
| Client Satisfaction | > 4.5/5 | Developer surveys |
| Documentation Coverage | 100% | OpenAPI completeness |
| Backward Compatibility | 100% for 12 months | Integration tests |

## Rollout Plan

### Month 1: Internal Testing
- Deploy versioning infrastructure
- Internal API testing
- Documentation preparation

### Month 2: Beta Release
- Selected client migration
- Gather feedback
- Refine migration tools

### Month 3: General Availability
- Public announcement
- Migration guides published
- Support channels ready

### Month 4-6: Migration Period
- Active client support
- Usage monitoring
- Performance optimization

### Month 7-12: Deprecation Phase
- Deprecation warnings active
- Reduced support for v1
- Final migration push

### Month 13: Sunset
- V1 API disabled
- Full transition to V2
- Post-mortem analysis

## Benefits Summary

1. **Backward Compatibility**: Existing clients continue working
2. **Gradual Migration**: Clients migrate at their own pace
3. **Clear Communication**: Deprecation dates and migration guides
4. **Performance Optimization**: Newer versions can be optimized
5. **Feature Evolution**: Add features without breaking existing APIs
6. **Analytics-Driven**: Data-informed deprecation decisions
7. **Developer Experience**: Clear versioning and documentation
8. **Risk Mitigation**: Reduced risk of breaking changes

## Conclusion

This comprehensive API versioning strategy provides ClinicX with a robust framework for API evolution while maintaining backward compatibility and ensuring smooth client migrations. The phased implementation approach minimizes risk and allows for iterative improvements based on real-world usage.

---

*Document Version: 1.0*  
*Last Updated: 2025-01-07*  
*Next Review: After Phase 1 Implementation*