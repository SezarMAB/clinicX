package sy.sezar.clinicx.tenant.constants;

/**
 * Constants used throughout the tenant management module.
 * Centralizes magic strings and configuration values.
 */
public final class TenantConstants {
    
    private TenantConstants() {
        // Private constructor to prevent instantiation
    }
    
    // Attribute Keys
    public static final String ATTR_TENANT_ID = "tenant_id";
    public static final String ATTR_ACTIVE_TENANT_ID = "active_tenant_id";
    public static final String ATTR_PRIMARY_REALM = "primary_realm";
    public static final String ATTR_ACCESSIBLE_TENANTS = "accessible_tenants";
    public static final String ATTR_USER_TENANT_ROLES = "user_tenant_roles";
    
    // JSON Field Names
    public static final String FIELD_TENANT_ID = "tenant_id";
    public static final String FIELD_CLINIC_NAME = "clinic_name";
    public static final String FIELD_CLINIC_TYPE = "clinic_type";
    public static final String FIELD_ROLES = "roles";
    
    // Error Messages
    public static final String ERROR_TENANT_NOT_FOUND = "Tenant not found: %s";
    public static final String ERROR_USER_NOT_FOUND = "User not found: %s";
    public static final String ERROR_USER_NOT_IN_TENANT = "User not found in this tenant";
    public static final String ERROR_EMAIL_EXISTS = "User with email %s already exists";
    public static final String ERROR_USER_ALREADY_HAS_ACCESS = "User already has active access to this tenant";
    public static final String ERROR_CANNOT_DEACTIVATE_ADMIN = "Cannot deactivate admin user";
    public static final String ERROR_CANNOT_REVOKE_PRIMARY = "Cannot revoke access to primary tenant";
    public static final String ERROR_CANNOT_REVOKE_ONLY_TENANT = "Cannot revoke access to the only tenant";
    public static final String ERROR_FAILED_TO_CREATE_ACCESS = "Failed to create user access record: %s";
    public static final String ERROR_FAILED_TO_UPDATE_USER = "Failed to update user: %s";
    public static final String ERROR_FAILED_TO_DEACTIVATE = "Failed to deactivate user: %s";
    public static final String ERROR_FAILED_TO_ACTIVATE = "Failed to activate user: %s";
    public static final String ERROR_FAILED_TO_DELETE = "Failed to delete user: %s";
    public static final String ERROR_FAILED_TO_UPDATE_ROLES = "Failed to update user roles: %s";
    public static final String ERROR_FAILED_TO_RESET_PASSWORD = "Failed to reset password: %s";
    
    // Log Messages
    public static final String LOG_USER_NOT_FOUND_IN_KEYCLOAK = "Could not find Keycloak user for Staff record: {}";
    public static final String LOG_CREATED_STAFF = "Created Staff record for user {} with ID {}";
    public static final String LOG_DEACTIVATED_STAFF = "Deactivated Staff record for user {}";
    public static final String LOG_ACTIVATED_STAFF = "Activated Staff record for user {}";
    public static final String LOG_UPDATED_STAFF_ROLE = "Updated Staff role for user {}";
    public static final String LOG_CREATED_ACCESS = "Created user_tenant_access with ID {} for user {} in tenant {}";
    public static final String LOG_DEACTIVATED_ACCESS = "Deactivated user_tenant_access for user {} in tenant {}";
    public static final String LOG_REACTIVATED_ACCESS = "Reactivated user_tenant_access for user {} in tenant {}";
    public static final String LOG_REVOKED_ACCESS = "Revoked user_tenant_access for user {} in tenant {}";
    public static final String LOG_DEACTIVATED_IN_KEYCLOAK = "Deactivated user {} in Keycloak";
    
    // Warning Messages
    public static final String WARN_COULD_NOT_UPDATE_ACCESS = "Could not update user_tenant_access for deactivation: {}";
    public static final String WARN_COULD_NOT_REVOKE_ACCESS = "Could not revoke user_tenant_access: {}";
    public static final String WARN_UNKNOWN_ROLE = "Unknown role: {}, defaulting to ASSISTANT";
    public static final String WARN_PARSE_ERROR = "Failed to parse {}: {}";
    
    // Default Values
    public static final boolean DEFAULT_IS_PRIMARY = false;
    public static final boolean DEFAULT_IS_ACTIVE = true;
    public static final boolean DEFAULT_TEMPORARY_PASSWORD = false;
    
    // Credential Types
    public static final String CREDENTIAL_TYPE_PASSWORD = "password";
}