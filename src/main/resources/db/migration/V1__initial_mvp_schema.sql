-- ====================================================================
-- DENTAL CLINIC MVP DATABASE SCHEMA
-- Version: 1.0 - MVP
-- ====================================================================

BEGIN;

-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Invoice number sequence
CREATE SEQUENCE invoice_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ====================================================================
-- CORE PLATFORM TABLES
-- ====================================================================

-- Clinic information (simplified for MVP)
CREATE TABLE clinic_info (
                             id           BOOLEAN PRIMARY KEY   DEFAULT TRUE,
                             name         VARCHAR(255) NOT NULL,
                             address      TEXT,
                             phone_number VARCHAR(50),
                             email        VARCHAR(100),
                             timezone     VARCHAR(50)  NOT NULL DEFAULT 'UTC',
                             created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                             updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                             CONSTRAINT clinic_info_singleton CHECK (id = TRUE)
);

-- Specialties (keeping for multi-specialty support)
CREATE TABLE specialties (
                             id          UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
                             name        VARCHAR(100) UNIQUE NOT NULL,
                             description TEXT,
                             is_active   BOOLEAN             NOT NULL DEFAULT TRUE,
                             created_at  TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
                             updated_at  TIMESTAMPTZ         NOT NULL DEFAULT NOW()
);

-- ====================================================================
-- STAFF TABLES
-- ====================================================================

