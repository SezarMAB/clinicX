-- ====================================================================
-- ADVANCED FEATURES MIGRATION
-- Version: 2.0 - Post-MVP Features
-- ====================================================================
rollback;

BEGIN;

-- ====================================================================
-- AUDIT AND SECURITY TABLES
-- ====================================================================

CREATE TABLE audit_log (
                           id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           table_name  VARCHAR(50)  NOT NULL,
                           record_id   UUID         NOT NULL,
                           action      VARCHAR(20)  NOT NULL,
                           changed_by  UUID         REFERENCES staff (id) ON DELETE SET NULL,
                           changed_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                           ip_address  INET,
                           user_agent  TEXT,
                           old_values  JSONB,
                           new_values  JSONB,
                           change_note TEXT
);

CREATE TABLE login_attempts (
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
-- COMPLEX SCHEDULING TABLES
-- ====================================================================

CREATE TABLE staff_schedules (
                                 id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 staff_id    UUID NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
                                 day_of_week INT  NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
                                 start_time  TIME NOT NULL,
                                 end_time    TIME NOT NULL,
                                 created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 created_by  UUID REFERENCES staff(id),
                                 updated_by  UUID REFERENCES staff(id),
                                 CONSTRAINT chk_schedule_times CHECK (end_time > start_time),
                                 UNIQUE (staff_id, day_of_week, start_time, end_time)
);

CREATE TABLE schedule_overrides (
                                    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    staff_id      UUID    NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
                                    override_date DATE    NOT NULL,
                                    is_available  BOOLEAN NOT NULL,
                                    start_time    TIME,
                                    end_time      TIME,
                                    reason        VARCHAR(255),
                                    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                    created_by    UUID REFERENCES staff(id),
                                    updated_by    UUID REFERENCES staff(id),
                                    CONSTRAINT chk_override_times CHECK (
                                        (is_available = FALSE) OR
                                        (is_available = TRUE AND start_time IS NOT NULL AND end_time IS NOT NULL AND end_time > start_time)
                                        ),
                                    UNIQUE (staff_id, override_date)
);

CREATE TABLE appointment_slots (
                                   id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   specialty_id     UUID        NOT NULL REFERENCES specialties (id) ON DELETE CASCADE,
                                   doctor_id        UUID        NOT NULL REFERENCES staff (id) ON DELETE CASCADE,
                                   slot_datetime    TIMESTAMPTZ NOT NULL,
                                   duration_minutes INT         NOT NULL,
                                   is_available     BOOLEAN     NOT NULL DEFAULT TRUE,
                                   max_patients     INT         NOT NULL DEFAULT 1,
                                   booked_count     INT         NOT NULL DEFAULT 0,
                                   created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                   updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                   CONSTRAINT chk_slot_duration CHECK (duration_minutes > 0),
                                   CONSTRAINT chk_max_patients CHECK (max_patients > 0),
                                   CONSTRAINT chk_booked_count CHECK (booked_count >= 0 AND booked_count <= max_patients),
                                   UNIQUE(doctor_id, slot_datetime)
);

-- ====================================================================
-- INSURANCE AUTHORIZATION TRACKING
-- ====================================================================

CREATE TABLE insurance_authorizations (
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
                                          created_by         UUID REFERENCES staff(id),
                                          updated_by         UUID REFERENCES staff(id),
                                          CONSTRAINT chk_auth_dates CHECK (approved_date IS NULL OR approved_date >= requested_date),
                                          CONSTRAINT chk_expiry_date CHECK (expiry_date IS NULL OR expiry_date > approved_date)
);

-- ====================================================================
-- REFERRAL TRACKING
-- ====================================================================

CREATE TABLE referrals (
                           id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           patient_id            UUID        NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                           from_doctor_id        UUID        REFERENCES staff (id) ON DELETE SET NULL,
                           to_doctor_id          UUID        REFERENCES staff (id) ON DELETE SET NULL,
                           to_specialty_id       UUID        REFERENCES specialties (id) ON DELETE SET NULL,
                           external_doctor_name  VARCHAR(255),
                           external_clinic_name  VARCHAR(255),
                           reason                TEXT        NOT NULL,
                           urgency               VARCHAR(20) NOT NULL DEFAULT 'ROUTINE',
                           status                VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                           referral_date         DATE        NOT NULL,
                           appointment_id        UUID        REFERENCES appointments (id) ON DELETE SET NULL,
                           notes                 TEXT,
                           created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           created_by            UUID REFERENCES staff(id),
                           updated_by            UUID REFERENCES staff(id),
                           CONSTRAINT chk_referral_type CHECK (
                               (to_doctor_id IS NOT NULL OR external_doctor_name IS NOT NULL)
                               )
);

-- ====================================================================
-- TASK MANAGEMENT
-- ====================================================================

CREATE TABLE tasks (
                       id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
                       patient_id           UUID REFERENCES patients (id) ON DELETE CASCADE,
                       assigned_to_staff_id UUID        REFERENCES staff (id) ON DELETE SET NULL,
                       assigned_by_staff_id UUID        REFERENCES staff (id) ON DELETE SET NULL,
                       description          TEXT        NOT NULL,
                       priority             VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                       due_date             DATE,
                       status               VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                       completed_at         TIMESTAMPTZ,
                       completed_by         UUID        REFERENCES staff (id) ON DELETE SET NULL,
                       created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ====================================================================
-- TAGS SYSTEM
-- ====================================================================

CREATE TABLE tags (
                      id          UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
                      tag_name    VARCHAR(50) UNIQUE NOT NULL,
                      description TEXT,
                      color_hex   VARCHAR(7),
                      is_active   BOOLEAN            NOT NULL DEFAULT TRUE,
                      created_at  TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
                      updated_at  TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
                      created_by  UUID REFERENCES staff(id),
                      updated_by  UUID REFERENCES staff(id)
);

CREATE TABLE patient_tags (
                              patient_id UUID NOT NULL REFERENCES patients (id) ON DELETE CASCADE,
                              tag_id     UUID NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                              created_by UUID REFERENCES staff(id),
                              PRIMARY KEY (patient_id, tag_id)
);

-- ====================================================================
-- ADD MISSING COLUMNS TO EXISTING TABLES
-- ====================================================================

-- Add audit fields to existing tables
ALTER TABLE clinic_info ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE clinic_info ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE specialties ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE specialties ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE staff ADD COLUMN initials VARCHAR(5) UNIQUE;
ALTER TABLE staff ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE staff ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE staff ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE patients ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE patients ADD COLUMN updated_by UUID REFERENCES staff(id);
ALTER TABLE patients ADD COLUMN deleted_by UUID REFERENCES staff(id);

ALTER TABLE procedures ADD COLUMN requires_authorization BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE procedures ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE procedures ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE appointments ADD COLUMN confirmation_sent_at TIMESTAMPTZ;
ALTER TABLE appointments ADD COLUMN reminder_sent_at TIMESTAMPTZ;
ALTER TABLE appointments ADD COLUMN checked_in_at TIMESTAMPTZ;
ALTER TABLE appointments ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE treatments ADD COLUMN assistant_id UUID REFERENCES staff(id);
ALTER TABLE treatments ADD COLUMN item_identifier VARCHAR(100);
ALTER TABLE treatments ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'EUR';
ALTER TABLE treatments ADD COLUMN material_used VARCHAR(255);
ALTER TABLE treatments ADD COLUMN post_op_instructions TEXT;
ALTER TABLE treatments ADD COLUMN is_billable BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE treatments ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE invoices ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'EUR';
ALTER TABLE invoices ADD COLUMN sent_at TIMESTAMPTZ;
ALTER TABLE invoices ADD COLUMN paid_at TIMESTAMPTZ;
ALTER TABLE invoices ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE invoice_items ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE invoice_items ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE invoice_items ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE payments ADD COLUMN reference_number VARCHAR(100);
ALTER TABLE payments ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE payments ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE lab_requests ADD COLUMN item_identifier VARCHAR(100);
ALTER TABLE lab_requests ADD COLUMN date_received DATE;
ALTER TABLE lab_requests ADD COLUMN lab_cost DECIMAL(10, 2);
ALTER TABLE lab_requests ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE lab_requests ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE documents ADD COLUMN is_archived BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE documents ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE notes ADD COLUMN is_pinned BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE notes ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE notes ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE tooth_conditions ADD COLUMN icon VARCHAR(50);
ALTER TABLE tooth_conditions ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE tooth_conditions ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE tooth_conditions ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE patient_teeth ADD COLUMN next_scheduled_treatment_date DATE;
ALTER TABLE patient_teeth ADD COLUMN is_monitored BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE patient_teeth ADD COLUMN created_by UUID REFERENCES staff(id);
ALTER TABLE patient_teeth ADD COLUMN updated_by UUID REFERENCES staff(id);

ALTER TABLE tooth_history ADD COLUMN mobility_score INT CHECK (mobility_score BETWEEN 0 AND 3);
ALTER TABLE tooth_history ADD COLUMN pocket_depth_mm DECIMAL(3,1);
ALTER TABLE tooth_history ADD COLUMN bleeding_on_probing BOOLEAN;
ALTER TABLE tooth_history ADD COLUMN plaque_present BOOLEAN;

-- ====================================================================
-- ADVANCED DENTAL TRACKING TABLES
-- ====================================================================

CREATE TABLE tooth_measurements (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    patient_tooth_id UUID NOT NULL REFERENCES patient_teeth(id) ON DELETE CASCADE,
                                    measurement_date DATE NOT NULL,
                                    mesial_buccal_depth DECIMAL(3,1),
                                    buccal_depth DECIMAL(3,1),
                                    distal_buccal_depth DECIMAL(3,1),
                                    mesial_lingual_depth DECIMAL(3,1),
                                    lingual_depth DECIMAL(3,1),
                                    distal_lingual_depth DECIMAL(3,1),
                                    recession_buccal DECIMAL(3,1),
                                    recession_lingual DECIMAL(3,1),
                                    furcation_involvement INT CHECK (furcation_involvement BETWEEN 0 AND 3),
                                    mobility_score INT CHECK (mobility_score BETWEEN 0 AND 3),
                                    recorded_by UUID NOT NULL REFERENCES staff(id),
                                    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                    UNIQUE(patient_tooth_id, measurement_date)
);

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
-- ADVANCED FUNCTIONS AND TRIGGERS
-- ====================================================================

-- Enhanced appointment validation
CREATE OR REPLACE FUNCTION appointment_end_time(start_time TIMESTAMPTZ, duration_mins INT)
    RETURNS TIMESTAMPTZ AS $$
BEGIN
    RETURN start_time + (duration_mins * INTERVAL '1 minute');
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Add exclusion constraint to prevent double-booking
ALTER TABLE appointments ADD CONSTRAINT prevent_double_booking
    EXCLUDE USING gist (
    doctor_id WITH =,
    tstzrange(appointment_datetime, appointment_end_time(appointment_datetime, duration_minutes)) WITH &&
    ) WHERE (status NOT IN ('CANCELLED', 'NO_SHOW'));

-- Function to validate appointment schedule
CREATE OR REPLACE FUNCTION validate_appointment_schedule()
    RETURNS TRIGGER AS $$
DECLARE
    v_day_of_week INT;
    v_appointment_time TIME;
    v_is_available BOOLEAN;
BEGIN
    v_day_of_week := EXTRACT(ISODOW FROM NEW.appointment_datetime);
    v_appointment_time := NEW.appointment_datetime::TIME;

    SELECT is_available INTO v_is_available
    FROM schedule_overrides
    WHERE staff_id = NEW.doctor_id
      AND override_date = NEW.appointment_datetime::DATE;

    IF FOUND THEN
        IF NOT v_is_available THEN
            RAISE EXCEPTION 'Doctor is not available on this date';
        END IF;
    ELSE
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

-- Function to update appointment slot availability
CREATE OR REPLACE FUNCTION update_appointment_slot()
    RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.status NOT IN ('CANCELLED', 'NO_SHOW') THEN
        UPDATE appointment_slots
        SET is_available = FALSE,
            booked_count = booked_count + 1,
            updated_at = NOW()
        WHERE doctor_id = NEW.doctor_id
          AND slot_datetime = NEW.appointment_datetime;
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.status NOT IN ('CANCELLED', 'NO_SHOW') AND NEW.status IN ('CANCELLED', 'NO_SHOW') THEN
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

-- Generic audit trigger function
CREATE OR REPLACE FUNCTION audit_trigger_function()
    RETURNS TRIGGER AS $$
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

-- Enhanced patient balance calculation with audit trail
CREATE OR REPLACE FUNCTION update_patient_balance()
    RETURNS TRIGGER AS $$
DECLARE
    v_patient_id UUID;
    v_old_balance DECIMAL(10, 2);
    v_new_balance DECIMAL(10, 2);
BEGIN
    IF (TG_OP = 'DELETE') THEN
        v_patient_id := OLD.patient_id;
    ELSE
        v_patient_id := NEW.patient_id;
    END IF;

    SELECT balance INTO v_old_balance FROM patients WHERE id = v_patient_id;

    v_new_balance := (
        COALESCE((SELECT SUM(total_amount) FROM invoices WHERE patient_id = v_patient_id AND status != 'CANCELLED'), 0)
            -
        COALESCE((SELECT SUM(amount) FROM payments WHERE patient_id = v_patient_id AND type IN ('PAYMENT', 'CREDIT')), 0)
        );

    UPDATE patients SET balance = v_new_balance WHERE id = v_patient_id;

    IF v_old_balance != v_new_balance THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, new_values)
        VALUES ('patients', v_patient_id, 'BALANCE_UPDATE',
                jsonb_build_object('balance', v_old_balance),
                jsonb_build_object('balance', v_new_balance));
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Function to update tooth from treatment
CREATE OR REPLACE FUNCTION update_tooth_from_treatment()
    RETURNS TRIGGER AS $$
DECLARE
    v_condition_id UUID;
    v_procedure_name VARCHAR(255);
BEGIN
    IF NEW.tooth_number IS NOT NULL THEN
        SELECT name INTO v_procedure_name
        FROM procedures
        WHERE id = NEW.procedure_id;

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

-- ====================================================================
-- ATTACH NEW TRIGGERS
-- ====================================================================

CREATE TRIGGER trg_appointment_slot_update
    AFTER INSERT OR UPDATE ON appointments
    FOR EACH ROW EXECUTE FUNCTION update_appointment_slot();

CREATE TRIGGER trg_validate_appointment_schedule
    BEFORE INSERT OR UPDATE ON appointments
    FOR EACH ROW EXECUTE FUNCTION validate_appointment_schedule();

CREATE TRIGGER trg_update_tooth_from_treatment
    AFTER INSERT ON treatments
    FOR EACH ROW EXECUTE FUNCTION update_tooth_from_treatment();

-- Audit triggers
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

CREATE TRIGGER audit_patient_teeth AFTER INSERT OR UPDATE OR DELETE ON patient_teeth
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_tooth_history AFTER INSERT OR UPDATE OR DELETE ON tooth_history
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- ====================================================================
-- ADDITIONAL INDEXES
-- ====================================================================

CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_by ON audit_log(changed_by);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at);
CREATE INDEX idx_login_attempts_email ON login_attempts(email);
CREATE INDEX idx_login_attempts_time ON login_attempts(attempted_at);

