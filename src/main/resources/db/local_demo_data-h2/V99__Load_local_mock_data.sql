-- Dental Clinic Demo Data (Arabic) - H2 Version
-- Generated on: 2025-07-12 17:08:22
-- This file contains additional data to supplement the existing demo data

-- Note: This demo data script avoids updating patient_teeth to prevent trigger errors
-- The track_tooth_history() trigger expects a created_by field that doesn't exist in patient_teeth table

MERGE INTO clinic_info (id, name, address, phone_number, email, timezone) KEY(id) VALUES 
(TRUE, 'عيادة الأسنان المتقدمة', 'شارع الملك فهد، حي الروضة، جدة', '+966 12 123 4567', 'info@advanced-dental.sa', 'Asia/Riyadh');

INSERT INTO staff (id, full_name, email, phone_number, is_active)
VALUES ('844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'د. علي الإبراهيم', 'doctor1@clinic.sa', '+966 571183769', TRUE);

INSERT INTO staff_roles (staff_id, role)
VALUES ('844e0809-f1e1-4f79-9c7c-d09754b25b6b', 'DOCTOR');

INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('844e0809-f1e1-4f79-9c7c-d09754b25b6b', (SELECT id FROM specialties WHERE name = 'General Dentistry'));

INSERT INTO staff (id, full_name, email, phone_number, is_active)
VALUES ('d9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 'د. حسن المصري', 'doctor2@clinic.sa', '+966 534077971', TRUE);

INSERT INTO staff_roles (staff_id, role)
VALUES ('d9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 'DOCTOR');

INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('d9dcc98a-517b-4518-8d50-acdbf4a0b5d2', (SELECT id FROM specialties WHERE name = 'General Dentistry'));

INSERT INTO staff (id, full_name, email, phone_number, is_active)
VALUES ('b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'د. عمر الحسن', 'doctor3@clinic.sa', '+966 560019543', TRUE);

INSERT INTO staff_roles (staff_id, role)
VALUES ('b5ee786d-b86e-4ca3-a705-4417f1c65b03', 'DOCTOR');

INSERT INTO staff_specialties (staff_id, specialty_id)
VALUES ('b5ee786d-b86e-4ca3-a705-4417f1c65b03', (SELECT id FROM specialties WHERE name = 'General Dentistry'));

INSERT INTO staff (id, full_name, email, phone_number, is_active)
VALUES ('e9947118-bf36-494d-a826-eb6d6a0571ca', 'أمل الخالد', 'nurse1@clinic.sa', '+966 575896163', TRUE);

INSERT INTO staff_roles (staff_id, role)
VALUES ('e9947118-bf36-494d-a826-eb6d6a0571ca', 'NURSE');

INSERT INTO staff (id, full_name, email, phone_number, is_active)
VALUES ('a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'رانيا الأحمد', 'nurse2@clinic.sa', '+966 595905431', TRUE);

INSERT INTO staff_roles (staff_id, role)
VALUES ('a56a1fa3-f4f2-4a38-9dad-3cb6fa710f08', 'NURSE');

INSERT INTO staff (id, full_name, email, phone_number, is_active)
VALUES ('d7f81d26-ce95-4104-aafb-5a0e355621a4', 'أمل السعيد', 'reception@clinic.sa', '+966 548333325', TRUE);

INSERT INTO staff_roles (staff_id, role)
VALUES ('d7f81d26-ce95-4104-aafb-5a0e355621a4', 'RECEPTIONIST');

-- Procedures
INSERT INTO procedures (specialty_id, procedure_code, name, description, default_cost, default_duration_minutes)
VALUES 
((SELECT id FROM specialties WHERE name = 'General Dentistry'), 'DEN001', 'كشف و استشارة', 'فحص شامل للأسنان واللثة', 150.00, 30),
((SELECT id FROM specialties WHERE name = 'General Dentistry'), 'DEN002', 'تنظيف الأسنان', 'إزالة الجير والتلميع', 300.00, 45),
((SELECT id FROM specialties WHERE name = 'General Dentistry'), 'DEN003', 'حشو بسيط', 'حشو تجاويف صغيرة', 250.00, 30),
((SELECT id FROM specialties WHERE name = 'General Dentistry'), 'DEN004', 'حشو مركب', 'حشو تجاويف متوسطة إلى كبيرة', 450.00, 45),
((SELECT id FROM specialties WHERE name = 'Endodontics'), 'ENDO001', 'علاج عصب', 'علاج قناة الجذر', 1500.00, 90),
((SELECT id FROM specialties WHERE name = 'Prosthodontics'), 'PROS001', 'تاج', 'تاج خزفي', 1200.00, 60),
((SELECT id FROM specialties WHERE name = 'Oral Surgery'), 'SURG001', 'خلع بسيط', 'خلع سن بسيط', 300.00, 30),
((SELECT id FROM specialties WHERE name = 'Oral Surgery'), 'SURG002', 'خلع جراحي', 'خلع سن مطمور', 800.00, 60),
((SELECT id FROM specialties WHERE name = 'General Dentistry'), 'DEN005', 'تبييض الأسنان', 'تبييض احترافي في العيادة', 1200.00, 90),
((SELECT id FROM specialties WHERE name = 'Orthodontics'), 'ORTHO001', 'تقويم معدني', 'تركيب تقويم معدني كامل', 5000.00, 120);

-- Patients with Arabic names
INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, gender, phone_number, email, address, insurance_provider, insurance_number, balance, created_by)
VALUES 
('c5a8b9e2-7f3d-4e2a-9b1c-8d5f6a3e2c1a', 'P-2024-0001', 'محمد أحمد الغامدي', '1985-03-15', 'MALE', '+966 551234567', 'mohammed.alghamdi@email.com', 'حي الروضة، جدة', 'بوبا العربية', 'BUPA123456', 500.00, '844e0809-f1e1-4f79-9c7c-d09754b25b6b'),
('d6b9c0f3-8e4e-5f3b-ac2d-9e6a7b4f3d2b', 'P-2024-0002', 'فاطمة علي الزهراني', '1990-07-22', 'FEMALE', '+966 552345678', 'fatima.zahrani@email.com', 'حي الشاطئ، جدة', 'التعاونية', 'TAWAN789012', -200.00, '844e0809-f1e1-4f79-9c7c-d09754b25b6b'),
('e7c0d1a4-9f5f-6a4c-bd3e-0f7b8c5a4e3c', 'P-2024-0003', 'عبدالله سعيد القحطاني', '1978-11-10', 'MALE', '+966 553456789', 'abdullah.qahtani@email.com', 'حي الحمراء، جدة', NULL, NULL, 0.00, '844e0809-f1e1-4f79-9c7c-d09754b25b6b'),
('f8d1e2b5-0a6a-7b5d-ce4f-1a8c9d6b5f4d', 'P-2024-0004', 'نورا خالد الشمري', '1995-05-30', 'FEMALE', '+966 554567890', 'nora.shammari@email.com', 'حي السلامة، جدة', 'ميدغلف', 'MEDG345678', 150.00, 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'),
('a9e2f3c6-1b7b-8c6e-df5a-2b9a0e7c6a5e', 'P-2024-0005', 'سارة محمد العتيبي', '2000-09-12', 'FEMALE', '+966 555678901', 'sara.otaibi@email.com', 'حي الزهراء، جدة', 'بوبا العربية', 'BUPA987654', 300.00, 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'),
('b0f3a4a7-2c8c-9a7f-ea6b-3c0a1f8a7b6f', 'P-2024-0006', 'خالد عمر الحربي', '1982-02-18', 'MALE', '+966 556789012', 'khalid.harbi@email.com', 'حي النزهة، جدة', NULL, NULL, -100.00, 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'),
('c1a4b5a8-3a9a-0a8a-fb7c-4a1a2a9a8c7a', 'P-2024-0007', 'مريم عبدالرحمن الدوسري', '1988-12-25', 'FEMALE', '+966 557890123', 'maryam.dosari@email.com', 'حي الصفا، جدة', 'التعاونية', 'TAWAN456789', 0.00, 'b5ee786d-b86e-4ca3-a705-4417f1c65b03'),
('a2b5c6a9-4a0a-1a9b-ac8a-5a2a3b0a9a8b', 'P-2024-0008', 'أحمد فهد المطيري', '1975-06-08', 'MALE', '+966 558901234', 'ahmed.mutairi@email.com', 'حي المروة، جدة', 'ميدغلف', 'MEDG789012', 750.00, 'b5ee786d-b86e-4ca3-a705-4417f1c65b03'),
('a3c6a7a0-5a1a-2a0c-ba9a-6a3a4c1a0a9c', 'P-2024-0009', 'هدى سالم الجهني', '1992-04-14', 'FEMALE', '+966 559012345', 'huda.juhani@email.com', 'حي البساتين، جدة', NULL, NULL, 200.00, 'b5ee786d-b86e-4ca3-a705-4417f1c65b03'),
('a4a7a8a1-6a2a-3a1a-ca0a-7a4a5a2a1a0a', 'P-2024-0010', 'يوسف ناصر العنزي', '1998-10-20', 'MALE', '+966 550123456', 'yousef.anzi@email.com', 'حي الفيصلية، جدة', 'بوبا العربية', 'BUPA321654', -50.00, '844e0809-f1e1-4f79-9c7c-d09754b25b6b');

-- Important medical notes
UPDATE patients SET important_medical_notes = 'حساسية من البنسلين، ضغط دم مرتفع' WHERE id = 'c5a8b9e2-7f3d-4e2a-9b1c-8d5f6a3e2c1a';
UPDATE patients SET important_medical_notes = 'مرض السكري النوع الثاني' WHERE id = 'd6b9c0f3-8e4e-5f3b-ac2d-9e6a7b4f3d2b';
UPDATE patients SET important_medical_notes = 'مدخن، أمراض اللثة المزمنة' WHERE id = 'e7c0d1a4-9f5f-6a4c-bd3e-0f7b8c5a4e3c';
UPDATE patients SET important_medical_notes = 'حامل في الشهر الخامس' WHERE id = 'f8d1e2b5-0a6a-7b5d-ce4f-1a8c9d6b5f4d';

-- Appointments
INSERT INTO appointments (id, specialty_id, patient_id, doctor_id, appointment_datetime, duration_minutes, status, notes, created_by)
VALUES 
-- Today's appointments
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', (SELECT id FROM specialties WHERE name = 'General Dentistry'), 'c5a8b9e2-7f3d-4e2a-9b1c-8d5f6a3e2c1a', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', CURRENT_TIMESTAMP + INTERVAL '2' HOUR, 30, 'SCHEDULED', 'كشف دوري', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
('b2c3d4e5-f6a7-8901-bcde-f23456789012', (SELECT id FROM specialties WHERE name = 'General Dentistry'), 'd6b9c0f3-8e4e-5f3b-ac2d-9e6a7b4f3d2b', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', CURRENT_TIMESTAMP + INTERVAL '3' HOUR, 45, 'SCHEDULED', 'تنظيف أسنان', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
('c3d4e5f6-a7b8-9012-cdef-345678901234', (SELECT id FROM specialties WHERE name = 'Endodontics'), 'e7c0d1a4-9f5f-6a4c-bd3e-0f7b8c5a4e3c', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', CURRENT_TIMESTAMP + INTERVAL '4' HOUR, 90, 'SCHEDULED', 'علاج عصب - ضرس 36', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),

-- Tomorrow's appointments
('d4e5f6a7-b8c9-0123-defa-456789012345', (SELECT id FROM specialties WHERE name = 'General Dentistry'), 'f8d1e2b5-0a6a-7b5d-ce4f-1a8c9d6b5f4d', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', CURRENT_TIMESTAMP + INTERVAL '1' DAY + INTERVAL '1' HOUR, 30, 'SCHEDULED', 'فحص حمل', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
('e5f6a7b8-c9a0-1234-efab-567890123456', (SELECT id FROM specialties WHERE name = 'Prosthodontics'), 'a9e2f3c6-1b7b-8c6e-df5a-2b9a0e7c6a5e', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', CURRENT_TIMESTAMP + INTERVAL '1' DAY + INTERVAL '2' HOUR, 60, 'SCHEDULED', 'تركيب تاج', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),

-- Past appointments (Completed)
('f6a7b8c9-a0a1-2345-fabc-678901234567', (SELECT id FROM specialties WHERE name = 'General Dentistry'), 'b0f3a4a7-2c8c-9a7f-ea6b-3c0a1f8a7b6f', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', CURRENT_TIMESTAMP - INTERVAL '7' DAY, 45, 'COMPLETED', 'تنظيف أسنان مكتمل', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
('a7b8c9a0-a1a2-3456-abca-789012345678', (SELECT id FROM specialties WHERE name = 'General Dentistry'), 'c1a4b5a8-3a9a-0a8a-fb7c-4a1a2a9a8c7a', '844e0809-f1e1-4f79-9c7c-d09754b25b6b', CURRENT_TIMESTAMP - INTERVAL '14' DAY, 30, 'COMPLETED', 'حشو سن 24', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),

-- Cancelled appointment
('b8c9a0a1-a2a3-4567-bcaa-890123456789', (SELECT id FROM specialties WHERE name = 'Oral Surgery'), 'a2b5c6a9-4a0a-1a9b-ac8a-5a2a3b0a9a8b', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', CURRENT_TIMESTAMP - INTERVAL '3' DAY, 60, 'CANCELLED', 'المريض ألغى الموعد', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),

-- No show appointment
('c9a0a1a2-a3a4-5678-caaa-901234567890', (SELECT id FROM specialties WHERE name = 'General Dentistry'), 'a3c6a7a0-5a1a-2a0c-ba9a-6a3a4c1a0a9c', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', CURRENT_TIMESTAMP - INTERVAL '5' DAY, 30, 'NO_SHOW', 'المريض لم يحضر', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

-- Treatments
INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, doctor_id, tooth_number, status, cost, treatment_notes, treatment_date, created_by)
VALUES 
-- Completed treatments
('a1a2b3c4-d5e6-f789-0abc-def123456789', 'f6a7b8c9-a0a1-2345-fabc-678901234567', 'b0f3a4a7-2c8c-9a7f-ea6b-3c0a1f8a7b6f', (SELECT id FROM procedures WHERE procedure_code = 'DEN002'), 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', NULL, 'COMPLETED', 300.00, 'تنظيف شامل مع إزالة الجير', CURRENT_DATE - INTERVAL '7' DAY, 'b5ee786d-b86e-4ca3-a705-4417f1c65b03'),
('a2b3c4d5-e6f7-a890-1bcd-ef234567890a', 'a7b8c9a0-a1a2-3456-abca-789012345678', 'c1a4b5a8-3a9a-0a8a-fb7c-4a1a2a9a8c7a', (SELECT id FROM procedures WHERE procedure_code = 'DEN003'), '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 24, 'COMPLETED', 250.00, 'حشو بسيط لتسوس سطحي', CURRENT_DATE - INTERVAL '14' DAY, '844e0809-f1e1-4f79-9c7c-d09754b25b6b'),

-- More completed treatments (for history)
('a3c4d5e6-f7a8-b901-2cde-f3456789012b', 'f6a7b8c9-a0a1-2345-fabc-678901234567', 'c5a8b9e2-7f3d-4e2a-9b1c-8d5f6a3e2c1a', (SELECT id FROM procedures WHERE procedure_code = 'DEN004'), '844e0809-f1e1-4f79-9c7c-d09754b25b6b', 36, 'COMPLETED', 450.00, 'حشو مركب لتسوس عميق', CURRENT_DATE - INTERVAL '30' DAY, '844e0809-f1e1-4f79-9c7c-d09754b25b6b'),
('a4d5e6f7-a8b9-c012-3def-456789012345', 'a7b8c9a0-a1a2-3456-abca-789012345678', 'd6b9c0f3-8e4e-5f3b-ac2d-9e6a7b4f3d2b', (SELECT id FROM procedures WHERE procedure_code = 'ENDO001'), 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2', 46, 'COMPLETED', 1500.00, 'علاج عصب مع حشو مؤقت', CURRENT_DATE - INTERVAL '45' DAY, 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'),
('a5e6f7a8-b9c0-a123-4efa-567890123456', 'f6a7b8c9-a0a1-2345-fabc-678901234567', 'e7c0d1a4-9f5f-6a4c-bd3e-0f7b8c5a4e3c', (SELECT id FROM procedures WHERE procedure_code = 'SURG001'), 'b5ee786d-b86e-4ca3-a705-4417f1c65b03', 38, 'COMPLETED', 300.00, 'خلع ضرس العقل السفلي', CURRENT_DATE - INTERVAL '60' DAY, 'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

-- Treatment materials for some treatments
INSERT INTO treatment_materials (treatment_id, material_name, quantity, unit, cost_per_unit, total_cost, supplier, batch_number, notes)
VALUES 
('a2b3c4d5-e6f7-a890-1bcd-ef234567890a', 'Composite Resin A2', 1.5, 'gm', 50.00, 75.00, 'Dental Supplies Co.', 'CR-2024-001', 'لون A2 للأسنان الأمامية'),
('a3c4d5e6-f7a8-b901-2cde-f3456789012b', 'Composite Resin A3', 2.0, 'gm', 50.00, 100.00, 'Dental Supplies Co.', 'CR-2024-002', 'لون A3 للأسنان الخلفية'),
('a4d5e6f7-a8b9-c012-3def-456789012345', 'Gutta Percha Points', 5.0, 'points', 2.00, 10.00, 'Endo Materials Ltd.', 'GP-2024-015', 'نقاط حشو قنوات الجذر'),
('a4d5e6f7-a8b9-c012-3def-456789012345', 'Root Canal Sealer', 0.5, 'ml', 100.00, 50.00, 'Endo Materials Ltd.', 'RCS-2024-008', 'مادة لاصقة لحشو القناة');

-- Invoices
INSERT INTO invoices (id, patient_id, invoice_number, issue_date, due_date, total_amount, status, created_by)
VALUES 
('b1a2b3c4-d5e6-f789-0123-456789abcdef', 'b0f3a4a7-2c8c-9a7f-ea6b-3c0a1f8a7b6f', 'INV-000001', CURRENT_DATE - INTERVAL '7' DAY, CURRENT_DATE + INTERVAL '23' DAY, 300.00, 'PAID', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03'),
('b2b3c4d5-e6f7-a890-1234-567890abcdef', 'c1a4b5a8-3a9a-0a8a-fb7c-4a1a2a9a8c7a', 'INV-000002', CURRENT_DATE - INTERVAL '14' DAY, CURRENT_DATE + INTERVAL '16' DAY, 250.00, 'PAID', '844e0809-f1e1-4f79-9c7c-d09754b25b6b'),
('b3c4d5e6-f7a8-b901-2345-678901abcdef', 'c5a8b9e2-7f3d-4e2a-9b1c-8d5f6a3e2c1a', 'INV-000003', CURRENT_DATE - INTERVAL '30' DAY, CURRENT_DATE, 450.00, 'UNPAID', '844e0809-f1e1-4f79-9c7c-d09754b25b6b'),
('b4d5e6f7-a8b9-c012-3456-789012abcdef', 'd6b9c0f3-8e4e-5f3b-ac2d-9e6a7b4f3d2b', 'INV-000004', CURRENT_DATE - INTERVAL '45' DAY, CURRENT_DATE - INTERVAL '15' DAY, 1500.00, 'PARTIALLY_PAID', 'd9dcc98a-517b-4518-8d50-acdbf4a0b5d2'),
('b5e6f7a8-b9c0-a123-4567-890123abcdef', 'e7c0d1a4-9f5f-6a4c-bd3e-0f7b8c5a4e3c', 'INV-000005', CURRENT_DATE - INTERVAL '60' DAY, CURRENT_DATE - INTERVAL '30' DAY, 300.00, 'PAID', 'b5ee786d-b86e-4ca3-a705-4417f1c65b03');

-- Invoice items
INSERT INTO invoice_items (invoice_id, treatment_id, description, amount)
VALUES 
('b1a2b3c4-d5e6-f789-0123-456789abcdef', 'a1a2b3c4-d5e6-f789-0abc-def123456789', 'تنظيف أسنان', 300.00),
('b2b3c4d5-e6f7-a890-1234-567890abcdef', 'a2b3c4d5-e6f7-a890-1bcd-ef234567890a', 'حشو بسيط - سن 24', 250.00),
('b3c4d5e6-f7a8-b901-2345-678901abcdef', 'a3c4d5e6-f7a8-b901-2cde-f3456789012b', 'حشو مركب - ضرس 36', 450.00),
('b4d5e6f7-a8b9-c012-3456-789012abcdef', 'a4d5e6f7-a8b9-c012-3def-456789012345', 'علاج عصب - ضرس 46', 1500.00),
('b5e6f7a8-b9c0-a123-4567-890123abcdef', 'a5e6f7a8-b9c0-a123-4efa-567890123456', 'خلع ضرس العقل', 300.00);

-- Payments
INSERT INTO payments (invoice_id, patient_id, payment_date, amount, payment_method, type, description, created_by)
VALUES 
('b1a2b3c4-d5e6-f789-0123-456789abcdef', 'b0f3a4a7-2c8c-9a7f-ea6b-3c0a1f8a7b6f', CURRENT_DATE - INTERVAL '7' DAY, 300.00, 'CASH', 'PAYMENT', 'دفع كامل', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
('b2b3c4d5-e6f7-a890-1234-567890abcdef', 'c1a4b5a8-3a9a-0a8a-fb7c-4a1a2a9a8c7a', CURRENT_DATE - INTERVAL '14' DAY, 250.00, 'CREDIT_CARD', 'PAYMENT', 'دفع كامل', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
('b4d5e6f7-a8b9-c012-3456-789012abcdef', 'd6b9c0f3-8e4e-5f3b-ac2d-9e6a7b4f3d2b', CURRENT_DATE - INTERVAL '40' DAY, 1000.00, 'BANK_TRANSFER', 'PAYMENT', 'دفع جزئي', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
('b5e6f7a8-b9c0-a123-4567-890123abcdef', 'e7c0d1a4-9f5f-6a4c-bd3e-0f7b8c5a4e3c', CURRENT_DATE - INTERVAL '55' DAY, 300.00, 'CASH', 'PAYMENT', 'دفع كامل', 'd7f81d26-ce95-4104-aafb-5a0e355621a4'),
(NULL, 'f8d1e2b5-0a6a-7b5d-ce4f-1a8c9d6b5f4d', CURRENT_DATE - INTERVAL '20' DAY, 150.00, 'CASH', 'DEPOSIT', 'دفعة مقدمة', 'd7f81d26-ce95-4104-aafb-5a0e355621a4');

-- Lab requests
INSERT INTO lab_requests (patient_id, order_number, item_description, tooth_number, date_sent, date_due, lab_name, status)
VALUES 
('a9e2f3c6-1b7b-8c6e-df5a-2b9a0e7c6a5e', 'LAB-2024-001', 'تاج خزفي - لون A2', 26, CURRENT_DATE - INTERVAL '3' DAY, CURRENT_DATE + INTERVAL '4' DAY, 'مختبر الأسنان المتقدم', 'IN_PROGRESS'),
('a2b5c6a9-4a0a-1a9b-ac8a-5a2a3b0a9a8b', 'LAB-2024-002', 'جسر ثلاثي 34-36', NULL, CURRENT_DATE - INTERVAL '10' DAY, CURRENT_DATE - INTERVAL '3' DAY, 'مختبر الابتسامة', 'COMPLETED'),
('a3c6a7a0-5a1a-2a0c-ba9a-6a3a4c1a0a9c', 'LAB-2024-003', 'قالب دراسة للفكين', NULL, CURRENT_DATE - INTERVAL '1' DAY, CURRENT_DATE + INTERVAL '7' DAY, 'مختبر الأسنان المتقدم', 'PENDING');

-- Update patient balances based on treatments and payments
UPDATE patients SET balance = 450.00 WHERE id = 'c5a8b9e2-7f3d-4e2a-9b1c-8d5f6a3e2c1a'; -- Has unpaid invoice
UPDATE patients SET balance = 500.00 WHERE id = 'd6b9c0f3-8e4e-5f3b-ac2d-9e6a7b4f3d2b'; -- Partially paid invoice
UPDATE patients SET balance = 0.00 WHERE id = 'e7c0d1a4-9f5f-6a4c-bd3e-0f7b8c5a4e3c';   -- All paid
UPDATE patients SET balance = -150.00 WHERE id = 'f8d1e2b5-0a6a-7b5d-ce4f-1a8c9d6b5f4d'; -- Has deposit
UPDATE patients SET balance = 0.00 WHERE id = 'b0f3a4a7-2c8c-9a7f-ea6b-3c0a1f8a7b6f';   -- All paid
UPDATE patients SET balance = 0.00 WHERE id = 'c1a4b5a8-3a9a-0a8a-fb7c-4a1a2a9a8c7a';   -- All paid