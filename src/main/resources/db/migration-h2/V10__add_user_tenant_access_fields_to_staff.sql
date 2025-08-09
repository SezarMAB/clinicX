-- Add UserTenantAccess fields to staff table
ALTER TABLE staff ADD COLUMN IF NOT EXISTS user_id VARCHAR(255);
ALTER TABLE staff ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE staff ADD COLUMN IF NOT EXISTS is_primary BOOLEAN NOT NULL DEFAULT FALSE;

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_staff_user_id ON staff(user_id);
CREATE INDEX IF NOT EXISTS idx_staff_tenant_id ON staff(tenant_id);
CREATE INDEX IF NOT EXISTS idx_staff_user_tenant ON staff(user_id, tenant_id);