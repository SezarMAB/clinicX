# Specialty-Realm Mapping Configuration Usage

## Configuration Overview

The `specialty-realm-mapping` configuration in `application-realm-per-type.yml` controls how the system handles realm creation and specialty mapping:

```yaml
app:
  tenant:
    specialty-realm-mapping:
      enabled: true              # Enable/disable realm-per-type mode
      auto-create-realm: true    # Auto-create realms for new specialties
      default-features:          # Features added to all specialties
        - APPOINTMENTS
```

## Where Each Setting is Used

### 1. `enabled` Property

**Location**: `DynamicRealmServiceImpl.resolveRealmForTenant()`

**Usage**:
- When `true`: Uses realm-per-type (multiple tenants per specialty realm)
- When `false`: Falls back to realm-per-tenant (one realm per tenant)

**Code Flow**:
```java
if (!mappingConfig.isEnabled()) {
    // Traditional mode: each tenant gets its own realm
    return "clinic-" + request.subdomain();  // e.g., "clinic-smile-dental"
} else {
    // Realm-per-type mode: tenants share specialty realms
    return specialtyType.getRealmName();     // e.g., "dental-realm"
}
```

**Example**:
- Enabled: All dental clinics go to "dental-realm"
- Disabled: Each clinic gets "clinic-{subdomain}" realm

### 2. `auto-create-realm` Property

**Location**: `DynamicRealmServiceImpl.resolveRealmForTenant()`

**Usage**:
- When `true`: Automatically creates realm when first tenant of a specialty is created
- When `false`: Throws error if realm doesn't exist

**Code Flow**:
```java
if (!keycloakAdminService.realmExists(realmName)) {
    if (mappingConfig.isAutoCreateRealm()) {
        // Create the realm automatically
        keycloakAdminService.createRealm(realmName, ...);
    } else {
        // Require manual realm creation
        throw new BusinessRuleException("Realm does not exist...");
    }
}
```

**Use Cases**:
- `true`: Good for development/dynamic environments
- `false`: Good for production where realms should be pre-configured

### 3. `default-features` Property

**Location**: `SpecialtyRegistryImpl.registerSpecialty()`

**Usage**:
- Features listed here are automatically added to every specialty
- Ensures all specialties have certain base features

**Code Flow**:
```java
// When registering a new specialty
List<String> allFeatures = new ArrayList<>(Arrays.asList(features));
for (String defaultFeature : mappingConfig.getDefaultFeatures()) {
    if (!allFeatures.contains(defaultFeature)) {
        allFeatures.add(defaultFeature);
    }
}
```

**Example**:
If `default-features: [APPOINTMENTS]`, then:
- DENTAL specialty gets: [DENTAL, APPOINTMENTS]
- CARDIOLOGY specialty gets: [CARDIOLOGY, APPOINTMENTS]
- Even if APPOINTMENTS wasn't explicitly specified

## Complete Usage Example

### Scenario 1: Creating First Dental Clinic

**Configuration**:
```yaml
specialty-realm-mapping:
  enabled: true
  auto-create-realm: true
  default-features:
    - APPOINTMENTS
```

**Flow**:
1. User creates tenant with specialty "DENTAL"
2. `DynamicRealmService` checks if mapping is enabled ✓
3. Looks up DENTAL specialty → realm name: "dental-realm"
4. Checks if "dental-realm" exists → No
5. Checks if auto-create is enabled ✓
6. Creates "dental-realm" automatically
7. Tenant is created in "dental-realm"

### Scenario 2: Disabled Mapping

**Configuration**:
```yaml
specialty-realm-mapping:
  enabled: false  # Disabled
```

**Flow**:
1. User creates "smile-dental" tenant
2. `DynamicRealmService` checks if mapping is enabled ✗
3. Returns traditional realm name: "clinic-smile-dental"
4. Each tenant gets its own realm (original behavior)

### Scenario 3: Manual Realm Management

**Configuration**:
```yaml
specialty-realm-mapping:
  enabled: true
  auto-create-realm: false  # Manual creation required
```

**Flow**:
1. User creates tenant with new specialty "CARDIOLOGY"
2. System looks for "cardiology-realm"
3. Realm doesn't exist
4. Auto-create is disabled
5. Throws error: "Realm does not exist for specialty CARDIOLOGY and auto-creation is disabled"
6. Admin must manually create realm in Keycloak first

## Integration Points

### 1. Application Startup
- `SpecialtyTypeInitializer` reads specialty configuration
- Adds default features to each specialty

### 2. Tenant Creation
- `EnhancedTenantServiceImpl` uses `DynamicRealmService`
- Realm selection based on configuration

### 3. Specialty Registration API
- `SpecialtyRegistry` adds default features to new specialties
- Ensures consistency across all specialties

### 4. Feature Checking (Future)
```java
// Example usage in controllers
@PreAuthorize("@featureChecker.hasFeature('APPOINTMENTS')")
@GetMapping("/appointments")
public List<Appointment> getAppointments() {
    // Only accessible if tenant's specialty includes APPOINTMENTS
}
```

## Best Practices

1. **Development Environment**:
   ```yaml
   specialty-realm-mapping:
     enabled: true
     auto-create-realm: true
     default-features: [APPOINTMENTS]
   ```

2. **Production Environment**:
   ```yaml
   specialty-realm-mapping:
     enabled: true
     auto-create-realm: false  # Require manual realm setup
     default-features: [APPOINTMENTS, AUDIT]
   ```

3. **Migration from Existing System**:
   ```yaml
   specialty-realm-mapping:
     enabled: false  # Keep existing behavior initially
   ```

## Monitoring

Check logs for configuration usage:
- "Specialty-realm mapping is disabled" - When using traditional mode
- "Auto-creating new realm" - When realm is created automatically
- "Registering specialty X with features" - Shows merged features