-- ====================================================================
-- DENTAL CLINIC MOCKUP ALIGNMENT MIGRATION
-- Version: 2.0 - Complete mockup alignment
-- Purpose: Add missing components to align with UI mockup requirements
-- ====================================================================

BEGIN;

-- ====================================================================
-- visit MATERIALS TABLE
-- ====================================================================

CREATE TABLE treatment_materials (
    id               UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    visit_id     UUID           NOT NULL REFERENCES visits (id) ON DELETE CASCADE,
    material_name    VARCHAR(100)   NOT NULL,
    quantity         DECIMAL(10, 3) NOT NULL,
    unit             VARCHAR(20),
    cost_per_unit    DECIMAL(10, 2) NOT NULL,
    total_cost       DECIMAL(10, 2) NOT NULL,
    supplier         VARCHAR(100),
    batch_number     VARCHAR(50),
    notes            TEXT,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_cost_per_unit_positive CHECK (cost_per_unit >= 0),
    CONSTRAINT chk_total_cost_positive CHECK (total_cost >= 0)
);

-- ====================================================================
-- INDEXES FOR visit MATERIALS
-- ====================================================================

CREATE INDEX idx_treatment_materials_treatment ON treatment_materials(visit_id);
CREATE INDEX idx_treatment_materials_name ON treatment_materials(material_name);
CREATE INDEX idx_treatment_materials_supplier ON treatment_materials(supplier);
CREATE INDEX idx_treatment_materials_created_at ON treatment_materials(created_at);

-- ====================================================================
-- ENHANCED APPOINTMENT STATUS ENUM VALUES
-- ====================================================================

-- Add PENDING status to appointment statuses (if not already exists)
-- This migration is idempotent and will not fail if these values already exist

-- The appointment statuses are handled by the enum in the Java code
-- No additional database changes needed for appointment enums

-- ====================================================================
-- ADDITIONAL INDEXES FOR PERFORMANCE OPTIMIZATION
-- ====================================================================

-- Enhanced search capabilities (Note: CONCURRENTLY removed for transactional compatibility)
CREATE INDEX IF NOT EXISTS idx_patients_full_name_lower ON patients(lower(full_name));
CREATE INDEX IF NOT EXISTS idx_patients_email_lower ON patients(lower(email)) WHERE email IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_visits_date ON visits(visit_date);
CREATE INDEX IF NOT EXISTS idx_visits_status ON visits(status);
CREATE INDEX IF NOT EXISTS idx_visits_tooth_number ON visits(tooth_number) WHERE tooth_number IS NOT NULL;

-- ====================================================================
-- ENHANCED VIEWS FOR visit MATERIALS
-- ====================================================================

-- View to get visit material summary by visit
CREATE VIEW v_treatment_material_summary AS
SELECT
    tm.visit_id,
    COUNT(tm.id) as material_count,
    SUM(tm.total_cost) as total_material_cost,
    STRING_AGG(tm.material_name, ', ' ORDER BY tm.material_name) as materials_used
FROM treatment_materials tm
GROUP BY tm.visit_id;

-- View to get material usage statistics
CREATE VIEW v_material_usage_stats AS
SELECT
    tm.material_name,
    COUNT(tm.id) as usage_count,
    SUM(tm.quantity) as total_quantity_used,
    AVG(tm.cost_per_unit) as avg_cost_per_unit,
    SUM(tm.total_cost) as total_cost,
    MAX(tm.created_at) as last_used_date
FROM treatment_materials tm
GROUP BY tm.material_name
ORDER BY usage_count DESC;

-- Enhanced patient financial summary with material costs
CREATE OR REPLACE VIEW v_patient_financial_summary AS
SELECT
    p.id,
    p.full_name,
    p.public_facing_id,
    p.balance,
    COUNT(DISTINCT i.id) AS total_invoices,
    COUNT(DISTINCT i.id) FILTER (WHERE i.status = 'UNPAID') AS unpaid_invoices,
    COALESCE(SUM(i.total_amount) FILTER (WHERE i.status = 'UNPAID'), 0) AS total_unpaid,
    COALESCE(SUM(tm.total_cost), 0) AS total_material_costs,
    COUNT(DISTINCT t.id) AS total_visits
FROM patients p
    LEFT JOIN invoices i ON p.id = i.patient_id
    LEFT JOIN visits t ON p.id = t.patient_id
    LEFT JOIN treatment_materials tm ON t.id = tm.visit_id
GROUP BY p.id, p.full_name, p.public_facing_id, p.balance;

-- ====================================================================
-- FUNCTIONS FOR MATERIAL COST CALCULATIONS
-- ====================================================================

-- Function to calculate total material cost for a visit
CREATE OR REPLACE FUNCTION get_treatment_material_cost(visits_uuid UUID)
RETURNS DECIMAL(10,2) AS $$
BEGIN
    RETURN COALESCE(
        (SELECT SUM(total_cost)
         FROM treatment_materials
         WHERE visit_id = visits_uuid),
        0.00
    );
END;
$$ LANGUAGE plpgsql;

-- Function to calculate total material cost for a patient
CREATE OR REPLACE FUNCTION get_patient_material_cost(patient_uuid UUID)
RETURNS DECIMAL(10,2) AS $$
BEGIN
    RETURN COALESCE(
        (SELECT SUM(tm.total_cost)
         FROM treatment_materials tm
         JOIN visits t ON tm.visit_id = t.id
         WHERE t.patient_id = patient_uuid),
        0.00
    );
END;
$$ LANGUAGE plpgsql;

-- ====================================================================
-- TRIGGER FOR AUTOMATIC TOTAL COST CALCULATION
-- ====================================================================

-- Function to automatically calculate total cost
CREATE OR REPLACE FUNCTION calculate_material_total_cost()
RETURNS TRIGGER AS $$
BEGIN
    NEW.total_cost := NEW.quantity * NEW.cost_per_unit;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for automatic total cost calculation
CREATE TRIGGER trg_calculate_material_total_cost
    BEFORE INSERT OR UPDATE OF quantity, cost_per_unit ON treatment_materials
    FOR EACH ROW EXECUTE FUNCTION calculate_material_total_cost();

-- ====================================================================
-- UPDATE EXISTING visit STATUSES (IF NEEDED)
-- ====================================================================

-- Ensure all visits have valid statuses
-- This is handled by the Java enum, no database changes needed

-- ====================================================================
-- ENHANCED CONSTRAINTS AND VALIDATIONS
-- ====================================================================

-- Add constraint to ensure material names are not empty
ALTER TABLE treatment_materials
ADD CONSTRAINT chk_material_name_not_empty
CHECK (LENGTH(TRIM(material_name)) > 0);

-- ====================================================================
-- GRANT PERMISSIONS (IF USING SPECIFIC DATABASE USERS)
-- ====================================================================

-- These would be uncommented if using specific database users
-- GRANT SELECT, INSERT, UPDATE, DELETE ON treatment_materials TO clinic_app_user;
-- GRANT USAGE ON SEQUENCE treatment_materials_id_seq TO clinic_app_user;

-- ====================================================================
-- COMMIT TRANSACTION
-- ====================================================================

COMMIT;
