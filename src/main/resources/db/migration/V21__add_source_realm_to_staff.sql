-- Add source_realm column to track which realm users originally come from
-- This is needed for cross-realm user access support
ALTER TABLE staff 
ADD COLUMN IF NOT EXISTS source_realm VARCHAR(255);

-- Add index for performance when querying by source realm
CREATE INDEX IF NOT EXISTS idx_staff_source_realm ON staff(source_realm);

-- Add comment to document the column
COMMENT ON COLUMN staff.source_realm IS 'The Keycloak realm where the user account originally exists. Used for cross-realm access scenarios.';
