# API Versioning Usage Examples

## How to Use API Versioning in Controllers

### 1. Check Current Version in Controller

```java
@GetMapping("/{id}")
public ResponseEntity<PatientSummaryDto> getPatientById(@PathVariable UUID id) {
    String currentVersion = ApiVersionContext.getVersion();
    
    if (ApiVersionContext.isVersion("v1")) {
        // V1 logic
        return ResponseEntity.ok(patientService.findPatientById(id));
    } else if (ApiVersionContext.isVersion("v2")) {
        // V2 logic with enhanced features
        PatientSummaryDto patient = patientService.findPatientByIdWithEnhancements(id);
        return ResponseEntity.ok(patient);
    }
    
    return ResponseEntity.ok(patientService.findPatientById(id));
}
```

### 2. Version-Specific Business Logic

```java
@Service
public class PatientServiceImpl implements PatientService {
    
    public PatientSummaryDto findPatientById(UUID id) {
        // Check version for feature toggles
        if (ApiVersionContext.isVersionOrNewer("v2")) {
            // Include additional fields for v2+
            return enrichedPatientMapping(patient);
        }
        
        // Basic mapping for v1
        return basicPatientMapping(patient);
    }
}
```

### 3. Deprecation Warnings

When v1 becomes deprecated (update application.yml):

```yaml
api:
  versioning:
    deprecated-versions:
      v1:
        deprecated-date: 2025-06-01
        sunset-date: 2025-12-01
        migration-guide-url: /api/migration/v1-to-v2
```

Clients using v1 will automatically receive headers:
```
X-API-Deprecation: 2025-06-01
X-API-Sunset: 2025-12-01
X-API-Migration-Guide: /api/migration/v1-to-v2
Warning: 299 - "API version v1 is deprecated and will be sunset on 2025-12-01"
```

## Next Steps for Full Version Support

### Phase 2: Create V2 Controllers

1. **Create V2 Controller Interface**
```java
@RestController
@RequestMapping("/api/v2/patients")  // Note: v2 instead of v1
public interface PatientControllerV2Api {
    // V2-specific endpoints with breaking changes
}
```

2. **Version-Specific DTOs**
```java
// V1 DTO
public class PatientDtoV1 {
    private String fullName;  // Single field
}

// V2 DTO
public class PatientDtoV2 {
    private String firstName;  // Split into separate fields
    private String lastName;
    private String middleName;
}
```

3. **Version Adapter**
```java
@Component
public class PatientVersionAdapter {
    public Object adaptToVersion(Patient entity) {
        String version = ApiVersionContext.getVersion();
        
        if ("v1".equals(version)) {
            return toV1Dto(entity);
        } else if ("v2".equals(version)) {
            return toV2Dto(entity);
        }
        
        return toV2Dto(entity); // Default to latest
    }
}
```

## Testing the Current Implementation

### Test with cURL

```bash
# V1 via URL path (existing controllers)
curl -X GET "http://localhost:8080/api/v1/patients" \
  -H "Authorization: Bearer $TOKEN"

# Response headers will include:
# X-API-Version: v1

# V1 via header (when controllers are updated to use /api/patients)
curl -X GET "http://localhost:8080/api/patients" \
  -H "API-Version: 1" \
  -H "Authorization: Bearer $TOKEN"

# V2 via header (when v2 controllers are created)
curl -X GET "http://localhost:8080/api/patients" \
  -H "API-Version: 2" \
  -H "Authorization: Bearer $TOKEN"
```

## Current Limitations

1. **Controllers are hardcoded to v1** - They use `/api/v1/` in their `@RequestMapping`
2. **No v2 controllers yet** - Need to create separate v2 controllers for breaking changes
3. **No version-specific DTOs** - All versions return the same DTO structure

## Benefits Already Available

1. ✅ **Version detection** - System knows which version is being used
2. ✅ **Version headers** - Clients receive version information in responses
3. ✅ **Deprecation support** - Can mark versions as deprecated
4. ✅ **Multiple version strategies** - URL, header, and content negotiation
5. ✅ **Version context** - Controllers can check current version for conditional logic