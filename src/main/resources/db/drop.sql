-- DROP SCRIPT FOR MULTI-SPECIALTY CLINIC DATABASE SCHEMA (WITH CASCADE)
-- ====================================================================
-- This script drops all database objects using CASCADE for tables
-- CASCADE will automatically drop all dependent objects
-- WARNING: Use with caution as CASCADE is destructive!
-- Updated: Added missing functions from routines
-- ====================================================================
rollback ;
BEGIN;

-- ====================================================================
-- STEP 1: DROP TRIGGERS
-- ====================================================================

-- Drop audit triggers
DROP TRIGGER IF EXISTS audit_tooth_history ON tooth_history;
DROP TRIGGER IF EXISTS audit_patient_teeth ON patient_teeth;
DROP TRIGGER IF EXISTS audit_payments ON payments;
DROP TRIGGER IF EXISTS audit_invoices ON invoices;
DROP TRIGGER IF EXISTS audit_treatments ON treatments;
DROP TRIGGER IF EXISTS audit_appointments ON appointments;
DROP TRIGGER IF EXISTS audit_patients ON patients;

-- Drop tooth-related triggers
DROP TRIGGER IF EXISTS trg_update_tooth_from_treatment ON treatments;
DROP TRIGGER IF EXISTS trg_link_treatment_to_tooth ON treatments;
DROP TRIGGER IF EXISTS trg_track_tooth_history ON patient_teeth;
DROP TRIGGER IF EXISTS trg_initialize_patient_teeth ON patients;

-- Drop other triggers
DROP TRIGGER IF EXISTS trg_prevent_last_specialty ON specialties;
DROP TRIGGER IF EXISTS trg_validate_appointment_schedule ON appointments;
DROP TRIGGER IF EXISTS trg_appointment_slot_update ON appointments;
DROP TRIGGER IF EXISTS trg_payments_balance_update ON payments;
DROP TRIGGER IF EXISTS trg_invoices_balance_update ON invoices;

-- ====================================================================
-- STEP 2: DROP FUNCTIONS
-- ====================================================================

-- Drop utility functions
DROP FUNCTION IF EXISTS appointment_end_time(timestamp with time zone, integer) CASCADE;
DROP FUNCTION IF EXISTS archive_old_appointments(integer);
DROP FUNCTION IF EXISTS cleanup_old_audit_logs(integer);

-- Drop dental functions
DROP FUNCTION IF EXISTS get_dental_chart_for_ui(UUID);
DROP FUNCTION IF EXISTS get_patient_tooth_history(UUID, INT);
DROP FUNCTION IF EXISTS update_tooth_from_treatment();
DROP FUNCTION IF EXISTS link_treatment_to_tooth();
DROP FUNCTION IF EXISTS track_tooth_history();
DROP FUNCTION IF EXISTS initialize_patient_teeth();

-- Drop other functions
DROP FUNCTION IF EXISTS prevent_last_specialty_deletion();
DROP FUNCTION IF EXISTS audit_trigger_function();
DROP FUNCTION IF EXISTS validate_appointment_schedule();
DROP FUNCTION IF EXISTS update_appointment_slot();
DROP FUNCTION IF EXISTS update_patient_balance();

-- Drop financial functions
DROP FUNCTION IF EXISTS update_updated_at_column();



-- ====================================================================
-- STEP 3: DROP VIEWS (Optional - CASCADE on tables will drop these too)
-- ====================================================================
-- These views will be automatically dropped by CASCADE when dropping tables
-- But we'll keep them here for clarity and in case tables don't exist

DROP VIEW IF EXISTS v_referral_tracking;
DROP VIEW IF EXISTS v_staff_availability_today;
DROP VIEW IF EXISTS v_patient_financial_summary;
DROP VIEW IF EXISTS v_upcoming_appointments;
DROP VIEW IF EXISTS v_patient_dental_summary;
DROP VIEW IF EXISTS v_teeth_requiring_attention;
DROP VIEW IF EXISTS v_dental_chart;

