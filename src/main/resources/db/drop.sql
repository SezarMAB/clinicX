-- ====================================================================
-- DROP SCRIPT FOR MULTI-SPECIALTY CLINIC DATABASE SCHEMA
-- ====================================================================
-- This script drops all database objects in the correct order to handle dependencies
-- Run this script to completely remove the schema

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

DROP FUNCTION IF EXISTS get_dental_chart_for_ui(UUID);
DROP FUNCTION IF EXISTS get_patient_tooth_history(UUID, INT);
DROP FUNCTION IF EXISTS update_tooth_from_treatment();
DROP FUNCTION IF EXISTS link_treatment_to_tooth();
DROP FUNCTION IF EXISTS track_tooth_history();
DROP FUNCTION IF EXISTS initialize_patient_teeth();
DROP FUNCTION IF EXISTS prevent_last_specialty_deletion();
DROP FUNCTION IF EXISTS audit_trigger_function();
DROP FUNCTION IF EXISTS validate_appointment_schedule();
DROP FUNCTION IF EXISTS update_appointment_slot();
DROP FUNCTION IF EXISTS update_patient_balance();

-- ====================================================================
-- STEP 3: DROP VIEWS
-- ====================================================================

DROP VIEW IF EXISTS v_staff_availability_today;
DROP VIEW IF EXISTS v_patient_financial_summary;
DROP VIEW IF EXISTS v_upcoming_appointments;
DROP VIEW IF EXISTS v_patient_dental_summary;
DROP VIEW IF EXISTS v_teeth_requiring_attention;
DROP VIEW IF EXISTS v_dental_chart;

-- ====================================================================
-- STEP 4: DROP INDEXES (that aren't automatically dropped with tables)
-- ====================================================================

-- Note: Most indexes will be automatically dropped with their tables
-- Only drop indexes that might have been created separately
DROP INDEX IF EXISTS idx_dental_charts_data;

-- ====================================================================
-- STEP 5: DROP TABLES (in dependency order)
-- ====================================================================

-- Drop audit and security tables
DROP TABLE IF EXISTS login_attempts;
DROP TABLE IF EXISTS audit_log;

-- Drop referral table
DROP TABLE IF EXISTS referrals;

-- Drop supporting tables
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS lab_requests;
DROP TABLE IF EXISTS patient_tags;
DROP TABLE IF EXISTS tags;

-- Drop financial tables
DROP TABLE IF EXISTS insurance_authorizations;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS invoice_items;
DROP TABLE IF EXISTS invoices;

-- Drop scheduling tables
DROP TABLE IF EXISTS appointment_slots;
DROP TABLE IF EXISTS schedule_overrides;
DROP TABLE IF EXISTS staff_schedules;

-- Drop tooth-related tables
DROP TABLE IF EXISTS tooth_surface_conditions;
DROP TABLE IF EXISTS tooth_surfaces;
DROP TABLE IF EXISTS tooth_measurements;
DROP TABLE IF EXISTS tooth_history;
DROP TABLE IF EXISTS patient_teeth;
DROP TABLE IF EXISTS tooth_conditions;

-- Drop medical records tables
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS procedures;

-- Drop patient tables
DROP TABLE IF EXISTS patients;

-- Drop staff tables
DROP TABLE IF EXISTS staff_specialties;
DROP TABLE IF EXISTS staff;

-- Drop core tables
DROP TABLE IF EXISTS specialties;
DROP TABLE IF EXISTS clinic_info;

-- ====================================================================
-- STEP 6: DROP SEQUENCES
-- ====================================================================

DROP SEQUENCE IF EXISTS invoice_number_seq;

-- ====================================================================
-- STEP 7: DROP EXTENSIONS (optional - comment out if used by other schemas)
-- ====================================================================

DROP EXTENSION IF EXISTS btree_gist;
DROP EXTENSION IF EXISTS pgcrypto;

COMMIT;

-- ====================================================================
-- VERIFICATION QUERIES (run these to ensure everything was dropped)
-- ====================================================================

-- Check for remaining tables
-- SELECT table_name FROM information_schema.tables
-- WHERE table_schema = 'public'
-- AND table_type = 'BASE TABLE';

-- Check for remaining views
-- SELECT table_name FROM information_schema.views
-- WHERE table_schema = 'public';

-- Check for remaining functions
-- SELECT routine_name FROM information_schema.routines
-- WHERE routine_schema = 'public'
-- AND routine_type = 'FUNCTION';

-- Check for remaining triggers
-- SELECT trigger_name, event_object_table
-- FROM information_schema.triggers
-- WHERE trigger_schema = 'public';

-- Check for remaining sequences
-- SELECT sequence_name FROM information_schema.sequences
-- WHERE sequence_schema = 'public';
