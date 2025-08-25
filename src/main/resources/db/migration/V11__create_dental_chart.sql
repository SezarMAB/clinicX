-- Create dental_charts table for H2 database
-- H2 uses JSON instead of JSONB
CREATE TABLE IF NOT EXISTS dental_charts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL UNIQUE,
    chart_data JSON NOT NULL DEFAULT '{"meta": {"version": "1.0", "lastUpdated": null, "updatedBy": null}, "teeth": {}}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_dental_chart_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE
);

-- Create index on patient_id for fast lookups
CREATE INDEX IF NOT EXISTS idx_dental_charts_patient_id ON dental_charts(patient_id);