CREATE INDEX idx_appointment_slots_availability ON appointment_slots(doctor_id, slot_datetime) WHERE is_available = TRUE;
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to_staff_id) WHERE status != 'COMPLETED';
CREATE INDEX idx_tasks_due_date ON tasks(due_date) WHERE status != 'COMPLETED';
CREATE INDEX idx_referrals_patient ON referrals(patient_id);
CREATE INDEX idx_referrals_status ON referrals(status) WHERE status != 'COMPLETED';

CREATE INDEX idx_tooth_measurements_tooth ON tooth_measurements(patient_tooth_id);
CREATE INDEX idx_tooth_measurements_date ON tooth_measurements(measurement_date);
CREATE INDEX idx_tooth_surface_conditions_tooth ON tooth_surface_conditions(patient_tooth_id);

-- ====================================================================
-- ADDITIONAL VIEWS
-- ====================================================================

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

CREATE VIEW v_staff_availability_today AS
WITH today_schedule AS (
    SELECT
        s.id,
        s.full_name,
        COALESCE(so.is_available, TRUE) AS is_available,
        COALESCE(so.start_time, ss.start_time) AS start_time,
        COALESCE(so.end_time, ss.end_time) AS end_time
    FROM staff s
             LEFT JOIN staff_schedules ss ON s.id = ss.staff_id
        AND ss.day_of_week = EXTRACT(ISODOW FROM CURRENT_DATE)
             LEFT JOIN schedule_overrides so ON s.id = so.staff_id
        AND so.override_date = CURRENT_DATE
    WHERE s.is_active = TRUE
)
SELECT
    ts.id,
    ts.full_name,
    ts.is_available,
    ts.start_time,
    ts.end_time,
    COUNT(a.id) AS appointments_today,
    STRING_AGG(
            a.appointment_datetime::TIME::TEXT || ' - ' ||
            (a.appointment_datetime + (a.duration_minutes || ' minutes')::INTERVAL)::TIME::TEXT,
            ', ' ORDER BY a.appointment_datetime
    ) AS appointment_times