-- ====================================================================
-- STEP 4: DROP INDEXES (that aren't automatically dropped with tables)
-- ====================================================================

DROP INDEX IF EXISTS idx_dental_charts_data;

-- ====================================================================
-- STEP 5: DROP TABLES WITH CASCADE
-- ====================================================================
-- CASCADE will automatically drop all dependent objects (views, foreign keys, etc.)

-- Drop audit and security tables
DROP TABLE IF EXISTS login_attempts CASCADE;
DROP TABLE IF EXISTS audit_log CASCADE;

-- Drop referral table
DROP TABLE IF EXISTS referrals CASCADE;

-- Drop supporting tables
DROP TABLE IF EXISTS notes CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS documents CASCADE;
DROP TABLE IF EXISTS lab_requests CASCADE;
DROP TABLE IF EXISTS patient_tags CASCADE;
DROP TABLE IF EXISTS tags CASCADE;

-- Drop financial tables
DROP TABLE IF EXISTS insurance_authorizations CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS invoice_items CASCADE;
DROP TABLE IF EXISTS invoices CASCADE;

-- Drop scheduling tables
DROP TABLE IF EXISTS appointment_slots CASCADE;
DROP TABLE IF EXISTS schedule_overrides CASCADE;
DROP TABLE IF EXISTS staff_schedules CASCADE;

-- Drop tooth-related tables
DROP TABLE IF EXISTS tooth_surface_conditions CASCADE;
DROP TABLE IF EXISTS tooth_surfaces CASCADE;
DROP TABLE IF EXISTS tooth_measurements CASCADE;
DROP TABLE IF EXISTS tooth_history CASCADE;
DROP TABLE IF EXISTS patient_teeth CASCADE;
DROP TABLE IF EXISTS tooth_conditions CASCADE;

-- Drop medical records tables
DROP TABLE IF EXISTS treatments CASCADE;
DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS procedures CASCADE;

-- Drop patient tables
DROP TABLE IF EXISTS patients CASCADE;

-- Drop staff tables
DROP TABLE IF EXISTS staff_specialties CASCADE;
DROP TABLE IF EXISTS staff CASCADE;

-- Drop core tables
DROP TABLE IF EXISTS specialties CASCADE;
DROP TABLE IF EXISTS clinic_info CASCADE;

-- Drop Tenant Management tables
DROP TABLE IF EXISTS tenants CASCADE;



DROP TABLE IF EXISTS flyway_schema_history CASCADE;

-- ====================================================================
-- STEP 6: DROP SEQUENCES
-- ====================================================================

DROP SEQUENCE IF EXISTS invoice_number_seq;

-- ====================================================================
-- STEP 7: DROP EXTENSIONS (optional - comment out if used by other schemas)
-- ====================================================================

-- Uncomment these lines only if you're sure no other schemas use these extensions
DROP EXTENSION IF EXISTS btree_gist;
DROP EXTENSION IF EXISTS pgcrypto;


-- ================================
-- DROP TRIGGERS AND FUNCTIONS
-- ================================

DROP TRIGGER IF EXISTS trg_calculate_material_total_cost ON treatment_materials;

DROP FUNCTION IF EXISTS calculate_material_total_cost CASCADE;
DROP FUNCTION IF EXISTS get_treatment_material_cost(UUID) CASCADE;
DROP FUNCTION IF EXISTS get_patient_material_cost(UUID) CASCADE;

-- ================================
-- DROP VIEWS
-- ================================

DROP VIEW IF EXISTS v_patient_financial_summary;
DROP VIEW IF EXISTS v_material_usage_stats;
DROP VIEW IF EXISTS v_treatment_material_summary;

-- ================================
-- DROP INDEXES
-- ================================

DROP INDEX IF EXISTS idx_treatment_materials_created_at;
DROP INDEX IF EXISTS idx_treatment_materials_supplier;
DROP INDEX IF EXISTS idx_treatment_materials_name;
DROP INDEX IF EXISTS idx_treatment_materials_treatment;

DROP INDEX IF EXISTS idx_patients_full_name_lower;
DROP INDEX IF EXISTS idx_patients_email_lower;
DROP INDEX IF EXISTS idx_treatments_date;
DROP INDEX IF EXISTS idx_treatments_status;
DROP INDEX IF EXISTS idx_treatments_tooth_number;

-- ================================
-- DROP TABLES
-- ================================

DROP TABLE IF EXISTS treatment_materials CASCADE;

COMMIT;

-- ====================================================================
-- VERIFICATION QUERIES (run these to ensure everything was dropped)
-- ====================================================================

-- Check for remaining tables
SELECT 'Tables:', COUNT(*) as count FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE';

-- Check for remaining views
SELECT 'Views:', COUNT(*) as count FROM information_schema.views
WHERE table_schema = 'public';

-- Check for remaining functions
SELECT 'Functions:', COUNT(*) as count FROM information_schema.routines
WHERE routine_schema = 'public'
  AND routine_type = 'FUNCTION';

-- Check for remaining triggers
SELECT 'Triggers:', COUNT(*) as count FROM information_schema.triggers
WHERE trigger_schema = 'public';

-- Check for remaining sequences
SELECT 'Sequences:', COUNT(*) as count FROM information_schema.sequences
WHERE sequence_schema = 'public';

-- List any remaining functions (for debugging)
SELECT routine_name, routine_type
FROM information_schema.routines
WHERE routine_schema = 'public'
ORDER BY routine_type, routine_name;

-- ====================================================================
-- NUCLEAR OPTION: Drop entire schema and recreate
-- ====================================================================
-- If you want to completely wipe everything in the public schema:
-- WARNING: This will drop EVERYTHING in the public schema!
--
-- DROP SCHEMA public CASCADE;
-- CREATE SCHEMA public;
-- GRANT ALL ON SCHEMA public TO postgres;
-- GRANT ALL ON SCHEMA public TO public;
