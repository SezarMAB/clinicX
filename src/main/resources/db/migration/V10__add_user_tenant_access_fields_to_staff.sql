-- Add UserTenantAccess fields to staff table
ALTER TABLE staff 
ADD COLUMN IF NOT EXISTS user_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS is_primary BOOLEAN NOT NULL DEFAULT FALSE;

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_staff_user_id ON staff(user_id);
CREATE INDEX IF NOT EXISTS idx_staff_tenant_id ON staff(tenant_id);
CREATE INDEX IF NOT EXISTS idx_staff_user_tenant ON staff(user_id, tenant_id);

-- Add comment to explain the purpose of these fields
COMMENT ON COLUMN staff.user_id IS 'Keycloak user ID associated with this staff member';
COMMENT ON COLUMN staff.tenant_id IS 'Tenant ID this staff member belongs to';
COMMENT ON COLUMN staff.is_primary IS 'Whether this is the primary tenant for this user';