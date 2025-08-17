-- ====================================================================
-- MASTER TENANT AND ADMIN USER INITIALIZATION
-- Version: 20.0 - Initialize master tenant and admin user
-- ====================================================================

BEGIN;

-- ====================================================================
-- MASTER TENANT INITIALIZATION
-- ====================================================================

-- Insert master tenant
INSERT INTO tenants (
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
) VALUES (
    gen_random_uuid(),
    'master',
    'Master Clinic',
    'master',
    'master',
    'CLINIC',
    TRUE,
    'admin@master-clinic.com',
    '+1-555-0100',
    '123 Main Street, City, State 12345',
    NOW(),
    NOW() + INTERVAL '10 years',
    'enterprise',
    100,
    10000,
    NOW(),
    NOW(),
    'system',
    'system'
) ON CONFLICT (tenant_id) DO NOTHING;

-- ====================================================================
-- ADMIN USER INITIALIZATION
-- ====================================================================

-- Insert admin staff member
INSERT INTO staff (
    id,
    full_name,
    email,
    phone_number,
    is_active,
    keycloak_user_id,
    tenant_id,
    created_at,
    updated_at
) VALUES (
    'd4184f81-356a-4d28-82a3-c457fe645d41'::uuid,
    'System Administrator',
    'admin@master-clinic.com',
    '+1-555-0100',
    TRUE,
    'd4184f81-356a-4d28-82a3-c457fe645d41',
    'master',
    NOW(),
    NOW()
) ON CONFLICT (id) DO UPDATE SET
    full_name = EXCLUDED.full_name,
    email = EXCLUDED.email,
    phone_number = EXCLUDED.phone_number,
    is_active = EXCLUDED.is_active,
    keycloak_user_id = EXCLUDED.keycloak_user_id,
    tenant_id = EXCLUDED.tenant_id,
    updated_at = NOW();

-- Insert role into staff_roles table
INSERT INTO staff_roles (staff_id, role)
VALUES ('d4184f81-356a-4d28-82a3-c457fe645d41'::uuid, 'ADMIN')
ON CONFLICT (staff_id, role) DO NOTHING;

-- ====================================================================
-- USER TENANT ACCESS RELATIONSHIP
-- ====================================================================

-- Grant admin user access to master tenant
DO $$
DECLARE
    access_id UUID;
BEGIN
    -- Insert or get the user_tenant_access record
    INSERT INTO user_tenant_access (
        id,
        user_id,
        tenant_id,
        is_primary,
        is_active,
        created_at,
        updated_at,
        created_by,
        updated_by
    ) VALUES (
        gen_random_uuid(),
        'd4184f81-356a-4d28-82a3-c457fe645d41',
        'master',
        TRUE,
        TRUE,
        NOW(),
        NOW(),
        'system',
        'system'
    ) ON CONFLICT (user_id, tenant_id) DO UPDATE SET
        is_primary = EXCLUDED.is_primary,
        is_active = EXCLUDED.is_active,
        updated_at = NOW(),
        updated_by = EXCLUDED.updated_by
    RETURNING id INTO access_id;

    -- Insert the role into the user_tenant_access_roles table
    INSERT INTO user_tenant_access_roles (user_tenant_access_id, role)
    VALUES (access_id, 'SUPER_ADMIN')
    ON CONFLICT (user_tenant_access_id, role) DO NOTHING;
END $$;

-- ====================================================================
-- VERIFY INITIALIZATION
-- ====================================================================

-- Create a view to verify the setup
CREATE OR REPLACE VIEW v_master_tenant_setup AS
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
JOIN staff s ON uta.user_id = s.id::text
LEFT JOIN staff_roles sr ON s.id = sr.staff_id
LEFT JOIN user_tenant_access_roles utar ON uta.id = utar.user_tenant_access_id
WHERE t.tenant_id = 'master';

-- Log the initialization
DO $$
DECLARE
    tenant_count INTEGER;
    staff_count INTEGER;
    access_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO tenant_count FROM tenants WHERE tenant_id = 'master';
    SELECT COUNT(*) INTO staff_count FROM staff WHERE id = 'd4184f81-356a-4d28-82a3-c457fe645d41'::uuid;
    SELECT COUNT(*) INTO access_count FROM user_tenant_access WHERE user_id = 'd4184f81-356a-4d28-82a3-c457fe645d41' AND tenant_id = 'master';

    RAISE NOTICE 'Master tenant initialization completed:';
    RAISE NOTICE '- Tenants created: %', tenant_count;
    RAISE NOTICE '- Admin staff created: %', staff_count;
    RAISE NOTICE '- User tenant access records created: %', access_count;
END $$;

COMMIT;