CREATE TABLE staff (
                       id           UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
                       full_name    VARCHAR(100)        NOT NULL,
                       role         VARCHAR(50)         NOT NULL,
                       email        VARCHAR(100) UNIQUE NOT NULL,
                       phone_number VARCHAR(30),
                       is_active    BOOLEAN                      DEFAULT TRUE,
                       created_at   TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
                       updated_at   TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
                       CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE TABLE staff_specialties (
                                   staff_id     UUID NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
                                   specialty_id UUID NOT NULL REFERENCES specialties (id) ON DELETE CASCADE,
                                   PRIMARY KEY (staff_id, specialty_id)
);

-- ====================================================================
-- PATIENT TABLES
-- ====================================================================

CREATE TABLE patients (
                          id                      UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
                          public_facing_id        VARCHAR(20) UNIQUE NOT NULL,
                          full_name               VARCHAR(150)       NOT NULL,
                          date_of_birth           DATE               NOT NULL,
                          gender                  VARCHAR(10),
                          phone_number            VARCHAR(30),
                          email                   VARCHAR(100),
                          address                 TEXT,
                          insurance_provider      VARCHAR(100),
                          insurance_number        VARCHAR(50),
                          important_medical_notes TEXT,
                          balance                 DECIMAL(10, 2)     NOT NULL DEFAULT 0.00,
                          is_active               BOOLEAN            NOT NULL DEFAULT TRUE,
                          created_at              TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
                          updated_at              TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
                          created_by              UUID REFERENCES staff(id),
                          CONSTRAINT chk_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- ====================================================================
-- MEDICAL RECORDS TABLES
-- ====================================================================

CREATE TABLE procedures (
                            id                       UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
                            specialty_id             UUID         NOT NULL REFERENCES specialties (id) ON DELETE CASCADE,
                            procedure_code           VARCHAR(20) UNIQUE,
                            name                     VARCHAR(255) NOT NULL,
                            description              TEXT,
                            default_cost             DECIMAL(10, 2),
                            default_duration_minutes INT,
                            is_active                BOOLEAN      NOT NULL DEFAULT TRUE,
                            created_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                            updated_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                            CONSTRAINT chk_cost_positive CHECK (default_cost IS NULL OR default_cost > 0),
                            CONSTRAINT chk_duration_positive CHECK (default_duration_minutes IS NULL OR default_duration_minutes > 0)
);

-- Appointments (simplified - no complex scheduling validation)
CREATE TABLE appointments (
                              id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
                              specialty_id         UUID        NOT NULL REFERENCES specialties (id) ON DELETE RESTRICT,
                              patient_id           UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                              doctor_id            UUID        REFERENCES staff (id) ON DELETE SET NULL,
                              appointment_datetime TIMESTAMPTZ NOT NULL,
                              duration_minutes     INT         NOT NULL DEFAULT 30,
                              status               VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
                              notes                TEXT,
                              created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                              updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                              created_by           UUID REFERENCES staff(id),
                              CONSTRAINT chk_appointment_duration CHECK (duration_minutes > 0)
);

CREATE TABLE treatments (
                            id                   UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
                            appointment_id       UUID           NOT NULL REFERENCES appointments (id) ON DELETE CASCADE,
                            patient_id           UUID           NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                            procedure_id         UUID           NOT NULL REFERENCES procedures (id) ON DELETE RESTRICT,
                            doctor_id            UUID           REFERENCES staff (id) ON DELETE SET NULL,
                            tooth_number         INT,
                            status               VARCHAR(50)    NOT NULL DEFAULT 'COMPLETED',
                            cost                 DECIMAL(10, 2) NOT NULL,
                            treatment_notes      TEXT,
                            treatment_date       DATE           NOT NULL,
                            created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                            updated_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                            created_by           UUID REFERENCES staff(id),
                            CONSTRAINT chk_cost_positive CHECK (cost >= 0),
                            CONSTRAINT chk_tooth_number CHECK (tooth_number IS NULL OR tooth_number BETWEEN 11 AND 48)
);

-- ====================================================================
-- DENTAL CHART TABLES
-- ====================================================================

CREATE TABLE tooth_conditions (
                                  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  code        VARCHAR(20) UNIQUE NOT NULL,
                                  name        VARCHAR(100) NOT NULL,
                                  description TEXT,
                                  color_hex   VARCHAR(7),
                                  is_active   BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Insert standard tooth conditions
INSERT INTO tooth_conditions (code, name, description, color_hex) VALUES
                                                                      ('HEALTHY', 'Healthy', 'Tooth is healthy with no issues', '#4CAF50'),
                                                                      ('CAVITY', 'Cavity', 'Tooth has cavity/caries', '#FF5722'),
                                                                      ('FILLED', 'Filled', 'Tooth has been filled', '#2196F3'),
                                                                      ('CROWN', 'Crown', 'Tooth has a crown', '#9C27B0'),
                                                                      ('ROOT_CANAL', 'Root Canal', 'Tooth has had root canal treatment', '#FF9800'),
                                                                      ('EXTRACTED', 'Extracted', 'Tooth has been extracted', '#9E9E9E'),
                                                                      ('MISSING', 'Missing', 'Tooth is congenitally missing', '#757575'),
                                                                      ('IMPLANT', 'Implant', 'Dental implant', '#00BCD4'),
                                                                      ('BRIDGE', 'Bridge', 'Part of dental bridge', '#3F51B5'),
                                                                      ('IMPACTED', 'Impacted', 'Tooth is impacted', '#E91E63'),
                                                                      ('FRACTURED', 'Fractured', 'Tooth is fractured', '#F44336');

CREATE TABLE patient_teeth (
                               id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               patient_id           UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
                               tooth_number         INT NOT NULL CHECK (tooth_number BETWEEN 11 AND 48),
                               current_condition_id UUID REFERENCES tooth_conditions(id),
                               notes                TEXT,
                               last_treatment_date  DATE,
                               created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               UNIQUE(patient_id, tooth_number)
);

CREATE TABLE tooth_history (
                               id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               patient_tooth_id UUID NOT NULL REFERENCES patient_teeth(id) ON DELETE CASCADE,
                               patient_id       UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
                               tooth_number     INT NOT NULL,
                               condition_id     UUID REFERENCES tooth_conditions(id),
                               treatment_id     UUID REFERENCES treatments(id),
                               change_date      TIMESTAMPTZ NOT NULL,
                               notes            TEXT,
                               recorded_by      UUID NOT NULL REFERENCES staff(id),
                               created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ====================================================================
-- FINANCIAL TABLES
-- ====================================================================

CREATE TABLE invoices (
                          id             UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
                          patient_id     UUID               NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                          invoice_number VARCHAR(50) UNIQUE NOT NULL,
                          issue_date     DATE               NOT NULL,
                          due_date       DATE,
                          total_amount   DECIMAL(10, 2)     NOT NULL,
                          status         VARCHAR(50)        NOT NULL DEFAULT 'UNPAID',
                          created_at     TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
                          updated_at     TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
                          created_by     UUID REFERENCES staff(id),
                          CONSTRAINT chk_invoice_dates CHECK (due_date IS NULL OR due_date >= issue_date),
                          CONSTRAINT chk_total_amount_positive CHECK (total_amount >= 0)
);

CREATE TABLE invoice_items (
                               id           UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
                               invoice_id   UUID           NOT NULL REFERENCES invoices (id) ON DELETE CASCADE,
                               treatment_id UUID UNIQUE             REFERENCES treatments (id) ON DELETE RESTRICT,
                               description  VARCHAR(255)   NOT NULL,
                               amount       DECIMAL(10, 2) NOT NULL,
                               created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                               CONSTRAINT chk_amount_positive CHECK (amount >= 0)
);

CREATE TABLE payments (
                          id               UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
                          invoice_id       UUID           REFERENCES invoices (id) ON DELETE SET NULL,
                          patient_id       UUID           NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                          payment_date     DATE           NOT NULL,
                          amount           DECIMAL(10, 2) NOT NULL,
                          payment_method   VARCHAR(50),
                          type             VARCHAR(50)    NOT NULL DEFAULT 'PAYMENT',
                          description      VARCHAR(255),
                          created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                          created_by       UUID REFERENCES staff(id),
                          CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- ====================================================================
-- SUPPORTING TABLES (MVP essentials only)
-- ====================================================================

CREATE TABLE lab_requests (
                              id               UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
                              patient_id       UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                              order_number     VARCHAR(50) UNIQUE,
                              item_description TEXT        NOT NULL,
                              tooth_number     INT,
                              date_sent        DATE,
                              date_due         DATE,
                              status           VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                              lab_name         VARCHAR(255),
                              created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                              updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE documents (
                           id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
                           patient_id           UUID         NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                           uploaded_by_staff_id UUID         REFERENCES staff (id) ON DELETE SET NULL,
                           file_name            VARCHAR(255) NOT NULL,
                           file_path            TEXT         NOT NULL,
                           file_size_bytes      BIGINT,
                           mime_type            VARCHAR(100),
                           type                 VARCHAR(50),
                           created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE notes (
                       id          UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
                       patient_id  UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                       content     TEXT        NOT NULL,
                       created_by  UUID        REFERENCES staff (id) ON DELETE SET NULL,
                       note_date   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at     TIMESTAMPTZ        NOT NULL DEFAULT NOW()
);

-- ====================================================================
-- INDEXES FOR PERFORMANCE
-- ====================================================================

-- Core table indexes
CREATE INDEX idx_staff_email ON staff(email) WHERE is_active = TRUE;
CREATE INDEX idx_patients_public_id ON patients(public_facing_id);
CREATE INDEX idx_patients_name ON patients(full_name);
CREATE INDEX idx_patients_phone ON patients(phone_number);

-- Appointment indexes
CREATE INDEX idx_appointments_datetime ON appointments(appointment_datetime);
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_status ON appointments(status);

-- Treatment indexes
CREATE INDEX idx_treatments_patient ON treatments(patient_id);
CREATE INDEX idx_treatments_appointment ON treatments(appointment_id);

-- Financial indexes
CREATE INDEX idx_invoices_patient ON invoices(patient_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_payments_patient ON payments(patient_id);
CREATE INDEX idx_payments_date ON payments(payment_date);

-- Dental chart indexes
CREATE INDEX idx_patient_teeth_patient ON patient_teeth(patient_id);
CREATE INDEX idx_tooth_history_patient ON tooth_history(patient_id);

-- ====================================================================
-- VIEWS FOR COMMON QUERIES
-- ====================================================================

CREATE VIEW v_dental_chart AS
SELECT
    pt.patient_id,
    pt.tooth_number,
    tc.code as condition_code,
    tc.name as condition_name,
    tc.color_hex,
    pt.notes,
    pt.last_treatment_date
FROM patient_teeth pt
         LEFT JOIN tooth_conditions tc ON pt.current_condition_id = tc.id
ORDER BY pt.tooth_number;

CREATE VIEW v_upcoming_appointments AS
SELECT
    a.id,
    a.appointment_datetime,
    a.duration_minutes,
    a.status,
    p.full_name AS patient_name,
    p.public_facing_id AS patient_id,
    p.phone_number AS patient_phone,
    s.full_name AS doctor_name,
    sp.name AS specialty
FROM appointments a
         JOIN patients p ON a.patient_id = p.id
         LEFT JOIN staff s ON a.doctor_id = s.id
         JOIN specialties sp ON a.specialty_id = sp.id
WHERE a.appointment_datetime >= NOW()
  AND a.status NOT IN ('CANCELLED', 'NO_SHOW')
ORDER BY a.appointment_datetime;

CREATE VIEW v_patient_financial_summary AS
SELECT
    p.id,
    p.full_name,
    p.public_facing_id,
    p.balance,
    COUNT(DISTINCT i.id) AS total_invoices,
    COUNT(DISTINCT i.id) FILTER (WHERE i.status = 'UNPAID') AS unpaid_invoices,
    COALESCE(SUM(i.total_amount) FILTER (WHERE i.status = 'UNPAID'), 0) AS total_unpaid
FROM patients p
         LEFT JOIN invoices i ON p.id = i.patient_id
GROUP BY p.id, p.full_name, p.public_facing_id, p.balance;

-- ====================================================================
-- FUNCTIONS AND TRIGGERS
-- ====================================================================

-- Function to update patient balance
CREATE OR REPLACE FUNCTION update_patient_balance()
    RETURNS TRIGGER AS $$
DECLARE
    v_patient_id UUID;
    v_new_balance DECIMAL(10, 2);
BEGIN
    IF (TG_OP = 'DELETE') THEN
        v_patient_id := OLD.patient_id;
    ELSE
        v_patient_id := NEW.patient_id;
    END IF;

    v_new_balance := (
        COALESCE((SELECT SUM(total_amount) FROM invoices WHERE patient_id = v_patient_id AND status != 'CANCELLED'), 0)
            -
        COALESCE((SELECT SUM(amount) FROM payments WHERE patient_id = v_patient_id AND type IN ('PAYMENT', 'CREDIT')), 0)
        );

    UPDATE patients SET balance = v_new_balance WHERE id = v_patient_id;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Function to initialize patient teeth
CREATE OR REPLACE FUNCTION initialize_patient_teeth()
    RETURNS TRIGGER AS $$
DECLARE
    tooth_numbers INT[] := ARRAY[
        11,12,13,14,15,16,17,18,
        21,22,23,24,25,26,27,28,
        31,32,33,34,35,36,37,38,
        41,42,43,44,45,46,47,48
        ];
    tooth INT;
    healthy_condition_id UUID;
BEGIN
    SELECT id INTO healthy_condition_id FROM tooth_conditions WHERE code = 'HEALTHY';

    FOREACH tooth IN ARRAY tooth_numbers
        LOOP
            INSERT INTO patient_teeth (patient_id, tooth_number, current_condition_id)
            VALUES (NEW.id, tooth, healthy_condition_id)
            ON CONFLICT (patient_id, tooth_number) DO NOTHING;
        END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Function to track tooth history
CREATE OR REPLACE FUNCTION track_tooth_history()
    RETURNS TRIGGER AS $$
BEGIN
    IF OLD.current_condition_id IS DISTINCT FROM NEW.current_condition_id THEN
        INSERT INTO tooth_history (
            patient_tooth_id,
            patient_id,
            tooth_number,
            condition_id,
            change_date,
            notes,
            recorded_by
        ) VALUES (
                     NEW.id,
                     NEW.patient_id,
                     NEW.tooth_number,
                     NEW.current_condition_id,
                     NOW(),
                     NEW.notes,
                     NEW.created_by
                 );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ====================================================================
-- ATTACH TRIGGERS
-- ====================================================================

CREATE TRIGGER trg_invoices_balance_update
    AFTER INSERT OR UPDATE OR DELETE ON invoices
    FOR EACH ROW EXECUTE FUNCTION update_patient_balance();

CREATE TRIGGER trg_payments_balance_update
    AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_patient_balance();

CREATE TRIGGER trg_initialize_patient_teeth
    AFTER INSERT ON patients
    FOR EACH ROW EXECUTE FUNCTION initialize_patient_teeth();

CREATE TRIGGER trg_track_tooth_history
    AFTER UPDATE ON patient_teeth
    FOR EACH ROW EXECUTE FUNCTION track_tooth_history();

-- ====================================================================
-- INITIAL DATA
-- ====================================================================

INSERT INTO clinic_info (name, timezone)
VALUES ('Dental Clinic', 'UTC')
ON CONFLICT (id) DO NOTHING;

INSERT INTO specialties (name, description)
VALUES ('General Dentistry', 'General dental care and procedures');

-- Initialize teeth for existing patients (if any)
DO $$
    DECLARE
        patient_record RECORD;
        tooth_numbers INT[] := ARRAY[
            11,12,13,14,15,16,17,18,
            21,22,23,24,25,26,27,28,
            31,32,33,34,35,36,37,38,
            41,42,43,44,45,46,47,48
            ];
        tooth INT;
        healthy_condition_id UUID;
    BEGIN
        SELECT id INTO healthy_condition_id FROM tooth_conditions WHERE code = 'HEALTHY';

        FOR patient_record IN SELECT id FROM patients WHERE is_active = TRUE
            LOOP
                FOREACH tooth IN ARRAY tooth_numbers
                    LOOP
                        INSERT INTO patient_teeth (patient_id, tooth_number, current_condition_id)
                        VALUES (patient_record.id, tooth, healthy_condition_id)
                        ON CONFLICT (patient_id, tooth_number) DO NOTHING;
                    END LOOP;
            END LOOP;
    END $$;

COMMIT;
