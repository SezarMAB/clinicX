-- ====================================================================
-- REMOVE STAFF ACCESS TRIGGER
-- Version: 16.0 - Remove automatic trigger to avoid conflicts
-- ====================================================================

BEGIN;

-- Drop the trigger if it exists
DROP TRIGGER IF EXISTS trg_ensure_staff_access ON staff;

-- Drop the function if it exists
DROP FUNCTION IF EXISTS ensure_staff_has_access();

-- Add comment to document the change
COMMENT ON TABLE user_tenant_access IS 'Central authorization table mapping Keycloak users to tenants with roles - managed by application code';
COMMENT ON TABLE staff IS 'Staff business data - user_tenant_access records are created by application when keycloak_user_id is present';

COMMIT;