FROM today_schedule ts
         LEFT JOIN appointments a ON ts.id = a.doctor_id
    AND DATE(a.appointment_datetime) = CURRENT_DATE
    AND a.status NOT IN ('CANCELLED', 'NO_SHOW')
WHERE ts.is_available = TRUE
GROUP BY ts.id, ts.full_name, ts.is_available, ts.start_time, ts.end_time;

CREATE VIEW v_insurance_authorization_summary AS
SELECT
    p.id AS patient_id,
    p.full_name,
    p.public_facing_id,
    p.insurance_provider,
    COUNT(ia.id) AS total_authorizations,
    COUNT(ia.id) FILTER (WHERE ia.status = 'PENDING') AS pending_authorizations,
    COUNT(ia.id) FILTER (WHERE ia.status = 'APPROVED') AS approved_authorizations,
    COUNT(ia.id) FILTER (WHERE ia.status = 'APPROVED' AND ia.expiry_date < CURRENT_DATE) AS expired_authorizations,
    SUM(ia.approved_amount) FILTER (WHERE ia.status = 'APPROVED') AS total_approved_amount
FROM patients p
         LEFT JOIN insurance_authorizations ia ON p.id = ia.patient_id
WHERE p.deleted_at IS NULL
GROUP BY p.id, p.full_name, p.public_facing_id, p.insurance_provider;

