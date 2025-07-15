-- Dental Clinic Demo Data (Arabic) - Expanded Version
-- Generated on: 2025-07-12 17:08:22
-- This file contains additional data to supplement the existing demo data

BEGIN;

-- Generated on: 2025-07-12 16:53:18
-- Note: This version avoids updating patient_teeth to prevent trigger errors


-- Note: This demo data script avoids updating patient_teeth records to prevent trigger errors
-- The track_tooth_history() trigger expects a created_by field that doesn't exist in patient_teeth table

UPDATE clinic_info
SET name = 'عيادة الأسنان المتقدمة',
    address = 'شارع الملك فهد، حي الروضة، جدة',
    phone_number = '+966 12 123 4567',
    email = 'info@advanced-dental.sa',
    timezone = 'Asia/Riyadh'
WHERE id = TRUE;

INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'د. علي الإبراهيم', 'DOCTOR', 'doctor1@clinic.sa', '+966 571183769', TRUE);

INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('844e0809-f1e1-4f79-9c7c-d09754b25b6b', (SELECT id FROM specialties WHERE name = 'General Dentistry'));

INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('d9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 'د. حسن المصري', 'DOCTOR', 'doctor2@clinic.sa', '+966 534077971', TRUE);

INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('d9dcc98a-517b-4518-8d50-acdbf4a0b5d2', (SELECT id FROM specialties WHERE name = 'General Dentistry'));

INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'د. عمر الحسن', 'DOCTOR', 'doctor3@clinic.sa', '+966 560019543', TRUE);

INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('b5ee786d-b86e-4ca3-a705-4417f1c65b03', (SELECT id FROM specialties WHERE name = 'General Dentistry'));

INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('e9947118-bf36-494d-a826-eb6d6a0571ca', 'أمل الخالد', 'NURSE', 'nurse1@clinic.sa', '+966 575896163', TRUE);

INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'رانيا الأحمد', 'NURSE', 'nurse2@clinic.sa', '+966 595905431', TRUE);

INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('d7f81d26-ce95-4104-aafb-5a0e355621a4', 'أمل السعيد', 'RECEPTIONIST', 'reception@clinic.sa', '+966 548333325', TRUE);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'CLEAN', 'تنظيف الأسنان', 'تنظيف روتيني للأسنان', 150.0, 30);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'FILL', 'حشو الأسنان', 'حشو تجويف الأسنان', 300.0, 45);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'EXTRACT', 'خلع الأسنان', 'إزالة الأسنان', 400.0, 60);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ROOT', 'علاج العصب', 'علاج قناة الجذر', 1200.0, 90);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'CROWN', 'تاج الأسنان', 'تركيب تاج للسن', 1500.0, 60);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'BRIDGE', 'جسر الأسنان', 'تركيب جسر أسنان', 3000.0, 120);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'IMPLANT', 'زراعة الأسنان', 'زراعة سن صناعي', 4500.0, 120);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'WHITENING', 'تبييض الأسنان', 'تبييض الأسنان التجميلي', 800.0, 60);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'XRAY', 'X_RAY', 'تصوير الأسنان بالأشعة', 100.0, 15);

INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'CONSULT', 'استشارة', 'فحص واستشارة', 100.0, 30);

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2', 'P2024001', 'يوسف الخالد', '1973-10-07', 'male',
        '+966 550694526', 'patient1@email.com', 'شارع فلسطين، حي البوادي', 'بوبا العربية',
        'INS800023',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('8b7a519e-6993-49a7-b712-6cfbcb387fae', 'P2024002', 'سعيد الخالد', '1989-12-07', 'male',
        '+966 551472081', 'patient2@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('2d467671-ea75-4f4d-af6a-76945d8445d0', 'P2024003', 'سعيد الإبراهيم', '1997-05-27', 'male',
        '+966 585012411', 'patient3@email.com', 'شارع الملك فهد، حي الروضة', 'بوبا العربية',
        'INS294392',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('8a74df49-6cec-4cb8-b096-583f82568f50', 'P2024004', 'سعيد الشامي', '1967-12-16', 'male',
        '+966 516047971', 'patient4@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('50f029c1-5c9b-4d7c-af2a-ee3e9b47505f', 'P2024005', 'علي المحمد', '1967-06-24', 'male',
        '+966 586924646', 'patient5@email.com', 'شارع التحلية، حي الزهراء', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('a92d8a07-88d8-47a5-a47c-f3828b552b81', 'P2024006', 'علي الشامي', '1994-08-14', 'male',
        '+966 582284947', 'patient6@email.com', 'شارع الأمير سلطان، حي السلامة', 'التعاونية للتأمين',
        'INS809607',
        'ضغط الدم',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('0808331a-cc14-45bd-900d-735efc50ce82', 'P2024007', 'أمل العلي', '1970-02-26', 'female',
        '+966 527592675', 'patient7@email.com', 'شارع الأمير سلطان، حي السلامة', NULL,
        NULL,
        'أمراض القلب',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('abffbe05-3216-44e9-a843-ea3a342f2c29', 'P2024008', 'خالد العلي', '1989-06-28', 'male',
        '+966 551646593', 'patient8@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'بوبا العربية',
        'INS520911',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('3027c252-9e51-454a-b713-055b52fc9c5e', 'P2024009', 'حسن السعيد', '1999-10-14', 'male',
        '+966 598316093', 'patient9@email.com', 'شارع فلسطين، حي البوادي', 'الدرع العربي',
        'INS907831',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('881eab7c-30db-4e17-aa99-03e93dde1ff7', 'P2024010', 'أمل الخالد', '1960-03-20', 'female',
        '+966 547684894', 'patient10@email.com', 'شارع الملك فهد، حي الروضة', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('55e06b34-32dc-4e01-8f7c-2d87e3db166a', 'P2024011', 'سارة الحسن', '1988-03-02', 'female',
        '+966 519152364', 'patient11@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'بوبا العربية',
        'INS608458',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('419f3922-59c9-488f-b175-3753c2e6797d', 'P2024012', 'هدى العمر', '1973-09-19', 'female',
        '+966 533895576', 'patient12@email.com', 'شارع التحلية، حي الزهراء', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('ea3193a8-394b-4d2a-8168-079caebf3db3', 'P2024013', 'زينب السعيد', '2005-12-06', 'female',
        '+966 531035739', 'patient13@email.com', 'شارع الأمير سلطان، حي السلامة', NULL,
        NULL,
        'حساسية البنسلين',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('29ed1c3a-1b46-4f01-af0a-16f97bdcb0d7', 'P2024014', 'فاطمة الإبراهيم', '1984-09-13', 'female',
        '+966 561888891', 'patient14@email.com', 'شارع فلسطين، حي البوادي', 'أليانز السعودي',
        'INS874765',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('73ffc159-4e56-4891-a951-a7d8e6b4120b', 'P2024015', 'عمر العمر', '2010-11-24', 'male',
        '+966 537958824', 'patient15@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'ميدغلف',
        'INS284522',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2', 'P2024016', 'ليلى العمر', '1963-03-17', 'female',
        '+966 563773911', 'patient16@email.com', 'شارع التحلية، حي الزهراء', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('29e9ce56-21f1-4c10-93c9-c4f7b4493c1b', 'P2024017', 'إبراهيم المصري', '1963-03-10', 'male',
        '+966 545747869', 'patient17@email.com', 'شارع فلسطين، حي البوادي', 'أليانز السعودي',
        'INS133725',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff', 'P2024018', 'أحمد السعيد', '1958-06-05', 'male',
        '+966 590470392', 'patient18@email.com', 'شارع الأمير سلطان، حي السلامة', 'بوبا العربية',
        'INS740500',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('f3350684-9bed-4605-82b7-9b905e723444', 'P2024019', 'رانيا السعيد', '1995-10-05', 'female',
        '+966 588281894', 'patient19@email.com', 'شارع الأمير سلطان، حي السلامة', NULL,
        NULL,
        'أمراض القلب',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('ed000d0a-4263-4295-9a1e-50df1f5bdc75', 'P2024020', 'مريم الأحمد', '1985-12-12', 'female',
        '+966 594712704', 'patient20@email.com', 'شارع الأمير سلطان، حي السلامة', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ead9d6e7-2598-42a9-8867-7ae716ae339c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-06 16:53:18+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1090132f-86d5-459f-869c-f8ff7ef99ee8', 'ead9d6e7-2598-42a9-8867-7ae716ae339c', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        267.48, 'علاج حشو الأسنان', '2025-05-06',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('14826bb3-2378-4c3f-a8b6-d704b45eb61a', 'ead9d6e7-2598-42a9-8867-7ae716ae339c', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 43, 'COMPLETED',
        95.10, 'علاج X_RAY - السن رقم 43', '2025-05-06',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('89467043-c5fd-4d5b-9181-e3818a473003', 'ead9d6e7-2598-42a9-8867-7ae716ae339c', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        3406.59, 'علاج جسر الأسنان', '2025-05-06',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('cc955bfc-6937-4ab1-9133-8576d5f63ab8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-07 16:53:18+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dceed4e2-98a3-43ca-a3d3-a27f1d635c2f', 'cc955bfc-6937-4ab1-9133-8576d5f63ab8', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 13, 'COMPLETED',
        3874.40, 'علاج زراعة الأسنان - السن رقم 13', '2025-04-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('90e46aba-5d72-4d50-865e-8ef95b9fa246', 'cc955bfc-6937-4ab1-9133-8576d5f63ab8', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        452.17, 'علاج خلع الأسنان', '2025-04-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('710bd909-1abe-4470-b08b-52a2e976912d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-15 16:53:18+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dead2fcc-b45e-464d-a6b2-26986f56e787', '710bd909-1abe-4470-b08b-52a2e976912d', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 26, 'COMPLETED',
        5113.13, 'علاج زراعة الأسنان - السن رقم 26', '2025-02-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f5f925c8-e67b-43bf-8679-f1418ca41759', '710bd909-1abe-4470-b08b-52a2e976912d', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        373.40, 'علاج خلع الأسنان', '2025-02-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9d1231b9-26a6-430f-893b-9196ce3696f0', '710bd909-1abe-4470-b08b-52a2e976912d', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        841.82, 'علاج تبييض الأسنان', '2025-02-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b2ca2c1c-46e6-4e2b-b916-4829b3a5275c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-29 16:53:18+03',
        60, 'NO_SHOW', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2e65fd32-de4a-4526-b705-160532556e18', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-27 16:53:18+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b55047d1-3f46-4f32-b9ea-6e8088d305af', '2e65fd32-de4a-4526-b705-160532556e18', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1732.69, 'علاج تاج الأسنان', '2025-05-27',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('61d82470-e596-4704-acd9-e9e438ac03ba', '2e65fd32-de4a-4526-b705-160532556e18', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 33, 'COMPLETED',
        987.80, 'علاج علاج العصب - السن رقم 33', '2025-05-27',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('19a4ead7-d96a-4378-baae-8eb4e93d0dfb', '2e65fd32-de4a-4526-b705-160532556e18', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 39, 'COMPLETED',
        4910.89, 'علاج زراعة الأسنان - السن رقم 39', '2025-05-27',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e7ba18ae-0466-4a7f-aafc-5e52183660cf', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8b7a519e-6993-49a7-b712-6cfbcb387fae', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-23 16:53:18+03',
        90, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c0ae0fb1-b103-4e91-a115-cc3a53ab1170', 'e7ba18ae-0466-4a7f-aafc-5e52183660cf', '8b7a519e-6993-49a7-b712-6cfbcb387fae',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        2734.74, 'علاج جسر الأسنان', '2025-05-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('37ccd9c3-1dab-4558-a13a-c5db4765a24e', 'e7ba18ae-0466-4a7f-aafc-5e52183660cf', '8b7a519e-6993-49a7-b712-6cfbcb387fae',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        118.43, 'علاج X_RAY', '2025-05-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3680ce4f-3eec-4da3-9a09-8f1cdf4f1817', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8b7a519e-6993-49a7-b712-6cfbcb387fae', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-18 16:53:18+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('69ab6904-c992-457b-98e8-13e19acff661', '3680ce4f-3eec-4da3-9a09-8f1cdf4f1817', '8b7a519e-6993-49a7-b712-6cfbcb387fae',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1332.84, 'علاج تاج الأسنان', '2025-03-18',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('145f6624-a2b1-4a36-836d-d8c01f0c68ea', '3680ce4f-3eec-4da3-9a09-8f1cdf4f1817', '8b7a519e-6993-49a7-b712-6cfbcb387fae',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 39, 'COMPLETED',
        1799.70, 'علاج تاج الأسنان - السن رقم 39', '2025-03-18',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d6b1723a-b589-4618-8a1f-f905134186ed', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '2d467671-ea75-4f4d-af6a-76945d8445d0', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-23 16:53:18+03',
        30, 'NO_SHOW', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bed22cc1-228b-4006-a20f-7d8e932133d5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '2d467671-ea75-4f4d-af6a-76945d8445d0', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-07-04 16:53:18+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('26cabb43-7060-49eb-a31d-5530292b8e12', 'bed22cc1-228b-4006-a20f-7d8e932133d5', '2d467671-ea75-4f4d-af6a-76945d8445d0',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 41, 'COMPLETED',
        88.79, 'علاج X_RAY - السن رقم 41', '2025-07-04',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('86c2dd00-36b6-4e9e-95ae-6085a21192d5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a74df49-6cec-4cb8-b096-583f82568f50', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-10 16:53:18+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e7d74f22-f396-4c6f-8184-e53c0266a2b6', '86c2dd00-36b6-4e9e-95ae-6085a21192d5', '8a74df49-6cec-4cb8-b096-583f82568f50',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 13, 'COMPLETED',
        91.22, 'علاج X_RAY - السن رقم 13', '2025-03-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1786e93c-d1cc-4303-a434-43b8b11175d5', '86c2dd00-36b6-4e9e-95ae-6085a21192d5', '8a74df49-6cec-4cb8-b096-583f82568f50',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        1571.33, 'علاج تاج الأسنان', '2025-03-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6cb28436-dc58-4ad7-b672-963a9473af6f', '86c2dd00-36b6-4e9e-95ae-6085a21192d5', '8a74df49-6cec-4cb8-b096-583f82568f50',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        752.36, 'علاج تبييض الأسنان', '2025-03-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7880a662-c011-4d5f-9c22-ab79a8f3d05b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a74df49-6cec-4cb8-b096-583f82568f50', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-28 16:53:18+03',
        30, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('293d9387-3f09-4ff6-9fc0-7ddeb058810c', '7880a662-c011-4d5f-9c22-ab79a8f3d05b', '8a74df49-6cec-4cb8-b096-583f82568f50',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        378.41, 'علاج خلع الأسنان', '2025-03-28',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('50b50a4c-04d2-4858-9003-9228810cf08b', '7880a662-c011-4d5f-9c22-ab79a8f3d05b', '8a74df49-6cec-4cb8-b096-583f82568f50',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 25, 'COMPLETED',
        279.27, 'علاج حشو الأسنان - السن رقم 25', '2025-03-28',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bccca500-f347-424f-9552-7f09779b5297', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a74df49-6cec-4cb8-b096-583f82568f50', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-13 16:53:18+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('14468a9b-679b-4de1-8ffb-3b7391e571d2', 'bccca500-f347-424f-9552-7f09779b5297', '8a74df49-6cec-4cb8-b096-583f82568f50',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1246.47, 'علاج علاج العصب', '2025-02-13',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('711a9447-b532-4444-86c1-e33def5e6e00', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '50f029c1-5c9b-4d7c-af2a-ee3e9b47505f', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-26 16:53:18+03',
        60, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c105d127-3a24-48a8-bf02-b96386972c91', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a92d8a07-88d8-47a5-a47c-f3828b552b81', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-04-24 16:53:18+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('079bddee-eeef-445d-a3c2-ef5ca85f0d8d', 'c105d127-3a24-48a8-bf02-b96386972c91', 'a92d8a07-88d8-47a5-a47c-f3828b552b81',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 37, 'COMPLETED',
        409.51, 'علاج خلع الأسنان - السن رقم 37', '2025-04-24',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('adf242a2-86be-40d8-a4a6-ff7e353d294f', 'c105d127-3a24-48a8-bf02-b96386972c91', 'a92d8a07-88d8-47a5-a47c-f3828b552b81',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        270.14, 'علاج حشو الأسنان', '2025-04-24',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('733c6323-6834-4246-b24f-555ce7119f67', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a92d8a07-88d8-47a5-a47c-f3828b552b81', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-02-05 16:53:18+03',
        90, 'NO_SHOW', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7a3e6a14-93e6-4425-953a-cfebf3e98060', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a92d8a07-88d8-47a5-a47c-f3828b552b81', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-11 16:53:18+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('04a1a54d-ef72-4a35-bbf9-3302703a6683', '7a3e6a14-93e6-4425-953a-cfebf3e98060', 'a92d8a07-88d8-47a5-a47c-f3828b552b81',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 20, 'COMPLETED',
        90.87, 'علاج استشارة - السن رقم 20', '2025-03-11',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('dafcd6fc-dae9-4f1a-afb6-e19143d6d4b9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0808331a-cc14-45bd-900d-735efc50ce82', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-29 16:53:18+03',
        60, 'CANCELLED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7f7367be-fafa-46eb-948f-d460102e3c3e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0808331a-cc14-45bd-900d-735efc50ce82', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-24 16:53:18+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a42ac48b-7622-4130-b10f-7383770fce46', '7f7367be-fafa-46eb-948f-d460102e3c3e', '0808331a-cc14-45bd-900d-735efc50ce82',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 15, 'COMPLETED',
        115.61, 'علاج استشارة - السن رقم 15', '2025-04-24',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('574a5440-b391-4615-a587-b49b0ff18c4d', '7f7367be-fafa-46eb-948f-d460102e3c3e', '0808331a-cc14-45bd-900d-735efc50ce82',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        362.23, 'علاج خلع الأسنان', '2025-04-24',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('87b6af70-ceb4-4ca7-82e3-d51519dae55c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'abffbe05-3216-44e9-a843-ea3a342f2c29', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-06 16:53:18+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6278ee41-14c7-4c6e-8bdc-598f77eadf34', '87b6af70-ceb4-4ca7-82e3-d51519dae55c', 'abffbe05-3216-44e9-a843-ea3a342f2c29',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        124.83, 'علاج تنظيف الأسنان', '2025-04-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1a76b45c-795f-44d3-a9ee-a263d5d7c59a', '87b6af70-ceb4-4ca7-82e3-d51519dae55c', 'abffbe05-3216-44e9-a843-ea3a342f2c29',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        143.71, 'علاج تنظيف الأسنان', '2025-04-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1c8ce108-0f25-47f9-915e-eab36ebd575a', '87b6af70-ceb4-4ca7-82e3-d51519dae55c', 'abffbe05-3216-44e9-a843-ea3a342f2c29',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 28, 'COMPLETED',
        968.90, 'علاج علاج العصب - السن رقم 28', '2025-04-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('eea3e227-1b70-4774-b141-e8392674c0da', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'abffbe05-3216-44e9-a843-ea3a342f2c29', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-07-19 16:53:18+03',
        30, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('63e08144-3533-429b-9384-46f5be171243', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3027c252-9e51-454a-b713-055b52fc9c5e', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-04 16:53:18+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ccc2e54b-11a6-4852-9400-2346a447d106', '63e08144-3533-429b-9384-46f5be171243', '3027c252-9e51-454a-b713-055b52fc9c5e',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 20, 'COMPLETED',
        86.75, 'علاج X_RAY - السن رقم 20', '2025-03-04',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d514e776-ee9a-4ad1-a572-fd6e58e39b2d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3027c252-9e51-454a-b713-055b52fc9c5e', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-30 16:53:18+03',
        45, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1fd07cc7-35e1-48d7-9eed-2e539cd437d0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3027c252-9e51-454a-b713-055b52fc9c5e', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-08-05 16:53:18+03',
        45, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b7a4bb1d-8e1f-4bf3-99df-8d08e110401f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '881eab7c-30db-4e17-aa99-03e93dde1ff7', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-06-21 16:53:18+03',
        60, 'NO_SHOW', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('520aae78-10a2-4cb4-b515-10306909ec19', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '55e06b34-32dc-4e01-8f7c-2d87e3db166a', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-02-09 16:53:18+03',
        30, 'NO_SHOW', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d72bc477-922c-4594-b2db-de1ea2f1eb89', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '55e06b34-32dc-4e01-8f7c-2d87e3db166a', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-01-19 16:53:18+03',
        30, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('40da0566-97f8-4511-8925-5da8fb2a4cf1', 'd72bc477-922c-4594-b2db-de1ea2f1eb89', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 37, 'COMPLETED',
        1025.48, 'علاج علاج العصب - السن رقم 37', '2025-01-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('af812e68-7d7d-4c56-b54f-b6f78f264b91', 'd72bc477-922c-4594-b2db-de1ea2f1eb89', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        135.03, 'علاج تنظيف الأسنان', '2025-01-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('20ee151b-9ca9-493a-84ce-1b0314d3cf26', 'd72bc477-922c-4594-b2db-de1ea2f1eb89', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 44, 'COMPLETED',
        3699.09, 'علاج زراعة الأسنان - السن رقم 44', '2025-01-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bb100277-ea27-4078-a089-4d906a39bdff', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '55e06b34-32dc-4e01-8f7c-2d87e3db166a', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-08 16:53:18+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('48791cb4-f7d8-4612-b363-9d39b110cea3', 'bb100277-ea27-4078-a089-4d906a39bdff', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 11, 'COMPLETED',
        318.72, 'علاج حشو الأسنان - السن رقم 11', '2025-02-08',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('194b0f7d-6fb9-4c89-a721-5d70867a09c6', 'bb100277-ea27-4078-a089-4d906a39bdff', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 42, 'COMPLETED',
        2777.96, 'علاج جسر الأسنان - السن رقم 42', '2025-02-08',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e59b7404-0805-44cf-a9ee-3cbc72b21e95', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '55e06b34-32dc-4e01-8f7c-2d87e3db166a', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-04-19 16:53:18+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4fe20d12-319f-4071-b9c4-7ddf30b8040c', 'e59b7404-0805-44cf-a9ee-3cbc72b21e95', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 22, 'COMPLETED',
        2802.69, 'علاج جسر الأسنان - السن رقم 22', '2025-04-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dfc5d6ba-5443-4e5d-ad5b-b6e2fb464010', 'e59b7404-0805-44cf-a9ee-3cbc72b21e95', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 29, 'COMPLETED',
        1771.81, 'علاج تاج الأسنان - السن رقم 29', '2025-04-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0a32e6b4-dea2-4dcd-8e3d-5b987f20c029', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '419f3922-59c9-488f-b175-3753c2e6797d', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-06-29 16:53:18+03',
        60, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('edadcb0e-d79c-4ae3-8c64-e224475f313c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '419f3922-59c9-488f-b175-3753c2e6797d', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-15 16:53:18+03',
        30, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a42c7eac-fc89-4c33-b382-5c243563ff83', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ea3193a8-394b-4d2a-8168-079caebf3db3', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-28 16:53:18+03',
        30, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('26b2f681-b474-41cd-82a4-871d863e28db', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '29ed1c3a-1b46-4f01-af0a-16f97bdcb0d7', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-04-04 16:53:18+03',
        45, 'NO_SHOW', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('297fe494-f22a-413d-8054-b1b881bf9e7c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '29ed1c3a-1b46-4f01-af0a-16f97bdcb0d7', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-08-25 16:53:18+03',
        60, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('dfe3e28c-37f1-4b47-b22f-d744ea7a3e09', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '73ffc159-4e56-4891-a951-a7d8e6b4120b', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-07-01 16:53:18+03',
        30, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0bc95328-1386-4024-8e7e-dc35eb696ddb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '73ffc159-4e56-4891-a951-a7d8e6b4120b', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-02-26 16:53:18+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('34ce4965-9296-4548-82b2-7adc002c33b3', '0bc95328-1386-4024-8e7e-dc35eb696ddb', '73ffc159-4e56-4891-a951-a7d8e6b4120b',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 17, 'COMPLETED',
        292.76, 'علاج حشو الأسنان - السن رقم 17', '2025-02-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a0a315e4-84e9-4ffa-9df4-70a4352d5f8e', '0bc95328-1386-4024-8e7e-dc35eb696ddb', '73ffc159-4e56-4891-a951-a7d8e6b4120b',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1351.94, 'علاج علاج العصب', '2025-02-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('69ea8185-995e-4c5e-b13f-bb768a659f91', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-02-05 16:53:18+03',
        45, 'CANCELLED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b11b5f76-6f7e-4c59-ab44-6706e1fb5070', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-07-16 16:53:18+03',
        60, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('171fa22c-4cd8-4cb4-b55b-f652a03a6873', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-05 16:53:18+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('beab4a72-67f5-44bd-a490-2a22d7f932ca', '171fa22c-4cd8-4cb4-b55b-f652a03a6873', '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 35, 'COMPLETED',
        142.99, 'علاج تنظيف الأسنان - السن رقم 35', '2025-02-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b0a9a152-1d41-43bc-9084-3f94b2cb72c4', '171fa22c-4cd8-4cb4-b55b-f652a03a6873', '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1615.25, 'علاج تاج الأسنان', '2025-02-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('effb4a22-a4bd-4b9e-8ba5-88781e42ebd4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-07-05 16:53:18+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('63ab5b9e-2531-4f34-a9df-71a558f05243', 'effb4a22-a4bd-4b9e-8ba5-88781e42ebd4', '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        3095.01, 'علاج جسر الأسنان', '2025-07-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ef005d8b-bafc-4be4-8e20-9f8f788986fc', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '29e9ce56-21f1-4c10-93c9-c4f7b4493c1b', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-04 16:53:18+03',
        30, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1f7f3349-4053-4c3f-9b3f-73ece00d282d', 'ef005d8b-bafc-4be4-8e20-9f8f788986fc', '29e9ce56-21f1-4c10-93c9-c4f7b4493c1b',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        101.73, 'علاج استشارة', '2025-04-04',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('cf173415-2100-4d6c-a975-0c2d6eeea29b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '29e9ce56-21f1-4c10-93c9-c4f7b4493c1b', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-08-12 16:53:18+03',
        45, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ba9719ff-129b-43f9-b799-bccceca55102', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '29e9ce56-21f1-4c10-93c9-c4f7b4493c1b', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-06-16 16:53:18+03',
        30, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0098cad9-5ae2-4420-ac80-a87106dd6e09', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '29e9ce56-21f1-4c10-93c9-c4f7b4493c1b', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-12 16:53:18+03',
        90, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cad4cac1-2919-4864-a331-0d8cc56bdf08', '0098cad9-5ae2-4420-ac80-a87106dd6e09', '29e9ce56-21f1-4c10-93c9-c4f7b4493c1b',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        285.50, 'علاج حشو الأسنان', '2025-05-12',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8aecefcf-12cd-4c8f-9a72-7488d78cf752', '0098cad9-5ae2-4420-ac80-a87106dd6e09', '29e9ce56-21f1-4c10-93c9-c4f7b4493c1b',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        102.45, 'علاج استشارة', '2025-05-12',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('736896b2-05df-416e-9efd-c399e0d00208', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-06-22 16:53:18+03',
        90, 'NO_SHOW', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c037c9a1-caa6-43ff-b275-724b0d86a6a2', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-05-29 16:53:18+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('994d7c7b-1be3-4152-85e4-aca74b2e1567', 'c037c9a1-caa6-43ff-b275-724b0d86a6a2', 'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        111.43, 'علاج X_RAY', '2025-05-29',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ad88d337-c85c-4c2b-ab90-3e941a7d76a5', 'c037c9a1-caa6-43ff-b275-724b0d86a6a2', 'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        346.19, 'علاج خلع الأسنان', '2025-05-29',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d06f300e-15f5-48d9-9f99-4acbf04597fb', 'c037c9a1-caa6-43ff-b275-724b0d86a6a2', 'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        146.13, 'علاج تنظيف الأسنان', '2025-05-29',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7aea363e-c823-42a4-b7ea-e3172e736a4f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-27 16:53:18+03',
        90, 'NO_SHOW', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c87887e2-4447-4c8c-8bae-63fb4813c483', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-20 16:53:18+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c4181099-3fb7-42dc-b20f-2bacd4e75cb4', 'c87887e2-4447-4c8c-8bae-63fb4813c483', 'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 21, 'COMPLETED',
        1464.58, 'علاج تاج الأسنان - السن رقم 21', '2025-02-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7c91cb90-99e0-4744-8dc5-776535749fa6', 'c87887e2-4447-4c8c-8bae-63fb4813c483', 'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        166.13, 'علاج تنظيف الأسنان', '2025-02-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5da4617b-0171-4728-982f-2fd553b0223c', 'c87887e2-4447-4c8c-8bae-63fb4813c483', 'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        5384.38, 'علاج زراعة الأسنان', '2025-02-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('609c76b5-6905-40be-b31d-f393b8690588', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3350684-9bed-4605-82b7-9b905e723444', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-11 16:53:18+03',
        30, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('26072cd0-2d95-42ab-befe-f4d901f1059c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3350684-9bed-4605-82b7-9b905e723444', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-02-08 16:53:18+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('85e9baf7-375e-493e-9aaa-96960e8b10c2', '26072cd0-2d95-42ab-befe-f4d901f1059c', 'f3350684-9bed-4605-82b7-9b905e723444',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        419.50, 'علاج خلع الأسنان', '2025-02-08',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3b3f9cb1-19b9-4938-97df-66b156d19b4e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3350684-9bed-4605-82b7-9b905e723444', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-08 16:53:18+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c9f39b45-3232-4ecd-ac6d-1ab472a699e7', '3b3f9cb1-19b9-4938-97df-66b156d19b4e', 'f3350684-9bed-4605-82b7-9b905e723444',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4279.57, 'علاج زراعة الأسنان', '2025-02-08',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ea6efae5-449d-4c96-b7da-70f4dfe34a50', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ed000d0a-4263-4295-9a1e-50df1f5bdc75', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-02 16:53:18+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ac8bfcd9-c3df-483b-9825-f788610e3e8d', 'ea6efae5-449d-4c96-b7da-70f4dfe34a50', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 26, 'COMPLETED',
        283.77, 'علاج حشو الأسنان - السن رقم 26', '2025-03-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d8da98d2-fa4f-408b-9f2c-48a5fe9ea87b', 'ea6efae5-449d-4c96-b7da-70f4dfe34a50', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        356.17, 'علاج خلع الأسنان', '2025-03-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('506f56bd-8b11-4d1a-ac7b-8fb9760394ea', 'ea6efae5-449d-4c96-b7da-70f4dfe34a50', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 31, 'COMPLETED',
        1286.59, 'علاج علاج العصب - السن رقم 31', '2025-03-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('50227ea3-9897-495c-a35b-a657795b99a4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ed000d0a-4263-4295-9a1e-50df1f5bdc75', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-18 16:53:18+03',
        45, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d5e69992-b139-4a73-a1ff-06f7287a2b75', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ed000d0a-4263-4295-9a1e-50df1f5bdc75', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-12 16:53:18+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('70056cf9-9045-47aa-b706-4a33577033b8', 'd5e69992-b139-4a73-a1ff-06f7287a2b75', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 42, 'COMPLETED',
        457.37, 'علاج خلع الأسنان - السن رقم 42', '2025-04-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c3ac492d-a471-4afb-8790-32c2ab43a7ef', 'd5e69992-b139-4a73-a1ff-06f7287a2b75', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 29, 'COMPLETED',
        282.59, 'علاج حشو الأسنان - السن رقم 29', '2025-04-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a617fc86-9095-4e34-8b9e-f86e7c50eb07', 'd5e69992-b139-4a73-a1ff-06f7287a2b75', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        94.21, 'علاج استشارة', '2025-04-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0eaf768f-4fda-4a40-8a60-454d1fe2a410', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ed000d0a-4263-4295-9a1e-50df1f5bdc75', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-06-07 16:53:18+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('47e062df-656b-4209-949d-b0a87e4038b8', '0eaf768f-4fda-4a40-8a60-454d1fe2a410', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 13, 'COMPLETED',
        743.24, 'علاج تبييض الأسنان - السن رقم 13', '2025-06-07',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e06c2b10-8761-4ece-9bd8-8c78061a9022', '0eaf768f-4fda-4a40-8a60-454d1fe2a410', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 43, 'COMPLETED',
        301.10, 'علاج حشو الأسنان - السن رقم 43', '2025-06-07',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');

-- Adding tooth history manually to avoid trigger issues
-- These would normally be created by updating patient_teeth records

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    '28aefe16-1604-468e-bc20-438708c5636d',
    pt.id,
    'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2',
    13,
    (SELECT id FROM tooth_conditions WHERE code = 'CROWN'),
    'dceed4e2-98a3-43ca-a3d3-a27f1d635c2f',
    '2025-04-07 12:00:00+03',
    'تم العلاج - CROWN',
    'b5ee786d-b86e-4ca3-a705-4417f1c65b03'
FROM patient_teeth pt
WHERE pt.patient_id = 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2'
  AND pt.tooth_number = 13;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    'e2692786-cd39-4917-ba3f-e048752e6bee',
    pt.id,
    '8b7a519e-6993-49a7-b712-6cfbcb387fae',
    39,
    (SELECT id FROM tooth_conditions WHERE code = 'CROWN'),
    '145f6624-a2b1-4a36-836d-d8c01f0c68ea',
    '2025-03-18 12:00:00+03',
    'تم العلاج - CROWN',
    'b5ee786d-b86e-4ca3-a705-4417f1c65b03'
FROM patient_teeth pt
WHERE pt.patient_id = '8b7a519e-6993-49a7-b712-6cfbcb387fae'
  AND pt.tooth_number = 39;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    '563ab58d-edd7-4c99-8fbd-5792fcddacf7',
    pt.id,
    '2d467671-ea75-4f4d-af6a-76945d8445d0',
    41,
    (SELECT id FROM tooth_conditions WHERE code = 'ROOT_CANAL'),
    '26cabb43-7060-49eb-a31d-5530292b8e12',
    '2025-07-04 12:00:00+03',
    'تم العلاج - ROOT_CANAL',
    'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'
FROM patient_teeth pt
WHERE pt.patient_id = '2d467671-ea75-4f4d-af6a-76945d8445d0'
  AND pt.tooth_number = 41;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    '487447a6-0f59-4365-8a4f-94dd6a85516d',
    pt.id,
    '8a74df49-6cec-4cb8-b096-583f82568f50',
    13,
    (SELECT id FROM tooth_conditions WHERE code = 'CROWN'),
    'e7d74f22-f396-4c6f-8184-e53c0266a2b6',
    '2025-03-10 12:00:00+03',
    'تم العلاج - CROWN',
    '844e0809-f1e1-4f79-9c7c-d09754b25b6b'
FROM patient_teeth pt
WHERE pt.patient_id = '8a74df49-6cec-4cb8-b096-583f82568f50'
  AND pt.tooth_number = 13;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    'd49cd817-eaf6-4d7c-bfd7-9b19ca15222f',
    pt.id,
    '8a74df49-6cec-4cb8-b096-583f82568f50',
    25,
    (SELECT id FROM tooth_conditions WHERE code = 'CROWN'),
    '50b50a4c-04d2-4858-9003-9228810cf08b',
    '2025-03-28 12:00:00+03',
    'تم العلاج - CROWN',
    'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'
FROM patient_teeth pt
WHERE pt.patient_id = '8a74df49-6cec-4cb8-b096-583f82568f50'
  AND pt.tooth_number = 25;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    'bff9e3fd-f409-4530-956d-941225b48f4e',
    pt.id,
    'a92d8a07-88d8-47a5-a47c-f3828b552b81',
    20,
    (SELECT id FROM tooth_conditions WHERE code = 'FILLED'),
    '04a1a54d-ef72-4a35-bbf9-3302703a6683',
    '2025-03-11 12:00:00+03',
    'تم العلاج - FILLED',
    'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'
FROM patient_teeth pt
WHERE pt.patient_id = 'a92d8a07-88d8-47a5-a47c-f3828b552b81'
  AND pt.tooth_number = 20;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    'edef2a85-3e14-48ba-9447-d213df03f338',
    pt.id,
    '0808331a-cc14-45bd-900d-735efc50ce82',
    15,
    (SELECT id FROM tooth_conditions WHERE code = 'ROOT_CANAL'),
    'a42ac48b-7622-4130-b10f-7383770fce46',
    '2025-04-24 12:00:00+03',
    'تم العلاج - ROOT_CANAL',
    'b5ee786d-b86e-4ca3-a705-4417f1c65b03'
FROM patient_teeth pt
WHERE pt.patient_id = '0808331a-cc14-45bd-900d-735efc50ce82'
  AND pt.tooth_number = 15;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    '69b971b0-6290-4156-b281-0724c690a857',
    pt.id,
    'abffbe05-3216-44e9-a843-ea3a342f2c29',
    28,
    (SELECT id FROM tooth_conditions WHERE code = 'ROOT_CANAL'),
    '1c8ce108-0f25-47f9-915e-eab36ebd575a',
    '2025-04-06 12:00:00+03',
    'تم العلاج - ROOT_CANAL',
    'b5ee786d-b86e-4ca3-a705-4417f1c65b03'
FROM patient_teeth pt
WHERE pt.patient_id = 'abffbe05-3216-44e9-a843-ea3a342f2c29'
  AND pt.tooth_number = 28;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    '053e78f8-e119-41d6-bbec-4ef0208e9486',
    pt.id,
    '3027c252-9e51-454a-b713-055b52fc9c5e',
    20,
    (SELECT id FROM tooth_conditions WHERE code = 'CROWN'),
    'ccc2e54b-11a6-4852-9400-2346a447d106',
    '2025-03-04 12:00:00+03',
    'تم العلاج - CROWN',
    '844e0809-f1e1-4f79-9c7c-d09754b25b6b'
FROM patient_teeth pt
WHERE pt.patient_id = '3027c252-9e51-454a-b713-055b52fc9c5e'
  AND pt.tooth_number = 20;

-- Manual tooth history entry for patient tooth
INSERT INTO tooth_history (id, patient_tooth_id, patient_id, tooth_number,
                           condition_id, treatment_id, change_date, notes, recorded_by)
SELECT
    '5b2eed05-3af5-4869-a75e-64318d5d2a81',
    pt.id,
    '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
    44,
    (SELECT id FROM tooth_conditions WHERE code = 'ROOT_CANAL'),
    '20ee151b-9ca9-493a-84ce-1b0314d3cf26',
    '2025-01-19 12:00:00+03',
    'تم العلاج - ROOT_CANAL',
    'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'
FROM patient_teeth pt
WHERE pt.patient_id = '55e06b34-32dc-4e01-8f7c-2d87e3db166a'
  AND pt.tooth_number = 44;

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('3497c2a4-c396-43d7-9a56-7d27f794da2c', 'bd2e7d3e-1e92-4bf6-9ac2-f212dee320a2', 'INV-1000',
        '2025-07-06', '2025-08-05',
        1799.78, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d0f20215-6d3a-4fb3-b2e1-3503d0de2957', '3497c2a4-c396-43d7-9a56-7d27f794da2c', 'تاج الأسنان', 899.89);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('bbfe634a-3340-4c0d-8210-5b789544c307', '3497c2a4-c396-43d7-9a56-7d27f794da2c', 'تبييض الأسنان', 899.89);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('754964ee-9ca1-4393-a45b-9d12f0b3b534', '8b7a519e-6993-49a7-b712-6cfbcb387fae', 'INV-1001',
        '2025-06-26', '2025-07-26',
        2159.70, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d9fde0cd-9b26-4689-adf0-de1377adcd43', '754964ee-9ca1-4393-a45b-9d12f0b3b534', 'تبييض الأسنان', 1079.85);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('ae1d8873-976d-4bbd-8066-013073113733', '754964ee-9ca1-4393-a45b-9d12f0b3b534', 'زراعة الأسنان', 1079.85);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('bac96a45-e987-4e9c-aaa6-0bcc00791fde', '2d467671-ea75-4f4d-af6a-76945d8445d0', 'INV-1002',
        '2025-05-09', '2025-06-08',
        537.22, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2fd4b927-e76f-44c2-b23e-33a3c30000ea', 'bac96a45-e987-4e9c-aaa6-0bcc00791fde', 'تاج الأسنان', 268.61);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('ffbd7864-553c-4494-a5f7-078c5f5f0717', 'bac96a45-e987-4e9c-aaa6-0bcc00791fde', 'X_RAY', 268.61);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('6a5133dd-cda2-4481-a23c-3647b61f5248', '8a74df49-6cec-4cb8-b096-583f82568f50', 'INV-1003',
        '2025-07-06', '2025-08-05',
        2322.56, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('eea337d4-52eb-4b2b-846a-a93935908f9b', '6a5133dd-cda2-4481-a23c-3647b61f5248', 'تنظيف الأسنان', 774.19);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c0e97e4a-3e81-4d53-a686-942af7794099', '6a5133dd-cda2-4481-a23c-3647b61f5248', 'تنظيف الأسنان', 774.19);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b338ca90-8a3b-4433-86b8-fd0daad5be3d', '6a5133dd-cda2-4481-a23c-3647b61f5248', 'خلع الأسنان', 774.19);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('2fa31d33-fc1c-4a21-8b19-12fe5f60a593', '50f029c1-5c9b-4d7c-af2a-ee3e9b47505f', 'INV-1004',
        '2025-07-12', '2025-08-11',
        584.12, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c9f62cf5-df81-475e-9b43-c38100fe4512', '2fa31d33-fc1c-4a21-8b19-12fe5f60a593', 'X_RAY', 584.12);

INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('87ec3950-c061-45f8-a672-20c772958b8f', '2fa31d33-fc1c-4a21-8b19-12fe5f60a593', '50f029c1-5c9b-4d7c-af2a-ee3e9b47505f',
        '2025-07-31', 584.12,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1004',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('7958e9e7-9279-4e7a-8caf-405f5eb1ecc5', 'a92d8a07-88d8-47a5-a47c-f3828b552b81', 'INV-1005',
        '2025-05-19', '2025-06-18',
        2078.45, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2e68dd8c-71af-44e9-8fa6-ff27c797f33f', '7958e9e7-9279-4e7a-8caf-405f5eb1ecc5', 'حشو الأسنان', 1039.23);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d1d6edb4-68dc-4538-813a-e883b0179a0f', '7958e9e7-9279-4e7a-8caf-405f5eb1ecc5', 'تنظيف الأسنان', 1039.23);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('2ef0679a-76b3-462c-8211-4a55fb62d715', '0808331a-cc14-45bd-900d-735efc50ce82', 'INV-1006',
        '2025-07-06', '2025-08-05',
        1381.48, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('db4083ef-18cb-429d-8b92-5a6ef81c718e', '2ef0679a-76b3-462c-8211-4a55fb62d715', 'حشو الأسنان', 1381.48);

INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('bd7c7387-31df-46fd-849f-7b5a83a8e513', '2ef0679a-76b3-462c-8211-4a55fb62d715', '0808331a-cc14-45bd-900d-735efc50ce82',
        '2025-07-08', 690.74,
        'نقدي', 'PAYMENT', 'دفعة فاتورة INV-1006',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('c1f15ed3-04e4-4e39-834b-7cf5663e81c9', 'abffbe05-3216-44e9-a843-ea3a342f2c29', 'INV-1007',
        '2025-06-27', '2025-07-27',
        2140.15, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0ca8cb44-b6f3-4281-8b1a-96d55e2976fd', 'c1f15ed3-04e4-4e39-834b-7cf5663e81c9', 'علاج العصب', 713.38);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('8abbbf39-7f5e-4aee-b21f-b4c6195593b0', 'c1f15ed3-04e4-4e39-834b-7cf5663e81c9', 'تاج الأسنان', 713.38);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('56fd47f4-42c0-48e5-98d0-85ff47dd08d8', 'c1f15ed3-04e4-4e39-834b-7cf5663e81c9', 'تبييض الأسنان', 713.38);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('a5813cc9-6290-4683-ba87-c0db5bbac6a0', '3027c252-9e51-454a-b713-055b52fc9c5e', 'INV-1008',
        '2025-05-26', '2025-06-25',
        316.13, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e8004de3-01cc-4e65-9117-e7be16f47918', 'a5813cc9-6290-4683-ba87-c0db5bbac6a0', 'X_RAY', 316.13);

INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('35a88d80-b010-45f5-b799-67ef02007512', 'a5813cc9-6290-4683-ba87-c0db5bbac6a0', '3027c252-9e51-454a-b713-055b52fc9c5e',
        '2025-06-06', 316.13,
        'تحويل بنكي', 'PAYMENT', 'دفعة فاتورة INV-1008',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('4db0e1f3-b3d2-49a5-9428-34bf54e36d27', '881eab7c-30db-4e17-aa99-03e93dde1ff7', 'INV-1009',
        '2025-06-25', '2025-07-25',
        2239.62, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6a5ae9bf-8869-4831-9327-04b1c95f1ed5', '4db0e1f3-b3d2-49a5-9428-34bf54e36d27', 'تبييض الأسنان', 746.54);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('9b5bc03a-a740-4f63-b1ef-7d1ba159d785', '4db0e1f3-b3d2-49a5-9428-34bf54e36d27', 'حشو الأسنان', 746.54);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('1fece4f1-21fe-42b9-9430-f67230c7f270', '4db0e1f3-b3d2-49a5-9428-34bf54e36d27', 'جسر الأسنان', 746.54);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('767c31cd-54e2-40e6-bd9c-91a6a53a3b94', '55e06b34-32dc-4e01-8f7c-2d87e3db166a', 'INV-1010',
        '2025-05-28', '2025-06-27',
        2368.90, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('503baec9-a8a6-4e24-b534-4be0af7044e0', '767c31cd-54e2-40e6-bd9c-91a6a53a3b94', 'زراعة الأسنان', 2368.90);

INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('775dd2d3-b022-4802-b8e2-b90826c3e3e3', '767c31cd-54e2-40e6-bd9c-91a6a53a3b94', '55e06b34-32dc-4e01-8f7c-2d87e3db166a',
        '2025-05-29', 2368.90,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1010',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('f90e42b9-81d2-4a91-ace6-87d278451910', '419f3922-59c9-488f-b175-3753c2e6797d', 'INV-1011',
        '2025-04-30', '2025-05-30',
        2458.80, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('95022d2a-6437-4ce8-8d80-e821db48323e', 'f90e42b9-81d2-4a91-ace6-87d278451910', 'خلع الأسنان', 2458.80);

INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('f6e84be8-2248-4281-9f65-6a134a896514', 'f90e42b9-81d2-4a91-ace6-87d278451910', '419f3922-59c9-488f-b175-3753c2e6797d',
        '2025-05-07', 2458.80,
        'نقدي', 'PAYMENT', 'دفعة فاتورة INV-1011',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('7755d10f-fa74-4f19-a26b-ce49aaa17d04', 'ea3193a8-394b-4d2a-8168-079caebf3db3', 'INV-1012',
        '2025-05-08', '2025-06-07',
        2381.31, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('81c3642a-8759-4f7f-879c-6137fdbf3161', '7755d10f-fa74-4f19-a26b-ce49aaa17d04', 'حشو الأسنان', 1190.65);

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0e86148d-17a2-4838-99e4-e20a8f4ad34a', '7755d10f-fa74-4f19-a26b-ce49aaa17d04', 'تنظيف الأسنان', 1190.65);

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('b2e7ada1-1c9e-497b-b584-eed1065df26f', '29ed1c3a-1b46-4f01-af0a-16f97bdcb0d7', 'INV-1013',
        '2025-04-24', '2025-05-24',
        2987.94, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('af1a8ff9-10dc-47ed-bcfc-dc3bf114716f', 'b2e7ada1-1c9e-497b-b584-eed1065df26f', 'خلع الأسنان', 2987.94);

INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('71600d18-2eab-4e32-a620-e7bd7374ae95', 'b2e7ada1-1c9e-497b-b584-eed1065df26f', '29ed1c3a-1b46-4f01-af0a-16f97bdcb0d7',
        '2025-05-13', 2987.94,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1013',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('cad71fbc-c59a-4b69-9b52-f4e72229cb7a', '73ffc159-4e56-4891-a951-a7d8e6b4120b', 'INV-1014',
        '2025-07-01', '2025-07-31',
        1471.02, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('08e84294-bdf4-402d-8789-a8d93690831d', 'cad71fbc-c59a-4b69-9b52-f4e72229cb7a', 'جسر الأسنان', 1471.02);

INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('8662de4d-a954-4c26-8104-bb8da49ecc46', 'cad71fbc-c59a-4b69-9b52-f4e72229cb7a', '73ffc159-4e56-4891-a951-a7d8e6b4120b',
        '2025-07-14', 735.51,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1014',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');

INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('ed3e7704-73f5-4b7e-a487-e2d25077a1ed', '0808331a-cc14-45bd-900d-735efc50ce82', 'LAB-2024001', 'طقم أسنان جزئي',
        NULL,
        '2025-07-07', '2025-07-21',
        'COMPLETED',
        'مختبر الأسنان المتقدم');

INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('a2b4f7d6-ee95-474c-a46c-459228cbd9d0', '419f3922-59c9-488f-b175-3753c2e6797d', 'LAB-2024002', 'جسر أسنان',
        37,
        '2025-06-27', '2025-07-11',
        'IN_PROGRESS',
        'مختبر الأسنان المتقدم');

INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('9fa62a7e-5566-4aea-afe5-e8e586935102', '2d467671-ea75-4f4d-af6a-76945d8445d0', 'LAB-2024003', 'جسر أسنان',
        38,
        '2025-07-02', '2025-07-16',
        'IN_PROGRESS',
        'مختبر الأسنان المتقدم');

INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('33287d45-1bfb-4c15-a313-b7b8e071e5b0', '39f3e20c-c5c9-475b-8bd3-f9d6eb63f4e2', 'LAB-2024004', 'واقي ليلي',
        43,
        '2025-06-17', '2025-07-01',
        'COMPLETED',
        'مختبر الأسنان المتقدم');

INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('ae378c9b-3def-4d39-a907-c26062f65ef3', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75', 'LAB-2024005', 'جسر أسنان',
        31,
        '2025-06-14', '2025-06-28',
        'COMPLETED',
        'مختبر الأسنان المتقدم');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('4f7fe26a-b0f6-4ad6-a623-66a51584f30a', '29ed1c3a-1b46-4f01-af0a-16f97bdcb0d7', 'يحتاج المريض إلى أشعة بانورامية',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '33 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('6838ea06-e8f5-4e34-a246-2c363bd3b51c', '73ffc159-4e56-4891-a951-a7d8e6b4120b', 'تم وصف مضاد حيوي للمريض',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '30 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('09bd9fa2-4c0a-4e7a-bee0-1a4e9d4cd766', 'ea5cb8ec-aec6-414c-9abe-1ac3b7f8e3ff', 'المريض يحتاج إلى متابعة بعد أسبوعين',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '35 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('4dadcf9b-4596-4861-b19d-958202a8f3af', '50f029c1-5c9b-4d7c-af2a-ee3e9b47505f', 'المريض يحتاج إلى متابعة بعد أسبوعين',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '77 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('366b8fcd-193f-4e5d-9bd6-1c8b4b91d037', '419f3922-59c9-488f-b175-3753c2e6797d', 'المريض يحتاج إلى متابعة بعد أسبوعين',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '62 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('c1682fba-1507-4dad-b16d-f95b8494daab', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75', 'المريض يحتاج إلى متابعة بعد أسبوعين',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '63 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('f3aa816c-cb26-4891-86cb-3143173ca7c4', '8a74df49-6cec-4cb8-b096-583f82568f50', 'المريض يحتاج إلى متابعة بعد أسبوعين',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '11 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('70f2b4f0-9f3d-49dc-a756-a06089658a1e', 'a92d8a07-88d8-47a5-a47c-f3828b552b81', 'المريض يعاني من حساسية الأسنان',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NOW() - INTERVAL '64 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('cd214d16-869f-4dd1-af3f-b8d523f54b8b', '73ffc159-4e56-4891-a951-a7d8e6b4120b', 'تم وصف مضاد حيوي للمريض',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NOW() - INTERVAL '29 days');

INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('30638235-d344-420d-9bc9-afded597259f', 'ed000d0a-4263-4295-9a1e-50df1f5bdc75', 'المريض يحتاج إلى متابعة بعد أسبوعين',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NOW() - INTERVAL '88 days');




-- ====== ADDITIONAL DATA STARTS HERE ======

-- Additional Staff Members
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('7e77e993-1599-497c-9a23-e5ea870a5d70', 'د. محمد الجهني', 'DOCTOR', 'doctor4@clinic.sa', '+966 554184209', TRUE);
INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('7e77e993-1599-497c-9a23-e5ea870a5d70', (SELECT id FROM specialties WHERE name = 'General Dentistry'));
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('cfb9217c-ca9d-4983-b4f0-66d4520641d8', 'د. ناصر الشامي', 'DOCTOR', 'doctor5@clinic.sa', '+966 535681234', TRUE);
INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('cfb9217c-ca9d-4983-b4f0-66d4520641d8', (SELECT id FROM specialties WHERE name = 'General Dentistry'));
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('3674d376-fca0-4d74-9aa4-bb2cc13b9021', 'د. عمر الأحمد', 'DOCTOR', 'doctor6@clinic.sa', '+966 510750284', TRUE);
INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('3674d376-fca0-4d74-9aa4-bb2cc13b9021', (SELECT id FROM specialties WHERE name = 'General Dentistry'));
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('a740d31b-41cc-4677-beac-f6bd82db089a', 'د. راشد العنزي', 'DOCTOR', 'doctor7@clinic.sa', '+966 526360818', TRUE);
INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('a740d31b-41cc-4677-beac-f6bd82db089a', (SELECT id FROM specialties WHERE name = 'General Dentistry'));
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('089fece7-70fe-4058-8e9e-1f33b3d845dd', 'منى العمر', 'NURSE', 'nurse5@clinic.sa', '+966 553546445', TRUE);
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('89d4aa01-4780-4211-9dff-0f2d77bf60b3', 'زينب الخالد', 'NURSE', 'nurse6@clinic.sa', '+966 533194371', TRUE);
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('f9d0146d-56b1-496a-bd0f-870170858d82', 'سلمى العلي', 'NURSE', 'nurse7@clinic.sa', '+966 576463868', TRUE);
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('6f554213-30e0-4b77-adfa-99a9f7a7c01f', 'فاطمة الجهني', 'ASSISTANT', 'hygienist1@clinic.sa', '+966 521911022', TRUE);
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('015d9f83-7100-49dc-8a45-dfaf8357ff3b', 'سلمى الشامي', 'ASSISTANT', 'hygienist2@clinic.sa', '+966 543013406', TRUE);
INSERT INTO staff (id, full_name, role, email, phone_number, is_active)
VALUES ('42ca1b05-54de-419e-81f9-0bef64406c92', 'ليلى الجهني', 'ADMIN', 'admin@clinic.sa', '+966 568017836', TRUE);
-- Additional Procedures
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'SCALING', 'إزالة الجير', 'إزالة الجير والترسبات', 200.0, 45);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'FLUORIDE', 'فلورايد', 'علاج الفلورايد الوقائي', 80.0, 20);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'SEALANT', 'حشو وقائي', 'حشو وقائي للأطفال', 120.0, 30);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'DENTURE', 'طقم أسنان', 'تركيب طقم أسنان كامل', 5000.0, 90);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'PARTIAL', 'طقم جزئي', 'تركيب طقم أسنان جزئي', 3500.0, 60);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ORTHO', 'تقويم', 'تركيب تقويم الأسنان', 8000.0, 60);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'RETAINER', 'مثبت تقويم', 'تركيب مثبت بعد التقويم', 500.0, 30);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'VENEER', 'قشرة تجميلية', 'تركيب قشرة تجميلية', 2000.0, 45);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'BONDING', 'ترميم تجميلي', 'ترميم تجميلي للأسنان', 600.0, 45);
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES ((SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'GUMTREAT', 'علاج اللثة', 'علاج أمراض اللثة', 400.0, 60);
-- Additional Patients
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'P2024021', 'رانيا الزهراني', '1944-12-20', 'female',
        '+966 544250775', 'patient21@email.com', 'شارع مكة، حي المروة', 'بوبا العربية',
        'INS268305',
        'أمراض الكبد',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('48bb9995-9330-4894-b74a-f24116c225ed', 'P2024022', 'رانيا الحربي', '1954-06-28', 'female',
        '+966 581562376', 'patient22@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', NULL,
        NULL,
        'أمراض القلب',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', 'P2024023', 'وليد القحطاني', '1969-07-02', 'male',
        '+966 570273461', 'patient23@email.com', 'شارع الأمير ماجد، حي الفيصلية', 'التعاونية للتأمين',
        'INS945878',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('517d1071-dde1-4d36-b1ef-5745264bf869', 'P2024024', 'سلمى الزهراني', '1979-04-19', 'female',
        '+966 571588559', 'patient24@email.com', 'شارع الأمير ماجد، حي الفيصلية', NULL,
        NULL,
        'فقر الدم',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('009f45fe-3809-464d-96ce-3956e150e456', 'P2024025', 'سلطان الغامدي', '1943-11-22', 'male',
        '+966 587294190', 'patient25@email.com', 'شارع الملك فهد، حي الروضة', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('8a77a5ce-84d2-4696-bbba-52f2304bdb8d', 'P2024026', 'رانيا الحربي', '1972-09-06', 'female',
        '+966 573044782', 'patient26@email.com', 'شارع الملك فهد، حي الروضة', 'سلامة',
        'INS165644',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('0f85ab34-195f-4d5b-8a27-47b733b7678f', 'P2024027', 'وليد الخالد', '2004-03-23', 'male',
        '+966 534417281', 'patient27@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'أليانز السعودي',
        'INS676661',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('81c2b2f8-63bb-488c-a276-d6766d4eae8a', 'P2024028', 'علي القحطاني', '1996-07-20', 'male',
        '+966 555963049', 'patient28@email.com', 'شارع الملك فهد، حي الروضة', 'سلامة',
        'INS165591',
        'الصرع',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('7aa917b9-c53b-4e77-935a-6297112588e6', 'P2024029', 'ريم العنزي', '1990-05-05', 'female',
        '+966 576540579', 'patient29@email.com', 'شارع الملك عبدالله، حي النزهة', 'بوبا العربية',
        'INS500031',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('33208c0c-cf5f-4fd4-9091-a6247d44f026', 'P2024030', 'وليد العتيبي', '1980-07-03', 'male',
        '+966 551568330', 'patient30@email.com', 'شارع التحلية، حي الزهراء', 'وقاية للتأمين',
        'INS507069',
        'ضغط الدم',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('3bba0776-a012-4b92-90f4-765a0ee510d6', 'P2024031', 'سارة القحطاني', '1956-05-02', 'female',
        '+966 520939301', 'patient31@email.com', 'شارع الأمير ماجد، حي الفيصلية', 'التعاونية للتأمين',
        'INS479695',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('51739c46-139f-499c-bec0-720560313310', 'P2024032', 'طارق الجهني', '2006-04-03', 'male',
        '+966 576408137', 'patient32@email.com', 'شارع المدينة، حي الصفا', 'بوبا العربية',
        'INS158069',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('cb4208fe-0afc-4844-80c5-e437eda54c4d', 'P2024033', 'رانيا المحمد', '1968-08-20', 'female',
        '+966 511525043', 'patient33@email.com', 'شارع مكة، حي المروة', 'بوبا العربية',
        'INS222408',
        'أمراض الكبد',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('3d209091-82a5-484c-91f7-6dbaf91154b1', 'P2024034', 'عبير الغامدي', '1954-05-28', 'female',
        '+966 584572348', 'patient34@email.com', 'شارع الأمير ماجد، حي الفيصلية', NULL,
        NULL,
        'السكري',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('74037d63-ac9e-4428-860f-3c0a0504a40c', 'P2024035', 'عائشة الحربي', '1999-01-15', 'female',
        '+966 589569548', 'patient35@email.com', 'شارع الأمير سلطان، حي السلامة', 'ميدغلف',
        'INS738799',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('998dbd19-94b9-4c62-9df1-202d06aa254a', 'P2024036', 'منى الإبراهيم', '2009-02-25', 'female',
        '+966 518631516', 'patient36@email.com', 'شارع المدينة، حي الصفا', 'التعاونية للتأمين',
        'INS727146',
        'أمراض القلب',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('c85699c1-ffc2-483b-8999-cacc91b76fa1', 'P2024037', 'نجلاء المصري', '1990-09-07', 'female',
        '+966 528702110', 'patient37@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', NULL,
        NULL,
        'حساسية البنسلين',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('75d43ba8-1c27-4d1b-99e6-5ce77cf08750', 'P2024038', 'منى المطيري', '1966-10-16', 'female',
        '+966 539030974', 'patient38@email.com', 'شارع المدينة، حي الصفا', NULL,
        NULL,
        'الصرع',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('536663f3-678d-4bc4-8f75-c188152194d9', 'P2024039', 'يوسف العتيبي', '2000-11-26', 'male',
        '+966 513883118', 'patient39@email.com', 'شارع الأمير محمد بن عبدالعزيز، حي الأندلس', NULL,
        NULL,
        'فقر الدم',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('f9e60397-26d4-4c86-98a7-d906d9359501', 'P2024040', 'سلطان المطيري', '1957-07-19', 'male',
        '+966 525859178', 'patient40@email.com', 'شارع فلسطين، حي البوادي', 'ملاذ للتأمين',
        'INS811130',
        'حساسية الأسبرين',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('14d28cf4-88b5-41f5-98b2-926cb0f94ae9', 'P2024041', 'ماجد الحربي', '1986-10-03', 'male',
        '+966 511544737', 'patient41@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'أكسا التعاونية',
        'INS635539',
        'أمراض الكلى',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('688b05f6-aa01-4e29-8e46-92af09f92a12', 'P2024042', 'ليلى العنزي', '1965-05-28', 'female',
        '+966 599919450', 'patient42@email.com', 'شارع الملك فهد، حي الروضة', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('fc2bd317-69b1-405e-9094-f85f96a7d53b', 'P2024043', 'منى السعيد', '2012-07-16', 'female',
        '+966 543081316', 'patient43@email.com', 'شارع فلسطين، حي البوادي', 'أليانز السعودي',
        'INS163301',
        'حساسية البنسلين',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('452847a3-bd3c-4666-8aef-9cdb7c508f60', 'P2024044', 'حسن الإبراهيم', '2012-10-13', 'male',
        '+966 585826375', 'patient44@email.com', 'شارع فلسطين، حي البوادي', NULL,
        NULL,
        'أمراض الكلى',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('a60a0f58-bc0e-4896-b8a8-25359d4827a4', 'P2024045', 'لينا الإبراهيم', '1989-10-13', 'female',
        '+966 575507961', 'patient45@email.com', 'شارع الملك عبدالله، حي النزهة', 'ميدغلف',
        'INS376969',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('3e8dcbc5-36b1-435b-bfef-356d7d615c73', 'P2024046', 'راشد الحربي', '1991-03-12', 'male',
        '+966 533912689', 'patient46@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'سلامة',
        'INS527794',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('bb002dda-1f45-4af2-b66d-13d745614679', 'P2024047', 'لينا السعيد', '2004-02-26', 'female',
        '+966 526289440', 'patient47@email.com', 'شارع الأمير سلطان، حي السلامة', 'المتحدة للتأمين',
        'INS471555',
        'الصرع',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('4c4eeae5-1844-4545-a6ce-2b8bbece8e88', 'P2024048', 'نجلاء الأحمد', '1998-03-22', 'female',
        '+966 536359429', 'patient48@email.com', 'شارع فلسطين، حي البوادي', 'سلامة',
        'INS842539',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'P2024049', 'ليلى المطيري', '1963-04-09', 'female',
        '+966 540198838', 'patient49@email.com', 'شارع الأمير محمد بن عبدالعزيز، حي الأندلس', 'ميدغلف',
        'INS897944',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('e6b862ca-2f88-41e6-a7e2-6b2e39bb9797', 'P2024050', 'أمل الدوسري', '1971-09-12', 'female',
        '+966 550132735', 'patient50@email.com', 'شارع الملك فهد، حي الروضة', 'الدرع العربي',
        'INS390278',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('b5ad516c-6b71-4416-b660-056ad1d34f79', 'P2024051', 'ليلى الإبراهيم', '1998-04-06', 'female',
        '+966 541879101', 'patient51@email.com', 'شارع المدينة، حي الصفا', 'سلامة',
        'INS167991',
        'الربو',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('578912a3-7404-4cc6-8ff7-d4c7332acc45', 'P2024052', 'لينا المحمد', '1964-04-12', 'female',
        '+966 539579657', 'patient52@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'وقاية للتأمين',
        'INS375725',
        'أمراض الكبد',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('81a36250-0264-4339-b35e-dd3786feca0e', 'P2024053', 'هدى العلي', '2003-02-27', 'female',
        '+966 540819595', 'patient53@email.com', 'شارع فلسطين، حي البوادي', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('c99fc741-2b1d-405b-ac34-dd92212fbc99', 'P2024054', 'راشد الحسن', '1980-11-14', 'male',
        '+966 535541767', 'patient54@email.com', 'شارع التحلية، حي الزهراء', NULL,
        NULL,
        'أمراض الكبد',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('951394c6-4e21-4d51-ba77-80c783a574e2', 'P2024055', 'عائشة العتيبي', '1971-07-18', 'female',
        '+966 512141801', 'patient55@email.com', 'شارع الأمير سلطان، حي السلامة', 'ميدغلف',
        'INS313563',
        'أمراض الكبد',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('94ebb4cc-4a6c-4063-8cea-3130a45e71e4', 'P2024056', 'منى الخالد', '2011-12-04', 'female',
        '+966 517538235', 'patient56@email.com', 'شارع التحلية، حي الزهراء', 'سلامة',
        'INS645754',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('638b6b84-50f9-4861-b001-c86ad2188ef1', 'P2024057', 'مريم الخالد', '1991-10-14', 'female',
        '+966 558171109', 'patient57@email.com', 'شارع الأمير محمد بن عبدالعزيز، حي الأندلس', NULL,
        NULL,
        'أمراض الكلى',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('4cf6838e-3cfd-4469-b010-38442aca0857', 'P2024058', 'سارة المطيري', '1940-09-01', 'female',
        '+966 542676953', 'patient58@email.com', 'شارع الملك فهد، حي الروضة', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'P2024059', 'طارق الأحمد', '1983-10-20', 'male',
        '+966 575005024', 'patient59@email.com', 'شارع الأمير محمد بن عبدالعزيز، حي الأندلس', 'وقاية للتأمين',
        'INS949941',
        'الصرع',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('e3e078df-a3c6-4446-900b-e3abe2ff19ff', 'P2024060', 'نورا الغامدي', '1959-03-14', 'female',
        '+966 562408265', 'patient60@email.com', 'شارع الأمير ماجد، حي الفيصلية', 'أليانز السعودي',
        'INS938932',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', 'P2024061', 'محمد الخالد', '2013-06-18', 'male',
        '+966 582227319', 'patient61@email.com', 'شارع مكة، حي المروة', 'أكسا التعاونية',
        'INS731805',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('bd71e492-b94d-44b1-8c0b-1012177e68d4', 'P2024062', 'خالد الدوسري', '1983-04-07', 'male',
        '+966 545487931', 'patient62@email.com', 'شارع مكة، حي المروة', 'أليانز السعودي',
        'INS998801',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('ae32aab1-387f-4742-a104-9593c8820a09', 'P2024063', 'زياد القحطاني', '1946-03-18', 'male',
        '+966 576474683', 'patient63@email.com', 'شارع الأمير سلطان، حي السلامة', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('b9b53000-347b-499c-9259-a16709d58358', 'P2024064', 'نورا المطيري', '1980-04-01', 'female',
        '+966 595990598', 'patient64@email.com', 'شارع الملك فهد، حي الروضة', NULL,
        NULL,
        'أمراض الكبد',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('5a0072a5-bc9f-40e0-a289-b750dfb2dc87', 'P2024065', 'فيصل الجهني', '1948-11-28', 'male',
        '+966 510959594', 'patient65@email.com', 'شارع الأمير محمد بن عبدالعزيز، حي الأندلس', 'المتحدة للتأمين',
        'INS884848',
        'الربو',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('3bac8559-06ab-4672-8336-9ad2a0cadce0', 'P2024066', 'فيصل الشامي', '1961-05-16', 'male',
        '+966 575430949', 'patient66@email.com', 'شارع التحلية، حي الزهراء', 'وقاية للتأمين',
        'INS479825',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('12e8d048-1f45-4dfe-920b-b3b28507702a', 'P2024067', 'فاطمة الزهراني', '1988-12-04', 'female',
        '+966 559326715', 'patient67@email.com', 'شارع المدينة، حي الصفا', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('9018a956-250f-40ca-be2c-bc6a37ec8b19', 'P2024068', 'ريم الدوسري', '1961-07-11', 'female',
        '+966 579285500', 'patient68@email.com', 'شارع مكة، حي المروة', 'ملاذ للتأمين',
        'INS590532',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('1d6f6927-2e1b-4928-ad7d-4416b021719e', 'P2024069', 'مريم الإبراهيم', '1971-09-20', 'female',
        '+966 549113994', 'patient69@email.com', 'شارع الملك فهد، حي الروضة', 'الدرع العربي',
        'INS826064',
        'أمراض القلب',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'P2024070', 'سعيد السعيد', '1967-08-09', 'male',
        '+966 547734926', 'patient70@email.com', 'شارع المدينة، حي الصفا', 'أكسا التعاونية',
        'INS478983',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('c788722c-3a96-4bd5-9139-33a8d8e65565', 'P2024071', 'وليد المصري', '1984-02-17', 'male',
        '+966 566287814', 'patient71@email.com', 'شارع التحلية، حي الزهراء', 'سلامة',
        'INS395806',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('7ecc8943-b925-4944-8e60-8c0570ad9404', 'P2024072', 'عبدالعزيز الغامدي', '2003-12-09', 'male',
        '+966 597600474', 'patient72@email.com', 'شارع الملك عبدالله، حي النزهة', 'أكسا التعاونية',
        'INS411374',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'P2024073', 'عبير الجهني', '1988-04-20', 'female',
        '+966 515128294', 'patient73@email.com', 'شارع مكة، حي المروة', 'التعاونية للتأمين',
        'INS594710',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('3057ea2c-1fdc-4d82-8e19-282a3566172b', 'P2024074', 'ناصر الدوسري', '1992-09-19', 'male',
        '+966 588516087', 'patient74@email.com', 'شارع الأمير ماجد، حي الفيصلية', 'أكسا التعاونية',
        'INS708462',
        'الصرع',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('875b2d85-4697-4477-9297-f19ffda87d21', 'P2024075', 'سعيد الشامي', '1973-10-02', 'male',
        '+966 538602897', 'patient75@email.com', 'شارع الأمير محمد بن عبدالعزيز، حي الأندلس', 'أليانز السعودي',
        'INS904459',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('060e5ecd-073f-4930-9a27-e7bfcf8245b0', 'P2024076', 'سلمى الخالد', '1947-06-04', 'female',
        '+966 571989054', 'patient76@email.com', 'شارع فلسطين، حي البوادي', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('42324f9a-fcea-4f6d-939b-64ec33f3defa', 'P2024077', 'زياد العمر', '2000-03-05', 'male',
        '+966 589279572', 'patient77@email.com', 'شارع فلسطين، حي البوادي', 'وقاية للتأمين',
        'INS323508',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'P2024078', 'طارق المحمد', '1941-01-08', 'male',
        '+966 591925501', 'patient78@email.com', 'شارع المدينة، حي الصفا', 'الدرع العربي',
        'INS836649',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('5747f021-7809-4e2d-856d-45ebdced300c', 'P2024079', 'رانيا العتيبي', '1964-02-08', 'female',
        '+966 567921025', 'patient79@email.com', 'شارع الملك فهد، حي الروضة', 'أكسا التعاونية',
        'INS852557',
        'حساسية الأسبرين',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('b25049f9-d533-495b-ba24-753998010265', 'P2024080', 'فيصل القحطاني', '1982-04-12', 'male',
        '+966 531146513', 'patient80@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'التعاونية للتأمين',
        'INS488186',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('b7fa9854-934d-4afa-862c-075bd03b04d0', 'P2024081', 'أحمد الإبراهيم', '1946-07-02', 'male',
        '+966 541914854', 'patient81@email.com', 'شارع الملك فهد، حي الروضة', 'وقاية للتأمين',
        'INS918686',
        'أمراض القلب',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('1d7de144-435b-4d30-aab6-6fb43d4ac723', 'P2024082', 'طارق الشهري', '2007-05-02', 'male',
        '+966 522977193', 'patient82@email.com', 'شارع فلسطين، حي البوادي', 'الدرع العربي',
        'INS739653',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('89ef4a50-600e-4ad1-8396-9eb6fa992e60', 'P2024083', 'سلطان الإبراهيم', '1943-05-05', 'male',
        '+966 558520398', 'patient83@email.com', 'شارع المدينة، حي الصفا', 'سلامة',
        'INS155281',
        'حساسية الأسبرين',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e', 'P2024084', 'دلال الخالد', '1943-02-05', 'female',
        '+966 538377470', 'patient84@email.com', 'شارع الملك عبدالله، حي النزهة', 'سلامة',
        'INS334619',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('66d56a9b-17c4-4183-aa15-2db3082c3874', 'P2024085', 'سلطان الزهراني', '1959-01-24', 'male',
        '+966 512562397', 'patient85@email.com', 'شارع المدينة، حي الصفا', 'ميدغلف',
        'INS554023',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('a84f91b9-21de-47fe-941e-023727b5a321', 'P2024086', 'عبدالله الأحمد', '1977-12-27', 'male',
        '+966 591429505', 'patient86@email.com', 'شارع المدينة، حي الصفا', 'أليانز السعودي',
        'INS872387',
        'فقر الدم',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('959023bc-fabf-46ba-8e10-5a218859f611', 'P2024087', 'مريم الجهني', '1943-09-23', 'female',
        '+966 530805617', 'patient87@email.com', 'شارع مكة، حي المروة', 'التعاونية للتأمين',
        'INS526859',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('4beb4577-a28a-4ae6-bf53-400e18edccac', 'P2024088', 'دلال المطيري', '2013-07-25', 'female',
        '+966 536287495', 'patient88@email.com', 'شارع مكة، حي المروة', NULL,
        NULL,
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', 'P2024089', 'سعيد الزهراني', '1991-11-11', 'male',
        '+966 539215766', 'patient89@email.com', 'شارع الأمير ماجد، حي الفيصلية', 'أليانز السعودي',
        'INS155012',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('cffe62fb-dd1c-444d-b911-422ba6f25a81', 'P2024090', 'راشد الشامي', '1995-01-13', 'male',
        '+966 573832910', 'patient90@email.com', 'شارع الملك عبدالله، حي النزهة', NULL,
        NULL,
        'السكري',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('61f80afa-ecc4-436d-918e-04afb7ef1a16', 'P2024091', 'أمل العتيبي', '1962-06-21', 'female',
        '+966 519833359', 'patient91@email.com', 'شارع الملك فهد، حي الروضة', 'التعاونية للتأمين',
        'INS587696',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('7c9db862-5ccd-4652-b053-cedc74f5dac5', 'P2024092', 'وليد المطيري', '2002-08-13', 'male',
        '+966 514641039', 'patient92@email.com', 'شارع الأمير سلطان، حي السلامة', NULL,
        NULL,
        'أمراض الكلى',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('7c87b878-6378-4a47-b464-5dc61e1020ee', 'P2024093', 'هدى العلي', '1950-08-17', 'female',
        '+966 559074217', 'patient93@email.com', 'شارع الأمير سلطان، حي السلامة', 'ميدغلف',
        'INS173813',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('f3c23c7a-cc29-4424-9995-c1e197b8489a', 'P2024094', 'فيصل العلي', '2001-11-15', 'male',
        '+966 587168462', 'patient94@email.com', 'شارع المدينة، حي الصفا', 'سلامة',
        'INS190121',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('97a843e7-13f3-4a2e-9009-2f26025ffc08', 'P2024095', 'عمر المطيري', '2014-04-21', 'male',
        '+966 515012925', 'patient95@email.com', 'شارع فلسطين، حي البوادي', 'أكسا التعاونية',
        'INS141336',
        'حساسية البنسلين',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', 'P2024096', 'سمر القحطاني', '1941-12-22', 'female',
        '+966 532520315', 'patient96@email.com', 'شارع فلسطين، حي البوادي', 'أليانز السعودي',
        'INS870133',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('e9591707-0295-4c79-ae31-86fce4108d2f', 'P2024097', 'حسن الجهني', '1987-06-06', 'male',
        '+966 576350654', 'patient97@email.com', 'شارع مكة، حي المروة', 'سلامة',
        'INS226178',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('28bb748d-3170-4652-87e7-871566ae3e74', 'P2024098', 'مريم الجهني', '1984-09-20', 'female',
        '+966 547810233', 'patient98@email.com', 'شارع الأمير سلطان، حي السلامة', 'أكسا التعاونية',
        'INS750335',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('4756d93c-b914-4fcd-ad22-9374cc1cbba8', 'P2024099', 'ليلى الزهراني', '1991-06-18', 'female',
        '+966 546388545', 'patient99@email.com', 'شارع الملك عبدالعزيز، حي الشاطئ', 'المتحدة للتأمين',
        'INS738355',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender,
                      phone_number, email, address, insurance_provider, insurance_number,
                      important_medical_notes, created_by)
VALUES ('adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'P2024100', 'منصور الجهني', '1989-06-17', 'male',
        '+966 516256375', 'patient100@email.com', 'شارع مكة، حي المروة', 'التعاونية للتأمين',
        'INS682169',
        NULL,
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
-- Additional Appointments and Treatments
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b0d2c21f-58e4-45d5-ab46-43514c4a8bc5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-10-02 17:08:22+03',
        30, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('656b8ef2-031b-44cd-9f08-667b57cf66a9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-08-05 17:08:22+03',
        60, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fa21da7c-6c09-49a9-9036-af34a8007657', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-10-31 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('76010d0c-0912-4879-afd5-67154f182a3f', 'fa21da7c-6c09-49a9-9036-af34a8007657', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        4283.86, 'علاج زراعة الأسنان', '2024-10-31',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f96e820d-5afb-4414-ba06-e6e018d6d1a3', 'fa21da7c-6c09-49a9-9036-af34a8007657', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 46, 'COMPLETED',
        3294.56, 'علاج جسر الأسنان - السن رقم 46', '2024-10-31',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('17b397e4-3f5d-4963-b46e-800197c6dbde', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-22 17:08:22+03',
        30, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('250d3a85-fddc-4ff0-b2fa-47069843ca42', '17b397e4-3f5d-4963-b46e-800197c6dbde', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 21, 'COMPLETED',
        124.69, 'علاج تنظيف الأسنان - السن رقم 21', '2025-03-22',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3c7e9992-5abd-426d-b3db-04fe5136c6f9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-10-27 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('209ab723-d1fd-45f2-bfae-bd49e471e687', '3c7e9992-5abd-426d-b3db-04fe5136c6f9', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        529.21, 'علاج ترميم تجميلي', '2024-10-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('90fe08cb-f8ca-44b2-a8fb-09391d5abb2e', '3c7e9992-5abd-426d-b3db-04fe5136c6f9', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 39, 'COMPLETED',
        4007.94, 'علاج زراعة الأسنان - السن رقم 39', '2024-10-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7e32c8d9-6758-4294-8928-de87df79525a', '3c7e9992-5abd-426d-b3db-04fe5136c6f9', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 27, 'COMPLETED',
        368.56, 'علاج خلع الأسنان - السن رقم 27', '2024-10-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ddf99cb9-42de-47b9-9510-2d7e10a4bf1a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-07-26 17:08:22+03',
        30, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b7558550-ad29-4407-bf7d-e0b32a6cf774', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'aadf80ef-bd99-4ecb-a1ff-653253e68df0', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-11-09 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cdaa73f2-2dbb-4efd-8a2b-718ebade4df7', 'b7558550-ad29-4407-bf7d-e0b32a6cf774', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        2308.51, 'علاج قشرة تجميلية', '2024-11-09',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('41e96999-63de-4016-b360-164f58b5233c', 'b7558550-ad29-4407-bf7d-e0b32a6cf774', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        1327.02, 'علاج تاج الأسنان', '2024-11-09',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6a064c3f-96ab-48c2-b1b3-2a0cecdae8a5', 'b7558550-ad29-4407-bf7d-e0b32a6cf774', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        494.41, 'علاج مثبت تقويم', '2024-11-09',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ddce5e44-d8e2-4db7-a8f1-8f2822868402', 'b7558550-ad29-4407-bf7d-e0b32a6cf774', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 34, 'COMPLETED',
        109.86, 'علاج X_RAY - السن رقم 34', '2024-11-09',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('35d1729c-8408-46a5-a031-32ca39a67725', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '48bb9995-9330-4894-b74a-f24116c225ed', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-09-23 17:08:22+03',
        120, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('579d54cd-c544-4b22-a2f1-e309b1525253', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '48bb9995-9330-4894-b74a-f24116c225ed', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-24 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7177acd4-9efb-444a-8755-c3387cfad920', '579d54cd-c544-4b22-a2f1-e309b1525253', '48bb9995-9330-4894-b74a-f24116c225ed',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1735.01, 'علاج قشرة تجميلية', '2025-05-24',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dae15911-b678-4f7f-bc97-59ab7637d85a', '579d54cd-c544-4b22-a2f1-e309b1525253', '48bb9995-9330-4894-b74a-f24116c225ed',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 13, 'COMPLETED',
        109.38, 'علاج حشو وقائي - السن رقم 13', '2025-05-24',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7cb80e6a-055a-480a-8222-36b6c8e46c64', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-01 17:08:22+03',
        60, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('396dfb34-2b24-4b3e-ab6d-c42f2cfd6a52', '7cb80e6a-055a-480a-8222-36b6c8e46c64', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 27, 'COMPLETED',
        1118.10, 'علاج علاج العصب - السن رقم 27', '2025-02-01',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('08c2b633-f13d-4d7a-b966-2bf09ada7df5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-02-23 17:08:22+03',
        120, 'NO_SHOW', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('08eda016-c4f8-41c4-8629-7ba3bdc36ad4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-10-29 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e13434b2-ec7b-439a-b6ff-ecb716dc2206', '08eda016-c4f8-41c4-8629-7ba3bdc36ad4', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 19, 'COMPLETED',
        935.50, 'علاج تبييض الأسنان - السن رقم 19', '2024-10-29',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2cf20f6a-56aa-4cbb-8796-f1d1afdf3a68', '08eda016-c4f8-41c4-8629-7ba3bdc36ad4', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        460.43, 'علاج علاج اللثة', '2024-10-29',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d325cd24-32b7-4ed0-b611-8164a0eeba0b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-01-20 17:08:22+03',
        60, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e9e9cb11-600d-4afa-8a1a-f7bef6ca46eb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-09-29 17:08:22+03',
        30, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('50bc4470-7578-4c5b-aa34-18261f8fd434', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '517d1071-dde1-4d36-b1ef-5745264bf869', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-07-15 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4a755262-302b-48a7-85a0-f59b3ccb5b05', '50bc4470-7578-4c5b-aa34-18261f8fd434', '517d1071-dde1-4d36-b1ef-5745264bf869',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 18, 'COMPLETED',
        189.20, 'علاج إزالة الجير - السن رقم 18', '2024-07-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e1b1c2aa-414c-4223-b7c9-edd59f06de80', '50bc4470-7578-4c5b-aa34-18261f8fd434', '517d1071-dde1-4d36-b1ef-5745264bf869',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 21, 'COMPLETED',
        2809.10, 'علاج طقم جزئي - السن رقم 21', '2024-07-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('08517a43-4845-4da8-be65-c16bc2841fa8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '517d1071-dde1-4d36-b1ef-5745264bf869', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-05-12 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b4c03a65-dd1d-4814-8138-5183fe09875c', '08517a43-4845-4da8-be65-c16bc2841fa8', '517d1071-dde1-4d36-b1ef-5745264bf869',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        76.31, 'علاج فلورايد', '2025-05-12',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6a637797-df41-458e-8918-5f4ce7a1c674', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '517d1071-dde1-4d36-b1ef-5745264bf869', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-08-04 17:08:22+03',
        60, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('22433e13-2823-4c7f-ac28-04e27ba80d40', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '517d1071-dde1-4d36-b1ef-5745264bf869', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-06-26 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e8f7436e-5a3b-4813-9e69-ee66871b8afd', '22433e13-2823-4c7f-ac28-04e27ba80d40', '517d1071-dde1-4d36-b1ef-5745264bf869',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        407.70, 'علاج مثبت تقويم', '2025-06-26',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('87253822-5c46-48d7-87d0-f0d9a1707434', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '517d1071-dde1-4d36-b1ef-5745264bf869', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-06-27 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8fbd9063-3894-42cf-9f79-0930b40b6f98', '87253822-5c46-48d7-87d0-f0d9a1707434', '517d1071-dde1-4d36-b1ef-5745264bf869',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        140.49, 'علاج حشو وقائي', '2025-06-27',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('65baa316-ae7d-492d-8c97-1eb3cb107b30', '87253822-5c46-48d7-87d0-f0d9a1707434', '517d1071-dde1-4d36-b1ef-5745264bf869',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 42, 'COMPLETED',
        1992.46, 'علاج قشرة تجميلية - السن رقم 42', '2025-06-27',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6767cd56-b5f1-47d4-af76-c2c02f8f7a1c', '87253822-5c46-48d7-87d0-f0d9a1707434', '517d1071-dde1-4d36-b1ef-5745264bf869',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 45, 'COMPLETED',
        7442.44, 'علاج تقويم - السن رقم 45', '2025-06-27',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c4abd7ae-670b-47a4-87dc-01f79b9d2ae9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '009f45fe-3809-464d-96ce-3956e150e456', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-01-02 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('68195a52-c052-43d0-b653-82081ae2f9ec', 'c4abd7ae-670b-47a4-87dc-01f79b9d2ae9', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        355.91, 'علاج خلع الأسنان', '2025-01-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d83d24c5-32e6-4f8e-bda1-43a4847c9c81', 'c4abd7ae-670b-47a4-87dc-01f79b9d2ae9', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1325.60, 'علاج تاج الأسنان', '2025-01-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('be1a7efc-c08b-44ac-ae39-a89ed2bdbb7e', 'c4abd7ae-670b-47a4-87dc-01f79b9d2ae9', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 16, 'COMPLETED',
        1202.06, 'علاج تاج الأسنان - السن رقم 16', '2025-01-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8538b58d-41cc-4152-969c-ab85ec708ed0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '009f45fe-3809-464d-96ce-3956e150e456', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-20 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5ce3eff1-f2bd-4d31-92ed-4f25119edd8e', '8538b58d-41cc-4152-969c-ab85ec708ed0', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 18, 'COMPLETED',
        267.92, 'علاج حشو الأسنان - السن رقم 18', '2025-05-20',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c204825d-b9fe-4c7c-b4e8-38ae9b7e3fb0', '8538b58d-41cc-4152-969c-ab85ec708ed0', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 13, 'COMPLETED',
        3797.48, 'علاج طقم جزئي - السن رقم 13', '2025-05-20',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('74e7175a-024a-4ed6-8720-dfc288cf86ec', '8538b58d-41cc-4152-969c-ab85ec708ed0', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 35, 'COMPLETED',
        1456.09, 'علاج تاج الأسنان - السن رقم 35', '2025-05-20',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1e5908e7-c785-4a8c-be94-560ab3aad763', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '009f45fe-3809-464d-96ce-3956e150e456', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-08-31 17:08:22+03',
        45, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9a9dee6c-41c1-4a9f-90a2-ca9858b4d2bb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '009f45fe-3809-464d-96ce-3956e150e456', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-10-20 17:08:22+03',
        30, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cc42d826-ffed-43cd-89c2-4b1dc4200cd8', '9a9dee6c-41c1-4a9f-90a2-ca9858b4d2bb', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        1477.95, 'علاج تاج الأسنان', '2024-10-20',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fb9c1908-79b7-414f-af4a-033b20d02589', '9a9dee6c-41c1-4a9f-90a2-ca9858b4d2bb', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 12, 'COMPLETED',
        490.60, 'علاج ترميم تجميلي - السن رقم 12', '2024-10-20',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('355edf2e-b5f5-4de8-bb2c-28bdbadabecf', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '009f45fe-3809-464d-96ce-3956e150e456', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-12-10 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d69e6351-9747-472a-8944-fc6738610060', '355edf2e-b5f5-4de8-bb2c-28bdbadabecf', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 46, 'COMPLETED',
        130.93, 'علاج تنظيف الأسنان - السن رقم 46', '2024-12-10',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('28b86a88-533a-47e3-9d69-befd516d1c88', '355edf2e-b5f5-4de8-bb2c-28bdbadabecf', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        376.83, 'علاج علاج اللثة', '2024-12-10',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b23d24b3-4bc4-401e-9841-0d8ce27569f7', '355edf2e-b5f5-4de8-bb2c-28bdbadabecf', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        193.28, 'علاج إزالة الجير', '2024-12-10',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('db4793ff-acc6-4125-bc20-002504a6f149', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '009f45fe-3809-464d-96ce-3956e150e456', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-09-01 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('790dfae8-59cb-430b-9489-f3ccd8fe4cc6', 'db4793ff-acc6-4125-bc20-002504a6f149', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        8518.83, 'علاج تقويم', '2024-09-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('137fe7d0-f4aa-4bc8-9cfd-6b6f89c5103c', 'db4793ff-acc6-4125-bc20-002504a6f149', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        1248.24, 'علاج علاج العصب', '2024-09-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('70ff7a32-776f-4cf0-afbb-8aa5b59f9fdb', 'db4793ff-acc6-4125-bc20-002504a6f149', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 34, 'COMPLETED',
        1707.89, 'علاج تاج الأسنان - السن رقم 34', '2024-09-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('12bb35f7-2545-4983-a17f-9396ec612b16', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '009f45fe-3809-464d-96ce-3956e150e456', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-07-10 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('661007ab-5622-4249-a1f9-f0e01ab8c4b0', '12bb35f7-2545-4983-a17f-9396ec612b16', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 23, 'COMPLETED',
        2784.42, 'علاج جسر الأسنان - السن رقم 23', '2025-07-10',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3bc338f0-b599-4115-8cc9-0fbf3f766fc5', '12bb35f7-2545-4983-a17f-9396ec612b16', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        381.41, 'علاج خلع الأسنان', '2025-07-10',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('605672d0-f752-465f-ab20-e3bfe01aee8d', '12bb35f7-2545-4983-a17f-9396ec612b16', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        144.13, 'علاج تنظيف الأسنان', '2025-07-10',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1cf58315-460a-4a6d-84f0-0212d839d209', '12bb35f7-2545-4983-a17f-9396ec612b16', '009f45fe-3809-464d-96ce-3956e150e456',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        3679.46, 'علاج زراعة الأسنان', '2025-07-10',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('30fde32c-b70c-4261-9eed-6b17cff40abc', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-03-04 17:08:22+03',
        60, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('aed918cf-5df8-4cf2-b92a-526f9288b74a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-12-13 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5866d652-6807-432d-a057-62008ae27008', 'aed918cf-5df8-4cf2-b92a-526f9288b74a', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        466.34, 'علاج مثبت تقويم', '2024-12-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ee20420e-c08a-4f3c-969c-8ab0af36a358', 'aed918cf-5df8-4cf2-b92a-526f9288b74a', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 42, 'COMPLETED',
        190.69, 'علاج إزالة الجير - السن رقم 42', '2024-12-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a012f0fe-9ffd-4afc-983e-a856205a1aa3', 'aed918cf-5df8-4cf2-b92a-526f9288b74a', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 15, 'COMPLETED',
        99.86, 'علاج حشو وقائي - السن رقم 15', '2024-12-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2883babb-1b78-4214-8611-b29c1ba96f5f', 'aed918cf-5df8-4cf2-b92a-526f9288b74a', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        612.70, 'علاج ترميم تجميلي', '2024-12-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('57d090b8-ced7-435b-8014-171ea968d79d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-09 17:08:22+03',
        30, 'NO_SHOW', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('50523a48-8d0f-4279-a8b1-57f552471a71', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-10-21 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4d45c2e5-f8ef-4c81-a65d-652dd31ec7be', '50523a48-8d0f-4279-a8b1-57f552471a71', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 43, 'COMPLETED',
        460.73, 'علاج علاج اللثة - السن رقم 43', '2024-10-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8516f2db-e40f-42f6-b324-2cee6562f456', '50523a48-8d0f-4279-a8b1-57f552471a71', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        356.88, 'علاج علاج اللثة', '2024-10-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('716ab2b0-5da4-49e3-a0d4-9b82aa19c6bb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-08-22 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e05be2d6-7611-41f0-9b4e-0839fb9d6ad8', '716ab2b0-5da4-49e3-a0d4-9b82aa19c6bb', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 40, 'COMPLETED',
        341.92, 'علاج علاج اللثة - السن رقم 40', '2024-08-22',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1cf06f9f-f200-4776-ab75-a9b397d86785', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-31 17:08:22+03',
        90, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e2234df6-b723-4f7f-aa2d-33d5884f10dc', '1cf06f9f-f200-4776-ab75-a9b397d86785', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 24, 'COMPLETED',
        1431.92, 'علاج علاج العصب - السن رقم 24', '2025-05-31',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8b3b6182-e849-44a9-898e-f88feff97e20', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0f85ab34-195f-4d5b-8a27-47b733b7678f', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-10-11 17:08:22+03',
        90, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4ee5b2aa-99b5-42b8-9a85-7023cc7e6f4d', '8b3b6182-e849-44a9-898e-f88feff97e20', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 16, 'COMPLETED',
        1081.32, 'علاج علاج العصب - السن رقم 16', '2024-10-11',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('55fb42f6-268f-4595-94a0-21425a54c8ae', '8b3b6182-e849-44a9-898e-f88feff97e20', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 12, 'COMPLETED',
        334.47, 'علاج حشو الأسنان - السن رقم 12', '2024-10-11',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('584d4b2d-6243-4bd6-a3a5-08f6550a286f', '8b3b6182-e849-44a9-898e-f88feff97e20', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 26, 'COMPLETED',
        418.76, 'علاج علاج اللثة - السن رقم 26', '2024-10-11',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('52fcb9d7-ff80-45b8-94e9-3d8e8fd5e8e1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0f85ab34-195f-4d5b-8a27-47b733b7678f', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-10 17:08:22+03',
        120, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cbd24953-a55f-4023-a459-a443ea572297', '52fcb9d7-ff80-45b8-94e9-3d8e8fd5e8e1', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        121.14, 'علاج تنظيف الأسنان', '2025-03-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cf94a65e-7c34-480e-b0a1-be705ea1ae46', '52fcb9d7-ff80-45b8-94e9-3d8e8fd5e8e1', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        5301.23, 'علاج زراعة الأسنان', '2025-03-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6fb92758-84b2-47b6-a91d-f867880eb41c', '52fcb9d7-ff80-45b8-94e9-3d8e8fd5e8e1', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        434.47, 'علاج مثبت تقويم', '2025-03-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d3a4a399-fbd1-4ffe-abbd-c52bb0b0e3b4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0f85ab34-195f-4d5b-8a27-47b733b7678f', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-05-08 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('470c40ad-e1ee-4399-8342-a1fbd03f7a6c', 'd3a4a399-fbd1-4ffe-abbd-c52bb0b0e3b4', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 11, 'COMPLETED',
        5654.71, 'علاج طقم أسنان - السن رقم 11', '2025-05-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f1106026-9b63-44a7-a620-17bc19c220ae', 'd3a4a399-fbd1-4ffe-abbd-c52bb0b0e3b4', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 43, 'COMPLETED',
        5176.88, 'علاج زراعة الأسنان - السن رقم 43', '2025-05-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('06f951c6-b983-47d0-8026-b737521eae30', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0f85ab34-195f-4d5b-8a27-47b733b7678f', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-26 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('43c027c8-d0de-4f6b-96b8-6e834c1b3713', '06f951c6-b983-47d0-8026-b737521eae30', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 21, 'COMPLETED',
        326.97, 'علاج علاج اللثة - السن رقم 21', '2025-03-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f05e50e4-7c39-46c2-927f-5b8bba59c873', '06f951c6-b983-47d0-8026-b737521eae30', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 24, 'COMPLETED',
        3956.23, 'علاج طقم جزئي - السن رقم 24', '2025-03-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fdb51011-a4c4-4957-b2bb-049e06ce1623', '06f951c6-b983-47d0-8026-b737521eae30', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 24, 'COMPLETED',
        1426.58, 'علاج تاج الأسنان - السن رقم 24', '2025-03-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b4394f8d-f468-4084-be26-ce467b2d2e10', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0f85ab34-195f-4d5b-8a27-47b733b7678f', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-04-19 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('66e746b1-ba46-4ed4-822e-b8c1214ffae4', 'b4394f8d-f468-4084-be26-ce467b2d2e10', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        155.44, 'علاج تنظيف الأسنان', '2025-04-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d91f44dd-c1af-4fef-9d83-68bb94eee020', 'b4394f8d-f468-4084-be26-ce467b2d2e10', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1667.02, 'علاج قشرة تجميلية', '2025-04-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0dcddf0a-8756-4dfa-95a6-91fbb2078d14', 'b4394f8d-f468-4084-be26-ce467b2d2e10', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        95.68, 'علاج فلورايد', '2025-04-19',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('93724dde-21eb-4059-bbdf-212a0e3db46a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0f85ab34-195f-4d5b-8a27-47b733b7678f', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-08-28 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b6c2de82-738d-4801-ab6e-2d033383cdb7', '93724dde-21eb-4059-bbdf-212a0e3db46a', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        3989.42, 'علاج زراعة الأسنان', '2024-08-28',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4936a331-72ed-49d3-bd61-7ed6709fb683', '93724dde-21eb-4059-bbdf-212a0e3db46a', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        108.30, 'علاج حشو وقائي', '2024-08-28',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ec0c42e6-2f3e-47c6-810d-35602c46de91', '93724dde-21eb-4059-bbdf-212a0e3db46a', '0f85ab34-195f-4d5b-8a27-47b733b7678f',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 25, 'COMPLETED',
        8336.42, 'علاج تقويم - السن رقم 25', '2024-08-28',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('34289005-0fd5-4f5a-8e1e-a9395e9854eb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0f85ab34-195f-4d5b-8a27-47b733b7678f', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-05-08 17:08:22+03',
        45, 'CANCELLED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bb77e6c3-9ad3-4399-ad4d-beff084462a1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81c2b2f8-63bb-488c-a276-d6766d4eae8a', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-12-13 17:08:22+03',
        45, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7d5acb5d-6f3a-4b30-99ce-59f5ff14ae5f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81c2b2f8-63bb-488c-a276-d6766d4eae8a', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-11-18 17:08:22+03',
        120, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('167c666d-d733-4017-b968-80ee3eb66e96', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81c2b2f8-63bb-488c-a276-d6766d4eae8a', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-03-17 17:08:22+03',
        45, 'CANCELLED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d575599d-099c-434c-9cda-c0396cbbb6d1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7aa917b9-c53b-4e77-935a-6297112588e6', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-02-02 17:08:22+03',
        30, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d7471c94-4c37-45d3-bf74-da981267a9cd', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7aa917b9-c53b-4e77-935a-6297112588e6', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-08-16 17:08:22+03',
        90, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('454cb8d5-ea8d-4f34-ae3b-ef2b41531615', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7aa917b9-c53b-4e77-935a-6297112588e6', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-02-26 17:08:22+03',
        30, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('43dc7ad3-2dc6-4958-8012-aea36ed2ee30', '454cb8d5-ea8d-4f34-ae3b-ef2b41531615', '7aa917b9-c53b-4e77-935a-6297112588e6',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        73.34, 'علاج فلورايد', '2025-02-26',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f5677e2f-76f2-4b18-9ef5-455f3afaac66', '454cb8d5-ea8d-4f34-ae3b-ef2b41531615', '7aa917b9-c53b-4e77-935a-6297112588e6',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 30, 'COMPLETED',
        554.70, 'علاج مثبت تقويم - السن رقم 30', '2025-02-26',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('314bad0f-697a-4200-9450-2263ebf3ae22', '454cb8d5-ea8d-4f34-ae3b-ef2b41531615', '7aa917b9-c53b-4e77-935a-6297112588e6',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        3635.26, 'علاج طقم جزئي', '2025-02-26',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9f136ec1-e84b-41ca-b42b-6ab1300dac45', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7aa917b9-c53b-4e77-935a-6297112588e6', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-06-09 17:08:22+03',
        30, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('10cd3210-d5d6-4203-8427-185d67d5fc48', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7aa917b9-c53b-4e77-935a-6297112588e6', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-07-27 17:08:22+03',
        30, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c1e68fca-b8d2-4aef-8392-0f23d3f0b435', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7aa917b9-c53b-4e77-935a-6297112588e6', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-05-28 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('58fedf38-e8bf-4cc4-b4cf-bd4f93026338', 'c1e68fca-b8d2-4aef-8392-0f23d3f0b435', '7aa917b9-c53b-4e77-935a-6297112588e6',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        136.54, 'علاج حشو وقائي', '2025-05-28',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('90a136f3-1fe3-4ab2-a969-5886463d7d77', 'c1e68fca-b8d2-4aef-8392-0f23d3f0b435', '7aa917b9-c53b-4e77-935a-6297112588e6',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 24, 'COMPLETED',
        366.45, 'علاج خلع الأسنان - السن رقم 24', '2025-05-28',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bb65e34e-1453-478c-ba26-2d036504bb15', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-09-29 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4fe57ebf-8878-4370-a71d-2a4b39d12f4b', 'bb65e34e-1453-478c-ba26-2d036504bb15', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 38, 'COMPLETED',
        86.31, 'علاج X_RAY - السن رقم 38', '2024-09-29',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bf0b2687-17f6-461b-890a-56dd4ae111e3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-28 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('310f3859-cf76-4d47-8806-e022334c49ab', 'bf0b2687-17f6-461b-890a-56dd4ae111e3', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        3385.64, 'علاج جسر الأسنان', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a113133e-42cc-46db-9791-aca97a55bf34', 'bf0b2687-17f6-461b-890a-56dd4ae111e3', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        709.06, 'علاج ترميم تجميلي', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ab9f668f-eef8-44a2-9a5d-5920c4d61f3e', 'bf0b2687-17f6-461b-890a-56dd4ae111e3', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        3057.37, 'علاج جسر الأسنان', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e4bd2a13-19a3-452f-8278-52b3369523a9', 'bf0b2687-17f6-461b-890a-56dd4ae111e3', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        219.22, 'علاج إزالة الجير', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e31c0cba-d2f5-45c3-bfce-0a7a4d85da7b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-06-27 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a7eb4250-f3d5-4032-b808-cadb667b4fee', 'e31c0cba-d2f5-45c3-bfce-0a7a4d85da7b', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 32, 'COMPLETED',
        2838.98, 'علاج جسر الأسنان - السن رقم 32', '2025-06-27',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('55188935-83fd-43d4-b734-9c7f4e2cb855', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-07-31 17:08:22+03',
        90, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('816dbff2-d41c-45e1-a814-452dabb3ee04', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-12-14 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('31ad9bba-dde4-49d6-9105-d9bab6c9bca6', '816dbff2-d41c-45e1-a814-452dabb3ee04', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 42, 'COMPLETED',
        1498.61, 'علاج تاج الأسنان - السن رقم 42', '2024-12-14',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('86688e8e-5ddc-46ab-acfc-3d47e2173848', '816dbff2-d41c-45e1-a814-452dabb3ee04', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 34, 'COMPLETED',
        7896.63, 'علاج تقويم - السن رقم 34', '2024-12-14',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('618d5bf6-ee9c-423f-aaf5-6b7bcb987816', '816dbff2-d41c-45e1-a814-452dabb3ee04', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        701.06, 'علاج ترميم تجميلي', '2024-12-14',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('db6c4c21-cf01-4a9e-98fe-a84b197578e9', '816dbff2-d41c-45e1-a814-452dabb3ee04', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 40, 'COMPLETED',
        223.16, 'علاج إزالة الجير - السن رقم 40', '2024-12-14',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('98aa714c-a75c-4b7f-bddf-91624f2a7edf', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-09-29 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c435e455-9cf8-464b-a846-02cc4cc12045', '98aa714c-a75c-4b7f-bddf-91624f2a7edf', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 32, 'COMPLETED',
        339.74, 'علاج حشو الأسنان - السن رقم 32', '2024-09-29',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ea0289c5-dcf0-4415-b811-beb0a384dab0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-01-27 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a6f712de-a04e-4466-8848-7eec74143413', 'ea0289c5-dcf0-4415-b811-beb0a384dab0', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 28, 'COMPLETED',
        342.27, 'علاج حشو الأسنان - السن رقم 28', '2025-01-27',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1fe86e8a-f6a6-47a5-87b1-5299a07935d4', 'ea0289c5-dcf0-4415-b811-beb0a384dab0', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        109.36, 'علاج X_RAY', '2025-01-27',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('beb485b8-b010-4159-b11f-54510ca15a9f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-11-10 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('527016ce-af79-44e6-bc4c-43e8329dcccc', 'beb485b8-b010-4159-b11f-54510ca15a9f', '33208c0c-cf5f-4fd4-9091-a6247d44f026',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        1218.67, 'علاج تاج الأسنان', '2024-11-10',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ade04cee-6237-4c54-a54d-7993369edbf0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bba0776-a012-4b92-90f4-765a0ee510d6', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-10-03 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8d797277-d65b-479c-b0de-fb15ae2b5cf4', 'ade04cee-6237-4c54-a54d-7993369edbf0', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        344.11, 'علاج خلع الأسنان', '2024-10-03',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('40ad278e-3d61-4e8a-ab6f-a5aa7b80ce36', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bba0776-a012-4b92-90f4-765a0ee510d6', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-02-16 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f07c3b30-5441-4691-9d4b-ccaabd44faac', '40ad278e-3d61-4e8a-ab6f-a5aa7b80ce36', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        2977.58, 'علاج طقم جزئي', '2025-02-16',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6cd59d7d-1803-4ad6-b379-65aac1a30454', '40ad278e-3d61-4e8a-ab6f-a5aa7b80ce36', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 18, 'COMPLETED',
        122.22, 'علاج حشو وقائي - السن رقم 18', '2025-02-16',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('65da636a-3d3b-4eda-acb5-51f5b30c8cf4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bba0776-a012-4b92-90f4-765a0ee510d6', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-11-08 17:08:22+03',
        30, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('32e9b554-c304-4a7c-ad7c-0570c30cbd6b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bba0776-a012-4b92-90f4-765a0ee510d6', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-10-26 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('097046a9-1f28-417f-83af-d1dd488b33cc', '32e9b554-c304-4a7c-ad7c-0570c30cbd6b', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        2278.37, 'علاج قشرة تجميلية', '2024-10-26',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('281023f5-3a54-4dc8-9d27-2caca25ca319', '32e9b554-c304-4a7c-ad7c-0570c30cbd6b', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        408.97, 'علاج خلع الأسنان', '2024-10-26',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8462e4fd-7f56-4d45-b7c8-bcf554f3d640', '32e9b554-c304-4a7c-ad7c-0570c30cbd6b', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 12, 'COMPLETED',
        95.42, 'علاج فلورايد - السن رقم 12', '2024-10-26',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7e8135d8-4d40-4e00-b142-42606e702066', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bba0776-a012-4b92-90f4-765a0ee510d6', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-09-25 17:08:22+03',
        60, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('397e89a7-0238-4134-ac2f-70b0d8ec3dc4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bba0776-a012-4b92-90f4-765a0ee510d6', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-12-09 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('55618580-25fd-4516-85b4-f5a43d9eead7', '397e89a7-0238-4134-ac2f-70b0d8ec3dc4', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        423.58, 'علاج خلع الأسنان', '2024-12-09',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e84e1a1d-8750-462d-a96b-89aa6f6e3fd4', '397e89a7-0238-4134-ac2f-70b0d8ec3dc4', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 47, 'COMPLETED',
        109.34, 'علاج استشارة - السن رقم 47', '2024-12-09',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('45e26d0b-6768-443e-9df6-f5fc614dacf8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bba0776-a012-4b92-90f4-765a0ee510d6', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-03-15 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5ed504c0-bd66-4454-82ee-9fb0db647d55', '45e26d0b-6768-443e-9df6-f5fc614dacf8', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        174.77, 'علاج تنظيف الأسنان', '2025-03-15',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8711b41e-5968-4011-8b1d-b27e56da43d7', '45e26d0b-6768-443e-9df6-f5fc614dacf8', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 45, 'COMPLETED',
        554.33, 'علاج ترميم تجميلي - السن رقم 45', '2025-03-15',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2027f1d6-559e-48e8-8c9c-a229a6d0263c', '45e26d0b-6768-443e-9df6-f5fc614dacf8', '3bba0776-a012-4b92-90f4-765a0ee510d6',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 22, 'COMPLETED',
        5348.99, 'علاج طقم أسنان - السن رقم 22', '2025-03-15',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('545f62f1-a912-489f-b97d-906d6d9fe86c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '51739c46-139f-499c-bec0-720560313310', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-07-28 17:08:22+03',
        60, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5011600a-991a-4b94-8eb5-3be6bd50144a', '545f62f1-a912-489f-b97d-906d6d9fe86c', '51739c46-139f-499c-bec0-720560313310',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 11, 'COMPLETED',
        189.99, 'علاج إزالة الجير - السن رقم 11', '2024-07-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a3631860-7eb0-408f-8297-a66648e3aaae', '545f62f1-a912-489f-b97d-906d6d9fe86c', '51739c46-139f-499c-bec0-720560313310',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 14, 'COMPLETED',
        272.60, 'علاج حشو الأسنان - السن رقم 14', '2024-07-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('08372503-8c55-4f6c-89be-4ac6255d4ceb', '545f62f1-a912-489f-b97d-906d6d9fe86c', '51739c46-139f-499c-bec0-720560313310',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 34, 'COMPLETED',
        84.82, 'علاج استشارة - السن رقم 34', '2024-07-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8bd148c1-c5c2-410f-997a-124031690a9d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '51739c46-139f-499c-bec0-720560313310', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-06-06 17:08:22+03',
        30, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7a2a7bfe-2577-4abf-8458-dda7dadd7302', '8bd148c1-c5c2-410f-997a-124031690a9d', '51739c46-139f-499c-bec0-720560313310',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 31, 'COMPLETED',
        8639.94, 'علاج تقويم - السن رقم 31', '2025-06-06',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4ccffa67-f888-484f-ab6e-8aa0e5d4e6bd', '8bd148c1-c5c2-410f-997a-124031690a9d', '51739c46-139f-499c-bec0-720560313310',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 26, 'COMPLETED',
        705.08, 'علاج ترميم تجميلي - السن رقم 26', '2025-06-06',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('688564cf-0f55-4225-bdd2-4269c16e0a2e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cb4208fe-0afc-4844-80c5-e437eda54c4d', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-02-23 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bbeb1559-836c-41b2-bb56-e16f331884cf', '688564cf-0f55-4225-bdd2-4269c16e0a2e', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        368.04, 'علاج خلع الأسنان', '2025-02-23',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c001836c-8ad3-4259-b034-09c3d0c73b8b', '688564cf-0f55-4225-bdd2-4269c16e0a2e', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        135.41, 'علاج تنظيف الأسنان', '2025-02-23',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7a4b292a-ce29-4ceb-869f-f6768778c8a6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cb4208fe-0afc-4844-80c5-e437eda54c4d', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-04 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f212a5f1-fcfc-47a1-9b20-65e2519ae69b', '7a4b292a-ce29-4ceb-869f-f6768778c8a6', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 34, 'COMPLETED',
        87.52, 'علاج فلورايد - السن رقم 34', '2024-09-04',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('da0f1920-9bcd-424e-b4b5-2c4dbfa6466c', '7a4b292a-ce29-4ceb-869f-f6768778c8a6', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 34, 'COMPLETED',
        213.78, 'علاج إزالة الجير - السن رقم 34', '2024-09-04',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6ade2c97-011c-4a29-a2a1-a654b27511ab', '7a4b292a-ce29-4ceb-869f-f6768778c8a6', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 16, 'COMPLETED',
        4158.03, 'علاج زراعة الأسنان - السن رقم 16', '2024-09-04',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fad03c4b-fcbd-44e9-9d0e-f6e05d54f703', '7a4b292a-ce29-4ceb-869f-f6768778c8a6', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 22, 'COMPLETED',
        207.50, 'علاج إزالة الجير - السن رقم 22', '2024-09-04',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('33885417-eaa1-4ff3-8ed2-0eb8c891e75e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cb4208fe-0afc-4844-80c5-e437eda54c4d', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-04-26 17:08:22+03',
        60, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2dc14e4e-0a74-4bdd-a0e5-788e39763a19', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cb4208fe-0afc-4844-80c5-e437eda54c4d', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-13 17:08:22+03',
        90, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d51a839a-f70d-420e-bfec-8dc4dfeb026e', '2dc14e4e-0a74-4bdd-a0e5-788e39763a19', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 29, 'COMPLETED',
        923.50, 'علاج تبييض الأسنان - السن رقم 29', '2025-05-13',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('569f0104-13a9-4884-aa50-cdc7866ccecc', '2dc14e4e-0a74-4bdd-a0e5-788e39763a19', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 25, 'COMPLETED',
        5006.10, 'علاج زراعة الأسنان - السن رقم 25', '2025-05-13',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2d32f67b-6785-4230-a4de-32153d815168', '2dc14e4e-0a74-4bdd-a0e5-788e39763a19', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        159.91, 'علاج تنظيف الأسنان', '2025-05-13',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3cdac811-91dd-4bb8-bdde-9a5d819634ef', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3d209091-82a5-484c-91f7-6dbaf91154b1', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-08-16 17:08:22+03',
        90, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3adaffc2-77ca-4a9a-91a1-ab963cf4daf1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3d209091-82a5-484c-91f7-6dbaf91154b1', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-08-04 17:08:22+03',
        90, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('15025fb4-c343-40f6-ba60-d1a240fe4965', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3d209091-82a5-484c-91f7-6dbaf91154b1', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-10-16 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('23f9aad8-7f57-459b-ac14-e257c9d92f48', '15025fb4-c343-40f6-ba60-d1a240fe4965', '3d209091-82a5-484c-91f7-6dbaf91154b1',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 19, 'COMPLETED',
        3342.24, 'علاج طقم جزئي - السن رقم 19', '2024-10-16',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e5a08cdf-5ed7-4065-9219-8ae341f072dd', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3d209091-82a5-484c-91f7-6dbaf91154b1', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-06-20 17:08:22+03',
        90, 'NO_SHOW', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1e56cab5-3004-4016-8599-4bc111a101ff', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '74037d63-ac9e-4428-860f-3c0a0504a40c', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-09-13 17:08:22+03',
        90, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5a0cdeef-02e5-4b68-9e66-05de818e840d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '74037d63-ac9e-4428-860f-3c0a0504a40c', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-12 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('087ccf25-bf44-4749-9adb-42f4a435942b', '5a0cdeef-02e5-4b68-9e66-05de818e840d', '74037d63-ac9e-4428-860f-3c0a0504a40c',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 15, 'COMPLETED',
        432.26, 'علاج خلع الأسنان - السن رقم 15', '2025-03-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('32adc463-4ebf-4914-8a00-1d1d70057e88', '5a0cdeef-02e5-4b68-9e66-05de818e840d', '74037d63-ac9e-4428-860f-3c0a0504a40c',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 42, 'COMPLETED',
        854.99, 'علاج تبييض الأسنان - السن رقم 42', '2025-03-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('51697597-b940-4d73-8d68-b0c08a0b144e', '5a0cdeef-02e5-4b68-9e66-05de818e840d', '74037d63-ac9e-4428-860f-3c0a0504a40c',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 27, 'COMPLETED',
        1267.67, 'علاج تاج الأسنان - السن رقم 27', '2025-03-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4f8141f4-dff8-46e2-bc75-367e7123f3b6', '5a0cdeef-02e5-4b68-9e66-05de818e840d', '74037d63-ac9e-4428-860f-3c0a0504a40c',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 34, 'COMPLETED',
        548.24, 'علاج مثبت تقويم - السن رقم 34', '2025-03-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('4a30e50c-e43a-4f5e-980c-ab367916d124', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '998dbd19-94b9-4c62-9df1-202d06aa254a', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-06-08 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c7502b98-ebc8-4762-927a-42c4d403e895', '4a30e50c-e43a-4f5e-980c-ab367916d124', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 28, 'COMPLETED',
        128.66, 'علاج تنظيف الأسنان - السن رقم 28', '2025-06-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d9b54715-ddff-4045-8aad-7c4408a88786', '4a30e50c-e43a-4f5e-980c-ab367916d124', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 35, 'COMPLETED',
        722.08, 'علاج تبييض الأسنان - السن رقم 35', '2025-06-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3311da24-71c8-47d2-9104-6f7bb1d9ffb6', '4a30e50c-e43a-4f5e-980c-ab367916d124', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        5829.85, 'علاج طقم أسنان', '2025-06-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9e5ee7a1-4167-459a-97f5-c6a420d68a86', '4a30e50c-e43a-4f5e-980c-ab367916d124', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 11, 'COMPLETED',
        458.84, 'علاج مثبت تقويم - السن رقم 11', '2025-06-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('52d400c6-9a01-4662-ad4c-2c04f2b5240b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '998dbd19-94b9-4c62-9df1-202d06aa254a', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-06-27 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7e9b5dc3-0aa0-49c7-a87c-9a74f2b8d23d', '52d400c6-9a01-4662-ad4c-2c04f2b5240b', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        709.90, 'علاج تبييض الأسنان', '2025-06-27',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('196b9eec-0af0-4840-8425-9e3065c83fb3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '998dbd19-94b9-4c62-9df1-202d06aa254a', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-12-15 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e1d9a94a-73c4-41a9-8ea8-2f23f008b9b0', '196b9eec-0af0-4840-8425-9e3065c83fb3', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 14, 'COMPLETED',
        846.41, 'علاج تبييض الأسنان - السن رقم 14', '2024-12-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('19e8719f-9a1b-440b-8d9f-123cd104ddf2', '196b9eec-0af0-4840-8425-9e3065c83fb3', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 13, 'COMPLETED',
        361.11, 'علاج علاج اللثة - السن رقم 13', '2024-12-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7f374c67-64f3-49e4-8d3f-67b070b1126f', '196b9eec-0af0-4840-8425-9e3065c83fb3', '998dbd19-94b9-4c62-9df1-202d06aa254a',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 24, 'COMPLETED',
        3521.81, 'علاج طقم جزئي - السن رقم 24', '2024-12-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ec0ab922-6dea-4b58-8573-dc1866965c82', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c85699c1-ffc2-483b-8999-cacc91b76fa1', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-08-18 17:08:22+03',
        90, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1025777f-bcae-4265-a49c-d0baac68ce39', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c85699c1-ffc2-483b-8999-cacc91b76fa1', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-08-08 17:08:22+03',
        90, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0d8ab012-dcd4-4396-b217-8491fa64ac80', '1025777f-bcae-4265-a49c-d0baac68ce39', 'c85699c1-ffc2-483b-8999-cacc91b76fa1',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        179.87, 'علاج إزالة الجير', '2024-08-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d090de26-ac28-4a11-b34e-25c1ac524f9d', '1025777f-bcae-4265-a49c-d0baac68ce39', 'c85699c1-ffc2-483b-8999-cacc91b76fa1',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 14, 'COMPLETED',
        410.04, 'علاج مثبت تقويم - السن رقم 14', '2024-08-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a572d96f-07f9-4ec3-bc24-effa3c2756f5', '1025777f-bcae-4265-a49c-d0baac68ce39', 'c85699c1-ffc2-483b-8999-cacc91b76fa1',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        5645.26, 'علاج طقم أسنان', '2024-08-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('81c61e1f-890f-45fd-8666-fda1444e50f6', '1025777f-bcae-4265-a49c-d0baac68ce39', 'c85699c1-ffc2-483b-8999-cacc91b76fa1',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        100.12, 'علاج X_RAY', '2024-08-08',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3468ef0a-9582-437b-abe5-993f44804b18', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '75d43ba8-1c27-4d1b-99e6-5ce77cf08750', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-01-18 17:08:22+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('88038dc1-24a3-4d03-94c3-39dc51ee7635', '3468ef0a-9582-437b-abe5-993f44804b18', '75d43ba8-1c27-4d1b-99e6-5ce77cf08750',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 44, 'COMPLETED',
        167.25, 'علاج تنظيف الأسنان - السن رقم 44', '2025-01-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('75e36ed9-77d9-4e83-a758-86e6ab1ce7b8', '3468ef0a-9582-437b-abe5-993f44804b18', '75d43ba8-1c27-4d1b-99e6-5ce77cf08750',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 26, 'COMPLETED',
        2854.38, 'علاج جسر الأسنان - السن رقم 26', '2025-01-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7dbd2b90-37e3-4d6e-80bf-ae680caa9659', '3468ef0a-9582-437b-abe5-993f44804b18', '75d43ba8-1c27-4d1b-99e6-5ce77cf08750',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 14, 'COMPLETED',
        1235.55, 'علاج علاج العصب - السن رقم 14', '2025-01-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5e681217-97c4-4932-a4c5-5ba4e21b0c43', '3468ef0a-9582-437b-abe5-993f44804b18', '75d43ba8-1c27-4d1b-99e6-5ce77cf08750',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        3287.13, 'علاج جسر الأسنان', '2025-01-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1fe2ea33-90ad-4f14-8503-2dc4333e4441', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '75d43ba8-1c27-4d1b-99e6-5ce77cf08750', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-02-08 17:08:22+03',
        90, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('15c967b0-e4f8-4c6a-b017-c723f9ab81a3', '1fe2ea33-90ad-4f14-8503-2dc4333e4441', '75d43ba8-1c27-4d1b-99e6-5ce77cf08750',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        143.46, 'علاج حشو وقائي', '2025-02-08',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2a6d19bc-7fb6-4c90-9f2a-46c6622bbb21', '1fe2ea33-90ad-4f14-8503-2dc4333e4441', '75d43ba8-1c27-4d1b-99e6-5ce77cf08750',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 22, 'COMPLETED',
        668.82, 'علاج تبييض الأسنان - السن رقم 22', '2025-02-08',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9033cae5-330e-4f76-8265-5a0de72525cb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '75d43ba8-1c27-4d1b-99e6-5ce77cf08750', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-07-13 17:08:22+03',
        60, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c5a603e0-3c48-4044-a977-997e3c821d3f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '75d43ba8-1c27-4d1b-99e6-5ce77cf08750', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-07-30 17:08:22+03',
        120, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a61324e0-ddf6-4e76-acb5-031375248b37', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '536663f3-678d-4bc4-8f75-c188152194d9', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-08-06 17:08:22+03',
        45, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('23363c46-8691-4c36-9a14-82551ae0924f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '536663f3-678d-4bc4-8f75-c188152194d9', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-07-30 17:08:22+03',
        90, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('54f18070-2aae-4efb-9237-032d50c37d04', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '536663f3-678d-4bc4-8f75-c188152194d9', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-09-30 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fb99a81d-2153-43dc-acd3-359cbed75245', '54f18070-2aae-4efb-9237-032d50c37d04', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 17, 'COMPLETED',
        4022.08, 'علاج زراعة الأسنان - السن رقم 17', '2024-09-30',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('999221f1-8d5b-4acf-b9a4-7cd22d8c9b81', '54f18070-2aae-4efb-9237-032d50c37d04', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 26, 'COMPLETED',
        1798.81, 'علاج تاج الأسنان - السن رقم 26', '2024-09-30',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6f61b25f-77c5-4a96-b73e-b76accc52436', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '536663f3-678d-4bc4-8f75-c188152194d9', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-07-12 17:08:22+03',
        30, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('51032a5d-0fa8-4156-9e4a-6075d4482599', '6f61b25f-77c5-4a96-b73e-b76accc52436', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 43, 'COMPLETED',
        347.94, 'علاج علاج اللثة - السن رقم 43', '2024-07-12',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fa529c9c-33b7-4ab0-abea-834778967d64', '6f61b25f-77c5-4a96-b73e-b76accc52436', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        656.48, 'علاج ترميم تجميلي', '2024-07-12',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3293bf39-5c64-49f0-a093-d1c82f1ae391', '6f61b25f-77c5-4a96-b73e-b76accc52436', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 31, 'COMPLETED',
        248.24, 'علاج حشو الأسنان - السن رقم 31', '2024-07-12',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2716f80a-31d4-457a-a852-06af206b5127', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '536663f3-678d-4bc4-8f75-c188152194d9', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-09-21 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0152fa3f-c070-49f2-a36c-eb20d0d7605b', '2716f80a-31d4-457a-a852-06af206b5127', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        331.93, 'علاج حشو الأسنان', '2024-09-21',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9af14b58-63ba-4b38-aff1-521712494f41', '2716f80a-31d4-457a-a852-06af206b5127', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        3546.62, 'علاج طقم جزئي', '2024-09-21',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fab085e1-795e-45f1-8af9-884e029cd48b', '2716f80a-31d4-457a-a852-06af206b5127', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        157.21, 'علاج تنظيف الأسنان', '2024-09-21',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a58e692b-6b01-4f4b-bb57-116d50500a7e', '2716f80a-31d4-457a-a852-06af206b5127', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        318.81, 'علاج حشو الأسنان', '2024-09-21',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0333778d-45e4-4185-8eda-374b7184fa85', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '536663f3-678d-4bc4-8f75-c188152194d9', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-11-10 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9172a194-64e9-4c4c-b629-ada2fe321df3', '0333778d-45e4-4185-8eda-374b7184fa85', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        286.30, 'علاج حشو الأسنان', '2024-11-10',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('106d6360-1a25-4ce3-9b32-5da5a00fa3e4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '536663f3-678d-4bc4-8f75-c188152194d9', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-03-22 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4668399a-da23-47de-a05c-dd32b1881690', '106d6360-1a25-4ce3-9b32-5da5a00fa3e4', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 37, 'COMPLETED',
        86.98, 'علاج استشارة - السن رقم 37', '2025-03-22',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('97572e1c-94d3-4c05-ba0b-97e6a5e17225', '106d6360-1a25-4ce3-9b32-5da5a00fa3e4', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 17, 'COMPLETED',
        5296.64, 'علاج طقم أسنان - السن رقم 17', '2025-03-22',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2ce95e3b-9f5d-439c-824f-944f5f62481a', '106d6360-1a25-4ce3-9b32-5da5a00fa3e4', '536663f3-678d-4bc4-8f75-c188152194d9',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 41, 'COMPLETED',
        6447.30, 'علاج تقويم - السن رقم 41', '2025-03-22',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('399101c8-573c-4e73-9c81-fa2bdeef8575', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f9e60397-26d4-4c86-98a7-d906d9359501', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-02-11 17:08:22+03',
        120, 'NO_SHOW', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('58a7d5b0-b7c8-4d70-a13a-956b843eb01e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f9e60397-26d4-4c86-98a7-d906d9359501', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-02-20 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('63645f17-074a-4913-9724-d207efa59bfb', '58a7d5b0-b7c8-4d70-a13a-956b843eb01e', 'f9e60397-26d4-4c86-98a7-d906d9359501',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        1693.70, 'علاج تاج الأسنان', '2025-02-20',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('95b80c12-b8e8-49bb-87d1-b47bfd57d86a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f9e60397-26d4-4c86-98a7-d906d9359501', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-10-29 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e9585c42-4124-415a-b5ab-59b905bed70a', '95b80c12-b8e8-49bb-87d1-b47bfd57d86a', 'f9e60397-26d4-4c86-98a7-d906d9359501',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        367.95, 'علاج خلع الأسنان', '2024-10-29',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('84eab78d-4c49-4f39-ad43-9fbbc887deab', '95b80c12-b8e8-49bb-87d1-b47bfd57d86a', 'f9e60397-26d4-4c86-98a7-d906d9359501',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        401.57, 'علاج علاج اللثة', '2024-10-29',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7454bf83-f8eb-4346-a008-3731bfd33c7b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f9e60397-26d4-4c86-98a7-d906d9359501', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-12-22 17:08:22+03',
        90, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('13811f39-4d28-4bfb-a255-01c28233d092', '7454bf83-f8eb-4346-a008-3731bfd33c7b', 'f9e60397-26d4-4c86-98a7-d906d9359501',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        708.63, 'علاج ترميم تجميلي', '2024-12-22',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('38641366-7655-4a59-bcd3-bfe757d554ff', '7454bf83-f8eb-4346-a008-3731bfd33c7b', 'f9e60397-26d4-4c86-98a7-d906d9359501',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 13, 'COMPLETED',
        120.55, 'علاج تنظيف الأسنان - السن رقم 13', '2024-12-22',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('259cb7be-0ed8-4580-9d14-975e1e5e4881', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f9e60397-26d4-4c86-98a7-d906d9359501', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-07-14 17:08:22+03',
        120, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('686e01d5-a690-478b-9f1d-add50a8381ab', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f9e60397-26d4-4c86-98a7-d906d9359501', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-12-01 17:08:22+03',
        30, 'NO_SHOW', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('240988b3-f065-401d-ad90-f96847442a17', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '14d28cf4-88b5-41f5-98b2-926cb0f94ae9', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-11-23 17:08:22+03',
        90, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('81dae164-33c8-42c1-9ad3-c323d9b492b8', '240988b3-f065-401d-ad90-f96847442a17', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        3117.06, 'علاج طقم جزئي', '2024-11-23',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b9cbf2cc-9421-4e97-bff5-80d7a2732bda', '240988b3-f065-401d-ad90-f96847442a17', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        87.31, 'علاج استشارة', '2024-11-23',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8568bbf1-1435-4ba3-8af8-70ebd2e0d8bd', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '14d28cf4-88b5-41f5-98b2-926cb0f94ae9', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-04-26 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b9ffd9ca-78fe-46d5-a60b-39c01ca79abf', '8568bbf1-1435-4ba3-8af8-70ebd2e0d8bd', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 21, 'COMPLETED',
        110.93, 'علاج X_RAY - السن رقم 21', '2025-04-26',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a0a3623a-d8f5-4a45-9267-d3f3690ee174', '8568bbf1-1435-4ba3-8af8-70ebd2e0d8bd', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        1652.70, 'علاج قشرة تجميلية', '2025-04-26',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('633222de-e514-4544-adf0-c31ccc7181c8', '8568bbf1-1435-4ba3-8af8-70ebd2e0d8bd', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        5298.78, 'علاج زراعة الأسنان', '2025-04-26',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('82218020-62bf-47f9-b9c4-b23f0e1eb8c3', '8568bbf1-1435-4ba3-8af8-70ebd2e0d8bd', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        6953.43, 'علاج تقويم', '2025-04-26',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7d649a1c-3af4-4559-a046-7dc90f27fab4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '14d28cf4-88b5-41f5-98b2-926cb0f94ae9', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-29 17:08:22+03',
        120, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7d621560-cbda-4659-a078-8bc52398134c', '7d649a1c-3af4-4559-a046-7dc90f27fab4', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        542.43, 'علاج ترميم تجميلي', '2025-05-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4253c050-0b68-4ff8-b676-84058d17d332', '7d649a1c-3af4-4559-a046-7dc90f27fab4', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        98.02, 'علاج استشارة', '2025-05-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3873ee89-79f8-4fab-9f65-a112fee8fc23', '7d649a1c-3af4-4559-a046-7dc90f27fab4', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 17, 'COMPLETED',
        82.36, 'علاج X_RAY - السن رقم 17', '2025-05-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fa8ec2d0-a561-4f75-bd8c-17ba6fb6ee0a', '7d649a1c-3af4-4559-a046-7dc90f27fab4', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 41, 'COMPLETED',
        5050.09, 'علاج طقم أسنان - السن رقم 41', '2025-05-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('552fd33f-2ab8-4065-8ecc-28ec612e4e2f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '688b05f6-aa01-4e29-8e46-92af09f92a12', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-08-02 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('573f6e2f-6d27-451a-a085-d9ed0c9ee82e', '552fd33f-2ab8-4065-8ecc-28ec612e4e2f', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 15, 'COMPLETED',
        1282.24, 'علاج تاج الأسنان - السن رقم 15', '2024-08-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d332cf92-b11b-4438-841c-db2c06bd6ae9', '552fd33f-2ab8-4065-8ecc-28ec612e4e2f', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 22, 'COMPLETED',
        336.48, 'علاج خلع الأسنان - السن رقم 22', '2024-08-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2b235d62-ab36-4cc8-acc4-e0eecd8fa396', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '688b05f6-aa01-4e29-8e46-92af09f92a12', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-06 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fc5b5791-328e-4d75-a539-8e753a73dc8a', '2b235d62-ab36-4cc8-acc4-e0eecd8fa396', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 26, 'COMPLETED',
        1889.35, 'علاج قشرة تجميلية - السن رقم 26', '2024-09-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dc7a3c9f-ce68-4286-aad5-134534878cdc', '2b235d62-ab36-4cc8-acc4-e0eecd8fa396', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 44, 'COMPLETED',
        65.39, 'علاج فلورايد - السن رقم 44', '2024-09-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('87c00250-bd0c-4234-a158-aa2afad56e12', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '688b05f6-aa01-4e29-8e46-92af09f92a12', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-07-24 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('10dd1714-d3c5-4c37-b8d3-cc601408b029', '87c00250-bd0c-4234-a158-aa2afad56e12', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        511.06, 'علاج ترميم تجميلي', '2024-07-24',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('05c2e228-cc69-440e-9c34-5c05b5e704d6', '87c00250-bd0c-4234-a158-aa2afad56e12', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 47, 'COMPLETED',
        184.43, 'علاج إزالة الجير - السن رقم 47', '2024-07-24',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1e5d2451-ee78-4f2b-bd1f-2996caae1074', '87c00250-bd0c-4234-a158-aa2afad56e12', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        338.28, 'علاج علاج اللثة', '2024-07-24',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fb56d13f-154e-4345-a998-71ec4f21f7ed', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '688b05f6-aa01-4e29-8e46-92af09f92a12', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-12-23 17:08:22+03',
        45, 'CANCELLED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('dfaf8e71-c883-49e5-8e7e-ff0fa4ccf22a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'fc2bd317-69b1-405e-9094-f85f96a7d53b', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-10-05 17:08:22+03',
        60, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('cb9f1165-e31c-4538-8ca4-33eeeee8a81c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'fc2bd317-69b1-405e-9094-f85f96a7d53b', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-09-17 17:08:22+03',
        120, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b725cbb8-51cf-417f-8265-f4d4017ef61e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'fc2bd317-69b1-405e-9094-f85f96a7d53b', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-08-22 17:08:22+03',
        120, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('59ab05b8-0dca-4b13-a6e2-547ff620ea13', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'fc2bd317-69b1-405e-9094-f85f96a7d53b', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-10-25 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c528fb58-6b77-4658-b3a7-19b05a66632a', '59ab05b8-0dca-4b13-a6e2-547ff620ea13', 'fc2bd317-69b1-405e-9094-f85f96a7d53b',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        7134.57, 'علاج تقويم', '2024-10-25',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('906e2838-f37e-4d49-b6eb-8d1994b3d90a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '452847a3-bd3c-4666-8aef-9cdb7c508f60', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-07-22 17:08:22+03',
        45, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9f7c6e39-6266-483f-b8b8-9c347271ed71', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '452847a3-bd3c-4666-8aef-9cdb7c508f60', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-10-29 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c59eb4e5-9984-4f23-8195-77b8165c945d', '9f7c6e39-6266-483f-b8b8-9c347271ed71', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        403.08, 'علاج خلع الأسنان', '2024-10-29',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c76bf769-f7be-40e8-ae68-9bac496351b0', '9f7c6e39-6266-483f-b8b8-9c347271ed71', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        5516.23, 'علاج طقم أسنان', '2024-10-29',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ba4beaa1-59be-46bb-b62b-f7eb94cfacd1', '9f7c6e39-6266-483f-b8b8-9c347271ed71', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 37, 'COMPLETED',
        2870.11, 'علاج طقم جزئي - السن رقم 37', '2024-10-29',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e4ac3d54-1e39-4a7d-a7c0-552fb4db1ec5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '452847a3-bd3c-4666-8aef-9cdb7c508f60', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-08-06 17:08:22+03',
        30, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0025d33a-d850-43ea-84a2-356433826ad7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '452847a3-bd3c-4666-8aef-9cdb7c508f60', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-09-24 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cc68f10d-a52a-4589-b721-783808cee9c7', '0025d33a-d850-43ea-84a2-356433826ad7', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 14, 'COMPLETED',
        2156.03, 'علاج قشرة تجميلية - السن رقم 14', '2024-09-24',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9608f46f-6f93-44c1-95ac-00dec32402fc', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '452847a3-bd3c-4666-8aef-9cdb7c508f60', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-04-01 17:08:22+03',
        120, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b037e9cb-f1c4-49af-80b4-3ae8181593a3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '452847a3-bd3c-4666-8aef-9cdb7c508f60', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-10-30 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b01dc402-3756-49d4-a070-8964c832f314', 'b037e9cb-f1c4-49af-80b4-3ae8181593a3', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        4686.78, 'علاج طقم أسنان', '2024-10-30',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2f3665d6-f07d-4516-a06e-b7445e5bbae2', 'b037e9cb-f1c4-49af-80b4-3ae8181593a3', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 42, 'COMPLETED',
        122.89, 'علاج تنظيف الأسنان - السن رقم 42', '2024-10-30',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('84c9aa57-fe1b-4354-962d-702656e66842', 'b037e9cb-f1c4-49af-80b4-3ae8181593a3', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        93.10, 'علاج فلورايد', '2024-10-30',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4f8939a2-6c7b-4a58-ad77-7f55443de431', 'b037e9cb-f1c4-49af-80b4-3ae8181593a3', '452847a3-bd3c-4666-8aef-9cdb7c508f60',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        181.22, 'علاج إزالة الجير', '2024-10-30',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7bcbc1ab-9d9d-4323-9bb5-e64018ae0aeb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a60a0f58-bc0e-4896-b8a8-25359d4827a4', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-10-05 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('26302113-af27-4a45-8168-9ae61447deee', '7bcbc1ab-9d9d-4323-9bb5-e64018ae0aeb', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 25, 'COMPLETED',
        541.48, 'علاج مثبت تقويم - السن رقم 25', '2024-10-05',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('76f6efe0-8f8c-4705-b298-4785da74bcd3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a60a0f58-bc0e-4896-b8a8-25359d4827a4', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-07-31 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('148443a1-b5a0-4ba7-9051-aeac0d760fa3', '76f6efe0-8f8c-4705-b298-4785da74bcd3', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        1623.53, 'علاج قشرة تجميلية', '2024-07-31',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8bc1413e-e7a8-46df-aac8-25734fc82e80', '76f6efe0-8f8c-4705-b298-4785da74bcd3', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        1463.16, 'علاج تاج الأسنان', '2024-07-31',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('64a86ce0-1341-4984-b22e-c7c321de7e97', '76f6efe0-8f8c-4705-b298-4785da74bcd3', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        4201.29, 'علاج طقم أسنان', '2024-07-31',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8ea575dd-325f-4eb0-90cc-6f24007f89f1', '76f6efe0-8f8c-4705-b298-4785da74bcd3', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        139.86, 'علاج حشو وقائي', '2024-07-31',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('69de1fd8-7b23-4a7a-a736-47d161586830', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a60a0f58-bc0e-4896-b8a8-25359d4827a4', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-08-02 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('07c4a61d-3838-462f-8a4c-01bbf936c751', '69de1fd8-7b23-4a7a-a736-47d161586830', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        438.58, 'علاج علاج اللثة', '2024-08-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fc38338b-78a9-49c8-be4c-311fa5ba02a7', '69de1fd8-7b23-4a7a-a736-47d161586830', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        507.78, 'علاج مثبت تقويم', '2024-08-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('722e212e-3589-4ff3-a673-ff5525696cad', '69de1fd8-7b23-4a7a-a736-47d161586830', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        476.43, 'علاج علاج اللثة', '2024-08-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5ac84cbd-9051-4345-bbf1-e89c4384009d', '69de1fd8-7b23-4a7a-a736-47d161586830', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        224.06, 'علاج إزالة الجير', '2024-08-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('08765e72-f513-4e0c-ba4a-5ecadc58ff97', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a60a0f58-bc0e-4896-b8a8-25359d4827a4', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-12-30 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1dc0eaa9-d9ff-470f-91fc-2ba6a7a2bf4a', '08765e72-f513-4e0c-ba4a-5ecadc58ff97', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 12, 'COMPLETED',
        4984.88, 'علاج طقم أسنان - السن رقم 12', '2024-12-30',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bf66a2c4-df63-4bb3-83fe-da8f87a02e07', '08765e72-f513-4e0c-ba4a-5ecadc58ff97', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        461.08, 'علاج مثبت تقويم', '2024-12-30',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('db34bda9-4777-4504-a0ac-84d3cf9e54a7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-10-25 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7188e547-2322-4c02-8199-0a85bc52593e', 'db34bda9-4777-4504-a0ac-84d3cf9e54a7', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 17, 'COMPLETED',
        419.43, 'علاج خلع الأسنان - السن رقم 17', '2024-10-25',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('caaff280-dabd-4cd6-a62b-48eaaddcdb06', 'db34bda9-4777-4504-a0ac-84d3cf9e54a7', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 23, 'COMPLETED',
        4917.71, 'علاج زراعة الأسنان - السن رقم 23', '2024-10-25',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9aa31a7e-8ac6-49a8-b580-74dadeed3697', 'db34bda9-4777-4504-a0ac-84d3cf9e54a7', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 48, 'COMPLETED',
        82.56, 'علاج فلورايد - السن رقم 48', '2024-10-25',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9fdd4829-d0b8-4b11-b726-979a90e29b97', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-03-19 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('18490815-cc9e-4aca-99c4-901621ebb56a', '9fdd4829-d0b8-4b11-b726-979a90e29b97', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        174.85, 'علاج إزالة الجير', '2025-03-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('593e6aa2-4e7c-4113-acf4-a261d77218fd', '9fdd4829-d0b8-4b11-b726-979a90e29b97', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        86.34, 'علاج فلورايد', '2025-03-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3f08af13-7087-43c1-9f97-6400c771f95b', '9fdd4829-d0b8-4b11-b726-979a90e29b97', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 11, 'COMPLETED',
        713.57, 'علاج تبييض الأسنان - السن رقم 11', '2025-03-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d1bbbf76-90a3-45e6-84eb-f7ad7fe1ae08', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-25 17:08:22+03',
        45, 'NO_SHOW', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2dae536a-090a-4292-9820-1a7f133ebac3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-09 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e3cd6af5-ed56-4b4f-a817-0822c59b9057', '2dae536a-090a-4292-9820-1a7f133ebac3', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 21, 'COMPLETED',
        5958.98, 'علاج طقم أسنان - السن رقم 21', '2025-01-09',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6dfed3ff-17a7-433d-a9a9-29d54da798a4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-05-04 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1bcd2f10-5b93-442c-ba2e-b3912433d24c', '6dfed3ff-17a7-433d-a9a9-29d54da798a4', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 32, 'COMPLETED',
        425.99, 'علاج علاج اللثة - السن رقم 32', '2025-05-04',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('19d935e9-cfe7-4744-8d47-4f715e5db71e', '6dfed3ff-17a7-433d-a9a9-29d54da798a4', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        539.65, 'علاج ترميم تجميلي', '2025-05-04',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3f96f7c3-ce08-4bcc-9a09-9f82dc842baf', '6dfed3ff-17a7-433d-a9a9-29d54da798a4', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        115.07, 'علاج X_RAY', '2025-05-04',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fcef2e61-18cc-4f01-9590-3c52b5faade9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-03-01 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b032bb17-bb3b-43cb-943f-73c940cce657', 'fcef2e61-18cc-4f01-9590-3c52b5faade9', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        92.97, 'علاج X_RAY', '2025-03-01',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5db895c9-1059-4656-9190-ce2d3170d308', 'fcef2e61-18cc-4f01-9590-3c52b5faade9', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 20, 'COMPLETED',
        1606.40, 'علاج تاج الأسنان - السن رقم 20', '2025-03-01',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bb388113-08e8-468d-be55-566a50884dae', 'fcef2e61-18cc-4f01-9590-3c52b5faade9', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        145.52, 'علاج تنظيف الأسنان', '2025-03-01',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9df5af3a-8b18-4247-94f3-ab488ce5b6e7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-27 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('84672d6a-cb24-4024-a80a-7f830689695a', '9df5af3a-8b18-4247-94f3-ab488ce5b6e7', '3e8dcbc5-36b1-435b-bfef-356d7d615c73',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        112.97, 'علاج استشارة', '2025-03-27',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('275d5264-2c2d-422f-aa07-8a335b2e65bc', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3e8dcbc5-36b1-435b-bfef-356d7d615c73', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-08-23 17:08:22+03',
        90, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c7d04a01-571a-49fd-9a31-6db72349d923', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bb002dda-1f45-4af2-b66d-13d745614679', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-12 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ecbf12aa-b454-4d88-8e55-0e12d86fb3f9', 'c7d04a01-571a-49fd-9a31-6db72349d923', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 28, 'COMPLETED',
        8058.94, 'علاج تقويم - السن رقم 28', '2025-01-12',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('26d8d587-2ead-4cfe-b266-83042878775a', 'c7d04a01-571a-49fd-9a31-6db72349d923', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        114.00, 'علاج حشو وقائي', '2025-01-12',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('226c2a80-6579-434b-aaf1-04e453ee900e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bb002dda-1f45-4af2-b66d-13d745614679', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-11-23 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d6441f3b-e00a-4ddc-966f-214766e99fbd', '226c2a80-6579-434b-aaf1-04e453ee900e', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1346.41, 'علاج علاج العصب', '2024-11-23',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('15471e50-8d97-4a9e-8fd6-a2122630e542', '226c2a80-6579-434b-aaf1-04e453ee900e', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        4791.69, 'علاج طقم أسنان', '2024-11-23',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5cb4ae01-92c9-4c1d-beb2-e03d500081b3', '226c2a80-6579-434b-aaf1-04e453ee900e', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 46, 'COMPLETED',
        837.38, 'علاج تبييض الأسنان - السن رقم 46', '2024-11-23',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('99369a89-bc30-49d4-8767-aab2dfdd09dc', '226c2a80-6579-434b-aaf1-04e453ee900e', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        618.41, 'علاج ترميم تجميلي', '2024-11-23',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7cd68a7c-6831-4365-ba72-58e9843529eb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bb002dda-1f45-4af2-b66d-13d745614679', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-06 17:08:22+03',
        90, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cdd63a60-4209-49d7-89b8-296c0375415e', '7cd68a7c-6831-4365-ba72-58e9843529eb', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        420.01, 'علاج مثبت تقويم', '2025-04-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('700dc278-48b8-4239-9fb6-66615bc9a2a4', '7cd68a7c-6831-4365-ba72-58e9843529eb', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1210.98, 'علاج علاج العصب', '2025-04-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('da47371e-fddc-4007-8dc6-6c66cf2841b9', '7cd68a7c-6831-4365-ba72-58e9843529eb', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        4390.89, 'علاج زراعة الأسنان', '2025-04-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('78280ca2-5946-4cb1-9a0f-9c741b625ab4', '7cd68a7c-6831-4365-ba72-58e9843529eb', 'bb002dda-1f45-4af2-b66d-13d745614679',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 24, 'COMPLETED',
        9501.30, 'علاج تقويم - السن رقم 24', '2025-04-06',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('81aaca27-b3dc-43b7-809b-8f57beeca9c8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-09-19 17:08:22+03',
        45, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6249ac69-ba24-4c16-bf8b-69ab1d4dd8ec', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-07-26 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4f091777-e4f8-4486-8252-6022ba735262', '6249ac69-ba24-4c16-bf8b-69ab1d4dd8ec', '4c4eeae5-1844-4545-a6ce-2b8bbece8e88',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 19, 'COMPLETED',
        9275.54, 'علاج تقويم - السن رقم 19', '2024-07-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c25275da-c63e-4371-b734-275316dd38c2', '6249ac69-ba24-4c16-bf8b-69ab1d4dd8ec', '4c4eeae5-1844-4545-a6ce-2b8bbece8e88',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 26, 'COMPLETED',
        386.98, 'علاج خلع الأسنان - السن رقم 26', '2024-07-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('442cf71d-2cbd-42f0-8cbe-92b51aa12dc1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-08-09 17:08:22+03',
        45, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c2a87b7e-bbac-4225-95b6-0956ae8c37e7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-04-14 17:08:22+03',
        30, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e2ee0307-2033-4f45-902c-092b9b84a95e', 'c2a87b7e-bbac-4225-95b6-0956ae8c37e7', '4c4eeae5-1844-4545-a6ce-2b8bbece8e88',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1367.86, 'علاج علاج العصب', '2025-04-14',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('97fe0c0a-65ab-4230-aa32-f38da05c778c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-09-15 17:08:22+03',
        90, 'CANCELLED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('516c9ac5-c56b-4ac9-8699-af73329e6a63', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-09-14 17:08:22+03',
        120, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7fcdb8e9-9270-4820-96f4-5a9866c458c7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-11-25 17:08:22+03',
        120, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a82b5e40-915e-47cf-b7cf-8e678052e3f4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-08-20 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2b9bb1ca-5819-4559-b149-ebe7c6aa6989', 'a82b5e40-915e-47cf-b7cf-8e678052e3f4', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 35, 'COMPLETED',
        3468.21, 'علاج طقم جزئي - السن رقم 35', '2024-08-20',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('81d7142b-91b3-4511-b9e1-2f2d107e9c11', 'a82b5e40-915e-47cf-b7cf-8e678052e3f4', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 41, 'COMPLETED',
        111.48, 'علاج X_RAY - السن رقم 41', '2024-08-20',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fff50548-fa47-409a-8dab-7a910e8cdfc2', 'a82b5e40-915e-47cf-b7cf-8e678052e3f4', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        270.41, 'علاج حشو الأسنان', '2024-08-20',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e6928513-a48d-4714-acd2-06990165ba3f', 'a82b5e40-915e-47cf-b7cf-8e678052e3f4', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        1059.04, 'علاج علاج العصب', '2024-08-20',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fc506606-a98d-4324-98c7-41c5ba860cea', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0d8194ad-2d57-446b-b73d-1b8ec4993b84', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-12-04 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fbde56d1-c231-4f80-a77a-cef7c19f44af', 'fc506606-a98d-4324-98c7-41c5ba860cea', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 16, 'COMPLETED',
        1153.56, 'علاج علاج العصب - السن رقم 16', '2024-12-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6ffe1a73-fae7-4b3e-b227-53dc1175a0aa', 'fc506606-a98d-4324-98c7-41c5ba860cea', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        4356.76, 'علاج زراعة الأسنان', '2024-12-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6caa8586-5e17-4e43-a177-128afa59b106', 'fc506606-a98d-4324-98c7-41c5ba860cea', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        665.30, 'علاج ترميم تجميلي', '2024-12-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1d626cdb-a36b-4c31-9160-195fc3e1ddeb', 'fc506606-a98d-4324-98c7-41c5ba860cea', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 24, 'COMPLETED',
        104.43, 'علاج حشو وقائي - السن رقم 24', '2024-12-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6c59003c-a8e9-4f61-b3ac-d2ecf270821e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-08-12 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1d9552bb-1997-43eb-8d90-62e5db146c5f', '6c59003c-a8e9-4f61-b3ac-d2ecf270821e', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 11, 'COMPLETED',
        4060.78, 'علاج زراعة الأسنان - السن رقم 11', '2024-08-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('af21db84-e5dc-465e-bbff-291190c93486', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0d8194ad-2d57-446b-b73d-1b8ec4993b84', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-09-30 17:08:22+03',
        90, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('96224930-0e28-42a3-8814-6f38f196d967', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-08 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c23195f0-93fa-49ae-aca3-052ef1ae9a6b', '96224930-0e28-42a3-8814-6f38f196d967', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4752.04, 'علاج طقم أسنان', '2025-05-08',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ab413f5c-ed48-4103-ba41-815db4a8cbd3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-07-03 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('83f97360-386d-457e-b360-8a85c0162d3b', 'ab413f5c-ed48-4103-ba41-815db4a8cbd3', '0d8194ad-2d57-446b-b73d-1b8ec4993b84',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 18, 'COMPLETED',
        166.65, 'علاج إزالة الجير - السن رقم 18', '2025-07-03',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c0eef15f-83f8-4d72-affb-41a6d0958d40', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-08 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('317848b4-626a-4122-ba17-4176eee51cad', 'c0eef15f-83f8-4d72-affb-41a6d0958d40', 'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 32, 'COMPLETED',
        915.59, 'علاج تبييض الأسنان - السن رقم 32', '2025-03-08',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('076b0b7f-b948-4ba6-961f-805c7fc754f7', 'c0eef15f-83f8-4d72-affb-41a6d0958d40', 'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 30, 'COMPLETED',
        730.88, 'علاج تبييض الأسنان - السن رقم 30', '2025-03-08',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3a83f9dc-28dd-4e06-afa6-15ab9727ab09', 'c0eef15f-83f8-4d72-affb-41a6d0958d40', 'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        89.32, 'علاج X_RAY', '2025-03-08',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('91d20610-1e14-4ffd-b050-c3bb633d76fe', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-09-12 17:08:22+03',
        120, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9f7edff9-ff61-42cc-a81b-cd6eb8399905', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-07-26 17:08:22+03',
        30, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('78e7ca80-f3d7-47de-9d39-ee4253326172', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-02-28 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('252960cb-f640-4bd8-80f4-5035fe0396d6', '78e7ca80-f3d7-47de-9d39-ee4253326172', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        8063.67, 'علاج تقويم', '2025-02-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1a8e0152-9fe1-4e87-8aa8-002f23fdb58e', '78e7ca80-f3d7-47de-9d39-ee4253326172', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        1268.76, 'علاج علاج العصب', '2025-02-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1f00acc3-a53c-4e5c-8153-5bafbf187a36', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-31 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('27eee58a-a909-4bea-95f5-5ef248c780cd', '1f00acc3-a53c-4e5c-8153-5bafbf187a36', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        395.30, 'علاج علاج اللثة', '2025-01-31',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('39984e1b-db68-4342-88c1-ca9e4a6d5217', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-03-03 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8ae49140-350f-4de7-a440-9f0224f27838', '39984e1b-db68-4342-88c1-ca9e4a6d5217', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        89.84, 'علاج X_RAY', '2025-03-03',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f8adb6f0-3127-4f0a-908c-bebf37dfa7b1', '39984e1b-db68-4342-88c1-ca9e4a6d5217', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        534.20, 'علاج مثبت تقويم', '2025-03-03',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('56576c79-e8b6-4d9d-a127-e4cd661b55d1', '39984e1b-db68-4342-88c1-ca9e4a6d5217', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        394.57, 'علاج علاج اللثة', '2025-03-03',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('17e4e0dd-8a33-4ffe-9862-20da85601906', '39984e1b-db68-4342-88c1-ca9e4a6d5217', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        122.40, 'علاج حشو وقائي', '2025-03-03',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fe3a5c46-6606-456b-b03c-d11e90d214fb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-02-24 17:08:22+03',
        45, 'NO_SHOW', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('def33337-8aa2-4007-8e5a-e74ccea6b391', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-08-18 17:08:22+03',
        90, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('630db280-a671-42f3-981a-c6ef26be87ac', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-08-14 17:08:22+03',
        45, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b0f4fc84-9b3e-4044-8460-c66fe5a79e6a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b5ad516c-6b71-4416-b660-056ad1d34f79', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-18 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('89362176-a803-40d2-acbe-038b25918fbf', 'b0f4fc84-9b3e-4044-8460-c66fe5a79e6a', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        4011.59, 'علاج طقم جزئي', '2025-03-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1230cfb3-f25e-4827-8b4a-433f3802d9f4', 'b0f4fc84-9b3e-4044-8460-c66fe5a79e6a', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 20, 'COMPLETED',
        3541.36, 'علاج جسر الأسنان - السن رقم 20', '2025-03-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('da12f5ca-fcec-46e1-9613-0715c5e29fd9', 'b0f4fc84-9b3e-4044-8460-c66fe5a79e6a', 'b5ad516c-6b71-4416-b660-056ad1d34f79',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        162.75, 'علاج تنظيف الأسنان', '2025-03-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('45cf6d05-f0d9-42aa-b9a2-5abeb8c42209', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '578912a3-7404-4cc6-8ff7-d4c7332acc45', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-12-17 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1300a2c0-52ac-4484-9871-dad8b671b6bc', '45cf6d05-f0d9-42aa-b9a2-5abeb8c42209', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        4189.22, 'علاج زراعة الأسنان', '2024-12-17',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('272e5eae-5c78-48b9-b65e-b5e6dfe67d09', '45cf6d05-f0d9-42aa-b9a2-5abeb8c42209', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        7201.80, 'علاج تقويم', '2024-12-17',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('56f46171-6dcd-4677-a006-e419626b09be', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '578912a3-7404-4cc6-8ff7-d4c7332acc45', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-03-02 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6050086e-8025-4eff-933b-2b1503270ddd', '56f46171-6dcd-4677-a006-e419626b09be', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 41, 'COMPLETED',
        79.11, 'علاج فلورايد - السن رقم 41', '2025-03-02',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8103e6da-8dab-43ad-8d01-b0f132fc2378', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '578912a3-7404-4cc6-8ff7-d4c7332acc45', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-09-07 17:08:22+03',
        60, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1985c626-fc8a-4603-9ddf-192d241bf3e9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '578912a3-7404-4cc6-8ff7-d4c7332acc45', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-08-28 17:08:22+03',
        120, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f6481282-f3cd-45b7-93e1-19e863046d64', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '578912a3-7404-4cc6-8ff7-d4c7332acc45', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-04-23 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('981b1a47-767f-4d2e-9490-0c91ccdfd65e', 'f6481282-f3cd-45b7-93e1-19e863046d64', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 18, 'COMPLETED',
        2096.21, 'علاج قشرة تجميلية - السن رقم 18', '2025-04-23',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5b9c14e0-bce0-4360-8a5a-0b6a03c71ce5', 'f6481282-f3cd-45b7-93e1-19e863046d64', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 22, 'COMPLETED',
        89.99, 'علاج استشارة - السن رقم 22', '2025-04-23',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5c7cf9ec-ef62-4f17-8a98-7e32f6c3b65b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '578912a3-7404-4cc6-8ff7-d4c7332acc45', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-12-27 17:08:22+03',
        45, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('18d90f6a-90b8-45f6-b782-717989d62b6e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '578912a3-7404-4cc6-8ff7-d4c7332acc45', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-10 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('656142f2-9bb2-4edf-83ca-658a293347fc', '18d90f6a-90b8-45f6-b782-717989d62b6e', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        870.73, 'علاج تبييض الأسنان', '2024-09-10',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('83263bd6-a846-4c0b-b873-e78a5460aa5f', '18d90f6a-90b8-45f6-b782-717989d62b6e', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        762.35, 'علاج تبييض الأسنان', '2024-09-10',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('84555f40-1bd0-45f9-a0c3-1def11681e67', '18d90f6a-90b8-45f6-b782-717989d62b6e', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 13, 'COMPLETED',
        76.31, 'علاج فلورايد - السن رقم 13', '2024-09-10',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9efe70ab-7e7d-44df-af89-3922c6ade71b', '18d90f6a-90b8-45f6-b782-717989d62b6e', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        83.16, 'علاج استشارة', '2024-09-10',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2a599af7-2546-4820-a087-3dbe8927b65d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81a36250-0264-4339-b35e-dd3786feca0e', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-07-27 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('86ba5acd-38fc-4fa8-b70a-c1289dd216b3', '2a599af7-2546-4820-a087-3dbe8927b65d', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        3474.22, 'علاج طقم جزئي', '2024-07-27',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('da49c5d2-c37a-43b9-b8d8-183fb1492719', '2a599af7-2546-4820-a087-3dbe8927b65d', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 43, 'COMPLETED',
        1542.75, 'علاج تاج الأسنان - السن رقم 43', '2024-07-27',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2d185230-5d24-4c74-93f5-82fda4e3049c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81a36250-0264-4339-b35e-dd3786feca0e', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-12-08 17:08:22+03',
        60, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5866d50e-4484-40fc-bc3a-8e68a010d6bf', '2d185230-5d24-4c74-93f5-82fda4e3049c', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        79.60, 'علاج فلورايد', '2024-12-08',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('73575ed2-d502-4709-a3bd-825dd6046b31', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81a36250-0264-4339-b35e-dd3786feca0e', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-06-28 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('abb435af-0a43-482a-927c-4372bbc6bbef', '73575ed2-d502-4709-a3bd-825dd6046b31', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        184.17, 'علاج إزالة الجير', '2025-06-28',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bed6e491-85f0-4065-82e6-582ee3991862', '73575ed2-d502-4709-a3bd-825dd6046b31', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        260.76, 'علاج حشو الأسنان', '2025-06-28',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e18f00d0-e87e-4387-88b2-6b43ffe074e9', '73575ed2-d502-4709-a3bd-825dd6046b31', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        5443.11, 'علاج طقم أسنان', '2025-06-28',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('df41df88-9a2d-4d7f-8a45-e0eea1781193', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81a36250-0264-4339-b35e-dd3786feca0e', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-05-14 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('13fade93-ebcf-47c1-a904-61d2752ee4a3', 'df41df88-9a2d-4d7f-8a45-e0eea1781193', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        107.14, 'علاج حشو وقائي', '2025-05-14',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('69aa6a2c-b1b1-4f52-92e9-d7df9a82605f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '81a36250-0264-4339-b35e-dd3786feca0e', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-01-14 17:08:22+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('24673c5d-8dc6-4cca-b4b9-67fb9de42779', '69aa6a2c-b1b1-4f52-92e9-d7df9a82605f', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 30, 'COMPLETED',
        1937.28, 'علاج قشرة تجميلية - السن رقم 30', '2025-01-14',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a8c8f7c2-e47d-4cff-b8a9-f44cb5e2cff5', '69aa6a2c-b1b1-4f52-92e9-d7df9a82605f', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        168.45, 'علاج تنظيف الأسنان', '2025-01-14',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('840c4694-da04-4abe-bcc5-1c25aa4996d4', '69aa6a2c-b1b1-4f52-92e9-d7df9a82605f', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 13, 'COMPLETED',
        542.67, 'علاج ترميم تجميلي - السن رقم 13', '2025-01-14',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ba501ffb-a464-4de3-bed3-11db31b8677c', '69aa6a2c-b1b1-4f52-92e9-d7df9a82605f', '81a36250-0264-4339-b35e-dd3786feca0e',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        4119.27, 'علاج طقم جزئي', '2025-01-14',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('af2b9d06-a643-44eb-9557-2130e1684e21', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c99fc741-2b1d-405b-ac34-dd92212fbc99', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-10-08 17:08:22+03',
        30, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('209be00c-6ff8-446f-bb4b-125764a14def', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c99fc741-2b1d-405b-ac34-dd92212fbc99', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-17 17:08:22+03',
        30, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d3b97c89-e2b3-4233-b4c0-c5a988d70ea6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c99fc741-2b1d-405b-ac34-dd92212fbc99', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-09-13 17:08:22+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('68dbda77-34cb-4d6e-ad1c-ea3510e27945', 'd3b97c89-e2b3-4233-b4c0-c5a988d70ea6', 'c99fc741-2b1d-405b-ac34-dd92212fbc99',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        1353.30, 'علاج علاج العصب', '2024-09-13',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('08656b71-0e6c-46cd-9c42-22a8e8ac17e0', 'd3b97c89-e2b3-4233-b4c0-c5a988d70ea6', 'c99fc741-2b1d-405b-ac34-dd92212fbc99',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        81.26, 'علاج فلورايد', '2024-09-13',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f470ec78-620c-4f76-9f16-0a1568f7a1b0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c99fc741-2b1d-405b-ac34-dd92212fbc99', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-06-26 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('801fbf73-1297-4bfa-a052-639f0ac48e33', 'f470ec78-620c-4f76-9f16-0a1568f7a1b0', 'c99fc741-2b1d-405b-ac34-dd92212fbc99',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        2919.55, 'علاج طقم جزئي', '2025-06-26',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('57946e40-3d6f-48da-94e1-10df1ae0ba97', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c99fc741-2b1d-405b-ac34-dd92212fbc99', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-08-23 17:08:22+03',
        45, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0b8fedf8-4517-4eb1-9f41-a30dc9b9a64d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '951394c6-4e21-4d51-ba77-80c783a574e2', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-29 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ab781b01-e9e6-479f-8ebf-7a8d336e01e6', '0b8fedf8-4517-4eb1-9f41-a30dc9b9a64d', '951394c6-4e21-4d51-ba77-80c783a574e2',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 29, 'COMPLETED',
        491.80, 'علاج مثبت تقويم - السن رقم 29', '2024-09-29',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d2059d32-529b-4ec8-a4b2-a5ca7217229a', '0b8fedf8-4517-4eb1-9f41-a30dc9b9a64d', '951394c6-4e21-4d51-ba77-80c783a574e2',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 36, 'COMPLETED',
        2026.06, 'علاج قشرة تجميلية - السن رقم 36', '2024-09-29',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('eaa66667-36af-4a42-9980-c9f86404e995', '0b8fedf8-4517-4eb1-9f41-a30dc9b9a64d', '951394c6-4e21-4d51-ba77-80c783a574e2',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 47, 'COMPLETED',
        664.58, 'علاج ترميم تجميلي - السن رقم 47', '2024-09-29',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7f788fee-f5c6-457d-a149-f7caf8778635', '0b8fedf8-4517-4eb1-9f41-a30dc9b9a64d', '951394c6-4e21-4d51-ba77-80c783a574e2',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        942.08, 'علاج تبييض الأسنان', '2024-09-29',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0bfee8d0-652c-4850-a203-517159541c98', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '951394c6-4e21-4d51-ba77-80c783a574e2', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-05-12 17:08:22+03',
        120, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0cb581c6-9bf8-45e1-8b15-25720483ba63', '0bfee8d0-652c-4850-a203-517159541c98', '951394c6-4e21-4d51-ba77-80c783a574e2',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        7074.19, 'علاج تقويم', '2025-05-12',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('947d9097-1808-4a5a-b677-97fe3aafce69', '0bfee8d0-652c-4850-a203-517159541c98', '951394c6-4e21-4d51-ba77-80c783a574e2',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 15, 'COMPLETED',
        1207.78, 'علاج تاج الأسنان - السن رقم 15', '2025-05-12',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0c2be007-9eaa-4b66-a37e-441109f1398b', '0bfee8d0-652c-4850-a203-517159541c98', '951394c6-4e21-4d51-ba77-80c783a574e2',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        3719.29, 'علاج طقم جزئي', '2025-05-12',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('29c42219-0818-41e4-9d54-eee27dcea0b8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '94ebb4cc-4a6c-4063-8cea-3130a45e71e4', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-10-09 17:08:22+03',
        90, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('406b5948-484c-432a-ba92-ef3acd1e647f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '94ebb4cc-4a6c-4063-8cea-3130a45e71e4', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-11-23 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cd01dda6-ebbe-4f7c-b5af-ff8ab357aa4c', '406b5948-484c-432a-ba92-ef3acd1e647f', '94ebb4cc-4a6c-4063-8cea-3130a45e71e4',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 28, 'COMPLETED',
        150.86, 'علاج تنظيف الأسنان - السن رقم 28', '2024-11-23',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6471d5ef-19f2-4f62-a4f3-a46e3acd15bf', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '94ebb4cc-4a6c-4063-8cea-3130a45e71e4', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-10-04 17:08:22+03',
        120, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f22eba26-769e-47db-8c26-89e23b90bb67', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '94ebb4cc-4a6c-4063-8cea-3130a45e71e4', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-08-05 17:08:22+03',
        120, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('37c26ac4-d878-46ea-b204-6fa0496c8088', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '94ebb4cc-4a6c-4063-8cea-3130a45e71e4', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-10-04 17:08:22+03',
        90, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7725756d-bd2a-49af-a5af-f05b164f1828', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '638b6b84-50f9-4861-b001-c86ad2188ef1', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-01-01 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b3e98a23-cd56-467d-8015-70b833606786', '7725756d-bd2a-49af-a5af-f05b164f1828', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        118.37, 'علاج X_RAY', '2025-01-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cd3eee4d-92fe-4228-9ba8-18d4dbb237c7', '7725756d-bd2a-49af-a5af-f05b164f1828', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        329.42, 'علاج علاج اللثة', '2025-01-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fc2c8816-7bd6-4950-a352-d3fdd2729fc3', '7725756d-bd2a-49af-a5af-f05b164f1828', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 48, 'COMPLETED',
        6655.70, 'علاج تقويم - السن رقم 48', '2025-01-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f825c19a-21fe-46f9-8e4b-da22d50f5b80', '7725756d-bd2a-49af-a5af-f05b164f1828', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        452.59, 'علاج خلع الأسنان', '2025-01-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('94951aaf-01df-40d4-b2b9-e535c90c2f1d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '638b6b84-50f9-4861-b001-c86ad2188ef1', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-07-12 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c318cc61-152e-4c52-b27a-3a9e959bb41f', '94951aaf-01df-40d4-b2b9-e535c90c2f1d', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 26, 'COMPLETED',
        502.40, 'علاج ترميم تجميلي - السن رقم 26', '2024-07-12',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c8afb570-a4af-4efe-b2c5-8865227fbd86', '94951aaf-01df-40d4-b2b9-e535c90c2f1d', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        4257.04, 'علاج زراعة الأسنان', '2024-07-12',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3a7ae272-0284-47f7-9fac-79987284556e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '638b6b84-50f9-4861-b001-c86ad2188ef1', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-07-25 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b4451b92-c0bb-4442-bc26-14aabfeed7e3', '3a7ae272-0284-47f7-9fac-79987284556e', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        81.52, 'علاج فلورايد', '2024-07-25',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('37d8edb5-d3f9-4b52-8eba-a6b9918c3f73', '3a7ae272-0284-47f7-9fac-79987284556e', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        577.43, 'علاج مثبت تقويم', '2024-07-25',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2b37c762-a73d-4285-974c-eef7b185db9a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '638b6b84-50f9-4861-b001-c86ad2188ef1', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-09-05 17:08:22+03',
        60, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ec8cabdf-7bcb-4388-8413-b6838e471407', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '638b6b84-50f9-4861-b001-c86ad2188ef1', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-01-19 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9ca51c1b-114d-439c-a1ff-7339203d9bcb', 'ec8cabdf-7bcb-4388-8413-b6838e471407', '638b6b84-50f9-4861-b001-c86ad2188ef1',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 44, 'COMPLETED',
        5610.05, 'علاج طقم أسنان - السن رقم 44', '2025-01-19',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('041b4608-7ca8-4660-a149-8570fcdfadf5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4cf6838e-3cfd-4469-b010-38442aca0857', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-15 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8c7da38b-190b-4e5f-9d11-f55ab84c9539', '041b4608-7ca8-4660-a149-8570fcdfadf5', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        120.47, 'علاج تنظيف الأسنان', '2025-03-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3a2ea488-30c5-4cc7-af7e-bef60df7621f', '041b4608-7ca8-4660-a149-8570fcdfadf5', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4007.36, 'علاج طقم أسنان', '2025-03-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('269b48f1-88be-47e0-86cf-ba868e08501e', '041b4608-7ca8-4660-a149-8570fcdfadf5', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4431.51, 'علاج طقم أسنان', '2025-03-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('90561c3e-0dda-4bd2-9f12-193f5c27ed50', '041b4608-7ca8-4660-a149-8570fcdfadf5', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        211.34, 'علاج إزالة الجير', '2025-03-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('16e553d1-2ebe-4ea8-9786-b69ab111bb8f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4cf6838e-3cfd-4469-b010-38442aca0857', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-28 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a501e5ca-9b0f-4b7d-8370-cae4f8606f1e', '16e553d1-2ebe-4ea8-9786-b69ab111bb8f', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        3121.13, 'علاج جسر الأسنان', '2025-03-28',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('86ba22a5-5edd-46bc-8a77-bab4972786b0', '16e553d1-2ebe-4ea8-9786-b69ab111bb8f', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        355.26, 'علاج خلع الأسنان', '2025-03-28',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1423adec-5a13-4242-a790-efd186418b39', '16e553d1-2ebe-4ea8-9786-b69ab111bb8f', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 31, 'COMPLETED',
        7328.44, 'علاج تقويم - السن رقم 31', '2025-03-28',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('60215038-ea22-4304-a1ae-944d2780fbe0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4cf6838e-3cfd-4469-b010-38442aca0857', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-02-08 17:08:22+03',
        60, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0c92f2e8-dd71-4234-a031-075b15d56fb3', '60215038-ea22-4304-a1ae-944d2780fbe0', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 12, 'COMPLETED',
        3532.81, 'علاج جسر الأسنان - السن رقم 12', '2025-02-08',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8f29ad15-2082-4834-8aad-9448578f3f65', '60215038-ea22-4304-a1ae-944d2780fbe0', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 38, 'COMPLETED',
        4982.52, 'علاج طقم أسنان - السن رقم 38', '2025-02-08',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('029a866b-7475-4944-96fa-3149bc82679b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4cf6838e-3cfd-4469-b010-38442aca0857', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-09-05 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5f277e62-729b-4555-9597-75f4792ab33c', '029a866b-7475-4944-96fa-3149bc82679b', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 33, 'COMPLETED',
        287.48, 'علاج حشو الأسنان - السن رقم 33', '2024-09-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('491ca6b3-8444-4d4e-90e6-6215da9384eb', '029a866b-7475-4944-96fa-3149bc82679b', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        768.73, 'علاج تبييض الأسنان', '2024-09-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e3c47e4b-fdd5-411d-a3ea-7a49111f755e', '029a866b-7475-4944-96fa-3149bc82679b', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        3803.21, 'علاج طقم جزئي', '2024-09-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('66d279b1-3b0e-47e7-a807-a2d483e94d05', '029a866b-7475-4944-96fa-3149bc82679b', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        555.78, 'علاج مثبت تقويم', '2024-09-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('15a677fd-3169-444d-a3b0-4647216be6ba', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4cf6838e-3cfd-4469-b010-38442aca0857', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-09-06 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5b53ce5d-2946-4c83-b549-9a560bfcf42f', '15a677fd-3169-444d-a3b0-4647216be6ba', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 30, 'COMPLETED',
        130.94, 'علاج حشو وقائي - السن رقم 30', '2024-09-06',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('90e90403-9b48-4d79-ae61-ec2b9e195e3e', '15a677fd-3169-444d-a3b0-4647216be6ba', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        108.74, 'علاج حشو وقائي', '2024-09-06',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5ceb7f4d-5ed7-4b42-91d6-d8abc790d3e2', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4cf6838e-3cfd-4469-b010-38442aca0857', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-06-11 17:08:22+03',
        120, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7897179a-eb5f-4bba-b491-f3ae09549ed9', '5ceb7f4d-5ed7-4b42-91d6-d8abc790d3e2', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        2108.27, 'علاج قشرة تجميلية', '2025-06-11',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ea810efd-40d9-4806-8ce1-a0e74208a740', '5ceb7f4d-5ed7-4b42-91d6-d8abc790d3e2', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        141.64, 'علاج تنظيف الأسنان', '2025-06-11',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1f708f3f-3dbc-41e4-bc77-6e7caf0a9949', '5ceb7f4d-5ed7-4b42-91d6-d8abc790d3e2', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        677.92, 'علاج تبييض الأسنان', '2025-06-11',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('eacf92b4-6e1f-4cc5-9e72-3f6c61c9facb', '5ceb7f4d-5ed7-4b42-91d6-d8abc790d3e2', '4cf6838e-3cfd-4469-b010-38442aca0857',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        4996.00, 'علاج طقم أسنان', '2025-06-11',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b2ca060b-410f-4d75-bd35-b412c274ccdf', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-08-15 17:08:22+03',
        120, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('28b0fab8-d544-4fa7-a4be-07c35dff5151', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-09-08 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('611486dd-fbe0-4c6b-9607-fab5463c1681', '28b0fab8-d544-4fa7-a4be-07c35dff5151', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        223.42, 'علاج إزالة الجير', '2024-09-08',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f5bc2f95-5d50-4505-ac58-f6f082017617', '28b0fab8-d544-4fa7-a4be-07c35dff5151', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 40, 'COMPLETED',
        1304.86, 'علاج علاج العصب - السن رقم 40', '2024-09-08',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c5cf609e-7cc9-4150-96f1-f11a915b9b80', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-22 17:08:22+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fbde7916-eda0-403e-b2b4-09386fbd6938', 'c5cf609e-7cc9-4150-96f1-f11a915b9b80', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        336.06, 'علاج خلع الأسنان', '2025-05-22',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cdb9d9e0-bccd-44c7-919e-65442de66eb6', 'c5cf609e-7cc9-4150-96f1-f11a915b9b80', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 14, 'COMPLETED',
        932.92, 'علاج تبييض الأسنان - السن رقم 14', '2025-05-22',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7527b2c8-37e0-4c56-b8ba-63e0e31b43c6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-08-25 17:08:22+03',
        90, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('011bd58f-8227-4a68-a41d-5b7fa4217a9c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-09-10 17:08:22+03',
        60, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('321f2f5c-84ba-4351-af66-e48a1deb2444', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-12-18 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4b093750-7bf2-4e5c-9c7d-d334cc3fe300', '321f2f5c-84ba-4351-af66-e48a1deb2444', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        927.05, 'علاج تبييض الأسنان', '2024-12-18',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('60d12aac-f666-4e62-8881-306c1dc8dec9', '321f2f5c-84ba-4351-af66-e48a1deb2444', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        7679.37, 'علاج تقويم', '2024-12-18',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('062d635e-5d34-4185-84ca-ef8a86e8c447', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-06-07 17:08:22+03',
        90, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('22a6d141-a06b-4e45-8839-34eb79f7ad4a', '062d635e-5d34-4185-84ca-ef8a86e8c447', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1789.88, 'علاج تاج الأسنان', '2025-06-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b29f77fa-1ba6-4826-ae32-e67a8ba8c5b2', '062d635e-5d34-4185-84ca-ef8a86e8c447', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 13, 'COMPLETED',
        1629.52, 'علاج قشرة تجميلية - السن رقم 13', '2025-06-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8bb44ee0-6fc5-4550-a6ae-cee62d8f40b8', '062d635e-5d34-4185-84ca-ef8a86e8c447', '68cedbfc-9ea7-4941-90d8-8927bcac14a8',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 28, 'COMPLETED',
        227.18, 'علاج إزالة الجير - السن رقم 28', '2025-06-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b30f309e-3cc0-4577-92fe-afd82ed360a3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '68cedbfc-9ea7-4941-90d8-8927bcac14a8', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-08-29 17:08:22+03',
        30, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a9d1ae3b-e289-4ca2-9277-fc6e15eea248', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e3e078df-a3c6-4446-900b-e3abe2ff19ff', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-16 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f08fa012-8d2b-4d73-b51d-6c6cb775f385', 'a9d1ae3b-e289-4ca2-9277-fc6e15eea248', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1109.33, 'علاج علاج العصب', '2025-05-16',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('16abdcc0-1a8d-4624-a233-79570eff088e', 'a9d1ae3b-e289-4ca2-9277-fc6e15eea248', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 29, 'COMPLETED',
        429.81, 'علاج خلع الأسنان - السن رقم 29', '2025-05-16',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a2f21ffe-2569-43c0-b5f1-466a85ca1975', 'a9d1ae3b-e289-4ca2-9277-fc6e15eea248', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        878.81, 'علاج تبييض الأسنان', '2025-05-16',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ab215b4f-750d-46f6-ab35-52336ba5d0a6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e3e078df-a3c6-4446-900b-e3abe2ff19ff', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-04-19 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7a643049-ab62-4b10-a0f7-04548c750360', 'ab215b4f-750d-46f6-ab35-52336ba5d0a6', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 38, 'COMPLETED',
        1375.36, 'علاج علاج العصب - السن رقم 38', '2025-04-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e07c9608-ebbc-47a4-9e42-6c8614257139', 'ab215b4f-750d-46f6-ab35-52336ba5d0a6', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        3109.49, 'علاج جسر الأسنان', '2025-04-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e5f1a5a8-fce6-480c-b39e-970d33642fdf', 'ab215b4f-750d-46f6-ab35-52336ba5d0a6', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 25, 'COMPLETED',
        77.02, 'علاج فلورايد - السن رقم 25', '2025-04-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('17590fd9-71e5-4505-8d16-1718f3055c29', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e3e078df-a3c6-4446-900b-e3abe2ff19ff', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-01-31 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c13b0d27-0847-42de-9223-b6d445ce69de', '17590fd9-71e5-4505-8d16-1718f3055c29', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        89.20, 'علاج X_RAY', '2025-01-31',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5a84c28a-41cb-405f-a34f-78e34d8e4191', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-11-23 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('49f68e52-d5a6-46d7-9119-756c10a93521', '5a84c28a-41cb-405f-a34f-78e34d8e4191', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        3112.94, 'علاج طقم جزئي', '2024-11-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('eef48d66-fb0d-4115-ac37-0685988a9cde', '5a84c28a-41cb-405f-a34f-78e34d8e4191', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 38, 'COMPLETED',
        1005.06, 'علاج علاج العصب - السن رقم 38', '2024-11-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('774536bb-d82b-4988-b305-e3cc9bbf525d', '5a84c28a-41cb-405f-a34f-78e34d8e4191', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        96.52, 'علاج X_RAY', '2024-11-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f5cc8ae0-c4bf-4e86-8310-6370a51bbf96', '5a84c28a-41cb-405f-a34f-78e34d8e4191', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        66.34, 'علاج فلورايد', '2024-11-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3aa24f1d-681e-4f5f-a4b7-09ccfe850a50', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-09-20 17:08:22+03',
        120, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f4cd03a3-8dcc-4700-9bac-235e5d6b1fac', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-04-12 17:08:22+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('94366b99-5e7f-47cb-b553-88290a5149e1', 'f4cd03a3-8dcc-4700-9bac-235e5d6b1fac', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        717.60, 'علاج تبييض الأسنان', '2025-04-12',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5bb59931-8f64-477e-a4f7-35f2331e5863', 'f4cd03a3-8dcc-4700-9bac-235e5d6b1fac', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 39, 'COMPLETED',
        2330.30, 'علاج قشرة تجميلية - السن رقم 39', '2025-04-12',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ad4800bb-afe7-404c-8f72-e94765911080', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-08-29 17:08:22+03',
        60, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d486cdc2-5b37-4007-8171-7f36432827fe', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-07-19 17:08:22+03',
        30, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e3fc92cc-e9f4-48fb-a6b7-ea0525fff065', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd71e492-b94d-44b1-8c0b-1012177e68d4', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-08-07 17:08:22+03',
        30, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ec5c87ed-394e-40b4-9d4e-b25af7ab6db4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd71e492-b94d-44b1-8c0b-1012177e68d4', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-09-16 17:08:22+03',
        45, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a67c1909-149e-442d-bc97-b90ecea02db8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd71e492-b94d-44b1-8c0b-1012177e68d4', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-06-26 17:08:22+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9db53a8c-e127-4219-9226-6e3a7edb3fba', 'a67c1909-149e-442d-bc97-b90ecea02db8', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        5159.30, 'علاج زراعة الأسنان', '2025-06-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('eea93de0-2fc6-4051-b39c-9484c8557f30', 'a67c1909-149e-442d-bc97-b90ecea02db8', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        440.87, 'علاج علاج اللثة', '2025-06-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6227a4ea-2328-44bd-b277-1e1f9d4a3580', 'a67c1909-149e-442d-bc97-b90ecea02db8', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 34, 'COMPLETED',
        8521.13, 'علاج تقويم - السن رقم 34', '2025-06-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('22df5e1b-9e04-43b5-892b-16f7b4bed472', 'a67c1909-149e-442d-bc97-b90ecea02db8', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        112.45, 'علاج X_RAY', '2025-06-26',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0d6d658c-7de5-4f2e-9a0f-781fb3e74fb1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd71e492-b94d-44b1-8c0b-1012177e68d4', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-06-26 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f9da51f5-1566-4f18-ba82-b6c871dfee9c', '0d6d658c-7de5-4f2e-9a0f-781fb3e74fb1', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 36, 'COMPLETED',
        1327.29, 'علاج علاج العصب - السن رقم 36', '2025-06-26',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0b3f9e1e-f489-4b2d-98ce-1429d36f8063', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd71e492-b94d-44b1-8c0b-1012177e68d4', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-05 17:08:22+03',
        90, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a04e2f4f-ac27-433c-8b6b-fcd47b6dd8df', '0b3f9e1e-f489-4b2d-98ce-1429d36f8063', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        375.35, 'علاج خلع الأسنان', '2025-03-05',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cfd18e09-eae5-43a4-8d5f-bcb2f1a91eb1', '0b3f9e1e-f489-4b2d-98ce-1429d36f8063', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 46, 'COMPLETED',
        4299.83, 'علاج زراعة الأسنان - السن رقم 46', '2025-03-05',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('543fb28b-15a1-44b1-b483-c51d0683f275', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd71e492-b94d-44b1-8c0b-1012177e68d4', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-28 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a063fd1e-79b9-4a9b-a1bc-b1e4c0246809', '543fb28b-15a1-44b1-b483-c51d0683f275', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        112.35, 'علاج استشارة', '2025-02-28',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7a3a0ead-46aa-4dfc-94f4-49b1ae2df730', '543fb28b-15a1-44b1-b483-c51d0683f275', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4706.03, 'علاج طقم أسنان', '2025-02-28',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e29509ae-ad92-4124-88c0-1e2d5069c542', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'bd71e492-b94d-44b1-8c0b-1012177e68d4', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-07-16 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9cc3709a-e380-421e-8ab1-d1121cb3ccdc', 'e29509ae-ad92-4124-88c0-1e2d5069c542', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        84.74, 'علاج استشارة', '2024-07-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f4109124-e5fb-4487-9f1f-f10563b7d1fa', 'e29509ae-ad92-4124-88c0-1e2d5069c542', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 24, 'COMPLETED',
        4069.32, 'علاج زراعة الأسنان - السن رقم 24', '2024-07-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1467b70f-a490-4d4a-86ab-da385eb0fc7f', 'e29509ae-ad92-4124-88c0-1e2d5069c542', 'bd71e492-b94d-44b1-8c0b-1012177e68d4',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 16, 'COMPLETED',
        2320.45, 'علاج قشرة تجميلية - السن رقم 16', '2024-07-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('976ff10c-814b-4ea4-8914-5aab457ff708', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ae32aab1-387f-4742-a104-9593c8820a09', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-12 17:08:22+03',
        120, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0d467f27-939a-4e52-9f67-19e0c8d1add1', '976ff10c-814b-4ea4-8914-5aab457ff708', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 38, 'COMPLETED',
        114.34, 'علاج حشو وقائي - السن رقم 38', '2025-04-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4d92a4d7-9730-4d27-a693-ee0dd9f22e3e', '976ff10c-814b-4ea4-8914-5aab457ff708', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        353.13, 'علاج حشو الأسنان', '2025-04-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c8290f1c-8a3f-4b89-bdf4-fc855e319241', '976ff10c-814b-4ea4-8914-5aab457ff708', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        2894.52, 'علاج جسر الأسنان', '2025-04-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a98f2a50-4ccf-4b43-82e1-b2d2344874ef', '976ff10c-814b-4ea4-8914-5aab457ff708', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 13, 'COMPLETED',
        669.67, 'علاج ترميم تجميلي - السن رقم 13', '2025-04-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('4b3b3bc3-077f-46f6-ba22-3b4836e54b68', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ae32aab1-387f-4742-a104-9593c8820a09', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-04-26 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3dcc3a3d-95ab-408e-a748-c0d8500844d3', '4b3b3bc3-077f-46f6-ba22-3b4836e54b68', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        1616.93, 'علاج قشرة تجميلية', '2025-04-26',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5a987b44-eb83-41e4-adb3-f665f3a4820b', '4b3b3bc3-077f-46f6-ba22-3b4836e54b68', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 40, 'COMPLETED',
        1368.17, 'علاج علاج العصب - السن رقم 40', '2025-04-26',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f4abc276-220a-4f9d-8b6a-f773167af2b2', '4b3b3bc3-077f-46f6-ba22-3b4836e54b68', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        166.15, 'علاج تنظيف الأسنان', '2025-04-26',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7f695dbf-b0c4-44d7-b729-01957c863803', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'ae32aab1-387f-4742-a104-9593c8820a09', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-01-02 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fb5d137a-ea86-477e-a82d-30c8561571b0', '7f695dbf-b0c4-44d7-b729-01957c863803', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 25, 'COMPLETED',
        154.90, 'علاج تنظيف الأسنان - السن رقم 25', '2025-01-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('96c304e7-4f4c-453f-a37e-455b2286d1fc', '7f695dbf-b0c4-44d7-b729-01957c863803', 'ae32aab1-387f-4742-a104-9593c8820a09',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 35, 'COMPLETED',
        3160.38, 'علاج جسر الأسنان - السن رقم 35', '2025-01-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ac785d8d-10ea-4e6d-a610-8745cd030670', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b9b53000-347b-499c-9259-a16709d58358', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-08-26 17:08:22+03',
        60, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3769c77c-55e8-4ed8-8aab-841bb49f6acd', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b9b53000-347b-499c-9259-a16709d58358', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-12-04 17:08:22+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d6c28bad-6848-4b4e-aa0f-02e676922ae5', '3769c77c-55e8-4ed8-8aab-841bb49f6acd', 'b9b53000-347b-499c-9259-a16709d58358',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 15, 'COMPLETED',
        320.31, 'علاج خلع الأسنان - السن رقم 15', '2024-12-04',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9410f713-463c-4cd9-a029-b5e13bc68214', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b9b53000-347b-499c-9259-a16709d58358', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-12-14 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a10ff056-9983-49d7-84b1-29608e7679d4', '9410f713-463c-4cd9-a029-b5e13bc68214', 'b9b53000-347b-499c-9259-a16709d58358',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 45, 'COMPLETED',
        4844.37, 'علاج زراعة الأسنان - السن رقم 45', '2024-12-14',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fe771743-9871-4f48-8552-3c9dd35ccd11', '9410f713-463c-4cd9-a029-b5e13bc68214', 'b9b53000-347b-499c-9259-a16709d58358',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 29, 'COMPLETED',
        1694.49, 'علاج قشرة تجميلية - السن رقم 29', '2024-12-14',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('81cf5ba0-7380-4210-b71b-051c2c0c3f52', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b9b53000-347b-499c-9259-a16709d58358', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-07-30 17:08:22+03',
        90, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('284e0257-f82d-485b-8a43-a36481b6f803', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5a0072a5-bc9f-40e0-a289-b750dfb2dc87', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-09-14 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('20a17c5c-e900-4214-b9d4-075abe3edaba', '284e0257-f82d-485b-8a43-a36481b6f803', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 46, 'COMPLETED',
        5790.91, 'علاج طقم أسنان - السن رقم 46', '2024-09-14',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1ad9f603-82e7-4437-9f14-5b1399438f30', '284e0257-f82d-485b-8a43-a36481b6f803', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        124.14, 'علاج حشو وقائي', '2024-09-14',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('50f9e9ca-018a-4318-87cf-2aeeb3576d1f', '284e0257-f82d-485b-8a43-a36481b6f803', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 12, 'COMPLETED',
        2386.85, 'علاج قشرة تجميلية - السن رقم 12', '2024-09-14',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1dfc3bfc-3a08-4742-9903-a67e04f69df3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5a0072a5-bc9f-40e0-a289-b750dfb2dc87', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-08-15 17:08:22+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d445b50e-2553-4149-b512-7c8ba8cb7505', '1dfc3bfc-3a08-4742-9903-a67e04f69df3', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 14, 'COMPLETED',
        469.60, 'علاج خلع الأسنان - السن رقم 14', '2024-08-15',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('56323d21-ac1f-48d5-8734-ef3f6af7093b', '1dfc3bfc-3a08-4742-9903-a67e04f69df3', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        321.58, 'علاج خلع الأسنان', '2024-08-15',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('485a3e05-7b51-4462-8b80-cf2c06ffda3e', '1dfc3bfc-3a08-4742-9903-a67e04f69df3', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        123.23, 'علاج تنظيف الأسنان', '2024-08-15',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('614c848f-a9a5-486a-be67-60806619ecaa', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5a0072a5-bc9f-40e0-a289-b750dfb2dc87', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-06-24 17:08:22+03',
        90, 'NO_SHOW', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('4d13730b-b52e-4bd8-8d86-0b54aee84766', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bac8559-06ab-4672-8336-9ad2a0cadce0', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-03-03 17:08:22+03',
        45, 'NO_SHOW', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e80c2777-1f9b-4965-92d6-1e9ef262a4ba', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bac8559-06ab-4672-8336-9ad2a0cadce0', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-12-29 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f6996566-ef1f-4717-b407-1db059a75e86', 'e80c2777-1f9b-4965-92d6-1e9ef262a4ba', '3bac8559-06ab-4672-8336-9ad2a0cadce0',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        332.26, 'علاج علاج اللثة', '2024-12-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('75a6509b-018e-4deb-8e79-bd132ffb0409', 'e80c2777-1f9b-4965-92d6-1e9ef262a4ba', '3bac8559-06ab-4672-8336-9ad2a0cadce0',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 22, 'COMPLETED',
        179.74, 'علاج تنظيف الأسنان - السن رقم 22', '2024-12-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('07ac650c-de80-4cb7-aa67-1baf11bc01e0', 'e80c2777-1f9b-4965-92d6-1e9ef262a4ba', '3bac8559-06ab-4672-8336-9ad2a0cadce0',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        716.75, 'علاج ترميم تجميلي', '2024-12-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b229c447-70f4-44e8-b1a0-1fbc769b8271', 'e80c2777-1f9b-4965-92d6-1e9ef262a4ba', '3bac8559-06ab-4672-8336-9ad2a0cadce0',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 48, 'COMPLETED',
        838.71, 'علاج تبييض الأسنان - السن رقم 48', '2024-12-29',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('26047c78-3b89-439d-88d3-a5cd4fe30625', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3bac8559-06ab-4672-8336-9ad2a0cadce0', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-10-14 17:08:22+03',
        120, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('aa9d9b5e-8d68-4fd2-bbc2-3c88dc2f5af5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '12e8d048-1f45-4dfe-920b-b3b28507702a', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-15 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5c4c8d26-1d1f-4482-898d-4ffc20507e7d', 'aa9d9b5e-8d68-4fd2-bbc2-3c88dc2f5af5', '12e8d048-1f45-4dfe-920b-b3b28507702a',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        144.64, 'علاج تنظيف الأسنان', '2025-04-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('aec8b436-bb78-4d8f-ba21-b4b787c5ba5c', 'aa9d9b5e-8d68-4fd2-bbc2-3c88dc2f5af5', '12e8d048-1f45-4dfe-920b-b3b28507702a',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        181.40, 'علاج إزالة الجير', '2025-04-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('66b679ef-e819-4df1-a132-d81510953310', 'aa9d9b5e-8d68-4fd2-bbc2-3c88dc2f5af5', '12e8d048-1f45-4dfe-920b-b3b28507702a',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 37, 'COMPLETED',
        291.83, 'علاج حشو الأسنان - السن رقم 37', '2025-04-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b58ce7c5-727b-41dc-b234-ba209d155573', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '12e8d048-1f45-4dfe-920b-b3b28507702a', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-12-28 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5de70979-3b59-45be-8c9b-46a05babe388', 'b58ce7c5-727b-41dc-b234-ba209d155573', '12e8d048-1f45-4dfe-920b-b3b28507702a',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 11, 'COMPLETED',
        95.66, 'علاج فلورايد - السن رقم 11', '2024-12-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('628ef31c-f852-4431-b2ef-3842e7bca1a6', 'b58ce7c5-727b-41dc-b234-ba209d155573', '12e8d048-1f45-4dfe-920b-b3b28507702a',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        176.35, 'علاج تنظيف الأسنان', '2024-12-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('484963bb-73eb-463d-b796-691dbb6e6548', 'b58ce7c5-727b-41dc-b234-ba209d155573', '12e8d048-1f45-4dfe-920b-b3b28507702a',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 13, 'COMPLETED',
        105.19, 'علاج X_RAY - السن رقم 13', '2024-12-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dfed8a6f-7f31-4dbf-be71-54686e1c3e5f', 'b58ce7c5-727b-41dc-b234-ba209d155573', '12e8d048-1f45-4dfe-920b-b3b28507702a',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 22, 'COMPLETED',
        4372.53, 'علاج زراعة الأسنان - السن رقم 22', '2024-12-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('24297187-1a3f-44f4-bd91-e07ee7562005', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '9018a956-250f-40ca-be2c-bc6a37ec8b19', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-10-15 17:08:22+03',
        30, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9f88565b-6232-4423-9ddf-838d3bf36f65', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '9018a956-250f-40ca-be2c-bc6a37ec8b19', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-07-26 17:08:22+03',
        60, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2c024893-7d71-4621-8605-8df18682e165', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '9018a956-250f-40ca-be2c-bc6a37ec8b19', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-08-27 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('33ec4e82-9a50-4e4c-8b4b-4765684dcb72', '2c024893-7d71-4621-8605-8df18682e165', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 44, 'COMPLETED',
        438.28, 'علاج خلع الأسنان - السن رقم 44', '2024-08-27',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('659977ec-86c1-47ad-a7fb-3db50d27134a', '2c024893-7d71-4621-8605-8df18682e165', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 34, 'COMPLETED',
        939.20, 'علاج تبييض الأسنان - السن رقم 34', '2024-08-27',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a2deb165-ab55-45c3-89fc-09fa9aa3f540', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '9018a956-250f-40ca-be2c-bc6a37ec8b19', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-02-11 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('29ad630e-d3f7-45e2-926d-266a2a3980ad', 'a2deb165-ab55-45c3-89fc-09fa9aa3f540', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 26, 'COMPLETED',
        110.32, 'علاج حشو وقائي - السن رقم 26', '2025-02-11',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8ba3ab9d-29e3-4435-905b-b2984548f598', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '9018a956-250f-40ca-be2c-bc6a37ec8b19', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-06-02 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6a066b46-582e-4240-8863-c86b376febf4', '8ba3ab9d-29e3-4435-905b-b2984548f598', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        95.26, 'علاج فلورايد', '2025-06-02',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7a2dc0bc-9d8e-4bf0-912d-4bb2cc5e0198', '8ba3ab9d-29e3-4435-905b-b2984548f598', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        268.59, 'علاج حشو الأسنان', '2025-06-02',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0553211b-e9fd-46b3-a002-d04ab23b0a6a', '8ba3ab9d-29e3-4435-905b-b2984548f598', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 46, 'COMPLETED',
        3538.87, 'علاج طقم جزئي - السن رقم 46', '2025-06-02',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0b195e69-c4da-48ff-9028-e27673849053', '8ba3ab9d-29e3-4435-905b-b2984548f598', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        3490.25, 'علاج جسر الأسنان', '2025-06-02',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('532545c5-c110-4a6d-88dd-13e5023246ae', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d6f6927-2e1b-4928-ad7d-4416b021719e', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-05 17:08:22+03',
        90, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3c382886-bd2c-435f-ad8a-06d095fd904e', '532545c5-c110-4a6d-88dd-13e5023246ae', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 17, 'COMPLETED',
        1863.31, 'علاج قشرة تجميلية - السن رقم 17', '2025-01-05',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dcf9c8a7-ba01-4ca9-9a1d-07944c643ea3', '532545c5-c110-4a6d-88dd-13e5023246ae', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 18, 'COMPLETED',
        551.75, 'علاج مثبت تقويم - السن رقم 18', '2025-01-05',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d6d07433-1611-4a40-a9de-65d30529bb2c', '532545c5-c110-4a6d-88dd-13e5023246ae', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        504.24, 'علاج مثبت تقويم', '2025-01-05',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b0762002-d05d-42ed-9130-5852de9e42cf', '532545c5-c110-4a6d-88dd-13e5023246ae', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        1445.66, 'علاج تاج الأسنان', '2025-01-05',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1828f4eb-81e5-4507-a85f-9358c31e9141', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d6f6927-2e1b-4928-ad7d-4416b021719e', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-08-09 17:08:22+03',
        120, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d15aa800-02b3-42d8-b58d-14e70802bbc3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d6f6927-2e1b-4928-ad7d-4416b021719e', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-20 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d5fea180-5d52-46a0-9f4a-47b23eb13980', 'd15aa800-02b3-42d8-b58d-14e70802bbc3', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        404.36, 'علاج علاج اللثة', '2025-02-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('97aaa611-09f3-4b63-b262-72a89999a40a', 'd15aa800-02b3-42d8-b58d-14e70802bbc3', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        2153.71, 'علاج قشرة تجميلية', '2025-02-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('de476129-9dc4-40df-a67a-2caff5b63704', 'd15aa800-02b3-42d8-b58d-14e70802bbc3', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        366.13, 'علاج خلع الأسنان', '2025-02-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('38b2ac26-23e2-4da3-a2bc-6d2a0e8e4c7a', 'd15aa800-02b3-42d8-b58d-14e70802bbc3', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 28, 'COMPLETED',
        7032.74, 'علاج تقويم - السن رقم 28', '2025-02-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('07035fd1-daa0-43fd-abd7-e830f34cee5a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d6f6927-2e1b-4928-ad7d-4416b021719e', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-05-14 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6cf69ba8-1dca-4cfc-8e32-33a74a49d65f', '07035fd1-daa0-43fd-abd7-e830f34cee5a', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 42, 'COMPLETED',
        466.43, 'علاج خلع الأسنان - السن رقم 42', '2025-05-14',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c208216b-34d7-49a5-82be-5692397d6275', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d6f6927-2e1b-4928-ad7d-4416b021719e', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-09-15 17:08:22+03',
        90, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d76625ef-e393-4553-8ea8-866e4adf87f2', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d6f6927-2e1b-4928-ad7d-4416b021719e', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-09-22 17:08:22+03',
        90, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b733e692-7b5e-4e70-a6b8-155abc92d858', 'd76625ef-e393-4553-8ea8-866e4adf87f2', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 22, 'COMPLETED',
        417.30, 'علاج مثبت تقويم - السن رقم 22', '2024-09-22',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7605e2ac-5cc4-409e-99d5-765a49400901', 'd76625ef-e393-4553-8ea8-866e4adf87f2', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        100.94, 'علاج حشو وقائي', '2024-09-22',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('58f072bd-76a9-4b2b-ae38-9cbd36a9e65a', 'd76625ef-e393-4553-8ea8-866e4adf87f2', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        3994.25, 'علاج زراعة الأسنان', '2024-09-22',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b1988d88-db6f-4b42-a835-6f47259e78fe', 'd76625ef-e393-4553-8ea8-866e4adf87f2', '1d6f6927-2e1b-4928-ad7d-4416b021719e',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        123.30, 'علاج تنظيف الأسنان', '2024-09-22',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e625384f-0e0d-4194-b755-bcab47115160', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-21 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fe4026fa-3713-469f-a8ae-7b7e37afb26f', 'e625384f-0e0d-4194-b755-bcab47115160', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 24, 'COMPLETED',
        725.39, 'علاج تبييض الأسنان - السن رقم 24', '2024-09-21',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b05760f3-64c6-41f5-93cf-bffebdc0f587', 'e625384f-0e0d-4194-b755-bcab47115160', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 27, 'COMPLETED',
        698.66, 'علاج تبييض الأسنان - السن رقم 27', '2024-09-21',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7a616881-3daf-4d33-8851-1ccd3901e48d', 'e625384f-0e0d-4194-b755-bcab47115160', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        3210.78, 'علاج طقم جزئي', '2024-09-21',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('77e52cf4-34cc-447c-9529-e36b00f45d0e', 'e625384f-0e0d-4194-b755-bcab47115160', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 47, 'COMPLETED',
        449.96, 'علاج مثبت تقويم - السن رقم 47', '2024-09-21',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ced46920-94db-453c-832f-9d0632077ef5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e614e5fe-eafc-4da8-9666-9e4b2bef565d', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-07-26 17:08:22+03',
        60, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1c232f2d-5a6b-415b-ba8f-7ea393800d15', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-10-05 17:08:22+03',
        45, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a0bee8fa-18bb-43db-9443-d19893776129', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e614e5fe-eafc-4da8-9666-9e4b2bef565d', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-09-29 17:08:22+03',
        60, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('91f26a89-39cc-4402-876c-235e75441faf', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-06-02 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c5a65e36-a79a-4132-af7c-2194bc307742', '91f26a89-39cc-4402-876c-235e75441faf', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 15, 'COMPLETED',
        533.49, 'علاج مثبت تقويم - السن رقم 15', '2025-06-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fcfb165b-0408-44f2-9b7c-2443399a2580', '91f26a89-39cc-4402-876c-235e75441faf', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 35, 'COMPLETED',
        137.13, 'علاج حشو وقائي - السن رقم 35', '2025-06-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6f88f449-514a-491d-aa0e-af7890d80036', '91f26a89-39cc-4402-876c-235e75441faf', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 35, 'COMPLETED',
        7800.88, 'علاج تقويم - السن رقم 35', '2025-06-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('55847992-e491-4de7-97c4-3510a8f7650b', '91f26a89-39cc-4402-876c-235e75441faf', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        955.27, 'علاج تبييض الأسنان', '2025-06-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('df5aa9e7-079b-443a-b518-931b581fd94c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-09-28 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('eb28d81f-17f5-4dac-a43b-eb4e752b734a', 'df5aa9e7-079b-443a-b518-931b581fd94c', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        7675.30, 'علاج تقويم', '2024-09-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b5e9f34a-9ace-4010-89d6-a597b292b247', 'df5aa9e7-079b-443a-b518-931b581fd94c', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        1964.15, 'علاج قشرة تجميلية', '2024-09-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('48d49c39-ea48-4a20-982d-b8f1a36fb3b9', 'df5aa9e7-079b-443a-b518-931b581fd94c', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 22, 'COMPLETED',
        2242.07, 'علاج قشرة تجميلية - السن رقم 22', '2024-09-28',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('57881f05-d06e-4937-9744-800338af0932', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-09-21 17:08:22+03',
        60, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('da05eebd-100d-479d-bc27-cde137e8dac9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c788722c-3a96-4bd5-9139-33a8d8e65565', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-09 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('08fdd9b9-e957-4008-8450-8045e01e6400', 'da05eebd-100d-479d-bc27-cde137e8dac9', 'c788722c-3a96-4bd5-9139-33a8d8e65565',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        8053.99, 'علاج تقويم', '2025-05-09',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2dbbfeb7-2fbf-4f83-b4c2-7d9fa6e502d1', 'da05eebd-100d-479d-bc27-cde137e8dac9', 'c788722c-3a96-4bd5-9139-33a8d8e65565',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 16, 'COMPLETED',
        203.83, 'علاج إزالة الجير - السن رقم 16', '2025-05-09',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7ac802b1-0e2d-4d90-adaa-4321bf547cde', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c788722c-3a96-4bd5-9139-33a8d8e65565', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-10-02 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a416a9e2-db83-4af4-b268-6dd40d434023', '7ac802b1-0e2d-4d90-adaa-4321bf547cde', 'c788722c-3a96-4bd5-9139-33a8d8e65565',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        102.60, 'علاج حشو وقائي', '2024-10-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fd21efbe-ff1b-42bc-a723-578933995540', '7ac802b1-0e2d-4d90-adaa-4321bf547cde', 'c788722c-3a96-4bd5-9139-33a8d8e65565',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        430.38, 'علاج خلع الأسنان', '2024-10-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('50037ad3-7904-4a6f-afd2-10c2277988e6', '7ac802b1-0e2d-4d90-adaa-4321bf547cde', 'c788722c-3a96-4bd5-9139-33a8d8e65565',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 15, 'COMPLETED',
        473.92, 'علاج خلع الأسنان - السن رقم 15', '2024-10-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9a4f6b89-49f9-4ab9-8658-8c186e93aa56', '7ac802b1-0e2d-4d90-adaa-4321bf547cde', 'c788722c-3a96-4bd5-9139-33a8d8e65565',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 32, 'COMPLETED',
        109.32, 'علاج حشو وقائي - السن رقم 32', '2024-10-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6bd7dda3-a34f-4741-afbd-ba2cc5bd5731', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'c788722c-3a96-4bd5-9139-33a8d8e65565', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-09-15 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('05626e47-a241-44c2-8ad1-d29a6c5d0cc8', '6bd7dda3-a34f-4741-afbd-ba2cc5bd5731', 'c788722c-3a96-4bd5-9139-33a8d8e65565',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        109.80, 'علاج X_RAY', '2024-09-15',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('631d1ac0-fdc9-4d05-afbc-81f2bdfe6cf1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7ecc8943-b925-4944-8e60-8c0570ad9404', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-08-07 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e1299017-a6cd-4198-ad0b-4127bd05a023', '631d1ac0-fdc9-4d05-afbc-81f2bdfe6cf1', '7ecc8943-b925-4944-8e60-8c0570ad9404',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        118.45, 'علاج حشو وقائي', '2024-08-07',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('dfb647b7-0ddb-4b07-84b8-b9ebc5324656', '631d1ac0-fdc9-4d05-afbc-81f2bdfe6cf1', '7ecc8943-b925-4944-8e60-8c0570ad9404',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        70.83, 'علاج فلورايد', '2024-08-07',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('de7c82b7-3a66-4372-baf0-7c07867fed56', '631d1ac0-fdc9-4d05-afbc-81f2bdfe6cf1', '7ecc8943-b925-4944-8e60-8c0570ad9404',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        8631.11, 'علاج تقويم', '2024-08-07',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d36f09f8-d3ab-432b-b4c4-eb5f980bc173', '631d1ac0-fdc9-4d05-afbc-81f2bdfe6cf1', '7ecc8943-b925-4944-8e60-8c0570ad9404',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 48, 'COMPLETED',
        95.97, 'علاج فلورايد - السن رقم 48', '2024-08-07',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('09619dba-8502-4822-ae33-8fa7f2de33fb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7ecc8943-b925-4944-8e60-8c0570ad9404', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-07-22 17:08:22+03',
        45, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('19b910b8-72f3-44e4-a624-aa5e9f81e3eb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-02-05 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('213e72a5-6943-478f-990a-549253b50821', '19b910b8-72f3-44e4-a624-aa5e9f81e3eb', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 27, 'COMPLETED',
        385.96, 'علاج خلع الأسنان - السن رقم 27', '2025-02-05',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5bde5bea-6c83-4efe-a4d4-75b81bb7274e', '19b910b8-72f3-44e4-a624-aa5e9f81e3eb', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        5680.96, 'علاج طقم أسنان', '2025-02-05',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e448571a-00b5-47a3-a36a-dc0ec70501ed', '19b910b8-72f3-44e4-a624-aa5e9f81e3eb', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 44, 'COMPLETED',
        89.84, 'علاج استشارة - السن رقم 44', '2025-02-05',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('74610909-eb21-47f1-8657-3bc8531b55c3', '19b910b8-72f3-44e4-a624-aa5e9f81e3eb', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 31, 'COMPLETED',
        374.18, 'علاج علاج اللثة - السن رقم 31', '2025-02-05',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1554aaed-b92d-4370-94da-2534523e0cba', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-02-05 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d053a59e-355b-4979-81a8-56508a4a9afd', '1554aaed-b92d-4370-94da-2534523e0cba', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        2275.34, 'علاج قشرة تجميلية', '2025-02-05',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('003be9b5-a6d7-45b5-b19d-1a110e118ce4', '1554aaed-b92d-4370-94da-2534523e0cba', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 26, 'COMPLETED',
        590.83, 'علاج مثبت تقويم - السن رقم 26', '2025-02-05',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6bda3e20-b5bc-4241-ac5e-40421aeb7e9a', '1554aaed-b92d-4370-94da-2534523e0cba', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 11, 'COMPLETED',
        1146.96, 'علاج علاج العصب - السن رقم 11', '2025-02-05',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5975752a-3d40-44a3-b8ff-0fcc5a6dc309', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-16 17:08:22+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('45ebec3d-bd54-4dfd-8d5e-0b69778ca05a', '5975752a-3d40-44a3-b8ff-0fcc5a6dc309', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 20, 'COMPLETED',
        102.95, 'علاج حشو وقائي - السن رقم 20', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('39a4531f-488e-4e31-9150-55cd0b91aecc', '5975752a-3d40-44a3-b8ff-0fcc5a6dc309', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 38, 'COMPLETED',
        323.13, 'علاج حشو الأسنان - السن رقم 38', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ddb998b3-bb32-405f-afba-f4b3ba719128', '5975752a-3d40-44a3-b8ff-0fcc5a6dc309', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        3280.79, 'علاج جسر الأسنان', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6bd31e34-df2f-4975-a876-d71f89b7bd58', '5975752a-3d40-44a3-b8ff-0fcc5a6dc309', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 31, 'COMPLETED',
        3186.17, 'علاج طقم جزئي - السن رقم 31', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7954ea82-c3db-4ef1-81f5-63e30a3c42c6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-11-30 17:08:22+03',
        60, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('096be49d-4ad8-49f6-b373-f63e0b47a97f', '7954ea82-c3db-4ef1-81f5-63e30a3c42c6', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 37, 'COMPLETED',
        993.51, 'علاج علاج العصب - السن رقم 37', '2024-11-30',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4354f2d3-49a5-4494-ba78-0fc3580d6cb2', '7954ea82-c3db-4ef1-81f5-63e30a3c42c6', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        984.17, 'علاج علاج العصب', '2024-11-30',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ad7c516f-7cd6-4256-b898-a2587a7d2aab', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '958efdc2-87c6-4770-8f11-b93c68c0fa8b', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-02-26 17:08:22+03',
        60, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('20d73229-f61e-48ee-89e9-d3b08df25b6b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3057ea2c-1fdc-4d82-8e19-282a3566172b', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-08-06 17:08:22+03',
        120, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f911571d-0a90-443d-be67-a3b8c386fb46', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3057ea2c-1fdc-4d82-8e19-282a3566172b', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-15 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('69cb767e-5bdd-4365-8211-9b86f2a31864', 'f911571d-0a90-443d-be67-a3b8c386fb46', '3057ea2c-1fdc-4d82-8e19-282a3566172b',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 36, 'COMPLETED',
        160.73, 'علاج تنظيف الأسنان - السن رقم 36', '2025-02-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b4b7eb26-fa26-4033-ab1e-2ad1b1160c0d', 'f911571d-0a90-443d-be67-a3b8c386fb46', '3057ea2c-1fdc-4d82-8e19-282a3566172b',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4391.94, 'علاج زراعة الأسنان', '2025-02-15',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fe43013f-12a1-428e-b151-7ca865ac0c36', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3057ea2c-1fdc-4d82-8e19-282a3566172b', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-09-21 17:08:22+03',
        60, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bb9014b0-e477-470b-88c2-d6b73716f4ec', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3057ea2c-1fdc-4d82-8e19-282a3566172b', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-12-16 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4e9aa42e-d0f3-4658-8ce7-d4ec4e24523d', 'bb9014b0-e477-470b-88c2-d6b73716f4ec', '3057ea2c-1fdc-4d82-8e19-282a3566172b',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 34, 'COMPLETED',
        1118.20, 'علاج علاج العصب - السن رقم 34', '2024-12-16',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('34c38a90-34f5-400d-a2b3-cb9a562bc377', 'bb9014b0-e477-470b-88c2-d6b73716f4ec', '3057ea2c-1fdc-4d82-8e19-282a3566172b',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 44, 'COMPLETED',
        3964.40, 'علاج طقم جزئي - السن رقم 44', '2024-12-16',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('47b8e52a-22ce-48a3-b956-a44831d7352f', 'bb9014b0-e477-470b-88c2-d6b73716f4ec', '3057ea2c-1fdc-4d82-8e19-282a3566172b',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        116.46, 'علاج حشو وقائي', '2024-12-16',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ba9c3da4-a9b2-4a5d-9264-e276290a3476', 'bb9014b0-e477-470b-88c2-d6b73716f4ec', '3057ea2c-1fdc-4d82-8e19-282a3566172b',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 14, 'COMPLETED',
        1719.82, 'علاج تاج الأسنان - السن رقم 14', '2024-12-16',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('521b172a-4236-46d8-8668-6f05a201dfa3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3057ea2c-1fdc-4d82-8e19-282a3566172b', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-09-24 17:08:22+03',
        30, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('322da919-49a9-41a9-aaae-6a6209a2dc16', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '3057ea2c-1fdc-4d82-8e19-282a3566172b', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-19 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c29c3d5a-695f-43d5-b4c0-41a06d3f69a1', '322da919-49a9-41a9-aaae-6a6209a2dc16', '3057ea2c-1fdc-4d82-8e19-282a3566172b',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        658.27, 'علاج تبييض الأسنان', '2025-05-19',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e4064b82-d4b2-42d5-9017-b9a0532794e2', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '875b2d85-4697-4477-9297-f19ffda87d21', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-07-21 17:08:22+03',
        90, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8fd0d207-4100-44fd-a2ce-617da724dc12', 'e4064b82-d4b2-42d5-9017-b9a0532794e2', '875b2d85-4697-4477-9297-f19ffda87d21',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        439.11, 'علاج خلع الأسنان', '2024-07-21',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3ff69171-19b5-43e6-adf9-f36b6712941f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '875b2d85-4697-4477-9297-f19ffda87d21', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-12-12 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('18e05924-0c32-49d7-b92b-ca0abe918e15', '3ff69171-19b5-43e6-adf9-f36b6712941f', '875b2d85-4697-4477-9297-f19ffda87d21',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        488.70, 'علاج مثبت تقويم', '2024-12-12',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('aa9d7e37-6e89-43b4-8f60-a68a6a096a9d', '3ff69171-19b5-43e6-adf9-f36b6712941f', '875b2d85-4697-4477-9297-f19ffda87d21',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        3132.33, 'علاج طقم جزئي', '2024-12-12',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('abf486b1-8ed5-4da0-8653-7c4e0132ff73', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '060e5ecd-073f-4930-9a27-e7bfcf8245b0', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-08-13 17:08:22+03',
        45, 'CANCELLED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5f541116-43ae-4f34-bbe9-545d942dc1ab', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '060e5ecd-073f-4930-9a27-e7bfcf8245b0', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-11-23 17:08:22+03',
        120, 'NO_SHOW', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f1379612-1c87-424e-9498-4fb443f29bd5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '060e5ecd-073f-4930-9a27-e7bfcf8245b0', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-07-12 17:08:22+03',
        90, 'NO_SHOW', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bb8572ad-4be8-4324-b332-2e6b7ae37876', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '060e5ecd-073f-4930-9a27-e7bfcf8245b0', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-10-20 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('80b0673d-b3ef-4e52-8d84-c987b8da8107', 'bb8572ad-4be8-4324-b332-2e6b7ae37876', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 18, 'COMPLETED',
        1409.73, 'علاج علاج العصب - السن رقم 18', '2024-10-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b09b7849-bcb2-4a39-be89-9d6f9f57ca70', 'bb8572ad-4be8-4324-b332-2e6b7ae37876', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        3051.95, 'علاج جسر الأسنان', '2024-10-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('35efbbe2-f654-4544-b5c3-e7a0094f8fa3', 'bb8572ad-4be8-4324-b332-2e6b7ae37876', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 14, 'COMPLETED',
        1069.18, 'علاج علاج العصب - السن رقم 14', '2024-10-20',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6d7c2bd9-bc83-4054-bdd1-5844a686496f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '060e5ecd-073f-4930-9a27-e7bfcf8245b0', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-12-07 17:08:22+03',
        120, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('71ca9641-750d-4a38-ab1c-53e49cd36042', '6d7c2bd9-bc83-4054-bdd1-5844a686496f', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 29, 'COMPLETED',
        3311.58, 'علاج طقم جزئي - السن رقم 29', '2024-12-07',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('810f121d-7a25-4b1e-bb89-274f1387215b', '6d7c2bd9-bc83-4054-bdd1-5844a686496f', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 15, 'COMPLETED',
        5756.39, 'علاج طقم أسنان - السن رقم 15', '2024-12-07',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4e6806db-5770-462f-bdf2-b846c2ff1b97', '6d7c2bd9-bc83-4054-bdd1-5844a686496f', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        271.16, 'علاج حشو الأسنان', '2024-12-07',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e940cc80-337f-4ac8-b4ff-d3d642ae594a', '6d7c2bd9-bc83-4054-bdd1-5844a686496f', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 35, 'COMPLETED',
        4815.55, 'علاج زراعة الأسنان - السن رقم 35', '2024-12-07',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('87cf417d-81ee-46f7-b219-e8309a1b6e2c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '060e5ecd-073f-4930-9a27-e7bfcf8245b0', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-11-17 17:08:22+03',
        60, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c2610d45-beab-4b43-84bf-7bb16734db03', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '060e5ecd-073f-4930-9a27-e7bfcf8245b0', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-08-21 17:08:22+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0cdd0928-705b-43f2-a74c-464222bcd6d0', 'c2610d45-beab-4b43-84bf-7bb16734db03', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        573.49, 'علاج مثبت تقويم', '2024-08-21',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('06104896-22e5-48b2-a45d-dcb37c9b600f', 'c2610d45-beab-4b43-84bf-7bb16734db03', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 12, 'COMPLETED',
        1521.61, 'علاج تاج الأسنان - السن رقم 12', '2024-08-21',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5a6b2cff-3fbd-4539-a8c7-e66047b9deb5', 'c2610d45-beab-4b43-84bf-7bb16734db03', '060e5ecd-073f-4930-9a27-e7bfcf8245b0',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 37, 'COMPLETED',
        435.58, 'علاج مثبت تقويم - السن رقم 37', '2024-08-21',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('be1684e8-ad58-4df5-9033-9b6e1c166fc6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '42324f9a-fcea-4f6d-939b-64ec33f3defa', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-09-20 17:08:22+03',
        120, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5464f11a-c8fc-4e77-91c4-548e473fe111', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '42324f9a-fcea-4f6d-939b-64ec33f3defa', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-09-01 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('692429f9-a075-411c-a9c8-77ff9121f3ad', '5464f11a-c8fc-4e77-91c4-548e473fe111', '42324f9a-fcea-4f6d-939b-64ec33f3defa',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        119.30, 'علاج حشو وقائي', '2024-09-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('73b0999d-645d-468a-90c7-fc014d808744', '5464f11a-c8fc-4e77-91c4-548e473fe111', '42324f9a-fcea-4f6d-939b-64ec33f3defa',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 19, 'COMPLETED',
        6499.54, 'علاج تقويم - السن رقم 19', '2024-09-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bbf88bce-2648-4b2b-b773-7108334e46a2', '5464f11a-c8fc-4e77-91c4-548e473fe111', '42324f9a-fcea-4f6d-939b-64ec33f3defa',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        4881.27, 'علاج طقم أسنان', '2024-09-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('96217964-0dc7-4ac3-aa85-35358e273f9d', '5464f11a-c8fc-4e77-91c4-548e473fe111', '42324f9a-fcea-4f6d-939b-64ec33f3defa',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 23, 'COMPLETED',
        86.06, 'علاج فلورايد - السن رقم 23', '2024-09-01',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c2f3be92-cf67-4011-bb34-e10bb9a160c8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '42324f9a-fcea-4f6d-939b-64ec33f3defa', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-05-02 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('334ba933-bce6-4a1f-b59b-e6dd259be302', 'c2f3be92-cf67-4011-bb34-e10bb9a160c8', '42324f9a-fcea-4f6d-939b-64ec33f3defa',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        472.85, 'علاج مثبت تقويم', '2025-05-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fca428b7-b4cb-4126-81e0-1a2491b48200', 'c2f3be92-cf67-4011-bb34-e10bb9a160c8', '42324f9a-fcea-4f6d-939b-64ec33f3defa',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 23, 'COMPLETED',
        7285.51, 'علاج تقويم - السن رقم 23', '2025-05-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('09c0640b-c7a7-4c77-9aaf-4f0609c1302e', 'c2f3be92-cf67-4011-bb34-e10bb9a160c8', '42324f9a-fcea-4f6d-939b-64ec33f3defa',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        190.20, 'علاج إزالة الجير', '2025-05-02',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('2180565f-00ba-4956-8e75-aaae40e077d9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '42324f9a-fcea-4f6d-939b-64ec33f3defa', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-07-30 17:08:22+03',
        60, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e230a36f-5c00-4147-a04a-74e95e5e6094', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '42324f9a-fcea-4f6d-939b-64ec33f3defa', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-09-13 17:08:22+03',
        120, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('18327a72-d00b-4d07-8c95-7ec321f01ed9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-10-18 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('03e177e7-8713-4c21-a358-aa28c58379fd', '18327a72-d00b-4d07-8c95-7ec321f01ed9', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        97.69, 'علاج X_RAY', '2024-10-18',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('40c892a0-71b6-4a2e-af18-3b1fda7cffcd', '18327a72-d00b-4d07-8c95-7ec321f01ed9', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        677.36, 'علاج ترميم تجميلي', '2024-10-18',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('cb6ef407-e2af-4e3b-8681-586c26339731', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-02-04 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('574a4c8f-6fa4-4b8d-87f0-c7e46135491b', 'cb6ef407-e2af-4e3b-8681-586c26339731', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        70.21, 'علاج فلورايد', '2025-02-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('272dbea6-20bf-4921-914b-15b046c75512', 'cb6ef407-e2af-4e3b-8681-586c26339731', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        576.18, 'علاج ترميم تجميلي', '2025-02-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('561a9010-d396-4957-9e25-ec7984e3f00a', 'cb6ef407-e2af-4e3b-8681-586c26339731', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 14, 'COMPLETED',
        3741.36, 'علاج طقم جزئي - السن رقم 14', '2025-02-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ff910525-989a-4617-a622-68fd59687d86', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-09-01 17:08:22+03',
        90, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5c725e0e-a126-4fe6-9d00-e1de8f7c0962', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-03 17:08:22+03',
        90, 'CANCELLED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('aff63d96-e180-4009-ac7e-9f76980d3cca', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-08-06 17:08:22+03',
        45, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6742743d-87dc-4cc4-9ac9-2d0dac8ba8b8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-06-26 17:08:22+03',
        60, 'CANCELLED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('30274492-6817-4762-ab92-c4c25becc8f7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-07 17:08:22+03',
        30, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6ddc2376-ca68-4385-a8f4-278876bfb53c', '30274492-6817-4762-ab92-c4c25becc8f7', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 40, 'COMPLETED',
        5072.57, 'علاج طقم أسنان - السن رقم 40', '2025-04-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('10ce4417-7545-4a83-b4b9-b804011bd119', '30274492-6817-4762-ab92-c4c25becc8f7', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 18, 'COMPLETED',
        112.23, 'علاج X_RAY - السن رقم 18', '2025-04-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('35fbe318-2d4a-437e-a535-3365390ea528', '30274492-6817-4762-ab92-c4c25becc8f7', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1601.62, 'علاج قشرة تجميلية', '2025-04-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f78ef1f1-f31f-41c1-962c-8c0e6ecfa7d9', '30274492-6817-4762-ab92-c4c25becc8f7', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        86.30, 'علاج فلورايد', '2025-04-07',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c841066e-61ad-4c3d-97ca-6225d2e9f7c3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-07-25 17:08:22+03',
        120, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e0c224b3-4212-4f2c-9921-3e4c59d07d0b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-09-14 17:08:22+03',
        90, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('419eae2f-61bc-409a-ac3b-011c7c7e5be0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-14 17:08:22+03',
        45, 'CANCELLED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5d62db18-f0c5-4b16-bcd1-031811f47609', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-02 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fec8e3b5-9b4e-4ba8-a0c8-3d78e282acf6', '5d62db18-f0c5-4b16-bcd1-031811f47609', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 35, 'COMPLETED',
        120.71, 'علاج حشو وقائي - السن رقم 35', '2025-01-02',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c2969b57-6637-46f3-a912-e2e1c8660d87', '5d62db18-f0c5-4b16-bcd1-031811f47609', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 38, 'COMPLETED',
        3112.76, 'علاج جسر الأسنان - السن رقم 38', '2025-01-02',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bb0c8c14-51a3-40ce-bda3-29ee34cb5ea1', '5d62db18-f0c5-4b16-bcd1-031811f47609', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        568.05, 'علاج ترميم تجميلي', '2025-01-02',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('aaed6f8c-f1d6-456f-961d-6fce963125e1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-09-21 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1173f474-c425-4a3f-a5a1-bedb04954cf0', 'aaed6f8c-f1d6-456f-961d-6fce963125e1', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1226.62, 'علاج علاج العصب', '2024-09-21',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8c9011a6-d932-4e05-a1e0-761425619f93', 'aaed6f8c-f1d6-456f-961d-6fce963125e1', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        224.62, 'علاج إزالة الجير', '2024-09-21',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d685be99-651e-49c3-8ca5-a24055f28e10', 'aaed6f8c-f1d6-456f-961d-6fce963125e1', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4978.62, 'علاج زراعة الأسنان', '2024-09-21',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1fe1881f-4d08-40a5-9792-c5fd187ac31f', 'aaed6f8c-f1d6-456f-961d-6fce963125e1', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        538.43, 'علاج ترميم تجميلي', '2024-09-21',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('49942701-2f25-49e0-b3dd-fc267e194a20', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-10-27 17:08:22+03',
        90, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e963403d-3ad3-40de-abd4-e296976d0a54', '49942701-2f25-49e0-b3dd-fc267e194a20', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 44, 'COMPLETED',
        695.37, 'علاج ترميم تجميلي - السن رقم 44', '2024-10-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('642abdc2-1a4a-4276-8659-63d792420a0f', '49942701-2f25-49e0-b3dd-fc267e194a20', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        137.96, 'علاج حشو وقائي', '2024-10-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('55ec7caf-b968-477c-85c4-e94fc734d12b', '49942701-2f25-49e0-b3dd-fc267e194a20', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        4690.81, 'علاج طقم أسنان', '2024-10-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d2d7bcba-087e-43ca-a59f-c0f3bfd439ee', '49942701-2f25-49e0-b3dd-fc267e194a20', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 45, 'COMPLETED',
        3612.42, 'علاج طقم جزئي - السن رقم 45', '2024-10-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('75eec5e2-154d-45e0-af18-b57e9572407f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '5747f021-7809-4e2d-856d-45ebdced300c', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-11-04 17:08:22+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('23d3812b-f00c-467a-a851-691da125fb0f', '75eec5e2-154d-45e0-af18-b57e9572407f', '5747f021-7809-4e2d-856d-45ebdced300c',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 41, 'COMPLETED',
        136.17, 'علاج تنظيف الأسنان - السن رقم 41', '2024-11-04',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('4e41f994-e257-419c-b0e7-569397407ad7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b25049f9-d533-495b-ba24-753998010265', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-03-27 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('81de4b54-524f-40e1-975d-09e03003f016', '4e41f994-e257-419c-b0e7-569397407ad7', 'b25049f9-d533-495b-ba24-753998010265',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 12, 'COMPLETED',
        140.75, 'علاج حشو وقائي - السن رقم 12', '2025-03-27',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('79b5ff0c-be55-456d-8f65-8fb56883df9d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b25049f9-d533-495b-ba24-753998010265', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-10-15 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a973b8c3-8d75-4a65-8628-5fa53e554731', '79b5ff0c-be55-456d-8f65-8fb56883df9d', 'b25049f9-d533-495b-ba24-753998010265',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        7952.66, 'علاج تقويم', '2024-10-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ef0c7f8e-9803-4a4e-9a45-ed18afaf84d2', '79b5ff0c-be55-456d-8f65-8fb56883df9d', 'b25049f9-d533-495b-ba24-753998010265',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 28, 'COMPLETED',
        4877.61, 'علاج زراعة الأسنان - السن رقم 28', '2024-10-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c1534f81-38a6-4888-ad75-db83ecbe1003', '79b5ff0c-be55-456d-8f65-8fb56883df9d', 'b25049f9-d533-495b-ba24-753998010265',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        670.16, 'علاج تبييض الأسنان', '2024-10-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8ec04c1f-600d-43f1-b780-a83314d3a3e5', '79b5ff0c-be55-456d-8f65-8fb56883df9d', 'b25049f9-d533-495b-ba24-753998010265',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        343.70, 'علاج خلع الأسنان', '2024-10-15',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b07f7f55-7a9f-4b88-89d1-47a93fbf2df4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b25049f9-d533-495b-ba24-753998010265', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-04-02 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('582a8411-683b-4f61-8627-84087a30351f', 'b07f7f55-7a9f-4b88-89d1-47a93fbf2df4', 'b25049f9-d533-495b-ba24-753998010265',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 30, 'COMPLETED',
        4222.20, 'علاج زراعة الأسنان - السن رقم 30', '2025-04-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9f782ac1-29dd-440a-a393-b2964b0578b5', 'b07f7f55-7a9f-4b88-89d1-47a93fbf2df4', 'b25049f9-d533-495b-ba24-753998010265',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 21, 'COMPLETED',
        4518.38, 'علاج زراعة الأسنان - السن رقم 21', '2025-04-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bdb5b012-4360-4248-9cd5-143196e1b187', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-02-23 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('86be6861-5ab0-4684-9d87-622a872fa7f9', 'bdb5b012-4360-4248-9cd5-143196e1b187', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        250.02, 'علاج حشو الأسنان', '2025-02-23',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('060c00bd-d0d1-42dc-aca0-ea716af55889', 'bdb5b012-4360-4248-9cd5-143196e1b187', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 46, 'COMPLETED',
        321.86, 'علاج علاج اللثة - السن رقم 46', '2025-02-23',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a20e1a22-0890-4933-a227-920cf980cbb1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-11-25 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('744c8328-1b8c-4901-aee2-187b1f054091', 'a20e1a22-0890-4933-a227-920cf980cbb1', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        9347.82, 'علاج تقويم', '2024-11-25',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1d38f886-ea60-429c-9b6c-c9fd9ce70b6e', 'a20e1a22-0890-4933-a227-920cf980cbb1', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 35, 'COMPLETED',
        2620.83, 'علاج جسر الأسنان - السن رقم 35', '2024-11-25',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('392e15aa-40c7-4d8b-8c75-7390a66493b1', 'a20e1a22-0890-4933-a227-920cf980cbb1', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        6824.12, 'علاج تقويم', '2024-11-25',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4d4a50d3-65b7-4335-b480-66c6f8701a58', 'a20e1a22-0890-4933-a227-920cf980cbb1', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1357.89, 'علاج علاج العصب', '2024-11-25',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('157aad50-51bd-41c1-b4ed-78e015ee0a28', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-12-25 17:08:22+03',
        90, 'NO_SHOW', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ab5a45a2-4dbf-474e-92bd-3f6905c9d723', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-01-25 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7787bedc-81e1-4937-9ece-80d82a6b0be2', 'ab5a45a2-4dbf-474e-92bd-3f6905c9d723', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        776.32, 'علاج تبييض الأسنان', '2025-01-25',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ae516a60-eec4-4552-940a-d65fb1b1bfd7', 'ab5a45a2-4dbf-474e-92bd-3f6905c9d723', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        3163.78, 'علاج جسر الأسنان', '2025-01-25',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1563fbaa-8d54-47c0-846a-c8a9d186dc37', 'ab5a45a2-4dbf-474e-92bd-3f6905c9d723', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 33, 'COMPLETED',
        142.50, 'علاج تنظيف الأسنان - السن رقم 33', '2025-01-25',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7171496b-b7dc-4898-894e-989fda64c2cc', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-01-05 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('55f5796d-098b-455d-b519-6100e96f7ced', '7171496b-b7dc-4898-894e-989fda64c2cc', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 41, 'COMPLETED',
        1688.71, 'علاج تاج الأسنان - السن رقم 41', '2025-01-05',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('cf45e73b-a467-40aa-a1ab-d737c5b8b53c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-08-18 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('38fb1ba0-8e6a-465a-919c-ef2a26dab187', 'cf45e73b-a467-40aa-a1ab-d737c5b8b53c', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 45, 'COMPLETED',
        177.67, 'علاج إزالة الجير - السن رقم 45', '2024-08-18',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1950f07a-bb5e-4c4b-8d7a-bbc4022db999', 'cf45e73b-a467-40aa-a1ab-d737c5b8b53c', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        374.00, 'علاج علاج اللثة', '2024-08-18',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ae28ab0c-8045-4c2f-aa44-7cf7af0c5b26', 'cf45e73b-a467-40aa-a1ab-d737c5b8b53c', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        7134.59, 'علاج تقويم', '2024-08-18',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8a70261a-cc96-4753-8c4a-25b10c2a1e53', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-08-18 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d3ba0431-8fb6-4088-b53a-bc99db05ee35', '8a70261a-cc96-4753-8c4a-25b10c2a1e53', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        1910.56, 'علاج قشرة تجميلية', '2024-08-18',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c4689d1a-d4aa-40b9-92a5-0baea00a9115', '8a70261a-cc96-4753-8c4a-25b10c2a1e53', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 11, 'COMPLETED',
        97.24, 'علاج X_RAY - السن رقم 11', '2024-08-18',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3225dc15-8a8e-4071-958d-404283182d36', '8a70261a-cc96-4753-8c4a-25b10c2a1e53', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 17, 'COMPLETED',
        96.64, 'علاج X_RAY - السن رقم 17', '2024-08-18',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('270235ed-3522-45e7-87d1-808ace2b7b26', '8a70261a-cc96-4753-8c4a-25b10c2a1e53', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 33, 'COMPLETED',
        3379.12, 'علاج طقم جزئي - السن رقم 33', '2024-08-18',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('702ec399-cb9b-4f47-9eb9-77568d103eff', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'b7fa9854-934d-4afa-862c-075bd03b04d0', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-28 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b278d074-5fab-488e-922d-f17321ca1e7a', '702ec399-cb9b-4f47-9eb9-77568d103eff', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 39, 'COMPLETED',
        5036.23, 'علاج طقم أسنان - السن رقم 39', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7b484d06-9e73-452f-9c39-42aeca0aea0b', '702ec399-cb9b-4f47-9eb9-77568d103eff', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 48, 'COMPLETED',
        5250.01, 'علاج زراعة الأسنان - السن رقم 48', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8bb5f585-d7f9-4cd5-95d1-ecfdcdf39fb5', '702ec399-cb9b-4f47-9eb9-77568d103eff', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 12, 'COMPLETED',
        3738.09, 'علاج زراعة الأسنان - السن رقم 12', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('78564b75-c748-4cd5-b215-65863a4740aa', '702ec399-cb9b-4f47-9eb9-77568d103eff', 'b7fa9854-934d-4afa-862c-075bd03b04d0',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 44, 'COMPLETED',
        5560.52, 'علاج طقم أسنان - السن رقم 44', '2024-09-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6f1adddb-4f5a-4ec2-a718-fa42a6ac73ff', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d7de144-435b-4d30-aab6-6fb43d4ac723', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-08-11 17:08:22+03',
        60, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9d346563-504d-4bf5-9546-39f85b187ba6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d7de144-435b-4d30-aab6-6fb43d4ac723', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-05-18 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f6fa77ac-2a9c-436a-b019-4ba752fa578c', '9d346563-504d-4bf5-9546-39f85b187ba6', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 43, 'COMPLETED',
        487.63, 'علاج مثبت تقويم - السن رقم 43', '2025-05-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bcd74f6a-155a-4782-8f05-7525d97c29ab', '9d346563-504d-4bf5-9546-39f85b187ba6', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 25, 'COMPLETED',
        82.77, 'علاج X_RAY - السن رقم 25', '2025-05-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5f5ca98e-061c-4234-a24a-9deaafc39704', '9d346563-504d-4bf5-9546-39f85b187ba6', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        111.42, 'علاج X_RAY', '2025-05-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d2ac1935-4283-49e6-977e-2c90aa61af7a', '9d346563-504d-4bf5-9546-39f85b187ba6', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 14, 'COMPLETED',
        7979.19, 'علاج تقويم - السن رقم 14', '2025-05-18',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c42f0297-3943-4e67-b143-1beb5f9ee662', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d7de144-435b-4d30-aab6-6fb43d4ac723', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-01-26 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8cde858a-aeb8-4a65-b4a3-91f02b784b48', 'c42f0297-3943-4e67-b143-1beb5f9ee662', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 20, 'COMPLETED',
        112.56, 'علاج حشو وقائي - السن رقم 20', '2025-01-26',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9fa0656e-7d2d-4525-8f50-2e4f217ada05', 'c42f0297-3943-4e67-b143-1beb5f9ee662', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        376.57, 'علاج علاج اللثة', '2025-01-26',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cc823210-3815-4014-bc66-d8635a23adf6', 'c42f0297-3943-4e67-b143-1beb5f9ee662', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 32, 'COMPLETED',
        3177.37, 'علاج جسر الأسنان - السن رقم 32', '2025-01-26',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('cffb7173-489a-4bb6-9c36-018fdf2382f1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d7de144-435b-4d30-aab6-6fb43d4ac723', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-03-19 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e2d2bf57-4477-42b2-9586-fb48f9fd0fe0', 'cffb7173-489a-4bb6-9c36-018fdf2382f1', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        176.82, 'علاج إزالة الجير', '2025-03-19',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7796f300-df68-47f3-9e50-0ca77484af90', 'cffb7173-489a-4bb6-9c36-018fdf2382f1', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 25, 'COMPLETED',
        137.27, 'علاج تنظيف الأسنان - السن رقم 25', '2025-03-19',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ed10da17-7d99-4e0c-9cd6-8a78c1bb421e', 'cffb7173-489a-4bb6-9c36-018fdf2382f1', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 42, 'COMPLETED',
        89.14, 'علاج X_RAY - السن رقم 42', '2025-03-19',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('10bd7eb4-ecf6-4e7a-bfff-37a75da90c1b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d7de144-435b-4d30-aab6-6fb43d4ac723', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-10-25 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('90d4cc23-40dd-4fa7-80af-6219ce8c0506', '10bd7eb4-ecf6-4e7a-bfff-37a75da90c1b', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 24, 'COMPLETED',
        443.80, 'علاج علاج اللثة - السن رقم 24', '2024-10-25',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2b41150f-97a4-442c-9cc9-210399d8aa67', '10bd7eb4-ecf6-4e7a-bfff-37a75da90c1b', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        468.82, 'علاج علاج اللثة', '2024-10-25',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('74d1bb3f-0927-469e-9ed0-a6b9e213a26e', '10bd7eb4-ecf6-4e7a-bfff-37a75da90c1b', '1d7de144-435b-4d30-aab6-6fb43d4ac723',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        109.93, 'علاج استشارة', '2024-10-25',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3fd7fde5-faf3-4770-83eb-c38032f4bb8b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '1d7de144-435b-4d30-aab6-6fb43d4ac723', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-08-02 17:08:22+03',
        90, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bdd28cd9-acc6-4506-b2a6-82fd3b78c562', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '89ef4a50-600e-4ad1-8396-9eb6fa992e60', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-11-18 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9527b9a6-a130-42fe-88ca-ce39552eade2', 'bdd28cd9-acc6-4506-b2a6-82fd3b78c562', '89ef4a50-600e-4ad1-8396-9eb6fa992e60',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        165.39, 'علاج إزالة الجير', '2024-11-18',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('07f13556-f5d8-4634-855f-aae92d22be46', 'bdd28cd9-acc6-4506-b2a6-82fd3b78c562', '89ef4a50-600e-4ad1-8396-9eb6fa992e60',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        4175.81, 'علاج طقم جزئي', '2024-11-18',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b8a02428-c18b-4677-a546-18a6bfc64062', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '89ef4a50-600e-4ad1-8396-9eb6fa992e60', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-08-14 17:08:22+03',
        30, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d4ecf3be-762b-46d2-bace-0f8e38ae8b61', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '89ef4a50-600e-4ad1-8396-9eb6fa992e60', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-16 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fb3ac1be-d27e-4055-b739-3ce6a061aa72', 'd4ecf3be-762b-46d2-bace-0f8e38ae8b61', '89ef4a50-600e-4ad1-8396-9eb6fa992e60',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        87.45, 'علاج استشارة', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('468f98d6-2700-4eb9-a6f9-b7246ed92fda', 'd4ecf3be-762b-46d2-bace-0f8e38ae8b61', '89ef4a50-600e-4ad1-8396-9eb6fa992e60',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 29, 'COMPLETED',
        1757.31, 'علاج تاج الأسنان - السن رقم 29', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('d7b0796b-39e8-4867-901a-5d104d68e91e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '89ef4a50-600e-4ad1-8396-9eb6fa992e60', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-09-27 17:08:22+03',
        90, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f2e46cc2-187b-464e-9828-55ff60677d8f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-03-26 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7d9c1bdc-c9dc-4a55-959c-0269d074e86d', 'f2e46cc2-187b-464e-9828-55ff60677d8f', 'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 41, 'COMPLETED',
        97.48, 'علاج حشو وقائي - السن رقم 41', '2025-03-26',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ccbe019e-9459-4a16-aabd-879832c5c8ff', 'f2e46cc2-187b-464e-9828-55ff60677d8f', 'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 43, 'COMPLETED',
        3644.55, 'علاج طقم جزئي - السن رقم 43', '2025-03-26',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('da378ded-c8cd-4bdb-a515-6e8bb90c03b8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-06-26 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6910526d-21bf-4830-b5bf-14343fda7761', 'da378ded-c8cd-4bdb-a515-6e8bb90c03b8', 'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        2821.32, 'علاج طقم جزئي', '2025-06-26',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2e8f907f-5dec-4d43-9485-44af4a727e8a', 'da378ded-c8cd-4bdb-a515-6e8bb90c03b8', 'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 18, 'COMPLETED',
        235.81, 'علاج إزالة الجير - السن رقم 18', '2025-06-26',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('91275ae3-b93c-47c3-b574-e4efa57372cb', 'da378ded-c8cd-4bdb-a515-6e8bb90c03b8', 'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 43, 'COMPLETED',
        838.66, 'علاج تبييض الأسنان - السن رقم 43', '2025-06-26',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e9e474bb-8355-4ac8-aeff-a8d88cdf7c8f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-23 17:08:22+03',
        60, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7b49d4d4-7d71-4ede-a03f-d7fff6e4ff51', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '66d56a9b-17c4-4183-aa15-2db3082c3874', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-09-04 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b4b33ece-8c0c-49d9-b6fe-f84a36408797', '7b49d4d4-7d71-4ede-a03f-d7fff6e4ff51', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        139.42, 'علاج تنظيف الأسنان', '2024-09-04',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c96fa951-002b-48f6-9567-a5cdafa650bf', '7b49d4d4-7d71-4ede-a03f-d7fff6e4ff51', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 48, 'COMPLETED',
        913.31, 'علاج تبييض الأسنان - السن رقم 48', '2024-09-04',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('212017a1-3a0b-494a-9f6f-d53ea2bf0db7', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '66d56a9b-17c4-4183-aa15-2db3082c3874', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-04-06 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('368a35c1-70ce-4f4a-8d27-f267fa1ed20e', '212017a1-3a0b-494a-9f6f-d53ea2bf0db7', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        2368.72, 'علاج قشرة تجميلية', '2025-04-06',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ecde6728-39c9-4f4e-a4d8-40b182bf9a21', '212017a1-3a0b-494a-9f6f-d53ea2bf0db7', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        644.06, 'علاج تبييض الأسنان', '2025-04-06',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('66776904-3225-4901-8974-e95f9784db99', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '66d56a9b-17c4-4183-aa15-2db3082c3874', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-02 17:08:22+03',
        45, 'CANCELLED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('f4017374-d41f-4679-a519-58b69363d487', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '66d56a9b-17c4-4183-aa15-2db3082c3874', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-02-28 17:08:22+03',
        30, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('36813408-7b73-4684-b5c4-d88eb22feb55', 'f4017374-d41f-4679-a519-58b69363d487', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        99.79, 'علاج حشو وقائي', '2025-02-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('58f530de-48fd-4282-bf1f-d6dcfbe27e37', 'f4017374-d41f-4679-a519-58b69363d487', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        602.60, 'علاج ترميم تجميلي', '2025-02-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('47d90824-c6a8-4aad-9fab-d9b408e3e572', 'f4017374-d41f-4679-a519-58b69363d487', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1998.02, 'علاج قشرة تجميلية', '2025-02-28',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6b62ca10-0bd4-491f-b73b-57eb70fb2192', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '66d56a9b-17c4-4183-aa15-2db3082c3874', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-07-19 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('efa8be0a-bc3f-4b42-88a1-a74b25720590', '6b62ca10-0bd4-491f-b73b-57eb70fb2192', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        110.81, 'علاج استشارة', '2024-07-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('99b7fe18-58bc-4b7c-9028-0295491b8b7c', '6b62ca10-0bd4-491f-b73b-57eb70fb2192', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        5981.35, 'علاج طقم أسنان', '2024-07-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('04eca1d3-6b22-414b-9f2b-de09b5bb358f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a84f91b9-21de-47fe-941e-023727b5a321', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-12-21 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4381c303-192f-4b96-a373-643979ce243d', '04eca1d3-6b22-414b-9f2b-de09b5bb358f', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        407.16, 'علاج مثبت تقويم', '2024-12-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8b698897-5f70-48b7-8a34-08498bdd3e5e', '04eca1d3-6b22-414b-9f2b-de09b5bb358f', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 30, 'COMPLETED',
        108.22, 'علاج X_RAY - السن رقم 30', '2024-12-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3613d0aa-ea81-42c3-9117-2bae435dcec0', '04eca1d3-6b22-414b-9f2b-de09b5bb358f', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 34, 'COMPLETED',
        81.72, 'علاج X_RAY - السن رقم 34', '2024-12-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f1995f2a-6b23-4023-adf0-a31e9cea21f2', '04eca1d3-6b22-414b-9f2b-de09b5bb358f', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        316.09, 'علاج حشو الأسنان', '2024-12-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7ed4e9aa-62e8-44d3-b450-afe41152e257', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a84f91b9-21de-47fe-941e-023727b5a321', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-02-10 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('47f5a8c6-a3a1-45a4-9547-eb67651d1cb9', '7ed4e9aa-62e8-44d3-b450-afe41152e257', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 12, 'COMPLETED',
        2324.33, 'علاج قشرة تجميلية - السن رقم 12', '2025-02-10',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('76bbc64e-466d-4f3a-8b97-c5947f1811f3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a84f91b9-21de-47fe-941e-023727b5a321', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-16 17:08:22+03',
        120, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('12def428-17c9-43a7-9ddd-535a258f7233', '76bbc64e-466d-4f3a-8b97-c5947f1811f3', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        2168.35, 'علاج قشرة تجميلية', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d0e9ee62-f607-401e-b042-b769814d345d', '76bbc64e-466d-4f3a-8b97-c5947f1811f3', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        115.67, 'علاج X_RAY', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8c661860-c707-4292-b726-da4e3971f791', '76bbc64e-466d-4f3a-8b97-c5947f1811f3', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 12, 'COMPLETED',
        190.53, 'علاج إزالة الجير - السن رقم 12', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('da022ebe-0ce2-42b1-8cb0-bc2de12e1239', '76bbc64e-466d-4f3a-8b97-c5947f1811f3', 'a84f91b9-21de-47fe-941e-023727b5a321',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 15, 'COMPLETED',
        4170.83, 'علاج زراعة الأسنان - السن رقم 15', '2025-01-16',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3e05768c-8894-48b0-bdc4-c7e0950518b9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'a84f91b9-21de-47fe-941e-023727b5a321', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-07-12 17:08:22+03',
        45, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0e0b9836-8430-4b65-9522-1502f1c82a9a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '959023bc-fabf-46ba-8e10-5a218859f611', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-09-04 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a6cae252-886a-440f-85f0-b520b5087755', '0e0b9836-8430-4b65-9522-1502f1c82a9a', '959023bc-fabf-46ba-8e10-5a218859f611',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        3113.49, 'علاج جسر الأسنان', '2024-09-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c21d3a5e-10a4-44ce-8c0b-ead3876b9006', '0e0b9836-8430-4b65-9522-1502f1c82a9a', '959023bc-fabf-46ba-8e10-5a218859f611',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        1108.64, 'علاج علاج العصب', '2024-09-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fd9c54ba-768a-4542-ab92-d19985951376', '0e0b9836-8430-4b65-9522-1502f1c82a9a', '959023bc-fabf-46ba-8e10-5a218859f611',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 16, 'COMPLETED',
        4719.35, 'علاج زراعة الأسنان - السن رقم 16', '2024-09-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3077d6ac-f973-4950-ac40-1e2e8b1456e0', '0e0b9836-8430-4b65-9522-1502f1c82a9a', '959023bc-fabf-46ba-8e10-5a218859f611',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 23, 'COMPLETED',
        404.82, 'علاج مثبت تقويم - السن رقم 23', '2024-09-04',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9913016a-8b08-4510-bcde-a15ee71db502', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '959023bc-fabf-46ba-8e10-5a218859f611', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-09-10 17:08:22+03',
        60, 'CANCELLED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b45fa301-ab7f-415e-9e6e-312aab20acc0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '959023bc-fabf-46ba-8e10-5a218859f611', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-08-27 17:08:22+03',
        90, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f135270b-d57f-446a-9bf9-9e892a8a339b', 'b45fa301-ab7f-415e-9e6e-312aab20acc0', '959023bc-fabf-46ba-8e10-5a218859f611',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 16, 'COMPLETED',
        386.69, 'علاج خلع الأسنان - السن رقم 16', '2024-08-27',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('89e2be69-0f80-4f42-aaf6-db76f2361652', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4beb4577-a28a-4ae6-bf53-400e18edccac', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-10-04 17:08:22+03',
        90, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1d91cde9-c494-4049-abf6-1c66769ddc72', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4beb4577-a28a-4ae6-bf53-400e18edccac', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-09-28 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('389f5668-f095-4db4-bde4-5119f713304c', '1d91cde9-c494-4049-abf6-1c66769ddc72', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 29, 'COMPLETED',
        139.55, 'علاج حشو وقائي - السن رقم 29', '2024-09-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1eb650ec-8a17-4cd8-acb4-0da570de385a', '1d91cde9-c494-4049-abf6-1c66769ddc72', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 34, 'COMPLETED',
        251.21, 'علاج حشو الأسنان - السن رقم 34', '2024-09-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('40f3d2c5-82c8-49ff-9083-d62f75258714', '1d91cde9-c494-4049-abf6-1c66769ddc72', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 28, 'COMPLETED',
        2250.97, 'علاج قشرة تجميلية - السن رقم 28', '2024-09-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0cbef647-8d9c-4502-85e8-553e7502ca96', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4beb4577-a28a-4ae6-bf53-400e18edccac', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-08-19 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fc724dd9-dec9-49f8-ac3b-b525cc93d431', '0cbef647-8d9c-4502-85e8-553e7502ca96', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 11, 'COMPLETED',
        280.13, 'علاج حشو الأسنان - السن رقم 11', '2024-08-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ff839573-b464-4784-8f4d-57b3d8a636d9', '0cbef647-8d9c-4502-85e8-553e7502ca96', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        131.68, 'علاج حشو وقائي', '2024-08-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f287e640-0a15-4771-abff-a6951774db1b', '0cbef647-8d9c-4502-85e8-553e7502ca96', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 30, 'COMPLETED',
        387.81, 'علاج خلع الأسنان - السن رقم 30', '2024-08-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9ebeea4c-0450-4859-89d4-a20217e5745c', '0cbef647-8d9c-4502-85e8-553e7502ca96', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 31, 'COMPLETED',
        1713.77, 'علاج قشرة تجميلية - السن رقم 31', '2024-08-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7ab14eb1-a98b-4334-a004-c80b57745b84', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4beb4577-a28a-4ae6-bf53-400e18edccac', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-03-10 17:08:22+03',
        120, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('75606c30-77da-435d-998e-7cac80ccc291', '7ab14eb1-a98b-4334-a004-c80b57745b84', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        107.93, 'علاج X_RAY', '2025-03-10',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1a4fc434-966f-4ff1-956d-81f14852edde', '7ab14eb1-a98b-4334-a004-c80b57745b84', '4beb4577-a28a-4ae6-bf53-400e18edccac',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        471.09, 'علاج علاج اللثة', '2025-03-10',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1002bd76-682d-4a0b-8ffc-c0b6fdef77b0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4beb4577-a28a-4ae6-bf53-400e18edccac', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-09-10 17:08:22+03',
        30, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7882ee12-5a94-4d43-b592-e5a18147b070', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4beb4577-a28a-4ae6-bf53-400e18edccac', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-09-10 17:08:22+03',
        120, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('41b97a8b-94fc-43be-af12-ead192c26c84', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4beb4577-a28a-4ae6-bf53-400e18edccac', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-09-17 17:08:22+03',
        45, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('845c9b4c-dd8e-4b99-bfe0-0311e4b2f01c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-11-28 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f5b6cf80-87eb-40c7-a58a-23e265977370', '845c9b4c-dd8e-4b99-bfe0-0311e4b2f01c', '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 44, 'COMPLETED',
        1080.95, 'علاج علاج العصب - السن رقم 44', '2024-11-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e5003161-7bcd-4ef9-87cf-997ce197daeb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-20 17:08:22+03',
        60, 'CANCELLED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c2f3c17a-c34a-4d0a-b3cd-f86cd48a466b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-08-17 17:08:22+03',
        30, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('cc8717cb-01b2-4c42-b869-0f8db6af5141', 'c2f3c17a-c34a-4d0a-b3cd-f86cd48a466b', '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 25, 'COMPLETED',
        558.02, 'علاج مثبت تقويم - السن رقم 25', '2024-08-17',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('23b0b287-8863-4a6d-a5bc-06d61a60cd7a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-08-24 17:08:22+03',
        60, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1a551881-dbd3-4e70-9926-ef95510cc7c3', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-09-19 17:08:22+03',
        30, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e5002576-859c-4346-853e-1cc2a0ee3f3d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-08-23 17:08:22+03',
        120, 'CANCELLED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b95b1ee4-060a-4d5e-97db-0c132ebfac56', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-10-20 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('63dd839a-7335-43fc-9d2f-69d37c525f7c', 'b95b1ee4-060a-4d5e-97db-0c132ebfac56', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 40, 'COMPLETED',
        1902.43, 'علاج قشرة تجميلية - السن رقم 40', '2024-10-20',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('437ae245-d951-4630-b2af-78f082cbe965', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-10-10 17:08:22+03',
        60, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('60910378-6ff9-4d8a-9af7-147c7cdcd09c', '437ae245-d951-4630-b2af-78f082cbe965', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        3170.74, 'علاج طقم جزئي', '2024-10-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('90fa3349-05e8-4030-8c75-654f5cc558f5', '437ae245-d951-4630-b2af-78f082cbe965', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 34, 'COMPLETED',
        8383.97, 'علاج تقويم - السن رقم 34', '2024-10-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fdb3abc8-9c55-4e6d-afbe-91862f36be75', '437ae245-d951-4630-b2af-78f082cbe965', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 44, 'COMPLETED',
        270.47, 'علاج حشو الأسنان - السن رقم 44', '2024-10-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('267063ea-c04b-4b7f-9076-8de83cf6e5f0', '437ae245-d951-4630-b2af-78f082cbe965', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 44, 'COMPLETED',
        321.94, 'علاج خلع الأسنان - السن رقم 44', '2024-10-10',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a40ff55d-6a38-421b-ace4-2d188a17a505', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-02-14 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('bd852f1d-d7d2-4647-a7ae-f7db6ec2cb53', 'a40ff55d-6a38-421b-ace4-2d188a17a505', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 14, 'COMPLETED',
        453.09, 'علاج علاج اللثة - السن رقم 14', '2025-02-14',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6b5afa83-ceec-451a-aff0-dc91ae59bca6', 'a40ff55d-6a38-421b-ace4-2d188a17a505', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 36, 'COMPLETED',
        107.12, 'علاج حشو وقائي - السن رقم 36', '2025-02-14',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('40a694b1-ea15-4010-9791-c9fd93a9c0c5', 'a40ff55d-6a38-421b-ace4-2d188a17a505', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        2029.09, 'علاج قشرة تجميلية', '2025-02-14',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c031ab62-d6fc-4767-aa78-732895f3214b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-02-13 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b47dc9b4-939a-4ae6-b55f-8b29c0e9a158', 'c031ab62-d6fc-4767-aa78-732895f3214b', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 26, 'COMPLETED',
        217.90, 'علاج إزالة الجير - السن رقم 26', '2025-02-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('07d8b85f-841a-40ae-8464-d2041e84ac05', 'c031ab62-d6fc-4767-aa78-732895f3214b', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 24, 'COMPLETED',
        680.29, 'علاج تبييض الأسنان - السن رقم 24', '2025-02-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('183cf6d5-c6de-40b7-99a4-dd70bf91a5ec', 'c031ab62-d6fc-4767-aa78-732895f3214b', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 25, 'COMPLETED',
        599.23, 'علاج ترميم تجميلي - السن رقم 25', '2025-02-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7fafb669-7a20-4ea9-ad00-7d1eb0c052bb', 'c031ab62-d6fc-4767-aa78-732895f3214b', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 15, 'COMPLETED',
        4594.07, 'علاج طقم أسنان - السن رقم 15', '2025-02-13',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9b892624-0984-4983-8fcb-21fc0d83fe37', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-08-13 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0cb70944-ea68-4226-9c54-4551a132506b', '9b892624-0984-4983-8fcb-21fc0d83fe37', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 35, 'COMPLETED',
        95.47, 'علاج استشارة - السن رقم 35', '2024-08-13',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c29352da-86b2-4e8d-a457-f411c00a32cb', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-07-25 17:08:22+03',
        45, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('6202d948-24c4-475a-8ac4-f3498c752e67', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-08-14 17:08:22+03',
        30, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e5641ff2-af38-43b3-8c87-61a84ecc4a2f', '6202d948-24c4-475a-8ac4-f3498c752e67', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 35, 'COMPLETED',
        558.94, 'علاج ترميم تجميلي - السن رقم 35', '2024-08-14',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('30e913c9-2b84-4745-8c6b-590a44bac2a1', '6202d948-24c4-475a-8ac4-f3498c752e67', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 28, 'COMPLETED',
        105.63, 'علاج حشو وقائي - السن رقم 28', '2024-08-14',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('879e76fc-9cbc-436d-8300-68afd39202f2', '6202d948-24c4-475a-8ac4-f3498c752e67', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 46, 'COMPLETED',
        1266.80, 'علاج علاج العصب - السن رقم 46', '2024-08-14',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7afdacb4-fd47-411a-b508-fe9c1e54f7e1', '6202d948-24c4-475a-8ac4-f3498c752e67', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 46, 'COMPLETED',
        86.96, 'علاج X_RAY - السن رقم 46', '2024-08-14',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('134e0394-cbd4-4fd1-ac95-7287b2d73c4d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'cffe62fb-dd1c-444d-b911-422ba6f25a81', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-11-28 17:08:22+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7aa28cd8-2077-465d-af2e-a987909805a2', '134e0394-cbd4-4fd1-ac95-7287b2d73c4d', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 46, 'COMPLETED',
        642.40, 'علاج تبييض الأسنان - السن رقم 46', '2024-11-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8e90b883-8daf-4762-b23a-41af05974b3f', '134e0394-cbd4-4fd1-ac95-7287b2d73c4d', 'cffe62fb-dd1c-444d-b911-422ba6f25a81',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        382.59, 'علاج خلع الأسنان', '2024-11-28',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('65bfcd35-f7e5-4706-b8e1-6e6076d696e1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-01-01 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('376bc9cc-b2c6-4ce8-b4a2-15b940031f78', '65bfcd35-f7e5-4706-b8e1-6e6076d696e1', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        83.75, 'علاج استشارة', '2025-01-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7a640dc5-b9ff-4ea2-849b-4dec730b29d1', '65bfcd35-f7e5-4706-b8e1-6e6076d696e1', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        423.37, 'علاج خلع الأسنان', '2025-01-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b161303c-10cb-4e31-9aef-c704c121f0d8', '65bfcd35-f7e5-4706-b8e1-6e6076d696e1', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 48, 'COMPLETED',
        476.66, 'علاج مثبت تقويم - السن رقم 48', '2025-01-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9753616f-638f-41a5-8c62-532594964a69', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-07-23 17:08:22+03',
        30, 'SCHEDULED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('98781eec-96c8-4e07-b907-fec6af29b60c', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-01-25 17:08:22+03',
        120, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8400f3a1-c4b4-4561-9555-172657e6cc80', '98781eec-96c8-4e07-b907-fec6af29b60c', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        101.25, 'علاج X_RAY', '2025-01-25',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5b6214c0-75b6-4c25-9b34-2af2a6e202b2', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-06-14 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6cf29b55-52fd-4788-ae4a-23e7c15ddf1f', '5b6214c0-75b6-4c25-9b34-2af2a6e202b2', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        111.86, 'علاج استشارة', '2025-06-14',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ff8049bb-314a-4eb0-b3ed-32752e8806f1', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-07-24 17:08:22+03',
        90, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8a4fddd7-7d8c-451b-a581-62a62cbaf797', 'ff8049bb-314a-4eb0-b3ed-32752e8806f1', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 26, 'COMPLETED',
        9478.59, 'علاج تقويم - السن رقم 26', '2024-07-24',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4e648e58-5a18-4ebb-bd8b-88360e39f6e5', 'ff8049bb-314a-4eb0-b3ed-32752e8806f1', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 25, 'COMPLETED',
        880.17, 'علاج تبييض الأسنان - السن رقم 25', '2024-07-24',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2fc432af-39ec-44d2-9500-d9286e267a79', 'ff8049bb-314a-4eb0-b3ed-32752e8806f1', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        273.39, 'علاج حشو الأسنان', '2024-07-24',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fe1cb837-d190-432e-8373-918032aa5cfa', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-10-31 17:08:22+03',
        45, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7d07f40d-14f0-41da-b6de-a8d07030db22', 'fe1cb837-d190-432e-8373-918032aa5cfa', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 40, 'COMPLETED',
        173.98, 'علاج تنظيف الأسنان - السن رقم 40', '2024-10-31',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c1fd922c-6460-4a8f-97c1-800ad91e5a9b', 'fe1cb837-d190-432e-8373-918032aa5cfa', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        347.44, 'علاج علاج اللثة', '2024-10-31',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a05fc338-ca88-4460-b2d7-734d3fc12467', 'fe1cb837-d190-432e-8373-918032aa5cfa', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 36, 'COMPLETED',
        4229.13, 'علاج زراعة الأسنان - السن رقم 36', '2024-10-31',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5219a008-a3de-4675-83a7-47a789873ea0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-09-25 17:08:22+03',
        60, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('015049ab-a5ea-48ec-9edb-73f3fc9a2f7a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '61f80afa-ecc4-436d-918e-04afb7ef1a16', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-01-22 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7da66095-ee81-4dcf-a3fe-a40f3e314699', '015049ab-a5ea-48ec-9edb-73f3fc9a2f7a', '61f80afa-ecc4-436d-918e-04afb7ef1a16',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 23, 'COMPLETED',
        112.43, 'علاج استشارة - السن رقم 23', '2025-01-22',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3957b702-0986-4510-a6a2-334e12a531c9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c9db862-5ccd-4652-b053-cedc74f5dac5', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-06-21 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a1ab3ff8-584c-46e0-b229-de0aa7763df0', '3957b702-0986-4510-a6a2-334e12a531c9', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 43, 'COMPLETED',
        5891.42, 'علاج طقم أسنان - السن رقم 43', '2025-06-21',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2abaf9c7-6572-468c-bf72-7df18e0fe455', '3957b702-0986-4510-a6a2-334e12a531c9', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 43, 'COMPLETED',
        92.97, 'علاج فلورايد - السن رقم 43', '2025-06-21',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fc498729-4984-4a0a-a402-332558f19525', '3957b702-0986-4510-a6a2-334e12a531c9', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 28, 'COMPLETED',
        127.40, 'علاج حشو وقائي - السن رقم 28', '2025-06-21',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bb45b143-3fbc-4eb7-8f8b-38e0a1353dae', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c9db862-5ccd-4652-b053-cedc74f5dac5', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-09-23 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b929c56f-4e9f-4efd-a95a-4502b581bf5b', 'bb45b143-3fbc-4eb7-8f8b-38e0a1353dae', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        99.16, 'علاج استشارة', '2024-09-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fa133a55-a0ee-4c46-b402-9715edc4e543', 'bb45b143-3fbc-4eb7-8f8b-38e0a1353dae', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 34, 'COMPLETED',
        431.34, 'علاج علاج اللثة - السن رقم 34', '2024-09-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a2ef5dc1-147a-44ee-af2c-da69a2fb9f62', 'bb45b143-3fbc-4eb7-8f8b-38e0a1353dae', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 27, 'COMPLETED',
        1536.89, 'علاج تاج الأسنان - السن رقم 27', '2024-09-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3e7e88fc-4610-4191-ac5b-657935c13d51', 'bb45b143-3fbc-4eb7-8f8b-38e0a1353dae', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 11, 'COMPLETED',
        71.38, 'علاج فلورايد - السن رقم 11', '2024-09-23',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ea904495-0bce-43dd-a1a2-3e2bbe51fbe5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c9db862-5ccd-4652-b053-cedc74f5dac5', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-03-18 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c70dfaec-ee7d-4f7e-8405-e11e934cc3c0', 'ea904495-0bce-43dd-a1a2-3e2bbe51fbe5', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        3392.68, 'علاج جسر الأسنان', '2025-03-18',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('fc1f3d5b-c045-4c66-99fd-d50a6a5087b6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c9db862-5ccd-4652-b053-cedc74f5dac5', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-08-09 17:08:22+03',
        120, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8fed6a51-f010-44ce-a9e4-e6d096731247', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c9db862-5ccd-4652-b053-cedc74f5dac5', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-09-30 17:08:22+03',
        120, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ab1b22df-6424-4f39-a3a1-f30c683865d4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c87b878-6378-4a47-b464-5dc61e1020ee', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-09-18 17:08:22+03',
        45, 'CANCELLED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('24dd7a06-5dd1-417f-87c1-13363f443aa9', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c87b878-6378-4a47-b464-5dc61e1020ee', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-06-12 17:08:22+03',
        30, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6686c622-d4d7-409d-a84a-c98db3daf404', '24dd7a06-5dd1-417f-87c1-13363f443aa9', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        511.61, 'علاج مثبت تقويم', '2025-06-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4b2584c0-f370-408a-8f3e-a9993efa4cb2', '24dd7a06-5dd1-417f-87c1-13363f443aa9', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        4525.03, 'علاج زراعة الأسنان', '2025-06-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0125b759-b5a8-42f6-8bcd-4bcc93e2a618', '24dd7a06-5dd1-417f-87c1-13363f443aa9', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 25, 'COMPLETED',
        556.22, 'علاج مثبت تقويم - السن رقم 25', '2025-06-12',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('34dd4140-685d-41ee-89f5-6856e238e817', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c87b878-6378-4a47-b464-5dc61e1020ee', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-07-08 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('67888779-e7cf-4bc0-9252-54dda8d0c007', '34dd4140-685d-41ee-89f5-6856e238e817', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        1812.94, 'علاج قشرة تجميلية', '2025-07-08',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e62dbc84-2a0b-4797-a780-64d78b9ba722', '34dd4140-685d-41ee-89f5-6856e238e817', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 17, 'COMPLETED',
        801.19, 'علاج تبييض الأسنان - السن رقم 17', '2025-07-08',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7e95276a-d8ed-4748-9cdc-8cf3531218f5', '34dd4140-685d-41ee-89f5-6856e238e817', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 23, 'COMPLETED',
        141.37, 'علاج تنظيف الأسنان - السن رقم 23', '2025-07-08',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('11030eba-3871-4edd-a48a-e41b527ca705', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '7c87b878-6378-4a47-b464-5dc61e1020ee', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-03-13 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1f0fabe9-017b-431b-aedd-a489b6549948', '11030eba-3871-4edd-a48a-e41b527ca705', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 28, 'COMPLETED',
        88.96, 'علاج فلورايد - السن رقم 28', '2025-03-13',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('715eb3d3-a301-4443-b9aa-e4bc819a9d7b', '11030eba-3871-4edd-a48a-e41b527ca705', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 17, 'COMPLETED',
        222.12, 'علاج إزالة الجير - السن رقم 17', '2025-03-13',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fc84d2d0-9b80-4323-8da3-0c21a3997688', '11030eba-3871-4edd-a48a-e41b527ca705', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 14, 'COMPLETED',
        101.35, 'علاج استشارة - السن رقم 14', '2025-03-13',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a43f2cac-e584-47fd-b851-8f57fff4f011', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3c23c7a-cc29-4424-9995-c1e197b8489a', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-03-02 17:08:22+03',
        60, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ff011066-3fe5-40bb-af1e-4462feda984d', 'a43f2cac-e584-47fd-b851-8f57fff4f011', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 23, 'COMPLETED',
        124.97, 'علاج حشو وقائي - السن رقم 23', '2025-03-02',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('92a4db61-0e8a-4cb7-be65-8ef45fc1d6e8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3c23c7a-cc29-4424-9995-c1e197b8489a', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-10-03 17:08:22+03',
        90, 'CANCELLED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('0611b09b-0fa0-4607-b059-fc866fe3491b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3c23c7a-cc29-4424-9995-c1e197b8489a', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-08-08 17:08:22+03',
        30, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b7b0d869-1415-462f-8f3e-264de8d6269a', '0611b09b-0fa0-4607-b059-fc866fe3491b', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 23, 'COMPLETED',
        387.42, 'علاج علاج اللثة - السن رقم 23', '2024-08-08',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('eb26e808-ad7a-4f4d-8ee3-cc6e2f0839a9', '0611b09b-0fa0-4607-b059-fc866fe3491b', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED',
        1296.32, 'علاج تاج الأسنان', '2024-08-08',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4c9c1fa3-8c04-422e-8c74-ed6398ead3fa', '0611b09b-0fa0-4607-b059-fc866fe3491b', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 13, 'COMPLETED',
        9501.37, 'علاج تقويم - السن رقم 13', '2024-08-08',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('040b6122-4d2b-4f95-a028-25b6452441ae', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3c23c7a-cc29-4424-9995-c1e197b8489a', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-05-27 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f97d7e19-9e4d-46fc-aac1-0d748b5f6438', '040b6122-4d2b-4f95-a028-25b6452441ae', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 28, 'COMPLETED',
        110.99, 'علاج حشو وقائي - السن رقم 28', '2025-05-27',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('885400e5-9c73-48cd-b959-20db35e6d7b4', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3c23c7a-cc29-4424-9995-c1e197b8489a', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-06-30 17:08:22+03',
        90, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('df6f54fb-aff9-4f85-8df6-6b1bfa564654', '885400e5-9c73-48cd-b959-20db35e6d7b4', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        922.38, 'علاج تبييض الأسنان', '2025-06-30',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2d6edbaa-683b-4c5e-9957-20b5ec55a0d8', '885400e5-9c73-48cd-b959-20db35e6d7b4', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        513.32, 'علاج مثبت تقويم', '2025-06-30',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('8c4221ed-3779-426e-8493-f30ff079d1c0', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3c23c7a-cc29-4424-9995-c1e197b8489a', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-05-19 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('71598d35-139a-4694-9a1e-4b34b0bb8b36', '8c4221ed-3779-426e-8493-f30ff079d1c0', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 47, 'COMPLETED',
        1827.45, 'علاج قشرة تجميلية - السن رقم 47', '2025-05-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5b89428a-3bad-4c7a-8742-d6c9171fecd8', '8c4221ed-3779-426e-8493-f30ff079d1c0', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 25, 'COMPLETED',
        1410.06, 'علاج علاج العصب - السن رقم 25', '2025-05-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2ba236af-ac54-45b8-8fd0-4676cf922b6f', '8c4221ed-3779-426e-8493-f30ff079d1c0', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 41, 'COMPLETED',
        98.05, 'علاج حشو وقائي - السن رقم 41', '2025-05-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('a65867ea-90cb-49b7-ac95-1c4d5dc31c30', '8c4221ed-3779-426e-8493-f30ff079d1c0', 'f3c23c7a-cc29-4424-9995-c1e197b8489a',
        (SELECT id FROM procedures WHERE procedure_code = 'PARTIAL'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 48, 'COMPLETED',
        3178.84, 'علاج طقم جزئي - السن رقم 48', '2025-05-19',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('b0f11c85-0c8d-429d-9ebc-d90bb23890c6', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'f3c23c7a-cc29-4424-9995-c1e197b8489a', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-09-11 17:08:22+03',
        120, 'SCHEDULED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('eee827f8-9035-40e0-afc2-ab1d562fc385', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '97a843e7-13f3-4a2e-9009-2f26025ffc08', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-04-21 17:08:22+03',
        120, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('047c3d5c-62e7-43d0-b529-046aa7ff37c4', 'eee827f8-9035-40e0-afc2-ab1d562fc385', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        88.02, 'علاج فلورايد', '2025-04-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2c81fb6b-b000-4042-a473-227b41b62eac', 'eee827f8-9035-40e0-afc2-ab1d562fc385', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'VENEER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        2320.67, 'علاج قشرة تجميلية', '2025-04-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('8f33f03e-439c-4541-881c-de59b3e2a6b6', 'eee827f8-9035-40e0-afc2-ab1d562fc385', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 20, 'COMPLETED',
        5753.70, 'علاج طقم أسنان - السن رقم 20', '2025-04-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('f6df0c9f-5e7d-4e31-805a-9bb2c3158a2c', 'eee827f8-9035-40e0-afc2-ab1d562fc385', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        103.03, 'علاج X_RAY', '2025-04-21',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('280568ae-528f-46ee-b951-2e33a1ca99ed', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '97a843e7-13f3-4a2e-9009-2f26025ffc08', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-07-06 17:08:22+03',
        60, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b96d7d36-8fe0-4f4b-bb2f-9f694a8b2f1a', '280568ae-528f-46ee-b951-2e33a1ca99ed', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        74.50, 'علاج فلورايد', '2025-07-06',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('15c5f49f-0bcc-426c-91ec-b1130c7aab1d', '280568ae-528f-46ee-b951-2e33a1ca99ed', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'BRIDGE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        2678.94, 'علاج جسر الأسنان', '2025-07-06',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('301985ff-d59d-4432-962b-36c2900cfe8d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '97a843e7-13f3-4a2e-9009-2f26025ffc08', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-08-11 17:08:22+03',
        120, 'SCHEDULED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1adf9525-82b4-4d1c-83d0-3e1079a73007', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '97a843e7-13f3-4a2e-9009-2f26025ffc08', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-01-08 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4ea429a3-2d0c-4312-8dfe-aeae0a9cc2c1', '1adf9525-82b4-4d1c-83d0-3e1079a73007', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        810.57, 'علاج تبييض الأسنان', '2025-01-08',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('75840254-afd9-48b3-b814-2b039d41e735', '1adf9525-82b4-4d1c-83d0-3e1079a73007', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 45, 'COMPLETED',
        590.31, 'علاج ترميم تجميلي - السن رقم 45', '2025-01-08',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('83426f35-438d-499c-beb9-c33207201df7', '1adf9525-82b4-4d1c-83d0-3e1079a73007', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 47, 'COMPLETED',
        950.21, 'علاج تبييض الأسنان - السن رقم 47', '2025-01-08',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('acee9b4e-d6f2-46d8-9dee-610d7517971b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '97a843e7-13f3-4a2e-9009-2f26025ffc08', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-08-20 17:08:22+03',
        120, 'SCHEDULED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('020e6c54-d148-4678-ab18-fa5dafc0235a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-07-31 17:08:22+03',
        120, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5d6b61a1-c3f5-4ab7-bc00-46d715795142', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-05-02 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7f7bf40e-907e-4da8-84c0-69b76e59925e', '5d6b61a1-c3f5-4ab7-bc00-46d715795142', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        1288.52, 'علاج تاج الأسنان', '2025-05-02',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6458db86-ab3d-4547-b1d1-605124426944', '5d6b61a1-c3f5-4ab7-bc00-46d715795142', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        4961.55, 'علاج طقم أسنان', '2025-05-02',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('62fd66c2-6fa3-435f-bdd1-6fdea242edaf', '5d6b61a1-c3f5-4ab7-bc00-46d715795142', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NULL, 'COMPLETED',
        9519.20, 'علاج تقويم', '2025-05-02',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('ccad14db-97d7-4b4a-9078-094e5879eed5', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2025-07-21 17:08:22+03',
        45, 'SCHEDULED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e7752cc8-5d44-458f-8e13-e690d0ddff1f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-02-27 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('5ba8e475-a328-4845-83a3-6076e4bef7b9', 'e7752cc8-5d44-458f-8e13-e690d0ddff1f', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        473.56, 'علاج علاج اللثة', '2025-02-27',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1190257e-2068-45e9-a37f-2392012ab10f', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-07-22 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('229c5d29-90e0-4b30-b460-4b21a970f644', '1190257e-2068-45e9-a37f-2392012ab10f', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        5371.20, 'علاج زراعة الأسنان', '2024-07-22',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7e193b68-7b43-4712-bb08-128af1cd2ed1', '1190257e-2068-45e9-a37f-2392012ab10f', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 11, 'COMPLETED',
        66.82, 'علاج فلورايد - السن رقم 11', '2024-07-22',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('69e34e1a-992a-4035-b1b6-b3da9403270d', '1190257e-2068-45e9-a37f-2392012ab10f', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'XRAY'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 32, 'COMPLETED',
        98.28, 'علاج X_RAY - السن رقم 32', '2024-07-22',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('01bec7f6-35c1-4858-a062-59aecf5ff6cd', '1190257e-2068-45e9-a37f-2392012ab10f', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        168.82, 'علاج إزالة الجير', '2024-07-22',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('bee83e20-3e11-4099-9d3a-2d784389086b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-11-17 17:08:22+03',
        45, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('23021db5-265a-4ed6-91ad-bbb12d1deb00', 'bee83e20-3e11-4099-9d3a-2d784389086b', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        227.63, 'علاج إزالة الجير', '2024-11-17',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('637dd105-98e3-4cfa-a0b1-d802c0c94038', 'bee83e20-3e11-4099-9d3a-2d784389086b', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        133.43, 'علاج حشو وقائي', '2024-11-17',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c6827c94-21c4-4021-a62b-c466778ec979', 'bee83e20-3e11-4099-9d3a-2d784389086b', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        136.27, 'علاج تنظيف الأسنان', '2024-11-17',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9f0322ce-27ea-4d11-a038-32fb0d2feb8a', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-04-01 17:08:22+03',
        120, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('9ebade96-04fe-4801-a94c-c35a3d8185aa', '9f0322ce-27ea-4d11-a038-32fb0d2feb8a', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 18, 'COMPLETED',
        436.44, 'علاج خلع الأسنان - السن رقم 18', '2025-04-01',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('86d60cee-bd3a-480a-8aae-34135ed4bd6d', '9f0322ce-27ea-4d11-a038-32fb0d2feb8a', '93dc2d56-7b5e-4607-b3c8-6aa2773fcc52',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 43, 'COMPLETED',
        96.11, 'علاج استشارة - السن رقم 43', '2025-04-01',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('094c415b-35c2-4d0d-b0bc-a8ba92d4610d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e9591707-0295-4c79-ae31-86fce4108d2f', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-07-05 17:08:22+03',
        90, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('13db9ad0-1eab-4e1f-b9f2-b5091a73be1d', '094c415b-35c2-4d0d-b0bc-a8ba92d4610d', 'e9591707-0295-4c79-ae31-86fce4108d2f',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        5259.97, 'علاج طقم أسنان', '2025-07-05',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('84f90240-ba2b-4c3b-b755-799f89b95c3e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e9591707-0295-4c79-ae31-86fce4108d2f', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2025-01-07 17:08:22+03',
        45, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ed60a5ca-5852-4ceb-894a-33453eaf91b2', '84f90240-ba2b-4c3b-b755-799f89b95c3e', 'e9591707-0295-4c79-ae31-86fce4108d2f',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        197.83, 'علاج إزالة الجير', '2025-01-07',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5eef94cc-a37d-45b3-8b3e-b91a48ffc8fd', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e9591707-0295-4c79-ae31-86fce4108d2f', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2025-08-28 17:08:22+03',
        90, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('a0892e14-3556-4275-a959-27fb48946003', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'e9591707-0295-4c79-ae31-86fce4108d2f', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2025-05-10 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('792f20bb-2be7-4f0b-bb84-1794832f17ec', 'a0892e14-3556-4275-a959-27fb48946003', 'e9591707-0295-4c79-ae31-86fce4108d2f',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 26, 'COMPLETED',
        337.03, 'علاج علاج اللثة - السن رقم 26', '2025-05-10',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('04da89e7-6479-4185-9b42-a61b0204f2ed', 'a0892e14-3556-4275-a959-27fb48946003', 'e9591707-0295-4c79-ae31-86fce4108d2f',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 45, 'COMPLETED',
        84.19, 'علاج استشارة - السن رقم 45', '2025-05-10',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('861ae38b-3408-49e1-8dd6-4259ec9fa714', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '28bb748d-3170-4652-87e7-871566ae3e74', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', '2024-09-27 17:08:22+03',
        120, 'CANCELLED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('9779b324-ccb5-483c-80b1-2b4951e86d52', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '28bb748d-3170-4652-87e7-871566ae3e74', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-12-26 17:08:22+03',
        45, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('66ce3393-419e-493f-9bc2-7f0ef026fcbe', '9779b324-ccb5-483c-80b1-2b4951e86d52', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 30, 'COMPLETED',
        9277.94, 'علاج تقويم - السن رقم 30', '2024-12-26',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('e80812e6-8cc4-4772-85d3-9c1983701035', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '28bb748d-3170-4652-87e7-871566ae3e74', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2024-08-04 17:08:22+03',
        60, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('b22ceb36-599c-4e6a-b198-cc2e483bd95d', 'e80812e6-8cc4-4772-85d3-9c1983701035', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 11, 'COMPLETED',
        690.58, 'علاج تبييض الأسنان - السن رقم 11', '2024-08-04',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e02ff2b8-82f2-4ff6-abda-6fb12d871903', 'e80812e6-8cc4-4772-85d3-9c1983701035', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'CROWN'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 43, 'COMPLETED',
        1475.76, 'علاج تاج الأسنان - السن رقم 43', '2024-08-04',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('fb4c80b0-e221-4141-9f7a-23a615c77ede', 'e80812e6-8cc4-4772-85d3-9c1983701035', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 17, 'COMPLETED',
        91.10, 'علاج فلورايد - السن رقم 17', '2024-08-04',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('6302adb6-561c-4265-9b7d-dde2f5d7fd1e', 'e80812e6-8cc4-4772-85d3-9c1983701035', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 12, 'COMPLETED',
        560.50, 'علاج مثبت تقويم - السن رقم 12', '2024-08-04',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('c254c882-ba4d-453b-94c4-7dfcf3e13fad', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '28bb748d-3170-4652-87e7-871566ae3e74', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2024-11-30 17:08:22+03',
        60, 'COMPLETED', 'موعد طوارئ', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3b9b7e6d-18c7-4c3d-9f76-d65134778199', 'c254c882-ba4d-453b-94c4-7dfcf3e13fad', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 40, 'COMPLETED',
        5716.38, 'علاج طقم أسنان - السن رقم 40', '2024-11-30',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('e2807ce5-72f0-4c1d-960c-059c344444d3', 'c254c882-ba4d-453b-94c4-7dfcf3e13fad', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 12, 'COMPLETED',
        4858.05, 'علاج زراعة الأسنان - السن رقم 12', '2024-11-30',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('c409eb27-ce71-48e3-8e1c-dfc37d11cc32', 'c254c882-ba4d-453b-94c4-7dfcf3e13fad', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'CLEAN'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        143.79, 'علاج تنظيف الأسنان', '2024-11-30',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('157c2cdf-5ed4-4bc1-b50d-b9de2baf042e', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '28bb748d-3170-4652-87e7-871566ae3e74', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-06-01 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('12df00d0-344f-4d4b-9585-aba1e6064f60', '157c2cdf-5ed4-4bc1-b50d-b9de2baf042e', '28bb748d-3170-4652-87e7-871566ae3e74',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 33, 'COMPLETED',
        448.89, 'علاج علاج اللثة - السن رقم 33', '2025-06-01',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1e998b85-a370-4b29-8998-5e140881628d', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4756d93c-b914-4fcd-ad22-9374cc1cbba8', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-05-04 17:08:22+03',
        45, 'COMPLETED', 'موعد تنظيف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('21da2455-878d-41d6-879a-3cbfe1748a9c', '1e998b85-a370-4b29-8998-5e140881628d', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 28, 'COMPLETED',
        97.73, 'علاج حشو وقائي - السن رقم 28', '2025-05-04',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2f63cc1f-e32b-4c43-8c63-b1473fd42047', '1e998b85-a370-4b29-8998-5e140881628d', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'ROOT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 28, 'COMPLETED',
        1136.62, 'علاج علاج العصب - السن رقم 28', '2025-05-04',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('be2e4815-1ddb-4139-96cc-edb1171d196d', '1e998b85-a370-4b29-8998-5e140881628d', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        332.33, 'علاج علاج اللثة', '2025-05-04',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d5ea0000-3114-4013-b424-b10c4b924a5c', '1e998b85-a370-4b29-8998-5e140881628d', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'WHITENING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        799.51, 'علاج تبييض الأسنان', '2025-05-04',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('7ae3caab-5dff-4f4b-8ef1-240675528d3b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4756d93c-b914-4fcd-ad22-9374cc1cbba8', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2024-12-19 17:08:22+03',
        45, 'COMPLETED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('799c96d7-824e-41cc-8cf8-5531e6c04231', '7ae3caab-5dff-4f4b-8ef1-240675528d3b', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'IMPLANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        5194.02, 'علاج زراعة الأسنان', '2024-12-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('3dedfcf4-d6af-404c-a4a5-a69ce58091e1', '7ae3caab-5dff-4f4b-8ef1-240675528d3b', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'BONDING'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', 19, 'COMPLETED',
        659.01, 'علاج ترميم تجميلي - السن رقم 19', '2024-12-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('7c2f3944-c142-47a1-8d09-8faf4b7bbad2', '7ae3caab-5dff-4f4b-8ef1-240675528d3b', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'SEALANT'),
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021', NULL, 'COMPLETED',
        138.22, 'علاج حشو وقائي', '2024-12-19',
        '3674d376-fca0-4d74-9aa4-bb2cc13b9021');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('5142aa01-00e2-4fba-bf2c-3e9814bef1c8', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        '4756d93c-b914-4fcd-ad22-9374cc1cbba8', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-11-14 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('21d592c7-a73d-4df6-9ac5-57d0b9236125', '5142aa01-00e2-4fba-bf2c-3e9814bef1c8', '4756d93c-b914-4fcd-ad22-9374cc1cbba8',
        (SELECT id FROM procedures WHERE procedure_code = 'DENTURE'),
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 46, 'COMPLETED',
        4720.30, 'علاج طقم أسنان - السن رقم 46', '2024-11-14',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('1d03888a-e3df-44bd-ad19-1c0c5ee5a2aa', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', '7e77e993-1599-497c-9a23-e5ea870a5d70', '2024-11-17 17:08:22+03',
        30, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1b5a3178-cf4b-41fd-907d-bd5414556c4e', '1d03888a-e3df-44bd-ad19-1c0c5ee5a2aa', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'SCALING'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        225.05, 'علاج إزالة الجير', '2024-11-17',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('1f3e2a4f-2d93-42fa-81ca-d5f7bae4fc1c', '1d03888a-e3df-44bd-ad19-1c0c5ee5a2aa', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'FLUORIDE'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', 43, 'COMPLETED',
        81.93, 'علاج فلورايد - السن رقم 43', '2024-11-17',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('4298ec0c-639a-4eb5-a3c7-ef8391a0b1b4', '1d03888a-e3df-44bd-ad19-1c0c5ee5a2aa', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'CONSULT'),
        '7e77e993-1599-497c-9a23-e5ea870a5d70', NULL, 'COMPLETED',
        103.12, 'علاج استشارة', '2024-11-17',
        '7e77e993-1599-497c-9a23-e5ea870a5d70');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('3cbb5535-8833-4564-9585-2db9034bd029', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', '2024-12-20 17:08:22+03',
        30, 'CANCELLED', 'موعد استشارة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('414726c2-dd93-431a-99f5-96816b2e4ecd', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', '3674d376-fca0-4d74-9aa4-bb2cc13b9021', '2025-09-17 17:08:22+03',
        45, 'SCHEDULED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('673001a2-f1e6-4254-badd-e41f3f3ade04', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'a740d31b-41cc-4677-beac-f6bd82db089a', '2024-09-24 17:08:22+03',
        120, 'COMPLETED', 'موعد فحص روتيني', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('0da0e957-d4c1-4b0c-acf8-e798c6d9bf79', '673001a2-f1e6-4254-badd-e41f3f3ade04', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'RETAINER'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        576.62, 'علاج مثبت تقويم', '2024-09-24',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('ec9ff79e-0747-4b24-809f-80ac0db5c594', '673001a2-f1e6-4254-badd-e41f3f3ade04', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', 18, 'COMPLETED',
        7169.47, 'علاج تقويم - السن رقم 18', '2024-09-24',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('2f4595f0-e408-4ad9-a2ea-d3d5e3805753', '673001a2-f1e6-4254-badd-e41f3f3ade04', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'ORTHO'),
        'a740d31b-41cc-4677-beac-f6bd82db089a', NULL, 'COMPLETED',
        6907.37, 'علاج تقويم', '2024-09-24',
        'a740d31b-41cc-4677-beac-f6bd82db089a');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('357a65ba-d276-4e0c-9a5f-74af3f6c9f70', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', '2025-01-28 17:08:22+03',
        90, 'COMPLETED', 'موعد متابعة علاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('d74e8456-a66b-4e03-b821-ab5bc29701cf', '357a65ba-d276-4e0c-9a5f-74af3f6c9f70', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'GUMTREAT'),
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NULL, 'COMPLETED',
        424.85, 'علاج علاج اللثة', '2025-01-28',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b');
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id,
                          appointment_datetime, duration_minutes, status, notes, created_by)
VALUES ('520bbf6b-468a-4049-bee3-290cc973966b', (SELECT id FROM specialties WHERE name = 'General Dentistry'),
        'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'cfb9217c-ca9d-4983-b4f0-66d4520641d8', '2025-05-01 17:08:22+03',
        60, 'COMPLETED', 'موعد علاج جديد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('463ff05b-f53e-4f4c-8906-e90df1cc0656', '520bbf6b-468a-4049-bee3-290cc973966b', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'EXTRACT'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', NULL, 'COMPLETED',
        373.04, 'علاج خلع الأسنان', '2025-05-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id,
                        tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES ('23ac1c14-0ce8-47c3-8cb0-a45ef51206d2', '520bbf6b-468a-4049-bee3-290cc973966b', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6',
        (SELECT id FROM procedures WHERE procedure_code = 'FILL'),
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8', 24, 'COMPLETED',
        265.49, 'علاج حشو الأسنان - السن رقم 24', '2025-05-01',
        'cfb9217c-ca9d-4983-b4f0-66d4520641d8');
-- Additional Invoices and Payments
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('5db2c5e7-178a-457b-ac02-7db9cff385e8', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87', 'INV-1015',
        '2025-06-12', '2025-07-12',
        2351.45, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d0ceb5a4-3649-459e-8b8a-9f94834e7c95', '5db2c5e7-178a-457b-ac02-7db9cff385e8', 'حشو الأسنان', 1175.72);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b66acf96-249f-4679-9ae7-5b8f669c226b', '5db2c5e7-178a-457b-ac02-7db9cff385e8', 'طقم جزئي', 1175.72);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('a2361dfb-e7e2-442f-a2da-52a6d1c05c27', '5db2c5e7-178a-457b-ac02-7db9cff385e8', '5a0072a5-bc9f-40e0-a289-b750dfb2dc87',
        '2025-06-13', 1129.77,
        'شيك', 'PAYMENT', 'دفعة فاتورة INV-1015',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('e7b83e0c-ffa2-420d-8fb9-08f22959cf63', '81c2b2f8-63bb-488c-a276-d6766d4eae8a', 'INV-1016',
        '2025-06-17', '2025-07-17',
        4555.07, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('27b30c45-8e32-48ec-a288-f938f0d8779e', 'e7b83e0c-ffa2-420d-8fb9-08f22959cf63', 'فلورايد', 2277.54);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b9d3018e-80db-486f-9ba5-d01c0b6a6679', 'e7b83e0c-ffa2-420d-8fb9-08f22959cf63', 'علاج العصب', 2277.54);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('cfcdaeee-514a-4d83-b0b1-ecda2fd34e1b', 'e7b83e0c-ffa2-420d-8fb9-08f22959cf63', '81c2b2f8-63bb-488c-a276-d6766d4eae8a',
        '2025-07-14', 1865.68,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1016',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('cbaf13fe-c04c-46e2-9ac2-fd0ab0121446', '97a843e7-13f3-4a2e-9009-2f26025ffc08', 'INV-1017',
        '2025-07-01', '2025-07-31',
        3526.87, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a8cf6249-f430-4ba8-af48-6dc6bf55ff35', 'cbaf13fe-c04c-46e2-9ac2-fd0ab0121446', 'تاج الأسنان', 3526.87);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('d4d626dd-a85a-4b48-bed0-89ef93ac5264', 'cbaf13fe-c04c-46e2-9ac2-fd0ab0121446', '97a843e7-13f3-4a2e-9009-2f26025ffc08',
        '2025-08-06', 3526.87,
        'نقدي', 'PAYMENT', 'دفعة فاتورة INV-1017',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('6ef4381c-9015-4ad1-bd1d-c0fd534c41ce', 'ae32aab1-387f-4742-a104-9593c8820a09', 'INV-1018',
        '2025-02-15', '2025-03-17',
        3373.77, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e8df9d45-6e03-4f6f-8caa-20dea86d1405', '6ef4381c-9015-4ad1-bd1d-c0fd534c41ce', 'قشرة تجميلية', 1686.88);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('dc40a8bd-dd92-4ac3-8424-913aca95b3e4', '6ef4381c-9015-4ad1-bd1d-c0fd534c41ce', 'ترميم تجميلي', 1686.88);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('ff017e9b-b62f-4e74-b2a5-8db9e1990471', '958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'INV-1019',
        '2025-01-23', '2025-02-22',
        227.29, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('9498ea1b-33c1-4dbf-a048-0f3a66d2c62b', 'ff017e9b-b62f-4e74-b2a5-8db9e1990471', 'قشرة تجميلية', 56.82);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5d85fa12-c60a-47a2-a745-f23769952524', 'ff017e9b-b62f-4e74-b2a5-8db9e1990471', 'حشو الأسنان', 56.82);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6f2c3be4-ec03-4d22-ad84-9787c6b9013b', 'ff017e9b-b62f-4e74-b2a5-8db9e1990471', 'X_RAY', 56.82);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('769eca74-9261-4785-bde6-c822d8f83f09', 'ff017e9b-b62f-4e74-b2a5-8db9e1990471', 'استشارة', 56.82);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('fb8cd68a-a6a3-4df4-bb5e-a3ff05b6dd86', 'ff017e9b-b62f-4e74-b2a5-8db9e1990471', '958efdc2-87c6-4770-8f11-b93c68c0fa8b',
        '2025-02-12', 132.25,
        'نقدي', 'PAYMENT', 'دفعة فاتورة INV-1019',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('6c3376fe-ebb6-4a87-9f0a-bd86ee740d6a', 'cb4208fe-0afc-4844-80c5-e437eda54c4d', 'INV-1020',
        '2025-06-06', '2025-07-06',
        3079.75, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('aa942c21-0801-4cd9-832c-6fd2aabb6894', '6c3376fe-ebb6-4a87-9f0a-bd86ee740d6a', 'خلع الأسنان', 3079.75);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('a6e1f0a9-4bef-437e-b13e-4832464b5bf9', '6c3376fe-ebb6-4a87-9f0a-bd86ee740d6a', 'cb4208fe-0afc-4844-80c5-e437eda54c4d',
        '2025-06-19', 3079.75,
        'تحويل بنكي', 'PAYMENT', 'دفعة فاتورة INV-1020',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('27179679-b81c-45b0-b2d8-66aa98bb1e3b', 'b5ad516c-6b71-4416-b660-056ad1d34f79', 'INV-1021',
        '2025-06-25', '2025-07-25',
        2884.06, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d9b98a36-aae6-4432-baf3-a9d087ea8db2', '27179679-b81c-45b0-b2d8-66aa98bb1e3b', 'تبييض الأسنان', 961.35);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('f5379583-652c-423f-926b-78550bdcff8b', '27179679-b81c-45b0-b2d8-66aa98bb1e3b', 'تقويم', 961.35);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('100da2bb-5fd2-4082-a42b-1d7f51e9489a', '27179679-b81c-45b0-b2d8-66aa98bb1e3b', 'X_RAY', 961.35);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('e6c55eea-f0e9-41f7-8b67-bd76801c95b5', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0', 'INV-1022',
        '2025-03-31', '2025-04-30',
        4155.57, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b602a651-cd9f-4579-bd23-15f57c2d844e', 'e6c55eea-f0e9-41f7-8b67-bd76801c95b5', 'حشو وقائي', 2077.78);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('52b36584-90d7-428c-b9f5-2068bf74bcc4', 'e6c55eea-f0e9-41f7-8b67-bd76801c95b5', 'طقم أسنان', 2077.78);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('25add93a-4b40-45cb-bda9-81573307cda8', 'e6c55eea-f0e9-41f7-8b67-bd76801c95b5', 'aadf80ef-bd99-4ecb-a1ff-653253e68df0',
        '2025-05-02', 2799.21,
        'نقدي', 'PAYMENT', 'دفعة فاتورة INV-1022',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('bc2deb52-042d-4013-add1-9db9a489fcfa', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff', 'INV-1023',
        '2025-06-11', '2025-07-11',
        4431.34, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('cbccebc4-dcdd-46c7-ba0a-84939b519205', 'bc2deb52-042d-4013-add1-9db9a489fcfa', 'طقم أسنان', 1107.83);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a06a456f-39f8-4033-974a-b0282763d8ee', 'bc2deb52-042d-4013-add1-9db9a489fcfa', 'علاج اللثة', 1107.83);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('570fd1ac-754d-447a-a368-c2a89eef52ed', 'bc2deb52-042d-4013-add1-9db9a489fcfa', 'طقم جزئي', 1107.83);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('9e4f64e1-c238-4463-8a03-4ecdfed59931', 'bc2deb52-042d-4013-add1-9db9a489fcfa', 'حشو الأسنان', 1107.83);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('874d21e9-34dd-4054-b06f-46e3cb1b7c81', 'bc2deb52-042d-4013-add1-9db9a489fcfa', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff',
        '2025-07-24', 4431.34,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1023',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('c87b7512-7450-4b6d-b8de-f0fa968abd62', '578912a3-7404-4cc6-8ff7-d4c7332acc45', 'INV-1024',
        '2025-03-24', '2025-04-23',
        3138.35, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('ca02faf3-e301-4a88-aa36-465c36939b38', 'c87b7512-7450-4b6d-b8de-f0fa968abd62', 'زراعة الأسنان', 1046.12);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('4b24712d-90d2-4b4f-9f70-97198a98052e', 'c87b7512-7450-4b6d-b8de-f0fa968abd62', 'فلورايد', 1046.12);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('88208f16-14d6-40b0-9fbf-7aa9c2719ff4', 'c87b7512-7450-4b6d-b8de-f0fa968abd62', 'مثبت تقويم', 1046.12);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('fb8b14a7-20e7-4702-93f1-4a952947f269', 'c87b7512-7450-4b6d-b8de-f0fa968abd62', '578912a3-7404-4cc6-8ff7-d4c7332acc45',
        '2025-04-15', 3138.35,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1024',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('00e98c5e-718f-4e13-83db-84c6a2395311', '0f85ab34-195f-4d5b-8a27-47b733b7678f', 'INV-1025',
        '2025-01-25', '2025-02-24',
        1281.32, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5e9ad12d-3c40-44e1-8a31-ca96ba0ae036', '00e98c5e-718f-4e13-83db-84c6a2395311', 'فلورايد', 640.66);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('08581888-9b84-4d0f-828d-997542770408', '00e98c5e-718f-4e13-83db-84c6a2395311', 'تنظيف الأسنان', 640.66);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('26edd6ba-ef34-4071-a16b-18ec27ac0443', '61f80afa-ecc4-436d-918e-04afb7ef1a16', 'INV-1026',
        '2025-05-31', '2025-06-30',
        3223.11, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2ee78e8d-d9cc-4020-91e0-c2059621f966', '26edd6ba-ef34-4071-a16b-18ec27ac0443', 'X_RAY', 644.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0861cf66-3637-4e9d-a1f4-3e82aa1b3bba', '26edd6ba-ef34-4071-a16b-18ec27ac0443', 'X_RAY', 644.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('ea731a9f-b644-4fda-9331-d03e820fcc80', '26edd6ba-ef34-4071-a16b-18ec27ac0443', 'قشرة تجميلية', 644.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('bde11b23-fae0-491b-b4a7-e475243f8ac7', '26edd6ba-ef34-4071-a16b-18ec27ac0443', 'قشرة تجميلية', 644.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('3e6f2c6b-1ca8-49da-8ef0-74fa7d99333d', '26edd6ba-ef34-4071-a16b-18ec27ac0443', 'تبييض الأسنان', 644.62);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('2b403cd2-e6a9-45f9-819e-306f55763540', '7c87b878-6378-4a47-b464-5dc61e1020ee', 'INV-1027',
        '2025-05-25', '2025-06-24',
        4739.09, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('4e58e96c-b520-44c0-9c4a-a851b98e1f4d', '2b403cd2-e6a9-45f9-819e-306f55763540', 'طقم جزئي', 2369.55);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('3c2107b3-b7d0-48e9-82f8-30ea49594e9d', '2b403cd2-e6a9-45f9-819e-306f55763540', 'فلورايد', 2369.55);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('154cca05-855d-4ade-b4ee-5d14134f011c', '2b403cd2-e6a9-45f9-819e-306f55763540', '7c87b878-6378-4a47-b464-5dc61e1020ee',
        '2025-07-04', 1968.53,
        'شيك', 'PAYMENT', 'دفعة فاتورة INV-1027',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('d528697a-9306-4992-9049-804fdc507cd5', '4756d93c-b914-4fcd-ad22-9374cc1cbba8', 'INV-1028',
        '2025-05-07', '2025-06-06',
        339.23, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2e87fc1c-a4e3-4037-9a8c-212cb5370163', 'd528697a-9306-4992-9049-804fdc507cd5', 'تبييض الأسنان', 84.81);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a1c77d45-9ec0-4b0b-b540-62a15dabbaa5', 'd528697a-9306-4992-9049-804fdc507cd5', 'تقويم', 84.81);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e4ec10e1-9eb2-413d-8e2d-ec3bb6793fbc', 'd528697a-9306-4992-9049-804fdc507cd5', 'زراعة الأسنان', 84.81);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b65627a4-a787-463d-a8dc-a805cf80eb55', 'd528697a-9306-4992-9049-804fdc507cd5', 'خلع الأسنان', 84.81);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('f2925aa1-be17-49ef-ac19-fdf9e52c82f3', '74037d63-ac9e-4428-860f-3c0a0504a40c', 'INV-1029',
        '2025-05-16', '2025-06-15',
        1255.62, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c0d3aa35-2453-4a61-a5a3-04185cd0da38', 'f2925aa1-be17-49ef-ac19-fdf9e52c82f3', 'تنظيف الأسنان', 251.12);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e72165fd-f193-4d0c-b07a-527a34ac43f6', 'f2925aa1-be17-49ef-ac19-fdf9e52c82f3', 'إزالة الجير', 251.12);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6ee533f2-fee7-413d-83f9-8cc7c7def5ca', 'f2925aa1-be17-49ef-ac19-fdf9e52c82f3', 'جسر الأسنان', 251.12);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('199dc782-f40b-496a-8660-838b19f0ac3c', 'f2925aa1-be17-49ef-ac19-fdf9e52c82f3', 'خلع الأسنان', 251.12);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2b962a8d-de33-42d5-a0da-435cc47e6c0b', 'f2925aa1-be17-49ef-ac19-fdf9e52c82f3', 'تنظيف الأسنان', 251.12);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('e05c14a3-b81c-4cc4-8a04-6bcf070973f1', 'f2925aa1-be17-49ef-ac19-fdf9e52c82f3', '74037d63-ac9e-4428-860f-3c0a0504a40c',
        '2025-06-27', 1255.62,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1029',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('5b34287d-62db-438b-91d8-618a0a140b13', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', 'INV-1030',
        '2025-04-27', '2025-05-27',
        3937.58, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0f979fc1-490d-4a83-9409-f201d9e7f565', '5b34287d-62db-438b-91d8-618a0a140b13', 'ترميم تجميلي', 1968.79);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('1e519787-eb0e-4b48-8e7b-0744623f2abf', '5b34287d-62db-438b-91d8-618a0a140b13', 'تاج الأسنان', 1968.79);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('4f9195f5-0c17-4ce6-bdb9-bd3f133cdb8e', '5b34287d-62db-438b-91d8-618a0a140b13', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56',
        '2025-05-09', 1922.54,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1030',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('b7b2cf12-1600-4bf8-bed7-e71ab7f65992', 'bb002dda-1f45-4af2-b66d-13d745614679', 'INV-1031',
        '2025-02-12', '2025-03-14',
        4508.17, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0b65bf21-edc0-4d38-af6c-eb817d1dbf04', 'b7b2cf12-1600-4bf8-bed7-e71ab7f65992', 'زراعة الأسنان', 901.63);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c2a631a6-c9f2-4e20-9a07-95471267d2f3', 'b7b2cf12-1600-4bf8-bed7-e71ab7f65992', 'ترميم تجميلي', 901.63);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2996412a-1aad-42ec-bd9d-31b32243dc1f', 'b7b2cf12-1600-4bf8-bed7-e71ab7f65992', 'طقم جزئي', 901.63);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e9ad19ad-864d-40fb-8d88-7d9a65f1f907', 'b7b2cf12-1600-4bf8-bed7-e71ab7f65992', 'تنظيف الأسنان', 901.63);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6b4d7541-a75a-47df-b55c-f2e69af3b4a2', 'b7b2cf12-1600-4bf8-bed7-e71ab7f65992', 'ترميم تجميلي', 901.63);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('d28734c2-85e2-4eba-b31a-4d5c9f16f93d', 'b7b2cf12-1600-4bf8-bed7-e71ab7f65992', 'bb002dda-1f45-4af2-b66d-13d745614679',
        '2025-02-13', 2268.55,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1031',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('20c7aa5f-639e-4259-b468-7f831810d3db', '66d56a9b-17c4-4183-aa15-2db3082c3874', 'INV-1032',
        '2025-02-13', '2025-03-15',
        2733.00, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d7b33989-0aa4-4d33-91d0-cf11a6ecad66', '20c7aa5f-639e-4259-b468-7f831810d3db', 'X_RAY', 1366.50);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('47827818-2fb6-49c2-bcba-66ecedc50cf1', '20c7aa5f-639e-4259-b468-7f831810d3db', 'طقم جزئي', 1366.50);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('45a7f70d-783e-49a3-ab85-d0418da9b722', '20c7aa5f-639e-4259-b468-7f831810d3db', '66d56a9b-17c4-4183-aa15-2db3082c3874',
        '2025-03-04', 2733.00,
        'شيك', 'PAYMENT', 'دفعة فاتورة INV-1032',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('2661ed19-357a-4d9c-82b9-6c7edae2c6f8', '1d6f6927-2e1b-4928-ad7d-4416b021719e', 'INV-1033',
        '2025-03-07', '2025-04-06',
        3814.71, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d5af395a-ea4b-4d2f-bd5e-5d926fe2e08b', '2661ed19-357a-4d9c-82b9-6c7edae2c6f8', 'علاج العصب', 1271.57);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('84c56472-a0b6-4590-9e81-e7b18c86a1f9', '2661ed19-357a-4d9c-82b9-6c7edae2c6f8', 'تقويم', 1271.57);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d8fe5e11-4cdc-458b-9a59-71eabca6a0ca', '2661ed19-357a-4d9c-82b9-6c7edae2c6f8', 'X_RAY', 1271.57);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('79556ccf-339e-466a-8154-1532514255ed', '1d7de144-435b-4d30-aab6-6fb43d4ac723', 'INV-1034',
        '2025-06-02', '2025-07-02',
        4554.19, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e5b69fce-fda1-4594-8f19-e138e433fa9c', '79556ccf-339e-466a-8154-1532514255ed', 'طقم جزئي', 910.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c7009cf4-9950-467c-a7be-bcccea816cd7', '79556ccf-339e-466a-8154-1532514255ed', 'طقم جزئي', 910.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('31964491-e842-4609-af1e-593ee38a7b66', '79556ccf-339e-466a-8154-1532514255ed', 'X_RAY', 910.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c3ff0330-a3d1-4921-999f-451e3710a692', '79556ccf-339e-466a-8154-1532514255ed', 'تنظيف الأسنان', 910.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2d05ed86-baa4-4ced-98c4-3190ab13fe62', '79556ccf-339e-466a-8154-1532514255ed', 'إزالة الجير', 910.84);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('a8718d78-f6d6-419e-8618-1f68d94dc036', 'c788722c-3a96-4bd5-9139-33a8d8e65565', 'INV-1035',
        '2025-01-18', '2025-02-17',
        3469.83, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d914f6fe-b7ef-4ab8-92b9-2bf399298a70', 'a8718d78-f6d6-419e-8618-1f68d94dc036', 'حشو الأسنان', 1156.61);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('fc3c0c8f-04df-4a3e-83e7-5c92834c938a', 'a8718d78-f6d6-419e-8618-1f68d94dc036', 'علاج العصب', 1156.61);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('9d402a5f-00ae-4623-8085-0163fc0eb6da', 'a8718d78-f6d6-419e-8618-1f68d94dc036', 'ترميم تجميلي', 1156.61);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('13aed163-1652-4024-a9d5-52f89ec3aa60', '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', 'INV-1036',
        '2025-04-06', '2025-05-06',
        374.94, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('67ba1919-2140-41c4-a408-1e4ac190cbbb', '13aed163-1652-4024-a9d5-52f89ec3aa60', 'حشو وقائي', 187.47);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0e89d0cd-56aa-47dc-ad3d-e4a172ff98ca', '13aed163-1652-4024-a9d5-52f89ec3aa60', 'حشو وقائي', 187.47);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('333b8a78-694f-4636-825c-b76a97368846', '4cf6838e-3cfd-4469-b010-38442aca0857', 'INV-1037',
        '2025-02-03', '2025-03-05',
        4907.11, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('7d4543fe-8709-4f9c-aba7-8da64ed00ea8', '333b8a78-694f-4636-825c-b76a97368846', 'تبييض الأسنان', 981.42);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('886f596f-f323-46ef-95d0-37502541e648', '333b8a78-694f-4636-825c-b76a97368846', 'جسر الأسنان', 981.42);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('39da1d8f-5838-4356-878f-d684efe07e1d', '333b8a78-694f-4636-825c-b76a97368846', 'X_RAY', 981.42);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e2fb6e8b-8769-42e4-a8b1-f3f84a0a02b6', '333b8a78-694f-4636-825c-b76a97368846', 'حشو وقائي', 981.42);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('8ad59350-e052-4b87-b16d-741a43253690', '333b8a78-694f-4636-825c-b76a97368846', 'حشو الأسنان', 981.42);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('1eab5e48-c138-4e58-b276-bcc1cd9fe125', '333b8a78-694f-4636-825c-b76a97368846', '4cf6838e-3cfd-4469-b010-38442aca0857',
        '2025-02-23', 2154.39,
        'تحويل بنكي', 'PAYMENT', 'دفعة فاتورة INV-1037',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('391461be-8b91-4f7d-89a4-6476053f3dbd', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', 'INV-1038',
        '2025-07-11', '2025-08-10',
        4604.24, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('aec79a65-6f71-4be8-8c9f-68f894cef6ad', '391461be-8b91-4f7d-89a4-6476053f3dbd', 'جسر الأسنان', 4604.24);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('9e1794eb-8d15-4ea5-8a19-3312c2f110ea', '28bb748d-3170-4652-87e7-871566ae3e74', 'INV-1039',
        '2025-03-13', '2025-04-12',
        3519.37, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('ae1e0987-0a44-41a7-a1b2-b3ff3a63c75c', '9e1794eb-8d15-4ea5-8a19-3312c2f110ea', 'جسر الأسنان', 879.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a6e0da28-55be-4f3c-898f-a3f8bba7cba5', '9e1794eb-8d15-4ea5-8a19-3312c2f110ea', 'تنظيف الأسنان', 879.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5b50f091-44e4-4539-8319-5a57c9e4256f', '9e1794eb-8d15-4ea5-8a19-3312c2f110ea', 'حشو الأسنان', 879.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0237fde4-26cd-492a-be31-7cdf29743a2e', '9e1794eb-8d15-4ea5-8a19-3312c2f110ea', 'خلع الأسنان', 879.84);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('3b90a210-e750-414d-96f8-a37ca3772aa8', '9018a956-250f-40ca-be2c-bc6a37ec8b19', 'INV-1040',
        '2025-02-06', '2025-03-08',
        2323.18, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('313f7b32-c1ca-4cc4-a4b0-8ad05d0bdade', '3b90a210-e750-414d-96f8-a37ca3772aa8', 'مثبت تقويم', 1161.59);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('02bbcb1f-c227-428c-ab55-22f8eed1764a', '3b90a210-e750-414d-96f8-a37ca3772aa8', 'تقويم', 1161.59);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('7bf05d45-0d87-4c74-85cf-d415527349ad', '3b90a210-e750-414d-96f8-a37ca3772aa8', '9018a956-250f-40ca-be2c-bc6a37ec8b19',
        '2025-02-22', 777.81,
        'شيك', 'PAYMENT', 'دفعة فاتورة INV-1040',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('833a1fca-38e9-461c-a13b-d0a8d590d26c', '688b05f6-aa01-4e29-8e46-92af09f92a12', 'INV-1041',
        '2025-04-23', '2025-05-23',
        3263.05, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('26c0c01e-d5b2-413d-99d0-f808849228aa', '833a1fca-38e9-461c-a13b-d0a8d590d26c', 'استشارة', 815.76);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('087e0674-6e38-4d25-986f-99f7598a62d7', '833a1fca-38e9-461c-a13b-d0a8d590d26c', 'ترميم تجميلي', 815.76);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('0ab4ed08-83b3-4192-b367-af99b6d9e710', '833a1fca-38e9-461c-a13b-d0a8d590d26c', 'طقم جزئي', 815.76);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6e04bd5b-f293-472e-b93b-31a4c3d1a6ce', '833a1fca-38e9-461c-a13b-d0a8d590d26c', 'طقم جزئي', 815.76);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('6866e5d9-30f6-4884-b35b-72fea88a4792', '833a1fca-38e9-461c-a13b-d0a8d590d26c', '688b05f6-aa01-4e29-8e46-92af09f92a12',
        '2025-06-02', 3263.05,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1041',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('83e0e8f3-73f5-4720-afef-567dd53dedc5', '3e8dcbc5-36b1-435b-bfef-356d7d615c73', 'INV-1042',
        '2025-01-19', '2025-02-18',
        2339.36, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('890c668c-7604-42e1-b3c4-340611915284', '83e0e8f3-73f5-4720-afef-567dd53dedc5', 'تاج الأسنان', 467.87);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5a045d98-e86a-4e6e-93da-40666207ff0f', '83e0e8f3-73f5-4720-afef-567dd53dedc5', 'ترميم تجميلي', 467.87);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('93fb1510-5098-4fcb-a38e-700298e3324c', '83e0e8f3-73f5-4720-afef-567dd53dedc5', 'علاج العصب', 467.87);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d7d29296-e85b-48b0-a67f-b55a46128cc7', '83e0e8f3-73f5-4720-afef-567dd53dedc5', 'إزالة الجير', 467.87);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('85dc5c1d-7db0-4197-b453-8844e226aa94', '83e0e8f3-73f5-4720-afef-567dd53dedc5', 'استشارة', 467.87);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('d5b36d9b-bc54-4925-904e-d6de0cb64677', 'b9b53000-347b-499c-9259-a16709d58358', 'INV-1043',
        '2025-03-24', '2025-04-23',
        2316.69, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('09d76bd4-a109-4726-b1ec-71f6358ce101', 'd5b36d9b-bc54-4925-904e-d6de0cb64677', 'حشو الأسنان', 2316.69);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('829ee705-7bbf-4293-96d2-451198811dd5', 'd5b36d9b-bc54-4925-904e-d6de0cb64677', 'b9b53000-347b-499c-9259-a16709d58358',
        '2025-03-30', 2316.69,
        'شيك', 'PAYMENT', 'دفعة فاتورة INV-1043',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('5ebdb62f-ff43-4d43-9ef8-c47778bcd1f9', '68cedbfc-9ea7-4941-90d8-8927bcac14a8', 'INV-1044',
        '2025-06-05', '2025-07-05',
        4916.11, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('860e5482-c444-46d8-91d4-c7405ec40eab', '5ebdb62f-ff43-4d43-9ef8-c47778bcd1f9', 'زراعة الأسنان', 1638.70);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2418cb30-d365-4eb5-9bbe-1f51d04c7ecc', '5ebdb62f-ff43-4d43-9ef8-c47778bcd1f9', 'ترميم تجميلي', 1638.70);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('53fd9063-18f3-4fa0-9f39-0b8b7ec48347', '5ebdb62f-ff43-4d43-9ef8-c47778bcd1f9', 'مثبت تقويم', 1638.70);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('f64fbe75-4bca-415f-90c5-fa0063dd9d07', '89ef4a50-600e-4ad1-8396-9eb6fa992e60', 'INV-1045',
        '2025-03-24', '2025-04-23',
        3401.92, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('09fc6af0-b3bc-4478-a277-add14ac32559', 'f64fbe75-4bca-415f-90c5-fa0063dd9d07', 'خلع الأسنان', 3401.92);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('ac908210-0814-43eb-a3c3-04a9811dfe0c', 'f64fbe75-4bca-415f-90c5-fa0063dd9d07', '89ef4a50-600e-4ad1-8396-9eb6fa992e60',
        '2025-04-12', 3401.92,
        'تحويل بنكي', 'PAYMENT', 'دفعة فاتورة INV-1045',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('6e5fd359-b661-47bb-8acd-0a408c04b97b', 'fc2bd317-69b1-405e-9094-f85f96a7d53b', 'INV-1046',
        '2025-07-01', '2025-07-31',
        2880.62, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a9513959-b203-4b28-8f27-847a0a396c59', '6e5fd359-b661-47bb-8acd-0a408c04b97b', 'حشو وقائي', 1440.31);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('f37e1a9a-c824-45d4-821e-df82248a00f2', '6e5fd359-b661-47bb-8acd-0a408c04b97b', 'زراعة الأسنان', 1440.31);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('b856a425-d2df-439b-a147-b08a94776fb3', '6e5fd359-b661-47bb-8acd-0a408c04b97b', 'fc2bd317-69b1-405e-9094-f85f96a7d53b',
        '2025-07-25', 2880.62,
        'تحويل بنكي', 'PAYMENT', 'دفعة فاتورة INV-1046',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('47716b7f-7160-488c-a3ff-bbc13f943316', '3057ea2c-1fdc-4d82-8e19-282a3566172b', 'INV-1047',
        '2025-04-21', '2025-05-21',
        3472.86, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e9d3a8b0-d58c-44b0-b1a6-43d48317c1e5', '47716b7f-7160-488c-a3ff-bbc13f943316', 'فلورايد', 1157.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('392db5a5-19bc-48f3-ba82-82d5caa25fa4', '47716b7f-7160-488c-a3ff-bbc13f943316', 'تاج الأسنان', 1157.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b04125f1-1a89-4fe6-ba97-5e176a8be80e', '47716b7f-7160-488c-a3ff-bbc13f943316', 'فلورايد', 1157.62);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('4bb52859-bec8-4107-80fc-42d9e488210c', '009f45fe-3809-464d-96ce-3956e150e456', 'INV-1048',
        '2025-05-28', '2025-06-27',
        349.10, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5005f80f-8194-4e10-9b4c-b0443eb4c843', '4bb52859-bec8-4107-80fc-42d9e488210c', 'علاج العصب', 174.55);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('edd91f00-2e20-466f-9900-82610200776c', '4bb52859-bec8-4107-80fc-42d9e488210c', 'حشو الأسنان', 174.55);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('793e2d64-9862-4d11-bab6-7345cf507d13', 'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797', 'INV-1049',
        '2025-02-08', '2025-03-10',
        1394.96, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c2d01a60-90a5-4ee7-9cd5-9e676109bc1e', '793e2d64-9862-4d11-bab6-7345cf507d13', 'علاج اللثة', 464.99);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('1be7c9b0-4473-424d-9603-7df193834158', '793e2d64-9862-4d11-bab6-7345cf507d13', 'علاج العصب', 464.99);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('f1285c91-bc3e-4cc7-aba4-d09fc4e616ce', '793e2d64-9862-4d11-bab6-7345cf507d13', 'تبييض الأسنان', 464.99);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('17d05a3c-231c-45bd-a111-e03b1d72b47c', '793e2d64-9862-4d11-bab6-7345cf507d13', 'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797',
        '2025-03-24', 463.44,
        'شيك', 'PAYMENT', 'دفعة فاتورة INV-1049',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('5298bd25-dfed-4e87-8251-bac3545f3981', 'f9e60397-26d4-4c86-98a7-d906d9359501', 'INV-1050',
        '2025-02-08', '2025-03-10',
        961.21, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c9fa311e-4abe-46d5-bc65-ae2ba2464d92', '5298bd25-dfed-4e87-8251-bac3545f3981', 'قشرة تجميلية', 961.21);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('27b07808-5627-4c87-82f2-f720fc413008', '5298bd25-dfed-4e87-8251-bac3545f3981', 'f9e60397-26d4-4c86-98a7-d906d9359501',
        '2025-03-13', 961.21,
        'شيك', 'PAYMENT', 'دفعة فاتورة INV-1050',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('b7ee05cd-286c-4627-abd5-9fea16ec8931', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'INV-1051',
        '2025-02-01', '2025-03-03',
        564.13, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('4b61c6c5-d592-4afa-a353-1f21d60f11a1', 'b7ee05cd-286c-4627-abd5-9fea16ec8931', 'تبييض الأسنان', 141.03);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('84955f25-0936-4cb1-a528-ed6749c260f2', 'b7ee05cd-286c-4627-abd5-9fea16ec8931', 'علاج اللثة', 141.03);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('3e959013-b05e-4480-a135-089d362589c6', 'b7ee05cd-286c-4627-abd5-9fea16ec8931', 'مثبت تقويم', 141.03);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('758eaf63-d455-415e-b884-a8390d0c5676', 'b7ee05cd-286c-4627-abd5-9fea16ec8931', 'تقويم', 141.03);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('d796b7ab-135c-433d-9695-e279f72466ca', '7c9db862-5ccd-4652-b053-cedc74f5dac5', 'INV-1052',
        '2025-05-30', '2025-06-29',
        4572.87, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('fb663741-a0f0-4665-83b7-b4ac9731d533', 'd796b7ab-135c-433d-9695-e279f72466ca', 'حشو الأسنان', 1143.22);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('598d8ead-7025-4124-acf4-c039db51565c', 'd796b7ab-135c-433d-9695-e279f72466ca', 'قشرة تجميلية', 1143.22);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a2016bcc-7104-4beb-a47c-19ec498bc678', 'd796b7ab-135c-433d-9695-e279f72466ca', 'ترميم تجميلي', 1143.22);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c1c42346-74e3-4887-ad7a-a537819d94ee', 'd796b7ab-135c-433d-9695-e279f72466ca', 'حشو الأسنان', 1143.22);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('4ee1e73f-a71f-44eb-a6cc-04a784fffcda', 'd796b7ab-135c-433d-9695-e279f72466ca', '7c9db862-5ccd-4652-b053-cedc74f5dac5',
        '2025-07-01', 2439.72,
        'نقدي', 'PAYMENT', 'دفعة فاتورة INV-1052',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('22b03fe3-6384-4bbd-b93e-f433ea349ba3', '959023bc-fabf-46ba-8e10-5a218859f611', 'INV-1053',
        '2025-04-09', '2025-05-09',
        4189.75, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('8399118e-1275-4082-a61e-1a096663daf5', '22b03fe3-6384-4bbd-b93e-f433ea349ba3', 'جسر الأسنان', 1396.58);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('65919ce9-63d7-447f-9a44-b6a699348e89', '22b03fe3-6384-4bbd-b93e-f433ea349ba3', 'ترميم تجميلي', 1396.58);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a2c9ddcf-cbc6-4ee2-8ab4-ec1c426f3b49', '22b03fe3-6384-4bbd-b93e-f433ea349ba3', 'فلورايد', 1396.58);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('8458c980-8eff-452e-97b2-9d1df924ac77', '22b03fe3-6384-4bbd-b93e-f433ea349ba3', '959023bc-fabf-46ba-8e10-5a218859f611',
        '2025-05-04', 1784.03,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1053',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('cd88fb7a-a793-49a5-9ebb-78bc41f6633c', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'INV-1054',
        '2025-02-27', '2025-03-29',
        653.60, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5e94282a-7f97-4f2a-92ec-f33f8850e4d2', 'cd88fb7a-a793-49a5-9ebb-78bc41f6633c', 'فلورايد', 326.80);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('deda8525-0fdd-47bd-9fa0-24b9eeee296b', 'cd88fb7a-a793-49a5-9ebb-78bc41f6633c', 'قشرة تجميلية', 326.80);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('62444536-4eee-4c62-9cbe-9dba962d8bf5', '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'INV-1055',
        '2025-03-13', '2025-04-12',
        4375.60, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('80eb8eec-2196-4550-9d4f-b0d1bfe0464a', '62444536-4eee-4c62-9cbe-9dba962d8bf5', 'حشو الأسنان', 2187.80);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('88c0e9ad-4976-404d-8f81-945a3b789569', '62444536-4eee-4c62-9cbe-9dba962d8bf5', 'طقم جزئي', 2187.80);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('040b014e-9a93-4b47-a312-333e73f1f905', 'e9591707-0295-4c79-ae31-86fce4108d2f', 'INV-1056',
        '2025-04-11', '2025-05-11',
        4637.34, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('dbb75fce-00b4-4ee4-bac1-85c7a2e35fd3', '040b014e-9a93-4b47-a312-333e73f1f905', 'مثبت تقويم', 927.47);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6845e2ee-3155-494b-9b5b-740047ee1dc9', '040b014e-9a93-4b47-a312-333e73f1f905', 'تاج الأسنان', 927.47);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('991a6616-3f75-411f-82e3-1d565ef6d61b', '040b014e-9a93-4b47-a312-333e73f1f905', 'جسر الأسنان', 927.47);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('3fa92f40-9c0a-4554-b303-1d70804e3960', '040b014e-9a93-4b47-a312-333e73f1f905', 'طقم جزئي', 927.47);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2f53ef31-b6fa-492b-bdc1-37c7c1cd87da', '040b014e-9a93-4b47-a312-333e73f1f905', 'إزالة الجير', 927.47);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('ef9e8a12-3d84-45a3-ac72-116a9c5e115d', '3bac8559-06ab-4672-8336-9ad2a0cadce0', 'INV-1057',
        '2025-03-03', '2025-04-02',
        2504.55, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b7784898-3e13-4ddf-bb24-e17af013c39f', 'ef9e8a12-3d84-45a3-ac72-116a9c5e115d', 'ترميم تجميلي', 2504.55);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('a1309805-adff-480a-afd0-64c3637f0749', '517d1071-dde1-4d36-b1ef-5745264bf869', 'INV-1058',
        '2025-03-25', '2025-04-24',
        2311.86, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d80dc437-8cda-4cca-9ec1-25204735729d', 'a1309805-adff-480a-afd0-64c3637f0749', 'خلع الأسنان', 1155.93);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('1e856cbb-5690-4f25-86e4-78a893b4f8ac', 'a1309805-adff-480a-afd0-64c3637f0749', 'استشارة', 1155.93);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('cc258293-8ffa-4168-86d5-1aed9bb0382e', '5747f021-7809-4e2d-856d-45ebdced300c', 'INV-1059',
        '2025-07-08', '2025-08-07',
        795.23, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('53ba5113-f600-4095-9db4-e2a2284e738d', 'cc258293-8ffa-4168-86d5-1aed9bb0382e', 'تنظيف الأسنان', 198.81);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('36ffc7e9-f69a-4fe2-aba3-d2bc907fb1e3', 'cc258293-8ffa-4168-86d5-1aed9bb0382e', 'طقم أسنان', 198.81);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a7f78c11-6347-4da2-9f3d-03f3377c0f96', 'cc258293-8ffa-4168-86d5-1aed9bb0382e', 'خلع الأسنان', 198.81);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c660e4e7-0c8e-4dc2-87e5-b0a861bbece1', 'cc258293-8ffa-4168-86d5-1aed9bb0382e', 'X_RAY', 198.81);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('913369b7-4cd6-4fcd-9d54-fd9f99f90ca6', 'cc258293-8ffa-4168-86d5-1aed9bb0382e', '5747f021-7809-4e2d-856d-45ebdced300c',
        '2025-08-13', 467.26,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1059',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('3a526473-9eb9-4f41-a8ff-ad7e97092db7', '14d28cf4-88b5-41f5-98b2-926cb0f94ae9', 'INV-1060',
        '2025-01-17', '2025-02-16',
        2961.13, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c030d7f1-aaeb-47f7-a2d4-4d5fd893e264', '3a526473-9eb9-4f41-a8ff-ad7e97092db7', 'تبييض الأسنان', 592.23);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5d72564e-2644-47a6-8797-745a17582e5d', '3a526473-9eb9-4f41-a8ff-ad7e97092db7', 'خلع الأسنان', 592.23);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('21140a86-a19f-406b-95aa-84ee6d6f9d59', '3a526473-9eb9-4f41-a8ff-ad7e97092db7', 'زراعة الأسنان', 592.23);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('dcfefbcb-a025-4907-9331-0edd479417a7', '3a526473-9eb9-4f41-a8ff-ad7e97092db7', 'زراعة الأسنان', 592.23);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('dc5fb287-d058-4b82-ab37-3ca0f5211f6e', '3a526473-9eb9-4f41-a8ff-ad7e97092db7', 'قشرة تجميلية', 592.23);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('2a6fb65f-faa0-4219-beb1-70c389bc1ff6', 'bd71e492-b94d-44b1-8c0b-1012177e68d4', 'INV-1061',
        '2025-02-09', '2025-03-11',
        2617.75, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5e129a54-f752-4034-a00a-7ecb84db742b', '2a6fb65f-faa0-4219-beb1-70c389bc1ff6', 'حشو الأسنان', 2617.75);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('7641f9e5-b7c9-45b1-9eec-b1edab85d1fc', 'cffe62fb-dd1c-444d-b911-422ba6f25a81', 'INV-1062',
        '2025-05-30', '2025-06-29',
        4382.08, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('5bb0837b-d964-4a5e-aed1-bcd8ee5d73af', '7641f9e5-b7c9-45b1-9eec-b1edab85d1fc', 'مثبت تقويم', 1095.52);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('fa6ebb54-05c1-4fbf-9d1c-c64e4cefa629', '7641f9e5-b7c9-45b1-9eec-b1edab85d1fc', 'تقويم', 1095.52);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('037437d8-cdd9-48ae-8ab0-bd6373ff9c54', '7641f9e5-b7c9-45b1-9eec-b1edab85d1fc', 'قشرة تجميلية', 1095.52);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('950d07be-156d-4551-b9df-c16931541563', '7641f9e5-b7c9-45b1-9eec-b1edab85d1fc', 'زراعة الأسنان', 1095.52);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('1efd6b55-c0b5-4096-87bb-72fe7ff6bcd6', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4', 'INV-1063',
        '2025-01-27', '2025-02-26',
        4570.68, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c1b4c4f4-d1f1-4154-af1b-8476621a8ad2', '1efd6b55-c0b5-4096-87bb-72fe7ff6bcd6', 'خلع الأسنان', 1523.56);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6b020ebb-c471-447e-877f-214b716e1678', '1efd6b55-c0b5-4096-87bb-72fe7ff6bcd6', 'تنظيف الأسنان', 1523.56);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('f29487ea-54f9-4644-b215-657bcbfedf50', '1efd6b55-c0b5-4096-87bb-72fe7ff6bcd6', 'حشو وقائي', 1523.56);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('9b8254c8-0abc-46ac-a303-c37162bd310f', '1efd6b55-c0b5-4096-87bb-72fe7ff6bcd6', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4',
        '2025-03-02', 4570.68,
        'نقدي', 'PAYMENT', 'دفعة فاتورة INV-1063',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('b79e2846-01c1-4597-9844-2fab995da599', '3d209091-82a5-484c-91f7-6dbaf91154b1', 'INV-1064',
        '2025-03-06', '2025-04-05',
        777.09, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('4050cf0c-c3cc-475c-8258-00da203d9d63', 'b79e2846-01c1-4597-9844-2fab995da599', 'زراعة الأسنان', 259.03);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('56a01894-7de7-4866-a1dc-4d2a23f769be', 'b79e2846-01c1-4597-9844-2fab995da599', 'تنظيف الأسنان', 259.03);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e01fb1e9-a7d7-4fd1-99b6-081bc0790197', 'b79e2846-01c1-4597-9844-2fab995da599', 'خلع الأسنان', 259.03);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('546f20a5-c07c-42de-8fd2-6cef6c8b3363', 'b79e2846-01c1-4597-9844-2fab995da599', '3d209091-82a5-484c-91f7-6dbaf91154b1',
        '2025-04-11', 777.09,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1064',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('a06ba880-3eb1-4c14-bea2-91f1f4f475f9', '060e5ecd-073f-4930-9a27-e7bfcf8245b0', 'INV-1065',
        '2025-06-20', '2025-07-20',
        885.50, 'CANCELLED', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('079eced2-fcad-440a-924a-ce4e8361d921', 'a06ba880-3eb1-4c14-bea2-91f1f4f475f9', 'حشو الأسنان', 442.75);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c4909637-ce49-44dc-8859-216b10596943', 'a06ba880-3eb1-4c14-bea2-91f1f4f475f9', 'طقم جزئي', 442.75);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('0d1e1834-0a14-4358-a67b-fcda8c9c3ff2', '51739c46-139f-499c-bec0-720560313310', 'INV-1066',
        '2025-03-21', '2025-04-20',
        2038.63, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('550723b8-466d-4892-96bc-0b89902877db', '0d1e1834-0a14-4358-a67b-fcda8c9c3ff2', 'خلع الأسنان', 407.73);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b3086b80-e584-41ca-bc09-828b0c668a69', '0d1e1834-0a14-4358-a67b-fcda8c9c3ff2', 'خلع الأسنان', 407.73);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('ff4fa001-9b53-49b5-9243-dd1a6f7e4f2b', '0d1e1834-0a14-4358-a67b-fcda8c9c3ff2', 'فلورايد', 407.73);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b76f2164-448e-48b3-a75b-0953a0f683a8', '0d1e1834-0a14-4358-a67b-fcda8c9c3ff2', 'تنظيف الأسنان', 407.73);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('9ec78774-7de5-422f-9372-002b63b566db', '0d1e1834-0a14-4358-a67b-fcda8c9c3ff2', 'حشو الأسنان', 407.73);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('bb91bf01-5150-4454-a7b1-32b60f599510', '0d1e1834-0a14-4358-a67b-fcda8c9c3ff2', '51739c46-139f-499c-bec0-720560313310',
        '2025-04-04', 878.12,
        'تحويل بنكي', 'PAYMENT', 'دفعة فاتورة INV-1066',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('9d8364b0-00ac-4eec-b518-8844ccc7e67b', 'b25049f9-d533-495b-ba24-753998010265', 'INV-1067',
        '2025-06-12', '2025-07-12',
        1214.20, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d7416b93-6726-400b-bb85-1cae53e9b5db', '9d8364b0-00ac-4eec-b518-8844ccc7e67b', 'علاج العصب', 242.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('09907171-4c4b-4708-bb59-14b24b8dc324', '9d8364b0-00ac-4eec-b518-8844ccc7e67b', 'تبييض الأسنان', 242.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6786220a-79c2-4c4b-84ef-4311989a3d0f', '9d8364b0-00ac-4eec-b518-8844ccc7e67b', 'جسر الأسنان', 242.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('7576bb36-1a98-49ec-84d7-92b8e9c13012', '9d8364b0-00ac-4eec-b518-8844ccc7e67b', 'حشو وقائي', 242.84);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('fadfee4a-c536-47e3-9220-7331f31cb102', '9d8364b0-00ac-4eec-b518-8844ccc7e67b', 'إزالة الجير', 242.84);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('dfbb5663-1d00-47c3-8553-a93ce38b57a4', '9d8364b0-00ac-4eec-b518-8844ccc7e67b', 'b25049f9-d533-495b-ba24-753998010265',
        '2025-06-27', 1214.20,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1067',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('ae3d4ac4-5605-4405-9880-d5d360d1a257', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', 'INV-1068',
        '2025-04-17', '2025-05-17',
        2893.44, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('9d134f79-94db-48da-a4ee-3e9cafae57b7', 'ae3d4ac4-5605-4405-9880-d5d360d1a257', 'استشارة', 723.36);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('8cd864e3-aad3-48e7-93f8-9b4ea8c11962', 'ae3d4ac4-5605-4405-9880-d5d360d1a257', 'علاج اللثة', 723.36);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('b3595ccc-e735-4d74-bc7c-2332bac69069', 'ae3d4ac4-5605-4405-9880-d5d360d1a257', 'إزالة الجير', 723.36);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('3e95937f-0505-4e60-ac07-99d3c53f7cc0', 'ae3d4ac4-5605-4405-9880-d5d360d1a257', 'طقم جزئي', 723.36);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('9ccdeb3a-99f9-4978-9662-3c59d5a4a2ef', 'ae3d4ac4-5605-4405-9880-d5d360d1a257', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa',
        '2025-05-28', 1632.16,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1068',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('4f8fd25a-4169-43d4-9619-c381de333abf', '7aa917b9-c53b-4e77-935a-6297112588e6', 'INV-1069',
        '2025-04-26', '2025-05-26',
        2369.82, 'PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('fbc47d1a-8334-4955-a3e5-642e633c14dd', '4f8fd25a-4169-43d4-9619-c381de333abf', 'علاج اللثة', 789.94);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('a34a72aa-7656-4f24-9387-06511e804e02', '4f8fd25a-4169-43d4-9619-c381de333abf', 'إزالة الجير', 789.94);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('ec21a076-7723-4501-bdaf-421ab9ffc9c5', '4f8fd25a-4169-43d4-9619-c381de333abf', 'حشو وقائي', 789.94);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('6ed80bf7-b9ec-49f2-9675-455ab18c7351', '4f8fd25a-4169-43d4-9619-c381de333abf', '7aa917b9-c53b-4e77-935a-6297112588e6',
        '2025-05-14', 2369.82,
        'تحويل بنكي', 'PAYMENT', 'دفعة فاتورة INV-1069',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('9fddf87a-0dfa-4709-af1e-5c8f38928279', 'c85699c1-ffc2-483b-8999-cacc91b76fa1', 'INV-1070',
        '2025-02-17', '2025-03-19',
        3270.65, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e64ee25b-439e-4926-ad74-9c08b004832a', '9fddf87a-0dfa-4709-af1e-5c8f38928279', 'ترميم تجميلي', 817.66);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('9bd96ab9-aa7f-43fe-9929-97c1d5ccb48c', '9fddf87a-0dfa-4709-af1e-5c8f38928279', 'X_RAY', 817.66);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d674ffb2-ea41-4c4f-8508-0a3515637dc2', '9fddf87a-0dfa-4709-af1e-5c8f38928279', 'خلع الأسنان', 817.66);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('69e6c0eb-bcf1-48c1-9ab2-f8f02205b8c0', '9fddf87a-0dfa-4709-af1e-5c8f38928279', 'زراعة الأسنان', 817.66);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('28fc34e4-8438-4a9b-8a5d-4e2fd7b0294a', '951394c6-4e21-4d51-ba77-80c783a574e2', 'INV-1071',
        '2025-04-29', '2025-05-29',
        2553.36, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('f010ff26-6dee-4a3a-a529-8ae12d9d5e19', '28fc34e4-8438-4a9b-8a5d-4e2fd7b0294a', 'تقويم', 2553.36);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('9b943394-6c53-4566-8a1e-bd39eefe2120', 'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e', 'INV-1072',
        '2025-02-03', '2025-03-05',
        417.17, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('3086fee4-6bb7-4446-9444-25a968e4e8d7', '9b943394-6c53-4566-8a1e-bd39eefe2120', 'علاج اللثة', 83.43);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('c1dea5c0-a4a9-43f6-a463-cc361e121da7', '9b943394-6c53-4566-8a1e-bd39eefe2120', 'ترميم تجميلي', 83.43);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6df1496d-2bc8-4731-8159-008fea03ee03', '9b943394-6c53-4566-8a1e-bd39eefe2120', 'علاج اللثة', 83.43);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('33538d95-6b83-4f64-9936-623a443f56c5', '9b943394-6c53-4566-8a1e-bd39eefe2120', 'حشو وقائي', 83.43);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('6e6db22d-4c12-4103-8ee7-48475bd36b6c', '9b943394-6c53-4566-8a1e-bd39eefe2120', 'X_RAY', 83.43);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('40779b1f-024e-4b6f-b57b-d68ca01f7b64', '7ecc8943-b925-4944-8e60-8c0570ad9404', 'INV-1073',
        '2025-02-25', '2025-03-27',
        1170.49, 'UNPAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('2dd31b84-9f74-4f12-a2e4-f3572f7dc746', '40779b1f-024e-4b6f-b57b-d68ca01f7b64', 'حشو الأسنان', 292.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('3f2f48d8-8855-4ff2-8f1f-abe4f3dedcd8', '40779b1f-024e-4b6f-b57b-d68ca01f7b64', 'تقويم', 292.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('d1e10ad1-5647-4886-812f-17a63488de43', '40779b1f-024e-4b6f-b57b-d68ca01f7b64', 'علاج اللثة', 292.62);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('46ba842e-d563-4af9-9da8-c692977fb895', '40779b1f-024e-4b6f-b57b-d68ca01f7b64', 'قشرة تجميلية', 292.62);
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date,
                      total_amount, status, created_by)
VALUES ('5ad6c92e-060b-4903-ac39-aaabd9ab3f6e', '875b2d85-4697-4477-9297-f19ffda87d21', 'INV-1074',
        '2025-05-15', '2025-06-14',
        2718.87, 'PARTIALLY_PAID', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('e5a2b662-378b-49f0-a618-4be48ecd15ea', '5ad6c92e-060b-4903-ac39-aaabd9ab3f6e', 'زراعة الأسنان', 906.29);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('32893ee9-51a4-4dff-a016-d2d330ddeb42', '5ad6c92e-060b-4903-ac39-aaabd9ab3f6e', 'تاج الأسنان', 906.29);
INSERT INTO invoice_items (id, invoice_id, description, amount)
VALUES ('dab4f7fb-76c7-45b0-983e-89da85fbc2d7', '5ad6c92e-060b-4903-ac39-aaabd9ab3f6e', 'تبييض الأسنان', 906.29);
INSERT INTO payments (id, invoice_id, patient_id, payment_date, amount,
                      payment_method, type, description, created_by)
VALUES ('8f7b0480-aef2-40f1-981e-549264b87d93', '5ad6c92e-060b-4903-ac39-aaabd9ab3f6e', '875b2d85-4697-4477-9297-f19ffda87d21',
        '2025-06-06', 1843.00,
        'بطاقة ائتمان', 'PAYMENT', 'دفعة فاتورة INV-1074',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4');
-- Additional Lab Requests
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('3c994fdc-8475-440f-885f-1fc6b29a1c6d', 'e9591707-0295-4c79-ae31-86fce4108d2f', 'LAB-2024006', 'تاج زيركون',
        12,
        '2025-05-16', '2025-05-29',
        'COMPLETED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('5815e139-f586-4b6c-9daa-947be29e8a44', '66d56a9b-17c4-4183-aa15-2db3082c3874', 'LAB-2024007', 'جهاز توسيع الفك',
        NULL,
        '2025-06-09', '2025-06-16',
        'PENDING',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('263c8e4e-26ff-4e10-a734-8802892a3c17', '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', 'LAB-2024008', 'قشرة تجميلية',
        NULL,
        '2025-06-15', '2025-07-02',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('73039d8f-6202-4756-9a0c-c972e1c0c347', '1d7de144-435b-4d30-aab6-6fb43d4ac723', 'LAB-2024009', 'قالب أسنان',
        20,
        '2025-06-01', '2025-06-17',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('7dfb1f13-d93f-4fb2-99bd-81a536d30200', 'f9e60397-26d4-4c86-98a7-d906d9359501', 'LAB-2024010', 'جهاز توسيع الفك',
        12,
        '2025-05-29', '2025-06-07',
        'PENDING',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('2a152a86-04ba-4fa9-9c92-45b1152b1c3c', '12e8d048-1f45-4dfe-920b-b3b28507702a', 'LAB-2024011', 'جهاز توسيع الفك',
        18,
        '2025-06-17', '2025-06-29',
        'COMPLETED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('3619eb78-07ff-402b-b5f0-d009ac747dc1', '94ebb4cc-4a6c-4063-8cea-3130a45e71e4', 'LAB-2024012', 'قشرة تجميلية',
        14,
        '2025-07-03', '2025-07-20',
        'COMPLETED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('c6d6dd1f-b6b1-4557-bfa9-648afbc4232a', '3e8dcbc5-36b1-435b-bfef-356d7d615c73', 'LAB-2024013', 'طقم أسنان جزئي',
        18,
        '2025-05-17', '2025-06-05',
        'IN_PROGRESS',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('13fa4c3d-433e-4365-9a96-79df41f883f2', '28bb748d-3170-4652-87e7-871566ae3e74', 'LAB-2024014', 'مثبت تقويم',
        39,
        '2025-06-23', '2025-07-12',
        'COMPLETED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('6cced8fe-38f2-4f5b-bf93-382cb92b804d', '5747f021-7809-4e2d-856d-45ebdced300c', 'LAB-2024015', 'جسر أسنان',
        NULL,
        '2025-05-31', '2025-06-15',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('63acf03d-d718-4716-873a-b4fc10b4418f', '48bb9995-9330-4894-b74a-f24116c225ed', 'LAB-2024016', 'تاج بورسلين',
        30,
        '2025-05-16', '2025-06-01',
        'COMPLETED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('7cbd20ca-aecf-4069-b3db-871f9e94d7c6', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', 'LAB-2024017', 'مثبت تقويم',
        NULL,
        '2025-06-16', '2025-07-04',
        'IN_PROGRESS',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('8f77a36c-9ff3-4c40-b23e-8c28525c04b1', '517d1071-dde1-4d36-b1ef-5745264bf869', 'LAB-2024018', 'طقم أسنان كامل',
        41,
        '2025-07-11', '2025-07-22',
        'PENDING',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('49e90c56-0a1c-465c-8566-e9852da8c0d6', '875b2d85-4697-4477-9297-f19ffda87d21', 'LAB-2024019', 'مثبت تقويم',
        NULL,
        '2025-06-14', '2025-06-27',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('2ed9beb8-bcc7-4997-b753-ea995d786660', '951394c6-4e21-4d51-ba77-80c783a574e2', 'LAB-2024020', 'طقم أسنان جزئي',
        13,
        '2025-07-11', '2025-07-23',
        'IN_PROGRESS',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('d903bce9-b673-4e8b-9abc-255f3a25767d', 'e51d0c71-9df6-4c78-88c4-56f6b3ecc3fa', 'LAB-2024021', 'قشرة تجميلية',
        36,
        '2025-05-20', '2025-06-01',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('727fec56-bb6f-4f84-97e5-c2b45a9e879e', '3bba0776-a012-4b92-90f4-765a0ee510d6', 'LAB-2024022', 'تاج بورسلين',
        NULL,
        '2025-06-17', '2025-07-03',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('62b94a9d-a4b6-4061-9d0b-868d8ced36b3', '3057ea2c-1fdc-4d82-8e19-282a3566172b', 'LAB-2024023', 'طقم أسنان كامل',
        31,
        '2025-05-17', '2025-05-31',
        'COMPLETED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('fe880312-8969-4ccb-8afa-475e9610d00f', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'LAB-2024024', 'جهاز توسيع الفك',
        NULL,
        '2025-07-06', '2025-07-18',
        'IN_PROGRESS',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('a6c17e59-ef9a-446e-8c01-c078efd91c2e', '998dbd19-94b9-4c62-9df1-202d06aa254a', 'LAB-2024025', 'جهاز توسيع الفك',
        NULL,
        '2025-06-16', '2025-06-29',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('da296114-82cb-4d65-8471-063fc0fc9155', '3bba0776-a012-4b92-90f4-765a0ee510d6', 'LAB-2024026', 'قشرة تجميلية',
        32,
        '2025-05-15', '2025-05-31',
        'COMPLETED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('762e0c92-7bf9-489a-a373-565756443704', 'c85699c1-ffc2-483b-8999-cacc91b76fa1', 'LAB-2024027', 'تاج زيركون',
        NULL,
        '2025-06-13', '2025-06-22',
        'PENDING',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('e37d7fc3-b6b5-440e-83cb-1614379e894b', 'a60a0f58-bc0e-4896-b8a8-25359d4827a4', 'LAB-2024028', 'تاج زيركون',
        23,
        '2025-05-29', '2025-06-07',
        'PENDING',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('44108884-8922-4145-b4c9-1adf146d312e', 'ae32aab1-387f-4742-a104-9593c8820a09', 'LAB-2024029', 'مثبت تقويم',
        NULL,
        '2025-06-29', '2025-07-16',
        'CANCELLED',
        'مختبر الأسنان المتقدم');
INSERT INTO lab_requests (id, patient_id, order_number, item_description,
                          tooth_number, date_sent, date_due, status, lab_name)
VALUES ('568e0899-6d85-4baf-a81e-0648de5f7e22', 'c99fc741-2b1d-405b-ac34-dd92212fbc99', 'LAB-2024030', 'طقم أسنان كامل',
        NULL,
        '2025-05-31', '2025-06-21',
        'PENDING',
        'مختبر الأسنان المتقدم');
-- Additional Notes
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('77fb02c7-b550-4a22-bc19-3cf6137d08c7', '78d7276b-275d-4e1b-a0b4-dfcc334bd2c3', 'المريض بحاجة لتنظيف عميق للثة',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '143 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('4ace55fc-13f6-4475-a44e-44febe6e6279', 'b5ad516c-6b71-4416-b660-056ad1d34f79', 'المريض يعاني من صرير الأسنان الليلي',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '143 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('352f49d5-5dc6-4d7a-bf93-4a3c05e83afe', '3bac8559-06ab-4672-8336-9ad2a0cadce0', 'ينصح باستخدام معجون أسنان للحساسية',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '19 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('fd480902-6a50-4baf-9387-88241c3ed3e3', 'c788722c-3a96-4bd5-9139-33a8d8e65565', 'المريض يعاني من حساسية الأسنان',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '134 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('c24a4bcd-ce29-44fe-83bd-e341394bdca6', '81a36250-0264-4339-b35e-dd3786feca0e', 'تم إعطاء المريض تعليمات ما بعد الخلع',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '9 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('1cc5d82b-ea11-4c73-91d7-cb14831f7137', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', 'يحتاج المريض لعملية خلع ضرس العقل',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '12 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('00211ca8-1d7d-4891-9bd0-aac054f06bef', '4cf6838e-3cfd-4469-b010-38442aca0857', 'يحتاج المريض لعملية خلع ضرس العقل',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '159 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('a343ae82-4ff7-4dc4-89bd-e0ea24433312', '5747f021-7809-4e2d-856d-45ebdced300c', 'يحتاج المريض لعملية خلع ضرس العقل',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4', NOW() - INTERVAL '87 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('a8e7935d-7220-4e97-a326-8ca2946c2e45', '3bba0776-a012-4b92-90f4-765a0ee510d6', 'المريض يعاني من التهاب اللثة',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '65 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('9d6895af-c030-40ac-ad9a-c53180ea5977', '81c2b2f8-63bb-488c-a276-d6766d4eae8a', 'يُنصح بتنظيف الأسنان كل 6 أشهر',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '32 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('04aae56b-89f3-492a-a6e2-c653001cfd5a', '81a36250-0264-4339-b35e-dd3786feca0e', 'ينصح باستخدام خيط الأسنان يومياً',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '84 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('34d35ea2-d36b-4821-b47e-ca76153b3c75', '009f45fe-3809-464d-96ce-3956e150e456', 'ينصح باستخدام خيط الأسنان يومياً',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '8 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('1227a9d9-9d54-4331-9528-a8e67b24c1ad', '638b6b84-50f9-4861-b001-c86ad2188ef1', 'ينصح باستخدام خيط الأسنان يومياً',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '154 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('ff44f7ce-85f9-4595-b516-68e3641b0d12', '66d56a9b-17c4-4183-aa15-2db3082c3874', 'المريض يعاني من حساسية الأسنان',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '141 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('1f0ba4c7-0047-4d2f-8d16-a6d87401f041', 'b25049f9-d533-495b-ba24-753998010265', 'يحتاج المريض لعملية خلع ضرس العقل',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '110 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('e4d42901-64f3-41cb-bd40-26ff718272a8', '536663f3-678d-4bc4-8f75-c188152194d9', 'المريض يعاني من حساسية الأسنان',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NOW() - INTERVAL '14 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('b6d9211c-c000-483a-9be7-a0abe101befe', '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'ينصح باستخدام خيط الأسنان يومياً',
        'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NOW() - INTERVAL '165 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('34884ef7-25d4-471f-8104-2a4cb8f43af0', '578912a3-7404-4cc6-8ff7-d4c7332acc45', 'المريض بحاجة لتنظيف عميق للثة',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '73 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('7a007131-4ab2-4a9f-96be-027865858eb1', '4beb4577-a28a-4ae6-bf53-400e18edccac', 'يحتاج المريض لعملية خلع ضرس العقل',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '169 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('86ac579b-2f0c-4b75-9248-fc2fccbd678d', '12e8d048-1f45-4dfe-920b-b3b28507702a', 'ينصح باستخدام معجون أسنان للحساسية',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '82 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('e7196730-6e6b-4660-81c0-807d3e7fc1c0', '33208c0c-cf5f-4fd4-9091-a6247d44f026', 'ينصح باستخدام خيط الأسنان يومياً',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '80 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('935d4f98-ab38-4dd2-aca1-af9c03133589', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'تم إعطاء المريض تعليمات ما بعد الخلع',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '72 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('74ff3a65-89ed-4fe5-bbe1-1dc02854c7d9', '28bb748d-3170-4652-87e7-871566ae3e74', 'المريض يعاني من حساسية الأسنان',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '50 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('ba81b0e2-53bc-4510-8e80-b7042218ffca', 'c85699c1-ffc2-483b-8999-cacc91b76fa1', 'تم مناقشة خيارات العلاج التجميلي',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '127 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('6a25c2c0-b004-49aa-8d34-0b152c101c02', 'c85699c1-ffc2-483b-8999-cacc91b76fa1', 'ينصح باستخدام خيط الأسنان يومياً',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4', NOW() - INTERVAL '61 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('b0db3dbc-21b1-43cb-a7a6-444975014b36', '4756d93c-b914-4fcd-ad22-9374cc1cbba8', 'تم وصف مضاد حيوي للمريض',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '34 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('f3f9caa7-a73e-4b68-8b8e-40b74a9bb5d0', 'e3e078df-a3c6-4446-900b-e3abe2ff19ff', 'ينصح باستخدام معجون أسنان للحساسية',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '35 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('06c82185-4dba-4493-aecc-67442b8a8adc', '7aa917b9-c53b-4e77-935a-6297112588e6', 'يحتاج المريض إلى أشعة بانورامية',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '167 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('90fccf2f-1e7e-4d2e-bc90-fe8f09497534', '28bb748d-3170-4652-87e7-871566ae3e74', 'تم إعطاء المريض تعليمات ما بعد الخلع',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '67 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('9136a25b-c853-4cd2-a1fa-e3623857bf8f', 'f3c23c7a-cc29-4424-9995-c1e197b8489a', 'المريض يعاني من التهاب اللثة',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4', NOW() - INTERVAL '167 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('f25760c0-0c67-4126-b4cb-d051a4f73671', 'e614e5fe-eafc-4da8-9666-9e4b2bef565d', 'يحتاج المريض لعملية خلع ضرس العقل',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '159 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('fdc760b7-65ad-40b8-b2d3-31b301ccaf59', '75d43ba8-1c27-4d1b-99e6-5ce77cf08750', 'المريض يعاني من صرير الأسنان الليلي',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '153 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('5a0ea91b-247d-49e6-a044-39137b1e5520', '3d209091-82a5-484c-91f7-6dbaf91154b1', 'المريض بحاجة لتنظيف عميق للثة',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '119 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('9a5742d6-ae08-4c7b-b100-effd9dc083b7', '4beb4577-a28a-4ae6-bf53-400e18edccac', 'تم مناقشة خيارات العلاج التجميلي',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '19 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('37050ecf-5e93-48bf-9e0d-46448cdc7196', '89ef4a50-600e-4ad1-8396-9eb6fa992e60', 'يحتاج المريض لعملية خلع ضرس العقل',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4', NOW() - INTERVAL '180 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('7e2ad699-e7ab-48fe-9d2b-1387bd0d497f', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'ينصح باستخدام خيط الأسنان يومياً',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '68 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('9c71ab70-7caa-4e1d-8496-7dd5387dbe87', '958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'يحتاج المريض إلى أشعة بانورامية',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '167 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('ef7bbbef-5138-470d-912d-aad4726ba301', '1d7de144-435b-4d30-aab6-6fb43d4ac723', 'تم مناقشة خيارات العلاج التجميلي',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '111 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('17eaefee-b7bd-42f3-8356-a3d680a84ece', '958efdc2-87c6-4770-8f11-b93c68c0fa8b', 'المريض يحتاج إلى متابعة بعد أسبوعين',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '127 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('0148e56b-c9c4-4715-9aa7-0e02d634da6d', '517d1071-dde1-4d36-b1ef-5745264bf869', 'المريض يعاني من التهاب اللثة',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '70 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('07e795a3-abef-4494-9f6b-c001dd3088ce', '66d56a9b-17c4-4183-aa15-2db3082c3874', 'ينصح باستخدام معجون أسنان للحساسية',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '94 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('aceb304b-63f6-4eee-8e38-1e00e679c913', 'c788722c-3a96-4bd5-9139-33a8d8e65565', 'يُنصح بتنظيف الأسنان كل 6 أشهر',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4', NOW() - INTERVAL '8 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('96521b33-b69f-45df-91e5-7693fcadfa97', '7c9db862-5ccd-4652-b053-cedc74f5dac5', 'تم إعطاء المريض تعليمات ما بعد الخلع',
        'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', NOW() - INTERVAL '122 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('0b15fc2f-3174-456c-bf27-7535be17a585', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'تم إعطاء المريض تعليمات ما بعد الخلع',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '54 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('38c476cf-3a02-42db-8e4d-5c4d9f3c50ac', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', 'تم وصف مضاد حيوي للمريض',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4', NOW() - INTERVAL '56 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('f9403861-e54d-4ddc-aa77-257abdb2faaa', '0d8194ad-2d57-446b-b73d-1b8ec4993b84', 'يحتاج المريض إلى أشعة بانورامية',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '92 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('39ac084f-6267-4dbf-a170-73c8a6cb9718', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'ينصح باستخدام معجون أسنان للحساسية',
        'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', NOW() - INTERVAL '36 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('7ca3ea00-24d5-49c8-a5a7-e28dd5e08252', '7c9db862-5ccd-4652-b053-cedc74f5dac5', 'تم شرح طريقة العناية بالأسنان',
        'e9947118-bf36-494d-a826-eb6d6a0571ca', NOW() - INTERVAL '101 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('d9c4e646-2e0f-4412-aa89-8f0dcf068a0a', '9018a956-250f-40ca-be2c-bc6a37ec8b19', 'المريض يعاني من صرير الأسنان الليلي',
        '844e0809-f1e1-4f79-9c7c-d09754b25b6b', NOW() - INTERVAL '45 days');
INSERT INTO notes (id, patient_id, content, created_by, note_date)
VALUES ('95d5d89f-4844-4d7a-89fa-76d8c1ee73ba', '060e5ecd-073f-4930-9a27-e7bfcf8245b0', 'تم مناقشة خيارات العلاج التجميلي',
        'd7f81d26-ce95-4104-aafb-5a0e355621a4', NOW() - INTERVAL '51 days');
-- Document Records
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('a6467e18-9deb-4957-b1fe-0a03a39ac19d', 'c99fc741-2b1d-405b-ac34-dd92212fbc99', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'TREATMENT_PHOTO_P2024054_9492.pdf',
        '/documents/P2024054/TREATMENT_PHOTO_P2024054_9492.pdf', 3854570, 'application/pdf', 'TREATMENT_PHOTO');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('b56d32ed-4f67-4d92-a6ac-725eb46f4f3c', 'e6b862ca-2f88-41e6-a7e2-6b2e39bb9797', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'MEDICAL_REPORT_P2024050_8575.pdf',
        '/documents/P2024050/MEDICAL_REPORT_P2024050_8575.pdf', 1899615, 'application/pdf', 'MEDICAL_REPORT');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('61f345d4-d833-4bc2-95d8-f61416eb505f', '517d1071-dde1-4d36-b1ef-5745264bf869', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 'MEDICAL_REPORT_P2024024_5523.pdf',
        '/documents/P2024024/MEDICAL_REPORT_P2024024_5523.pdf', 818411, 'application/pdf', 'MEDICAL_REPORT');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('ac19fd96-1be2-4d5f-8c0c-550c248e3ed4', 'b9b53000-347b-499c-9259-a16709d58358', 'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'TREATMENT_PHOTO_P2024064_1100.pdf',
        '/documents/P2024064/TREATMENT_PHOTO_P2024064_1100.pdf', 694288, 'application/pdf', 'TREATMENT_PHOTO');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('2121d550-1ff9-4919-9cb0-76d192b88bff', 'fb732ed6-a4cf-4db4-bf66-4f2b6c327b3e', 'e9947118-bf36-494d-a826-eb6d6a0571ca', 'X_RAY_P2024084_3759.pdf',
        '/documents/P2024084/X_RAY_P2024084_3759.pdf', 132250, 'application/pdf', 'X_RAY');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('c8ce0b25-b84a-446b-96d4-bf969939fdd6', '4beb4577-a28a-4ae6-bf53-400e18edccac', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'صورة قبل العلاج_P2024088_6877.pdf',
        '/documents/P2024088/صورة قبل العلاج_P2024088_6877.pdf', 622249, 'application/pdf', 'صورة قبل العلاج');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('db686c75-98e9-4f3e-844c-d7d878bb0d41', '81a36250-0264-4339-b35e-dd3786feca0e', 'e9947118-bf36-494d-a826-eb6d6a0571ca', 'MEDICAL_REPORT_P2024053_1301.pdf',
        '/documents/P2024053/MEDICAL_REPORT_P2024053_1301.pdf', 3592304, 'application/pdf', 'MEDICAL_REPORT');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('bb473bb4-7841-44e4-9994-1d2cc13a6483', '810a56b5-6e4f-42d9-9aa8-e1d8a1477db7', 'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'صورة قبل العلاج_P2024078_4747.pdf',
        '/documents/P2024078/صورة قبل العلاج_P2024078_4747.pdf', 1302388, 'application/pdf', 'صورة قبل العلاج');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('0bd55aeb-9fde-4305-af96-efddfc95de68', '5747f021-7809-4e2d-856d-45ebdced300c', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'TREATMENT_PHOTO_P2024079_2232.pdf',
        '/documents/P2024079/TREATMENT_PHOTO_P2024079_2232.pdf', 2080189, 'application/pdf', 'TREATMENT_PHOTO');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('c61e54cc-674b-4e74-9ed1-54465a10bbd6', 'adcd0fd2-cfe1-4f19-ada9-f7dcb5e18aa6', 'e9947118-bf36-494d-a826-eb6d6a0571ca', 'MEDICAL_REPORT_P2024100_2998.pdf',
        '/documents/P2024100/MEDICAL_REPORT_P2024100_2998.pdf', 1088927, 'application/pdf', 'MEDICAL_REPORT');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('212e4504-4024-44a1-81c5-e83d35fff7b6', '81c2b2f8-63bb-488c-a276-d6766d4eae8a', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'TREATMENT_APPROVAL_P2024028_2708.pdf',
        '/documents/P2024028/TREATMENT_APPROVAL_P2024028_2708.pdf', 162288, 'application/pdf', 'TREATMENT_APPROVAL');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('e3811bcd-991a-4bfe-ab91-6d0c6edb9ab3', '517d1071-dde1-4d36-b1ef-5745264bf869', 'd7f81d26-ce95-4104-aafb-5a0e355621a4', 'TREATMENT_PHOTO_P2024024_3881.pdf',
        '/documents/P2024024/TREATMENT_PHOTO_P2024024_3881.pdf', 4946132, 'application/pdf', 'TREATMENT_PHOTO');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('d8b9c8ea-cba6-4e91-869e-b2ce94692f72', '4c4eeae5-1844-4545-a6ce-2b8bbece8e88', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'MEDICAL_REPORT_P2024048_6005.pdf',
        '/documents/P2024048/MEDICAL_REPORT_P2024048_6005.pdf', 225891, 'application/pdf', 'MEDICAL_REPORT');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('fdd76190-9e03-4454-a247-971f6892cb7e', 'c788722c-3a96-4bd5-9139-33a8d8e65565', 'd7f81d26-ce95-4104-aafb-5a0e355621a4', 'صورة قبل العلاج_P2024071_2800.pdf',
        '/documents/P2024071/صورة قبل العلاج_P2024071_2800.pdf', 696525, 'application/pdf', 'صورة قبل العلاج');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('c3144d8f-80b4-4b78-86ea-47db9f8e0191', 'b9b53000-347b-499c-9259-a16709d58358', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'صورة قبل العلاج_P2024064_4559.pdf',
        '/documents/P2024064/صورة قبل العلاج_P2024064_4559.pdf', 3673458, 'application/pdf', 'صورة قبل العلاج');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('2907006c-fce8-4f07-9726-4d6b168a6034', '517d1071-dde1-4d36-b1ef-5745264bf869', 'e9947118-bf36-494d-a826-eb6d6a0571ca', 'X_RAY_P2024024_5027.pdf',
        '/documents/P2024024/X_RAY_P2024024_5027.pdf', 3421397, 'application/pdf', 'X_RAY');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('e5e0aff7-f80b-4486-8479-7c50cd9b649a', 'c788722c-3a96-4bd5-9139-33a8d8e65565', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'X_RAY_P2024071_6915.pdf',
        '/documents/P2024071/X_RAY_P2024071_6915.pdf', 1782673, 'application/pdf', 'X_RAY');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('97373807-e8d9-46fc-b3fe-11c248d3aebd', 'a84f91b9-21de-47fe-941e-023727b5a321', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 'صورة قبل العلاج_P2024086_4915.pdf',
        '/documents/P2024086/صورة قبل العلاج_P2024086_4915.pdf', 3736898, 'application/pdf', 'صورة قبل العلاج');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('094e68d5-6a12-4170-9809-9288a368f65e', '51739c46-139f-499c-bec0-720560313310', 'd7f81d26-ce95-4104-aafb-5a0e355621a4', 'X_RAY_P2024032_8175.pdf',
        '/documents/P2024032/X_RAY_P2024032_8175.pdf', 3986699, 'application/pdf', 'X_RAY');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('a1b2c7a9-df61-436b-85dc-3bc1d12a1c80', '998dbd19-94b9-4c62-9df1-202d06aa254a', 'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'TREATMENT_APPROVAL_P2024036_2420.pdf',
        '/documents/P2024036/TREATMENT_APPROVAL_P2024036_2420.pdf', 4070746, 'application/pdf', 'TREATMENT_APPROVAL');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('aaad7c1b-0bd6-4a30-a104-cf1dbd2373ae', '9018a956-250f-40ca-be2c-bc6a37ec8b19', 'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'صورة قبل العلاج_P2024068_1545.pdf',
        '/documents/P2024068/صورة قبل العلاج_P2024068_1545.pdf', 4344671, 'application/pdf', 'صورة قبل العلاج');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('c91981ef-220b-4f2e-bfa2-b798c8cff7c5', '51739c46-139f-499c-bec0-720560313310', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'TREATMENT_APPROVAL_P2024032_6827.pdf',
        '/documents/P2024032/TREATMENT_APPROVAL_P2024032_6827.pdf', 1241463, 'application/pdf', 'TREATMENT_APPROVAL');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('786c2f95-c0ba-4ece-bee8-bfd3b857e3a2', '4096b1dc-2e28-4fe4-a22c-b62c1ba7cb56', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'X_RAY_P2024023_1435.pdf',
        '/documents/P2024023/X_RAY_P2024023_1435.pdf', 1003415, 'application/pdf', 'X_RAY');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('183d0053-8fcc-46f6-a816-fdccce3983b9', '81c2b2f8-63bb-488c-a276-d6766d4eae8a', 'd7f81d26-ce95-4104-aafb-5a0e355621a4', 'TREATMENT_APPROVAL_P2024028_7474.pdf',
        '/documents/P2024028/TREATMENT_APPROVAL_P2024028_7474.pdf', 4276599, 'application/pdf', 'TREATMENT_APPROVAL');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('b5c73141-1e28-4043-a5e1-8c2e3ad6ba2c', 'b25049f9-d533-495b-ba24-753998010265', 'e9947118-bf36-494d-a826-eb6d6a0571ca', 'TREATMENT_PHOTO_P2024080_6498.pdf',
        '/documents/P2024080/TREATMENT_PHOTO_P2024080_6498.pdf', 3590113, 'application/pdf', 'TREATMENT_PHOTO');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('7d7e4e0d-57c8-4317-9be0-fc73974c92d5', '8a77a5ce-84d2-4696-bbba-52f2304bdb8d', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'MEDICAL_REPORT_P2024026_1093.pdf',
        '/documents/P2024026/MEDICAL_REPORT_P2024026_1093.pdf', 4145317, 'application/pdf', 'MEDICAL_REPORT');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('20499268-9e01-4ff7-806e-d5f983256344', '578912a3-7404-4cc6-8ff7-d4c7332acc45', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'TREATMENT_APPROVAL_P2024052_7376.pdf',
        '/documents/P2024052/TREATMENT_APPROVAL_P2024052_7376.pdf', 2263924, 'application/pdf', 'TREATMENT_APPROVAL');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('f020dc68-85c3-4b6e-955f-4a5ac872e089', '688b05f6-aa01-4e29-8e46-92af09f92a12', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'MEDICAL_REPORT_P2024042_5635.pdf',
        '/documents/P2024042/MEDICAL_REPORT_P2024042_5635.pdf', 1639980, 'application/pdf', 'MEDICAL_REPORT');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('329f75ff-2381-4dab-895d-851e5fc60742', 'f9e60397-26d4-4c86-98a7-d906d9359501', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'TREATMENT_APPROVAL_P2024040_5168.pdf',
        '/documents/P2024040/TREATMENT_APPROVAL_P2024040_5168.pdf', 1933421, 'application/pdf', 'TREATMENT_APPROVAL');
INSERT INTO documents (id, patient_id, uploaded_by_staff_id, file_name,
                       file_path, file_size_bytes, mime_type, type)
VALUES ('0ca0cf7a-2695-4276-abc4-0e990c6a8774', 'c788722c-3a96-4bd5-9139-33a8d8e65565', 'a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'MEDICAL_REPORT_P2024071_5996.pdf',
        '/documents/P2024071/MEDICAL_REPORT_P2024071_5996.pdf', 971107, 'application/pdf', 'MEDICAL_REPORT');

COMMIT;
