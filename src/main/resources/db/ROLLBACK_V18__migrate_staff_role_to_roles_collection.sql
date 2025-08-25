-- ====================================================================
-- ROLLBACK SCRIPT FOR V18 STAFF ROLES MIGRATION
-- 
-- WARNING: This rollback script should only be used if the migration
-- needs to be reversed. Staff with multiple roles will lose all but one role.
-- 
-- Usage: Execute this script manually if rollback is required
-- ====================================================================

BEGIN;

-- ====================================================================
-- STEP 1: ADD ROLE COLUMN BACK TO STAFF TABLE
-- ====================================================================

-- Add the single role column back to staff table
ALTER TABLE staff ADD COLUMN role VARCHAR(50);

-- ====================================================================
-- STEP 2: RESTORE ROLE DATA FROM STAFF_ROLES
-- ====================================================================

-- Restore role data - if staff has multiple roles, only one will be kept
-- Priority order: SUPER_ADMIN > ADMIN > DOCTOR > others
UPDATE staff SET role = (
    SELECT sr.role 
    FROM staff_roles sr 
    WHERE sr.staff_id = staff.id 
    ORDER BY 
        CASE sr.role
            WHEN 'SUPER_ADMIN' THEN 1
            WHEN 'ADMIN' THEN 2
            WHEN 'DOCTOR' THEN 3
            WHEN 'NURSE' THEN 4
            WHEN 'ASSISTANT' THEN 5
            WHEN 'RECEPTIONIST' THEN 6
            WHEN 'ACCOUNTANT' THEN 7
            WHEN 'EXTERNAL' THEN 8
            WHEN 'INTERNAL' THEN 9
            ELSE 10
        END
    LIMIT 1
);

-- ====================================================================
-- STEP 3: VERIFY ROLLBACK DATA
-- ====================================================================

DO $$
DECLARE
    staff_with_roles INTEGER;
    staff_without_roles INTEGER;
    multi_role_staff INTEGER;
BEGIN
    -- Count staff that now have roles
    SELECT COUNT(*) INTO staff_with_roles 
    FROM staff 
    WHERE role IS NOT NULL;
    
    -- Count staff that don't have roles
    SELECT COUNT(*) INTO staff_without_roles 
    FROM staff 
    WHERE role IS NULL;
    
    -- Count staff that had multiple roles (will show data loss)
    SELECT COUNT(*) INTO multi_role_staff 
    FROM (
        SELECT staff_id, COUNT(*) as role_count
        FROM staff_roles 
        GROUP BY staff_id 
        HAVING COUNT(*) > 1
    ) multi;
    
    RAISE NOTICE 'Rollback complete - Staff with roles: %, Staff without roles: %', 
        staff_with_roles, staff_without_roles;
        
    IF multi_role_staff > 0 THEN
        RAISE WARNING 'Data loss occurred: % staff members had multiple roles, only one role was preserved per staff member', 
            multi_role_staff;
    END IF;
END $$;

-- ====================================================================
-- STEP 4: DROP STAFF_ROLES TABLE
-- ====================================================================

-- Drop the staff_roles table
DROP TABLE staff_roles;

COMMIT;

-- ====================================================================
-- POST-ROLLBACK VERIFICATION
-- ====================================================================
-- 
-- After rollback, verify the staff table structure:
-- SELECT column_name, data_type, is_nullable 
-- FROM information_schema.columns 
-- WHERE table_name = 'staff' AND column_name = 'role';
-- 
-- Verify staff roles:
-- SELECT role, COUNT(*) 
-- FROM staff 
-- WHERE role IS NOT NULL 
-- GROUP BY role;
-- ====================================================================