CREATE VIEW v_task_dashboard AS
SELECT
    t.id,
    t.description,
    t.priority,
    t.due_date,
    t.status,
    p.full_name AS patient_name,
    p.public_facing_id AS patient_id,
    assigned_to.full_name AS assigned_to_name,
    assigned_by.full_name AS assigned_by_name,
    t.created_at,
    CASE
        WHEN t.due_date < CURRENT_DATE AND t.status != 'COMPLETED' THEN 'OVERDUE'
        WHEN t.due_date = CURRENT_DATE AND t.status != 'COMPLETED' THEN 'DUE_TODAY'
        WHEN t.due_date = CURRENT_DATE + INTERVAL '1 day' AND t.status != 'COMPLETED' THEN 'DUE_TOMORROW'
        ELSE 'FUTURE'
        END AS urgency
FROM tasks t
         LEFT JOIN patients p ON t.patient_id = p.id
         LEFT JOIN staff assigned_to ON t.assigned_to_staff_id = assigned_to.id
         LEFT JOIN staff assigned_by ON t.assigned_by_staff_id = assigned_by.id
WHERE t.status != 'COMPLETED'
ORDER BY
    CASE t.priority
        WHEN 'HIGH' THEN 1
        WHEN 'MEDIUM' THEN 2
        WHEN 'LOW' THEN 3
        END,
    t.due_date NULLS LAST;

