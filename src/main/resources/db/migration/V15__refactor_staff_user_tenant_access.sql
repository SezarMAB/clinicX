-- ====================================================================
-- REFACTOR STAFF TABLE TO USE SEPARATE USER_TENANT_ACCESS
-- Version: 15.0 - Implement loose coupling architecture
-- ====================================================================

BEGIN;

-- ====================================================================
-- STEP 1: ENSURE USER_TENANT_ACCESS TABLE EXISTS (FROM V8)
-- ====================================================================

-- Table already exists from V8 migration, but let's ensure it has all columns
ALTER TABLE user_tenant_access 
    ALTER COLUMN is_primary SET DEFAULT FALSE,
    ALTER COLUMN is_active SET DEFAULT TRUE;

-- ====================================================================
-- STEP 2: MIGRATE EXISTING STAFF DATA TO USER_TENANT_ACCESS
-- ====================================================================

-- Skip migration if columns don't exist (for new installations)
DO $$
BEGIN
    -- Only migrate if the old columns exist
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'staff' 
        AND column_name = 'user_id'
    ) THEN
        -- Populate user_tenant_access from existing staff records
        INSERT INTO user_tenant_access (
            id,
            user_id,
            tenant_id,
            role,
            is_primary,
            is_active,
            created_at,
            updated_at
        )
        SELECT 
            gen_random_uuid(),
            s.user_id,
            s.tenant_id,
            s.role::text,
            COALESCE(s.is_primary, false),
            s.is_active,
            s.created_at,
            s.updated_at
        FROM staff s
        WHERE s.user_id IS NOT NULL 
          AND s.tenant_id IS NOT NULL
          AND NOT EXISTS (
            SELECT 1 FROM user_tenant_access uta 
            WHERE uta.user_id = s.user_id 
              AND uta.tenant_id = s.tenant_id
          );
    END IF;
END $$;

-- ====================================================================
-- STEP 3: REFACTOR STAFF TABLE STRUCTURE
-- ====================================================================

-- Add keycloak_user_id column if it doesn't exist
ALTER TABLE staff 
ADD COLUMN IF NOT EXISTS keycloak_user_id VARCHAR(255);

-- Copy user_id to keycloak_user_id if user_id exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'staff' 
        AND column_name = 'user_id'
    ) THEN
        UPDATE staff 
        SET keycloak_user_id = user_id 
        WHERE user_id IS NOT NULL AND keycloak_user_id IS NULL;
    END IF;
END $$;

-- Drop the old multi-tenant fields that now live in user_tenant_access
ALTER TABLE staff 
DROP COLUMN IF EXISTS user_id,
DROP COLUMN IF EXISTS is_primary;

-- Keep tenant_id for now (will be removed in schema-per-tenant migration)
-- It's still needed for data isolation in shared schema mode

-- ====================================================================
-- STEP 4: ADD INDEXES FOR PERFORMANCE
-- ====================================================================

CREATE INDEX IF NOT EXISTS idx_staff_keycloak_user_id 
ON staff(keycloak_user_id);

CREATE INDEX IF NOT EXISTS idx_staff_tenant_keycloak 
ON staff(tenant_id, keycloak_user_id);

-- Ensure indexes on user_tenant_access exist
CREATE INDEX IF NOT EXISTS idx_uta_user_tenant 
ON user_tenant_access(user_id, tenant_id);

CREATE INDEX IF NOT EXISTS idx_uta_tenant_active 
ON user_tenant_access(tenant_id, is_active);

CREATE INDEX IF NOT EXISTS idx_uta_user_active 
ON user_tenant_access(user_id, is_active);

-- ====================================================================
-- STEP 5: CREATE VIEW FOR CONVENIENT ACCESS TO STAFF WITH PERMISSIONS
-- ====================================================================

CREATE OR REPLACE VIEW v_staff_with_access AS
SELECT 
    s.*,
    uta.role as access_role,
    uta.is_primary,
    uta.is_active as access_active,
    t.name as tenant_name,
    t.subdomain as tenant_subdomain
FROM staff s
LEFT JOIN user_tenant_access uta 
    ON s.keycloak_user_id = uta.user_id 
    AND s.tenant_id = uta.tenant_id
LEFT JOIN tenants t 
    ON s.tenant_id = t.tenant_id
WHERE s.is_active = true;

-- ====================================================================
-- STEP 6: REMOVED DATABASE TRIGGER - HANDLED IN APPLICATION CODE
-- ====================================================================
-- The ensure_staff_has_access() trigger has been removed.
-- User access management is now handled entirely in the application layer
-- to avoid conflicts and provide better control over the process.

-- ====================================================================
-- STEP 7: CLEANUP AND VALIDATION
-- ====================================================================

-- Add comment to document the architecture
COMMENT ON COLUMN staff.keycloak_user_id IS 'Links to Keycloak user - loose coupling with user_tenant_access via this ID';
COMMENT ON COLUMN staff.tenant_id IS 'Tenant context - will be removed in schema-per-tenant migration';
COMMENT ON TABLE user_tenant_access IS 'Central authorization table mapping Keycloak users to tenants with roles';

-- Validate migration
DO $$
DECLARE
    v_orphaned_staff INTEGER;
BEGIN
    -- Check for staff without corresponding access records
    SELECT COUNT(*) INTO v_orphaned_staff
    FROM staff s
    WHERE s.keycloak_user_id IS NOT NULL 
      AND s.tenant_id IS NOT NULL
      AND NOT EXISTS (
          SELECT 1 FROM user_tenant_access uta
          WHERE uta.user_id = s.keycloak_user_id 
            AND uta.tenant_id = s.tenant_id
      );
    
    IF v_orphaned_staff > 0 THEN
        RAISE WARNING 'Found % staff records without user_tenant_access entries', v_orphaned_staff;
    END IF;
END $$;

COMMIT;