-- V13: Remove legacy patient_teeth and tooth_history tables
-- These tables have been replaced by JSONB-based dental_charts table

-- Drop tooth_history table first (has foreign key to patient_teeth)
DROP TABLE IF EXISTS tooth_conditions CASCADE;