CREATE VIEW v_referral_tracking AS
SELECT
    r.id,
    p.full_name AS patient_name,
    p.public_facing_id AS patient_id,
    from_doc.full_name AS referring_doctor,
    COALESCE(to_doc.full_name, r.external_doctor_name) AS referred_to,
    COALESCE(sp.name, r.external_clinic_name) AS specialty_or_clinic,
    r.reason,
    r.urgency,
    r.status,
    r.referral_date,
    a.appointment_datetime AS follow_up_appointment,
    r.created_at
FROM referrals r
         JOIN patients p ON r.patient_id = p.id
         LEFT JOIN staff from_doc ON r.from_doctor_id = from_doc.id
         LEFT JOIN staff to_doc ON r.to_doctor_id = to_doc.id
         LEFT JOIN specialties sp ON r.to_specialty_id = sp.id
         LEFT JOIN appointments a ON r.appointment_id = a.id
ORDER BY
    CASE r.urgency
        WHEN 'URGENT' THEN 1
        WHEN 'SEMI_URGENT' THEN 2
        WHEN 'ROUTINE' THEN 3
        END,
    r.referral_date DESC;

-- ====================================================================
-- PERFORMANCE OPTIMIZATION INDEXES
-- ====================================================================

-- Composite indexes for common queries
CREATE INDEX idx_appointments_composite ON appointments(patient_id, appointment_datetime, status);
CREATE INDEX idx_treatments_composite ON treatments(patient_id, treatment_date, status);
CREATE INDEX idx_invoices_composite ON invoices(patient_id, issue_date, status);
CREATE INDEX idx_payments_composite ON payments(patient_id, payment_date);

