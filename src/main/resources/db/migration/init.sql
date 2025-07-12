rollback;
begin ;

-- STEP 1: Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS btree_gist;    -- For GIST indexes on standard types

CREATE SEQUENCE invoice_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
-- ====================================================================
--      CORE PLATFORM TABLES (ENHANCED)
-- ====================================================================

-- Stores the details for the single clinic this platform instance serves
CREATE TABLE clinic_info
(
    id           BOOLEAN PRIMARY KEY   DEFAULT TRUE,
    name         VARCHAR(255) NOT NULL,
    address      TEXT,
    phone_number VARCHAR(50),
    email        VARCHAR(100),
    timezone     VARCHAR(50)  NOT NULL DEFAULT 'UTC', -- NEW: For proper appointment scheduling
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by   UUID,                                -- NEW: Audit field
    updated_by   UUID,                                -- NEW: Audit field
    CONSTRAINT clinic_info_singleton CHECK (id = TRUE)
);

-- Master list of all medical specialties offered by the clinic
CREATE TABLE specialties
(
    id          UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active   BOOLEAN             NOT NULL DEFAULT TRUE, -- NEW: Soft delete
    created_at  TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    created_by  UUID,                                      -- NEW: Audit field
    updated_by  UUID                                       -- NEW: Audit field
);

-- ====================================================================
--      STAFF TABLES (ENHANCED)
-- ====================================================================

