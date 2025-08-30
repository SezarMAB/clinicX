-- =========================================================
-- CLINICX COMPREHENSIVE DEMO DATA
-- =========================================================
-- This script loads comprehensive demo data for all tables
-- Includes multi-tenant setup with realistic medical data
-- =========================================================

-- Clean up any conflicting demo data first
DELETE FROM staff_specialties WHERE staff_id::text LIKE '40000001-%';
DELETE FROM specialties WHERE id::text LIKE '20000001-%' OR name IN (
    'General Dentistry', 'Orthodontics', 'Endodontics', 'Periodontics', 
    'Oral Surgery', 'Pediatric Dentistry', 'Prosthodontics', 
    'General Medicine', 'Cardiology', 'Dermatology'
);

-- =========================================================
-- CLINIC INFORMATION
-- =========================================================
INSERT INTO clinic_info (id, name, address, phone_number, email, timezone)
VALUES (true, 'ClinicX Demo Center', '123 Medical Plaza, Suite 100, Healthcare City, HC 12345', 
        '+1-555-0100', 'info@clinicx-demo.com', 'America/New_York')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    address = EXCLUDED.address,
    phone_number = EXCLUDED.phone_number,
    email = EXCLUDED.email,
    timezone = EXCLUDED.timezone;

-- =========================================================
-- SPECIALTY TYPES (for multi-tenant architecture)
-- =========================================================
INSERT INTO specialty_types (id, code, name, features, realm_name, is_active, created_by)
VALUES 
    ('10000001-0000-0000-0000-000000000001', 'DENTAL', 'Dental', 'dental_charts,tooth_tracking,lab_cases', 'dental-realm', true, 'system'),
    ('10000001-0000-0000-0000-000000000002', 'GP', 'General Practice', 'general_care,prescriptions,referrals', 'general-realm', true, 'system'),
    ('10000001-0000-0000-0000-000000000003', 'ORTHO', 'Orthodontics', 'braces,aligners,treatment_plans', 'ortho-realm', true, 'system'),
    ('10000001-0000-0000-0000-000000000004', 'CARDIO', 'Cardiology', 'ecg,stress_tests,cardiac_monitoring', 'cardio-realm', true, 'system'),
    ('10000001-0000-0000-0000-000000000005', 'PEDS', 'Pediatrics', 'growth_charts,immunizations,child_development', 'peds-realm', true, 'system')
ON CONFLICT (code) DO NOTHING;

-- =========================================================
-- SPECIALTIES
-- =========================================================
INSERT INTO specialties (id, name, description, is_active)
VALUES 
    ('20000001-0000-0000-0000-000000000001', 'General Dentistry', 'General dental care including cleanings, fillings, and preventive care', true),
    ('20000001-0000-0000-0000-000000000002', 'Orthodontics', 'Specialized dental care for teeth alignment and bite correction', true),
    ('20000001-0000-0000-0000-000000000003', 'Endodontics', 'Root canal therapy and treatment of dental pulp', true),
    ('20000001-0000-0000-0000-000000000004', 'Periodontics', 'Treatment of gum diseases and conditions', true),
    ('20000001-0000-0000-0000-000000000005', 'Oral Surgery', 'Surgical procedures including extractions and implants', true),
    ('20000001-0000-0000-0000-000000000006', 'Pediatric Dentistry', 'Dental care for children and adolescents', true),
    ('20000001-0000-0000-0000-000000000007', 'Prosthodontics', 'Restoration and replacement of teeth', true),
    ('20000001-0000-0000-0000-000000000008', 'General Medicine', 'Primary care and general health services', true),
    ('20000001-0000-0000-0000-000000000009', 'Cardiology', 'Heart and cardiovascular system care', true),
    ('20000001-0000-0000-0000-000000000010', 'Dermatology', 'Skin, hair, and nail conditions', true)
ON CONFLICT (id) DO NOTHING;

-- =========================================================
-- TENANTS (Multi-tenant demo setup)
-- =========================================================
INSERT INTO tenants (id, tenant_id, name, subdomain, realm_name, specialty, is_active, 
                    contact_email, contact_phone, address, subscription_plan, max_users, max_patients,
                    subscription_start_date, subscription_end_date, created_by)
VALUES 
    ('30000001-0000-0000-0000-000000000001', 'demo-dental', 'Smile Dental Clinic', 'smile-dental', 
     'dental-realm', 'DENTAL', true, 'admin@smile-dental.com', '+1-555-0201', 
     '456 Dental Ave, Tooth City, TC 23456', 'PROFESSIONAL', 20, 5000,
     '2024-01-01'::timestamp, '2025-12-31'::timestamp, 'system'),
    
    ('30000001-0000-0000-0000-000000000002', 'demo-general', 'Family Care Clinic', 'family-care', 
     'general-realm', 'GP', true, 'admin@family-care.com', '+1-555-0202', 
     '789 Health St, Wellness Town, WT 34567', 'ENTERPRISE', 50, 10000,
     '2024-01-01'::timestamp, '2025-12-31'::timestamp, 'system'),
    
    ('30000001-0000-0000-0000-000000000003', 'demo-ortho', 'Perfect Smile Orthodontics', 'perfect-smile', 
     'ortho-realm', 'ORTHO', true, 'admin@perfect-smile.com', '+1-555-0203', 
     '321 Brace Blvd, Align City, AC 45678', 'PROFESSIONAL', 15, 3000,
     '2024-01-01'::timestamp, '2025-12-31'::timestamp, 'system'),
    
    ('30000001-0000-0000-0000-000000000004', 'demo-cardio', 'Heart Health Center', 'heart-health', 
     'cardio-realm', 'CARDIO', true, 'admin@heart-health.com', '+1-555-0204', 
     '654 Cardiac Lane, Pulse City, PC 56789', 'ENTERPRISE', 30, 8000,
     '2024-01-01'::timestamp, '2025-12-31'::timestamp, 'system')
ON CONFLICT (tenant_id) DO NOTHING;

