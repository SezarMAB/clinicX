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
    confirmation_sent_at TIMESTAMPTZ,                                  -- NEW: Track confirmations
    reminder_sent_at     TIMESTAMPTZ,                                  -- NEW: Track reminders
    checked_in_at        TIMESTAMPTZ,                                  -- NEW: Track check-ins
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by           UUID,                                          -- NEW: Audit field
    updated_by           UUID,                                          -- NEW: Audit field
    CONSTRAINT chk_appointment_duration CHECK (duration_minutes > 0),
    -- NEW: Prevent double-booking using exclusion constraint
    EXCLUDE USING gist (
        doctor_id WITH =,
        tstzrange(appointment_datetime, appointment_datetime + (duration_minutes || ' minutes')::interval) WITH &&
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

CREATE TABLE dental_charts
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    patient_id UUID UNIQUE NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
    chart_data JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,                                                    -- NEW: Audit field
    updated_by UUID                                                     -- NEW: Audit field
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

-- Audit indexes
CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_by ON audit_log(changed_by);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at);
CREATE INDEX idx_login_attempts_email ON login_attempts(email);
CREATE INDEX idx_login_attempts_time ON login_attempts(attempted_at);

-- JSON indexes
CREATE INDEX idx_dental_charts_data ON dental_charts USING GIN (chart_data);

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
