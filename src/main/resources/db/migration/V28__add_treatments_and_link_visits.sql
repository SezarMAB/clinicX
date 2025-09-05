-- V28: Introduce treatments between patients and visits (without removing patient_id from visits)

-- 1) Create treatments table (one-to-one with patients)
CREATE TABLE IF NOT EXISTS treatments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL UNIQUE,
    name VARCHAR(150),
    status VARCHAR(50),
    start_date DATE,
    end_date DATE,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_treatments_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- 2) Add treatment_id to visits (nullable during transition)
ALTER TABLE visits ADD COLUMN IF NOT EXISTS treatment_id UUID;
-- Postgres does not support IF NOT EXISTS for ADD CONSTRAINT
DO $$
BEGIN
    ALTER TABLE visits
        ADD CONSTRAINT fk_visits_treatment FOREIGN KEY (treatment_id)
        REFERENCES treatments(id) ON DELETE RESTRICT;
EXCEPTION WHEN duplicate_object THEN
    -- constraint already exists, ignore
END $$;

-- 3) Backfill: create one treatment per patient that has visits
INSERT INTO treatments (patient_id, name, status, start_date)
SELECT DISTINCT v.patient_id, 'Default Treatment Plan', 'ACTIVE', COALESCE(MIN(v.date), CURRENT_DATE)
FROM visits v
LEFT JOIN treatments t ON t.patient_id = v.patient_id
WHERE t.id IS NULL
GROUP BY v.patient_id;

-- 4) Link visits to their patient treatment
UPDATE visits v
SET treatment_id = t.id
FROM treatments t
WHERE v.patient_id = t.patient_id
  AND v.treatment_id IS NULL;

-- 5) Index for performance
CREATE INDEX IF NOT EXISTS idx_visits_treatment_id ON visits(treatment_id);