-- =========================================================
-- STAFF MEMBERS
-- =========================================================
INSERT INTO staff (id, full_name, email, phone_number, is_active, tenant_id, keycloak_user_id, source_realm)
VALUES 
    -- Dental Clinic Staff
    ('40000001-0000-0000-0000-000000000001', 'Dr. Sarah Johnson', 'sarah.johnson@smile-dental.com', 
     '+1-555-1001', true, 'demo-dental', 'kc-sarah-001', 'dental-realm'),
    ('40000001-0000-0000-0000-000000000002', 'Dr. Michael Chen', 'michael.chen@smile-dental.com', 
     '+1-555-1002', true, 'demo-dental', 'kc-michael-001', 'dental-realm'),
    ('40000001-0000-0000-0000-000000000003', 'Emily Watson', 'emily.watson@smile-dental.com', 
     '+1-555-1003', true, 'demo-dental', 'kc-emily-001', 'dental-realm'),
    ('40000001-0000-0000-0000-000000000004', 'James Miller', 'james.miller@smile-dental.com', 
     '+1-555-1004', true, 'demo-dental', 'kc-james-001', 'dental-realm'),
    
    -- General Practice Staff
    ('40000001-0000-0000-0000-000000000005', 'Dr. Robert Davis', 'robert.davis@family-care.com', 
     '+1-555-1005', true, 'demo-general', 'kc-robert-001', 'general-realm'),
    ('40000001-0000-0000-0000-000000000006', 'Dr. Lisa Anderson', 'lisa.anderson@family-care.com', 
     '+1-555-1006', true, 'demo-general', 'kc-lisa-001', 'general-realm'),
    ('40000001-0000-0000-0000-000000000007', 'Maria Garcia', 'maria.garcia@family-care.com', 
     '+1-555-1007', true, 'demo-general', 'kc-maria-001', 'general-realm'),
    
    -- Orthodontics Staff
    ('40000001-0000-0000-0000-000000000008', 'Dr. Jennifer White', 'jennifer.white@perfect-smile.com', 
     '+1-555-1008', true, 'demo-ortho', 'kc-jennifer-001', 'ortho-realm'),
    ('40000001-0000-0000-0000-000000000009', 'Dr. Thomas Brown', 'thomas.brown@perfect-smile.com', 
     '+1-555-1009', true, 'demo-ortho', 'kc-thomas-001', 'ortho-realm'),
    
    -- Cardiology Staff
    ('40000001-0000-0000-0000-000000000010', 'Dr. William Martinez', 'william.martinez@heart-health.com', 
     '+1-555-1010', true, 'demo-cardio', 'kc-william-001', 'cardio-realm'),
    ('40000001-0000-0000-0000-000000000011', 'Dr. Patricia Wilson', 'patricia.wilson@heart-health.com', 
     '+1-555-1011', true, 'demo-cardio', 'kc-patricia-001', 'cardio-realm'),
    
    -- Super Admin (cross-tenant access)
    ('40000001-0000-0000-0000-000000000099', 'System Administrator', 'admin@clinicx.com', 
     '+1-555-9999', true, NULL, 'kc-admin-001', 'master')
ON CONFLICT (email, tenant_id) DO NOTHING;

