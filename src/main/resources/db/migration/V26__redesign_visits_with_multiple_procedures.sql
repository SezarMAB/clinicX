-- V26: Complete redesign of visits to support multiple procedures
-- This migration transforms visits from single-procedure to multi-procedure model

-- First, rename the old visits table to preserve data
ALTER TABLE visits RENAME TO visits_old;

-- Create new visits table as header only
CREATE TABLE visits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    appointment_id UUID,
    provider_id UUID NOT NULL,
    date DATE NOT NULL,
    time TIME,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_visits_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_visits_appointment FOREIGN KEY (appointment_id)
        REFERENCES appointments(id) ON DELETE SET NULL,
    CONSTRAINT fk_visits_provider FOREIGN KEY (provider_id)
        REFERENCES staff(id) ON DELETE RESTRICT
);
-- Create procedures table
CREATE TABLE procedures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    visit_id UUID NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    tooth_number INTEGER,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_fee DECIMAL(8,2) NOT NULL,
    duration_minutes INTEGER,
    performed_by_id UUID,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED',
    billable BOOLEAN NOT NULL DEFAULT true,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_procedures_visit FOREIGN KEY (visit_id)
        REFERENCES visits(id) ON DELETE CASCADE,
    CONSTRAINT fk_procedures_performed_by FOREIGN KEY (performed_by_id)
        REFERENCES staff(id) ON DELETE SET NULL,
    CONSTRAINT check_procedure_status CHECK (status IN (
        'PLANNED', 'IN_PROGRESS', 'SENT_TO_LAB',
        'RECEIVED_FROM_LAB', 'COMPLETED', 'CANCELLED'
    )),
    CONSTRAINT check_tooth_number CHECK (
        tooth_number IS NULL OR (tooth_number >= 11 AND tooth_number <= 48)
    ),
    CONSTRAINT check_quantity CHECK (quantity > 0 AND quantity <= 32),
    CONSTRAINT check_unit_fee CHECK (unit_fee >= 0)
);

-- Create procedure surfaces table
CREATE TABLE procedure_surfaces (
    procedure_id UUID NOT NULL,
    surface VARCHAR(1) NOT NULL,

    CONSTRAINT fk_surfaces_procedure FOREIGN KEY (procedure_id)
        REFERENCES procedures(id) ON DELETE CASCADE,
    CONSTRAINT check_surface CHECK (surface IN ('M', 'O', 'D', 'B', 'L')),
    PRIMARY KEY (procedure_id, surface)
);

-- Create lab_cases table
CREATE TABLE lab_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    procedure_id UUID NOT NULL UNIQUE,
    lab_name VARCHAR(255) NOT NULL,
    sent_date DATE NOT NULL,
    due_date DATE NOT NULL,
    received_date DATE,
    tracking_number VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'SENT',
    technician_name VARCHAR(100),
    shade VARCHAR(50),
    material_type VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lab_cases_procedure FOREIGN KEY (procedure_id)
        REFERENCES procedures(id) ON DELETE CASCADE,
    CONSTRAINT check_lab_status CHECK (status IN (
        'SENT', 'IN_PROGRESS', 'RECEIVED', 'DELIVERED',
        'CANCELLED', 'REJECTED', 'REMAKE'
    )),
    CONSTRAINT check_dates CHECK (sent_date <= due_date),
    CONSTRAINT check_received_date CHECK (
        received_date IS NULL OR received_date >= sent_date
    )
);

-- Create procedure_materials table
CREATE TABLE procedure_materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    procedure_id UUID NOT NULL,
    material_id UUID,
    material_name VARCHAR(255) NOT NULL,
    material_code VARCHAR(50),
    quantity DECIMAL(10,3) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    consumed_at TIMESTAMP,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_procedure_materials_procedure FOREIGN KEY (procedure_id)
        REFERENCES procedures(id) ON DELETE CASCADE,
    CONSTRAINT check_quantity CHECK (quantity > 0),
    CONSTRAINT check_costs CHECK (unit_cost >= 0 AND total_cost >= 0)
);