CREATE TABLE staff
(
    id           UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    full_name    VARCHAR(100)        NOT NULL,
    role         VARCHAR(50)         NOT NULL,
    initials     VARCHAR(5) UNIQUE,
    email        VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(30),
    is_active    BOOLEAN                      DEFAULT TRUE,
    deleted_at   TIMESTAMPTZ,                              -- NEW: Soft delete timestamp
    created_at   TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    created_by   UUID,                                      -- NEW: Audit field
    updated_by   UUID,                                      -- NEW: Audit field
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE TABLE staff_specialties
(
    staff_id     UUID NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
    specialty_id UUID NOT NULL REFERENCES specialties (id) ON DELETE CASCADE,
    PRIMARY KEY (staff_id, specialty_id)
);

-- ====================================================================
--      PATIENT TABLES (ENHANCED)
-- ====================================================================

CREATE TABLE patients
(
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
    is_active               BOOLEAN            NOT NULL DEFAULT TRUE, -- NEW: Soft delete
    deleted_at              TIMESTAMPTZ,                              -- NEW: Soft delete timestamp
    created_at              TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    created_by              UUID,                                     -- NEW: Audit field
    updated_by              UUID,                                     -- NEW: Audit field
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- ====================================================================
--      MEDICAL RECORDS TABLES (ENHANCED)
-- ====================================================================

CREATE TABLE procedures
(
    id                       UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    specialty_id             UUID         NOT NULL REFERENCES specialties (id) ON DELETE CASCADE,
    procedure_code           VARCHAR(20) UNIQUE,
    name                     VARCHAR(255) NOT NULL,
    description              TEXT,
    default_cost             DECIMAL(10, 2),
    default_duration_minutes INT,
    requires_authorization   BOOLEAN      NOT NULL DEFAULT FALSE,      -- NEW: Insurance auth tracking
    is_active                BOOLEAN      NOT NULL DEFAULT TRUE,       -- NEW: Soft delete
    created_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by               UUID,                                      -- NEW: Audit field
    updated_by               UUID,                                      -- NEW: Audit field
    CONSTRAINT chk_cost_positive CHECK (default_cost IS NULL OR default_cost > 0),
    CONSTRAINT chk_duration_positive CHECK (default_duration_minutes IS NULL OR default_duration_minutes > 0)
);

-- ENHANCED: Appointments with conflict prevention
-- First create an immutable function to calculate end time
CREATE OR REPLACE FUNCTION appointment_end_time(start_time TIMESTAMPTZ, duration_mins INT)
    RETURNS TIMESTAMPTZ AS $$
BEGIN
    RETURN start_time + (duration_mins * INTERVAL '1 minute');
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Then create the table using this function
CREATE TABLE appointments
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    specialty_id         UUID        NOT NULL REFERENCES specialties (id) ON DELETE RESTRICT,
    patient_id           UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    doctor_id            UUID        REFERENCES staff (id) ON DELETE SET NULL,
    appointment_datetime TIMESTAMPTZ NOT NULL,
    duration_minutes     INT         NOT NULL,
    status               VARCHAR(50) NOT NULL,
    notes                TEXT,
    confirmation_sent_at TIMESTAMPTZ,
    reminder_sent_at     TIMESTAMPTZ,
    checked_in_at        TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by           UUID,
    updated_by           UUID,
    CONSTRAINT chk_appointment_duration CHECK (duration_minutes > 0),
    -- Prevent double-booking using exclusion constraint
    EXCLUDE USING gist (
        doctor_id WITH =,
        tstzrange(appointment_datetime, appointment_end_time(appointment_datetime, duration_minutes)) WITH &&
        ) WHERE (status NOT IN ('CANCELLED', 'NO_SHOW'))
);

CREATE TABLE treatments
(
    id                   UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    appointment_id       UUID           NOT NULL REFERENCES appointments (id) ON DELETE CASCADE,
    patient_id           UUID           NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    procedure_id         UUID           NOT NULL REFERENCES procedures (id) ON DELETE RESTRICT,
    doctor_id            UUID           REFERENCES staff (id) ON DELETE SET NULL,
    assistant_id         UUID           REFERENCES staff (id) ON DELETE SET NULL,
    item_identifier      VARCHAR(100),
    tooth_number         INT,
    status               VARCHAR(50)    NOT NULL,
    cost                 DECIMAL(10, 2) NOT NULL,
    currency             VARCHAR(3)     NOT NULL DEFAULT 'EUR',
    material_used        VARCHAR(255),
    treatment_notes      TEXT,
    post_op_instructions TEXT,
    treatment_date       DATE           NOT NULL,
    is_billable          BOOLEAN        NOT NULL DEFAULT TRUE,         -- NEW: Some treatments might be non-billable
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by           UUID,                                          -- NEW: Audit field
    updated_by           UUID,                                          -- NEW: Audit field
    CONSTRAINT chk_cost_positive CHECK (cost >= 0),
    CONSTRAINT chk_tooth_number CHECK (tooth_number IS NULL OR tooth_number BETWEEN 1 AND 32)
);


-- Tooth conditions/states that can be tracked
CREATE TABLE tooth_conditions (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  code VARCHAR(20) UNIQUE NOT NULL,
                                  name VARCHAR(100) NOT NULL,
                                  description TEXT,
                                  color_hex VARCHAR(7), -- For UI display
                                  icon VARCHAR(50), -- For UI icons
                                  is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                  created_by UUID REFERENCES staff(id),
                                  updated_by UUID REFERENCES staff(id)
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

-- Current state of each tooth (one record per tooth per patient)
CREATE TABLE patient_teeth (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
                               tooth_number INT NOT NULL CHECK (tooth_number BETWEEN 11 AND 48),
                               current_condition_id UUID REFERENCES tooth_conditions(id),
                               notes TEXT,
    -- Denormalized fields for performance
                               last_treatment_date DATE,
                               next_scheduled_treatment_date DATE,
                               is_monitored BOOLEAN NOT NULL DEFAULT FALSE, -- Flag for teeth requiring monitoring
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               created_by UUID REFERENCES staff(id),
                               updated_by UUID REFERENCES staff(id),
                               UNIQUE(patient_id, tooth_number)
);

-- Complete history of tooth state changes
CREATE TABLE tooth_history (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               patient_tooth_id UUID NOT NULL REFERENCES patient_teeth(id) ON DELETE CASCADE,
                               patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE, -- Denormalized for query performance
                               tooth_number INT NOT NULL,
                               condition_id UUID REFERENCES tooth_conditions(id),
                               treatment_id UUID REFERENCES treatments(id), -- Link to treatment that caused the change
                               change_date TIMESTAMPTZ NOT NULL,
                               notes TEXT,
    -- Additional clinical data
                               mobility_score INT CHECK (mobility_score BETWEEN 0 AND 3), -- 0=none, 1=slight, 2=moderate, 3=severe
                               pocket_depth_mm DECIMAL(3,1),
                               bleeding_on_probing BOOLEAN,
                               plaque_present BOOLEAN,
    -- Who made the change
                               recorded_by UUID NOT NULL REFERENCES staff(id),
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tooth-specific clinical measurements over time
CREATE TABLE tooth_measurements (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    patient_tooth_id UUID NOT NULL REFERENCES patient_teeth(id) ON DELETE CASCADE,
                                    measurement_date DATE NOT NULL,
    -- Periodontal measurements (6 sites per tooth)
                                    mesial_buccal_depth DECIMAL(3,1),
                                    buccal_depth DECIMAL(3,1),
                                    distal_buccal_depth DECIMAL(3,1),
                                    mesial_lingual_depth DECIMAL(3,1),
                                    lingual_depth DECIMAL(3,1),
                                    distal_lingual_depth DECIMAL(3,1),
    -- Recession measurements
                                    recession_buccal DECIMAL(3,1),
                                    recession_lingual DECIMAL(3,1),
    -- Other measurements
                                    furcation_involvement INT CHECK (furcation_involvement BETWEEN 0 AND 3),
                                    mobility_score INT CHECK (mobility_score BETWEEN 0 AND 3),
    -- Who recorded
                                    recorded_by UUID NOT NULL REFERENCES staff(id),
                                    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                    UNIQUE(patient_tooth_id, measurement_date)
);

-- Tooth surfaces for detailed tracking
CREATE TABLE tooth_surfaces (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                code VARCHAR(10) UNIQUE NOT NULL,
                                name VARCHAR(50) NOT NULL
);

INSERT INTO tooth_surfaces (code, name) VALUES
                                            ('M', 'Mesial'),
                                            ('D', 'Distal'),
                                            ('B', 'Buccal'),
                                            ('L', 'Lingual'),
                                            ('O', 'Occlusal'),
                                            ('I', 'Incisal');

-- Track conditions per tooth surface
CREATE TABLE tooth_surface_conditions (
                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          patient_tooth_id UUID NOT NULL REFERENCES patient_teeth(id) ON DELETE CASCADE,
                                          surface_id UUID NOT NULL REFERENCES tooth_surfaces(id),
                                          condition_id UUID REFERENCES tooth_conditions(id),
                                          severity VARCHAR(20) CHECK (severity IN ('MILD', 'MODERATE', 'SEVERE')),
                                          notes TEXT,
                                          recorded_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                          recorded_by UUID NOT NULL REFERENCES staff(id),
                                          UNIQUE(patient_tooth_id, surface_id)
);


-- ====================================================================
--      SCHEDULING TABLES (ENHANCED)
-- ====================================================================

CREATE TABLE staff_schedules
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id    UUID NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
    day_of_week INT  NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),                    -- NEW: Audit fields
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID,
    CONSTRAINT chk_schedule_times CHECK (end_time > start_time),
    UNIQUE (staff_id, day_of_week, start_time, end_time)
);

CREATE TABLE schedule_overrides
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id      UUID    NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
    override_date DATE    NOT NULL,
    is_available  BOOLEAN NOT NULL,
    start_time    TIME,
    end_time      TIME,
    reason        VARCHAR(255),                                         -- NEW: Track why schedule changed
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),                  -- NEW: Audit fields
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    UUID,
    updated_by    UUID,
    CONSTRAINT chk_override_times CHECK (
        (is_available = FALSE) OR
        (is_available = TRUE AND start_time IS NOT NULL AND end_time IS NOT NULL AND end_time > start_time)
        ),
    UNIQUE (staff_id, override_date)
);

