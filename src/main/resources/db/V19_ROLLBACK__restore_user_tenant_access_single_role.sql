-- ====================================================================
-- ROLLBACK SCRIPT FOR V19: RESTORE USER_TENANT_ACCESS SINGLE ROLE
-- 
-- IMPORTANT: This is a ROLLBACK script and should only be used if
-- V19 migration needs to be reversed. Do NOT run this during normal
-- operation.
-- 
-- WARNING: If users had multiple roles, only the first role found
-- will be preserved during rollback. Data loss may occur.
-- ====================================================================

BEGIN;

-- ====================================================================
-- STEP 1: ADD ROLE COLUMN BACK TO USER_TENANT_ACCESS
-- ====================================================================

-- Add the role column back to user_tenant_access table
ALTER TABLE user_tenant_access ADD COLUMN role VARCHAR(50);

-- ====================================================================
-- STEP 2: RESTORE ROLE DATA FROM COLLECTION TABLE
-- ====================================================================

-- Restore role data from user_tenant_access_roles to user_tenant_access.role
-- Note: If a user has multiple roles, only the first one alphabetically will be kept
UPDATE user_tenant_access SET role = (
    SELECT utar.role 
    FROM user_tenant_access_roles utar 
    WHERE utar.user_tenant_access_id = user_tenant_access.id 
    ORDER BY utar.role ASC  -- Choose first role alphabetically for consistency
    LIMIT 1
);

-- Verify rollback data migration
DO $$
DECLARE
    access_with_roles INTEGER;
    access_restored INTEGER;
    multiple_roles_count INTEGER;
BEGIN
    -- Count distinct user_tenant_access records that had roles
    SELECT COUNT(DISTINCT user_tenant_access_id) INTO access_with_roles 
    FROM user_tenant_access_roles;
    
    -- Count user_tenant_access records that now have roles restored
    SELECT COUNT(*) INTO access_restored 
    FROM user_tenant_access 
    WHERE role IS NOT NULL AND role != '';
    
    -- Count user_tenant_access records that had multiple roles (potential data loss)
    SELECT COUNT(*) INTO multiple_roles_count
    FROM (
        SELECT user_tenant_access_id 
        FROM user_tenant_access_roles 
        GROUP BY user_tenant_access_id 
        HAVING COUNT(*) > 1
    ) subq;
    
    -- Ensure counts match
    IF access_with_roles != access_restored THEN
        RAISE EXCEPTION 'Rollback verification failed: records with roles = %, restored = %', 
            access_with_roles, access_restored;
    END IF;
    
    RAISE NOTICE 'Rollback successful: % user tenant access roles restored', access_restored;
    
    IF multiple_roles_count > 0 THEN
        RAISE WARNING 'DATA LOSS WARNING: % user_tenant_access records had multiple roles. Only first role was preserved.', 
            multiple_roles_count;
    END IF;
END $$;

-- ====================================================================
-- STEP 3: DROP COLLECTION TABLE AND RESTORE ORIGINAL VIEW
-- ====================================================================

-- Drop the collection table
DROP TABLE user_tenant_access_roles;

-- Recreate the original view structure
DROP VIEW IF EXISTS v_staff_with_access;

CREATE OR REPLACE VIEW v_staff_with_access AS
SELECT 
    s.id,
    s.full_name,
    s.email,
    s.phone_number,
    s.is_active,
    s.keycloak_user_id,
    s.tenant_id,
    s.created_at,
    s.updated_at,
    uta.role as access_role,
    uta.is_primary,
    uta.is_active as access_active
FROM staff s
LEFT JOIN user_tenant_access uta 
    ON s.keycloak_user_id = uta.user_id
    AND s.tenant_id = uta.tenant_id;

COMMIT;

-- ====================================================================
-- ROLLBACK VERIFICATION QUERY
-- ====================================================================
-- 
-- Query to verify rollback success (commented for production):
-- SELECT 
--     uta.id,
--     uta.user_id,
--     uta.tenant_id,
--     uta.role,
--     uta.is_primary,
--     uta.is_active
-- FROM user_tenant_access uta
-- WHERE uta.role IS NOT NULL
-- ORDER BY uta.user_id, uta.tenant_id;
-- ====================================================================