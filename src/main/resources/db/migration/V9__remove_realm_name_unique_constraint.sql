-- Remove unique constraint on realm_name to support realm-per-type architecture
-- In realm-per-type, multiple tenants can share the same realm based on their specialty

ALTER TABLE tenants DROP CONSTRAINT IF EXISTS tenants_realm_name_key;

-- Add comment to document the change
COMMENT ON COLUMN tenants.realm_name IS 'Keycloak realm name - shared by tenants of the same specialty in realm-per-type architecture';