-- NEW: Pre-generated appointment slots for online booking
CREATE TABLE appointment_slots
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    specialty_id     UUID        NOT NULL REFERENCES specialties (id) ON DELETE CASCADE,
    doctor_id        UUID        NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
    slot_datetime    TIMESTAMPTZ NOT NULL,
    duration_minutes INT         NOT NULL,
    is_available     BOOLEAN     NOT NULL DEFAULT TRUE,
    max_patients     INT         NOT NULL DEFAULT 1,                   -- For group appointments
    booked_count     INT         NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_slot_duration CHECK (duration_minutes > 0),
    CONSTRAINT chk_max_patients CHECK (max_patients > 0),
    CONSTRAINT chk_booked_count CHECK (booked_count >= 0 AND booked_count <= max_patients),
    UNIQUE(doctor_id, slot_datetime)
);

-- ====================================================================
--      FINANCIAL TABLES (ENHANCED)
-- ====================================================================

CREATE TABLE invoices
(
    id             UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
    patient_id     UUID               NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    issue_date     DATE               NOT NULL,
    due_date       DATE,
    total_amount   DECIMAL(10, 2)     NOT NULL,
    currency       VARCHAR(3)         NOT NULL DEFAULT 'EUR',
    status         VARCHAR(50)        NOT NULL,
    sent_at        TIMESTAMPTZ,                                        -- NEW: Track when sent
    paid_at        TIMESTAMPTZ,                                        -- NEW: Track when paid
    created_at     TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    created_by     UUID,                                               -- NEW: Audit field
    updated_by     UUID,                                               -- NEW: Audit field
    CONSTRAINT chk_invoice_dates CHECK (due_date IS NULL OR due_date >= issue_date),
    CONSTRAINT chk_total_amount_positive CHECK (total_amount >= 0)
);

CREATE TABLE invoice_items
(
    id           UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    invoice_id   UUID           NOT NULL REFERENCES invoices (id) ON DELETE CASCADE,
    treatment_id UUID UNIQUE             REFERENCES treatments (id) ON DELETE RESTRICT, -- Can be NULL for manual items
    description  VARCHAR(255)   NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by   UUID,                                                 -- NEW: Audit field
    updated_by   UUID,                                                 -- NEW: Audit field
    CONSTRAINT chk_amount_positive CHECK (amount >= 0)
);

CREATE TABLE payments
(
    id               UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    invoice_id       UUID           REFERENCES invoices (id) ON DELETE SET NULL,
    patient_id       UUID           NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    payment_date     DATE           NOT NULL,
    amount           DECIMAL(10, 2) NOT NULL,
    payment_method   VARCHAR(50),
    type             VARCHAR(50)    NOT NULL,
    description      VARCHAR(255),
    reference_number VARCHAR(100),                                     -- NEW: External payment reference
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by       UUID,                                             -- NEW: Audit field
    updated_by       UUID,                                             -- NEW: Audit field
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- NEW: Insurance authorization tracking
CREATE TABLE insurance_authorizations
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id         UUID           NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    procedure_id       UUID           NOT NULL REFERENCES procedures (id) ON DELETE CASCADE,
    authorization_code VARCHAR(100),
    status             VARCHAR(50)    NOT NULL,
    requested_date     DATE           NOT NULL,
    approved_date      DATE,
    expiry_date        DATE,
    approved_amount    DECIMAL(10, 2),
    notes              TEXT,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by         UUID,
    updated_by         UUID,
    CONSTRAINT chk_auth_dates CHECK (approved_date IS NULL OR approved_date >= requested_date),
    CONSTRAINT chk_expiry_date CHECK (expiry_date IS NULL OR expiry_date > approved_date)
);

-- ====================================================================
--      SUPPORTING TABLES (ENHANCED)
-- ====================================================================

CREATE TABLE tags
(
    id          UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
    tag_name    VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    color_hex   VARCHAR(7),                                            -- NEW: For UI display
    is_active   BOOLEAN            NOT NULL DEFAULT TRUE,              -- NEW: Soft delete
    created_at  TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    created_by  UUID,                                                  -- NEW: Audit field
    updated_by  UUID                                                   -- NEW: Audit field
);

CREATE TABLE patient_tags
(
    patient_id UUID NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    tag_id     UUID NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),                     -- NEW: Track when tagged
    created_by UUID,                                                   -- NEW: Who tagged
    PRIMARY KEY (patient_id, tag_id)
);

