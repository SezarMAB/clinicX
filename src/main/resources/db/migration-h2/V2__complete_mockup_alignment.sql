-- ====================================================================
-- DENTAL CLINIC MOCKUP ALIGNMENT MIGRATION (H2 Version)
-- Version: 2.0 - Complete mockup alignment
-- Purpose: Add missing components to align with UI mockup requirements
-- ====================================================================

-- ====================================================================
-- TREATMENT MATERIALS TABLE
-- ====================================================================

CREATE TABLE IF NOT EXISTS treatment_materials (
    id               UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    treatment_id     UUID           NOT NULL,
    material_name    VARCHAR(100)   NOT NULL,
    quantity         DECIMAL(10, 3) NOT NULL,
    unit             VARCHAR(20),
    cost_per_unit    DECIMAL(10, 2) NOT NULL,
    total_cost       DECIMAL(10, 2) NOT NULL,
    supplier         VARCHAR(100),
    batch_number     VARCHAR(50),
    notes            TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (treatment_id) REFERENCES treatments (id) ON DELETE CASCADE,
    CONSTRAINT chk_material_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_material_cost_per_unit_positive CHECK (cost_per_unit >= 0),
    CONSTRAINT chk_material_total_cost_positive CHECK (total_cost >= 0)
);

-- ====================================================================
-- INDEXES FOR TREATMENT MATERIALS
-- ====================================================================

CREATE INDEX IF NOT EXISTS idx_treatment_materials_treatment ON treatment_materials(treatment_id);
CREATE INDEX IF NOT EXISTS idx_treatment_materials_name ON treatment_materials(material_name);
CREATE INDEX IF NOT EXISTS idx_treatment_materials_supplier ON treatment_materials(supplier);
CREATE INDEX IF NOT EXISTS idx_treatment_materials_created_at ON treatment_materials(created_at);

-- ====================================================================
-- ADDITIONAL INDEXES FOR PERFORMANCE OPTIMIZATION
-- ====================================================================

-- Enhanced search capabilities (H2 doesn't support functional indexes)
-- CREATE INDEX IF NOT EXISTS idx_patients_full_name_lower ON patients(LOWER(full_name));
-- CREATE INDEX IF NOT EXISTS idx_patients_email_lower ON patients(LOWER(email));
CREATE INDEX IF NOT EXISTS idx_treatments_date ON treatments( visit_date);
CREATE INDEX IF NOT EXISTS idx_treatments_status ON treatments(status);
CREATE INDEX IF NOT EXISTS idx_treatments_tooth_number ON treatments(tooth_number);

-- ====================================================================
-- ENHANCED VIEWS FOR TREATMENT MATERIALS
-- ====================================================================

-- View to get treatment material summary by treatment
CREATE VIEW IF NOT EXISTS v_treatment_material_summary AS
SELECT
    tm.treatment_id,
    COUNT(tm.id) as material_count,
    SUM(tm.total_cost) as total_material_cost,
    LISTAGG(tm.material_name, ', ') WITHIN GROUP (ORDER BY tm.material_name) as materials_used
FROM treatment_materials tm
GROUP BY tm.treatment_id;

-- View to get material usage statistics
CREATE VIEW IF NOT EXISTS v_material_usage_stats AS
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
    COUNT(DISTINCT CASE WHEN i.status = 'UNPAID' THEN i.id END) AS unpaid_invoices,
    COALESCE(SUM(CASE WHEN i.status = 'UNPAID' THEN i.total_amount END), 0) AS total_unpaid,
    COALESCE(SUM(tm.total_cost), 0) AS total_material_costs,
    COUNT(DISTINCT t.id) AS total_treatments
FROM patients p
    LEFT JOIN invoices i ON p.id = i.patient_id
    LEFT JOIN treatments t ON p.id = t.patient_id
    LEFT JOIN treatment_materials tm ON t.id = tm.treatment_id
GROUP BY p.id, p.full_name, p.public_facing_id, p.balance;

-- ====================================================================
-- FUNCTIONS FOR MATERIAL COST CALCULATIONS
-- ====================================================================

-- H2 doesn't support stored procedures in the same way as PostgreSQL
-- These functions would need to be implemented in the application layer
-- or using Java stored procedures in H2

-- Function to calculate total material cost for a treatment (H2 Java function)
CREATE ALIAS IF NOT EXISTS get_treatment_material_cost AS $$
import java.sql.*;
import java.math.BigDecimal;
@CODE
BigDecimal getTreatmentMaterialCost(Connection conn, String treatmentUuid) throws SQLException {
    String sql = "SELECT COALESCE(SUM(total_cost), 0) FROM treatment_materials WHERE treatment_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setObject(1, java.util.UUID.fromString(treatmentUuid));
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}
$$;

-- Function to calculate total material cost for a patient (H2 Java function)
CREATE ALIAS IF NOT EXISTS get_patient_material_cost AS $$
import java.sql.*;
import java.math.BigDecimal;
@CODE
BigDecimal getPatientMaterialCost(Connection conn, String patientUuid) throws SQLException {
    String sql = "SELECT COALESCE(SUM(tm.total_cost), 0) " +
                 "FROM treatment_materials tm " +
                 "JOIN treatments t ON tm.treatment_id = t.id " +
                 "WHERE t.patient_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setObject(1, java.util.UUID.fromString(patientUuid));
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}
$$;

-- ====================================================================
-- TRIGGER FOR AUTOMATIC TOTAL COST CALCULATION
-- ====================================================================

-- Note: H2 triggers require Java implementation, so we'll handle this in the application layer
-- The actual calculation would be: NEW.total_cost = NEW.quantity * NEW.cost_per_unit
--
-- If you need triggers in H2, you would create a Java class that implements org.h2.api.Trigger
-- and then reference it in the CREATE TRIGGER statement

-- ====================================================================
-- ENHANCED CONSTRAINTS AND VALIDATIONS
-- ====================================================================

-- Add constraint to ensure material names are not empty
ALTER TABLE treatment_materials
ADD CONSTRAINT IF NOT EXISTS chk_material_name_not_empty
CHECK (LENGTH(TRIM(material_name)) > 0);
