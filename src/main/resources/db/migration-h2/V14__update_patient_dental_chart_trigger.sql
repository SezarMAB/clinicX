-- V14: Update patient dental chart initialization for H2
-- H2 doesn't support triggers/functions like PostgreSQL, so this is a simplified version
-- Dental chart initialization will be handled at the application level

-- This migration is intentionally left mostly empty as H2 doesn't support:
-- 1. PL/pgSQL functions
-- 2. Triggers
-- 3. DO blocks

-- The application layer will handle:
-- 1. Automatic dental chart creation when a patient is created
-- 2. Updating chart metadata when chart data is modified

-- Add comment to document the migration
-- Note: H2 doesn't support COMMENT ON statements, so we'll just document this in comments
-- The dental charts table was already created in V11 with proper structure