-- ====================================================================
-- MIGRATE USER_TENANT_ACCESS ROLE TO ROLES COLLECTION (H2 Version)
-- Version: 19
-- 
-- Description: Migrates from single user_tenant_access.role column to Set<StaffRole> 
-- using @ElementCollection with separate user_tenant_access_roles table
-- ====================================================================

-- ====================================================================
-- STEP 1: CREATE USER_TENANT_ACCESS_ROLES TABLE
-- ====================================================================

-- Create user_tenant_access_roles table for @ElementCollection mapping
-- This table will store the many-to-many relationship between user_tenant_access and roles
CREATE TABLE user_tenant_access_roles (
    user_tenant_access_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_tenant_access_id, role),
    CONSTRAINT fk_user_tenant_access_roles_user_tenant_access_id 
        FOREIGN KEY (user_tenant_access_id) 
        REFERENCES user_tenant_access(id) 
        ON DELETE CASCADE
);

-- Add index for performance on user_tenant_access_id lookups
CREATE INDEX idx_user_tenant_access_roles_user_tenant_access_id 
ON user_tenant_access_roles(user_tenant_access_id);

-- ====================================================================
-- STEP 2: MIGRATE EXISTING DATA
-- ====================================================================

-- Insert existing role data from user_tenant_access.role into user_tenant_access_roles table
-- This preserves all current role assignments
INSERT INTO user_tenant_access_roles (user_tenant_access_id, role)
SELECT 
    id as user_tenant_access_id,
    role
FROM user_tenant_access 
WHERE role IS NOT NULL
  AND role != '';

-- ====================================================================
-- STEP 3: UPDATE DEPENDENT OBJECTS AND DROP OLD ROLE COLUMN
-- ====================================================================

-- Drop the view that depends on the role column
DROP VIEW IF EXISTS v_staff_with_access;

-- Drop the old single role column since we now use the user_tenant_access_roles table
ALTER TABLE user_tenant_access DROP COLUMN role;

-- Recreate the view to work with the new roles collection structure
-- Note: This view will show multiple rows per staff member if they have multiple roles
-- This is the expected behavior when transitioning to a roles collection
CREATE VIEW v_staff_with_access AS
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
    utar.role as access_role,
    uta.is_primary,
    uta.is_active as access_active
FROM staff s
LEFT JOIN user_tenant_access uta 
    ON s.keycloak_user_id = uta.user_id
    AND s.tenant_id = uta.tenant_id
LEFT JOIN user_tenant_access_roles utar
    ON uta.id = utar.user_tenant_access_id;

-- ====================================================================
-- ROLLBACK INSTRUCTIONS (for reference)
-- ====================================================================
-- 
-- In case rollback is needed (must be done manually):
-- 
-- 1. Add role column back to user_tenant_access table:
--    ALTER TABLE user_tenant_access ADD COLUMN role VARCHAR(50);
-- 
-- 2. Restore role data (choose appropriate role if user has multiple):
--    UPDATE user_tenant_access SET role = (
--        SELECT utar.role 
--        FROM user_tenant_access_roles utar 
--        WHERE utar.user_tenant_access_id = user_tenant_access.id 
--        LIMIT 1
--    );
-- 
-- 3. Drop user_tenant_access_roles table:
--    DROP TABLE user_tenant_access_roles;
-- 
-- 4. Recreate the original view:
--    CREATE OR REPLACE VIEW v_staff_with_access AS
--    SELECT 
--        s.id, s.full_name, s.email, s.phone_number, s.is_active, s.keycloak_user_id,
--        s.tenant_id, s.created_at, s.updated_at,
--        uta.role as access_role, uta.is_primary, uta.is_active as access_active
--    FROM staff s
--    LEFT JOIN user_tenant_access uta 
--        ON s.keycloak_user_id = uta.user_id AND s.tenant_id = uta.tenant_id;
-- 
-- Note: If users had multiple roles, only one will be preserved in rollback
-- ====================================================================