CREATE TABLE lab_requests
(
    id               UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    patient_id       UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    order_number     VARCHAR(50) UNIQUE,
    item_description TEXT        NOT NULL,
    item_identifier  VARCHAR(100),
    tooth_number     INT,
    date_sent        DATE,
    date_due         DATE,
    date_received    DATE,                                             -- NEW: Track actual receipt
    status           VARCHAR(50) NOT NULL,
    lab_name         VARCHAR(255),                                     -- NEW: Track which lab
    lab_cost         DECIMAL(10, 2),                                  -- NEW: Track lab charges
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       UUID,                                             -- NEW: Audit field
    updated_by       UUID,                                             -- NEW: Audit field
    CONSTRAINT chk_lab_dates CHECK (
        (date_sent IS NULL OR date_due IS NULL OR date_due >= date_sent) AND
        (date_sent IS NULL OR date_received IS NULL OR date_received >= date_sent)
        )
);

CREATE TABLE documents
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    patient_id           UUID         NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    uploaded_by_staff_id UUID         REFERENCES staff (id) ON DELETE SET NULL,
    file_name            VARCHAR(255) NOT NULL,
    file_path            TEXT         NOT NULL,
    file_size_bytes      BIGINT,                                       -- NEW: Track file size
    mime_type            VARCHAR(100),                                 -- NEW: Track file type
    type                 VARCHAR(50),
    is_archived          BOOLEAN      NOT NULL DEFAULT FALSE,          -- NEW: Soft delete
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    patient_id           UUID REFERENCES patients (id) ON DELETE CASCADE,
    assigned_to_staff_id UUID        REFERENCES staff (id) ON DELETE SET NULL,
    assigned_by_staff_id UUID        REFERENCES staff (id) ON DELETE SET NULL, -- NEW: Track who assigned
    description          TEXT        NOT NULL,
    priority             VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',        -- NEW: Task priority
    due_date             DATE,
    status               VARCHAR(50) NOT NULL,
    completed_at         TIMESTAMPTZ,
    completed_by         UUID        REFERENCES staff (id) ON DELETE SET NULL, -- NEW: Track who completed
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE notes
(
    id          UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    patient_id  UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    content     TEXT        NOT NULL,
    created_by  UUID        REFERENCES staff (id) ON DELETE SET NULL,  -- ENHANCED: Now FK
    note_date   TIMESTAMPTZ NOT NULL,
    is_pinned   BOOLEAN     NOT NULL DEFAULT FALSE,                    -- NEW: Pin important notes
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by  UUID                                                   -- NEW: Audit field
);

-- NEW: Referral tracking for multi-specialty clinics
CREATE TABLE referrals
(
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id            UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    from_doctor_id        UUID        REFERENCES staff (id) ON DELETE SET NULL,
    to_doctor_id          UUID        REFERENCES staff (id) ON DELETE SET NULL,
    to_specialty_id       UUID        REFERENCES specialties (id) ON DELETE SET NULL,
    external_doctor_name  VARCHAR(255),                                -- For external referrals
    external_clinic_name  VARCHAR(255),                                -- For external referrals
    reason                TEXT        NOT NULL,
    urgency               VARCHAR(20) NOT NULL DEFAULT 'ROUTINE',
    status                VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    referral_date         DATE        NOT NULL,
    appointment_id        UUID        REFERENCES appointments (id) ON DELETE SET NULL, -- Link to resulting appointment
    notes                 TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by            UUID,
    updated_by            UUID,
    CONSTRAINT chk_referral_type CHECK (
        (to_doctor_id IS NOT NULL OR external_doctor_name IS NOT NULL)
        )
);

-- ====================================================================
--      AUDIT AND SECURITY TABLES (NEW)
-- ====================================================================

-- Comprehensive audit log for all changes
CREATE TABLE audit_log
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    table_name  VARCHAR(50)  NOT NULL,
    record_id   UUID         NOT NULL,
    action      VARCHAR(20)  NOT NULL,                                 -- INSERT, UPDATE, DELETE
    changed_by  UUID         REFERENCES staff (id) ON DELETE SET NULL,
    changed_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    ip_address  INET,                                                  -- Track source IP
    user_agent  TEXT,                                                  -- Track browser/app
    old_values  JSONB,                                                 -- Previous values for UPDATE
    new_values  JSONB,                                                 -- New values for INSERT/UPDATE
    change_note TEXT                                                   -- Optional explanation
);

-- Track all login attempts for security
CREATE TABLE login_attempts
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(100) NOT NULL,
    staff_id    UUID REFERENCES staff (id) ON DELETE SET NULL,
    success     BOOLEAN      NOT NULL,
    ip_address  INET,
    user_agent  TEXT,
    error_message VARCHAR(255),
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ====================================================================
--      PERFORMANCE INDEXES
-- ====================================================================

