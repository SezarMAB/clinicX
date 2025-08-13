-- V13: Fix staff email constraint and add keycloak_user_id column
-- This migration modifies the unique constraint to be per tenant and adds keycloak_user_id

-- Step 1: Add keycloak_user_id column if it doesn't exist
ALTER TABLE staff ADD COLUMN IF NOT EXISTS keycloak_user_id VARCHAR(255);

-- Step 2: Migrate existing user_id to keycloak_user_id if needed
-- UPDATE staff SET keycloak_user_id = user_id WHERE keycloak_user_id IS NULL AND user_id IS NOT NULL;

-- Step 3: Drop the existing unique constraint on email alone
ALTER TABLE staff DROP CONSTRAINT IF EXISTS staff_email_key;

-- Step 4: Create a new unique constraint that includes tenant_id
-- This allows the same email to be used across different tenants
ALTER TABLE staff DROP CONSTRAINT IF EXISTS staff_email_tenant_unique;
ALTER TABLE staff ADD CONSTRAINT staff_email_tenant_unique UNIQUE (email, tenant_id);

-- Step 5: Add unique constraint for keycloak_user_id and tenant_id
ALTER TABLE staff DROP CONSTRAINT IF EXISTS staff_keycloak_tenant_unique;
ALTER TABLE staff ADD CONSTRAINT staff_keycloak_tenant_unique UNIQUE (keycloak_user_id, tenant_id);

-- Step 6: Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_staff_email ON staff(email);
CREATE INDEX IF NOT EXISTS idx_staff_keycloak_user_id ON staff(keycloak_user_id);
CREATE INDEX IF NOT EXISTS idx_staff_keycloak_user_tenant ON staff(keycloak_user_id, tenant_id);

-- Step 7: Add comment to explain the constraints
COMMENT ON CONSTRAINT staff_email_tenant_unique ON staff IS 'Ensures email uniqueness per tenant, allowing same user to work in multiple tenants';
COMMENT ON CONSTRAINT staff_keycloak_tenant_unique ON staff IS 'Ensures one staff record per user per tenant';
