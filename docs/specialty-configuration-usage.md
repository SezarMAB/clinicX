# Specialty Configuration Usage Guide

## Where the Specialty Configuration is Used

The specialty configuration defined in `application-realm-per-type.yml` is used in several places throughout the system:

### 1. Database Initialization (via SpecialtyTypeInitializer)
- **Location**: `SpecialtyTypeInitializer.java`
- **Purpose**: Loads specialty types into the `specialty_types` table on application startup
- **When**: Only when `app.multi-tenant.realm-per-type=true`

### 2. Tenant Creation Process
- **Location**: `EnhancedTenantServiceImpl.createTenant()`
- **Flow**:
  ```
  1. User creates tenant with specialty (e.g., "DENTAL")
  2. DynamicRealmService looks up specialty in database
  3. Gets realm name from specialty (e.g., "dental-realm")
  4. Creates/uses realm based on specialty configuration
  ```

### 3. Realm Creation and Configuration
- **Location**: `DynamicRealmServiceImpl.resolveRealmForTenant()`
- **Usage**:
  - Determines which realm to use based on specialty
  - Creates realm if it doesn't exist
  - Configures realm with specialty-specific features

### 4. Feature Access Control
The `features` array in each specialty defines what modules are available:
- `ALL` - Access to all system features
- `DENTAL` - Dental-specific features (tooth charts, dental procedures)
- `APPOINTMENTS` - Appointment scheduling features

### 5. API Validation
- **Location**: `TenantCreateRequest.java`
- **Usage**: Validates that the specialty provided is one of the configured types

### 6. User Interface (Future)
The features array will be used to:
- Show/hide menu items based on specialty
- Enable/disable functionality
- Customize UI for specialty type

## Example Usage Flow

1. **Application Startup**:
   ```
   SpecialtyTypeInitializer reads application-realm-per-type.yml
   → Loads CLINIC, DENTAL, APPOINTMENTS into database
   ```

2. **Creating a Dental Clinic**:
   ```
   POST /api/v1/tenants
   {
     "specialty": "DENTAL",
     "subdomain": "smile-dental",
     ...
   }
   
   → System looks up DENTAL in specialty_types table
   → Gets realm-name: "dental-realm"
   → Creates tenant in dental-realm
   → User has access to DENTAL + APPOINTMENTS features
   ```

3. **Feature Checking** (Example implementation):
   ```java
   @Service
   public class FeatureService {
       public boolean hasFeature(String tenantId, String feature) {
           Tenant tenant = tenantRepository.findByTenantId(tenantId);
           SpecialtyType specialty = specialtyTypeRepository.findByCode(tenant.getSpecialty());
           
           return Arrays.asList(specialty.getFeatures()).contains(feature) 
                  || Arrays.asList(specialty.getFeatures()).contains("ALL");
       }
   }
   ```

## Adding New Specialties

To add a new specialty (e.g., CARDIOLOGY):

1. **Update application-realm-per-type.yml**:
   ```yaml
   specialty:
     types:
       - code: CARDIOLOGY
         name: Cardiology Clinic
         realm-name: cardiology-realm
         features:
           - CARDIOLOGY
           - APPOINTMENTS
           - IMAGING
   ```

2. **Restart application** - SpecialtyTypeInitializer will load it

3. **Or use API** (if already running):
   ```
   POST /api/admin/specialties
   {
     "code": "CARDIOLOGY",
     "name": "Cardiology Clinic",
     "realmName": "cardiology-realm",
     "features": ["CARDIOLOGY", "APPOINTMENTS", "IMAGING"]
   }
   ```

## Configuration Hierarchy

1. **Database** (specialty_types table) - Runtime source of truth
2. **Configuration file** (application-realm-per-type.yml) - Initial values
3. **API** (/api/admin/specialties) - Dynamic updates

The database always takes precedence, but the configuration file provides initial values and can update existing entries on restart.