-- Core table indexes
CREATE INDEX idx_staff_email ON staff(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_staff_active ON staff(is_active) WHERE deleted_at IS NULL;
CREATE INDEX idx_patients_public_id ON patients(public_facing_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_patients_email ON patients(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_patients_phone ON patients(phone_number) WHERE deleted_at IS NULL;
CREATE INDEX idx_patients_active ON patients(is_active) WHERE deleted_at IS NULL;

-- Appointment indexes
CREATE INDEX idx_appointments_datetime ON appointments(appointment_datetime);
CREATE INDEX idx_appointments_patient_doctor ON appointments(patient_id, doctor_id);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_doctor_date ON appointments(doctor_id, appointment_datetime);
CREATE INDEX idx_appointment_slots_availability ON appointment_slots(doctor_id, slot_datetime) WHERE is_available = TRUE;

-- Treatment indexes
CREATE INDEX idx_treatments_patient_date ON treatments(patient_id, treatment_date);
CREATE INDEX idx_treatments_appointment ON treatments(appointment_id);
CREATE INDEX idx_treatments_billable ON treatments(id) WHERE is_billable = TRUE;

-- Financial indexes
CREATE INDEX idx_invoices_patient_status ON invoices(patient_id, status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date) WHERE status != 'PAID';
CREATE INDEX idx_payments_patient_date ON payments(patient_id, payment_date);
CREATE INDEX idx_payments_invoice ON payments(invoice_id) WHERE invoice_id IS NOT NULL;

-- Supporting table indexes
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to_staff_id) WHERE status != 'COMPLETED';
CREATE INDEX idx_tasks_due_date ON tasks(due_date) WHERE status != 'COMPLETED';
CREATE INDEX idx_documents_patient ON documents(patient_id) WHERE is_archived = FALSE;
CREATE INDEX idx_notes_patient ON notes(patient_id);
CREATE INDEX idx_notes_pinned ON notes(patient_id) WHERE is_pinned = TRUE;
CREATE INDEX idx_referrals_patient ON referrals(patient_id);
CREATE INDEX idx_referrals_status ON referrals(status) WHERE status != 'COMPLETED';

-- Teeth and conditions indexes

CREATE INDEX idx_patient_teeth_patient ON patient_teeth(patient_id);
CREATE INDEX idx_patient_teeth_condition ON patient_teeth(current_condition_id);
CREATE INDEX idx_patient_teeth_monitored ON patient_teeth(patient_id) WHERE is_monitored = TRUE;

CREATE INDEX idx_tooth_history_patient ON tooth_history(patient_id);
CREATE INDEX idx_tooth_history_tooth ON tooth_history(patient_tooth_id);
CREATE INDEX idx_tooth_history_date ON tooth_history(change_date);
CREATE INDEX idx_tooth_history_treatment ON tooth_history(treatment_id) WHERE treatment_id IS NOT NULL;

CREATE INDEX idx_tooth_measurements_tooth ON tooth_measurements(patient_tooth_id);
CREATE INDEX idx_tooth_measurements_date ON tooth_measurements(measurement_date);

CREATE INDEX idx_tooth_surface_conditions_tooth ON tooth_surface_conditions(patient_tooth_id);

-- Audit indexes
CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_by ON audit_log(changed_by);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at);
CREATE INDEX idx_login_attempts_email ON login_attempts(email);
CREATE INDEX idx_login_attempts_time ON login_attempts(attempted_at);

-- ====================================================================
--      VIEWS FOR COMMON QUERIES
-- ====================================================================

-- Current dental chart view
CREATE VIEW v_dental_chart AS
SELECT
    pt.patient_id,
    pt.tooth_number,
    tc.code as condition_code,
    tc.name as condition_name,
    tc.color_hex,
    pt.notes,
    pt.is_monitored,
    pt.last_treatment_date,
    pt.next_scheduled_treatment_date,
    pt.updated_at as last_updated
FROM patient_teeth pt
         LEFT JOIN tooth_conditions tc ON pt.current_condition_id = tc.id
ORDER BY pt.tooth_number;

-- Teeth requiring attention
CREATE VIEW v_teeth_requiring_attention AS
SELECT
    p.id as patient_id,
    p.full_name as patient_name,
    p.public_facing_id,
    pt.tooth_number,
    tc.name as condition_name,
    pt.notes,
    pt.last_treatment_date,
    CASE
        WHEN tc.code IN ('CAVITY', 'FRACTURED') THEN 'HIGH'
        WHEN pt.is_monitored THEN 'MEDIUM'
        ELSE 'LOW'
        END as priority
FROM patient_teeth pt
         JOIN patients p ON pt.patient_id = p.id
         JOIN tooth_conditions tc ON pt.current_condition_id = tc.id
WHERE (tc.code IN ('CAVITY', 'FRACTURED', 'IMPACTED')
    OR pt.is_monitored = TRUE
    OR pt.next_scheduled_treatment_date < CURRENT_DATE)
  AND p.deleted_at IS NULL
ORDER BY priority DESC, p.full_name;

-- Patient dental chart summary
CREATE VIEW v_patient_dental_summary AS
SELECT
    p.id as patient_id,
    p.full_name,
    p.public_facing_id,
    COUNT(DISTINCT pt.id) FILTER (WHERE tc.code = 'HEALTHY') as healthy_teeth,
    COUNT(DISTINCT pt.id) FILTER (WHERE tc.code = 'CAVITY') as cavities,
    COUNT(DISTINCT pt.id) FILTER (WHERE tc.code = 'FILLED') as filled_teeth,
    COUNT(DISTINCT pt.id) FILTER (WHERE tc.code IN ('EXTRACTED', 'MISSING')) as missing_teeth,
    COUNT(DISTINCT pt.id) FILTER (WHERE pt.is_monitored = TRUE) as monitored_teeth,
    MAX(pt.last_treatment_date) as last_dental_treatment
FROM patients p
         LEFT JOIN patient_teeth pt ON p.id = pt.patient_id
         LEFT JOIN tooth_conditions tc ON pt.current_condition_id = tc.id
WHERE p.deleted_at IS NULL
GROUP BY p.id, p.full_name, p.public_facing_id;

-- ====================================================================
--      ENHANCED TRIGGERS AND FUNCTIONS
-- ====================================================================

-- Enhanced patient balance calculation with audit trail
CREATE OR REPLACE FUNCTION update_patient_balance()
    RETURNS TRIGGER AS
$$
DECLARE
    v_patient_id UUID;
    v_old_balance DECIMAL(10, 2);
    v_new_balance DECIMAL(10, 2);
BEGIN
    -- Determine the affected patient_id
    IF (TG_OP = 'DELETE') THEN
        v_patient_id := OLD.patient_id;
    ELSE
        v_patient_id := NEW.patient_id;
    END IF;

    -- Get old balance
    SELECT balance INTO v_old_balance FROM patients WHERE id = v_patient_id;

    -- Calculate new balance
    v_new_balance := (
        COALESCE((SELECT SUM(total_amount) FROM invoices WHERE patient_id = v_patient_id), 0)
            -
        COALESCE((SELECT SUM(amount) FROM payments WHERE patient_id = v_patient_id AND type IN ('PAYMENT', 'CREDIT')), 0)
        );

    -- Update patient balance
    UPDATE patients SET balance = v_new_balance WHERE id = v_patient_id;

    -- Log the balance change
    IF v_old_balance != v_new_balance THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, new_values)
        VALUES ('patients', v_patient_id, 'BALANCE_UPDATE',
                jsonb_build_object('balance', v_old_balance),
                jsonb_build_object('balance', v_new_balance));
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Function to update appointment slot availability
CREATE OR REPLACE FUNCTION update_appointment_slot()
    RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.status NOT IN ('CANCELLED', 'NO_SHOW') THEN
        -- Mark slot as unavailable when appointment is created
        UPDATE appointment_slots
        SET is_available = FALSE,
            booked_count = booked_count + 1,
            updated_at = NOW()
        WHERE doctor_id = NEW.doctor_id
          AND slot_datetime = NEW.appointment_datetime;
    ELSIF TG_OP = 'UPDATE' THEN
        -- Handle status changes
        IF OLD.status NOT IN ('CANCELLED', 'NO_SHOW') AND NEW.status IN ('CANCELLED', 'NO_SHOW') THEN
            -- Free up the slot
            UPDATE appointment_slots
            SET is_available = CASE WHEN booked_count <= 1 THEN TRUE ELSE is_available END,
                booked_count = GREATEST(0, booked_count - 1),
                updated_at = NOW()
            WHERE doctor_id = NEW.doctor_id
              AND slot_datetime = NEW.appointment_datetime;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Function to validate appointment times against staff schedule