-- Partial indexes for active records
CREATE INDEX idx_staff_active ON staff(email, full_name) WHERE is_active = TRUE AND deleted_at IS NULL;
CREATE INDEX idx_patients_active ON patients(full_name, public_facing_id) WHERE is_active = TRUE AND deleted_at IS NULL;
CREATE INDEX idx_procedures_active ON procedures(name, specialty_id) WHERE is_active = TRUE;

-- Indexes for scheduling
CREATE INDEX idx_staff_schedules_lookup ON staff_schedules(staff_id, day_of_week);
CREATE INDEX idx_schedule_overrides_lookup ON schedule_overrides(staff_id, override_date) WHERE is_available = TRUE;

-- Indexes for dental chart
CREATE INDEX idx_tooth_surface_conditions_lookup ON tooth_surface_conditions(patient_tooth_id, surface_id);
CREATE INDEX idx_tooth_measurements_latest ON tooth_measurements(patient_tooth_id, measurement_date DESC);

-- ====================================================================
-- MAINTENANCE FUNCTIONS
-- ====================================================================

-- Function to clean up old audit logs
CREATE OR REPLACE FUNCTION cleanup_old_audit_logs(days_to_keep INT DEFAULT 365)
    RETURNS INT AS $$
DECLARE
    deleted_count INT;
BEGIN
    DELETE FROM audit_log
    WHERE changed_at < CURRENT_DATE - (days_to_keep || ' days')::INTERVAL;

    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Function to archive old appointments
CREATE OR REPLACE FUNCTION archive_old_appointments(months_old INT DEFAULT 24)
    RETURNS TABLE(archived_count INT) AS $$
DECLARE
    v_archived_count INT;
BEGIN
    -- Create archive table if not exists
    CREATE TABLE IF NOT EXISTS appointments_archive (LIKE appointments INCLUDING ALL);

    -- Move old appointments to archive
    WITH moved AS (
        DELETE FROM appointments
            WHERE appointment_datetime < CURRENT_DATE - (months_old || ' months')::INTERVAL
                AND status IN ('COMPLETED', 'CANCELLED', 'NO_SHOW')
            RETURNING *
    )
    INSERT INTO appointments_archive SELECT * FROM moved;

    GET DIAGNOSTICS v_archived_count = ROW_COUNT;

    RETURN QUERY SELECT v_archived_count;
END;
$$ LANGUAGE plpgsql;

COMMIT;
