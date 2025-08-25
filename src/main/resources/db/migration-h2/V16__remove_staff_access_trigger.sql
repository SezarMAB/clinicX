-- ====================================================================
-- REMOVE STAFF ACCESS TRIGGER
-- Version: 16.0 - Remove automatic trigger to avoid conflicts (H2 version)
-- ====================================================================

-- H2 doesn't support triggers/functions like PostgreSQL
-- This migration is kept for consistency with PostgreSQL migrations

-- In H2, we don't have triggers to drop
-- The application layer handles all user_tenant_access management

-- Note: H2 doesn't support COMMENT ON statements
-- Documentation: user_tenant_access records are created by application when keycloak_user_id is present