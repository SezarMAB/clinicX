-- V13: Remove legacy patient_teeth and tooth_history tables
-- These tables have been replaced by JSONB-based dental_charts table

-- Drop tooth_history table first (has foreign key to patient_teeth)
DROP TABLE IF EXISTS tooth_history CASCADE;

-- Drop patient_teeth table
DROP TABLE IF EXISTS patient_teeth CASCADE;

-- Drop tooth_conditions table if no longer needed
-- Note: Keep this table if it's still used for condition reference data
-- DROP TABLE IF EXISTS tooth_conditions CASCADE;

-- Drop any remaining sequences if they exist
DROP SEQUENCE IF EXISTS tooth_history_seq CASCADE;

-- Clean up any orphaned indexes that might remain
DROP INDEX IF EXISTS idx_patient_teeth_patient_id;
DROP INDEX IF EXISTS idx_patient_teeth_tooth_number;
DROP INDEX IF EXISTS idx_tooth_history_patient_tooth;

-- Drop the view that was based on patient_teeth (already cascaded, but explicitly for clarity)
DROP VIEW IF EXISTS v_dental_chart CASCADE;


-- Log migration completion
DO $$
BEGIN
    RAISE NOTICE 'Migration V13 completed: Removed legacy patient_teeth and tooth_history tables';
END $$;