CREATE OR REPLACE FUNCTION validate_appointment_schedule()
    RETURNS TRIGGER AS
$$
DECLARE
    v_day_of_week INT;
    v_appointment_time TIME;
    v_is_available BOOLEAN;
BEGIN
    -- Extract day of week and time
    v_day_of_week := EXTRACT(ISODOW FROM NEW.appointment_datetime);
    v_appointment_time := NEW.appointment_datetime::TIME;

    -- Check if there's a schedule override
    SELECT is_available INTO v_is_available
    FROM schedule_overrides
    WHERE staff_id = NEW.doctor_id
      AND override_date = NEW.appointment_datetime::DATE;

    IF FOUND THEN
        IF NOT v_is_available THEN
            RAISE EXCEPTION 'Doctor is not available on this date';
        END IF;
    ELSE
        -- Check regular schedule
        IF NOT EXISTS (
            SELECT 1 FROM staff_schedules
            WHERE staff_id = NEW.doctor_id
              AND day_of_week = v_day_of_week
              AND start_time <= v_appointment_time
              AND end_time >= v_appointment_time + (NEW.duration_minutes || ' minutes')::INTERVAL
        ) THEN
            RAISE EXCEPTION 'Appointment time is outside doctor''s regular schedule';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Generic audit trigger function
CREATE OR REPLACE FUNCTION audit_trigger_function()
    RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, record_id, action, new_values, changed_by)
        VALUES (TG_TABLE_NAME, NEW.id, 'INSERT', to_jsonb(NEW), NEW.created_by);
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, new_values, changed_by)
        VALUES (TG_TABLE_NAME, NEW.id, 'UPDATE', to_jsonb(OLD), to_jsonb(NEW), NEW.updated_by);
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values)
        VALUES (TG_TABLE_NAME, OLD.id, 'DELETE', to_jsonb(OLD));
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- ====================================================================
--      ATTACH TRIGGERS
-- ====================================================================

-- Balance update triggers
CREATE TRIGGER trg_invoices_balance_update
    AFTER INSERT OR UPDATE OR DELETE ON invoices
    FOR EACH ROW EXECUTE FUNCTION update_patient_balance();

CREATE TRIGGER trg_payments_balance_update
    AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_patient_balance();

-- Appointment slot triggers
CREATE TRIGGER trg_appointment_slot_update
    AFTER INSERT OR UPDATE ON appointments
    FOR EACH ROW EXECUTE FUNCTION update_appointment_slot();

-- Schedule validation trigger
CREATE TRIGGER trg_validate_appointment_schedule
    BEFORE INSERT OR UPDATE ON appointments
    FOR EACH ROW EXECUTE FUNCTION validate_appointment_schedule();

-- Audit triggers for critical tables
CREATE TRIGGER audit_patients AFTER INSERT OR UPDATE OR DELETE ON patients
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_appointments AFTER INSERT OR UPDATE OR DELETE ON appointments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_treatments AFTER INSERT OR UPDATE OR DELETE ON treatments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_invoices AFTER INSERT OR UPDATE OR DELETE ON invoices
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_payments AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- ====================================================================
--      VIEWS FOR COMMON QUERIES
-- ====================================================================

