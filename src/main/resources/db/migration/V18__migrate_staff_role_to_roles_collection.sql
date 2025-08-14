-- ====================================================================
-- MIGRATE STAFF ROLE TO ROLES COLLECTION
-- Version: 18
-- 
-- Description: Migrates from single staff.role column to Set<StaffRole> 
-- using @ElementCollection with separate staff_roles table
-- 
-- Changes:
-- 1. Create staff_roles table for @ElementCollection mapping
-- 2. Migrate existing role data from staff.role to staff_roles
-- 3. Drop the old role column from staff table
-- 
-- Risk Assessment: LOW
-- - Data migration preserves all existing role assignments
-- - Rollback available if needed within same transaction
-- - No foreign key constraints affected
-- 
-- Performance Impact: MINIMAL
-- - Small table operation
-- - No long-running queries
-- - Minimal downtime
-- ====================================================================

BEGIN;

-- ====================================================================
-- STEP 1: CREATE STAFF_ROLES TABLE
-- ====================================================================

-- Create staff_roles table for @ElementCollection mapping
-- This table will store the many-to-many relationship between staff and their roles
CREATE TABLE staff_roles (
    staff_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (staff_id, role),
    CONSTRAINT fk_staff_roles_staff_id 
        FOREIGN KEY (staff_id) 
        REFERENCES staff(id) 
        ON DELETE CASCADE
);

-- Add index for performance on staff_id lookups
CREATE INDEX idx_staff_roles_staff_id ON staff_roles(staff_id);

-- ====================================================================
-- STEP 2: MIGRATE EXISTING DATA
-- ====================================================================

-- Insert existing role data from staff.role into staff_roles table
-- This preserves all current role assignments
INSERT INTO staff_roles (staff_id, role)
SELECT 
    id as staff_id,
    role
FROM staff 
WHERE role IS NOT NULL
  AND role != '';

-- Verify data migration
DO $$
DECLARE
    staff_count INTEGER;
    roles_count INTEGER;
BEGIN
    -- Count staff with non-null roles
    SELECT COUNT(*) INTO staff_count 
    FROM staff 
    WHERE role IS NOT NULL AND role != '';
    
    -- Count migrated roles
    SELECT COUNT(*) INTO roles_count 
    FROM staff_roles;
    
    -- Ensure counts match
    IF staff_count != roles_count THEN
        RAISE EXCEPTION 'Data migration verification failed: staff count = %, roles count = %', 
            staff_count, roles_count;
    END IF;
    
    RAISE NOTICE 'Data migration successful: % staff roles migrated', roles_count;
END $$;

-- ====================================================================
-- STEP 3: UPDATE DEPENDENT OBJECTS AND DROP OLD ROLE COLUMN
-- ====================================================================

-- Drop the view that depends on the role column
DROP VIEW IF EXISTS v_staff_with_access;

-- Drop the old single role column since we now use the staff_roles table
ALTER TABLE staff DROP COLUMN role;

-- Recreate the view without the role column dependency (simplified without tenant table)
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

-- ====================================================================
-- VERIFICATION QUERIES (for manual testing)
-- ====================================================================

-- Query to verify migration success (commented for production)
-- SELECT 
--     s.id,
--     s.full_name,
--     s.email,
--     sr.role
-- FROM staff s
-- LEFT JOIN staff_roles sr ON s.id = sr.staff_id
-- ORDER BY s.full_name, sr.role;

COMMIT;

-- ====================================================================
-- ROLLBACK INSTRUCTIONS
-- ====================================================================
-- 
-- In case rollback is needed (must be done manually):
-- 
-- 1. Add role column back to staff table:
--    ALTER TABLE staff ADD COLUMN role VARCHAR(50);
-- 
-- 2. Restore role data (choose appropriate role if staff has multiple):
--    UPDATE staff SET role = (
--        SELECT sr.role 
--        FROM staff_roles sr 
--        WHERE sr.staff_id = staff.id 
--        LIMIT 1
--    );
-- 
-- 3. Drop staff_roles table:
--    DROP TABLE staff_roles;
-- 
-- Note: If staff had multiple roles, only one will be preserved in rollback
-- ====================================================================