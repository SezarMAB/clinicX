-- Add source_realm column to track which realm users originally come from
-- This is needed for cross-realm user access support (H2 Version)
ALTER TABLE staff 
ADD COLUMN IF NOT EXISTS source_realm VARCHAR(255);

-- Add index for performance when querying by source realm
CREATE INDEX IF NOT EXISTS idx_staff_source_realm ON staff(source_realm);

-- Note: H2 doesn't support COMMENT ON statements
-- Documentation: The source_realm column stores the Keycloak realm where the user account 
-- originally exists. Used for cross-realm access scenarios.