-- View for upcoming appointments with full details
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
  AND p.deleted_at IS NULL
ORDER BY a.appointment_datetime;

-- View for patient financial summary
CREATE VIEW v_patient_financial_summary AS
SELECT
    p.id,
    p.full_name,
    p.public_facing_id,
    p.balance,
    COUNT(DISTINCT i.id) AS total_invoices,
    COUNT(DISTINCT i.id) FILTER (WHERE i.status = 'UNPAID') AS unpaid_invoices,
    COALESCE(SUM(i.total_amount) FILTER (WHERE i.status = 'UNPAID'), 0) AS total_unpaid,
    MAX(pay.payment_date) AS last_payment_date
FROM patients p
         LEFT JOIN invoices i ON p.id = i.patient_id
         LEFT JOIN payments pay ON p.id = pay.patient_id
WHERE p.deleted_at IS NULL
GROUP BY p.id, p.full_name, p.public_facing_id, p.balance;

-- View for staff availability today
CREATE VIEW v_staff_availability_today AS
WITH today_schedule AS (
    SELECT
        s.id,
        s.full_name,
        COALESCE(so.is_available, TRUE) AS is_available,
        COALESCE(so.start_time, ss.start_time) AS start_time,
        COALESCE(so.end_time, ss.end_time) AS end_time,
        so.reason AS override_reason
    FROM staff s
             LEFT JOIN staff_schedules ss ON s.id = ss.staff_id
        AND ss.day_of_week = EXTRACT(ISODOW FROM CURRENT_DATE)
             LEFT JOIN schedule_overrides so ON s.id = so.staff_id
        AND so.override_date = CURRENT_DATE
    WHERE s.is_active = TRUE AND s.deleted_at IS NULL
)
SELECT * FROM today_schedule WHERE is_available = TRUE;

-- ====================================================================
--      INITIAL DATA AND CONSTRAINTS
-- ====================================================================

-- Ensure at least one record in clinic_info
INSERT INTO clinic_info (name, timezone)
VALUES ('Multi-Specialty Clinic', 'UTC')
ON CONFLICT (id) DO NOTHING;

-- Add check to prevent deletion of last active specialty
CREATE OR REPLACE FUNCTION prevent_last_specialty_deletion()
    RETURNS TRIGGER AS
$$
BEGIN
    IF (SELECT COUNT(*) FROM specialties WHERE is_active = TRUE) <= 1
        AND NEW.is_active = FALSE THEN
        RAISE EXCEPTION 'Cannot deactivate the last active specialty';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_last_specialty
    BEFORE UPDATE ON specialties
    FOR EACH ROW
    WHEN (OLD.is_active = TRUE AND NEW.is_active = FALSE)
EXECUTE FUNCTION prevent_last_specialty_deletion();


-- ====================================================================
--     Teeth FUNCTIONS AND TRIGGERS
-- ====================================================================

-- Function to initialize patient teeth records
CREATE OR REPLACE FUNCTION initialize_patient_teeth()
    RETURNS TRIGGER AS $$
DECLARE
    tooth_numbers INT[] := ARRAY[
        11,12,13,14,15,16,17,18,  -- Upper right
        21,22,23,24,25,26,27,28,  -- Upper left
        31,32,33,34,35,36,37,38,  -- Lower left
        41,42,43,44,45,46,47,48   -- Lower right
        ];
    tooth INT;
    healthy_condition_id UUID;
BEGIN
    -- Get the ID for healthy condition
    SELECT id INTO healthy_condition_id FROM tooth_conditions WHERE code = 'HEALTHY';

    -- Create a record for each tooth
    FOREACH tooth IN ARRAY tooth_numbers
        LOOP
            INSERT INTO patient_teeth (patient_id, tooth_number, current_condition_id, created_by)
            VALUES (NEW.id, tooth, healthy_condition_id, NEW.created_by)
            ON CONFLICT (patient_id, tooth_number) DO NOTHING;
        END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to initialize teeth when patient is created
CREATE TRIGGER trg_initialize_patient_teeth
    AFTER INSERT ON patients
    FOR EACH ROW EXECUTE FUNCTION initialize_patient_teeth();

-- Function to track tooth history changes
CREATE OR REPLACE FUNCTION track_tooth_history()
    RETURNS TRIGGER AS $$
BEGIN
    -- Only track if condition actually changed
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
                     COALESCE(NEW.updated_by, NEW.created_by)
                 );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to track history
CREATE TRIGGER trg_track_tooth_history
    AFTER UPDATE ON patient_teeth
    FOR EACH ROW EXECUTE FUNCTION track_tooth_history();

-- Function to update tooth condition from treatment
CREATE OR REPLACE FUNCTION update_tooth_from_treatment()
    RETURNS TRIGGER AS $$
DECLARE
    v_condition_id UUID;
    v_procedure_name VARCHAR(255);