-- Create indexes for performance
CREATE INDEX idx_visits_patient_id ON visits(patient_id);
-- CREATE INDEX idx_visits_date ON visits(date);
CREATE INDEX idx_visits_provider_id ON visits(provider_id);

CREATE INDEX idx_procedures_visit_id ON procedures(visit_id);
CREATE INDEX idx_procedures_status ON procedures(status);
CREATE INDEX idx_procedures_tooth_number ON procedures(tooth_number);
CREATE INDEX idx_procedures_code ON procedures(code);

CREATE INDEX idx_lab_cases_status ON lab_cases(status);
CREATE INDEX idx_lab_cases_due_date ON lab_cases(due_date);

CREATE INDEX idx_procedure_materials_procedure_id ON procedure_materials(procedure_id);

-- Migrate data from old visits table
INSERT INTO visits (id, patient_id, appointment_id, provider_id, date, notes, created_at, updated_at)
SELECT
    id,
    patient_id,
    appointment_id,
    doctor_id as provider_id,
    visit_date as date,
    visit_notes as notes,
    created_at,
    updated_at
FROM visits_old;

-- Migrate existing visits as single procedures
INSERT INTO procedures (
    visit_id, code, name, tooth_number, quantity,
    unit_fee, performed_by_id, status, created_at, updated_at
)
SELECT
    v.id as visit_id,
    COALESCE(p.code, 'LEGACY') as code,
    COALESCE(p.name, 'Legacy Procedure') as name,
    v.tooth_number,
    1 as quantity,
    v.cost as unit_fee,
    v.doctor_id as performed_by_id,
    CASE
        WHEN v.status = 'COMPLETED' THEN 'COMPLETED'
        WHEN v.status = 'IN_PROGRESS' THEN 'IN_PROGRESS'
        WHEN v.status = 'CANCELLED' THEN 'CANCELLED'
        ELSE 'PLANNED'
    END as status,
    v.created_at,
    v.updated_at
FROM visits_old v
LEFT JOIN procedures p ON v.procedure_id = p.id;

-- Update TreatmentMaterial to link to new procedures table
ALTER TABLE treatment_materials ADD COLUMN IF NOT EXISTS procedure_id UUID;

UPDATE treatment_materials tm
SET procedure_id = (
    SELECT p.id
    FROM procedures p
    WHERE p.visit_id = tm.visit_id
    LIMIT 1
)
WHERE procedure_id IS NULL;

-- Drop the constraint first with CASCADE to handle dependencies
ALTER TABLE treatment_materials
    DROP CONSTRAINT IF EXISTS treatment_materials_visit_id_fkey CASCADE;

-- Drop the column with CASCADE to handle any dependencies
ALTER TABLE treatment_materials
    DROP COLUMN IF EXISTS visit_id CASCADE;

-- Now add the new constraint (only if it doesn't exist)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_treatment_materials_procedure') THEN
        ALTER TABLE treatment_materials
            ALTER COLUMN procedure_id SET NOT NULL,
            ADD CONSTRAINT fk_treatment_materials_procedure
                FOREIGN KEY (procedure_id) REFERENCES procedures(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Create update triggers
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_visits_updated_at BEFORE UPDATE ON visits
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_procedures_updated_at BEFORE UPDATE ON procedures
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_lab_cases_updated_at BEFORE UPDATE ON lab_cases
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_procedure_materials_updated_at BEFORE UPDATE ON procedure_materials
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Drop the old visits table after successful migration
DROP TABLE visits_old CASCADE;

-- Add comments for documentation
COMMENT ON TABLE visits IS 'Visit header - container for multiple procedures';
COMMENT ON TABLE procedures IS 'Individual billable procedures performed during a visit';
COMMENT ON TABLE lab_cases IS 'External lab work tracking for procedures';
COMMENT ON TABLE procedure_materials IS 'Materials consumed during procedures';
COMMENT ON COLUMN procedures.tooth_number IS 'FDI notation (11-48)';
COMMENT ON COLUMN procedures.billable IS 'Whether this procedure appears on invoices';
