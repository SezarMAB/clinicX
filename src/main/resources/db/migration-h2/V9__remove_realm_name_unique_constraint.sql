-- Remove unique constraint on realm_name to support realm-per-type architecture
-- In realm-per-type, multiple tenants can share the same realm based on their specialty

-- H2 syntax for dropping constraint
ALTER TABLE tenants DROP CONSTRAINT IF EXISTS tenants_realm_name_key;

-- H2 doesn't support COMMENT ON COLUMN, so we'll skip that part