-- =========================================================
-- STAFF ROLES
-- =========================================================
INSERT INTO staff_roles (staff_id, role)
VALUES 
    ('40000001-0000-0000-0000-000000000001', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000001', 'ADMIN'),
    ('40000001-0000-0000-0000-000000000002', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000003', 'ASSISTANT'),
    ('40000001-0000-0000-0000-000000000004', 'RECEPTIONIST'),
    ('40000001-0000-0000-0000-000000000005', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000005', 'ADMIN'),
    ('40000001-0000-0000-0000-000000000006', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000007', 'NURSE'),
    ('40000001-0000-0000-0000-000000000008', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000009', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000010', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000011', 'DOCTOR'),
    ('40000001-0000-0000-0000-000000000099', 'SUPER_ADMIN')
ON CONFLICT DO NOTHING;

-- =========================================================
-- STAFF SPECIALTIES
-- =========================================================
INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES 
    ('40000001-0000-0000-0000-000000000001', '20000001-0000-0000-0000-000000000001'), -- Dr. Sarah - General Dentistry
    ('40000001-0000-0000-0000-000000000001', '20000001-0000-0000-0000-000000000003'), -- Dr. Sarah - Endodontics
    ('40000001-0000-0000-0000-000000000002', '20000001-0000-0000-0000-000000000001'), -- Dr. Michael - General Dentistry
    ('40000001-0000-0000-0000-000000000002', '20000001-0000-0000-0000-000000000005'), -- Dr. Michael - Oral Surgery
    ('40000001-0000-0000-0000-000000000005', '20000001-0000-0000-0000-000000000008'), -- Dr. Robert - General Medicine
    ('40000001-0000-0000-0000-000000000006', '20000001-0000-0000-0000-000000000008'), -- Dr. Lisa - General Medicine
    ('40000001-0000-0000-0000-000000000008', '20000001-0000-0000-0000-000000000002'), -- Dr. Jennifer - Orthodontics
    ('40000001-0000-0000-0000-000000000009', '20000001-0000-0000-0000-000000000002'), -- Dr. Thomas - Orthodontics
    ('40000001-0000-0000-0000-000000000010', '20000001-0000-0000-0000-000000000009'), -- Dr. William - Cardiology
    ('40000001-0000-0000-0000-000000000011', '20000001-0000-0000-0000-000000000009')  -- Dr. Patricia - Cardiology
ON CONFLICT DO NOTHING;

-- =========================================================
-- USER TENANT ACCESS
-- =========================================================
INSERT INTO user_tenant_access (id, user_id, tenant_id, is_primary, is_active, created_by)
VALUES 
    ('50000001-0000-0000-0000-000000000001', 'kc-sarah-001', 'demo-dental', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000002', 'kc-michael-001', 'demo-dental', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000003', 'kc-emily-001', 'demo-dental', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000004', 'kc-james-001', 'demo-dental', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000005', 'kc-robert-001', 'demo-general', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000006', 'kc-lisa-001', 'demo-general', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000007', 'kc-maria-001', 'demo-general', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000008', 'kc-jennifer-001', 'demo-ortho', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000009', 'kc-thomas-001', 'demo-ortho', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000010', 'kc-william-001', 'demo-cardio', true, true, 'system'),
    ('50000001-0000-0000-0000-000000000011', 'kc-patricia-001', 'demo-cardio', true, true, 'system'),
    -- Super admin has access to all tenants
    ('50000001-0000-0000-0000-000000000098', 'kc-admin-001', 'demo-dental', false, true, 'system'),
    ('50000001-0000-0000-0000-000000000097', 'kc-admin-001', 'demo-general', false, true, 'system'),
    ('50000001-0000-0000-0000-000000000096', 'kc-admin-001', 'demo-ortho', false, true, 'system'),
    ('50000001-0000-0000-0000-000000000095', 'kc-admin-001', 'demo-cardio', true, true, 'system')
ON CONFLICT (user_id, tenant_id) DO NOTHING;

-- =========================================================
-- USER TENANT ACCESS ROLES
-- =========================================================
INSERT INTO user_tenant_access_roles (user_tenant_access_id, role)
VALUES 
    ('50000001-0000-0000-0000-000000000001', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000001', 'ADMIN'),
    ('50000001-0000-0000-0000-000000000002', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000003', 'ASSISTANT'),
    ('50000001-0000-0000-0000-000000000004', 'RECEPTIONIST'),
    ('50000001-0000-0000-0000-000000000005', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000005', 'ADMIN'),
    ('50000001-0000-0000-0000-000000000006', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000007', 'NURSE'),
    ('50000001-0000-0000-0000-000000000008', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000009', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000010', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000011', 'DOCTOR'),
    ('50000001-0000-0000-0000-000000000095', 'SUPER_ADMIN'),
    ('50000001-0000-0000-0000-000000000096', 'SUPER_ADMIN'),
    ('50000001-0000-0000-0000-000000000097', 'SUPER_ADMIN'),
    ('50000001-0000-0000-0000-000000000098', 'SUPER_ADMIN')
ON CONFLICT DO NOTHING;

-- (procedure_templates removed in this schema)

-- =========================================================
-- PATIENTS
-- =========================================================
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender, phone_number, email, 
                     address, insurance_provider, insurance_number, important_medical_notes, 
                     balance, is_active, created_by)
VALUES 
    -- Dental Clinic Patients
    ('70000001-0000-0000-0000-000000000001', 'PT-2024-001', 'جون سميث', '1985-03-15', 'Male', 
     '+1-555-2001', 'john.smith@email.com', '123 Main St, Apt 4B, Springfield, SP 12345', 
     'Delta Dental', 'DD123456789', 'Allergic to penicillin', 150.00, true, '40000001-0000-0000-0000-000000000001'),
    
    ('70000001-0000-0000-0000-000000000002', 'PT-2024-002', 'إيما جونسون', '1992-07-22', 'Female', 
     '+1-555-2002', 'emma.j@email.com', '456 Oak Ave, Riverside, RS 23456', 
     'Aetna Dental', 'AD987654321', 'Diabetic - Type 2', 0.00, true, '40000001-0000-0000-0000-000000000001'),
    
    ('70000001-0000-0000-0000-000000000003', 'PT-2024-003', 'مايكل براون', '1978-11-08', 'Male', 
     '+1-555-2003', 'mbrown@email.com', '789 Pine Rd, Hillside, HS 34567', 
     'United Healthcare', 'UH456789123', 'High blood pressure, takes Lisinopril', 325.50, true, '40000001-0000-0000-0000-000000000001'),
    
    ('70000001-0000-0000-0000-000000000004', 'PT-2024-004', 'صوفيا ديفيس', '2010-04-18', 'Female', 
     '+1-555-2004', 'sophia.davis@email.com', '321 Elm St, Greenville, GV 45678', 
     'Cigna', 'CG789123456', 'Pediatric patient, anxiety about dental procedures', 75.00, true, '40000001-0000-0000-0000-000000000001'),
    
    ('70000001-0000-0000-0000-000000000005', 'PT-2024-005', 'ويليام غارسيا', '1965-09-30', 'Male', 
     '+1-555-2005', 'w.garcia@email.com', '654 Maple Dr, Lakeside, LS 56789', 
     'MetLife', 'ML321654987', 'Previous heart surgery, requires antibiotic prophylaxis', 0.00, true, '40000001-0000-0000-0000-000000000001'),
    
    -- General Practice Patients
    ('70000001-0000-0000-0000-000000000006', 'PT-2024-006', 'أوليفيا مارتينيز', '1988-02-14', 'Female', 
     '+1-555-2006', 'olivia.m@email.com', '987 Cedar Ln, Northtown, NT 67890', 
     'Blue Cross Blue Shield', 'BCBS147258369', 'Asthma, uses inhaler', 0.00, true, '40000001-0000-0000-0000-000000000005'),
    
    ('70000001-0000-0000-0000-000000000007', 'PT-2024-007', 'جيمس ويلسون', '1973-06-25', 'Male', 
     '+1-555-2007', 'j.wilson@email.com', '741 Birch Way, Southside, SS 78901', 
     'Humana', 'HM963852741', 'Thyroid condition, takes Levothyroxine', 45.00, true, '40000001-0000-0000-0000-000000000005'),
    
    ('70000001-0000-0000-0000-000000000008', 'PT-2024-008', 'إيزابيلا أندرسون', '1995-12-03', 'Female', 
     '+1-555-2008', 'isabella.a@email.com', '852 Spruce Ct, Eastview, EV 89012', 
     'Kaiser Permanente', 'KP159753486', 'No known allergies', 0.00, true, '40000001-0000-0000-0000-000000000005'),
    
    -- Orthodontics Patients
    ('70000001-0000-0000-0000-000000000009', 'PT-2024-009', 'لوكاس تومبسون', '2008-08-17', 'Male', 
     '+1-555-2009', 'lucas.t@email.com', '963 Ash Blvd, Westfield, WF 90123', 
     'Guardian', 'GD753159842', 'Teenage patient, compliant with treatment', 2750.00, true, '40000001-0000-0000-0000-000000000008'),
    
    ('70000001-0000-0000-0000-000000000010', 'PT-2024-010', 'ميا وايت', '2009-05-28', 'Female', 
     '+1-555-2010', 'mia.white@email.com', '159 Willow Dr, Central City, CC 01234', 
     'Anthem', 'AN486237951', 'Good oral hygiene, motivated patient', 1500.00, true, '40000001-0000-0000-0000-000000000008'),
    
    -- Cardiology Patients
    ('70000001-0000-0000-0000-000000000011', 'PT-2024-011', 'روبرت تايلور', '1952-10-12', 'Male', 
     '+1-555-2011', 'r.taylor@email.com', '753 Poplar Ave, Heartville, HV 11223', 
     'Medicare', 'MC789456123A', 'Coronary artery disease, s/p stent placement', 0.00, true, '40000001-0000-0000-0000-000000000010'),
    
    ('70000001-0000-0000-0000-000000000012', 'PT-2024-012', 'باتريشيا هاريس', '1960-01-20', 'Female', 
     '+1-555-2012', 'p.harris@email.com', '951 Sycamore St, Cardio Heights, CH 22334', 
     'United Healthcare', 'UH852963741', 'Atrial fibrillation, on Warfarin', 125.00, true, '40000001-0000-0000-0000-000000000010'),
    
    -- Additional diverse patients
    ('70000001-0000-0000-0000-000000000013', 'PT-2024-013', 'أحمد حسن', '1982-04-07', 'Male', 
     '+1-555-2013', 'a.hassan@email.com', '357 Palm Dr, Desert View, DV 33445', 
     'Aetna', 'AE147852369', 'Ramadan fasting considerations', 0.00, true, '40000001-0000-0000-0000-000000000001'),
    
    ('70000001-0000-0000-0000-000000000014', 'PT-2024-014', 'يوكي تاناكا', '1990-11-15', 'Female', 
     '+1-555-2014', 'y.tanaka@email.com', '258 Cherry Blossom Ln, Sakura Hills, SH 44556', 
     'Cigna', 'CG963741852', 'Pregnant - 2nd trimester', 50.00, true, '40000001-0000-0000-0000-000000000001'),
    
    ('70000001-0000-0000-0000-000000000015', 'PT-2024-015', 'كارلوس رودريغيز', '1975-08-23', 'Male', 
     '+1-555-2015', 'c.rodriguez@email.com', '456 Vista Rd, Mountain View, MV 55667', 
     'Blue Shield', 'BS741852963', 'Former smoker, quit 2020', 200.00, true, '40000001-0000-0000-0000-000000000002')
ON CONFLICT DO NOTHING;

-- =========================================================
-- APPOINTMENTS
-- =========================================================
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id, appointment_datetime, 
                         duration_minutes, status, notes, created_by)
VALUES 
    -- Past appointments (completed)
    ('80000001-0000-0000-0000-000000000001', '20000001-0000-0000-0000-000000000001', 
     '70000001-0000-0000-0000-000000000001', '40000001-0000-0000-0000-000000000001', 
     '2024-10-15 09:00:00-04', 30, 'COMPLETED', 'Regular checkup and cleaning', '40000001-0000-0000-0000-000000000003'),
    
    ('80000001-0000-0000-0000-000000000002', '20000001-0000-0000-0000-000000000001', 
     '70000001-0000-0000-0000-000000000002', '40000001-0000-0000-0000-000000000002', 
     '2024-10-20 14:30:00-04', 60, 'COMPLETED', 'Crown preparation', '40000001-0000-0000-0000-000000000003'),
    
    ('80000001-0000-0000-0000-000000000003', '20000001-0000-0000-0000-000000000003', 
     '70000001-0000-0000-0000-000000000003', '40000001-0000-0000-0000-000000000001', 
     '2024-11-05 10:00:00-05', 90, 'COMPLETED', 'Root canal treatment - molar', '40000001-0000-0000-0000-000000000003'),
    
    -- Current/Recent appointments
    ('80000001-0000-0000-0000-000000000004', '20000001-0000-0000-0000-000000000001', 
     '70000001-0000-0000-0000-000000000004', '40000001-0000-0000-0000-000000000001', 
     CURRENT_DATE - INTERVAL '2 days' + TIME '11:00:00', 45, 'COMPLETED', 'Pediatric cleaning and fluoride', '40000001-0000-0000-0000-000000000003'),
    
    ('80000001-0000-0000-0000-000000000005', '20000001-0000-0000-0000-000000000005', 
     '70000001-0000-0000-0000-000000000005', '40000001-0000-0000-0000-000000000002', 
     CURRENT_DATE - INTERVAL '1 day' + TIME '15:00:00', 30, 'COMPLETED', 'Extraction consultation', '40000001-0000-0000-0000-000000000004'),
    
    -- Today's appointments
    ('80000001-0000-0000-0000-000000000006', '20000001-0000-0000-0000-000000000008', 
     '70000001-0000-0000-0000-000000000006', '40000001-0000-0000-0000-000000000005', 
     CURRENT_DATE + TIME '09:30:00', 45, 'CONFIRMED', 'Annual physical examination', '40000001-0000-0000-0000-000000000007'),
    
    ('80000001-0000-0000-0000-000000000007', '20000001-0000-0000-0000-000000000001', 
     '70000001-0000-0000-0000-000000000013', '40000001-0000-0000-0000-000000000001', 
     CURRENT_DATE + TIME '14:00:00', 30, 'SCHEDULED', 'Filling - tooth #14', '40000001-0000-0000-0000-000000000003'),
    
    -- Future appointments
    ('80000001-0000-0000-0000-000000000008', '20000001-0000-0000-0000-000000000002', 
     '70000001-0000-0000-0000-000000000009', '40000001-0000-0000-0000-000000000008', 
     CURRENT_DATE + INTERVAL '3 days' + TIME '10:00:00', 30, 'SCHEDULED', 'Braces adjustment', '40000001-0000-0000-0000-000000000003'),
    
    ('80000001-0000-0000-0000-000000000009', '20000001-0000-0000-0000-000000000002', 
     '70000001-0000-0000-0000-000000000010', '40000001-0000-0000-0000-000000000009', 
     CURRENT_DATE + INTERVAL '5 days' + TIME '15:30:00', 30, 'SCHEDULED', 'Orthodontic consultation', '40000001-0000-0000-0000-000000000003'),
    
    ('80000001-0000-0000-0000-000000000010', '20000001-0000-0000-0000-000000000009', 
     '70000001-0000-0000-0000-000000000011', '40000001-0000-0000-0000-000000000010', 
     CURRENT_DATE + INTERVAL '7 days' + TIME '08:00:00', 120, 'SCHEDULED', 'Stress test', '40000001-0000-0000-0000-000000000007'),
    
    ('80000001-0000-0000-0000-000000000011', '20000001-0000-0000-0000-000000000009', 
     '70000001-0000-0000-0000-000000000012', '40000001-0000-0000-0000-000000000011', 
     CURRENT_DATE + INTERVAL '10 days' + TIME '11:00:00', 30, 'SCHEDULED', 'Follow-up ECG', '40000001-0000-0000-0000-000000000007'),
    
    ('80000001-0000-0000-0000-000000000012', '20000001-0000-0000-0000-000000000001', 
     '70000001-0000-0000-0000-000000000014', '40000001-0000-0000-0000-000000000001', 
     CURRENT_DATE + INTERVAL '14 days' + TIME '09:00:00', 45, 'SCHEDULED', 'Prenatal dental checkup', '40000001-0000-0000-0000-000000000003'),
    
    -- Cancelled appointment
    ('80000001-0000-0000-0000-000000000013', '20000001-0000-0000-0000-000000000001', 
     '70000001-0000-0000-0000-000000000015', '40000001-0000-0000-0000-000000000002', 
     CURRENT_DATE + INTERVAL '2 days' + TIME '16:00:00', 30, 'CANCELLED', 'Patient rescheduled', '40000001-0000-0000-0000-000000000004')
ON CONFLICT DO NOTHING;

-- =========================================================
-- VISITS (Encounter with multiple procedures)
-- =========================================================
INSERT INTO visits (id, patient_id, appointment_id, provider_id, date, time, notes)
VALUES 
    -- Completed visits from past appointments
    ('90000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000001', 
     '80000001-0000-0000-0000-000000000001', '40000001-0000-0000-0000-000000000001', 
     '2024-10-15', '09:00:00', 'Patient presented for routine checkup. No complaints.'),
    
    ('90000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000002', 
     '80000001-0000-0000-0000-000000000002', '40000001-0000-0000-0000-000000000002', 
     '2024-10-20', '14:30:00', 'Crown preparation completed successfully. Temporary crown placed.'),
    
    ('90000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000003', 
     '80000001-0000-0000-0000-000000000003', '40000001-0000-0000-0000-000000000001', 
     '2024-11-05', '10:00:00', 'Root canal therapy performed on tooth #30. Patient tolerated procedure well.'),
    
    ('90000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000004', 
     '80000001-0000-0000-0000-000000000004', '40000001-0000-0000-0000-000000000001', 
     CURRENT_DATE - INTERVAL '2 days', '11:00:00', 'Pediatric cleaning completed. Parent educated on brushing technique.'),
    
    ('90000001-0000-0000-0000-000000000005', '70000001-0000-0000-0000-000000000005', 
     '80000001-0000-0000-0000-000000000005', '40000001-0000-0000-0000-000000000002', 
     CURRENT_DATE - INTERVAL '1 day', '15:00:00', 'Consultation for extraction of tooth #32. Scheduled for next week.'),
    
    -- Today's visit (in progress)
    ('90000001-0000-0000-0000-000000000006', '70000001-0000-0000-0000-000000000006', 
     '80000001-0000-0000-0000-000000000006', '40000001-0000-0000-0000-000000000005', 
     CURRENT_DATE, '09:30:00', 'Annual physical in progress.')
ON CONFLICT DO NOTHING;

-- =========================================================
-- PROCEDURES (Multiple procedures per visit)
-- =========================================================
INSERT INTO procedures (id, visit_id, code, name, tooth_number, quantity, unit_fee, 
                       duration_minutes, performed_by_id, status, billable, notes, started_at, completed_at)
VALUES 
    -- Visit 1 procedures (John Smith - checkup and cleaning)
    ('a0000001-0000-0000-0000-000000000001', '90000001-0000-0000-0000-000000000001', 
     'D0120', 'Periodic Oral Evaluation', NULL, 1, 45.00, 15, 
     '40000001-0000-0000-0000-000000000001', 'COMPLETED', true, 'No issues found', 
     '2024-10-15 09:00:00', '2024-10-15 09:15:00'),
    
    ('a0000001-0000-0000-0000-000000000002', '90000001-0000-0000-0000-000000000001', 
     'D1110', 'Prophylaxis - Adult', NULL, 1, 95.00, 45, 
     '40000001-0000-0000-0000-000000000003', 'COMPLETED', true, 'Moderate calculus removed', 
     '2024-10-15 09:15:00', '2024-10-15 10:00:00'),
    
    -- Visit 2 procedures (Emma Johnson - crown prep)
    ('a0000001-0000-0000-0000-000000000003', '90000001-0000-0000-0000-000000000002', 
     'D2740', 'Crown - Porcelain/Ceramic', 14, 1, 1250.00, 60, 
     '40000001-0000-0000-0000-000000000002', 'SENT_TO_LAB', true, 'Shade A2 selected', 
     '2024-10-20 14:30:00', NULL),
    
    ('a0000001-0000-0000-0000-000000000004', '90000001-0000-0000-0000-000000000002', 
     'D2970', 'Temporary Crown', 14, 1, 150.00, 15, 
     '40000001-0000-0000-0000-000000000002', 'COMPLETED', true, 'Temporary crown placed', 
     '2024-10-20 15:30:00', '2024-10-20 15:45:00'),
    
    -- Visit 3 procedures (Michael Brown - root canal)
    ('a0000001-0000-0000-0000-000000000005', '90000001-0000-0000-0000-000000000003', 
     'D3330', 'Root Canal - Molar', 30, 1, 1150.00, 90, 
     '40000001-0000-0000-0000-000000000001', 'COMPLETED', true, 'Four canals treated', 
     '2024-11-05 10:00:00', '2024-11-05 11:30:00'),
    
    ('a0000001-0000-0000-0000-000000000006', '90000001-0000-0000-0000-000000000003', 
     'D2950', 'Core Buildup', 30, 1, 350.00, 30, 
     '40000001-0000-0000-0000-000000000001', 'COMPLETED', true, 'Core buildup completed', 
     '2024-11-05 11:30:00', '2024-11-05 12:00:00'),
    
    -- Visit 4 procedures (Sophia Davis - pediatric)
    ('a0000001-0000-0000-0000-000000000007', '90000001-0000-0000-0000-000000000004', 
     'D1120', 'Prophylaxis - Child', NULL, 1, 75.00, 30, 
     '40000001-0000-0000-0000-000000000003', 'COMPLETED', true, 'Good cooperation from patient', 
     CURRENT_DATE - INTERVAL '2 days' + TIME '11:00:00', CURRENT_DATE - INTERVAL '2 days' + TIME '11:30:00'),
    
    ('a0000001-0000-0000-0000-000000000008', '90000001-0000-0000-0000-000000000004', 
     'D1208', 'Topical Fluoride', NULL, 1, 35.00, 5, 
     '40000001-0000-0000-0000-000000000003', 'COMPLETED', true, 'Fluoride varnish applied', 
     CURRENT_DATE - INTERVAL '2 days' + TIME '11:30:00', CURRENT_DATE - INTERVAL '2 days' + TIME '11:35:00'),
    
    -- Visit 5 procedures (William Garcia - consultation)
    ('a0000001-0000-0000-0000-000000000009', '90000001-0000-0000-0000-000000000005', 
     'D0140', 'Limited Oral Evaluation', NULL, 1, 65.00, 15, 
     '40000001-0000-0000-0000-000000000002', 'COMPLETED', true, 'Evaluated for extraction', 
     CURRENT_DATE - INTERVAL '1 day' + TIME '15:00:00', CURRENT_DATE - INTERVAL '1 day' + TIME '15:15:00'),
    
    ('a0000001-0000-0000-0000-000000000010', '90000001-0000-0000-0000-000000000005', 
     'D0220', 'Periapical X-ray', 32, 1, 35.00, 5, 
     '40000001-0000-0000-0000-000000000004', 'COMPLETED', true, 'X-ray taken for tooth #32', 
     CURRENT_DATE - INTERVAL '1 day' + TIME '15:15:00', CURRENT_DATE - INTERVAL '1 day' + TIME '15:20:00'),
    
    -- Visit 6 procedures (Olivia Martinez - physical exam, in progress)
    ('a0000001-0000-0000-0000-000000000011', '90000001-0000-0000-0000-000000000006', 
     'GM001', 'Annual Physical Exam', NULL, 1, 200.00, 45, 
     '40000001-0000-0000-0000-000000000005', 'IN_PROGRESS', true, 'Vitals recorded, exam in progress', 
     CURRENT_DATE + TIME '09:30:00', NULL)
ON CONFLICT DO NOTHING;

-- =========================================================
-- PROCEDURE SURFACES (for dental procedures)
-- =========================================================
INSERT INTO procedure_surfaces (procedure_id, surface)
VALUES 
    ('a0000001-0000-0000-0000-000000000003', 'O'), -- Crown on occlusal surface
    ('a0000001-0000-0000-0000-000000000003', 'B'), -- Crown on buccal surface
    ('a0000001-0000-0000-0000-000000000003', 'L')  -- Crown on lingual surface
ON CONFLICT DO NOTHING;

-- =========================================================
-- LAB CASES
-- =========================================================
INSERT INTO lab_cases (id, procedure_id, lab_name, sent_date, due_date, received_date, 
                      tracking_number, status, technician_name, shade, material_type, notes)
VALUES 
    ('b0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000003', 
     'Premium Dental Lab', '2024-10-20', '2024-10-30', NULL, 
     'LAB-2024-1020-001', 'IN_PROGRESS', 'John Technical', 'A2', 'Zirconia', 
     'Rush case - patient traveling next month')
ON CONFLICT DO NOTHING;

-- =========================================================
-- PROCEDURE MATERIALS
-- =========================================================
INSERT INTO procedure_materials (id, procedure_id, material_name, material_code, quantity, 
                                unit, unit_cost, total_cost, consumed_at, notes)
VALUES 
    ('c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000003', 
     'Zirconia Crown Blank', 'ZCB-A2', 1, 'piece', 125.00, 125.00, 
     '2024-10-20 15:00:00', 'High translucency zirconia'),
    
    ('c0000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000004', 
     'Temporary Crown Material', 'TCM-01', 1, 'unit', 15.00, 15.00, 
     '2024-10-20 15:30:00', 'Bis-acryl composite'),
    
    ('c0000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000005', 
     'Gutta Percha Points', 'GP-M', 4, 'points', 2.50, 10.00, 
     '2024-11-05 11:00:00', 'Medium size points'),
    
    ('c0000001-0000-0000-0000-000000000004', 'a0000001-0000-0000-0000-000000000005', 
     'Root Canal Sealer', 'RCS-01', 0.5, 'ml', 50.00, 25.00, 
     '2024-11-05 11:15:00', 'Bioceramic sealer'),
    
    ('c0000001-0000-0000-0000-000000000005', 'a0000001-0000-0000-0000-000000000008', 
     'Fluoride Varnish', 'FV-5', 0.25, 'ml', 20.00, 5.00, 
     CURRENT_DATE - INTERVAL '2 days' + TIME '11:30:00', '5% sodium fluoride')
ON CONFLICT DO NOTHING;

-- =========================================================
-- INVOICES
-- =========================================================
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date, 
                     total_amount, status, created_by)
VALUES 
    -- John Smith - Completed visit invoice
    ('d0000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000001', 
     'INV-2024-10001', '2024-10-15', '2024-11-15', 
     140.00, 'UNPAID', '40000001-0000-0000-0000-000000000003'),
    
    -- Emma Johnson - Crown work
    ('d0000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000002', 
     'INV-2024-10002', '2024-10-20', '2024-11-20', 
     1300.00, 'PARTIALLY_PAID', '40000001-0000-0000-0000-000000000003'),
    
    -- Michael Brown - Root canal
    ('d0000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000003', 
     'INV-2024-11001', '2024-11-05', '2024-12-05', 
     1500.00, 'PAID', '40000001-0000-0000-0000-000000000003'),
    
    -- Sophia Davis - Pediatric cleaning
    ('d0000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000004', 
     'INV-2024-11002', CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '28 days', 
     100.00, 'PARTIALLY_PAID', '40000001-0000-0000-0000-000000000003'),
    
    -- William Garcia - Consultation
    ('d0000001-0000-0000-0000-000000000005', '70000001-0000-0000-0000-000000000005', 
     'INV-2024-11003', CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '29 days', 
     100.00, 'PAID', '40000001-0000-0000-0000-000000000004'),
    
    -- Lucas Thompson - Orthodontics (installment plan)
    ('d0000001-0000-0000-0000-000000000006', '70000001-0000-0000-0000-000000000009', 
     'INV-2024-09001', '2024-09-01', '2024-09-30', 
     5500.00, 'PARTIALLY_PAID', '40000001-0000-0000-0000-000000000003')
ON CONFLICT DO NOTHING;

-- =========================================================
-- INVOICE ITEMS
-- =========================================================
INSERT INTO invoice_items (id, invoice_id, visit_id, description, amount, item_type)
VALUES 
    -- Invoice 1 items (John Smith)
    ('e0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', 
     '90000001-0000-0000-0000-000000000001', 'Periodic Oral Evaluation', 45.00, 'PROCEDURE'),
    ('e0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000001', 
     NULL, 'Prophylaxis - Adult', 95.00, 'PROCEDURE'),
    
    -- Invoice 2 items (Emma Johnson)
    ('e0000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000002', 
     '90000001-0000-0000-0000-000000000002', 'Crown - Porcelain/Ceramic', 1250.00, 'PROCEDURE'),
    ('e0000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000002', 
     NULL, 'Temporary Crown', 150.00, 'PROCEDURE'),
    
    -- Invoice 3 items (Michael Brown)
    ('e0000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000003', 
     '90000001-0000-0000-0000-000000000003', 'Root Canal - Molar', 1150.00, 'PROCEDURE'),
    ('e0000001-0000-0000-0000-000000000006', 'd0000001-0000-0000-0000-000000000003', 
     NULL, 'Core Buildup', 350.00, 'PROCEDURE'),
    
    -- Invoice 4 items (Sophia Davis)
    ('e0000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000004', 
     '90000001-0000-0000-0000-000000000004', 'Prophylaxis - Child', 75.00, 'PROCEDURE'),
    ('e0000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000004', 
     NULL, 'Topical Fluoride', 35.00, 'PROCEDURE'),
    
    -- Invoice 5 items (William Garcia)
    ('e0000001-0000-0000-0000-000000000009', 'd0000001-0000-0000-0000-000000000005', 
     '90000001-0000-0000-0000-000000000005', 'Limited Oral Evaluation', 65.00, 'PROCEDURE'),
    ('e0000001-0000-0000-0000-000000000010', 'd0000001-0000-0000-0000-000000000005', 
     NULL, 'Periapical X-ray', 35.00, 'PROCEDURE'),
    
    -- Invoice 6 items (Lucas Thompson)
    ('e0000001-0000-0000-0000-000000000011', 'd0000001-0000-0000-0000-000000000006', 
     NULL, 'Comprehensive Orthodontic Treatment - Full Payment', 5500.00, 'PROCEDURE')
ON CONFLICT DO NOTHING;

-- =========================================================
-- PAYMENTS
-- =========================================================
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount, 
                     payment_method, type, description, created_by)
VALUES 
    -- Emma Johnson partial payment
    ('f0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000002', 
     '70000001-0000-0000-0000-000000000002', '2024-10-20', 650.00, 
     'CREDIT_CARD', 'PAYMENT', 'Partial payment for crown', '40000001-0000-0000-0000-000000000003'),
    
    -- Michael Brown full payment
    ('f0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000003', 
     '70000001-0000-0000-0000-000000000003', '2024-11-05', 1500.00, 
     'INSURANCE', 'PAYMENT', 'Insurance payment for root canal', '40000001-0000-0000-0000-000000000003'),
    
    -- Sophia Davis partial payment
    ('f0000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000004', 
     '70000001-0000-0000-0000-000000000004', CURRENT_DATE - INTERVAL '2 days', 25.00, 
     'CASH', 'PAYMENT', 'Copay for pediatric visit', '40000001-0000-0000-0000-000000000003'),
    
    -- William Garcia full payment
    ('f0000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000005', 
     '70000001-0000-0000-0000-000000000005', CURRENT_DATE - INTERVAL '1 day', 100.00, 
     'CHECK', 'PAYMENT', 'Payment for consultation', '40000001-0000-0000-0000-000000000004'),
    
    -- Lucas Thompson orthodontics down payment
    ('f0000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000006', 
     '70000001-0000-0000-0000-000000000009', '2024-09-01', 1500.00, 
     'CREDIT_CARD', 'PAYMENT', 'Down payment for orthodontic treatment', '40000001-0000-0000-0000-000000000003'),
    
    -- Lucas Thompson monthly payments
    ('f0000001-0000-0000-0000-000000000006', 'd0000001-0000-0000-0000-000000000006', 
     '70000001-0000-0000-0000-000000000009', '2024-10-01', 250.00, 
     'CREDIT_CARD', 'PAYMENT', 'Monthly orthodontic payment', '40000001-0000-0000-0000-000000000003'),
    
    ('f0000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000006', 
     '70000001-0000-0000-0000-000000000009', '2024-11-01', 250.00, 
     'CREDIT_CARD', 'PAYMENT', 'Monthly orthodontic payment', '40000001-0000-0000-0000-000000000003'),
    
    ('f0000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000006', 
     '70000001-0000-0000-0000-000000000009', '2024-12-01', 250.00, 
     'CREDIT_CARD', 'PAYMENT', 'Monthly orthodontic payment', '40000001-0000-0000-0000-000000000003'),
    
    ('f0000001-0000-0000-0000-000000000009', 'd0000001-0000-0000-0000-000000000006', 
     '70000001-0000-0000-0000-000000000009', '2025-01-01', 250.00, 
     'CREDIT_CARD', 'PAYMENT', 'Monthly orthodontic payment', '40000001-0000-0000-0000-000000000003'),
    
    -- Advance payment (credit on account)
    ('f0000001-0000-0000-0000-000000000010', NULL, 
     '70000001-0000-0000-0000-000000000010', '2024-09-15', 1500.00, 
     'CREDIT_CARD', 'CREDIT', 'Advance payment for orthodontic treatment', '40000001-0000-0000-0000-000000000003')
ON CONFLICT DO NOTHING;

-- =========================================================
-- PAYMENT ALLOCATIONS
-- =========================================================
/* INSERT INTO payment_allocations (id, payment_id, invoice_id, allocated_amount)
VALUES 
    ('01000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 
     'd0000001-0000-0000-0000-000000000002', 650.00),
    ('01000001-0000-0000-0000-000000000002', 'f0000001-0000-0000-0000-000000000002', 
     'd0000001-0000-0000-0000-000000000003', 1500.00),
    ('01000001-0000-0000-0000-000000000003', 'f0000001-0000-0000-0000-000000000003', 
     'd0000001-0000-0000-0000-000000000004', 25.00),
    ('01000001-0000-0000-0000-000000000004', 'f0000001-0000-0000-0000-000000000004', 
     'd0000001-0000-0000-0000-000000000005', 100.00),
    ('01000001-0000-0000-0000-000000000005', 'f0000001-0000-0000-0000-000000000005', 
     'd0000001-0000-0000-0000-000000000006', 1500.00),
    ('01000001-0000-0000-0000-000000000006', 'f0000001-0000-0000-0000-000000000006', 
     'd0000001-0000-0000-0000-000000000006', 250.00),
    ('01000001-0000-0000-0000-000000000007', 'f0000001-0000-0000-0000-000000000007', 
     'd0000001-0000-0000-0000-000000000006', 250.00),
    ('01000001-0000-0000-0000-000000000008', 'f0000001-0000-0000-0000-000000000008', 
     'd0000001-0000-0000-0000-000000000006', 250.00),
    ('01000001-0000-0000-0000-000000000009', 'f0000001-0000-0000-0000-000000000009', 
     'd0000001-0000-0000-0000-000000000006', 250.00)
ON CONFLICT DO NOTHING; */

-- =========================================================
-- PAYMENT PLANS
-- =========================================================
/* INSERT INTO payment_plans (id, patient_id, invoice_id, plan_name, total_amount, 
                          installment_count, installment_amount, start_date, end_date, 
                          frequency_days, status, notes, created_by)
VALUES 
    -- Lucas Thompson orthodontic payment plan
    ('02000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000009', 
     'd0000001-0000-0000-0000-000000000006', 'Orthodontic Treatment Plan - Lucas Thompson', 
     4000.00, 16, 250.00, '2024-10-01', '2026-01-01', 30, 'ACTIVE', 
     'Monthly payments for comprehensive orthodontic treatment after down payment', '40000001-0000-0000-0000-000000000008'),
    
    -- John Smith payment plan for outstanding balance
    ('02000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000001', 
     'd0000001-0000-0000-0000-000000000001', 'Payment Plan - John Smith', 
     140.00, 4, 35.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 months', 30, 'ACTIVE', 
     'Monthly payment plan for cleaning and evaluation', '40000001-0000-0000-0000-000000000003')
ON CONFLICT DO NOTHING; */

-- =========================================================
-- PAYMENT PLAN INSTALLMENTS
-- =========================================================
/* INSERT INTO payment_plan_installments (id, payment_plan_id, installment_number, 
                                      due_date, amount, paid_amount, paid_date, status, notes)
VALUES 
    -- Lucas Thompson installments (first 4 are paid)
    ('03000001-0000-0000-0000-000000000001', '02000001-0000-0000-0000-000000000001', 
     1, '2024-10-01', 250.00, 250.00, '2024-10-01', 'PAID', 'On-time payment'),
    ('03000001-0000-0000-0000-000000000002', '02000001-0000-0000-0000-000000000001', 
     2, '2024-11-01', 250.00, 250.00, '2024-11-01', 'PAID', 'On-time payment'),
    ('03000001-0000-0000-0000-000000000003', '02000001-0000-0000-0000-000000000001', 
     3, '2024-12-01', 250.00, 250.00, '2024-12-01', 'PAID', 'On-time payment'),
    ('03000001-0000-0000-0000-000000000004', '02000001-0000-0000-0000-000000000001', 
     4, '2025-01-01', 250.00, 250.00, '2025-01-01', 'PAID', 'On-time payment'),
    ('03000001-0000-0000-0000-000000000005', '02000001-0000-0000-0000-000000000001', 
     5, '2025-02-01', 250.00, 0.00, NULL, 'PENDING', NULL),
    ('03000001-0000-0000-0000-000000000006', '02000001-0000-0000-0000-000000000001', 
     6, '2025-03-01', 250.00, 0.00, NULL, 'PENDING', NULL),
    
    -- John Smith installments (all pending)
    ('03000001-0000-0000-0000-000000000007', '02000001-0000-0000-0000-000000000002', 
     1, CURRENT_DATE, 35.00, 0.00, NULL, 'PENDING', NULL),
    ('03000001-0000-0000-0000-000000000008', '02000001-0000-0000-0000-000000000002', 
     2, CURRENT_DATE + INTERVAL '1 month', 35.00, 0.00, NULL, 'PENDING', NULL),
    ('03000001-0000-0000-0000-000000000009', '02000001-0000-0000-0000-000000000002', 
     3, CURRENT_DATE + INTERVAL '2 months', 35.00, 0.00, NULL, 'PENDING', NULL),
    ('03000001-0000-0000-0000-000000000010', '02000001-0000-0000-0000-000000000002', 
     4, CURRENT_DATE + INTERVAL '3 months', 35.00, 0.00, NULL, 'PENDING', NULL)
ON CONFLICT DO NOTHING; */

-- =========================================================
-- DENTAL CHARTS
-- =========================================================
/* INSERT INTO dental_charts (id, patient_id, chart_data)
VALUES 
    ('04000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000001', 
     '{"meta": {"version": "1.0", "lastUpdated": "2024-10-15", "updatedBy": "Dr. Sarah Johnson"}, 
       "teeth": {
         "11": {"status": "healthy", "notes": "No issues"},
         "12": {"status": "healthy", "notes": "No issues"},
         "13": {"status": "healthy", "notes": "No issues"},
         "14": {"status": "filled", "notes": "Composite filling placed 2022"},
         "15": {"status": "healthy", "notes": "No issues"},
         "21": {"status": "healthy", "notes": "No issues"},
         "22": {"status": "healthy", "notes": "No issues"},
         "23": {"status": "healthy", "notes": "No issues"},
         "24": {"status": "crown", "notes": "Porcelain crown 2023"},
         "25": {"status": "healthy", "notes": "No issues"},
         "31": {"status": "healthy", "notes": "No issues"},
         "32": {"status": "healthy", "notes": "No issues"},
         "33": {"status": "healthy", "notes": "No issues"},
         "34": {"status": "filled", "notes": "Amalgam filling 2019"},
         "35": {"status": "healthy", "notes": "No issues"},
         "36": {"status": "root_canal", "notes": "RCT completed 2021"},
         "41": {"status": "healthy", "notes": "No issues"},
         "42": {"status": "healthy", "notes": "No issues"},
         "43": {"status": "healthy", "notes": "No issues"},
         "44": {"status": "healthy", "notes": "No issues"},
         "45": {"status": "healthy", "notes": "No issues"},
         "46": {"status": "filled", "notes": "Large composite 2020"},
         "47": {"status": "healthy", "notes": "No issues"},
         "48": {"status": "extracted", "notes": "Wisdom tooth removed 2018"}
       }}'::json),
       
    ('04000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000002', 
     '{"meta": {"version": "1.0", "lastUpdated": "2024-10-20", "updatedBy": "Dr. Michael Chen"}, 
       "teeth": {
         "14": {"status": "crown_prep", "notes": "Crown preparation 2024-10-20"},
         "36": {"status": "filled", "notes": "Composite filling 2023"},
         "46": {"status": "filled", "notes": "Composite filling 2022"}
       }}'::json),
       
    ('04000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000003', 
     '{"meta": {"version": "1.0", "lastUpdated": "2024-11-05", "updatedBy": "Dr. Sarah Johnson"}, 
       "teeth": {
         "30": {"status": "root_canal", "notes": "RCT completed 2024-11-05"},
         "18": {"status": "extracted", "notes": "Wisdom tooth 2020"},
         "28": {"status": "extracted", "notes": "Wisdom tooth 2020"},
         "38": {"status": "extracted", "notes": "Wisdom tooth 2021"},
         "48": {"status": "extracted", "notes": "Wisdom tooth 2021"}
       }}'::json),
       
    ('04000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000004', 
     '{"meta": {"version": "1.0", "lastUpdated": "' || (CURRENT_DATE - INTERVAL '2 days')::text || '", "updatedBy": "Dr. Sarah Johnson"}, 
       "teeth": {
         "16": {"status": "sealant", "notes": "Sealant applied"},
         "26": {"status": "sealant", "notes": "Sealant applied"},
         "36": {"status": "sealant", "notes": "Sealant applied"},
         "46": {"status": "sealant", "notes": "Sealant applied"}
       }}'::json)
ON CONFLICT DO NOTHING; */

-- =========================================================
-- NOTES
-- =========================================================
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES 
    ('05000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000001', 
     'Patient expressed concern about sensitivity in upper left quadrant. Recommended sensitive toothpaste and follow-up in 2 weeks if symptoms persist.', 
     '40000001-0000-0000-0000-000000000001', '2024-10-15 10:30:00'),
     
    ('05000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000002', 
     'Crown preparation went smoothly. Patient tolerated procedure well. Temporary crown fitted properly. Final crown delivery scheduled for 2 weeks.', 
     '40000001-0000-0000-0000-000000000002', '2024-10-20 16:00:00'),
     
    ('05000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000003', 
     'Root canal treatment successful. Four canals located and treated. Patient experienced minimal discomfort. Prescribed antibiotics and pain medication.', 
     '40000001-0000-0000-0000-000000000001', '2024-11-05 12:15:00'),
     
    ('05000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000009', 
     'Orthodontic progress satisfactory. Teeth moving as planned. Patient compliance with elastics is excellent. Next adjustment in 4 weeks.', 
     '40000001-0000-0000-0000-000000000008', CURRENT_DATE - INTERVAL '7 days' + TIME '14:00:00'),
     
    ('05000001-0000-0000-0000-000000000005', '70000001-0000-0000-0000-000000000011', 
     'Cardiac evaluation shows improvement. ECG normal sinus rhythm. Continue current medication regimen. Follow-up in 3 months.', 
     '40000001-0000-0000-0000-000000000010', CURRENT_DATE - INTERVAL '14 days' + TIME '11:00:00')
ON CONFLICT DO NOTHING;

-- =========================================================
-- LAB REQUESTS (Legacy table, keeping for compatibility)
-- =========================================================
INSERT INTO lab_requests (id, patient_id, order_number, item_description, tooth_number, 
                         date_sent, date_due, status, lab_name)
VALUES 
    ('06000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000002', 
     'LAB-2024-001', 'Porcelain crown for tooth #14, shade A2', 14, 
     '2024-10-20', '2024-10-30', 'IN_PROGRESS', 'Premium Dental Lab'),
     
    ('06000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000010', 
     'LAB-2024-002', 'Clear aligners - Set 1 of 12', NULL, 
     '2024-09-01', '2024-09-15', 'RECEIVED', 'Align Tech Laboratory')
ON CONFLICT DO NOTHING;

-- =========================================================
-- LEDGER ENTRIES
-- =========================================================
-- Note: These would typically be created by triggers, but including for completeness
/* INSERT INTO ledger_entries (id, patient_id, invoice_id, payment_id, entry_type, amount, occurred_at, description)
VALUES 
    -- John Smith ledger
    ('07000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000001', 
     'd0000001-0000-0000-0000-000000000001', NULL, 'CHARGE', 140.00, 
     '2024-10-15 16:00:00', 'Invoice INV-2024-10001 - Checkup and cleaning'),
     
    -- Emma Johnson ledger
    ('07000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000002', 
     'd0000001-0000-0000-0000-000000000002', NULL, 'CHARGE', 1300.00, 
     '2024-10-20 17:00:00', 'Invoice INV-2024-10002 - Crown work'),
    ('07000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000002', 
     NULL, 'f0000001-0000-0000-0000-000000000001', 'PAYMENT', -650.00, 
     '2024-10-20 17:30:00', 'Partial payment - Credit card'),
     
    -- Michael Brown ledger
    ('07000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000003', 
     'd0000001-0000-0000-0000-000000000003', NULL, 'CHARGE', 1500.00, 
     '2024-11-05 13:00:00', 'Invoice INV-2024-11001 - Root canal'),
    ('07000001-0000-0000-0000-000000000005', '70000001-0000-0000-0000-000000000003', 
     NULL, 'f0000001-0000-0000-0000-000000000002', 'PAYMENT', -1500.00, 
     '2024-11-05 13:30:00', 'Full payment - Insurance'),
     
    -- Mia White advance payment
    ('07000001-0000-0000-0000-000000000006', '70000001-0000-0000-0000-000000000010', 
     NULL, 'f0000001-0000-0000-0000-000000000010', 'CREDIT', -1500.00, 
     '2024-09-15 10:00:00', 'Advance payment for orthodontic treatment')
ON CONFLICT DO NOTHING; */

-- =========================================================
-- DOCUMENTS (Sample document records)
-- =========================================================
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name, file_path, 
                      file_size_bytes, mime_type, type)
VALUES 
    ('08000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000001', 
     '40000001-0000-0000-0000-000000000003', 'john_smith_xray_20241015.jpg', 
     '/documents/patients/70000001-0000-0000-0000-000000000001/xrays/john_smith_xray_20241015.jpg', 
     2458624, 'image/jpeg', 'X_RAY'),
     
    ('08000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000002', 
     '40000001-0000-0000-0000-000000000003', 'emma_johnson_consent_20241020.pdf', 
     '/documents/patients/70000001-0000-0000-0000-000000000002/consents/emma_johnson_consent_20241020.pdf', 
     156789, 'application/pdf', 'TREATMENT_APPROVAL'),
     
    ('08000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000003', 
     '40000001-0000-0000-0000-000000000003', 'michael_brown_insurance_card.pdf', 
     '/documents/patients/70000001-0000-0000-0000-000000000003/insurance/michael_brown_insurance_card.pdf', 
     98456, 'application/pdf', 'MEDICAL_REPORT'),
     
    ('08000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000009', 
     '40000001-0000-0000-0000-000000000003', 'lucas_thompson_ortho_plan.pdf', 
     '/documents/patients/70000001-0000-0000-0000-000000000009/treatment_plans/lucas_thompson_ortho_plan.pdf', 
     456789, 'application/pdf', 'MEDICAL_REPORT'),
     
    ('08000001-0000-0000-0000-000000000005', '70000001-0000-0000-0000-000000000011', 
     '40000001-0000-0000-0000-000000000007', 'robert_taylor_ecg_report.pdf', 
     '/documents/patients/70000001-0000-0000-0000-000000000011/reports/robert_taylor_ecg_report.pdf', 
     234567, 'application/pdf', 'MEDICAL_REPORT')
ON CONFLICT DO NOTHING;

-- =========================================================
-- END OF DEMO DATA SCRIPT
-- =========================================================

-- Display summary
DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Demo data loaded successfully!';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Summary:';
    RAISE NOTICE '- 4 Tenants (multi-tenant setup)';
    RAISE NOTICE '- 12 Staff members';
    RAISE NOTICE '- 15 Patients';
    RAISE NOTICE '- 13 Appointments';
    RAISE NOTICE '- 6 Visits with procedures';
    RAISE NOTICE '- 6 Invoices with payment history';
    RAISE NOTICE '- 2 Payment plans';
    RAISE NOTICE '- 4 Dental charts';
    RAISE NOTICE '- Sample lab cases and materials';
    RAISE NOTICE '========================================';
END $$;