BEGIN
    -- Only process if tooth_number is specified
    IF NEW.tooth_number IS NOT NULL THEN
        -- Get the procedure name
        SELECT name INTO v_procedure_name
        FROM procedures
        WHERE id = NEW.procedure_id;

        -- Map procedure to tooth condition
        -- This is a simplified mapping - you might want to create a mapping table
        SELECT id INTO v_condition_id
        FROM tooth_conditions
        WHERE code = CASE
                         WHEN v_procedure_name ILIKE '%filling%' OR v_procedure_name ILIKE '%restoration%' THEN 'FILLED'
                         WHEN v_procedure_name ILIKE '%extraction%' THEN 'EXTRACTED'
                         WHEN v_procedure_name ILIKE '%root canal%' THEN 'ROOT_CANAL'
                         WHEN v_procedure_name ILIKE '%crown%' THEN 'CROWN'
                         WHEN v_procedure_name ILIKE '%implant%' THEN 'IMPLANT'
                         ELSE NULL
            END;

        -- Update tooth condition if mapping found
        IF v_condition_id IS NOT NULL THEN
            UPDATE patient_teeth
            SET current_condition_id = v_condition_id,
                updated_at = NOW(),
                updated_by = NEW.created_by
            WHERE patient_id = NEW.patient_id
              AND tooth_number = NEW.tooth_number;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_tooth_from_treatment
    AFTER INSERT ON treatments
    FOR EACH ROW EXECUTE FUNCTION update_tooth_from_treatment();

-- Add audit triggers for dental tables
CREATE TRIGGER audit_patient_teeth AFTER INSERT OR UPDATE OR DELETE ON patient_teeth
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_tooth_history AFTER INSERT OR UPDATE OR DELETE ON tooth_history
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- ====================================================================
--      HELPER FUNCTIONS FOR DENTAL CHART OPERATIONS
-- ====================================================================

-- Function to get complete tooth history for a patient
CREATE OR REPLACE FUNCTION get_patient_tooth_history(
    p_patient_id UUID,
    p_tooth_number INT DEFAULT NULL
)
    RETURNS TABLE (
                      tooth_number INT,
                      condition_name VARCHAR(100),
                      change_date TIMESTAMPTZ,
                      treatment_description VARCHAR(255),
                      notes TEXT,
                      recorded_by_name VARCHAR(100)
                  ) AS $$
BEGIN
    RETURN QUERY
        SELECT
            th.tooth_number,
            tc.name as condition_name,
            th.change_date,
            p.name as treatment_description,
            th.notes,
            s.full_name as recorded_by_name
        FROM tooth_history th
                 LEFT JOIN tooth_conditions tc ON th.condition_id = tc.id
                 LEFT JOIN treatments t ON th.treatment_id = t.id
                 LEFT JOIN procedures p ON t.procedure_id = p.id
                 LEFT JOIN staff s ON th.recorded_by = s.id
        WHERE th.patient_id = p_patient_id
          AND (p_tooth_number IS NULL OR th.tooth_number = p_tooth_number)
        ORDER BY th.change_date DESC;
END;
$$ LANGUAGE plpgsql;

-- Function to get dental chart summary for UI
CREATE OR REPLACE FUNCTION get_dental_chart_for_ui(p_patient_id UUID)
    RETURNS JSON AS $$
DECLARE
    v_result JSON;
BEGIN
    SELECT json_build_object(
                   'teeth', json_agg(
                    json_build_object(
                            'toothNumber', pt.tooth_number,
                            'condition', tc.code,
                            'conditionName', tc.name,
                            'colorHex', tc.color_hex,
                            'notes', pt.notes,
                            'isMonitored', pt.is_monitored,
                            'lastTreatmentDate', pt.last_treatment_date,
                            'nextScheduledDate', pt.next_scheduled_treatment_date
                    ) ORDER BY pt.tooth_number
                            )
           ) INTO v_result
    FROM patient_teeth pt
             LEFT JOIN tooth_conditions tc ON pt.current_condition_id = tc.id
    WHERE pt.patient_id = p_patient_id;

    RETURN v_result;
END;
$$ LANGUAGE plpgsql;

-- ====================================================================
--      INITIALIZE EXISTING PATIENTS
-- ====================================================================

-- Initialize teeth for all existing patients
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
        -- Get healthy condition ID
        SELECT id INTO healthy_condition_id FROM tooth_conditions WHERE code = 'HEALTHY';

        -- Loop through all active patients
        FOR patient_record IN SELECT id FROM patients WHERE deleted_at IS NULL
            LOOP
                -- Create teeth records for each patient
                FOREACH tooth IN ARRAY tooth_numbers
                    LOOP
                        INSERT INTO patient_teeth (patient_id, tooth_number, current_condition_id)
                        VALUES (patient_record.id, tooth, healthy_condition_id)
                        ON CONFLICT (patient_id, tooth_number) DO NOTHING;
                    END LOOP;
            END LOOP;
    END $$;

-- ====================================================================
--      SAMPLE QUERIES FOR COMMON OPERATIONS
-- ====================================================================

-- Get all teeth requiring attention across all patients
-- SELECT * FROM v_teeth_requiring_attention;

-- Get dental chart for a specific patient
-- SELECT * FROM v_dental_chart WHERE patient_id = 'patient-uuid-here';

-- Get tooth history for a specific patient
-- SELECT * FROM get_patient_tooth_history('patient-uuid-here');

-- Get dental chart JSON for UI
-- SELECT get_dental_chart_for_ui('patient-uuid-here');

-- Update a tooth condition
-- UPDATE patient_teeth
-- SET current_condition_id = (SELECT id FROM tooth_conditions WHERE code = 'CAVITY'),
--     notes = 'Cavity detected on mesial surface',
--     is_monitored = TRUE,
--     updated_by = 'staff-uuid-here'
-- WHERE patient_id = 'patient-uuid-here' AND tooth_number = 14;
commit ;
