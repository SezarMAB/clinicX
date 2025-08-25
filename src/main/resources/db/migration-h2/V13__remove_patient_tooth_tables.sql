-- V13: Remove legacy patient_teeth and tooth_history tables (H2 version)
-- These tables have been replaced by JSON-based dental_charts table

-- Drop tooth_history table first (has foreign key to patient_teeth)
DROP TABLE IF EXISTS tooth_history CASCADE;

-- Drop patient_teeth table
DROP TABLE IF EXISTS patient_teeth CASCADE;

-- Drop tooth_conditions table if no longer needed
-- Note: Keep this table if it's still used for condition reference data
-- DROP TABLE IF EXISTS tooth_conditions CASCADE;

-- Drop any remaining sequences if they exist
DROP SEQUENCE IF EXISTS tooth_history_seq;

-- Drop the view that was based on patient_teeth
DROP VIEW IF EXISTS v_dental_chart CASCADE;

-- Note: H2 doesn't support COMMENT ON TABLE/COLUMN in the same way as PostgreSQL
-- The dental_charts table now replaces the legacy patient_teeth normalized structure