/* ===================================================================
   DENTAL CLINIC MVP  –  H2 IN-MEMORY VERSION
   Adapted from PostgreSQL script  (2025-07-20)
   =================================================================== */

-- Enable PostgreSQL-like syntax where possible
SET MODE PostgreSQL;

-----------------------------------------------------------------------
-- SEQUENCES
-----------------------------------------------------------------------
CREATE SEQUENCE invoice_number_seq START WITH 1 INCREMENT BY 1;

-----------------------------------------------------------------------
-- CORE PLATFORM TABLES
-----------------------------------------------------------------------
CREATE TABLE clinic_info (
                             id            BOOLEAN DEFAULT TRUE PRIMARY KEY,
                             name          VARCHAR(255) NOT NULL,
                             address       TEXT,
                             phone_number  VARCHAR(50),
                             email         VARCHAR(100),
                             timezone      VARCHAR(50)  NOT NULL DEFAULT 'UTC',
                             created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT clinic_info_singleton CHECK (id=TRUE)
);

CREATE TABLE specialties (
                             id          UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                             name        VARCHAR(100) UNIQUE NOT NULL,
                             description TEXT,
                             is_active   BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-----------------------------------------------------------------------
-- STAFF TABLES
-----------------------------------------------------------------------
CREATE TABLE staff (
                       id           UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                       full_name    VARCHAR(100) NOT NULL,
                       role         VARCHAR(50)  NOT NULL,
                       email        VARCHAR(100) UNIQUE NOT NULL,
                       phone_number VARCHAR(30),
                       is_active    BOOLEAN DEFAULT TRUE,
                       created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT chk_staff_email_format
                           CHECK (email LIKE '%@%.%')
    );

CREATE TABLE staff_specialties (
                                   staff_id     UUID NOT NULL,
                                   specialty_id UUID NOT NULL,
                                   PRIMARY KEY (staff_id, specialty_id),
                                   FOREIGN KEY (staff_id)     REFERENCES staff(id)       ON DELETE CASCADE,
                                   FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE CASCADE
);

-----------------------------------------------------------------------
-- PATIENT TABLES
-----------------------------------------------------------------------
CREATE TABLE patients (
                          id                      UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                          public_facing_id        VARCHAR(20) UNIQUE NOT NULL,
                          full_name               VARCHAR(150) NOT NULL,
                          date_of_birth           DATE NOT NULL,
                          gender                  VARCHAR(10),
                          phone_number            VARCHAR(30),
                          email                   VARCHAR(100),
                          address                 TEXT,
                          insurance_provider      VARCHAR(100),
                          insurance_number        VARCHAR(50),
                          important_medical_notes TEXT,
                          balance                 DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                          is_active               BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          created_by              UUID,
                          CONSTRAINT fk_patient_created_by FOREIGN KEY (created_by) REFERENCES staff(id),
                          CONSTRAINT chk_patient_email_format
                              CHECK (email IS NULL OR email LIKE '%@%.%')
    );

-----------------------------------------------------------------------
-- MEDICAL RECORDS
-----------------------------------------------------------------------
CREATE TABLE procedures (
                            id                       UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                            specialty_id             UUID NOT NULL,
                            procedure_code           VARCHAR(20) UNIQUE,
                            name                     VARCHAR(255) NOT NULL,
                            description              TEXT,
                            default_cost             DECIMAL(10,2),
                            default_duration_minutes INT,
                            is_active                BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT chk_procedure_cost_positive     CHECK (default_cost IS NULL OR default_cost>0),
                            CONSTRAINT chk_procedure_duration_positive CHECK (default_duration_minutes IS NULL OR default_duration_minutes>0),
                            FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE CASCADE
);

CREATE TABLE appointments (
                              id                   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                              specialty_id         UUID NOT NULL,
                              patient_id           UUID NOT NULL,
                              doctor_id            UUID,
                              appointment_datetime TIMESTAMP NOT NULL,
                              duration_minutes     INT NOT NULL DEFAULT 30,
                              status               VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
                              notes                TEXT,
                              created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              created_by           UUID,
                              CONSTRAINT chk_appointment_duration CHECK (duration_minutes>0),
                              FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE RESTRICT,
                              FOREIGN KEY (patient_id)   REFERENCES patients(id)     ON DELETE CASCADE,
                              FOREIGN KEY (doctor_id)    REFERENCES staff(id)        ON DELETE SET NULL,
                              FOREIGN KEY (created_by)   REFERENCES staff(id)
);

CREATE TABLE treatments (
                            id              UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                            appointment_id  UUID NOT NULL,
                            patient_id      UUID NOT NULL,
                            procedure_id    UUID NOT NULL,
                            doctor_id       UUID,
                            tooth_number    INT,
                            status          VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
                            cost            DECIMAL(10,2) NOT NULL,
                             visit_notes TEXT,
                             visit_date  DATE NOT NULL,
                            created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            created_by      UUID,
                            CONSTRAINT chk_treatment_cost_positive  CHECK (cost>=0),
                            CONSTRAINT chk_tooth_number   CHECK (tooth_number IS NULL OR tooth_number BETWEEN 11 AND 48),
                            FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
                            FOREIGN KEY (patient_id)     REFERENCES patients(id)     ON DELETE CASCADE,
                            FOREIGN KEY (procedure_id)   REFERENCES procedures(id)   ON DELETE RESTRICT,
                            FOREIGN KEY (doctor_id)      REFERENCES staff(id)        ON DELETE SET NULL,
                            FOREIGN KEY (created_by)     REFERENCES staff(id)
);

-----------------------------------------------------------------------
-- DENTAL CHART
-----------------------------------------------------------------------
CREATE TABLE tooth_conditions (
                                  id          UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                  code        VARCHAR(20) UNIQUE NOT NULL,
                                  name        VARCHAR(100) NOT NULL,
                                  description TEXT,
                                  color_hex   VARCHAR(7),
                                  is_active   BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tooth_conditions (code,name,description,color_hex) VALUES
                                                                   ('HEALTHY','Healthy','Tooth is healthy with no issues','#4CAF50'),
                                                                   ('CAVITY','Cavity','Tooth has cavity/caries','#FF5722'),
                                                                   ('FILLED','Filled','Tooth has been filled','#2196F3'),
                                                                   ('CROWN','Crown','Tooth has a crown','#9C27B0'),
                                                                   ('ROOT_CANAL','Root Canal','Tooth has had root canal treatment','#FF9800'),
                                                                   ('EXTRACTED','Extracted','Tooth has been extracted','#9E9E9E'),
                                                                   ('MISSING','Missing','Tooth is congenitally missing','#757575'),
                                                                   ('IMPLANT','Implant','Dental implant','#00BCD4'),
                                                                   ('BRIDGE','Bridge','Part of dental bridge','#3F51B5'),
                                                                   ('IMPACTED','Impacted','Tooth is impacted','#E91E63'),
                                                                   ('FRACTURED','Fractured','Tooth is fractured','#F44336');

CREATE TABLE patient_teeth (
                               id                   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                               patient_id           UUID NOT NULL,
                               tooth_number         INT  NOT NULL CHECK (tooth_number BETWEEN 11 AND 48),
                               current_condition_id UUID,
                               notes                TEXT,
                               last_ visit_date  DATE,
                               created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               UNIQUE(patient_id,tooth_number),
                               FOREIGN KEY (patient_id)           REFERENCES patients(id)          ON DELETE CASCADE,
                               FOREIGN KEY (current_condition_id) REFERENCES tooth_conditions(id)
);

CREATE TABLE tooth_history (
                               id               UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                               patient_tooth_id UUID NOT NULL,
                               patient_id       UUID NOT NULL,
                               tooth_number     INT NOT NULL,
                               condition_id     UUID,
                               treatment_id     UUID,
                               change_date      TIMESTAMP NOT NULL,
                               notes            TEXT,
                               recorded_by      UUID NOT NULL,
                               created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (patient_tooth_id) REFERENCES patient_teeth(id) ON DELETE CASCADE,
                               FOREIGN KEY (patient_id)       REFERENCES patients(id)      ON DELETE CASCADE,
                               FOREIGN KEY (condition_id)     REFERENCES tooth_conditions(id),
                               FOREIGN KEY (treatment_id)     REFERENCES treatments(id),
                               FOREIGN KEY (recorded_by)      REFERENCES staff(id)
);

-----------------------------------------------------------------------
-- FINANCIAL
-----------------------------------------------------------------------
CREATE TABLE invoices (
                          id            UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                          patient_id    UUID NOT NULL,
                          invoice_number VARCHAR(50) UNIQUE NOT NULL,
                          issue_date    DATE NOT NULL,
                          due_date      DATE,
                          total_amount  DECIMAL(10,2) NOT NULL,
                          status        VARCHAR(50) NOT NULL DEFAULT 'UNPAID',
                          created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          created_by    UUID,
                          CONSTRAINT chk_invoice_due_after_issue CHECK (due_date IS NULL OR due_date>=issue_date),
                          CONSTRAINT chk_invoice_total_positive  CHECK (total_amount>=0),
                          FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
                          FOREIGN KEY (created_by) REFERENCES staff(id)
);

CREATE TABLE invoice_items (
                               id           UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                               invoice_id   UUID NOT NULL,
                               treatment_id UUID UNIQUE,
                               description  VARCHAR(255) NOT NULL,
                               amount       DECIMAL(10,2) NOT NULL,
                               created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT chk_invoice_item_amount_positive CHECK (amount>=0),
                               FOREIGN KEY (invoice_id)  REFERENCES invoices(id)    ON DELETE CASCADE,
                               FOREIGN KEY (treatment_id) REFERENCES treatments(id) ON DELETE RESTRICT
);

CREATE TABLE payments (
                          id             UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                          invoice_id     UUID,
                          patient_id     UUID NOT NULL,
                          payment_date   DATE NOT NULL,
                          amount         DECIMAL(10,2) NOT NULL,
                          payment_method VARCHAR(50),
                          type           VARCHAR(50) NOT NULL DEFAULT 'PAYMENT',
                          description    VARCHAR(255),
                          created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          created_by     UUID,
                          CONSTRAINT chk_payment_amount_positive CHECK (amount>0),
                          FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE SET NULL,
                          FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
                          FOREIGN KEY (created_by) REFERENCES staff(id)
);

-----------------------------------------------------------------------
-- SUPPORTING TABLES
-----------------------------------------------------------------------
CREATE TABLE lab_requests (
                              id               UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                              patient_id       UUID NOT NULL,
                              order_number     VARCHAR(50) UNIQUE,
                              item_description TEXT NOT NULL,
                              tooth_number     INT,
                              date_sent        DATE,
                              date_due         DATE,
                              lab_name         VARCHAR(255),
                              status           VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                              created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE documents (
                           id                   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                           patient_id           UUID NOT NULL,
                           uploaded_by_staff_id UUID,
                           file_name            VARCHAR(255) NOT NULL,
                           file_path            TEXT NOT NULL,
                           file_size_bytes      BIGINT,
                           mime_type            VARCHAR(100),
                           type                 VARCHAR(50),
                           created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (patient_id)           REFERENCES patients(id) ON DELETE CASCADE,
                           FOREIGN KEY (uploaded_by_staff_id) REFERENCES staff(id)    ON DELETE SET NULL
);

CREATE TABLE notes (
                       id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                       patient_id UUID NOT NULL,
                       content    TEXT NOT NULL,
                       created_by UUID,
                       note_date  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
                       FOREIGN KEY (created_by) REFERENCES staff(id)    ON DELETE SET NULL
);

-----------------------------------------------------------------------
-- INDEXES
-----------------------------------------------------------------------
CREATE INDEX idx_staff_email        ON staff(email);
CREATE INDEX idx_patients_public_id ON patients(public_facing_id);
CREATE INDEX idx_patients_name      ON patients(full_name);
CREATE INDEX idx_patients_phone     ON patients(phone_number);

CREATE INDEX idx_appointments_datetime ON appointments(appointment_datetime);
CREATE INDEX idx_appointments_patient  ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor   ON appointments(doctor_id);
CREATE INDEX idx_appointments_status   ON appointments(status);

CREATE INDEX idx_treatments_patient    ON treatments(patient_id);
CREATE INDEX idx_treatments_appointment ON treatments(appointment_id);

CREATE INDEX idx_invoices_patient ON invoices(patient_id);
CREATE INDEX idx_invoices_status  ON invoices(status);
CREATE INDEX idx_payments_patient ON payments(patient_id);
CREATE INDEX idx_payments_date    ON payments(payment_date);

CREATE INDEX idx_patient_teeth_patient ON patient_teeth(patient_id);
CREATE INDEX idx_tooth_history_patient ON tooth_history(patient_id);

-----------------------------------------------------------------------
-- VIEWS
-----------------------------------------------------------------------
CREATE VIEW v_dental_chart AS
SELECT
    pt.patient_id,
    pt.tooth_number,
    tc.code  AS condition_code,
    tc.name  AS condition_name,
    tc.color_hex,
    pt.notes,
    pt.last_ visit_date
FROM patient_teeth pt
         LEFT JOIN tooth_conditions tc ON pt.current_condition_id = tc.id
ORDER BY pt.tooth_number;

CREATE VIEW v_upcoming_appointments AS
SELECT
    a.id,
    a.appointment_datetime,
    a.duration_minutes,
    a.status,
    p.full_name        AS patient_name,
    p.public_facing_id AS patient_id,
    p.phone_number     AS patient_phone,
    s.full_name        AS doctor_name,
    sp.name            AS specialty
FROM appointments a
         JOIN patients     p  ON a.patient_id = p.id
         LEFT JOIN staff   s  ON a.doctor_id = s.id
         JOIN specialties  sp ON a.specialty_id = sp.id
WHERE a.appointment_datetime >= CURRENT_TIMESTAMP
  AND a.status NOT IN ('CANCELLED','NO_SHOW')
ORDER BY a.appointment_datetime;

CREATE VIEW v_patient_financial_summary AS
SELECT
    p.id,
    p.full_name,
    p.public_facing_id,
    p.balance,
    COUNT(DISTINCT i.id)                            AS total_invoices,
    SUM(CASE WHEN i.status='UNPAID' THEN 1 ELSE 0 END) AS unpaid_invoices,
    COALESCE(SUM(CASE WHEN i.status='UNPAID' THEN i.total_amount ELSE 0 END),0) AS total_unpaid
FROM patients p
         LEFT JOIN invoices i ON p.id = i.patient_id
GROUP BY p.id, p.full_name, p.public_facing_id, p.balance;

-----------------------------------------------------------------------
-- INITIAL DATA
-----------------------------------------------------------------------
MERGE INTO clinic_info (id, name, timezone) KEY(id) VALUES (TRUE, 'Dental Clinic', 'UTC');

INSERT INTO specialties (name, description) VALUES ('General Dentistry', 'General dental care and procedures');
INSERT INTO specialties (name, description) VALUES ('Orthodontics', 'Braces and teeth alignment');
INSERT INTO specialties (name, description) VALUES ('Endodontics', 'Root canal treatment');
INSERT INTO specialties (name, description) VALUES ('Periodontics', 'Gum disease treatment');
INSERT INTO specialties (name, description) VALUES ('Prosthodontics', 'Crowns, bridges, and dentures');
INSERT INTO specialties (name, description) VALUES ('Oral Surgery', 'Tooth extractions and surgical procedures');
INSERT INTO specialties (name, description) VALUES ('Pediatric Dentistry', 'Children''s dental care');

-- Default system user for automated processes
MERGE INTO staff (id, full_name, role, email, phone_number) KEY(id)
VALUES ('00000000-0000-0000-0000-000000000000', 'System', 'ADMIN', 'system@clinic.sa', '0000000000');
