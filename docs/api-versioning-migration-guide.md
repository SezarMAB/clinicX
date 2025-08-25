# API Versioning Migration Guide

## Removing Hardcoded Versions from Controllers

### Current State (Hardcoded)
```java
@RestController
@RequestMapping("/api/v1/patients")  // Version hardcoded
public interface PatientControllerApi {
    // endpoints...
}
```

### Option 1: Simple Migration (Recommended for Phase 1)

#### Step 1: Update Controller Mapping
```java
@RestController
@RequestMapping("/api/patients")  // No version!
public interface PatientControllerApi {
    // endpoints remain the same
}
```

#### Step 2: Let VersionRoutingFilter Handle It
The `VersionRoutingFilter` already handles this! It will:
1. Accept requests to `/api/patients`
2. Accept requests to `/api/v1/patients` (if needed for backward compatibility)
3. Route based on headers/content negotiation

#### Step 3: Update the Filter for Better Routing
Update `VersionRoutingFilter` to handle both patterns:

```java
@Override
protected void doFilterInternal(HttpServletRequest request, 
                              HttpServletResponse response, 
                              FilterChain filterChain) throws ServletException, IOException {
    
    String requestURI = request.getRequestURI();
    
    // For all API requests
    if (requestURI.startsWith("/api/")) {
        String version = versionResolver.resolveVersion(request);
        response.addHeader("X-API-Version", version);
        ApiVersionContext.setVersion(version);
        
        // If URL already has version, just continue
        if (requestURI.matches("/api/v\\d+/.*")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // For version-less URLs, we have two options:
        
        // Option A: Forward to versioned URL (for backward compatibility)
        if (needsVersionRouting()) {
            String versionedPath = requestURI.replaceFirst("/api/", "/api/" + version + "/");
            request.getRequestDispatcher(versionedPath).forward(request, response);
            return;
        }
        
        // Option B: Just continue (if controllers use /api without version)
        filterChain.doFilter(request, response);
    }
}
```

### Option 2: Using @ApiVersioned Annotation

#### Step 1: Annotate Controller
```java
@RestController
@ApiVersioned(versions = {"v1", "v2"})
@RequestMapping("/api/patients")  // No version in mapping
public interface PatientControllerApi {
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable UUID id) {
        // Check version for logic differences
        if (ApiVersionContext.isVersion("v2")) {
            // V2 logic
        } else {
            // V1 logic
        }
    }
}
```

### Option 3: Version-Specific Controllers (For Breaking Changes)

When you have breaking changes between versions:

```java
// Base interface
public interface PatientControllerBase {
    ResponseEntity<?> getPatient(UUID id);
}

// V1 Controller
@RestController
@RequestMapping("/api/v1/patients")
public class PatientControllerV1 implements PatientControllerBase {
    public ResponseEntity<PatientDtoV1> getPatient(UUID id) {
        // V1 implementation
    }
}

// V2 Controller  
@RestController
@RequestMapping("/api/v2/patients")
public class PatientControllerV2 implements PatientControllerBase {
    public ResponseEntity<PatientDtoV2> getPatient(UUID id) {
        // V2 implementation with breaking changes
    }
}

// Version Router (handles /api/patients)
@RestController
@RequestMapping("/api/patients")
public class PatientControllerRouter {
    @Autowired private PatientControllerV1 v1Controller;
    @Autowired private PatientControllerV2 v2Controller;
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(@PathVariable UUID id) {
        String version = ApiVersionContext.getVersion();
        
        if ("v2".equals(version)) {
            return v2Controller.getPatient(id);
        }
        return v1Controller.getPatient(id);
    }
}
```

## Recommended Migration Path

### Phase 1: Remove Hardcoded Versions (Quick Win)
1. Update all controllers from `/api/v1/X` to `/api/X`
2. Let `VersionRoutingFilter` handle version detection
3. Test with existing clients

### Phase 2: Add Version-Specific Logic
1. Use `ApiVersionContext` in services for version-specific behavior
2. Create version-specific DTOs where needed
3. Add `@ApiVersioned` annotation support

### Phase 3: Support Multiple Versions
1. Create separate controllers for breaking changes
2. Implement version adapters
3. Add deprecation warnings

## Testing After Migration

```bash
# All these should work after migration:

# Version from URL (backward compatibility)
curl http://localhost:8080/api/v1/patients

# Version from header
curl http://localhost:8080/api/patients -H "API-Version: 1"

# Version from content negotiation
curl http://localhost:8080/api/patients -H "Accept: application/vnd.clinicx.v1+json"

# Default version
curl http://localhost:8080/api/patients
```

## Benefits of Removing Hardcoded Versions

1. **Flexibility**: Clients can choose versioning strategy
2. **Cleaner Code**: Controllers don't repeat `/v1/` everywhere
3. **Easier Migration**: Adding v2 doesn't require changing all mappings
4. **Better Testing**: Can test different versions without changing URLs
5. **Future-Proof**: Easy to add new versioning strategies

## Example: Migrating PatientController

### Before:
```java
@RestController
@RequestMapping("/api/v1/patients")
public interface PatientControllerApi {
    @GetMapping("/{id}")
    ResponseEntity<PatientSummaryDto> getPatientById(@PathVariable UUID id);
}
```

### After:
```java
@RestController
@RequestMapping("/api/patients")  // No version!
@ApiVersioned(versions = {"v1", "v2"})  // Optional: explicit version support
public interface PatientControllerApi {
    @GetMapping("/{id}")
    ResponseEntity<PatientSummaryDto> getPatientById(@PathVariable UUID id);
}
```

The system will now handle:
- `/api/patients/{id}` - Version from header/content-negotiation
- `/api/v1/patients/{id}` - Explicit v1 (via filter forwarding)
- `/api/v2/patients/{id}` - Explicit v2 (when v2 is added)