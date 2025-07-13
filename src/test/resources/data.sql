-- Test data for H2 database
INSERT INTO specialties (id, name, description, is_active) VALUES 
('123e4567-e89b-12d3-a456-426614174000', 'General Dentistry', 'General dental care', true);

INSERT INTO patients (id, public_facing_id, full_name, date_of_birth, balance, is_active) VALUES 
('223e4567-e89b-12d3-a456-426614174000', 'P001', 'John Doe', '1990-01-01', 0.00, true);

INSERT INTO procedures (id, specialty_id, procedure_code, name, default_cost, is_active) VALUES 
('323e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-426614174000', 'FILL001', 'Composite Filling', 150.00, true);

INSERT INTO appointments (id, specialty_id, patient_id, appointment_datetime, duration_minutes, status) VALUES 
('423e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-426614174000', '223e4567-e89b-12d3-a456-426614174000', '2024-07-15 10:00:00', 30, 'SCHEDULED');

INSERT INTO treatments (id, appointment_id, patient_id, procedure_id, tooth_number, status, cost, treatment_date) VALUES 
('523e4567-e89b-12d3-a456-426614174000', '423e4567-e89b-12d3-a456-426614174000', '223e4567-e89b-12d3-a456-426614174000', '323e4567-e89b-12d3-a456-426614174000', 11, 'COMPLETED', 150.00, '2024-07-15');