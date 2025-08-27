-- ====================================================================
-- MASTER TENANT AND ADMIN USER INITIALIZATION (H2 Version)
-- Version: 20.0 - Initialize master tenant and admin user
-- ====================================================================

-- ====================================================================
-- MASTER TENANT INITIALIZATION
-- ====================================================================

-- Insert master tenant
MERGE INTO tenants (
    id,
    tenant_id,
    name,
    subdomain,
    realm_name,
    specialty,
    is_active,
    contact_email,
    contact_phone,
    address,
    subscription_start_date,
    subscription_end_date,
    subscription_plan,
    max_users,
    max_patients,
    created_at,
    updated_at,
    created_by,
    updated_by
) KEY(tenant_id) VALUES (
    RANDOM_UUID(),
    'master',
    'Master Clinic',
    'master',
    'master',
    'CLINIC',
    TRUE,
    'admin@master-clinic.com',
    '+1-555-0100',
    '123 Main Street, City, State 12345',
    CURRENT_TIMESTAMP,
    DATEADD('YEAR', 10, CURRENT_TIMESTAMP),
    'enterprise',
    100,
    10000,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
);

-- ====================================================================
-- ADMIN USER INITIALIZATION
-- ====================================================================

-- Insert admin staff member
MERGE INTO staff (
    id,
    full_name,
    email,
    phone_number,
    is_active,
    keycloak_user_id,
    tenant_id,
    created_at,
    updated_at
) KEY(id) VALUES (
    'd4184f81-356a-4d28-82a3-c457fe645d41',
    'System Administrator',
    'admin@master-clinic.com',
    '+1-555-0100',
    TRUE,
    'd4184f81-356a-4d28-82a3-c457fe645d41',
    'master',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert role into staff_roles table
MERGE INTO staff_roles (staff_id, role)
KEY(staff_id, role)
VALUES ('d4184f81-356a-4d28-82a3-c457fe645d41', 'ADMIN');

-- ====================================================================
-- USER TENANT ACCESS RELATIONSHIP
-- ====================================================================

-- Grant admin user access to master tenant
-- First, insert or update the user_tenant_access record
MERGE INTO user_tenant_access (
    id,
    user_id,
    tenant_id,
    is_primary,
    is_active,
    created_at,
    updated_at,
    created_by,
    updated_by
) KEY(user_id, tenant_id) VALUES (
    RANDOM_UUID(),
    'd4184f81-356a-4d28-82a3-c457fe645d41',
    'master',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
);

-- Insert the role into the user_tenant_access_roles table
-- We need to get the ID of the user_tenant_access record we just created/updated
MERGE INTO user_tenant_access_roles (user_tenant_access_id, role)
KEY(user_tenant_access_id, role)
SELECT id, 'SUPER_ADMIN'
FROM user_tenant_access
WHERE user_id = 'd4184f81-356a-4d28-82a3-c457fe645d41' 
  AND tenant_id = 'master';

-- ====================================================================
-- VERIFY INITIALIZATION
-- ====================================================================

-- Create a view to verify the setup
DROP VIEW IF EXISTS v_master_tenant_setup;

CREATE VIEW v_master_tenant_setup AS
SELECT
    t.tenant_id,
    t.name as tenant_name,
    t.subdomain,
    t.realm_name,
    t.specialty,
    t.is_active as tenant_active,
    s.id as staff_id,
    s.full_name as admin_name,
    s.email as admin_email,
    sr.role as staff_role,
    s.is_active as staff_active,
    uta.user_id as keycloak_user_id,
    utar.role as tenant_role,
    uta.is_primary,
    uta.is_active as access_active
FROM tenants t
JOIN user_tenant_access uta ON t.tenant_id = uta.tenant_id
JOIN staff s ON uta.user_id = CAST(s.id AS VARCHAR)
LEFT JOIN staff_roles sr ON s.id = sr.staff_id
LEFT JOIN user_tenant_access_roles utar ON uta.id = utar.user_tenant_access_id
WHERE t.tenant_id = 'master';